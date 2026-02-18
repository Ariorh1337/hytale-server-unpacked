/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum WALRecoveryMode {
    TolerateCorruptedTailRecords(0),
    AbsoluteConsistency(1),
    PointInTimeRecovery(2),
    SkipAnyCorruptedRecords(3);

    private final byte value;

    private WALRecoveryMode(byte by) {
        this.value = by;
    }

    public byte getValue() {
        return this.value;
    }

    public static WALRecoveryMode getWALRecoveryMode(byte by) {
        for (WALRecoveryMode wALRecoveryMode : WALRecoveryMode.values()) {
            if (wALRecoveryMode.getValue() != by) continue;
            return wALRecoveryMode;
        }
        throw new IllegalArgumentException("Illegal value provided for WALRecoveryMode.");
    }
}

