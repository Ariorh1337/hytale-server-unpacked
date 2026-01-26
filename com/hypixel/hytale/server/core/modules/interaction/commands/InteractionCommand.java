/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.modules.interaction.commands.InteractionClearCommand;
import com.hypixel.hytale.server.core.modules.interaction.commands.InteractionRunCommand;
import com.hypixel.hytale.server.core.modules.interaction.commands.InteractionSnapshotSourceCommand;

public class InteractionCommand
extends AbstractCommandCollection {
    public InteractionCommand() {
        super("interaction", "server.commands.interaction.desc");
        this.addAliases("interact");
        this.addSubCommand(new InteractionRunCommand());
        this.addSubCommand(new InteractionSnapshotSourceCommand());
        this.addSubCommand(new InteractionClearCommand());
    }
}

