/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.inventory.container.filter;

import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterActionType;
import javax.annotation.Nullable;

public interface SlotFilter {
    public static final SlotFilter ALLOW = (actionType, container, slot, itemStack) -> true;
    public static final SlotFilter DENY = (actionType, container, slot, itemStack) -> false;

    public boolean test(FilterActionType var1, ItemContainer var2, short var3, @Nullable ItemStack var4);
}

