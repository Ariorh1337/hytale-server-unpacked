/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum OperationType {
    OP_UNKNOWN(0),
    OP_COMPACTION(1),
    OP_FLUSH(2),
    OP_DBOPEN(3);

    private final byte value;

    private OperationType(byte by) {
        this.value = by;
    }

    byte getValue() {
        return this.value;
    }

    static OperationType fromValue(byte by) throws IllegalArgumentException {
        for (OperationType operationType : OperationType.values()) {
            if (operationType.value != by) continue;
            return operationType;
        }
        throw new IllegalArgumentException("Unknown value for OperationType: " + by);
    }
}

