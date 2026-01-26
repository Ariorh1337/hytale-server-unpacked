/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

import com.github.luben.zstd.BufferPool;
import com.github.luben.zstd.NoPool;
import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdDictDecompress;
import com.github.luben.zstd.ZstdIOException;
import com.github.luben.zstd.util.Native;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ZstdInputStreamNoFinalizer
extends FilterInputStream {
    private final long stream;
    private long dstPos = 0L;
    private long srcPos = 0L;
    private long srcSize = 0L;
    private boolean needRead = true;
    private final BufferPool bufferPool;
    private final ByteBuffer srcByteBuffer;
    private final byte[] src;
    private static final int srcBuffSize;
    private boolean isContinuous = false;
    private boolean frameFinished = true;
    private boolean isClosed = false;

    public static native long recommendedDInSize();

    public static native long recommendedDOutSize();

    private static native long createDStream();

    private static native int freeDStream(long var0);

    private native int initDStream(long var1);

    private native int decompressStream(long var1, byte[] var3, int var4, byte[] var5, int var6);

    public ZstdInputStreamNoFinalizer(InputStream inputStream) throws IOException {
        this(inputStream, NoPool.INSTANCE);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ZstdInputStreamNoFinalizer(InputStream inputStream, BufferPool bufferPool) throws IOException {
        super(inputStream);
        this.bufferPool = bufferPool;
        this.srcByteBuffer = Zstd.getArrayBackedBuffer(bufferPool, srcBuffSize);
        this.src = this.srcByteBuffer.array();
        ZstdInputStreamNoFinalizer zstdInputStreamNoFinalizer = this;
        synchronized (zstdInputStreamNoFinalizer) {
            this.stream = ZstdInputStreamNoFinalizer.createDStream();
            this.initDStream(this.stream);
        }
    }

    public synchronized ZstdInputStreamNoFinalizer setContinuous(boolean bl) {
        this.isContinuous = bl;
        return this;
    }

    public synchronized boolean getContinuous() {
        return this.isContinuous;
    }

    public synchronized ZstdInputStreamNoFinalizer setDict(byte[] byArray) throws IOException {
        int n = Zstd.loadDictDecompress(this.stream, byArray, byArray.length);
        if (Zstd.isError(n)) {
            throw new ZstdIOException(n);
        }
        return this;
    }

    public synchronized ZstdInputStreamNoFinalizer setDict(ZstdDictDecompress zstdDictDecompress) throws IOException {
        zstdDictDecompress.acquireSharedLock();
        try {
            int n = Zstd.loadFastDictDecompress(this.stream, zstdDictDecompress);
            if (Zstd.isError(n)) {
                throw new ZstdIOException(n);
            }
        }
        finally {
            zstdDictDecompress.releaseSharedLock();
        }
        return this;
    }

    public synchronized ZstdInputStreamNoFinalizer setLongMax(int n) throws IOException {
        int n2 = Zstd.setDecompressionLongMax(this.stream, n);
        if (Zstd.isError(n2)) {
            throw new ZstdIOException(n2);
        }
        return this;
    }

    public synchronized ZstdInputStreamNoFinalizer setRefMultipleDDicts(boolean bl) throws IOException {
        int n = Zstd.setRefMultipleDDicts(this.stream, bl);
        if (Zstd.isError(n)) {
            throw new ZstdIOException(n);
        }
        return this;
    }

    @Override
    public synchronized int read(byte[] byArray, int n, int n2) throws IOException {
        if (n < 0 || n2 > byArray.length - n) {
            throw new IndexOutOfBoundsException("Requested length " + n2 + " from offset " + n + " in buffer of size " + byArray.length);
        }
        if (n2 == 0) {
            return 0;
        }
        int n3 = 0;
        while (n3 == 0) {
            n3 = this.readInternal(byArray, n, n2);
        }
        return n3;
    }

    int readInternal(byte[] byArray, int n, int n2) throws IOException {
        if (this.isClosed) {
            throw new IOException("Stream closed");
        }
        if (n < 0 || n2 > byArray.length - n) {
            throw new IndexOutOfBoundsException("Requested length " + n2 + " from offset " + n + " in buffer of size " + byArray.length);
        }
        int n3 = n + n2;
        this.dstPos = n;
        long l = -1L;
        while (this.dstPos < (long)n3 && l < this.dstPos) {
            if (this.needRead && (this.in.available() > 0 || this.dstPos == (long)n)) {
                this.srcSize = this.in.read(this.src, 0, srcBuffSize);
                this.srcPos = 0L;
                if (this.srcSize < 0L) {
                    this.srcSize = 0L;
                    if (this.frameFinished) {
                        return -1;
                    }
                    if (this.isContinuous) {
                        this.srcSize = (int)(this.dstPos - (long)n);
                        if (this.srcSize > 0L) {
                            return (int)this.srcSize;
                        }
                        return -1;
                    }
                    throw new ZstdIOException(Zstd.errCorruptionDetected(), "Truncated source");
                }
                this.frameFinished = false;
            }
            l = this.dstPos;
            int n4 = this.decompressStream(this.stream, byArray, n3, this.src, (int)this.srcSize);
            if (Zstd.isError(n4)) {
                throw new ZstdIOException(n4);
            }
            if (n4 == 0) {
                this.frameFinished = true;
                this.needRead = this.srcPos == this.srcSize;
                return (int)(this.dstPos - (long)n);
            }
            this.needRead = this.dstPos < (long)n3;
        }
        return (int)(this.dstPos - (long)n);
    }

    @Override
    public synchronized int read() throws IOException {
        byte[] byArray = new byte[1];
        int n = 0;
        while (n == 0) {
            n = this.readInternal(byArray, 0, 1);
        }
        if (n == 1) {
            return byArray[0] & 0xFF;
        }
        return -1;
    }

    @Override
    public synchronized int available() throws IOException {
        if (this.isClosed) {
            throw new IOException("Stream closed");
        }
        if (!this.needRead) {
            return 1;
        }
        return this.in.available();
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized long skip(long l) throws IOException {
        long l2;
        if (this.isClosed) {
            throw new IOException("Stream closed");
        }
        if (l <= 0L) {
            return 0L;
        }
        int n = (int)ZstdInputStreamNoFinalizer.recommendedDOutSize();
        if ((long)n > l) {
            n = (int)l;
        }
        ByteBuffer byteBuffer = Zstd.getArrayBackedBuffer(this.bufferPool, n);
        try {
            int n2;
            byte[] byArray = byteBuffer.array();
            for (l2 = l; l2 > 0L; l2 -= (long)n2) {
                n2 = this.read(byArray, 0, (int)Math.min((long)n, l2));
                if (n2 >= 0) continue;
                break;
            }
        }
        finally {
            this.bufferPool.release(byteBuffer);
        }
        return l - l2;
    }

    @Override
    public synchronized void close() throws IOException {
        if (this.isClosed) {
            return;
        }
        this.isClosed = true;
        this.bufferPool.release(this.srcByteBuffer);
        ZstdInputStreamNoFinalizer.freeDStream(this.stream);
        this.in.close();
    }

    static {
        Native.load();
        srcBuffSize = (int)ZstdInputStreamNoFinalizer.recommendedDInSize();
    }
}

