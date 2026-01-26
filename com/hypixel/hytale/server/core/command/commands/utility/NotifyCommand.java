/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.commands.utility;

import com.hypixel.hytale.protocol.packets.interface_.NotificationStyle;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandUtil;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import javax.annotation.Nonnull;

public class NotifyCommand
extends CommandBase {
    public NotifyCommand() {
        super("notify", "server.commands.notify.desc");
        this.setAllowsExtraArguments(true);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        Message message;
        String firstArg;
        String inputString = context.getInputString();
        String rawArgs = CommandUtil.stripCommandName(inputString).trim();
        if (rawArgs.isEmpty()) {
            context.sendMessage(Message.translation("server.commands.parsing.error.wrongNumberRequiredParameters").param("expected", 1).param("actual", 0));
            return;
        }
        String[] args = rawArgs.split("\\s+");
        if (args.length == 0) {
            context.sendMessage(Message.translation("server.commands.parsing.error.wrongNumberRequiredParameters").param("expected", 1).param("actual", 0));
            return;
        }
        NotificationStyle style = NotificationStyle.Default;
        int messageStartIndex = 0;
        if (args.length >= 2 && !(firstArg = args[0]).startsWith("{")) {
            try {
                style = NotificationStyle.valueOf(firstArg.toUpperCase());
                messageStartIndex = 1;
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        StringBuilder messageBuilder = new StringBuilder();
        for (int i = messageStartIndex; i < args.length; ++i) {
            if (i > messageStartIndex) {
                messageBuilder.append(' ');
            }
            messageBuilder.append(args[i]);
        }
        String messageString = messageBuilder.toString();
        if (messageString.startsWith("{")) {
            try {
                message = Message.parse(messageString);
            }
            catch (IllegalArgumentException e) {
                context.sendMessage(Message.raw("Invalid formatted message: " + e.getMessage()));
                return;
            }
        } else {
            message = Message.raw(messageString);
        }
        Message senderName = Message.raw(context.sender().getDisplayName());
        NotificationUtil.sendNotificationToUniverse(message, senderName, "announcement", null, style);
    }
}

