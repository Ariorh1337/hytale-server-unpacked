/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.util;

import com.hypixel.hytale.common.util.StringUtil;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemArmor;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemDropList;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.item.ItemModule;
import com.hypixel.hytale.server.npc.NPCPlugin;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InventoryHelper {
    public static final short DEFAULT_NPC_HOTBAR_SLOTS = 3;
    public static final short MAX_NPC_HOTBAR_SLOTS = 8;
    public static final short DEFAULT_NPC_INVENTORY_SLOTS = 0;
    public static final short DEFAULT_NPC_UTILITY_SLOTS = 0;
    public static final short MAX_NPC_UTILITY_SLOTS = 4;
    public static final short DEFAULT_NPC_TOOL_SLOTS = 0;
    public static final short MAX_NPC_INVENTORY_SLOTS = 36;

    private InventoryHelper() {
    }

    public static boolean matchesItem(@Nullable String pattern, @Nonnull ItemStack itemStack) {
        if (pattern == null || pattern.isEmpty() || ItemStack.isEmpty(itemStack)) {
            return false;
        }
        return StringUtil.isGlobMatching(pattern, itemStack.getItem().getId());
    }

    public static boolean matchesItem(@Nullable List<String> patterns, @Nonnull ItemStack itemStack) {
        if (patterns == null || patterns.isEmpty() || ItemStack.isEmpty(itemStack)) {
            return false;
        }
        return InventoryHelper.matchesPatterns(patterns, itemStack.getItem().getId());
    }

    protected static boolean matchesPatterns(@Nonnull List<String> patterns, @Nullable String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        for (int i = 0; i < patterns.size(); ++i) {
            String pattern = patterns.get(i);
            if (pattern == null || pattern.isEmpty() || !StringUtil.isGlobMatching(pattern, name)) continue;
            return true;
        }
        return false;
    }

    public static boolean itemKeyExists(@Nullable String name) {
        if (name != null && !name.isEmpty()) {
            return ItemModule.exists(name);
        }
        return false;
    }

    public static boolean itemKeyIsBlockType(@Nullable String name) {
        Item item;
        return name != null && !name.isEmpty() && (item = Item.getAssetMap().getAsset(name)) != null && item.hasBlockType();
    }

    public static boolean itemDropListKeyExists(@Nullable String name) {
        if (name != null && !name.isEmpty()) {
            ItemDropList dropList = ItemDropList.getAssetMap().getAsset(name);
            return dropList != null;
        }
        return false;
    }

    public static byte findHotbarSlotWithItem(@Nonnull Inventory inventory, String name) {
        ItemContainer hotbar = inventory.getHotbar();
        for (byte i = 0; i < hotbar.getCapacity(); i = (byte)(i + 1)) {
            if (!InventoryHelper.matchesItem(name, hotbar.getItemStack(i))) continue;
            return i;
        }
        return -1;
    }

    public static short findHotbarSlotWithItem(@Nonnull Inventory inventory, List<String> name) {
        ItemContainer hotbar = inventory.getHotbar();
        for (short i = 0; i < hotbar.getCapacity(); i = (short)(i + 1)) {
            if (!InventoryHelper.matchesItem(name, hotbar.getItemStack(i))) continue;
            return i;
        }
        return -1;
    }

    public static byte findHotbarEmptySlot(@Nonnull Inventory inventory) {
        ItemContainer hotbar = inventory.getHotbar();
        for (byte i = 0; i < hotbar.getCapacity(); i = (byte)(i + 1)) {
            if (!ItemStack.isEmpty(hotbar.getItemStack(i))) continue;
            return i;
        }
        return -1;
    }

    public static short findInventorySlotWithItem(@Nonnull Inventory inventory, String name) {
        CombinedItemContainer container = inventory.getCombinedHotbarFirst();
        for (short i = 0; i < container.getCapacity(); i = (short)(i + 1)) {
            if (!InventoryHelper.matchesItem(name, container.getItemStack(i))) continue;
            return i;
        }
        return -1;
    }

    public static short findInventorySlotWithItem(@Nonnull Inventory inventory, List<String> name) {
        CombinedItemContainer container = inventory.getCombinedHotbarFirst();
        for (short i = 0; i < container.getCapacity(); i = (short)(i + 1)) {
            if (!InventoryHelper.matchesItem(name, container.getItemStack(i))) continue;
            return i;
        }
        return -1;
    }

    public static int countItems(@Nonnull ItemContainer container, List<String> name) {
        int count = 0;
        for (short i = 0; i < container.getCapacity(); i = (short)(i + 1)) {
            ItemStack item = container.getItemStack(i);
            if (!InventoryHelper.matchesItem(name, item)) continue;
            count += item.getQuantity();
        }
        return count;
    }

    public static int countFreeSlots(@Nonnull ItemContainer container) {
        int count = 0;
        for (short i = 0; i < container.getCapacity(); i = (short)(i + 1)) {
            ItemStack item = container.getItemStack(i);
            if (item != null && !item.isEmpty()) continue;
            ++count;
        }
        return count;
    }

    public static boolean hotbarContainsItem(@Nonnull Inventory inventory, String name) {
        return InventoryHelper.findHotbarSlotWithItem(inventory, name) != -1;
    }

    public static boolean hotbarContainsItem(@Nonnull Inventory inventory, List<String> name) {
        return InventoryHelper.findHotbarSlotWithItem(inventory, name) != -1;
    }

    public static boolean holdsItem(@Nonnull Inventory inventory, String name) {
        return InventoryHelper.matchesItem(name, inventory.getItemInHand());
    }

    public static boolean containsItem(@Nonnull Inventory inventory, String name) {
        return InventoryHelper.findInventorySlotWithItem(inventory, name) != -1;
    }

    public static boolean containsItem(@Nonnull Inventory inventory, List<String> name) {
        return InventoryHelper.findInventorySlotWithItem(inventory, name) != -1;
    }

    public static boolean clearItemInHand(@Nonnull Inventory inventory, byte slotHint) {
        if (ItemStack.isEmpty(inventory.getItemInHand())) {
            return true;
        }
        byte slot = InventoryHelper.findHotbarEmptySlot(inventory);
        if (slot >= 0) {
            inventory.setActiveHotbarSlot(slot);
            return true;
        }
        slot = slotHint != -1 ? slotHint : (byte)0;
        inventory.getHotbar().removeItemStackFromSlot(slot);
        inventory.setActiveHotbarSlot(slot);
        return true;
    }

    public static void removeItemInHand(@Nonnull Inventory inventory, int count) {
        if (ItemStack.isEmpty(inventory.getItemInHand())) {
            return;
        }
        byte activeHotbarSlot = inventory.getActiveHotbarSlot();
        if (activeHotbarSlot == -1) {
            return;
        }
        inventory.getHotbar().removeItemStackFromSlot((short)activeHotbarSlot, count);
    }

    public static boolean checkHotbarSlot(@Nonnull Inventory inventory, byte slot) {
        ItemContainer hotbar = inventory.getHotbar();
        if (slot >= hotbar.getCapacity() || slot < 0) {
            NPCPlugin.get().getLogger().at(Level.WARNING).log("Invalid hotbar slot %s. Max is %s", slot, hotbar.getCapacity() - 1);
            return false;
        }
        return true;
    }

    public static boolean checkOffHandSlot(@Nonnull Inventory inventory, byte slot) {
        ItemContainer utility = inventory.getUtility();
        if (slot >= utility.getCapacity() || slot < -1) {
            NPCPlugin.get().getLogger().at(Level.WARNING).log("Invalid utility slot %s. Max is %s, Min is %s", slot, utility.getCapacity() - 1, -1);
            return false;
        }
        return true;
    }

    public static void setHotbarSlot(@Nonnull Inventory inventory, byte slot) {
        if (inventory.getActiveHotbarSlot() == slot) {
            return;
        }
        if (InventoryHelper.checkHotbarSlot(inventory, slot)) {
            inventory.setActiveHotbarSlot(slot);
        }
    }

    public static void setOffHandSlot(@Nonnull Inventory inventory, byte slot) {
        if (inventory.getActiveUtilitySlot() == slot) {
            return;
        }
        if (InventoryHelper.checkOffHandSlot(inventory, slot)) {
            inventory.setActiveUtilitySlot(slot);
        }
    }

    public static boolean setHotbarItem(@Nonnull Inventory inventory, @Nullable String name, byte slot) {
        if (name == null || name.isEmpty() || !InventoryHelper.itemKeyExists(name)) {
            return false;
        }
        ItemContainer hotbar = inventory.getHotbar();
        if (!InventoryHelper.checkHotbarSlot(inventory, slot)) {
            return false;
        }
        if (InventoryHelper.matchesItem(name, hotbar.getItemStack(slot))) {
            return true;
        }
        hotbar.setItemStackForSlot(slot, InventoryHelper.createItem(name));
        return true;
    }

    public static boolean setOffHandItem(@Nonnull Inventory inventory, @Nullable String name, byte slot) {
        if (name == null || name.isEmpty() || !InventoryHelper.itemKeyExists(name)) {
            return false;
        }
        ItemContainer utility = inventory.getUtility();
        if (!InventoryHelper.checkOffHandSlot(inventory, slot)) {
            return false;
        }
        if (InventoryHelper.matchesItem(name, utility.getItemStack(slot))) {
            return true;
        }
        utility.setItemStackForSlot(slot, InventoryHelper.createItem(name));
        return true;
    }

    public static boolean useItem(@Nonnull Inventory inventory, @Nullable String name, byte slotHint) {
        if (name == null || name.isEmpty() || !InventoryHelper.itemKeyExists(name)) {
            return false;
        }
        if (InventoryHelper.holdsItem(inventory, name)) {
            return true;
        }
        byte slot = InventoryHelper.findHotbarSlotWithItem(inventory, name);
        if (slot >= 0) {
            inventory.setActiveHotbarSlot(slot);
            return true;
        }
        if (slotHint == -1) {
            slotHint = InventoryHelper.findHotbarEmptySlot(inventory);
        }
        if (slotHint == -1) {
            slotHint = 0;
        }
        inventory.getHotbar().setItemStackForSlot(slotHint, InventoryHelper.createItem(name));
        inventory.setActiveHotbarSlot(slotHint);
        return true;
    }

    @Nullable
    public static ItemStack createItem(@Nullable String name) {
        if (!InventoryHelper.itemKeyExists(name)) {
            return null;
        }
        return new ItemStack(name, 1);
    }

    public static boolean useItem(@Nonnull Inventory inventory, @Nullable String name) {
        return name == null || name.isEmpty() ? InventoryHelper.clearItemInHand(inventory, (byte)-1) : InventoryHelper.useItem(inventory, name, (byte)-1);
    }

    public static boolean useArmor(@Nonnull ItemContainer armorInventory, @Nullable String armorItem) {
        ItemStack itemStack = InventoryHelper.createItem(armorItem);
        return InventoryHelper.useArmor(armorInventory, itemStack);
    }

    public static boolean useArmor(@Nonnull ItemContainer armorInventory, @Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        Item item = itemStack.getItem();
        if (item == null) {
            return false;
        }
        ItemArmor armor = item.getArmor();
        if (armor == null) {
            return false;
        }
        short slot = (short)armor.getArmorSlot().ordinal();
        if (slot < 0 || slot > armorInventory.getCapacity()) {
            return false;
        }
        return armorInventory.setItemStackForSlot(slot, itemStack).succeeded();
    }
}

