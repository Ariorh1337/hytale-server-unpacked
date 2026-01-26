/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.inventory.transaction;

import com.hypixel.hytale.server.core.inventory.MaterialQuantity;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ActionType;
import com.hypixel.hytale.server.core.inventory.transaction.ListTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.MaterialSlotTransaction;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MaterialTransaction
extends ListTransaction<MaterialSlotTransaction> {
    @Nonnull
    private final ActionType action;
    @Nonnull
    private final MaterialQuantity material;
    private final int remainder;
    private final boolean allOrNothing;
    private final boolean exactAmount;
    private final boolean filter;

    public MaterialTransaction(boolean succeeded, @Nonnull ActionType action, @Nonnull MaterialQuantity material, int remainder, boolean allOrNothing, boolean exactAmount, boolean filter, @Nonnull List<MaterialSlotTransaction> slotTransactions) {
        super(succeeded, slotTransactions);
        this.action = action;
        this.material = material;
        this.remainder = remainder;
        this.allOrNothing = allOrNothing;
        this.exactAmount = exactAmount;
        this.filter = filter;
    }

    @Nonnull
    public ActionType getAction() {
        return this.action;
    }

    @Nonnull
    public MaterialQuantity getMaterial() {
        return this.material;
    }

    public int getRemainder() {
        return this.remainder;
    }

    public boolean isAllOrNothing() {
        return this.allOrNothing;
    }

    public boolean isExactAmount() {
        return this.exactAmount;
    }

    public boolean isFilter() {
        return this.filter;
    }

    @Override
    @Nonnull
    public MaterialTransaction toParent(ItemContainer parent, short start, ItemContainer container) {
        List<MaterialSlotTransaction> slotTransactions = this.getList().stream().map(transaction -> transaction.toParent(parent, start, container)).collect(Collectors.toList());
        return new MaterialTransaction(this.succeeded(), this.action, this.material, this.remainder, this.allOrNothing, this.exactAmount, this.filter, slotTransactions);
    }

    @Override
    @Nullable
    public MaterialTransaction fromParent(ItemContainer parent, short start, @Nonnull ItemContainer container) {
        List<MaterialSlotTransaction> slotTransactions = this.getList().stream().map(transaction -> transaction.fromParent(parent, start, container)).filter(Objects::nonNull).collect(Collectors.toList());
        if (slotTransactions.isEmpty()) {
            return null;
        }
        boolean succeeded = false;
        for (MaterialSlotTransaction transaction2 : slotTransactions) {
            if (!transaction2.succeeded()) continue;
            succeeded = true;
            break;
        }
        return new MaterialTransaction(succeeded, this.action, this.material, this.remainder, this.allOrNothing, this.exactAmount, this.filter, slotTransactions);
    }

    @Override
    @Nonnull
    public String toString() {
        return "MaterialTransaction{action=" + String.valueOf((Object)this.action) + ", material=" + String.valueOf(this.material) + ", remainder=" + this.remainder + ", allOrNothing=" + this.allOrNothing + ", exactAmount=" + this.exactAmount + ", filter=" + this.filter + "} " + super.toString();
    }
}

