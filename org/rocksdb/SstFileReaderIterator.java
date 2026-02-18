/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.nio.ByteBuffer;
import org.rocksdb.AbstractRocksIterator;
import org.rocksdb.RocksDBException;
import org.rocksdb.SstFileReader;

public class SstFileReaderIterator
extends AbstractRocksIterator<SstFileReader> {
    protected SstFileReaderIterator(SstFileReader sstFileReader, long l) {
        super(sstFileReader, l);
    }

    public byte[] key() {
        assert (this.isOwningHandle());
        return SstFileReaderIterator.key0(this.nativeHandle_);
    }

    public int key(ByteBuffer byteBuffer) {
        assert (this.isOwningHandle());
        int n = byteBuffer.isDirect() ? SstFileReaderIterator.keyDirect0(this.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining()) : SstFileReaderIterator.keyByteArray0(this.nativeHandle_, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining());
        byteBuffer.limit(Math.min(byteBuffer.position() + n, byteBuffer.limit()));
        return n;
    }

    public byte[] value() {
        assert (this.isOwningHandle());
        return SstFileReaderIterator.value0(this.nativeHandle_);
    }

    public int value(ByteBuffer byteBuffer) {
        assert (this.isOwningHandle());
        int n = byteBuffer.isDirect() ? SstFileReaderIterator.valueDirect0(this.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining()) : SstFileReaderIterator.valueByteArray0(this.nativeHandle_, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining());
        byteBuffer.limit(Math.min(byteBuffer.position() + n, byteBuffer.limit()));
        return n;
    }

    @Override
    final native void refresh1(long var1, long var3);

    @Override
    protected final void disposeInternal(long l) {
        SstFileReaderIterator.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    @Override
    final boolean isValid0(long l) {
        return SstFileReaderIterator.isValid0Jni(l);
    }

    private static native boolean isValid0Jni(long var0);

    @Override
    final void seekToFirst0(long l) {
        SstFileReaderIterator.seekToFirst0Jni(l);
    }

    private static native void seekToFirst0Jni(long var0);

    @Override
    final void seekToLast0(long l) {
        SstFileReaderIterator.seekToLast0Jni(l);
    }

    private static native void seekToLast0Jni(long var0);

    @Override
    final void next0(long l) {
        SstFileReaderIterator.next0Jni(l);
    }

    private static native void next0Jni(long var0);

    @Override
    final void prev0(long l) {
        SstFileReaderIterator.prev0Jni(l);
    }

    private static native void prev0Jni(long var0);

    @Override
    final void refresh0(long l) throws RocksDBException {
        SstFileReaderIterator.refresh0Jni(l);
    }

    private static native void refresh0Jni(long var0) throws RocksDBException;

    @Override
    final void seek0(long l, byte[] byArray, int n) {
        SstFileReaderIterator.seek0Jni(l, byArray, n);
    }

    private static native void seek0Jni(long var0, byte[] var2, int var3);

    @Override
    final void seekForPrev0(long l, byte[] byArray, int n) {
        SstFileReaderIterator.seekForPrev0Jni(l, byArray, n);
    }

    private static native void seekForPrev0Jni(long var0, byte[] var2, int var3);

    @Override
    final void status0(long l) throws RocksDBException {
        SstFileReaderIterator.status0Jni(l);
    }

    private static native void status0Jni(long var0) throws RocksDBException;

    @Override
    final void seekDirect0(long l, ByteBuffer byteBuffer, int n, int n2) {
        SstFileReaderIterator.seekDirect0Jni(l, byteBuffer, n, n2);
    }

    private static native void seekDirect0Jni(long var0, ByteBuffer var2, int var3, int var4);

    @Override
    final void seekForPrevDirect0(long l, ByteBuffer byteBuffer, int n, int n2) {
        SstFileReaderIterator.seekForPrevDirect0Jni(l, byteBuffer, n, n2);
    }

    private static native void seekForPrevDirect0Jni(long var0, ByteBuffer var2, int var3, int var4);

    @Override
    final void seekByteArray0(long l, byte[] byArray, int n, int n2) {
        SstFileReaderIterator.seekByteArray0Jni(l, byArray, n, n2);
    }

    private static native void seekByteArray0Jni(long var0, byte[] var2, int var3, int var4);

    @Override
    final void seekForPrevByteArray0(long l, byte[] byArray, int n, int n2) {
        SstFileReaderIterator.seekForPrevByteArray0Jni(l, byArray, n, n2);
    }

    private static native void seekForPrevByteArray0Jni(long var0, byte[] var2, int var3, int var4);

    private static native byte[] key0(long var0);

    private static native byte[] value0(long var0);

    private static native int keyDirect0(long var0, ByteBuffer var2, int var3, int var4);

    private static native int keyByteArray0(long var0, byte[] var2, int var3, int var4);

    private static native int valueDirect0(long var0, ByteBuffer var2, int var3, int var4);

    private static native int valueByteArray0(long var0, byte[] var2, int var3, int var4);
}

