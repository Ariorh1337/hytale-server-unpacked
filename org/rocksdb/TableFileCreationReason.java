/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum TableFileCreationReason {
    FLUSH(0),
    COMPACTION(1),
    RECOVERY(2),
    MISC(3);

    private final byte value;

    private TableFileCreationReason(byte by) {
        this.value = by;
    }

    byte getValue() {
        return this.value;
    }

    static TableFileCreationReason fromValue(byte by) {
        for (TableFileCreationReason tableFileCreationReason : TableFileCreationReason.values()) {
            if (tableFileCreationReason.value != by) continue;
            return tableFileCreationReason;
        }
        throw new IllegalArgumentException("Illegal value provided for TableFileCreationReason: " + by);
    }
}

