/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.inventory.container;

import com.hypixel.hytale.protocol.ItemArmorSlot;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.inventory.container.filter.ArmorSlotAddFilter;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterActionType;
import com.hypixel.hytale.server.core.inventory.container.filter.NoDuplicateFilter;
import com.hypixel.hytale.server.core.inventory.container.filter.SlotFilter;

public class ItemContainerUtil {
    public static <T extends ItemContainer> T trySetArmorFilters(T container) {
        if (container instanceof SimpleItemContainer) {
            SimpleItemContainer itemContainer = (SimpleItemContainer)container;
            ItemArmorSlot[] itemArmorSlots = ItemArmorSlot.VALUES;
            for (short i = 0; i < itemContainer.getCapacity(); i = (short)(i + 1)) {
                if (i < itemArmorSlots.length) {
                    if (i < 5) {
                        itemContainer.setSlotFilter(FilterActionType.ADD, i, new ArmorSlotAddFilter(itemArmorSlots[i]));
                        continue;
                    }
                    itemContainer.setSlotFilter(FilterActionType.ADD, i, new NoDuplicateFilter(itemContainer));
                    continue;
                }
                itemContainer.setSlotFilter(FilterActionType.ADD, i, SlotFilter.DENY);
            }
        }
        return container;
    }

    public static <T extends ItemContainer> T trySetSlotFilters(T container, SlotFilter filter) {
        if (container instanceof SimpleItemContainer) {
            SimpleItemContainer itemContainer = (SimpleItemContainer)container;
            for (short i = 0; i < itemContainer.getCapacity(); i = (short)(i + 1)) {
                itemContainer.setSlotFilter(FilterActionType.ADD, i, filter);
            }
        }
        return container;
    }
}

