/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum ReusedSynchronisationType {
    MUTEX(0),
    ADAPTIVE_MUTEX(1),
    THREAD_LOCAL(2);

    private final byte value;

    private ReusedSynchronisationType(byte by) {
        this.value = by;
    }

    public byte getValue() {
        return this.value;
    }

    public static ReusedSynchronisationType getReusedSynchronisationType(byte by) {
        for (ReusedSynchronisationType reusedSynchronisationType : ReusedSynchronisationType.values()) {
            if (reusedSynchronisationType.getValue() != by) continue;
            return reusedSynchronisationType;
        }
        throw new IllegalArgumentException("Illegal value provided for ReusedSynchronisationType.");
    }
}

