/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum CompactionPriority {
    ByCompensatedSize(0),
    OldestLargestSeqFirst(1),
    OldestSmallestSeqFirst(2),
    MinOverlappingRatio(3),
    RoundRobin(4);

    private final byte value;

    private CompactionPriority(byte by) {
        this.value = by;
    }

    public byte getValue() {
        return this.value;
    }

    public static CompactionPriority getCompactionPriority(byte by) {
        for (CompactionPriority compactionPriority : CompactionPriority.values()) {
            if (compactionPriority.getValue() != by) continue;
            return compactionPriority;
        }
        throw new IllegalArgumentException("Illegal value provided for CompactionPriority.");
    }
}

