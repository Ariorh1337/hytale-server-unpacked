/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.nio.ByteBuffer;
import org.rocksdb.DirectSlice;
import org.rocksdb.EnvOptions;
import org.rocksdb.Options;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksObject;
import org.rocksdb.Slice;

public class SstFileWriter
extends RocksObject {
    public SstFileWriter(EnvOptions envOptions, Options options) {
        super(SstFileWriter.newSstFileWriter(envOptions.nativeHandle_, options.nativeHandle_));
    }

    public void open(String string) throws RocksDBException {
        SstFileWriter.open(this.nativeHandle_, string);
    }

    public void put(Slice slice, Slice slice2) throws RocksDBException {
        SstFileWriter.put(this.nativeHandle_, slice.getNativeHandle(), slice2.getNativeHandle());
    }

    public void put(DirectSlice directSlice, DirectSlice directSlice2) throws RocksDBException {
        SstFileWriter.put(this.nativeHandle_, directSlice.getNativeHandle(), directSlice2.getNativeHandle());
    }

    public void put(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws RocksDBException {
        assert (byteBuffer.isDirect() && byteBuffer2.isDirect());
        SstFileWriter.putDirect(this.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining(), byteBuffer2, byteBuffer2.position(), byteBuffer2.remaining());
        byteBuffer.position(byteBuffer.limit());
        byteBuffer2.position(byteBuffer2.limit());
    }

    public void put(byte[] byArray, byte[] byArray2) throws RocksDBException {
        SstFileWriter.put(this.nativeHandle_, byArray, byArray2);
    }

    public void merge(Slice slice, Slice slice2) throws RocksDBException {
        SstFileWriter.merge(this.nativeHandle_, slice.getNativeHandle(), slice2.getNativeHandle());
    }

    public void merge(byte[] byArray, byte[] byArray2) throws RocksDBException {
        SstFileWriter.merge(this.nativeHandle_, byArray, byArray2);
    }

    public void merge(DirectSlice directSlice, DirectSlice directSlice2) throws RocksDBException {
        SstFileWriter.merge(this.nativeHandle_, directSlice.getNativeHandle(), directSlice2.getNativeHandle());
    }

    public void delete(Slice slice) throws RocksDBException {
        SstFileWriter.delete(this.nativeHandle_, slice.getNativeHandle());
    }

    public void delete(DirectSlice directSlice) throws RocksDBException {
        SstFileWriter.delete(this.nativeHandle_, directSlice.getNativeHandle());
    }

    public void delete(byte[] byArray) throws RocksDBException {
        SstFileWriter.delete(this.nativeHandle_, byArray);
    }

    public void finish() throws RocksDBException {
        SstFileWriter.finish(this.nativeHandle_);
    }

    public long fileSize() throws RocksDBException {
        return SstFileWriter.fileSize(this.nativeHandle_);
    }

    private static native long newSstFileWriter(long var0, long var2, long var4, byte var6);

    private static native long newSstFileWriter(long var0, long var2);

    private static native void open(long var0, String var2) throws RocksDBException;

    private static native void put(long var0, long var2, long var4) throws RocksDBException;

    private static native void put(long var0, byte[] var2, byte[] var3) throws RocksDBException;

    private static native void putDirect(long var0, ByteBuffer var2, int var3, int var4, ByteBuffer var5, int var6, int var7) throws RocksDBException;

    private static native long fileSize(long var0) throws RocksDBException;

    private static native void merge(long var0, long var2, long var4) throws RocksDBException;

    private static native void merge(long var0, byte[] var2, byte[] var3) throws RocksDBException;

    private static native void delete(long var0, long var2) throws RocksDBException;

    private static native void delete(long var0, byte[] var2) throws RocksDBException;

    private static native void finish(long var0) throws RocksDBException;

    @Override
    protected final void disposeInternal(long l) {
        SstFileWriter.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);
}

