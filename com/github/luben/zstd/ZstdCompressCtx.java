/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

import com.github.luben.zstd.AutoCloseBase;
import com.github.luben.zstd.EndDirective;
import com.github.luben.zstd.Objects;
import com.github.luben.zstd.SequenceProducer;
import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdDictCompress;
import com.github.luben.zstd.ZstdException;
import com.github.luben.zstd.ZstdFrameProgression;
import com.github.luben.zstd.util.Native;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ZstdCompressCtx
extends AutoCloseBase {
    private long nativePtr = ZstdCompressCtx.init();
    private ZstdDictCompress compression_dict = null;
    private SequenceProducer seqprod = null;
    private long seqprod_state = 0L;

    private static native long init();

    private static native void free(long var0);

    public ZstdCompressCtx() {
        if (0L == this.nativePtr) {
            throw new IllegalStateException("ZSTD_createCompressCtx failed");
        }
        this.storeFence();
    }

    @Override
    void doClose() {
        if (this.nativePtr != 0L) {
            ZstdCompressCtx.free(this.nativePtr);
            this.nativePtr = 0L;
            if (this.seqprod != null) {
                this.seqprod.freeState(this.seqprod_state);
                this.seqprod = null;
            }
        }
    }

    private void ensureOpen() {
        if (this.nativePtr == 0L) {
            throw new IllegalStateException("Compression context is closed");
        }
    }

    public ZstdCompressCtx setLevel(int n) {
        this.ensureOpen();
        this.acquireSharedLock();
        ZstdCompressCtx.setLevel0(this.nativePtr, n);
        this.releaseSharedLock();
        return this;
    }

    private static native void setLevel0(long var0, int var2);

    public ZstdCompressCtx setMagicless(boolean bl) {
        this.ensureOpen();
        this.acquireSharedLock();
        Zstd.setCompressionMagicless(this.nativePtr, bl);
        this.releaseSharedLock();
        return this;
    }

    public ZstdCompressCtx setChecksum(boolean bl) {
        this.ensureOpen();
        this.acquireSharedLock();
        ZstdCompressCtx.setChecksum0(this.nativePtr, bl);
        this.releaseSharedLock();
        return this;
    }

    private static native void setChecksum0(long var0, boolean var2);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ZstdCompressCtx setWorkers(int n) {
        this.ensureOpen();
        this.acquireSharedLock();
        try {
            long l = Zstd.setCompressionWorkers(this.nativePtr, n);
            if (Zstd.isError(l)) {
                throw new ZstdException(l);
            }
        }
        finally {
            this.releaseSharedLock();
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ZstdCompressCtx setOverlapLog(int n) {
        this.ensureOpen();
        this.acquireSharedLock();
        try {
            long l = Zstd.setCompressionOverlapLog(this.nativePtr, n);
            if (Zstd.isError(l)) {
                throw new ZstdException(l);
            }
        }
        finally {
            this.releaseSharedLock();
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ZstdCompressCtx setJobSize(int n) {
        this.ensureOpen();
        this.acquireSharedLock();
        try {
            long l = Zstd.setCompressionJobSize(this.nativePtr, n);
            if (Zstd.isError(l)) {
                throw new ZstdException(l);
            }
        }
        finally {
            this.releaseSharedLock();
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ZstdCompressCtx setTargetLength(int n) {
        this.ensureOpen();
        this.acquireSharedLock();
        try {
            long l = Zstd.setCompressionTargetLength(this.nativePtr, n);
            if (Zstd.isError(l)) {
                throw new ZstdException(l);
            }
        }
        finally {
            this.releaseSharedLock();
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ZstdCompressCtx setMinMatch(int n) {
        this.ensureOpen();
        this.acquireSharedLock();
        try {
            long l = Zstd.setCompressionMinMatch(this.nativePtr, n);
            if (Zstd.isError(l)) {
                throw new ZstdException(l);
            }
        }
        finally {
            this.releaseSharedLock();
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ZstdCompressCtx setSearchLog(int n) {
        this.ensureOpen();
        this.acquireSharedLock();
        try {
            long l = Zstd.setCompressionSearchLog(this.nativePtr, n);
            if (Zstd.isError(l)) {
                throw new ZstdException(l);
            }
        }
        finally {
            this.releaseSharedLock();
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ZstdCompressCtx setChainLog(int n) {
        this.ensureOpen();
        this.acquireSharedLock();
        try {
            long l = Zstd.setCompressionChainLog(this.nativePtr, n);
            if (Zstd.isError(l)) {
                throw new ZstdException(l);
            }
        }
        finally {
            this.releaseSharedLock();
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ZstdCompressCtx setHashLog(int n) {
        this.ensureOpen();
        this.acquireSharedLock();
        try {
            long l = Zstd.setCompressionHashLog(this.nativePtr, n);
            if (Zstd.isError(l)) {
                throw new ZstdException(l);
            }
        }
        finally {
            this.releaseSharedLock();
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ZstdCompressCtx setWindowLog(int n) {
        this.ensureOpen();
        this.acquireSharedLock();
        try {
            long l = Zstd.setCompressionWindowLog(this.nativePtr, n);
            if (Zstd.isError(l)) {
                throw new ZstdException(l);
            }
        }
        finally {
            this.releaseSharedLock();
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ZstdCompressCtx setStrategy(int n) {
        this.ensureOpen();
        this.acquireSharedLock();
        try {
            long l = Zstd.setCompressionStrategy(this.nativePtr, n);
            if (Zstd.isError(l)) {
                throw new ZstdException(l);
            }
        }
        finally {
            this.releaseSharedLock();
        }
        return this;
    }

    public ZstdCompressCtx setContentSize(boolean bl) {
        this.ensureOpen();
        this.acquireSharedLock();
        ZstdCompressCtx.setContentSize0(this.nativePtr, bl);
        this.releaseSharedLock();
        return this;
    }

    private static native void setContentSize0(long var0, boolean var2);

    public ZstdCompressCtx setDictID(boolean bl) {
        this.ensureOpen();
        this.acquireSharedLock();
        ZstdCompressCtx.setDictID0(this.nativePtr, bl);
        this.releaseSharedLock();
        return this;
    }

    private static native void setDictID0(long var0, boolean var2);

    public ZstdCompressCtx setLong(int n) {
        this.ensureOpen();
        this.acquireSharedLock();
        Zstd.setCompressionLong(this.nativePtr, n);
        this.releaseSharedLock();
        return this;
    }

    public ZstdCompressCtx registerSequenceProducer(SequenceProducer sequenceProducer) {
        this.ensureOpen();
        this.acquireSharedLock();
        try {
            if (this.seqprod != null) {
                this.seqprod.freeState(this.seqprod_state);
                this.seqprod = null;
            }
            if (sequenceProducer == null) {
                Zstd.registerSequenceProducer(this.nativePtr, 0L, 0L);
            } else {
                this.seqprod_state = sequenceProducer.createState();
                Zstd.registerSequenceProducer(this.nativePtr, this.seqprod_state, sequenceProducer.getFunctionPointer());
                this.seqprod = sequenceProducer;
            }
        }
        catch (Exception exception) {
            this.seqprod = null;
            Zstd.registerSequenceProducer(this.nativePtr, 0L, 0L);
            throw exception;
        }
        finally {
            this.releaseSharedLock();
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ZstdCompressCtx setSequenceProducerFallback(boolean bl) {
        this.ensureOpen();
        this.acquireSharedLock();
        try {
            long l = Zstd.setSequenceProducerFallback(this.nativePtr, bl);
            if (Zstd.isError(l)) {
                throw new ZstdException(l);
            }
        }
        finally {
            this.releaseSharedLock();
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ZstdCompressCtx setSearchForExternalRepcodes(Zstd.ParamSwitch paramSwitch) {
        this.ensureOpen();
        this.acquireSharedLock();
        try {
            long l = Zstd.setSearchForExternalRepcodes(this.nativePtr, paramSwitch.getValue());
            if (Zstd.isError(l)) {
                throw new ZstdException(l);
            }
        }
        finally {
            this.releaseSharedLock();
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ZstdCompressCtx setValidateSequences(Zstd.ParamSwitch paramSwitch) {
        this.ensureOpen();
        this.acquireSharedLock();
        try {
            long l = Zstd.setValidateSequences(this.nativePtr, paramSwitch.getValue());
            if (Zstd.isError(l)) {
                throw new ZstdException(l);
            }
        }
        finally {
            this.releaseSharedLock();
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ZstdCompressCtx setEnableLongDistanceMatching(Zstd.ParamSwitch paramSwitch) {
        this.ensureOpen();
        this.acquireSharedLock();
        try {
            long l = Zstd.setEnableLongDistanceMatching(this.nativePtr, paramSwitch.getValue());
            if (Zstd.isError(l)) {
                throw new ZstdException(l);
            }
        }
        finally {
            this.releaseSharedLock();
        }
        return this;
    }

    long getNativePtr() {
        return this.nativePtr;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ZstdCompressCtx loadDict(ZstdDictCompress zstdDictCompress) {
        this.ensureOpen();
        this.acquireSharedLock();
        zstdDictCompress.acquireSharedLock();
        try {
            long l = this.loadCDictFast0(this.nativePtr, zstdDictCompress);
            if (Zstd.isError(l)) {
                throw new ZstdException(l);
            }
            this.compression_dict = zstdDictCompress;
        }
        finally {
            zstdDictCompress.releaseSharedLock();
            this.releaseSharedLock();
        }
        return this;
    }

    private native long loadCDictFast0(long var1, ZstdDictCompress var3);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ZstdCompressCtx loadDict(byte[] byArray) {
        this.ensureOpen();
        this.acquireSharedLock();
        try {
            long l = this.loadCDict0(this.nativePtr, byArray);
            if (Zstd.isError(l)) {
                throw new ZstdException(l);
            }
            this.compression_dict = null;
        }
        finally {
            this.releaseSharedLock();
        }
        return this;
    }

    private native long loadCDict0(long var1, byte[] var3);

    public ZstdFrameProgression getFrameProgression() {
        this.ensureOpen();
        this.acquireSharedLock();
        try {
            ZstdFrameProgression zstdFrameProgression = ZstdCompressCtx.getFrameProgression0(this.nativePtr);
            return zstdFrameProgression;
        }
        finally {
            this.releaseSharedLock();
        }
    }

    private static native ZstdFrameProgression getFrameProgression0(long var0);

    public void reset() {
        this.ensureOpen();
        this.acquireSharedLock();
        try {
            long l = ZstdCompressCtx.reset0(this.nativePtr);
            if (Zstd.isError(l)) {
                throw new ZstdException(l);
            }
        }
        finally {
            this.releaseSharedLock();
        }
    }

    private static native long reset0(long var0);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setPledgedSrcSize(long l) {
        this.ensureOpen();
        this.acquireSharedLock();
        try {
            long l2 = ZstdCompressCtx.setPledgedSrcSize0(this.nativePtr, l);
            if (Zstd.isError(l2)) {
                throw new ZstdException(l2);
            }
        }
        finally {
            this.releaseSharedLock();
        }
    }

    private static native long setPledgedSrcSize0(long var0, long var2);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean compressDirectByteBufferStream(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, EndDirective endDirective) {
        this.ensureOpen();
        this.acquireSharedLock();
        try {
            long l = ZstdCompressCtx.compressDirectByteBufferStream0(this.nativePtr, byteBuffer, byteBuffer.position(), byteBuffer.limit(), byteBuffer2, byteBuffer2.position(), byteBuffer2.limit(), endDirective.value());
            if ((l & 0x80000000L) != 0L) {
                long l2 = -(l & 0xFFL);
                throw new ZstdException(l2, Zstd.getErrorName(l2));
            }
            byteBuffer2.position((int)(l & Integer.MAX_VALUE));
            byteBuffer.position((int)(l >>> 32) & Integer.MAX_VALUE);
            boolean bl = l >>> 63 == 1L;
            return bl;
        }
        finally {
            this.releaseSharedLock();
        }
    }

    private static native long compressDirectByteBufferStream0(long var0, ByteBuffer var2, int var3, int var4, ByteBuffer var5, int var6, int var7, int var8);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int compressDirectByteBuffer(ByteBuffer byteBuffer, int n, int n2, ByteBuffer byteBuffer2, int n3, int n4) {
        this.ensureOpen();
        if (!byteBuffer2.isDirect()) {
            throw new IllegalArgumentException("srcBuff must be a direct buffer");
        }
        if (!byteBuffer.isDirect()) {
            throw new IllegalArgumentException("dstBuff must be a direct buffer");
        }
        Objects.checkFromIndexSize(n3, n4, byteBuffer2.limit());
        Objects.checkFromIndexSize(n, n2, byteBuffer.limit());
        this.acquireSharedLock();
        try {
            long l = ZstdCompressCtx.compressDirectByteBuffer0(this.nativePtr, byteBuffer, n, n2, byteBuffer2, n3, n4);
            if (Zstd.isError(l)) {
                throw new ZstdException(l);
            }
            if (l > Integer.MAX_VALUE) {
                throw new ZstdException(Zstd.errGeneric(), "Output size is greater than MAX_INT");
            }
            int n5 = (int)l;
            return n5;
        }
        finally {
            this.releaseSharedLock();
        }
    }

    private static native long compressDirectByteBuffer0(long var0, ByteBuffer var2, int var3, int var4, ByteBuffer var5, int var6, int var7);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int compressByteArray(byte[] byArray, int n, int n2, byte[] byArray2, int n3, int n4) {
        Objects.checkFromIndexSize(n3, n4, byArray2.length);
        Objects.checkFromIndexSize(n, n2, byArray.length);
        this.ensureOpen();
        this.acquireSharedLock();
        try {
            long l = ZstdCompressCtx.compressByteArray0(this.nativePtr, byArray, n, n2, byArray2, n3, n4);
            if (Zstd.isError(l)) {
                throw new ZstdException(l);
            }
            if (l > Integer.MAX_VALUE) {
                throw new ZstdException(Zstd.errGeneric(), "Output size is greater than MAX_INT");
            }
            int n5 = (int)l;
            return n5;
        }
        finally {
            this.releaseSharedLock();
        }
    }

    private static native long compressByteArray0(long var0, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7);

    public int compress(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        int n = this.compressDirectByteBuffer(byteBuffer, byteBuffer.position(), byteBuffer.limit() - byteBuffer.position(), byteBuffer2, byteBuffer2.position(), byteBuffer2.limit() - byteBuffer2.position());
        byteBuffer2.position(byteBuffer2.limit());
        byteBuffer.position(byteBuffer.position() + n);
        return n;
    }

    public ByteBuffer compress(ByteBuffer byteBuffer) throws ZstdException {
        long l = Zstd.compressBound(byteBuffer.limit() - byteBuffer.position());
        if (l > Integer.MAX_VALUE) {
            throw new ZstdException(Zstd.errGeneric(), "Max output size is greater than MAX_INT");
        }
        ByteBuffer byteBuffer2 = ByteBuffer.allocateDirect((int)l);
        int n = this.compressDirectByteBuffer(byteBuffer2, 0, (int)l, byteBuffer, byteBuffer.position(), byteBuffer.limit() - byteBuffer.position());
        byteBuffer.position(byteBuffer.limit());
        byteBuffer2.limit(n);
        return byteBuffer2;
    }

    public int compress(byte[] byArray, byte[] byArray2) {
        return this.compressByteArray(byArray, 0, byArray.length, byArray2, 0, byArray2.length);
    }

    public byte[] compress(byte[] byArray) {
        long l = Zstd.compressBound(byArray.length);
        if (l > Integer.MAX_VALUE) {
            throw new ZstdException(Zstd.errGeneric(), "Max output size is greater than MAX_INT");
        }
        byte[] byArray2 = new byte[(int)l];
        int n = this.compressByteArray(byArray2, 0, byArray2.length, byArray, 0, byArray.length);
        return Arrays.copyOfRange(byArray2, 0, n);
    }

    static {
        Native.load();
    }
}

