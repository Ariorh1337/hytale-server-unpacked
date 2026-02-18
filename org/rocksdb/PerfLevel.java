/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum PerfLevel {
    UNINITIALIZED(0),
    DISABLE(1),
    ENABLE_COUNT(2),
    ENABLE_TIME_EXCEPT_FOR_MUTEX(3),
    ENABLE_TIME_AND_CPU_TIME_EXCEPT_FOR_MUTEX(4),
    ENABLE_TIME(5),
    OUT_OF_BOUNDS(6);

    private final byte _value;

    private PerfLevel(byte by) {
        this._value = by;
    }

    public byte getValue() {
        return this._value;
    }

    public static PerfLevel getPerfLevel(byte by) {
        for (PerfLevel perfLevel : PerfLevel.values()) {
            if (perfLevel.getValue() != by) continue;
            return perfLevel;
        }
        throw new IllegalArgumentException("Uknknown PerfLevel constant : " + by);
    }
}

