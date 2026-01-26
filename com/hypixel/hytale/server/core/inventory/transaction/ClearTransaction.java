/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.inventory.transaction;

import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.Transaction;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ClearTransaction
implements Transaction {
    public static final ClearTransaction EMPTY = new ClearTransaction(true, 0, ItemStack.EMPTY_ARRAY);
    private final boolean succeeded;
    private final short start;
    @Nonnull
    private final ItemStack[] items;

    public ClearTransaction(boolean succeeded, short start, @Nonnull ItemStack[] items) {
        this.succeeded = succeeded;
        this.start = start;
        this.items = items;
    }

    @Override
    public boolean succeeded() {
        return this.succeeded;
    }

    @Override
    public boolean wasSlotModified(short slot) {
        if (!this.succeeded) {
            return false;
        }
        return (slot = (short)(slot - this.start)) >= 0 && slot < this.items.length && this.items[slot] != null && !this.items[slot].isEmpty();
    }

    @Nonnull
    public ItemStack[] getItems() {
        return this.items;
    }

    @Override
    @Nonnull
    public ClearTransaction toParent(ItemContainer parent, short start, ItemContainer container) {
        short newStart = (short)(start + this.start);
        return new ClearTransaction(this.succeeded, newStart, this.items);
    }

    @Override
    @Nullable
    public ClearTransaction fromParent(ItemContainer parent, short start, @Nonnull ItemContainer container) {
        short newStart = (short)(this.start - start);
        short capacity = container.getCapacity();
        if (newStart < 0) {
            short from = -newStart;
            if (this.items.length + newStart > capacity) {
                return new ClearTransaction(this.succeeded, 0, Arrays.copyOfRange(this.items, (int)from, from + capacity));
            }
            return new ClearTransaction(this.succeeded, 0, Arrays.copyOfRange(this.items, (int)from, this.items.length));
        }
        if (this.items.length > capacity) {
            return new ClearTransaction(this.succeeded, newStart, Arrays.copyOf(this.items, (int)capacity));
        }
        return new ClearTransaction(this.succeeded, newStart, this.items);
    }

    @Nonnull
    public String toString() {
        return "ClearTransaction{items=" + Arrays.toString(this.items) + "}";
    }
}

