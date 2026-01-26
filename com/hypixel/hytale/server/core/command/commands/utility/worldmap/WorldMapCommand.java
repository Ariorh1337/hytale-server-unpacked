/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.commands.utility.worldmap;

import com.hypixel.hytale.server.core.command.commands.utility.worldmap.WorldMapClearMarkersCommand;
import com.hypixel.hytale.server.core.command.commands.utility.worldmap.WorldMapDiscoverCommand;
import com.hypixel.hytale.server.core.command.commands.utility.worldmap.WorldMapReloadCommand;
import com.hypixel.hytale.server.core.command.commands.utility.worldmap.WorldMapUndiscoverCommand;
import com.hypixel.hytale.server.core.command.commands.utility.worldmap.WorldMapViewRadiusSubCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class WorldMapCommand
extends AbstractCommandCollection {
    public WorldMapCommand() {
        super("worldmap", "server.commands.worldmap.desc");
        this.addAliases("map");
        this.addSubCommand(new WorldMapReloadCommand());
        this.addSubCommand(new WorldMapDiscoverCommand());
        this.addSubCommand(new WorldMapUndiscoverCommand());
        this.addSubCommand(new WorldMapClearMarkersCommand());
        this.addSubCommand(new WorldMapViewRadiusSubCommand());
    }
}

