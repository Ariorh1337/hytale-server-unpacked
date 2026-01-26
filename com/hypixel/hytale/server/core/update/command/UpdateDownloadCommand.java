/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.update.command;

import com.hypixel.hytale.common.util.FormatUtil;
import com.hypixel.hytale.common.util.java.ManifestUtil;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.auth.ServerAuthManager;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.update.UpdateModule;
import com.hypixel.hytale.server.core.update.UpdateService;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;

public class UpdateDownloadCommand
extends AbstractAsyncCommand {
    private static final Message MSG_NOT_AUTHENTICATED = Message.translation("server.commands.update.not_authenticated");
    private static final Message MSG_CHECK_FAILED = Message.translation("server.commands.update.check_failed");
    private static final Message MSG_NO_UPDATE = Message.translation("server.commands.update.no_update");
    private static final Message MSG_DOWNLOAD_FAILED = Message.translation("server.commands.update.download_failed");
    private static final Message MSG_DOWNLOAD_COMPLETE = Message.translation("server.commands.update.download_complete");
    private static final Message MSG_DOWNLOAD_IN_PROGRESS = Message.translation("server.commands.update.download_in_progress");
    private static final Message MSG_INVALID_LAYOUT = Message.translation("server.commands.update.invalid_layout_download");
    private final FlagArg forceFlag = this.withFlagArg("force", "server.commands.update.download.force.desc");

    public UpdateDownloadCommand() {
        super("download", "server.commands.update.download.desc");
    }

    @Override
    @Nonnull
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
        UpdateModule updateModule = UpdateModule.get();
        if (updateModule != null && updateModule.isDownloadInProgress()) {
            context.sendMessage(MSG_DOWNLOAD_IN_PROGRESS);
            return CompletableFuture.completedFuture(null);
        }
        if (!UpdateService.isValidUpdateLayout() && !((Boolean)this.forceFlag.get(context)).booleanValue()) {
            context.sendMessage(MSG_INVALID_LAYOUT);
            return CompletableFuture.completedFuture(null);
        }
        ServerAuthManager authManager = ServerAuthManager.getInstance();
        if (!authManager.hasSessionToken()) {
            context.sendMessage(MSG_NOT_AUTHENTICATED);
            return CompletableFuture.completedFuture(null);
        }
        UpdateService updateService = new UpdateService();
        return updateService.checkForUpdate(UpdateService.getEffectivePatchline()).thenCompose(manifest -> {
            String currentVersion;
            if (manifest == null) {
                context.sendMessage(MSG_CHECK_FAILED);
                return CompletableFuture.completedFuture(null);
            }
            if (updateModule != null) {
                updateModule.setLatestKnownVersion((UpdateService.VersionManifest)manifest);
            }
            if ((currentVersion = ManifestUtil.getImplementationVersion()) != null && currentVersion.equals(manifest.version)) {
                context.sendMessage(MSG_NO_UPDATE);
                return CompletableFuture.completedFuture(null);
            }
            if (updateModule != null && !updateModule.tryAcquireDownloadLock()) {
                context.sendMessage(MSG_DOWNLOAD_IN_PROGRESS);
                return CompletableFuture.completedFuture(null);
            }
            context.sendMessage(Message.translation("server.commands.update.downloading").param("version", manifest.version));
            AtomicInteger lastPercent = new AtomicInteger(-1);
            UpdateService.DownloadTask downloadTask = updateService.downloadUpdate((UpdateService.VersionManifest)manifest, UpdateService.getStagingDir(), (percent, downloaded, total) -> {
                int rounded;
                if (updateModule != null) {
                    updateModule.updateDownloadProgress(downloaded, total);
                }
                if ((rounded = percent / 10 * 10) > lastPercent.get()) {
                    lastPercent.set(rounded);
                    context.sendMessage(Message.translation("server.commands.update.download_progress").param("percent", String.valueOf(percent)).param("downloaded", FormatUtil.bytesToString(downloaded)).param("total", FormatUtil.bytesToString(total)));
                }
            });
            CompletionStage downloadFuture = downloadTask.future().whenComplete((success, error) -> {
                if (updateModule != null) {
                    updateModule.releaseDownloadLock();
                }
                if (error instanceof CancellationException) {
                    UpdateService.deleteStagedUpdate();
                } else if (error != null || !Boolean.TRUE.equals(success)) {
                    context.sendMessage(MSG_DOWNLOAD_FAILED);
                    UpdateService.deleteStagedUpdate();
                } else {
                    context.sendMessage(MSG_DOWNLOAD_COMPLETE);
                }
            });
            if (updateModule != null) {
                updateModule.setActiveDownload((CompletableFuture<?>)downloadFuture, downloadTask.thread());
            }
            return ((CompletableFuture)downloadFuture).thenApply(v -> null);
        });
    }
}

