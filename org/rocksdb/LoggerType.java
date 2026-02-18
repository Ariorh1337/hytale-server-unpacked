/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum LoggerType {
    JAVA_IMPLEMENTATION(1),
    STDERR_IMPLEMENTATION(2);

    private final byte value;

    private LoggerType(byte by) {
        this.value = by;
    }

    byte getValue() {
        return this.value;
    }

    static LoggerType getLoggerType(byte by) {
        for (LoggerType loggerType : LoggerType.values()) {
            if (loggerType.getValue() != by) continue;
            return loggerType;
        }
        throw new IllegalArgumentException("Illegal value provided for LoggerType.");
    }
}

