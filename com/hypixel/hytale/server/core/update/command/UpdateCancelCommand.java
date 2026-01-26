/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.update.command;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.update.UpdateModule;
import com.hypixel.hytale.server.core.update.UpdateService;
import javax.annotation.Nonnull;

public class UpdateCancelCommand
extends CommandBase {
    private static final Message MSG_NOTHING_TO_CANCEL = Message.translation("server.commands.update.nothing_to_cancel");
    private static final Message MSG_DOWNLOAD_CANCELLED = Message.translation("server.commands.update.download_cancelled");
    private static final Message MSG_CANCEL_FAILED = Message.translation("server.commands.update.cancel_failed");

    public UpdateCancelCommand() {
        super("cancel", "server.commands.update.cancel.desc");
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        String stagedVersion;
        UpdateModule updateModule = UpdateModule.get();
        boolean didSomething = false;
        if (updateModule != null && updateModule.cancelDownload()) {
            context.sendMessage(MSG_DOWNLOAD_CANCELLED);
            didSomething = true;
        }
        if ((stagedVersion = UpdateService.getStagedVersion()) != null || didSomething) {
            if (UpdateService.deleteStagedUpdate()) {
                if (stagedVersion != null) {
                    context.sendMessage(Message.translation("server.commands.update.cancelled").param("version", stagedVersion));
                }
            } else if (stagedVersion != null) {
                context.sendMessage(MSG_CANCEL_FAILED);
            }
            return;
        }
        context.sendMessage(MSG_NOTHING_TO_CANCEL);
    }
}

