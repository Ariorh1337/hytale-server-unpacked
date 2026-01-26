/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.commands.worldconfig;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.universe.world.commands.worldconfig.WorldConfigPauseTimeCommand;
import com.hypixel.hytale.server.core.universe.world.commands.worldconfig.WorldConfigSeedCommand;
import com.hypixel.hytale.server.core.universe.world.commands.worldconfig.WorldConfigSetPvpCommand;
import com.hypixel.hytale.server.core.universe.world.commands.worldconfig.WorldConfigSetSpawnCommand;

public class WorldConfigCommand
extends AbstractCommandCollection {
    public WorldConfigCommand() {
        super("config", "server.commands.world.config.desc");
        this.addSubCommand(new WorldConfigPauseTimeCommand());
        this.addSubCommand(new WorldConfigSeedCommand());
        this.addSubCommand(new WorldConfigSetPvpCommand());
        this.addSubCommand(new WorldConfigSetSpawnCommand());
    }
}

