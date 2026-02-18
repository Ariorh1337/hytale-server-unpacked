/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum ThreadType {
    HIGH_PRIORITY(0),
    LOW_PRIORITY(1),
    USER(2),
    BOTTOM_PRIORITY(3);

    private final byte value;

    private ThreadType(byte by) {
        this.value = by;
    }

    byte getValue() {
        return this.value;
    }

    static ThreadType fromValue(byte by) throws IllegalArgumentException {
        for (ThreadType threadType : ThreadType.values()) {
            if (threadType.value != by) continue;
            return threadType;
        }
        throw new IllegalArgumentException("Unknown value for ThreadType: " + by);
    }
}

