/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.permissions.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import java.util.List;
import javax.annotation.Nonnull;

public class PermTestCommand
extends CommandBase {
    @Nonnull
    private final RequiredArg<List<String>> nodesArg = this.withListRequiredArg("nodes", "server.commands.perm.test.nodes.desc", ArgTypes.STRING);

    public PermTestCommand() {
        super("test", "server.commands.testperm.desc");
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        CommandSender sender = context.sender();
        List nodes = (List)this.nodesArg.get(context);
        for (String node : nodes) {
            context.sendMessage(Message.translation("server.commands.testperm.hasPermission").param("permission", node).param("hasPermission", sender.hasPermission(node)));
        }
    }
}

