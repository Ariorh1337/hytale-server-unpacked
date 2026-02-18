/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.nio.ByteBuffer;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksObject;
import org.rocksdb.WriteBatch;
import org.rocksdb.WriteBatchInterface;

public abstract class AbstractWriteBatch
extends RocksObject
implements WriteBatchInterface {
    protected AbstractWriteBatch(long l) {
        super(l);
    }

    @Override
    public int count() {
        return this.count0(this.nativeHandle_);
    }

    @Override
    public void put(byte[] byArray, byte[] byArray2) throws RocksDBException {
        this.put(this.nativeHandle_, byArray, byArray.length, byArray2, byArray2.length);
    }

    @Override
    public void put(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, byte[] byArray2) throws RocksDBException {
        this.put(this.nativeHandle_, byArray, byArray.length, byArray2, byArray2.length, columnFamilyHandle.nativeHandle_);
    }

    @Override
    public void merge(byte[] byArray, byte[] byArray2) throws RocksDBException {
        this.merge(this.nativeHandle_, byArray, byArray.length, byArray2, byArray2.length);
    }

    @Override
    public void merge(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, byte[] byArray2) throws RocksDBException {
        this.merge(this.nativeHandle_, byArray, byArray.length, byArray2, byArray2.length, columnFamilyHandle.nativeHandle_);
    }

    @Override
    public void put(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws RocksDBException {
        assert (byteBuffer.isDirect() && byteBuffer2.isDirect());
        this.putDirect(this.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining(), byteBuffer2, byteBuffer2.position(), byteBuffer2.remaining(), 0L);
        byteBuffer.position(byteBuffer.limit());
        byteBuffer2.position(byteBuffer2.limit());
    }

    @Override
    public void put(ColumnFamilyHandle columnFamilyHandle, ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws RocksDBException {
        assert (byteBuffer.isDirect() && byteBuffer2.isDirect());
        this.putDirect(this.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining(), byteBuffer2, byteBuffer2.position(), byteBuffer2.remaining(), columnFamilyHandle.nativeHandle_);
        byteBuffer.position(byteBuffer.limit());
        byteBuffer2.position(byteBuffer2.limit());
    }

    @Override
    public void delete(byte[] byArray) throws RocksDBException {
        this.delete(this.nativeHandle_, byArray, byArray.length);
    }

    @Override
    public void delete(ColumnFamilyHandle columnFamilyHandle, byte[] byArray) throws RocksDBException {
        this.delete(this.nativeHandle_, byArray, byArray.length, columnFamilyHandle.nativeHandle_);
    }

    @Override
    public void delete(ByteBuffer byteBuffer) throws RocksDBException {
        this.deleteDirect(this.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining(), 0L);
        byteBuffer.position(byteBuffer.limit());
    }

    @Override
    public void delete(ColumnFamilyHandle columnFamilyHandle, ByteBuffer byteBuffer) throws RocksDBException {
        this.deleteDirect(this.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining(), columnFamilyHandle.nativeHandle_);
        byteBuffer.position(byteBuffer.limit());
    }

    @Override
    public void singleDelete(byte[] byArray) throws RocksDBException {
        this.singleDelete(this.nativeHandle_, byArray, byArray.length);
    }

    @Override
    public void singleDelete(ColumnFamilyHandle columnFamilyHandle, byte[] byArray) throws RocksDBException {
        this.singleDelete(this.nativeHandle_, byArray, byArray.length, columnFamilyHandle.nativeHandle_);
    }

    @Override
    public void deleteRange(byte[] byArray, byte[] byArray2) throws RocksDBException {
        this.deleteRange(this.nativeHandle_, byArray, byArray.length, byArray2, byArray2.length);
    }

    @Override
    public void deleteRange(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, byte[] byArray2) throws RocksDBException {
        this.deleteRange(this.nativeHandle_, byArray, byArray.length, byArray2, byArray2.length, columnFamilyHandle.nativeHandle_);
    }

    @Override
    public void putLogData(byte[] byArray) throws RocksDBException {
        this.putLogData(this.nativeHandle_, byArray, byArray.length);
    }

    @Override
    public void clear() {
        this.clear0(this.nativeHandle_);
    }

    @Override
    public void setSavePoint() {
        this.setSavePoint0(this.nativeHandle_);
    }

    @Override
    public void rollbackToSavePoint() throws RocksDBException {
        this.rollbackToSavePoint0(this.nativeHandle_);
    }

    @Override
    public void popSavePoint() throws RocksDBException {
        this.popSavePoint(this.nativeHandle_);
    }

    @Override
    public void setMaxBytes(long l) {
        this.setMaxBytes(this.nativeHandle_, l);
    }

    @Override
    public WriteBatch getWriteBatch() {
        return this.getWriteBatch(this.nativeHandle_);
    }

    abstract int count0(long var1);

    abstract void put(long var1, byte[] var3, int var4, byte[] var5, int var6) throws RocksDBException;

    abstract void put(long var1, byte[] var3, int var4, byte[] var5, int var6, long var7) throws RocksDBException;

    abstract void putDirect(long var1, ByteBuffer var3, int var4, int var5, ByteBuffer var6, int var7, int var8, long var9) throws RocksDBException;

    abstract void merge(long var1, byte[] var3, int var4, byte[] var5, int var6) throws RocksDBException;

    abstract void merge(long var1, byte[] var3, int var4, byte[] var5, int var6, long var7) throws RocksDBException;

    abstract void delete(long var1, byte[] var3, int var4) throws RocksDBException;

    abstract void delete(long var1, byte[] var3, int var4, long var5) throws RocksDBException;

    abstract void singleDelete(long var1, byte[] var3, int var4) throws RocksDBException;

    abstract void singleDelete(long var1, byte[] var3, int var4, long var5) throws RocksDBException;

    abstract void deleteDirect(long var1, ByteBuffer var3, int var4, int var5, long var6) throws RocksDBException;

    abstract void deleteRange(long var1, byte[] var3, int var4, byte[] var5, int var6) throws RocksDBException;

    abstract void deleteRange(long var1, byte[] var3, int var4, byte[] var5, int var6, long var7) throws RocksDBException;

    abstract void putLogData(long var1, byte[] var3, int var4) throws RocksDBException;

    abstract void clear0(long var1);

    abstract void setSavePoint0(long var1);

    abstract void rollbackToSavePoint0(long var1);

    abstract void popSavePoint(long var1) throws RocksDBException;

    abstract void setMaxBytes(long var1, long var3);

    abstract WriteBatch getWriteBatch(long var1);
}

