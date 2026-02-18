/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum InfoLogLevel {
    DEBUG_LEVEL(0),
    INFO_LEVEL(1),
    WARN_LEVEL(2),
    ERROR_LEVEL(3),
    FATAL_LEVEL(4),
    HEADER_LEVEL(5),
    NUM_INFO_LOG_LEVELS(6);

    private final byte value_;

    private InfoLogLevel(byte by) {
        this.value_ = by;
    }

    public byte getValue() {
        return this.value_;
    }

    public static InfoLogLevel getInfoLogLevel(byte by) {
        for (InfoLogLevel infoLogLevel : InfoLogLevel.values()) {
            if (infoLogLevel.getValue() != by) continue;
            return infoLogLevel;
        }
        throw new IllegalArgumentException("Illegal value provided for InfoLogLevel.");
    }
}

