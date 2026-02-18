/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.CompactionStopStyle;
import org.rocksdb.RocksObject;

public class CompactionOptionsUniversal
extends RocksObject {
    public CompactionOptionsUniversal() {
        super(CompactionOptionsUniversal.newCompactionOptionsUniversal());
    }

    public CompactionOptionsUniversal setSizeRatio(int n) {
        CompactionOptionsUniversal.setSizeRatio(this.nativeHandle_, n);
        return this;
    }

    public int sizeRatio() {
        return CompactionOptionsUniversal.sizeRatio(this.nativeHandle_);
    }

    public CompactionOptionsUniversal setMinMergeWidth(int n) {
        CompactionOptionsUniversal.setMinMergeWidth(this.nativeHandle_, n);
        return this;
    }

    public int minMergeWidth() {
        return CompactionOptionsUniversal.minMergeWidth(this.nativeHandle_);
    }

    public CompactionOptionsUniversal setMaxMergeWidth(int n) {
        CompactionOptionsUniversal.setMaxMergeWidth(this.nativeHandle_, n);
        return this;
    }

    public int maxMergeWidth() {
        return CompactionOptionsUniversal.maxMergeWidth(this.nativeHandle_);
    }

    public CompactionOptionsUniversal setMaxSizeAmplificationPercent(int n) {
        CompactionOptionsUniversal.setMaxSizeAmplificationPercent(this.nativeHandle_, n);
        return this;
    }

    public int maxSizeAmplificationPercent() {
        return CompactionOptionsUniversal.maxSizeAmplificationPercent(this.nativeHandle_);
    }

    public CompactionOptionsUniversal setCompressionSizePercent(int n) {
        CompactionOptionsUniversal.setCompressionSizePercent(this.nativeHandle_, n);
        return this;
    }

    public int compressionSizePercent() {
        return CompactionOptionsUniversal.compressionSizePercent(this.nativeHandle_);
    }

    public CompactionOptionsUniversal setStopStyle(CompactionStopStyle compactionStopStyle) {
        CompactionOptionsUniversal.setStopStyle(this.nativeHandle_, compactionStopStyle.getValue());
        return this;
    }

    public CompactionStopStyle stopStyle() {
        return CompactionStopStyle.getCompactionStopStyle(CompactionOptionsUniversal.stopStyle(this.nativeHandle_));
    }

    public CompactionOptionsUniversal setAllowTrivialMove(boolean bl) {
        CompactionOptionsUniversal.setAllowTrivialMove(this.nativeHandle_, bl);
        return this;
    }

    public boolean allowTrivialMove() {
        return CompactionOptionsUniversal.allowTrivialMove(this.nativeHandle_);
    }

    private static native long newCompactionOptionsUniversal();

    @Override
    protected final void disposeInternal(long l) {
        CompactionOptionsUniversal.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    private static native void setSizeRatio(long var0, int var2);

    private static native int sizeRatio(long var0);

    private static native void setMinMergeWidth(long var0, int var2);

    private static native int minMergeWidth(long var0);

    private static native void setMaxMergeWidth(long var0, int var2);

    private static native int maxMergeWidth(long var0);

    private static native void setMaxSizeAmplificationPercent(long var0, int var2);

    private static native int maxSizeAmplificationPercent(long var0);

    private static native void setCompressionSizePercent(long var0, int var2);

    private static native int compressionSizePercent(long var0);

    private static native void setStopStyle(long var0, byte var2);

    private static native byte stopStyle(long var0);

    private static native void setAllowTrivialMove(long var0, boolean var2);

    private static native boolean allowTrivialMove(long var0);
}

