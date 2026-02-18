/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.RocksObject;

public class CompressionOptions
extends RocksObject {
    public CompressionOptions() {
        super(CompressionOptions.newCompressionOptions());
    }

    public CompressionOptions setWindowBits(int n) {
        CompressionOptions.setWindowBits(this.nativeHandle_, n);
        return this;
    }

    public int windowBits() {
        return CompressionOptions.windowBits(this.nativeHandle_);
    }

    public CompressionOptions setLevel(int n) {
        CompressionOptions.setLevel(this.nativeHandle_, n);
        return this;
    }

    public int level() {
        return CompressionOptions.level(this.nativeHandle_);
    }

    public CompressionOptions setStrategy(int n) {
        CompressionOptions.setStrategy(this.nativeHandle_, n);
        return this;
    }

    public int strategy() {
        return CompressionOptions.strategy(this.nativeHandle_);
    }

    public CompressionOptions setMaxDictBytes(int n) {
        CompressionOptions.setMaxDictBytes(this.nativeHandle_, n);
        return this;
    }

    public int maxDictBytes() {
        return CompressionOptions.maxDictBytes(this.nativeHandle_);
    }

    public CompressionOptions setZStdMaxTrainBytes(int n) {
        CompressionOptions.setZstdMaxTrainBytes(this.nativeHandle_, n);
        return this;
    }

    public int zstdMaxTrainBytes() {
        return CompressionOptions.zstdMaxTrainBytes(this.nativeHandle_);
    }

    public CompressionOptions setEnabled(boolean bl) {
        CompressionOptions.setEnabled(this.nativeHandle_, bl);
        return this;
    }

    public boolean enabled() {
        return CompressionOptions.enabled(this.nativeHandle_);
    }

    private static native long newCompressionOptions();

    @Override
    protected final void disposeInternal(long l) {
        CompressionOptions.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    private static native void setWindowBits(long var0, int var2);

    private static native int windowBits(long var0);

    private static native void setLevel(long var0, int var2);

    private static native int level(long var0);

    private static native void setStrategy(long var0, int var2);

    private static native int strategy(long var0);

    private static native void setMaxDictBytes(long var0, int var2);

    private static native int maxDictBytes(long var0);

    private static native void setZstdMaxTrainBytes(long var0, int var2);

    private static native int zstdMaxTrainBytes(long var0);

    private static native void setEnabled(long var0, boolean var2);

    private static native boolean enabled(long var0);
}

