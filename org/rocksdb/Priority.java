/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum Priority {
    BOTTOM(0),
    LOW(1),
    HIGH(2),
    TOTAL(3);

    private final byte value;

    private Priority(byte by) {
        this.value = by;
    }

    byte getValue() {
        return this.value;
    }

    static Priority getPriority(byte by) {
        for (Priority priority : Priority.values()) {
            if (priority.getValue() != by) continue;
            return priority;
        }
        throw new IllegalArgumentException("Illegal value provided for Priority.");
    }
}

