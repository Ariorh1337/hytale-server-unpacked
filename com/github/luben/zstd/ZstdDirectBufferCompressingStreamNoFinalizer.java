/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdDictCompress;
import com.github.luben.zstd.ZstdIOException;
import com.github.luben.zstd.util.Native;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ZstdDirectBufferCompressingStreamNoFinalizer
implements Closeable,
Flushable {
    private ByteBuffer target;
    private final long stream;
    private int consumed = 0;
    private int produced = 0;
    private boolean closed = false;
    private boolean initialized = false;
    private int level = Zstd.defaultCompressionLevel();
    private byte[] dict = null;
    private ZstdDictCompress fastDict = null;

    protected ByteBuffer flushBuffer(ByteBuffer byteBuffer) throws IOException {
        return byteBuffer;
    }

    public ZstdDirectBufferCompressingStreamNoFinalizer(ByteBuffer byteBuffer, int n) throws IOException {
        if (!byteBuffer.isDirect()) {
            throw new IllegalArgumentException("Target buffer should be a direct buffer");
        }
        this.target = byteBuffer;
        this.level = n;
        this.stream = ZstdDirectBufferCompressingStreamNoFinalizer.createCStream();
    }

    public static int recommendedOutputBufferSize() {
        return (int)ZstdDirectBufferCompressingStreamNoFinalizer.recommendedCOutSize();
    }

    private static native long recommendedCOutSize();

    private static native long createCStream();

    private static native long freeCStream(long var0);

    private native long initCStream(long var1, int var3);

    private native long initCStreamWithDict(long var1, byte[] var3, int var4, int var5);

    private native long initCStreamWithFastDict(long var1, ZstdDictCompress var3);

    private native long compressDirectByteBuffer(long var1, ByteBuffer var3, int var4, int var5, ByteBuffer var6, int var7, int var8);

    private native long flushStream(long var1, ByteBuffer var3, int var4, int var5);

    private native long endStream(long var1, ByteBuffer var3, int var4, int var5);

    public ZstdDirectBufferCompressingStreamNoFinalizer setDict(byte[] byArray) {
        if (this.initialized) {
            throw new IllegalStateException("Change of parameter on initialized stream");
        }
        this.dict = byArray;
        this.fastDict = null;
        return this;
    }

    public ZstdDirectBufferCompressingStreamNoFinalizer setDict(ZstdDictCompress zstdDictCompress) {
        if (this.initialized) {
            throw new IllegalStateException("Change of parameter on initialized stream");
        }
        this.dict = null;
        this.fastDict = zstdDictCompress;
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void compress(ByteBuffer byteBuffer) throws IOException {
        long l;
        if (!byteBuffer.isDirect()) {
            throw new IllegalArgumentException("Source buffer should be a direct buffer");
        }
        if (this.closed) {
            throw new IOException("Stream closed");
        }
        if (!this.initialized) {
            l = 0L;
            ZstdDictCompress zstdDictCompress = this.fastDict;
            if (zstdDictCompress != null) {
                zstdDictCompress.acquireSharedLock();
                try {
                    l = this.initCStreamWithFastDict(this.stream, zstdDictCompress);
                }
                finally {
                    zstdDictCompress.releaseSharedLock();
                }
            } else {
                l = this.dict != null ? this.initCStreamWithDict(this.stream, this.dict, this.dict.length, this.level) : this.initCStream(this.stream, this.level);
            }
            if (Zstd.isError(l)) {
                throw new ZstdIOException(l);
            }
            this.initialized = true;
        }
        while (byteBuffer.hasRemaining()) {
            if (!this.target.hasRemaining()) {
                this.target = this.flushBuffer(this.target);
                if (!this.target.isDirect()) {
                    throw new IllegalArgumentException("Target buffer should be a direct buffer");
                }
                if (!this.target.hasRemaining()) {
                    throw new IOException("The target buffer has no more space, even after flushing, and there are still bytes to compress");
                }
            }
            if (Zstd.isError(l = this.compressDirectByteBuffer(this.stream, this.target, this.target.position(), this.target.remaining(), byteBuffer, byteBuffer.position(), byteBuffer.remaining()))) {
                throw new ZstdIOException(l);
            }
            this.target.position(this.target.position() + this.produced);
            byteBuffer.position(byteBuffer.position() + this.consumed);
        }
    }

    @Override
    public void flush() throws IOException {
        if (this.closed) {
            throw new IOException("Already closed");
        }
        if (this.initialized) {
            long l;
            do {
                if (Zstd.isError(l = this.flushStream(this.stream, this.target, this.target.position(), this.target.remaining()))) {
                    throw new ZstdIOException(l);
                }
                this.target.position(this.target.position() + this.produced);
                this.target = this.flushBuffer(this.target);
                if (!this.target.isDirect()) {
                    throw new IllegalArgumentException("Target buffer should be a direct buffer");
                }
                if (l <= 0L || this.target.hasRemaining()) continue;
                throw new IOException("The target buffer has no more space, even after flushing, and there are still bytes to compress");
            } while (l > 0L);
        }
    }

    @Override
    public void close() throws IOException {
        if (!this.closed) {
            try {
                if (this.initialized) {
                    long l;
                    do {
                        if (Zstd.isError(l = this.endStream(this.stream, this.target, this.target.position(), this.target.remaining()))) {
                            throw new ZstdIOException(l);
                        }
                        this.target.position(this.target.position() + this.produced);
                        this.target = this.flushBuffer(this.target);
                        if (!this.target.isDirect()) {
                            throw new IllegalArgumentException("Target buffer should be a direct buffer");
                        }
                        if (l <= 0L || this.target.hasRemaining()) continue;
                        throw new IOException("The target buffer has no more space, even after flushing, and there are still bytes to compress");
                    } while (l > 0L);
                }
            }
            finally {
                ZstdDirectBufferCompressingStreamNoFinalizer.freeCStream(this.stream);
                this.closed = true;
                this.initialized = false;
                this.target = null;
            }
        }
    }

    static {
        Native.load();
    }
}

