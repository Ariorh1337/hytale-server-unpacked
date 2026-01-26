/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.commands.player.inventory;

import com.hypixel.hytale.server.core.command.commands.player.inventory.InventoryBackpackCommand;
import com.hypixel.hytale.server.core.command.commands.player.inventory.InventoryClearCommand;
import com.hypixel.hytale.server.core.command.commands.player.inventory.InventoryItemCommand;
import com.hypixel.hytale.server.core.command.commands.player.inventory.InventorySeeCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class InventoryCommand
extends AbstractCommandCollection {
    public InventoryCommand() {
        super("inventory", "server.commands.inventory.desc");
        this.addAliases("inv");
        this.addSubCommand(new InventoryClearCommand());
        this.addSubCommand(new InventorySeeCommand());
        this.addSubCommand(new InventoryItemCommand());
        this.addSubCommand(new InventoryBackpackCommand());
    }
}

