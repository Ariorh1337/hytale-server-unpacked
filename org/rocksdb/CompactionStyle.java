/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum CompactionStyle {
    LEVEL(0),
    UNIVERSAL(1),
    FIFO(2),
    NONE(3);

    private final byte value;

    private CompactionStyle(byte by) {
        this.value = by;
    }

    public byte getValue() {
        return this.value;
    }

    static CompactionStyle fromValue(byte by) throws IllegalArgumentException {
        for (CompactionStyle compactionStyle : CompactionStyle.values()) {
            if (compactionStyle.value != by) continue;
            return compactionStyle;
        }
        throw new IllegalArgumentException("Unknown value for CompactionStyle: " + by);
    }
}

