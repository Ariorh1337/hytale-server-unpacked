/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum ReadTier {
    READ_ALL_TIER(0),
    BLOCK_CACHE_TIER(1),
    PERSISTED_TIER(2),
    MEMTABLE_TIER(3);

    private final byte value;

    private ReadTier(byte by) {
        this.value = by;
    }

    public byte getValue() {
        return this.value;
    }

    public static ReadTier getReadTier(byte by) {
        for (ReadTier readTier : ReadTier.values()) {
            if (readTier.getValue() != by) continue;
            return readTier;
        }
        throw new IllegalArgumentException("Illegal value provided for ReadTier.");
    }
}

