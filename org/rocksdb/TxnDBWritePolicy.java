/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum TxnDBWritePolicy {
    WRITE_COMMITTED(0),
    WRITE_PREPARED(1),
    WRITE_UNPREPARED(2);

    private final byte value;

    private TxnDBWritePolicy(byte by) {
        this.value = by;
    }

    public byte getValue() {
        return this.value;
    }

    public static TxnDBWritePolicy getTxnDBWritePolicy(byte by) {
        for (TxnDBWritePolicy txnDBWritePolicy : TxnDBWritePolicy.values()) {
            if (txnDBWritePolicy.getValue() != by) continue;
            return txnDBWritePolicy;
        }
        throw new IllegalArgumentException("Illegal value provided for TxnDBWritePolicy.");
    }
}

