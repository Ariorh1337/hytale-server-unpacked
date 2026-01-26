/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.reputation.command;

import com.hypixel.hytale.builtin.adventure.reputation.command.ReputationAddCommand;
import com.hypixel.hytale.builtin.adventure.reputation.command.ReputationRankCommand;
import com.hypixel.hytale.builtin.adventure.reputation.command.ReputationSetCommand;
import com.hypixel.hytale.builtin.adventure.reputation.command.ReputationValueCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class ReputationCommand
extends AbstractCommandCollection {
    public ReputationCommand() {
        super("reputation", "server.commands.reputation.desc");
        this.addSubCommand(new ReputationAddCommand());
        this.addSubCommand(new ReputationSetCommand());
        this.addSubCommand(new ReputationRankCommand());
        this.addSubCommand(new ReputationValueCommand());
    }
}

