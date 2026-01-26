/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.commands.player.stats;

import com.hypixel.hytale.server.core.command.commands.player.stats.PlayerStatsAddCommand;
import com.hypixel.hytale.server.core.command.commands.player.stats.PlayerStatsDumpCommand;
import com.hypixel.hytale.server.core.command.commands.player.stats.PlayerStatsGetCommand;
import com.hypixel.hytale.server.core.command.commands.player.stats.PlayerStatsResetCommand;
import com.hypixel.hytale.server.core.command.commands.player.stats.PlayerStatsSetCommand;
import com.hypixel.hytale.server.core.command.commands.player.stats.PlayerStatsSetToMaxCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class PlayerStatsSubCommand
extends AbstractCommandCollection {
    public PlayerStatsSubCommand() {
        super("stats", "server.commands.player.stats.desc");
        this.addAliases("stat");
        this.addSubCommand(new PlayerStatsAddCommand());
        this.addSubCommand(new PlayerStatsGetCommand());
        this.addSubCommand(new PlayerStatsSetCommand());
        this.addSubCommand(new PlayerStatsSetToMaxCommand());
        this.addSubCommand(new PlayerStatsDumpCommand());
        this.addSubCommand(new PlayerStatsResetCommand());
    }
}

