/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.inventory.transaction;

import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.MoveType;
import com.hypixel.hytale.server.core.inventory.transaction.SlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.Transaction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MoveTransaction<T extends Transaction>
implements Transaction {
    private final boolean succeeded;
    @Nonnull
    private final SlotTransaction removeTransaction;
    @Nonnull
    private final MoveType moveType;
    @Nonnull
    private final ItemContainer otherContainer;
    private final T addTransaction;

    public MoveTransaction(boolean succeeded, @Nonnull SlotTransaction removeTransaction, @Nonnull MoveType moveType, @Nonnull ItemContainer otherContainer, T addTransaction) {
        this.succeeded = succeeded;
        this.removeTransaction = removeTransaction;
        this.moveType = moveType;
        this.otherContainer = otherContainer;
        this.addTransaction = addTransaction;
    }

    @Override
    public boolean succeeded() {
        return this.succeeded;
    }

    @Nonnull
    public SlotTransaction getRemoveTransaction() {
        return this.removeTransaction;
    }

    @Nonnull
    public MoveType getMoveType() {
        return this.moveType;
    }

    @Nonnull
    public ItemContainer getOtherContainer() {
        return this.otherContainer;
    }

    public T getAddTransaction() {
        return this.addTransaction;
    }

    @Nonnull
    public MoveTransaction<T> toInverted(@Nonnull ItemContainer itemContainer) {
        return new MoveTransaction<T>(this.succeeded(), this.removeTransaction, MoveType.MOVE_TO_SELF, itemContainer, this.addTransaction);
    }

    @Override
    public boolean wasSlotModified(short slot) {
        if (!this.succeeded) {
            return false;
        }
        return this.addTransaction.succeeded() && this.addTransaction.wasSlotModified(slot) || this.removeTransaction.succeeded() && this.removeTransaction.wasSlotModified(slot);
    }

    @Override
    @Nonnull
    public MoveTransaction<T> toParent(ItemContainer parent, short start, ItemContainer container) {
        MoveType moveType = this.getMoveType();
        return switch (moveType) {
            default -> throw new MatchException(null, null);
            case MoveType.MOVE_TO_SELF -> new MoveTransaction<Transaction>(this.succeeded(), this.removeTransaction, moveType, this.getOtherContainer(), this.addTransaction.toParent(parent, start, container));
            case MoveType.MOVE_FROM_SELF -> new MoveTransaction<T>(this.succeeded(), this.removeTransaction.toParent(parent, start, container), moveType, this.getOtherContainer(), this.addTransaction);
        };
    }

    @Override
    @Nullable
    public MoveTransaction<T> fromParent(ItemContainer parent, short start, @Nonnull ItemContainer container) {
        MoveType moveType = this.getMoveType();
        switch (moveType) {
            case MOVE_TO_SELF: {
                Transaction newAddTransaction = this.addTransaction.fromParent(parent, start, container);
                if (newAddTransaction == null) {
                    return null;
                }
                return new MoveTransaction<Transaction>(this.succeeded(), this.getRemoveTransaction(), this.getMoveType(), this.getOtherContainer(), newAddTransaction);
            }
            case MOVE_FROM_SELF: {
                SlotTransaction newRemoveTransaction = this.getRemoveTransaction().fromParent(parent, start, container);
                if (newRemoveTransaction == null) {
                    return null;
                }
                return new MoveTransaction<T>(this.succeeded(), newRemoveTransaction, this.getMoveType(), this.getOtherContainer(), this.addTransaction);
            }
        }
        throw new IllegalStateException("Unexpected value: " + String.valueOf((Object)moveType));
    }

    @Nonnull
    public String toString() {
        return "MoveTransaction{succeeded=" + this.succeeded + ", removeTransaction=" + String.valueOf(this.removeTransaction) + ", moveType=" + String.valueOf((Object)this.moveType) + ", otherContainer=" + String.valueOf(this.otherContainer) + ", addTransaction=" + String.valueOf(this.addTransaction) + "}";
    }
}

