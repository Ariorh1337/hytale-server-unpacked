/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.inventory.transaction;

import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ActionType;
import com.hypixel.hytale.server.core.inventory.transaction.ListTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.TagSlotTransaction;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TagTransaction
extends ListTransaction<TagSlotTransaction> {
    @Nonnull
    private final ActionType action;
    private final int tagIndex;
    private final int remainder;
    private final boolean allOrNothing;
    private final boolean exactAmount;
    private final boolean filter;

    public TagTransaction(boolean succeeded, @Nonnull ActionType action, int tagIndex, int remainder, boolean allOrNothing, boolean exactAmount, boolean filter, @Nonnull List<TagSlotTransaction> slotTransactions) {
        super(succeeded, slotTransactions);
        this.action = action;
        this.tagIndex = tagIndex;
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
    public int getTagIndex() {
        return this.tagIndex;
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
    public TagTransaction toParent(ItemContainer parent, short start, ItemContainer container) {
        List<TagSlotTransaction> slotTransactions = this.getList().stream().map(transaction -> transaction.toParent(parent, start, container)).collect(Collectors.toList());
        return new TagTransaction(this.succeeded(), this.action, this.tagIndex, this.remainder, this.allOrNothing, this.exactAmount, this.filter, slotTransactions);
    }

    @Override
    @Nullable
    public TagTransaction fromParent(ItemContainer parent, short start, @Nonnull ItemContainer container) {
        List<TagSlotTransaction> slotTransactions = this.getList().stream().map(transaction -> transaction.fromParent(parent, start, container)).filter(Objects::nonNull).collect(Collectors.toList());
        if (slotTransactions.isEmpty()) {
            return null;
        }
        boolean succeeded = false;
        for (TagSlotTransaction transaction2 : slotTransactions) {
            if (!transaction2.succeeded()) continue;
            succeeded = true;
            break;
        }
        return new TagTransaction(succeeded, this.action, this.tagIndex, this.remainder, this.allOrNothing, this.exactAmount, this.filter, slotTransactions);
    }

    @Override
    @Nonnull
    public String toString() {
        return "TagTransaction{action=" + String.valueOf((Object)this.action) + ", tag=" + this.tagIndex + ", remainder=" + this.remainder + ", allOrNothing=" + this.allOrNothing + ", exactAmount=" + this.exactAmount + ", filter=" + this.filter + "} " + super.toString();
    }
}

