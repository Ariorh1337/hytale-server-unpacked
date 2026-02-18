/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum WriteStallCondition {
    DELAYED(0),
    STOPPED(1),
    NORMAL(2);

    private final byte value;

    private WriteStallCondition(byte by) {
        this.value = by;
    }

    byte getValue() {
        return this.value;
    }

    static WriteStallCondition fromValue(byte by) {
        for (WriteStallCondition writeStallCondition : WriteStallCondition.values()) {
            if (writeStallCondition.value != by) continue;
            return writeStallCondition;
        }
        throw new IllegalArgumentException("Illegal value provided for WriteStallCondition: " + by);
    }
}

