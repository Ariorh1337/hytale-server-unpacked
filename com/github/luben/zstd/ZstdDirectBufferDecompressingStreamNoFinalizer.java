/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

import com.github.luben.zstd.BaseZstdBufferDecompressingStreamNoFinalizer;
import com.github.luben.zstd.util.Native;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ZstdDirectBufferDecompressingStreamNoFinalizer
extends BaseZstdBufferDecompressingStreamNoFinalizer {
    public ZstdDirectBufferDecompressingStreamNoFinalizer(ByteBuffer byteBuffer) {
        super(byteBuffer);
        if (!byteBuffer.isDirect()) {
            throw new IllegalArgumentException("Source buffer should be a direct buffer");
        }
        this.source = byteBuffer;
        this.stream = this.createDStream();
        this.initDStream(this.stream);
    }

    @Override
    public int read(ByteBuffer byteBuffer) throws IOException {
        if (!byteBuffer.isDirect()) {
            throw new IllegalArgumentException("Target buffer should be a direct buffer");
        }
        return this.readInternal(byteBuffer, true);
    }

    @Override
    long createDStream() {
        return ZstdDirectBufferDecompressingStreamNoFinalizer.createDStreamNative();
    }

    @Override
    long freeDStream(long l) {
        return ZstdDirectBufferDecompressingStreamNoFinalizer.freeDStreamNative(l);
    }

    @Override
    long initDStream(long l) {
        return this.initDStreamNative(l);
    }

    @Override
    long decompressStream(long l, ByteBuffer byteBuffer, int n, int n2, ByteBuffer byteBuffer2, int n3, int n4) {
        return this.decompressStreamNative(l, byteBuffer, n, n2, byteBuffer2, n3, n4);
    }

    public static int recommendedTargetBufferSize() {
        return (int)ZstdDirectBufferDecompressingStreamNoFinalizer.recommendedDOutSizeNative();
    }

    private static native long createDStreamNative();

    private static native long freeDStreamNative(long var0);

    private native long initDStreamNative(long var1);

    private native long decompressStreamNative(long var1, ByteBuffer var3, int var4, int var5, ByteBuffer var6, int var7, int var8);

    private static native long recommendedDOutSizeNative();

    static {
        Native.load();
    }
}

