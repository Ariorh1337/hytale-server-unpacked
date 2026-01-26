/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.debug.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.modules.debug.commands.DebugShapeSubCommand;

public class DebugCommand
extends AbstractCommandCollection {
    public DebugCommand() {
        super("debug", "server.commands.debug.desc");
        this.addSubCommand(new DebugShapeSubCommand());
    }
}

