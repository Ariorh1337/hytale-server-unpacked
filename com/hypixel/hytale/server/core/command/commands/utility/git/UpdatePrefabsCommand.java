/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.commands.utility.git;

import com.hypixel.hytale.common.util.PathUtil;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public class UpdatePrefabsCommand
extends AbstractCommandCollection {
    public UpdatePrefabsCommand() {
        super("prefabs", "server.commands.git.prefabs.desc");
        this.addSubCommand(new UpdatePrefabsStatusCommand());
        this.addSubCommand(new UpdatePrefabsCommitCommand());
        this.addSubCommand(new UpdatePrefabsPullCommand());
        this.addSubCommand(new UpdatePrefabsPushCommand());
        this.addSubCommand(new UpdatePrefabsAllCommand());
    }

    private static class UpdatePrefabsStatusCommand
    extends UpdatePrefabsGitCommand {
        public UpdatePrefabsStatusCommand() {
            super("status", "server.commands.git.prefabs.status.desc");
        }

        @Override
        @Nonnull
        protected String[][] getCommands(@Nonnull String senderDisplayName) {
            return new String[][]{{"git", "status"}, {"git", "submodule", "foreach", "git", "status"}};
        }
    }

    private static class UpdatePrefabsCommitCommand
    extends UpdatePrefabsGitCommand {
        public UpdatePrefabsCommitCommand() {
            super("commit", "server.commands.git.prefabs.commit.desc");
        }

        @Override
        @Nonnull
        protected String[][] getCommands(@Nonnull String senderDisplayName) {
            return new String[][]{{"git", "add", "--all", "."}, {"git", "commit", "-am", "Update prefabs by " + senderDisplayName}, {"git", "submodule", "foreach", "git", "add", "--all", "."}, {"git", "submodule", "foreach", "git", "commit", "-am", "\"Update prefabs by " + senderDisplayName + "\""}};
        }
    }

    private static class UpdatePrefabsPullCommand
    extends UpdatePrefabsGitCommand {
        public UpdatePrefabsPullCommand() {
            super("pull", "server.commands.git.prefabs.pull.desc");
        }

        @Override
        @Nonnull
        protected String[][] getCommands(@Nonnull String senderDisplayName) {
            return new String[][]{{"git", "pull"}, {"git", "submodule", "foreach", "git", "pull"}};
        }
    }

    private static class UpdatePrefabsPushCommand
    extends UpdatePrefabsGitCommand {
        public UpdatePrefabsPushCommand() {
            super("push", "server.commands.git.prefabs.push.desc");
        }

        @Override
        @Nonnull
        protected String[][] getCommands(@Nonnull String senderDisplayName) {
            return new String[][]{{"git", "push", "origin", "master"}, {"git", "submodule", "foreach", "git", "push"}};
        }
    }

    private static class UpdatePrefabsAllCommand
    extends UpdatePrefabsGitCommand {
        public UpdatePrefabsAllCommand() {
            super("all", "server.commands.git.prefabs.all.desc");
        }

        @Override
        @Nonnull
        protected String[][] getCommands(@Nonnull String senderDisplayName) {
            return new String[][]{{"git", "submodule", "foreach", "git", "add", "--all", "."}, {"git", "submodule", "foreach", "git", "commit", "-am", "\"Update prefabs by " + senderDisplayName + "\""}, {"git", "submodule", "foreach", "git", "pull"}, {"git", "submodule", "foreach", "git", "push"}, {"git", "add", "--all", "."}, {"git", "commit", "-am", "Update prefabs by " + senderDisplayName}, {"git", "pull"}, {"git", "push"}};
        }
    }

    private static abstract class UpdatePrefabsGitCommand
    extends AbstractAsyncCommand {
        protected UpdatePrefabsGitCommand(@Nonnull String name, @Nonnull String description) {
            super(name, description);
        }

        @Nonnull
        protected abstract String[][] getCommands(@Nonnull String var1);

        @Override
        @Nonnull
        protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
            return CompletableFuture.runAsync(() -> {
                String[][] cmds;
                Path prefabsPath = PrefabStore.get().getServerPrefabsPath();
                Path gitPath = null;
                if (Files.isDirectory(prefabsPath.resolve(".git"), new LinkOption[0])) {
                    gitPath = prefabsPath;
                } else {
                    Path parent = PathUtil.getParent(prefabsPath);
                    if (Files.isDirectory(parent.resolve(".git"), new LinkOption[0])) {
                        gitPath = parent;
                    }
                }
                if (gitPath == null) {
                    context.sendMessage(Message.translation("server.general.pathNotGitRepo").param("path", prefabsPath.toString()));
                    return;
                }
                String senderDisplayName = context.sender().getDisplayName().replaceAll("[^a-zA-Z0-9 ._-]", "");
                if (senderDisplayName.isEmpty()) {
                    senderDisplayName = "Unknown";
                }
                String finalSenderDisplayName = senderDisplayName;
                for (CharSequence[] charSequenceArray : cmds = this.getCommands(finalSenderDisplayName)) {
                    try {
                        String commandDisplay = String.join((CharSequence)" ", charSequenceArray);
                        context.sendMessage(Message.translation("server.commands.git.runningCmd").param("cmd", commandDisplay));
                        Process process = new ProcessBuilder((String[])charSequenceArray).directory(gitPath.toFile()).start();
                        try {
                            String line;
                            process.waitFor();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                            while ((line = reader.readLine()) != null) {
                                context.sendMessage(Message.translation("server.commands.git.runningStdOut").param("cmd", commandDisplay).param("line", line));
                            }
                            reader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
                            while ((line = reader.readLine()) != null) {
                                context.sendMessage(Message.translation("server.commands.git.runningStdErr").param("cmd", commandDisplay).param("line", line));
                            }
                            context.sendMessage(Message.translation("server.commands.git.done").param("cmd", commandDisplay));
                            continue;
                        }
                        catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    catch (IOException e) {
                        context.sendMessage(Message.translation("server.commands.git.failed").param("cmd", String.join((CharSequence)" ", charSequenceArray)).param("msg", e.getMessage()));
                    }
                    break;
                }
            });
        }
    }
}

