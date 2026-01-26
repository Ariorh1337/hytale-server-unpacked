/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.commands.world;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.universe.world.commands.WorldSettingsCommand;
import com.hypixel.hytale.server.core.universe.world.commands.world.WorldAddCommand;
import com.hypixel.hytale.server.core.universe.world.commands.world.WorldListCommand;
import com.hypixel.hytale.server.core.universe.world.commands.world.WorldLoadCommand;
import com.hypixel.hytale.server.core.universe.world.commands.world.WorldPruneCommand;
import com.hypixel.hytale.server.core.universe.world.commands.world.WorldRemoveCommand;
import com.hypixel.hytale.server.core.universe.world.commands.world.WorldSaveCommand;
import com.hypixel.hytale.server.core.universe.world.commands.world.WorldSetDefaultCommand;
import com.hypixel.hytale.server.core.universe.world.commands.world.perf.WorldPerfCommand;
import com.hypixel.hytale.server.core.universe.world.commands.world.tps.WorldTpsCommand;
import com.hypixel.hytale.server.core.universe.world.commands.worldconfig.WorldConfigCommand;
import com.hypixel.hytale.server.core.universe.world.commands.worldconfig.WorldPauseCommand;

public class WorldCommand
extends AbstractCommandCollection {
    public WorldCommand() {
        super("world", "server.commands.world.desc");
        this.addAliases("worlds");
        this.addSubCommand(new WorldListCommand());
        this.addSubCommand(new WorldRemoveCommand());
        this.addSubCommand(new WorldPruneCommand());
        this.addSubCommand(new WorldLoadCommand());
        this.addSubCommand(new WorldAddCommand());
        this.addSubCommand(new WorldSetDefaultCommand());
        this.addSubCommand(new WorldSaveCommand());
        this.addSubCommand(new WorldPauseCommand());
        this.addSubCommand(new WorldConfigCommand());
        this.addSubCommand(new WorldSettingsCommand());
        this.addSubCommand(new WorldPerfCommand());
        this.addSubCommand(new WorldTpsCommand());
    }
}

