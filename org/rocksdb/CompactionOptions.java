/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.CompressionType;
import org.rocksdb.RocksObject;

public class CompactionOptions
extends RocksObject {
    public CompactionOptions() {
        super(CompactionOptions.newCompactionOptions());
    }

    public CompressionType compression() {
        return CompressionType.getCompressionType(CompactionOptions.compression(this.nativeHandle_));
    }

    public CompactionOptions setCompression(CompressionType compressionType) {
        CompactionOptions.setCompression(this.nativeHandle_, compressionType.getValue());
        return this;
    }

    public long outputFileSizeLimit() {
        return CompactionOptions.outputFileSizeLimit(this.nativeHandle_);
    }

    public CompactionOptions setOutputFileSizeLimit(long l) {
        CompactionOptions.setOutputFileSizeLimit(this.nativeHandle_, l);
        return this;
    }

    public int maxSubcompactions() {
        return CompactionOptions.maxSubcompactions(this.nativeHandle_);
    }

    public CompactionOptions setMaxSubcompactions(int n) {
        CompactionOptions.setMaxSubcompactions(this.nativeHandle_, n);
        return this;
    }

    private static native long newCompactionOptions();

    @Override
    protected final void disposeInternal(long l) {
        CompactionOptions.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    private static native byte compression(long var0);

    private static native void setCompression(long var0, byte var2);

    private static native long outputFileSizeLimit(long var0);

    private static native void setOutputFileSizeLimit(long var0, long var2);

    private static native int maxSubcompactions(long var0);

    private static native void setMaxSubcompactions(long var0, int var2);
}

