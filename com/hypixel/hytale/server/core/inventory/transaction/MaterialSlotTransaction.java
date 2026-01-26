/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.inventory.transaction;

import com.hypixel.hytale.server.core.inventory.MaterialQuantity;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.SlotTransaction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MaterialSlotTransaction
extends SlotTransaction {
    @Nonnull
    private final MaterialQuantity query;
    private final int remainder;
    @Nonnull
    private final SlotTransaction transaction;

    public MaterialSlotTransaction(@Nonnull MaterialQuantity query, int remainder, @Nonnull SlotTransaction transaction) {
        super(transaction.succeeded(), transaction.getAction(), transaction.getSlot(), transaction.getSlotBefore(), transaction.getSlotAfter(), transaction.getOutput(), transaction.isAllOrNothing(), transaction.isExactAmount(), transaction.isFilter());
        this.query = query;
        this.remainder = remainder;
        this.transaction = transaction;
    }

    @Nonnull
    public MaterialQuantity getQuery() {
        return this.query;
    }

    public int getRemainder() {
        return this.remainder;
    }

    @Nonnull
    public SlotTransaction getTransaction() {
        return this.transaction;
    }

    @Override
    @Nonnull
    public MaterialSlotTransaction toParent(ItemContainer parent, short start, ItemContainer container) {
        return new MaterialSlotTransaction(this.query, this.remainder, this.transaction.toParent(parent, start, container));
    }

    @Override
    @Nullable
    public MaterialSlotTransaction fromParent(ItemContainer parent, short start, @Nonnull ItemContainer container) {
        SlotTransaction newTransaction = this.transaction.fromParent(parent, start, container);
        if (newTransaction == null) {
            return null;
        }
        return new MaterialSlotTransaction(this.query, this.remainder, newTransaction);
    }

    @Override
    @Nonnull
    public String toString() {
        return "MaterialSlotTransaction{query=" + String.valueOf(this.query) + ", remainder=" + this.remainder + ", transaction=" + String.valueOf(this.transaction) + "} " + super.toString();
    }
}

