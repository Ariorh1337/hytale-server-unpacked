/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.prefabspawner.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.modules.prefabspawner.commands.PrefabSpawnerGetCommand;
import com.hypixel.hytale.server.core.modules.prefabspawner.commands.PrefabSpawnerSetCommand;
import com.hypixel.hytale.server.core.modules.prefabspawner.commands.PrefabSpawnerWeightCommand;

public class PrefabSpawnerCommand
extends AbstractCommandCollection {
    public PrefabSpawnerCommand() {
        super("prefabspawner", "server.commands.prefabspawner.desc");
        this.addAliases("pspawner");
        this.addSubCommand(new PrefabSpawnerGetCommand());
        this.addSubCommand(new PrefabSpawnerSetCommand());
        this.addSubCommand(new PrefabSpawnerWeightCommand());
    }
}

