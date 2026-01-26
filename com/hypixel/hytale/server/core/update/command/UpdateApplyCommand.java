/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.update.command;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.HytaleServerConfig;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.Options;
import com.hypixel.hytale.server.core.ShutdownReason;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.update.UpdateService;
import com.hypixel.hytale.server.core.util.io.FileUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class UpdateApplyCommand
extends CommandBase {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final Message MSG_NO_STAGED = Message.translation("server.commands.update.no_staged");
    private static final Message MSG_BACKUP_FAILED = Message.translation("server.commands.update.backup_failed");
    private final FlagArg confirmFlag = this.withFlagArg("confirm", "server.commands.update.apply.confirm.desc");
    private static final String[] CONFIG_FILES = new String[]{"config.json", "permissions.json", "bans.json", "whitelist.json"};

    public UpdateApplyCommand() {
        super("apply", "server.commands.update.apply.desc");
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        String stagedVersion = UpdateService.getStagedVersion();
        if (stagedVersion == null) {
            context.sendMessage(MSG_NO_STAGED);
            return;
        }
        if (!((Boolean)this.confirmFlag.get(context)).booleanValue()) {
            context.sendMessage(Message.translation("server.commands.update.apply_confirm_required").param("version", stagedVersion));
            return;
        }
        if (!UpdateService.isValidUpdateLayout()) {
            context.sendMessage(Message.translation("server.commands.update.applying_no_launcher").param("version", stagedVersion));
            LOGGER.at(Level.WARNING).log("No launcher script detected - update must be applied manually after shutdown");
        } else {
            context.sendMessage(Message.translation("server.commands.update.applying").param("version", stagedVersion));
        }
        HytaleServerConfig.UpdateConfig config = HytaleServer.get().getConfig().getUpdateConfig();
        try {
            this.backupCurrentFiles();
            if (config.isRunBackupBeforeUpdate() && Options.getOptionSet().has(Options.BACKUP_DIRECTORY)) {
                Universe universe = Universe.get();
                if (universe != null) {
                    LOGGER.at(Level.INFO).log("Running server backup before update...");
                    universe.runBackup().join();
                    LOGGER.at(Level.INFO).log("Server backup completed");
                }
            } else if (config.isRunBackupBeforeUpdate()) {
                LOGGER.at(Level.WARNING).log("RunBackupBeforeUpdate is enabled but backups are not configured (no --backup-dir)");
            }
            if (config.isBackupConfigBeforeUpdate()) {
                this.backupConfigFiles();
            }
        }
        catch (IOException e) {
            ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(e)).log("Failed to create backups before update");
            context.sendMessage(MSG_BACKUP_FAILED);
            return;
        }
        HytaleServer.get().shutdownServer(ShutdownReason.UPDATE);
    }

    private void backupCurrentFiles() throws IOException {
        Path assetsZip;
        Path licensesDir;
        Path currentAot;
        Path backupDir = UpdateService.getBackupDir();
        Path backupServerDir = backupDir.resolve("Server");
        if (!UpdateService.deleteBackupDir()) {
            throw new IOException("Failed to clear existing backup directory");
        }
        Files.createDirectories(backupServerDir, new FileAttribute[0]);
        Path currentJar = Path.of("HytaleServer.jar", new String[0]);
        if (Files.exists(currentJar, new LinkOption[0])) {
            Files.copy(currentJar, backupServerDir.resolve("HytaleServer.jar"), StandardCopyOption.REPLACE_EXISTING);
        }
        if (Files.exists(currentAot = Path.of("HytaleServer.aot", new String[0]), new LinkOption[0])) {
            Files.copy(currentAot, backupServerDir.resolve("HytaleServer.aot"), StandardCopyOption.REPLACE_EXISTING);
        }
        if (Files.exists(licensesDir = Path.of("Licenses", new String[0]), new LinkOption[0])) {
            FileUtil.copyDirectory(licensesDir, backupServerDir.resolve("Licenses"));
        }
        if (Files.exists(assetsZip = Path.of("..", new String[0]).resolve("Assets.zip"), new LinkOption[0])) {
            Files.copy(assetsZip, backupDir.resolve("Assets.zip"), StandardCopyOption.REPLACE_EXISTING);
        }
        LOGGER.at(Level.INFO).log("Backed up current server files to %s", backupDir);
    }

    private void backupConfigFiles() throws IOException {
        Path backupServerDir = UpdateService.getBackupDir().resolve("Server");
        Files.createDirectories(backupServerDir, new FileAttribute[0]);
        int count = 0;
        for (String fileName : CONFIG_FILES) {
            Path file = Path.of(fileName, new String[0]);
            if (!Files.exists(file, new LinkOption[0])) continue;
            Files.copy(file, backupServerDir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            ++count;
        }
        LOGGER.at(Level.INFO).log("Backed up %d config files to %s", count, (Object)backupServerDir);
    }
}

