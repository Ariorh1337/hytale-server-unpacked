/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.mounts.commands;

import com.hypixel.hytale.builtin.mounts.commands.DismountCommand;
import com.hypixel.hytale.builtin.mounts.commands.MountCheckCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class MountCommand
extends AbstractCommandCollection {
    public MountCommand() {
        super("mount", "server.commands.mount");
        this.addSubCommand(new DismountCommand());
        this.addSubCommand(new MountCheckCommand());
    }
}

