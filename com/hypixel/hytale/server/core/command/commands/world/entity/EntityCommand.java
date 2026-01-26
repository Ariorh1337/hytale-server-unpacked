/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.commands.world.entity;

import com.hypixel.hytale.server.core.command.commands.world.entity.EntityCleanCommand;
import com.hypixel.hytale.server.core.command.commands.world.entity.EntityCloneCommand;
import com.hypixel.hytale.server.core.command.commands.world.entity.EntityCountCommand;
import com.hypixel.hytale.server.core.command.commands.world.entity.EntityDumpCommand;
import com.hypixel.hytale.server.core.command.commands.world.entity.EntityEffectCommand;
import com.hypixel.hytale.server.core.command.commands.world.entity.EntityHideFromAdventurePlayersCommand;
import com.hypixel.hytale.server.core.command.commands.world.entity.EntityIntangibleCommand;
import com.hypixel.hytale.server.core.command.commands.world.entity.EntityInvulnerableCommand;
import com.hypixel.hytale.server.core.command.commands.world.entity.EntityLodCommand;
import com.hypixel.hytale.server.core.command.commands.world.entity.EntityMakeInteractableCommand;
import com.hypixel.hytale.server.core.command.commands.world.entity.EntityNameplateCommand;
import com.hypixel.hytale.server.core.command.commands.world.entity.EntityRemoveCommand;
import com.hypixel.hytale.server.core.command.commands.world.entity.EntityResendCommand;
import com.hypixel.hytale.server.core.command.commands.world.entity.EntityTrackerCommand;
import com.hypixel.hytale.server.core.command.commands.world.entity.snapshot.EntitySnapshotSubCommand;
import com.hypixel.hytale.server.core.command.commands.world.entity.stats.EntityStatsSubCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class EntityCommand
extends AbstractCommandCollection {
    public EntityCommand() {
        super("entity", "server.commands.entity.desc");
        this.addAliases("entities");
        this.addSubCommand(new EntityCloneCommand());
        this.addSubCommand(new EntityRemoveCommand());
        this.addSubCommand(new EntityDumpCommand());
        this.addSubCommand(new EntityCleanCommand());
        this.addSubCommand(new EntityLodCommand());
        this.addSubCommand(new EntityTrackerCommand());
        this.addSubCommand(new EntityResendCommand());
        this.addSubCommand(new EntityNameplateCommand());
        this.addSubCommand(new EntityStatsSubCommand());
        this.addSubCommand(new EntitySnapshotSubCommand());
        this.addSubCommand(new EntityEffectCommand());
        this.addSubCommand(new EntityMakeInteractableCommand());
        this.addSubCommand(new EntityIntangibleCommand());
        this.addSubCommand(new EntityInvulnerableCommand());
        this.addSubCommand(new EntityHideFromAdventurePlayersCommand());
        this.addSubCommand(new EntityCountCommand());
    }
}

