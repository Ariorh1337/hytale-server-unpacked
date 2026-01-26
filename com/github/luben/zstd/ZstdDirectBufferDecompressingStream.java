/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

import com.github.luben.zstd.ZstdDictDecompress;
import com.github.luben.zstd.ZstdDirectBufferDecompressingStreamNoFinalizer;
import com.github.luben.zstd.util.Native;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ZstdDirectBufferDecompressingStream
implements Closeable {
    private ZstdDirectBufferDecompressingStreamNoFinalizer inner;
    private boolean finalize = true;

    protected ByteBuffer refill(ByteBuffer byteBuffer) {
        return byteBuffer;
    }

    public ZstdDirectBufferDecompressingStream(ByteBuffer byteBuffer) {
        this.inner = new ZstdDirectBufferDecompressingStreamNoFinalizer(byteBuffer){

            @Override
            protected ByteBuffer refill(ByteBuffer byteBuffer) {
                return ZstdDirectBufferDecompressingStream.this.refill(byteBuffer);
            }
        };
    }

    public void setFinalize(boolean bl) {
        this.finalize = bl;
    }

    public synchronized boolean hasRemaining() {
        return this.inner.hasRemaining();
    }

    public static int recommendedTargetBufferSize() {
        return ZstdDirectBufferDecompressingStreamNoFinalizer.recommendedTargetBufferSize();
    }

    public synchronized ZstdDirectBufferDecompressingStream setDict(byte[] byArray) throws IOException {
        this.inner.setDict(byArray);
        return this;
    }

    public synchronized ZstdDirectBufferDecompressingStream setDict(ZstdDictDecompress zstdDictDecompress) throws IOException {
        this.inner.setDict(zstdDictDecompress);
        return this;
    }

    public ZstdDirectBufferDecompressingStream setLongMax(int n) throws IOException {
        this.inner.setLongMax(n);
        return this;
    }

    public synchronized int read(ByteBuffer byteBuffer) throws IOException {
        return this.inner.read(byteBuffer);
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

