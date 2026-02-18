/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.nio.ByteBuffer;
import org.rocksdb.AbstractComparator;
import org.rocksdb.AbstractWriteBatch;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.DBOptions;
import org.rocksdb.ReadOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.rocksdb.WBWIRocksIterator;
import org.rocksdb.WriteBatch;

public class WriteBatchWithIndex
extends AbstractWriteBatch {
    public WriteBatchWithIndex() {
        super(WriteBatchWithIndex.newWriteBatchWithIndex());
    }

    public WriteBatchWithIndex(boolean bl) {
        super(WriteBatchWithIndex.newWriteBatchWithIndex(bl));
    }

    public WriteBatchWithIndex(AbstractComparator abstractComparator, int n, boolean bl) {
        super(WriteBatchWithIndex.newWriteBatchWithIndex(abstractComparator.nativeHandle_, abstractComparator.getComparatorType().getValue(), n, bl));
    }

    WriteBatchWithIndex(long l) {
        super(l);
        this.disOwnNativeHandle();
    }

    public WBWIRocksIterator newIterator(ColumnFamilyHandle columnFamilyHandle) {
        return new WBWIRocksIterator(this, WriteBatchWithIndex.iterator1(this.nativeHandle_, columnFamilyHandle.nativeHandle_));
    }

    public WBWIRocksIterator newIterator() {
        return new WBWIRocksIterator(this, WriteBatchWithIndex.iterator0(this.nativeHandle_));
    }

    public RocksIterator newIteratorWithBase(ColumnFamilyHandle columnFamilyHandle, RocksIterator rocksIterator) {
        return this.newIteratorWithBase(columnFamilyHandle, rocksIterator, null);
    }

    public RocksIterator newIteratorWithBase(ColumnFamilyHandle columnFamilyHandle, RocksIterator rocksIterator, ReadOptions readOptions) {
        RocksIterator rocksIterator2 = new RocksIterator((RocksDB)rocksIterator.parent_, WriteBatchWithIndex.iteratorWithBase(this.nativeHandle_, columnFamilyHandle.nativeHandle_, rocksIterator.nativeHandle_, readOptions == null ? 0L : readOptions.nativeHandle_));
        rocksIterator.disOwnNativeHandle();
        return rocksIterator2;
    }

    public RocksIterator newIteratorWithBase(RocksIterator rocksIterator) {
        return this.newIteratorWithBase(((RocksDB)rocksIterator.parent_).getDefaultColumnFamily(), rocksIterator, null);
    }

    public RocksIterator newIteratorWithBase(RocksIterator rocksIterator, ReadOptions readOptions) {
        return this.newIteratorWithBase(((RocksDB)rocksIterator.parent_).getDefaultColumnFamily(), rocksIterator, readOptions);
    }

    public byte[] getFromBatch(ColumnFamilyHandle columnFamilyHandle, DBOptions dBOptions, byte[] byArray) throws RocksDBException {
        return WriteBatchWithIndex.getFromBatch(this.nativeHandle_, dBOptions.nativeHandle_, byArray, byArray.length, columnFamilyHandle.nativeHandle_);
    }

    public byte[] getFromBatch(DBOptions dBOptions, byte[] byArray) throws RocksDBException {
        return WriteBatchWithIndex.getFromBatch(this.nativeHandle_, dBOptions.nativeHandle_, byArray, byArray.length);
    }

    public byte[] getFromBatchAndDB(RocksDB rocksDB, ColumnFamilyHandle columnFamilyHandle, ReadOptions readOptions, byte[] byArray) throws RocksDBException {
        return WriteBatchWithIndex.getFromBatchAndDB(this.nativeHandle_, rocksDB.nativeHandle_, readOptions.nativeHandle_, byArray, byArray.length, columnFamilyHandle.nativeHandle_);
    }

    public byte[] getFromBatchAndDB(RocksDB rocksDB, ReadOptions readOptions, byte[] byArray) throws RocksDBException {
        return WriteBatchWithIndex.getFromBatchAndDB(this.nativeHandle_, rocksDB.nativeHandle_, readOptions.nativeHandle_, byArray, byArray.length);
    }

    @Override
    protected final void disposeInternal(long l) {
        WriteBatchWithIndex.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    @Override
    final int count0(long l) {
        return WriteBatchWithIndex.count0Jni(l);
    }

    private static native int count0Jni(long var0);

    @Override
    final void put(long l, byte[] byArray, int n, byte[] byArray2, int n2) {
        WriteBatchWithIndex.putJni(l, byArray, n, byArray2, n2);
    }

    private static native void putJni(long var0, byte[] var2, int var3, byte[] var4, int var5);

    @Override
    final void put(long l, byte[] byArray, int n, byte[] byArray2, int n2, long l2) {
        WriteBatchWithIndex.putJni(l, byArray, n, byArray2, n2, l2);
    }

    private static native void putJni(long var0, byte[] var2, int var3, byte[] var4, int var5, long var6);

    @Override
    final void putDirect(long l, ByteBuffer byteBuffer, int n, int n2, ByteBuffer byteBuffer2, int n3, int n4, long l2) {
        WriteBatchWithIndex.putDirectJni(l, byteBuffer, n, n2, byteBuffer2, n3, n4, l2);
    }

    private static native void putDirectJni(long var0, ByteBuffer var2, int var3, int var4, ByteBuffer var5, int var6, int var7, long var8);

    @Override
    final void merge(long l, byte[] byArray, int n, byte[] byArray2, int n2) {
        WriteBatchWithIndex.mergeJni(l, byArray, n, byArray2, n2);
    }

    private static native void mergeJni(long var0, byte[] var2, int var3, byte[] var4, int var5);

    @Override
    final void merge(long l, byte[] byArray, int n, byte[] byArray2, int n2, long l2) {
        WriteBatchWithIndex.mergeJni(l, byArray, n, byArray2, n2, l2);
    }

    private static native void mergeJni(long var0, byte[] var2, int var3, byte[] var4, int var5, long var6);

    @Override
    final void delete(long l, byte[] byArray, int n) throws RocksDBException {
        WriteBatchWithIndex.deleteJni(l, byArray, n);
    }

    private static native void deleteJni(long var0, byte[] var2, int var3) throws RocksDBException;

    @Override
    final void delete(long l, byte[] byArray, int n, long l2) throws RocksDBException {
        WriteBatchWithIndex.deleteJni(l, byArray, n, l2);
    }

    private static native void deleteJni(long var0, byte[] var2, int var3, long var4) throws RocksDBException;

    @Override
    final void singleDelete(long l, byte[] byArray, int n) throws RocksDBException {
        WriteBatchWithIndex.singleDeleteJni(l, byArray, n);
    }

    private static native void singleDeleteJni(long var0, byte[] var2, int var3) throws RocksDBException;

    @Override
    final void singleDelete(long l, byte[] byArray, int n, long l2) throws RocksDBException {
        WriteBatchWithIndex.singleDeleteJni(l, byArray, n, l2);
    }

    private static native void singleDeleteJni(long var0, byte[] var2, int var3, long var4) throws RocksDBException;

    @Override
    final void deleteDirect(long l, ByteBuffer byteBuffer, int n, int n2, long l2) throws RocksDBException {
        WriteBatchWithIndex.deleteDirectJni(l, byteBuffer, n, n2, l2);
    }

    private static native void deleteDirectJni(long var0, ByteBuffer var2, int var3, int var4, long var5) throws RocksDBException;

    @Override
    final void deleteRange(long l, byte[] byArray, int n, byte[] byArray2, int n2) {
        WriteBatchWithIndex.deleteRangeJni(l, byArray, n, byArray2, n2);
    }

    private static native void deleteRangeJni(long var0, byte[] var2, int var3, byte[] var4, int var5);

    @Override
    final void deleteRange(long l, byte[] byArray, int n, byte[] byArray2, int n2, long l2) {
        WriteBatchWithIndex.deleteRangeJni(l, byArray, n, byArray2, n2, l2);
    }

    private static native void deleteRangeJni(long var0, byte[] var2, int var3, byte[] var4, int var5, long var6);

    @Override
    final void putLogData(long l, byte[] byArray, int n) throws RocksDBException {
        WriteBatchWithIndex.putLogDataJni(l, byArray, n);
    }

    private static native void putLogDataJni(long var0, byte[] var2, int var3) throws RocksDBException;

    @Override
    final void clear0(long l) {
        WriteBatchWithIndex.clear0Jni(l);
    }

    private static native void clear0Jni(long var0);

    @Override
    final void setSavePoint0(long l) {
        WriteBatchWithIndex.setSavePoint0Jni(l);
    }

    private static native void setSavePoint0Jni(long var0);

    @Override
    final void rollbackToSavePoint0(long l) {
        WriteBatchWithIndex.rollbackToSavePoint0Jni(l);
    }

    private static native void rollbackToSavePoint0Jni(long var0);

    @Override
    final void popSavePoint(long l) throws RocksDBException {
        WriteBatchWithIndex.popSavePointJni(l);
    }

    private static native void popSavePointJni(long var0) throws RocksDBException;

    @Override
    final void setMaxBytes(long l, long l2) {
        WriteBatchWithIndex.setMaxBytesJni(l, l2);
    }

    private static native void setMaxBytesJni(long var0, long var2);

    @Override
    final WriteBatch getWriteBatch(long l) {
        return WriteBatchWithIndex.getWriteBatchJni(l);
    }

    private static native WriteBatch getWriteBatchJni(long var0);

    private static native long newWriteBatchWithIndex();

    private static native long newWriteBatchWithIndex(boolean var0);

    private static native long newWriteBatchWithIndex(long var0, byte var2, int var3, boolean var4);

    private static native long iterator0(long var0);

    private static native long iterator1(long var0, long var2);

    private static native long iteratorWithBase(long var0, long var2, long var4, long var6);

    private static native byte[] getFromBatch(long var0, long var2, byte[] var4, int var5);

    private static native byte[] getFromBatch(long var0, long var2, byte[] var4, int var5, long var6);

    private static native byte[] getFromBatchAndDB(long var0, long var2, long var4, byte[] var6, int var7);

    private static native byte[] getFromBatchAndDB(long var0, long var2, long var4, byte[] var6, int var7, long var8);
}

