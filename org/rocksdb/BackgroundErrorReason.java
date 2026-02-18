/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum BackgroundErrorReason {
    FLUSH(0),
    COMPACTION(1),
    WRITE_CALLBACK(2),
    MEMTABLE(3);

    private final byte value;

    private BackgroundErrorReason(byte by) {
        this.value = by;
    }

    byte getValue() {
        return this.value;
    }

    static BackgroundErrorReason fromValue(byte by) {
        for (BackgroundErrorReason backgroundErrorReason : BackgroundErrorReason.values()) {
            if (backgroundErrorReason.value != by) continue;
            return backgroundErrorReason;
        }
        throw new IllegalArgumentException("Illegal value provided for BackgroundErrorReason: " + by);
    }
}

