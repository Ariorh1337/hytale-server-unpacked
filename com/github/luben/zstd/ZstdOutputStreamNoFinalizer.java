/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

import com.github.luben.zstd.BufferPool;
import com.github.luben.zstd.NoPool;
import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdDictCompress;
import com.github.luben.zstd.ZstdIOException;
import com.github.luben.zstd.util.Native;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ZstdOutputStreamNoFinalizer
extends FilterOutputStream {
    private final long stream = ZstdOutputStreamNoFinalizer.createCStream();
    private long srcPos = 0L;
    private long dstPos = 0L;
    private final BufferPool bufferPool;
    private final ByteBuffer dstByteBuffer;
    private final byte[] dst;
    private boolean isClosed = false;
    private static final int dstSize;
    private boolean closeFrameOnFlush = false;
    private boolean frameClosed = true;
    private boolean frameStarted = false;

    public static native long recommendedCOutSize();

    private static native long createCStream();

    private static native int freeCStream(long var0);

    private native int resetCStream(long var1);

    private native int compressStream(long var1, byte[] var3, int var4, byte[] var5, int var6);

    private native int flushStream(long var1, byte[] var3, int var4);

    private native int endStream(long var1, byte[] var3, int var4);

    public ZstdOutputStreamNoFinalizer(OutputStream outputStream, int n) throws IOException {
        this(outputStream, NoPool.INSTANCE);
        Zstd.setCompressionLevel(this.stream, n);
    }

    public ZstdOutputStreamNoFinalizer(OutputStream outputStream) throws IOException {
        this(outputStream, NoPool.INSTANCE);
    }

    public ZstdOutputStreamNoFinalizer(OutputStream outputStream, BufferPool bufferPool, int n) throws IOException {
        this(outputStream, bufferPool);
        Zstd.setCompressionLevel(this.stream, n);
    }

    public ZstdOutputStreamNoFinalizer(OutputStream outputStream, BufferPool bufferPool) throws IOException {
        super(outputStream);
        this.bufferPool = bufferPool;
        this.dstByteBuffer = Zstd.getArrayBackedBuffer(bufferPool, dstSize);
        this.dst = this.dstByteBuffer.array();
    }

    public synchronized ZstdOutputStreamNoFinalizer setChecksum(boolean bl) throws IOException {
        if (!this.frameClosed) {
            throw new IllegalStateException("Change of parameter on initialized stream");
        }
        int n = Zstd.setCompressionChecksums(this.stream, bl);
        if (Zstd.isError(n)) {
            throw new ZstdIOException(n);
        }
        return this;
    }

    public synchronized ZstdOutputStreamNoFinalizer setLevel(int n) throws IOException {
        if (!this.frameClosed) {
            throw new IllegalStateException("Change of parameter on initialized stream");
        }
        int n2 = Zstd.setCompressionLevel(this.stream, n);
        if (Zstd.isError(n2)) {
            throw new ZstdIOException(n2);
        }
        return this;
    }

    public synchronized ZstdOutputStreamNoFinalizer setLong(int n) throws IOException {
        if (!this.frameClosed) {
            throw new IllegalStateException("Change of parameter on initialized stream");
        }
        int n2 = Zstd.setCompressionLong(this.stream, n);
        if (Zstd.isError(n2)) {
            throw new ZstdIOException(n2);
        }
        return this;
    }

    public synchronized ZstdOutputStreamNoFinalizer setWorkers(int n) throws IOException {
        if (!this.frameClosed) {
            throw new IllegalStateException("Change of parameter on initialized stream");
        }
        int n2 = Zstd.setCompressionWorkers(this.stream, n);
        if (Zstd.isError(n2)) {
            throw new ZstdIOException(n2);
        }
        return this;
    }

    public synchronized ZstdOutputStreamNoFinalizer setOverlapLog(int n) throws IOException {
        if (!this.frameClosed) {
            throw new IllegalStateException("Change of parameter on initialized stream");
        }
        int n2 = Zstd.setCompressionOverlapLog(this.stream, n);
        if (Zstd.isError(n2)) {
            throw new ZstdIOException(n2);
        }
        return this;
    }

    public synchronized ZstdOutputStreamNoFinalizer setJobSize(int n) throws IOException {
        if (!this.frameClosed) {
            throw new IllegalStateException("Change of parameter on initialized stream");
        }
        int n2 = Zstd.setCompressionJobSize(this.stream, n);
        if (Zstd.isError(n2)) {
            throw new ZstdIOException(n2);
        }
        return this;
    }

    public synchronized ZstdOutputStreamNoFinalizer setTargetLength(int n) throws IOException {
        if (!this.frameClosed) {
            throw new IllegalStateException("Change of parameter on initialized stream");
        }
        int n2 = Zstd.setCompressionTargetLength(this.stream, n);
        if (Zstd.isError(n2)) {
            throw new ZstdIOException(n2);
        }
        return this;
    }

    public synchronized ZstdOutputStreamNoFinalizer setMinMatch(int n) throws IOException {
        if (!this.frameClosed) {
            throw new IllegalStateException("Change of parameter on initialized stream");
        }
        int n2 = Zstd.setCompressionMinMatch(this.stream, n);
        if (Zstd.isError(n2)) {
            throw new ZstdIOException(n2);
        }
        return this;
    }

    public synchronized ZstdOutputStreamNoFinalizer setSearchLog(int n) throws IOException {
        if (!this.frameClosed) {
            throw new IllegalStateException("Change of parameter on initialized stream");
        }
        int n2 = Zstd.setCompressionSearchLog(this.stream, n);
        if (Zstd.isError(n2)) {
            throw new ZstdIOException(n2);
        }
        return this;
    }

    public synchronized ZstdOutputStreamNoFinalizer setChainLog(int n) throws IOException {
        if (!this.frameClosed) {
            throw new IllegalStateException("Change of parameter on initialized stream");
        }
        int n2 = Zstd.setCompressionChainLog(this.stream, n);
        if (Zstd.isError(n2)) {
            throw new ZstdIOException(n2);
        }
        return this;
    }

    public synchronized ZstdOutputStreamNoFinalizer setHashLog(int n) throws IOException {
        if (!this.frameClosed) {
            throw new IllegalStateException("Change of parameter on initialized stream");
        }
        int n2 = Zstd.setCompressionHashLog(this.stream, n);
        if (Zstd.isError(n2)) {
            throw new ZstdIOException(n2);
        }
        return this;
    }

    public synchronized ZstdOutputStreamNoFinalizer setWindowLog(int n) throws IOException {
        if (!this.frameClosed) {
            throw new IllegalStateException("Change of parameter on initialized stream");
        }
        int n2 = Zstd.setCompressionWindowLog(this.stream, n);
        if (Zstd.isError(n2)) {
            throw new ZstdIOException(n2);
        }
        return this;
    }

    public synchronized ZstdOutputStreamNoFinalizer setStrategy(int n) throws IOException {
        if (!this.frameClosed) {
            throw new IllegalStateException("Change of parameter on initialized stream");
        }
        int n2 = Zstd.setCompressionStrategy(this.stream, n);
        if (Zstd.isError(n2)) {
            throw new ZstdIOException(n2);
        }
        return this;
    }

    public synchronized ZstdOutputStreamNoFinalizer setCloseFrameOnFlush(boolean bl) {
        if (!this.frameClosed) {
            throw new IllegalStateException("Change of parameter on initialized stream");
        }
        this.closeFrameOnFlush = bl;
        return this;
    }

    public synchronized ZstdOutputStreamNoFinalizer setDict(byte[] byArray) throws IOException {
        if (!this.frameClosed) {
            throw new IllegalStateException("Change of parameter on initialized stream");
        }
        int n = Zstd.loadDictCompress(this.stream, byArray, byArray.length);
        if (Zstd.isError(n)) {
            throw new ZstdIOException(n);
        }
        return this;
    }

    public synchronized ZstdOutputStreamNoFinalizer setDict(ZstdDictCompress zstdDictCompress) throws IOException {
        if (!this.frameClosed) {
            throw new IllegalStateException("Change of parameter on initialized stream");
        }
        int n = Zstd.loadFastDictCompress(this.stream, zstdDictCompress);
        if (Zstd.isError(n)) {
            throw new ZstdIOException(n);
        }
        return this;
    }

    @Override
    public synchronized void write(byte[] byArray, int n, int n2) throws IOException {
        int n3;
        if (this.isClosed) {
            throw new IOException("StreamClosed");
        }
        if (this.frameClosed) {
            n3 = this.resetCStream(this.stream);
            if (Zstd.isError(n3)) {
                throw new ZstdIOException(n3);
            }
            this.frameClosed = false;
            this.frameStarted = true;
        }
        n3 = n + n2;
        this.srcPos = n;
        while (this.srcPos < (long)n3) {
            int n4 = this.compressStream(this.stream, this.dst, dstSize, byArray, n3);
            if (Zstd.isError(n4)) {
                throw new ZstdIOException(n4);
            }
            if (this.dstPos <= 0L) continue;
            this.out.write(this.dst, 0, (int)this.dstPos);
        }
    }

    @Override
    public void write(int n) throws IOException {
        byte[] byArray = new byte[]{(byte)n};
        this.write(byArray, 0, 1);
    }

    @Override
    public synchronized void flush() throws IOException {
        if (this.isClosed) {
            throw new IOException("StreamClosed");
        }
        if (!this.frameClosed) {
            if (this.closeFrameOnFlush) {
                int n;
                do {
                    if (Zstd.isError(n = this.endStream(this.stream, this.dst, dstSize))) {
                        throw new ZstdIOException(n);
                    }
                    this.out.write(this.dst, 0, (int)this.dstPos);
                } while (n > 0);
                this.frameClosed = true;
            } else {
                int n;
                do {
                    if (Zstd.isError(n = this.flushStream(this.stream, this.dst, dstSize))) {
                        throw new ZstdIOException(n);
                    }
                    this.out.write(this.dst, 0, (int)this.dstPos);
                } while (n > 0);
            }
            this.out.flush();
        }
    }

    @Override
    public synchronized void close() throws IOException {
        this.close(true);
    }

    public synchronized void closeWithoutClosingParentStream() throws IOException {
        this.close(false);
    }

    private void close(boolean bl) throws IOException {
        if (this.isClosed) {
            return;
        }
        try {
            int n;
            if (!this.frameStarted) {
                n = this.resetCStream(this.stream);
                if (Zstd.isError(n)) {
                    throw new ZstdIOException(n);
                }
                this.frameClosed = false;
            }
            if (!this.frameClosed) {
                do {
                    if (Zstd.isError(n = this.endStream(this.stream, this.dst, dstSize))) {
                        throw new ZstdIOException(n);
                    }
                    this.out.write(this.dst, 0, (int)this.dstPos);
                } while (n > 0);
            }
            if (bl) {
                this.out.close();
            }
        }
        finally {
            this.isClosed = true;
            this.bufferPool.release(this.dstByteBuffer);
            ZstdOutputStreamNoFinalizer.freeCStream(this.stream);
        }
    }

    static {
        Native.load();
        dstSize = (int)ZstdOutputStreamNoFinalizer.recommendedCOutSize();
    }
}

