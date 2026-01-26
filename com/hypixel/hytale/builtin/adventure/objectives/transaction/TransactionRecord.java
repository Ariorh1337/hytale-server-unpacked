/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.objectives.transaction;

import com.hypixel.hytale.builtin.adventure.objectives.transaction.SpawnEntityTransactionRecord;
import com.hypixel.hytale.builtin.adventure.objectives.transaction.SpawnTreasureChestTransactionRecord;
import com.hypixel.hytale.builtin.adventure.objectives.transaction.TransactionStatus;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.common.util.ArrayUtil;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class TransactionRecord {
    public static final CodecMapCodec<TransactionRecord> CODEC = new CodecMapCodec("Type");
    public static final BuilderCodec<TransactionRecord> BASE_CODEC = ((BuilderCodec.Builder)BuilderCodec.abstractBuilder(TransactionRecord.class).append(new KeyedCodec<TransactionStatus>("Status", new EnumCodec<TransactionStatus>(TransactionStatus.class, EnumCodec.EnumStyle.LEGACY)), (spawnEntityTransactionRecord, status) -> {
        spawnEntityTransactionRecord.status = status;
    }, spawnEntityTransactionRecord -> spawnEntityTransactionRecord.status).add()).build();
    protected TransactionStatus status = TransactionStatus.SUCCESS;
    private String reason;

    public TransactionStatus getStatus() {
        return this.status;
    }

    public abstract void revert();

    public abstract void complete();

    public abstract void unload();

    public abstract boolean shouldBeSerialized();

    @Nonnull
    public TransactionRecord fail(String reason) {
        this.status = TransactionStatus.FAIL;
        this.reason = reason;
        return this;
    }

    @Nonnull
    public String toString() {
        return "TransactionRecord{status=" + String.valueOf((Object)this.status) + ", reason='" + this.reason + "'}";
    }

    @Nonnull
    public static <T extends TransactionRecord> TransactionRecord[] appendTransaction(@Nullable TransactionRecord[] transactions, @Nonnull T transaction) {
        if (transactions == null) {
            return new TransactionRecord[]{transaction};
        }
        return ArrayUtil.append(transactions, transaction);
    }

    @Nonnull
    public static <T extends TransactionRecord> TransactionRecord[] appendFailedTransaction(TransactionRecord[] transactions, @Nonnull T transaction, String reason) {
        return TransactionRecord.appendTransaction(transactions, transaction.fail(reason));
    }

    static {
        CODEC.register("SpawnEntity", (Class<TransactionRecord>)SpawnEntityTransactionRecord.class, (Codec<TransactionRecord>)SpawnEntityTransactionRecord.CODEC);
        CODEC.register("SpawnBlock", (Class<TransactionRecord>)SpawnTreasureChestTransactionRecord.class, (Codec<TransactionRecord>)SpawnTreasureChestTransactionRecord.CODEC);
    }
}

