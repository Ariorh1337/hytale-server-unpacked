/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.commands.world.chunk;

import com.hypixel.hytale.server.core.command.commands.world.chunk.ChunkFixHeightMapCommand;
import com.hypixel.hytale.server.core.command.commands.world.chunk.ChunkForceTickCommand;
import com.hypixel.hytale.server.core.command.commands.world.chunk.ChunkInfoCommand;
import com.hypixel.hytale.server.core.command.commands.world.chunk.ChunkLightingCommand;
import com.hypixel.hytale.server.core.command.commands.world.chunk.ChunkLoadCommand;
import com.hypixel.hytale.server.core.command.commands.world.chunk.ChunkLoadedCommand;
import com.hypixel.hytale.server.core.command.commands.world.chunk.ChunkMarkSaveCommand;
import com.hypixel.hytale.server.core.command.commands.world.chunk.ChunkMaxSendRateCommand;
import com.hypixel.hytale.server.core.command.commands.world.chunk.ChunkRegenerateCommand;
import com.hypixel.hytale.server.core.command.commands.world.chunk.ChunkResendCommand;
import com.hypixel.hytale.server.core.command.commands.world.chunk.ChunkTintCommand;
import com.hypixel.hytale.server.core.command.commands.world.chunk.ChunkTrackerCommand;
import com.hypixel.hytale.server.core.command.commands.world.chunk.ChunkUnloadCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class ChunkCommand
extends AbstractCommandCollection {
    public ChunkCommand() {
        super("chunk", "server.commands.chunk.desc");
        this.addAliases("chunks");
        this.addSubCommand(new ChunkFixHeightMapCommand());
        this.addSubCommand(new ChunkForceTickCommand());
        this.addSubCommand(new ChunkInfoCommand());
        this.addSubCommand(new ChunkLightingCommand());
        this.addSubCommand(new ChunkLoadCommand());
        this.addSubCommand(new ChunkLoadedCommand());
        this.addSubCommand(new ChunkMarkSaveCommand());
        this.addSubCommand(new ChunkMaxSendRateCommand());
        this.addSubCommand(new ChunkRegenerateCommand());
        this.addSubCommand(new ChunkResendCommand());
        this.addSubCommand(new ChunkTintCommand());
        this.addSubCommand(new ChunkTrackerCommand());
        this.addSubCommand(new ChunkUnloadCommand());
    }
}

