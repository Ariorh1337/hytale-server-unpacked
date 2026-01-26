/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.inventory.transaction;

import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Transaction {
    public boolean succeeded();

    public boolean wasSlotModified(short var1);

    @Nonnull
    public Transaction toParent(ItemContainer var1, short var2, ItemContainer var3);

    @Nullable
    public Transaction fromParent(ItemContainer var1, short var2, ItemContainer var3);
}

