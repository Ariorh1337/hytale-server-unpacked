/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum RateLimiterMode {
    READS_ONLY(0),
    WRITES_ONLY(1),
    ALL_IO(2);

    private final byte value;

    private RateLimiterMode(byte by) {
        this.value = by;
    }

    public byte getValue() {
        return this.value;
    }

    public static RateLimiterMode getRateLimiterMode(byte by) {
        for (RateLimiterMode rateLimiterMode : RateLimiterMode.values()) {
            if (rateLimiterMode.getValue() != by) continue;
            return rateLimiterMode;
        }
        throw new IllegalArgumentException("Illegal value provided for RateLimiterMode.");
    }
}

