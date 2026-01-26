/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.permissions.commands.op;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.permissions.commands.op.OpAddCommand;
import com.hypixel.hytale.server.core.permissions.commands.op.OpRemoveCommand;
import com.hypixel.hytale.server.core.permissions.commands.op.OpSelfCommand;

public class OpCommand
extends AbstractCommandCollection {
    public OpCommand() {
        super("op", "server.commands.op.desc");
        this.addSubCommand(new OpSelfCommand());
        this.addSubCommand(new OpAddCommand());
        this.addSubCommand(new OpRemoveCommand());
    }

    @Override
    protected boolean canGeneratePermission() {
        return false;
    }
}

