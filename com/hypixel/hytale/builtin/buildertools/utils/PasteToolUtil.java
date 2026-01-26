/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.utils;

import com.hypixel.hytale.protocol.packets.inventory.SetActiveSlot;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import javax.annotation.Nonnull;

public final class PasteToolUtil {
    private static final String PASTE_TOOL_ID = "EditorTool_Paste";

    private PasteToolUtil() {
    }

    public static void switchToPasteTool(@Nonnull Player player, @Nonnull PlayerRef playerRef) {
        ItemStack itemStack;
        short slot;
        Inventory inventory = player.getInventory();
        ItemContainer hotbar = inventory.getHotbar();
        ItemContainer storage = inventory.getStorage();
        ItemContainer tools = inventory.getTools();
        short hotbarSize = hotbar.getCapacity();
        for (short slot2 = 0; slot2 < hotbarSize; slot2 = (short)(slot2 + 1)) {
            ItemStack itemStack2 = hotbar.getItemStack(slot2);
            if (itemStack2 == null || itemStack2.isEmpty() || !PASTE_TOOL_ID.equals(itemStack2.getItemId())) continue;
            inventory.setActiveHotbarSlot((byte)slot2);
            playerRef.getPacketHandler().writeNoCache(new SetActiveSlot(-1, (byte)slot2));
            return;
        }
        short emptySlot = -1;
        for (slot = 0; slot < hotbarSize; slot = (short)(slot + 1)) {
            itemStack = hotbar.getItemStack(slot);
            if (itemStack != null && !itemStack.isEmpty()) continue;
            emptySlot = slot;
            break;
        }
        if (emptySlot == -1) {
            return;
        }
        for (slot = 0; slot < storage.getCapacity(); slot = (short)(slot + 1)) {
            itemStack = storage.getItemStack(slot);
            if (itemStack == null || itemStack.isEmpty() || !PASTE_TOOL_ID.equals(itemStack.getItemId())) continue;
            storage.moveItemStackFromSlotToSlot(slot, 1, hotbar, emptySlot);
            inventory.setActiveHotbarSlot((byte)emptySlot);
            playerRef.getPacketHandler().writeNoCache(new SetActiveSlot(-1, (byte)emptySlot));
            return;
        }
        ItemStack pasteToolStack = null;
        for (short slot3 = 0; slot3 < tools.getCapacity(); slot3 = (short)(slot3 + 1)) {
            ItemStack itemStack3 = tools.getItemStack(slot3);
            if (itemStack3 == null || itemStack3.isEmpty() || !PASTE_TOOL_ID.equals(itemStack3.getItemId())) continue;
            pasteToolStack = itemStack3;
            break;
        }
        if (pasteToolStack == null) {
            return;
        }
        hotbar.setItemStackForSlot(emptySlot, new ItemStack(pasteToolStack.getItemId()));
        inventory.setActiveHotbarSlot((byte)emptySlot);
        playerRef.getPacketHandler().writeNoCache(new SetActiveSlot(-1, (byte)emptySlot));
    }
}

