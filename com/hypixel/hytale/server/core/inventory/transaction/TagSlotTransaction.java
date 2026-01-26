/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.inventory.transaction;

import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ActionType;
import com.hypixel.hytale.server.core.inventory.transaction.SlotTransaction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TagSlotTransaction
extends SlotTransaction {
    private final int query;
    private final int remainder;

    public TagSlotTransaction(boolean succeeded, @Nonnull ActionType action, short slot, @Nullable ItemStack slotBefore, @Nullable ItemStack slotAfter, @Nullable ItemStack output, boolean allOrNothing, boolean exactAmount, boolean filter, @Nonnull int query, int remainder) {
        super(succeeded, action, slot, slotBefore, slotAfter, output, allOrNothing, exactAmount, filter);
        this.query = query;
        this.remainder = remainder;
    }

    public int getQuery() {
        return this.query;
    }

    public int getRemainder() {
        return this.remainder;
    }

    @Override
    @Nonnull
    public TagSlotTransaction toParent(ItemContainer parent, short start, ItemContainer container) {
        short newSlot = (short)(start + this.getSlot());
        return new TagSlotTransaction(this.succeeded(), this.getAction(), newSlot, this.getSlotBefore(), this.getSlotAfter(), this.getOutput(), this.isAllOrNothing(), this.isExactAmount(), this.isFilter(), this.query, this.remainder);
    }

    @Override
    @Nullable
    public TagSlotTransaction fromParent(ItemContainer parent, short start, @Nonnull ItemContainer container) {
        short newSlot = (short)(this.getSlot() - start);
        if (newSlot < 0 || newSlot >= container.getCapacity()) {
            return null;
        }
        return new TagSlotTransaction(this.succeeded(), this.getAction(), newSlot, this.getSlotBefore(), this.getSlotAfter(), this.getOutput(), this.isAllOrNothing(), this.isExactAmount(), this.isFilter(), this.query, this.remainder);
    }

    @Override
    @Nonnull
    public String toString() {
        return "TagSlotTransaction{tag=" + this.query + ", remainder=" + this.remainder + "} " + super.toString();
    }
}

