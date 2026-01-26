/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.permissions.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.permissions.commands.PermGroupCommand;
import com.hypixel.hytale.server.core.permissions.commands.PermTestCommand;
import com.hypixel.hytale.server.core.permissions.commands.PermUserCommand;

public class PermCommand
extends AbstractCommandCollection {
    public PermCommand() {
        super("perm", "server.commands.perm.desc");
        this.addSubCommand(new PermGroupCommand());
        this.addSubCommand(new PermUserCommand());
        this.addSubCommand(new PermTestCommand());
    }
}

