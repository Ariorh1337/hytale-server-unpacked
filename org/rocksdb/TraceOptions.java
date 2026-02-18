/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public class TraceOptions {
    private final long maxTraceFileSize;

    public TraceOptions() {
        this.maxTraceFileSize = 0x1000000000L;
    }

    public TraceOptions(long l) {
        this.maxTraceFileSize = l;
    }

    public long getMaxTraceFileSize() {
        return this.maxTraceFileSize;
    }
}

