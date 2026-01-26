/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.inventory.transaction;

import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ActionType;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.Transaction;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemStackTransaction
implements Transaction {
    public static final ItemStackTransaction FAILED_ADD = new ItemStackTransaction(false, ActionType.ADD, null, null, false, false, Collections.emptyList());
    private final boolean succeeded;
    @Nullable
    private final ActionType action;
    @Nullable
    private final ItemStack query;
    @Nullable
    private final ItemStack remainder;
    private final boolean allOrNothing;
    private final boolean filter;
    @Nonnull
    private final List<ItemStackSlotTransaction> slotTransactions;

    public ItemStackTransaction(boolean succeeded, @Nullable ActionType action, @Nullable ItemStack query, @Nullable ItemStack remainder, boolean allOrNothing, boolean filter, @Nonnull List<ItemStackSlotTransaction> slotTransactions) {
        this.succeeded = succeeded;
        this.action = action;
        this.query = query;
        this.remainder = remainder;
        this.allOrNothing = allOrNothing;
        this.filter = filter;
        this.slotTransactions = slotTransactions;
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
        for (ItemStackSlotTransaction t : this.slotTransactions) {
            if (!t.succeeded() || !t.wasSlotModified(slot)) continue;
            return true;
        }
        return false;
    }

    @Nullable
    public ActionType getAction() {
        return this.action;
    }

    @Nullable
    public ItemStack getQuery() {
        return this.query;
    }

    @Nullable
    public ItemStack getRemainder() {
        return this.remainder;
    }

    public boolean isAllOrNothing() {
        return this.allOrNothing;
    }

    public boolean isFilter() {
        return this.filter;
    }

    @Nonnull
    public List<ItemStackSlotTransaction> getSlotTransactions() {
        return this.slotTransactions;
    }

    @Override
    @Nonnull
    public ItemStackTransaction toParent(ItemContainer parent, short start, ItemContainer container) {
        List<ItemStackSlotTransaction> slotTransactions = this.slotTransactions.stream().map(transaction -> transaction.toParent(parent, start, container)).collect(Collectors.toList());
        return new ItemStackTransaction(this.succeeded, this.action, this.query, this.remainder, this.allOrNothing, this.filter, slotTransactions);
    }

    @Override
    @Nullable
    public ItemStackTransaction fromParent(ItemContainer parent, short start, @Nonnull ItemContainer container) {
        List<ItemStackSlotTransaction> slotTransactions = this.slotTransactions.stream().map(transaction -> transaction.fromParent(parent, start, container)).filter(Objects::nonNull).collect(Collectors.toList());
        if (slotTransactions.isEmpty()) {
            return null;
        }
        boolean succeeded = false;
        for (ItemStackSlotTransaction transaction2 : slotTransactions) {
            if (!transaction2.succeeded()) continue;
            succeeded = true;
            break;
        }
        return new ItemStackTransaction(succeeded, this.action, this.query, this.remainder, this.allOrNothing, this.filter, slotTransactions);
    }

    @Nonnull
    public String toString() {
        return "ItemStackTransaction{succeeded=" + this.succeeded + ", action=" + String.valueOf((Object)this.action) + ", query=" + String.valueOf(this.query) + ", remainder=" + String.valueOf(this.remainder) + ", allOrNothing=" + this.allOrNothing + ", filter=" + this.filter + ", slotTransactions=" + String.valueOf(this.slotTransactions) + "}";
    }
}

