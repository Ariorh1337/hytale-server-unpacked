/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.AbstractSlice;

public class Slice
extends AbstractSlice<byte[]> {
    private volatile boolean cleared;
    private volatile long internalBufferOffset = 0L;

    private Slice() {
    }

    Slice(long l) {
        this(l, false);
    }

    Slice(long l, boolean bl) {
        this.setNativeHandle(l, bl);
    }

    public Slice(String string) {
        super(Slice.createNewSliceFromString(string));
    }

    public Slice(byte[] byArray, int n) {
        super(Slice.createNewSlice0(byArray, n));
    }

    public Slice(byte[] byArray) {
        super(Slice.createNewSlice1(byArray));
    }

    @Override
    public void clear() {
        Slice.clear0(this.getNativeHandle(), !this.cleared, this.internalBufferOffset);
        this.cleared = true;
    }

    @Override
    public void removePrefix(int n) {
        Slice.removePrefix0(this.getNativeHandle(), n);
        this.internalBufferOffset += (long)n;
    }

    @Override
    protected void disposeInternal() {
        long l = this.getNativeHandle();
        if (!this.cleared) {
            Slice.disposeInternalBuf(l, this.internalBufferOffset);
        }
        super.disposeInternal(l);
    }

    @Override
    protected final native byte[] data0(long var1);

    private static native long createNewSlice0(byte[] var0, int var1);

    private static native long createNewSlice1(byte[] var0);

    private static native void clear0(long var0, boolean var2, long var3);

    private static native void removePrefix0(long var0, int var2);

    private static native void disposeInternalBuf(long var0, long var2);
}

