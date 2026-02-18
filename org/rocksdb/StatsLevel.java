/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum StatsLevel {
    EXCEPT_DETAILED_TIMERS(0),
    EXCEPT_TIME_FOR_MUTEX(1),
    ALL(2);

    private final byte value;

    private StatsLevel(byte by) {
        this.value = by;
    }

    public byte getValue() {
        return this.value;
    }

    public static StatsLevel getStatsLevel(byte by) {
        for (StatsLevel statsLevel : StatsLevel.values()) {
            if (statsLevel.getValue() != by) continue;
            return statsLevel;
        }
        throw new IllegalArgumentException("Illegal value provided for StatsLevel.");
    }
}

