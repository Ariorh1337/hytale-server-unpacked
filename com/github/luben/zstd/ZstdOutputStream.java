/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

import com.github.luben.zstd.BufferPool;
import com.github.luben.zstd.NoPool;
import com.github.luben.zstd.ZstdDictCompress;
import com.github.luben.zstd.ZstdOutputStreamNoFinalizer;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ZstdOutputStream
extends FilterOutputStream {
    private ZstdOutputStreamNoFinalizer inner;

    @Deprecated
    public ZstdOutputStream(OutputStream outputStream, int n, boolean bl, boolean bl2) throws IOException {
        super(outputStream);
        this.inner = new ZstdOutputStreamNoFinalizer(outputStream, n);
        this.inner.setCloseFrameOnFlush(bl);
        this.inner.setChecksum(bl2);
    }

    @Deprecated
    public ZstdOutputStream(OutputStream outputStream, int n, boolean bl) throws IOException {
        super(outputStream);
        this.inner = new ZstdOutputStreamNoFinalizer(outputStream, n);
        this.inner.setCloseFrameOnFlush(bl);
    }

    public ZstdOutputStream(OutputStream outputStream, int n) throws IOException {
        this(outputStream, NoPool.INSTANCE);
        this.inner.setLevel(n);
    }

    public ZstdOutputStream(OutputStream outputStream) throws IOException {
        this(outputStream, NoPool.INSTANCE);
    }

    public ZstdOutputStream(OutputStream outputStream, BufferPool bufferPool, int n) throws IOException {
        this(outputStream, bufferPool);
        this.inner.setLevel(n);
    }

    public ZstdOutputStream(OutputStream outputStream, BufferPool bufferPool) throws IOException {
        super(outputStream);
        this.inner = new ZstdOutputStreamNoFinalizer(outputStream, bufferPool);
    }

    @Deprecated
    public void setFinalize(boolean bl) {
    }

    protected void finalize() throws Throwable {
        this.close();
    }

    public static long recommendedCOutSize() {
        return ZstdOutputStreamNoFinalizer.recommendedCOutSize();
    }

    public ZstdOutputStream setChecksum(boolean bl) throws IOException {
        this.inner.setChecksum(bl);
        return this;
    }

    public ZstdOutputStream setLevel(int n) throws IOException {
        this.inner.setLevel(n);
        return this;
    }

    public ZstdOutputStream setLong(int n) throws IOException {
        this.inner.setLong(n);
        return this;
    }

    public ZstdOutputStream setWorkers(int n) throws IOException {
        this.inner.setWorkers(n);
        return this;
    }

    public ZstdOutputStream setOverlapLog(int n) throws IOException {
        this.inner.setOverlapLog(n);
        return this;
    }

    public ZstdOutputStream setJobSize(int n) throws IOException {
        this.inner.setJobSize(n);
        return this;
    }

    public ZstdOutputStream setTargetLength(int n) throws IOException {
        this.inner.setTargetLength(n);
        return this;
    }

    public ZstdOutputStream setMinMatch(int n) throws IOException {
        this.inner.setMinMatch(n);
        return this;
    }

    public ZstdOutputStream setSearchLog(int n) throws IOException {
        this.inner.setSearchLog(n);
        return this;
    }

    public ZstdOutputStream setChainLog(int n) throws IOException {
        this.inner.setChainLog(n);
        return this;
    }

    public ZstdOutputStream setHashLog(int n) throws IOException {
        this.inner.setHashLog(n);
        return this;
    }

    public ZstdOutputStream setWindowLog(int n) throws IOException {
        this.inner.setWindowLog(n);
        return this;
    }

    public ZstdOutputStream setStrategy(int n) throws IOException {
        this.inner.setStrategy(n);
        return this;
    }

    public ZstdOutputStream setCloseFrameOnFlush(boolean bl) {
        this.inner.setCloseFrameOnFlush(bl);
        return this;
    }

    public ZstdOutputStream setDict(byte[] byArray) throws IOException {
        this.inner.setDict(byArray);
        return this;
    }

    public ZstdOutputStream setDict(ZstdDictCompress zstdDictCompress) throws IOException {
        this.inner.setDict(zstdDictCompress);
        return this;
    }

    @Override
    public void write(byte[] byArray, int n, int n2) throws IOException {
        this.inner.write(byArray, n, n2);
    }

    @Override
    public void write(int n) throws IOException {
        this.inner.write(n);
    }

    @Override
    public void flush() throws IOException {
        this.inner.flush();
    }

    @Override
    public void close() throws IOException {
        this.inner.close();
    }
}

