/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

import com.github.luben.zstd.ZstdDictCompress;
import com.github.luben.zstd.ZstdDirectBufferCompressingStreamNoFinalizer;
import com.github.luben.zstd.util.Native;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ZstdDirectBufferCompressingStream
implements Closeable,
Flushable {
    ZstdDirectBufferCompressingStreamNoFinalizer inner;
    private boolean finalize;

    protected ByteBuffer flushBuffer(ByteBuffer byteBuffer) throws IOException {
        return byteBuffer;
    }

    public ZstdDirectBufferCompressingStream(ByteBuffer byteBuffer, int n) throws IOException {
        this.inner = new ZstdDirectBufferCompressingStreamNoFinalizer(byteBuffer, n){

            @Override
            protected ByteBuffer flushBuffer(ByteBuffer byteBuffer) throws IOException {
                return ZstdDirectBufferCompressingStream.this.flushBuffer(byteBuffer);
            }
        };
    }

    public static int recommendedOutputBufferSize() {
        return ZstdDirectBufferCompressingStreamNoFinalizer.recommendedOutputBufferSize();
    }

    public synchronized ZstdDirectBufferCompressingStream setDict(byte[] byArray) throws IOException {
        this.inner.setDict(byArray);
        return this;
    }

    public synchronized ZstdDirectBufferCompressingStream setDict(ZstdDictCompress zstdDictCompress) throws IOException {
        this.inner.setDict(zstdDictCompress);
        return this;
    }

    public void setFinalize(boolean bl) {
        this.finalize = bl;
    }

    public synchronized void compress(ByteBuffer byteBuffer) throws IOException {
        this.inner.compress(byteBuffer);
    }

    @Override
    public synchronized void flush() throws IOException {
        this.inner.flush();
    }

    @Override
    public synchronized void close() throws IOException {
        this.inner.close();
    }

    protected void finalize() throws Throwable {
        if (this.finalize) {
            this.close();
        }
    }

    static {
        Native.load();
    }
}

