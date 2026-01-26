/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.path.commands;

import com.hypixel.hytale.builtin.path.commands.PrefabPathUpdateObservationAngleCommand;
import com.hypixel.hytale.builtin.path.commands.PrefabPathUpdatePauseCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class PrefabPathUpdateCommand
extends AbstractCommandCollection {
    public PrefabPathUpdateCommand() {
        super("update", "server.commands.npcpath.update.desc");
        this.addSubCommand(new PrefabPathUpdatePauseCommand());
        this.addSubCommand(new PrefabPathUpdateObservationAngleCommand());
    }
}

