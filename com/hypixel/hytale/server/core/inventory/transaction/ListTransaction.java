/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.inventory.transaction;

import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.Transaction;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ListTransaction<T extends Transaction>
implements Transaction {
    public static final ListTransaction<?> EMPTY_SUCCESSFUL_TRANSACTION = new ListTransaction(true);
    public static final ListTransaction<?> EMPTY_FAILED_TRANSACTION = new ListTransaction(false);
    private final boolean succeeded;
    @Nonnull
    private final List<T> list;

    public static <T extends Transaction> ListTransaction<T> getEmptyTransaction(boolean succeeded) {
        return succeeded ? EMPTY_SUCCESSFUL_TRANSACTION : EMPTY_FAILED_TRANSACTION;
    }

    private ListTransaction(boolean succeeded) {
        this.succeeded = succeeded;
        this.list = Collections.emptyList();
    }

    public ListTransaction(boolean succeeded, @Nonnull List<T> list) {
        this.succeeded = succeeded;
        this.list = Collections.unmodifiableList(list);
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
        for (Transaction t : this.list) {
            if (!t.succeeded() || !t.wasSlotModified(slot)) continue;
            return true;
        }
        return false;
    }

    @Nonnull
    public List<T> getList() {
        return this.list;
    }

    public int size() {
        return this.list.size();
    }

    @Override
    @Nonnull
    public ListTransaction<T> toParent(ItemContainer parent, short start, ItemContainer container) {
        List list = this.list.stream().map(transaction -> transaction.toParent(parent, start, container)).collect(Collectors.toList());
        return new ListTransaction(this.succeeded, list);
    }

    @Override
    @Nullable
    public ListTransaction<T> fromParent(ItemContainer parent, short start, ItemContainer container) {
        List list = this.list.stream().map(transaction -> transaction.fromParent(parent, start, container)).filter(Objects::nonNull).collect(Collectors.toList());
        if (list.isEmpty()) {
            return null;
        }
        boolean succeeded = false;
        for (Transaction transaction2 : list) {
            if (!transaction2.succeeded()) continue;
            succeeded = true;
            break;
        }
        return new ListTransaction(succeeded, list);
    }

    @Nonnull
    public String toString() {
        return "ListTransaction{succeeded=" + this.succeeded + ", list=" + String.valueOf(this.list) + "}";
    }
}

