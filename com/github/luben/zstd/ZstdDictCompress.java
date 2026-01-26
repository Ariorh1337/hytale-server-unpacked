/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

import com.github.luben.zstd.SharedDictBase;
import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.util.Native;
import java.nio.ByteBuffer;

public class ZstdDictCompress
extends SharedDictBase {
    private long nativePtr = 0L;
    private ByteBuffer sharedDict = null;
    private int level = Zstd.defaultCompressionLevel();

    private native void init(byte[] var1, int var2, int var3, int var4);

    private native void initDirect(ByteBuffer var1, int var2, int var3, int var4, int var5);

    private native void free();

    public ByteBuffer getByReferenceBuffer() {
        return this.sharedDict;
    }

    public ZstdDictCompress(byte[] byArray, int n) {
        this(byArray, 0, byArray.length, n);
    }

    public ZstdDictCompress(byte[] byArray, int n, int n2, int n3) {
        this.level = n3;
        if (byArray.length - n < 0) {
            throw new IllegalArgumentException("Dictionary buffer is to short");
        }
        this.init(byArray, n, n2, n3);
        if (0L == this.nativePtr) {
            throw new IllegalStateException("ZSTD_createCDict failed");
        }
        this.storeFence();
    }

    public ZstdDictCompress(ByteBuffer byteBuffer, int n) {
        this(byteBuffer, n, false);
    }

    public ZstdDictCompress(ByteBuffer byteBuffer, int n, boolean bl) {
        this.level = n;
        int n2 = byteBuffer.limit() - byteBuffer.position();
        if (!byteBuffer.isDirect()) {
            throw new IllegalArgumentException("dict must be a direct buffer");
        }
        if (n2 < 0) {
            throw new IllegalArgumentException("dict cannot be empty.");
        }
        this.initDirect(byteBuffer, byteBuffer.position(), n2, n, bl ? 1 : 0);
        if (this.nativePtr == 0L) {
            throw new IllegalStateException("ZSTD_createCDict failed");
        }
        if (bl) {
            this.sharedDict = byteBuffer;
        }
        this.storeFence();
    }

    int level() {
        return this.level;
    }

    @Override
    void doClose() {
        if (this.nativePtr != 0L) {
            this.free();
            this.nativePtr = 0L;
            this.sharedDict = null;
        }
    }

    static {
        Native.load();
    }
}

