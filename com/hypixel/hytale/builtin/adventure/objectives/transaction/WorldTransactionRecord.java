/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.objectives.transaction;

import com.hypixel.hytale.builtin.adventure.objectives.transaction.TransactionRecord;
import javax.annotation.Nonnull;

public class WorldTransactionRecord
extends TransactionRecord {
    @Override
    public void revert() {
    }

    @Override
    public void complete() {
    }

    @Override
    public void unload() {
    }

    @Override
    public boolean shouldBeSerialized() {
        return false;
    }

    @Override
    @Nonnull
    public String toString() {
        return "WorldTransactionRecord{} " + super.toString();
    }
}

