/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.nio.ByteBuffer;
import org.rocksdb.AbstractRocksIterator;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.util.BufferUtil;

public class RocksIterator
extends AbstractRocksIterator<RocksDB> {
    protected RocksIterator(RocksDB rocksDB, long l) {
        super(rocksDB, l);
    }

    public byte[] key() {
        assert (this.isOwningHandle());
        return RocksIterator.key0(this.nativeHandle_);
    }

    public int key(byte[] byArray) {
        assert (this.isOwningHandle());
        return RocksIterator.keyByteArray0(this.nativeHandle_, byArray, 0, byArray.length);
    }

    public int key(byte[] byArray, int n, int n2) {
        assert (this.isOwningHandle());
        BufferUtil.CheckBounds(n, n2, byArray.length);
        return RocksIterator.keyByteArray0(this.nativeHandle_, byArray, n, n2);
    }

    public int key(ByteBuffer byteBuffer) {
        int n;
        assert (this.isOwningHandle());
        if (byteBuffer.isDirect()) {
            n = RocksIterator.keyDirect0(this.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining());
        } else {
            assert (byteBuffer.hasArray());
            n = RocksIterator.keyByteArray0(this.nativeHandle_, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining());
        }
        byteBuffer.limit(Math.min(byteBuffer.position() + n, byteBuffer.limit()));
        return n;
    }

    public byte[] value() {
        assert (this.isOwningHandle());
        return RocksIterator.value0(this.nativeHandle_);
    }

    public int value(ByteBuffer byteBuffer) {
        int n;
        assert (this.isOwningHandle());
        if (byteBuffer.isDirect()) {
            n = RocksIterator.valueDirect0(this.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining());
        } else {
            assert (byteBuffer.hasArray());
            n = RocksIterator.valueByteArray0(this.nativeHandle_, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining());
        }
        byteBuffer.limit(Math.min(byteBuffer.position() + n, byteBuffer.limit()));
        return n;
    }

    public int value(byte[] byArray) {
        assert (this.isOwningHandle());
        return RocksIterator.valueByteArray0(this.nativeHandle_, byArray, 0, byArray.length);
    }

    public int value(byte[] byArray, int n, int n2) {
        assert (this.isOwningHandle());
        BufferUtil.CheckBounds(n, n2, byArray.length);
        return RocksIterator.valueByteArray0(this.nativeHandle_, byArray, n, n2);
    }

    @Override
    final native void refresh1(long var1, long var3);

    @Override
    protected final void disposeInternal(long l) {
        RocksIterator.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    @Override
    final boolean isValid0(long l) {
        return RocksIterator.isValid0Jni(l);
    }

    private static native boolean isValid0Jni(long var0);

    @Override
    final void seekToFirst0(long l) {
        RocksIterator.seekToFirst0Jni(l);
    }

    private static native void seekToFirst0Jni(long var0);

    @Override
    final void seekToLast0(long l) {
        RocksIterator.seekToLast0Jni(l);
    }

    private static native void seekToLast0Jni(long var0);

    @Override
    final void next0(long l) {
        RocksIterator.next0Jni(l);
    }

    private static native void next0Jni(long var0);

    @Override
    final void prev0(long l) {
        RocksIterator.prev0Jni(l);
    }

    private static native void prev0Jni(long var0);

    @Override
    final void refresh0(long l) {
        RocksIterator.refresh0Jni(l);
    }

    private static native void refresh0Jni(long var0);

    @Override
    final void seek0(long l, byte[] byArray, int n) {
        RocksIterator.seek0Jni(l, byArray, n);
    }

    private static native void seek0Jni(long var0, byte[] var2, int var3);

    @Override
    final void seekForPrev0(long l, byte[] byArray, int n) {
        RocksIterator.seekForPrev0Jni(l, byArray, n);
    }

    private static native void seekForPrev0Jni(long var0, byte[] var2, int var3);

    @Override
    final void seekDirect0(long l, ByteBuffer byteBuffer, int n, int n2) {
        RocksIterator.seekDirect0Jni(l, byteBuffer, n, n2);
    }

    private static native void seekDirect0Jni(long var0, ByteBuffer var2, int var3, int var4);

    @Override
    final void seekByteArray0(long l, byte[] byArray, int n, int n2) {
        RocksIterator.seekByteArray0Jni(l, byArray, n, n2);
    }

    private static native void seekByteArray0Jni(long var0, byte[] var2, int var3, int var4);

    @Override
    final void seekForPrevDirect0(long l, ByteBuffer byteBuffer, int n, int n2) {
        RocksIterator.seekForPrevDirect0Jni(l, byteBuffer, n, n2);
    }

    private static native void seekForPrevDirect0Jni(long var0, ByteBuffer var2, int var3, int var4);

    @Override
    final void seekForPrevByteArray0(long l, byte[] byArray, int n, int n2) {
        RocksIterator.seekForPrevByteArray0Jni(l, byArray, n, n2);
    }

    private static native void seekForPrevByteArray0Jni(long var0, byte[] var2, int var3, int var4);

    @Override
    final void status0(long l) throws RocksDBException {
        RocksIterator.status0Jni(l);
    }

    private static native void status0Jni(long var0) throws RocksDBException;

    private static native byte[] key0(long var0);

    private static native byte[] value0(long var0);

    private static native int keyDirect0(long var0, ByteBuffer var2, int var3, int var4);

    private static native int keyByteArray0(long var0, byte[] var2, int var3, int var4);

    private static native int valueDirect0(long var0, ByteBuffer var2, int var3, int var4);

    private static native int valueByteArray0(long var0, byte[] var2, int var3, int var4);
}

