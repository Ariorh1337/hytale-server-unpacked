/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum CompactionStopStyle {
    CompactionStopStyleSimilarSize(0),
    CompactionStopStyleTotalSize(1);

    private final byte value;

    private CompactionStopStyle(byte by) {
        this.value = by;
    }

    public byte getValue() {
        return this.value;
    }

    public static CompactionStopStyle getCompactionStopStyle(byte by) {
        for (CompactionStopStyle compactionStopStyle : CompactionStopStyle.values()) {
            if (compactionStopStyle.getValue() != by) continue;
            return compactionStopStyle;
        }
        throw new IllegalArgumentException("Illegal value provided for CompactionStopStyle.");
    }
}

