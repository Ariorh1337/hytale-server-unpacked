/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.update.command;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.command.system.ParserContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.update.UpdateModule;
import com.hypixel.hytale.server.core.update.command.UpdateApplyCommand;
import com.hypixel.hytale.server.core.update.command.UpdateCancelCommand;
import com.hypixel.hytale.server.core.update.command.UpdateCheckCommand;
import com.hypixel.hytale.server.core.update.command.UpdateDownloadCommand;
import com.hypixel.hytale.server.core.update.command.UpdatePatchlineCommand;
import com.hypixel.hytale.server.core.update.command.UpdateStatusCommand;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpdateCommand
extends AbstractCommandCollection {
    private static final Message MSG_DISABLED = Message.translation("server.commands.update.disabled");

    public UpdateCommand() {
        super("update", "server.commands.update.desc");
        this.addSubCommand(new UpdateCheckCommand());
        this.addSubCommand(new UpdateDownloadCommand());
        this.addSubCommand(new UpdateApplyCommand());
        this.addSubCommand(new UpdateCancelCommand());
        this.addSubCommand(new UpdateStatusCommand());
        this.addSubCommand(new UpdatePatchlineCommand());
    }

    @Override
    @Nullable
    public CompletableFuture<Void> acceptCall(@Nonnull CommandSender sender, @Nonnull ParserContext parserContext, @Nonnull ParseResult parseResult) {
        if (UpdateModule.KILL_SWITCH_ENABLED) {
            sender.sendMessage(MSG_DISABLED);
            return CompletableFuture.completedFuture(null);
        }
        return super.acceptCall(sender, parserContext, parseResult);
    }
}

