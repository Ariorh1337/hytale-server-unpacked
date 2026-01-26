/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.inventory.container.filter;

import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterActionType;
import com.hypixel.hytale.server.core.inventory.container.filter.SlotFilter;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ItemSlotFilter
extends SlotFilter {
    @Override
    default public boolean test(@Nonnull FilterActionType actionType, @Nonnull ItemContainer container, short slot, @Nullable ItemStack itemStack) {
        return switch (actionType) {
            default -> throw new MatchException(null, null);
            case FilterActionType.ADD -> this.test(itemStack != null ? itemStack.getItem() : null);
            case FilterActionType.REMOVE, FilterActionType.DROP -> {
                itemStack = container.getItemStack(slot);
                yield this.test(itemStack != null ? itemStack.getItem() : null);
            }
        };
    }

    public boolean test(@Nullable Item var1);
}

