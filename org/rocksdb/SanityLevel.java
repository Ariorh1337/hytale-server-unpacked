/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum SanityLevel {
    NONE(0),
    LOOSELY_COMPATIBLE(1),
    EXACT_MATCH(-1);

    private final byte value;

    private SanityLevel(byte by) {
        this.value = by;
    }

    byte getValue() {
        return this.value;
    }

    static SanityLevel fromValue(byte by) throws IllegalArgumentException {
        for (SanityLevel sanityLevel : SanityLevel.values()) {
            if (sanityLevel.value != by) continue;
            return sanityLevel;
        }
        throw new IllegalArgumentException("Unknown value for SanityLevel: " + by);
    }
}

