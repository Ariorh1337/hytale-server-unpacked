/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.inventory.container.filter;

import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.inventory.container.filter.ItemSlotFilter;
import javax.annotation.Nullable;

public class NoDuplicateFilter
implements ItemSlotFilter {
    private final SimpleItemContainer container;

    public NoDuplicateFilter(SimpleItemContainer container) {
        this.container = container;
    }

    @Override
    public boolean test(@Nullable Item item) {
        if (item == null || item.getId() == null) {
            return false;
        }
        for (short i = 0; i < this.container.getCapacity(); i = (short)(i + 1)) {
            ItemStack itemStack = this.container.getItemStack(i);
            if (itemStack == null || !itemStack.getItemId().equals(item.getId())) continue;
            return false;
        }
        return true;
    }
}

