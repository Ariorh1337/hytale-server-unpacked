/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.util.backup;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Options;
import com.hypixel.hytale.server.core.util.backup.BackupUtil;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BackupTask {
    private static final DateTimeFormatter BACKUP_FILE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final Duration BACKUP_ARCHIVE_FREQUENCY = Duration.of(12L, ChronoUnit.HOURS);
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    @Nonnull
    private final CompletableFuture<Void> completion = new CompletableFuture();

    public static CompletableFuture<Void> start(@Nonnull Path universeDir, @Nonnull Path backupDir) {
        BackupTask task = new BackupTask(universeDir, backupDir);
        return task.completion;
    }

    private BackupTask(final @Nonnull Path universeDir, final @Nonnull Path backupDir) {
        new Thread(this, "Backup Runner"){
            final /* synthetic */ BackupTask this$0;
            {
                this.this$0 = this$0;
                super(arg0);
                this.setDaemon(false);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                BackupUtil.broadcastBackupStatus(true);
                try {
                    Path archiveDir = backupDir.resolve("archive");
                    Files.createDirectories(backupDir, new FileAttribute[0]);
                    Files.createDirectories(archiveDir, new FileAttribute[0]);
                    BackupTask.cleanOrArchiveOldBackups(backupDir, archiveDir);
                    BackupTask.cleanOldBackups(archiveDir);
                    String backupName = BACKUP_FILE_DATE_FORMATTER.format(LocalDateTime.now()) + ".zip";
                    Path tempZip = backupDir.resolve(backupName + ".tmp");
                    BackupUtil.walkFileTreeAndZip(universeDir, tempZip);
                    Path backupZip = backupDir.resolve(backupName);
                    Files.move(tempZip, backupZip, StandardCopyOption.REPLACE_EXISTING);
                    LOGGER.at(Level.INFO).log("Successfully created backup %s", backupZip);
                    this.this$0.completion.complete(null);
                }
                catch (Throwable t) {
                    ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(t)).log("Backup failed with exception");
                    BackupUtil.broadcastBackupError(t);
                    this.this$0.completion.completeExceptionally(t);
                }
                finally {
                    BackupUtil.broadcastBackupStatus(false);
                }
            }
        }.start();
    }

    private static void cleanOrArchiveOldBackups(@Nonnull Path sourceDir, @Nonnull Path archiveDir) throws IOException {
        boolean doArchive;
        int maxCount = Options.getOptionSet().valueOf(Options.BACKUP_MAX_COUNT);
        if (maxCount < 1) {
            return;
        }
        List<Path> oldBackups = BackupUtil.findOldBackups(sourceDir, maxCount);
        if (oldBackups == null || oldBackups.isEmpty()) {
            return;
        }
        Path oldestBackup = oldBackups.getFirst();
        FileTime oldestBackupTime = Files.getLastModifiedTime(oldestBackup, new LinkOption[0]);
        FileTime lastArchive = BackupTask.getMostRecentArchive(archiveDir);
        boolean bl = doArchive = lastArchive == null || Duration.between(oldestBackupTime.toInstant(), lastArchive.toInstant()).compareTo(BACKUP_ARCHIVE_FREQUENCY) > 0;
        if (doArchive) {
            oldBackups = oldBackups.subList(1, oldBackups.size());
            Files.move(oldestBackup, archiveDir.resolve(oldestBackup.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            LOGGER.at(Level.INFO).log("Archived old backup: %s", oldestBackup);
        }
        for (Path path : oldBackups) {
            LOGGER.at(Level.INFO).log("Clearing old backup: %s", path);
            Files.deleteIfExists(path);
        }
    }

    private static void cleanOldBackups(@Nonnull Path dir) throws IOException {
        int maxCount = Options.getOptionSet().valueOf(Options.BACKUP_MAX_COUNT);
        if (maxCount < 1) {
            return;
        }
        List<Path> oldBackups = BackupUtil.findOldBackups(dir, maxCount);
        if (oldBackups == null || oldBackups.isEmpty()) {
            return;
        }
        for (Path path : oldBackups) {
            LOGGER.at(Level.INFO).log("Clearing old backup: %s", path);
            Files.deleteIfExists(path);
        }
    }

    @Nullable
    private static FileTime getMostRecentArchive(@Nonnull Path dir) throws IOException {
        FileTime mostRecent = null;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir);){
            for (Path path : stream) {
                if (!Files.isRegularFile(path, new LinkOption[0])) continue;
                FileTime modifiedTime = Files.getLastModifiedTime(path, new LinkOption[0]);
                if (mostRecent != null && modifiedTime.compareTo(mostRecent) <= 0) continue;
                mostRecent = modifiedTime;
            }
        }
        return mostRecent;
    }
}

