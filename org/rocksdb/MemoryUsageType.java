/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum MemoryUsageType {
    kMemTableTotal(0),
    kMemTableUnFlushed(1),
    kTableReadersTotal(2),
    kCacheTotal(3),
    kNumUsageTypes(4);

    private final byte value_;

    public byte getValue() {
        return this.value_;
    }

    public static MemoryUsageType getMemoryUsageType(byte by) {
        for (MemoryUsageType memoryUsageType : MemoryUsageType.values()) {
            if (memoryUsageType.getValue() != by) continue;
            return memoryUsageType;
        }
        throw new IllegalArgumentException("Illegal value provided for MemoryUsageType.");
    }

    private MemoryUsageType(byte by) {
        this.value_ = by;
    }
}

