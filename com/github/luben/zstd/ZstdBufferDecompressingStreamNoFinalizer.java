/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

import com.github.luben.zstd.BaseZstdBufferDecompressingStreamNoFinalizer;
import com.github.luben.zstd.util.Native;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ZstdBufferDecompressingStreamNoFinalizer
extends BaseZstdBufferDecompressingStreamNoFinalizer {
    public ZstdBufferDecompressingStreamNoFinalizer(ByteBuffer byteBuffer) {
        super(byteBuffer);
        if (byteBuffer.isDirect()) {
            throw new IllegalArgumentException("Source buffer should be a non-direct buffer");
        }
        this.stream = this.createDStream();
        this.initDStream(this.stream);
    }

    @Override
    public int read(ByteBuffer byteBuffer) throws IOException {
        if (byteBuffer.isDirect()) {
            throw new IllegalArgumentException("Target buffer should be a non-direct buffer");
        }
        return this.readInternal(byteBuffer, false);
    }

    @Override
    long createDStream() {
        return this.createDStreamNative();
    }

    @Override
    long freeDStream(long l) {
        return this.freeDStreamNative(l);
    }

    @Override
    long initDStream(long l) {
        return this.initDStreamNative(l);
    }

    @Override
    long decompressStream(long l, ByteBuffer byteBuffer, int n, int n2, ByteBuffer byteBuffer2, int n3, int n4) {
        if (!byteBuffer2.hasArray()) {
            throw new IllegalArgumentException("provided source ByteBuffer lacks array");
        }
        if (!byteBuffer.hasArray()) {
            throw new IllegalArgumentException("provided destination ByteBuffer lacks array");
        }
        byte[] byArray = byteBuffer.array();
        byte[] byArray2 = byteBuffer2.array();
        return this.decompressStreamNative(l, byArray, n + byteBuffer.arrayOffset(), n2, byArray2, n3 + byteBuffer2.arrayOffset(), n4);
    }

    public static int recommendedTargetBufferSize() {
        return (int)ZstdBufferDecompressingStreamNoFinalizer.recommendedDOutSizeNative();
    }

    private native long createDStreamNative();

    private native long freeDStreamNative(long var1);

    private native long initDStreamNative(long var1);

    private native long decompressStreamNative(long var1, byte[] var3, int var4, int var5, byte[] var6, int var7, int var8);

    private static native long recommendedDOutSizeNative();

    static {
        Native.load();
    }
}

