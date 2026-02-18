/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum SizeApproximationFlag {
    NONE(0),
    INCLUDE_MEMTABLES(1),
    INCLUDE_FILES(2);

    private final byte value;

    private SizeApproximationFlag(byte by) {
        this.value = by;
    }

    byte getValue() {
        return this.value;
    }
}

