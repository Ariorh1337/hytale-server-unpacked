/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.nio.ByteBuffer;
import org.rocksdb.AbstractSlice;

public class DirectSlice
extends AbstractSlice<ByteBuffer> {
    public static final DirectSlice NONE = new DirectSlice();
    private final boolean internalBuffer;
    private volatile boolean cleared = false;
    private volatile long internalBufferOffset = 0L;

    DirectSlice() {
        this.internalBuffer = false;
    }

    public DirectSlice(String string) {
        super(DirectSlice.createNewSliceFromString(string));
        this.internalBuffer = true;
    }

    public DirectSlice(ByteBuffer byteBuffer, int n) {
        super(DirectSlice.createNewDirectSlice0(DirectSlice.ensureDirect(byteBuffer), n));
        this.internalBuffer = false;
    }

    public DirectSlice(ByteBuffer byteBuffer) {
        super(DirectSlice.createNewDirectSlice1(DirectSlice.ensureDirect(byteBuffer)));
        this.internalBuffer = false;
    }

    private static ByteBuffer ensureDirect(ByteBuffer byteBuffer) {
        if (!byteBuffer.isDirect()) {
            throw new IllegalArgumentException("The ByteBuffer must be direct");
        }
        return byteBuffer;
    }

    public byte get(int n) {
        return DirectSlice.get0(this.getNativeHandle(), n);
    }

    @Override
    public void clear() {
        DirectSlice.clear0(this.getNativeHandle(), !this.cleared && this.internalBuffer, this.internalBufferOffset);
        this.cleared = true;
    }

    @Override
    public void removePrefix(int n) {
        DirectSlice.removePrefix0(this.getNativeHandle(), n);
        this.internalBufferOffset += (long)n;
    }

    public void setLength(int n) {
        DirectSlice.setLength0(this.getNativeHandle(), n);
    }

    @Override
    protected void disposeInternal() {
        long l = this.getNativeHandle();
        if (!this.cleared && this.internalBuffer) {
            DirectSlice.disposeInternalBuf(l, this.internalBufferOffset);
        }
        this.disposeInternal(l);
    }

    private static native long createNewDirectSlice0(ByteBuffer var0, int var1);

    private static native long createNewDirectSlice1(ByteBuffer var0);

    @Override
    protected final native ByteBuffer data0(long var1);

    private static native byte get0(long var0, int var2);

    private static native void clear0(long var0, boolean var2, long var3);

    private static native void removePrefix0(long var0, int var2);

    private static native void setLength0(long var0, int var2);

    private static native void disposeInternalBuf(long var0, long var2);
}

