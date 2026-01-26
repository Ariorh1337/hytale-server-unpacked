/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.role;

import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.util.InventoryHelper;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RoleUtils {
    public static void setHotbarItems(@Nonnull NPCEntity npcComponent, @Nonnull String[] hotbarItems) {
        Inventory inventory = npcComponent.getInventory();
        for (byte i = 0; i < hotbarItems.length; i = (byte)(i + 1)) {
            InventoryHelper.setHotbarItem(inventory, hotbarItems[i], i);
        }
    }

    public static void setOffHandItems(@Nonnull NPCEntity npcComponent, @Nonnull String[] offHandItems) {
        Inventory inventory = npcComponent.getInventory();
        for (byte i = 0; i < offHandItems.length; i = (byte)(i + 1)) {
            InventoryHelper.setOffHandItem(inventory, offHandItems[i], i);
        }
    }

    public static void setItemInHand(@Nonnull NPCEntity npcComponent, @Nullable String itemInHand) {
        if (!InventoryHelper.useItem(npcComponent.getInventory(), itemInHand)) {
            NPCPlugin.get().getLogger().at(Level.WARNING).log("NPC of type '%s': Failed to use item '%s'", (Object)npcComponent.getRoleName(), (Object)itemInHand);
        }
    }

    public static void setArmor(@Nonnull NPCEntity npcComponent, @Nullable String armor) {
        if (!InventoryHelper.useArmor(npcComponent.getInventory().getArmor(), armor)) {
            NPCPlugin.get().getLogger().at(Level.WARNING).log("NPC of type '%s': Failed to use armor '%s'", (Object)npcComponent.getRoleName(), (Object)armor);
        }
    }
}

