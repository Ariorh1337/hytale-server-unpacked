/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.blockspawner.command;

import com.hypixel.hytale.builtin.blockspawner.command.BlockSpawnerGetCommand;
import com.hypixel.hytale.builtin.blockspawner.command.BlockSpawnerSetCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class BlockSpawnerCommand
extends AbstractCommandCollection {
    public BlockSpawnerCommand() {
        super("blockspawner", "server.commands.blockspawner.desc");
        this.addSubCommand(new BlockSpawnerSetCommand());
        this.addSubCommand(new BlockSpawnerGetCommand());
    }
}

