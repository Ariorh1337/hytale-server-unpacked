/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.commands.block.bulk;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.universe.world.commands.block.bulk.BlockBulkFindCommand;
import com.hypixel.hytale.server.core.universe.world.commands.block.bulk.BlockBulkFindHereCommand;
import com.hypixel.hytale.server.core.universe.world.commands.block.bulk.BlockBulkReplaceCommand;

public class BlockBulkCommand
extends AbstractCommandCollection {
    public BlockBulkCommand() {
        super("bulk", "server.commands.block.bulk.desc");
        this.setPermissionGroup(null);
        this.addSubCommand(new BlockBulkFindCommand());
        this.addSubCommand(new BlockBulkFindHereCommand());
        this.addSubCommand(new BlockBulkReplaceCommand());
    }
}

