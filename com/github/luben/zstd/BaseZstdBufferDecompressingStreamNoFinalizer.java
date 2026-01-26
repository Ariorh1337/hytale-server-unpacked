/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdDictDecompress;
import com.github.luben.zstd.ZstdIOException;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class BaseZstdBufferDecompressingStreamNoFinalizer
implements Closeable {
    protected long stream;
    protected ByteBuffer source;
    protected boolean closed = false;
    private boolean finishedFrame = false;
    private boolean streamEnd = false;
    private int consumed;
    private int produced;

    BaseZstdBufferDecompressingStreamNoFinalizer(ByteBuffer byteBuffer) {
        this.source = byteBuffer;
    }

    protected ByteBuffer refill(ByteBuffer byteBuffer) {
        return byteBuffer;
    }

    public boolean hasRemaining() {
        return !this.streamEnd && (this.source.hasRemaining() || !this.finishedFrame);
    }

    public BaseZstdBufferDecompressingStreamNoFinalizer setDict(byte[] byArray) throws IOException {
        long l = Zstd.loadDictDecompress(this.stream, byArray, byArray.length);
        if (Zstd.isError(l)) {
            throw new ZstdIOException(l);
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public BaseZstdBufferDecompressingStreamNoFinalizer setDict(ZstdDictDecompress zstdDictDecompress) throws IOException {
        zstdDictDecompress.acquireSharedLock();
        try {
            long l = Zstd.loadFastDictDecompress(this.stream, zstdDictDecompress);
            if (Zstd.isError(l)) {
                throw new ZstdIOException(l);
            }
        }
        finally {
            zstdDictDecompress.releaseSharedLock();
        }
        return this;
    }

    public BaseZstdBufferDecompressingStreamNoFinalizer setLongMax(int n) throws IOException {
        long l = Zstd.setDecompressionLongMax(this.stream, n);
        if (Zstd.isError(l)) {
            throw new ZstdIOException(l);
        }
        return this;
    }

    int readInternal(ByteBuffer byteBuffer, boolean bl) throws IOException {
        if (this.closed) {
            throw new IOException("Stream closed");
        }
        if (this.streamEnd) {
            return 0;
        }
        long l = this.decompressStream(this.stream, byteBuffer, byteBuffer.position(), byteBuffer.remaining(), this.source, this.source.position(), this.source.remaining());
        if (Zstd.isError(l)) {
            throw new ZstdIOException(l);
        }
        this.source.position(this.source.position() + this.consumed);
        byteBuffer.position(byteBuffer.position() + this.produced);
        if (!this.source.hasRemaining()) {
            this.source = this.refill(this.source);
            if (!bl && this.source.isDirect()) {
                throw new IllegalArgumentException("Source buffer should be a non-direct buffer");
            }
            if (bl && !this.source.isDirect()) {
                throw new IllegalArgumentException("Source buffer should be a direct buffer");
            }
        }
        boolean bl2 = this.finishedFrame = l == 0L;
        if (this.finishedFrame) {
            this.streamEnd = !this.source.hasRemaining();
        }
        return this.produced;
    }

    @Override
    public void close() {
        if (!this.closed) {
            try {
                this.freeDStream(this.stream);
            }
            finally {
                this.closed = true;
                this.source = null;
            }
        }
    }

    public abstract int read(ByteBuffer var1) throws IOException;

    abstract long createDStream();

    abstract long freeDStream(long var1);

    abstract long initDStream(long var1);

    abstract long decompressStream(long var1, ByteBuffer var3, int var4, int var5, ByteBuffer var6, int var7, int var8);
}

