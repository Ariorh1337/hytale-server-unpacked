/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.commands.utility.metacommands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.command.system.CommandOwner;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DumpCommandsCommand
extends CommandBase {
    public DumpCommandsCommand() {
        super("dump", "server.commands.meta.dump.desc");
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        JsonObject outputJson = new JsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<CommandDef> modernDefs = this.gatherCommandDefs();
        outputJson.add("modern", gson.toJsonTree(modernDefs));
        CompletableFuture.runAsync(() -> {
            try {
                String outputStr = gson.toJson(outputJson);
                Path path = Paths.get("dumps/commands.dump.json", new String[0]);
                Files.createDirectories(path.getParent(), new FileAttribute[0]);
                Files.writeString(path, (CharSequence)outputStr, new OpenOption[0]);
                context.sendMessage(Message.translation("server.commands.meta.dump.success").param("file", path.toAbsolutePath().toString()));
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }).exceptionally(t -> {
            context.sendMessage(Message.translation("server.commands.meta.dump.error"));
            ((HytaleLogger.Api)HytaleLogger.getLogger().at(Level.SEVERE).withCause((Throwable)t)).log("Couldn't write command dump");
            return null;
        });
    }

    private List<CommandDef> gatherCommandDefs() {
        Map<String, AbstractCommand> registrations = CommandManager.get().getCommandRegistration();
        ObjectArrayList<CommandDef> defs = new ObjectArrayList<CommandDef>(registrations.size() * 2);
        registrations.forEach((name, command) -> this.extractCommand((AbstractCommand)command, (List<CommandDef>)defs));
        return defs;
    }

    private void extractCommand(@Nonnull AbstractCommand command, @Nonnull List<CommandDef> defs) {
        String outputName = "/" + command.getFullyQualifiedName();
        String className = command.getClass().getName();
        String owner = this.formatNullable(command.getOwner(), CommandOwner::getName);
        String ownerClass = this.formatNullable(command.getOwner(), o -> o.getClass().getName());
        String permission = this.formatPermission(command.getPermission());
        List<String> permissionGroups = command.getPermissionGroups();
        defs.add(new CommandDef(outputName, className, owner, ownerClass, permission, permissionGroups));
        for (AbstractCommand subCommand : command.getSubCommands().values()) {
            this.extractCommand(subCommand, defs);
        }
    }

    private <T> String formatNullable(@Nullable T something, Function<T, String> func) {
        try {
            if (something == null) {
                return "NULL";
            }
            return func.apply(something);
        }
        catch (Throwable t) {
            return "ERROR";
        }
    }

    private String formatPermission(@Nullable String permission) {
        return permission == null ? "NULL" : permission;
    }

    private record CommandDef(String name, String className, String owner, String ownerClass, String permission, List<String> permissionGroups) {
    }
}

