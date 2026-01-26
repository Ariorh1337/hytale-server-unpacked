/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.commands.debug.component.repulsion;

import com.hypixel.hytale.server.core.command.commands.debug.component.repulsion.RepulsionAddCommand;
import com.hypixel.hytale.server.core.command.commands.debug.component.repulsion.RepulsionRemoveCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class RepulsionCommand
extends AbstractCommandCollection {
    public RepulsionCommand() {
        super("repulsion", "server.commands.repulsion.desc");
        this.addSubCommand(new RepulsionAddCommand());
        this.addSubCommand(new RepulsionRemoveCommand());
    }
}

