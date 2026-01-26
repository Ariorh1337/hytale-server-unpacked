/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

import com.github.luben.zstd.SharedDictBase;
import com.github.luben.zstd.util.Native;
import java.nio.ByteBuffer;

public class ZstdDictDecompress
extends SharedDictBase {
    private long nativePtr = 0L;
    private ByteBuffer sharedDict = null;

    private native void init(byte[] var1, int var2, int var3);

    private native void initDirect(ByteBuffer var1, int var2, int var3, int var4);

    private native void free();

    public ByteBuffer getByReferenceBuffer() {
        return this.sharedDict;
    }

    public ZstdDictDecompress(byte[] byArray) {
        this(byArray, 0, byArray.length);
    }

    public ZstdDictDecompress(byte[] byArray, int n, int n2) {
        this.init(byArray, n, n2);
        if (this.nativePtr == 0L) {
            throw new IllegalStateException("ZSTD_createDDict failed");
        }
        this.storeFence();
    }

    public ZstdDictDecompress(ByteBuffer byteBuffer) {
        this(byteBuffer, false);
    }

    public ZstdDictDecompress(ByteBuffer byteBuffer, boolean bl) {
        int n = byteBuffer.limit() - byteBuffer.position();
        if (!byteBuffer.isDirect()) {
            throw new IllegalArgumentException("dict must be a direct buffer");
        }
        if (n < 0) {
            throw new IllegalArgumentException("dict cannot be empty.");
        }
        this.initDirect(byteBuffer, byteBuffer.position(), n, bl ? 1 : 0);
        if (this.nativePtr == 0L) {
            throw new IllegalStateException("ZSTD_createDDict failed");
        }
        if (bl) {
            this.sharedDict = byteBuffer;
        }
        this.storeFence();
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

