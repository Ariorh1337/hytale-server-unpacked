package org.rocksdb;

import java.nio.ByteBuffer;

public class WriteBatchWithIndex extends AbstractWriteBatch {
   public WriteBatchWithIndex() {
      super(newWriteBatchWithIndex());
   }

   public WriteBatchWithIndex(boolean var1) {
      super(newWriteBatchWithIndex(var1));
   }

   public WriteBatchWithIndex(AbstractComparator var1, int var2, boolean var3) {
      super(newWriteBatchWithIndex(var1.nativeHandle_, var1.getComparatorType().getValue(), var2, var3));
   }

   WriteBatchWithIndex(long var1) {
      super(var1);
      this.disOwnNativeHandle();
   }

   public WBWIRocksIterator newIterator(ColumnFamilyHandle var1) {
      return new WBWIRocksIterator(this, iterator1(this.nativeHandle_, var1.nativeHandle_));
   }

   public WBWIRocksIterator newIterator() {
      return new WBWIRocksIterator(this, iterator0(this.nativeHandle_));
   }

   public RocksIterator newIteratorWithBase(ColumnFamilyHandle var1, RocksIterator var2) {
      return this.newIteratorWithBase(var1, var2, null);
   }

   public RocksIterator newIteratorWithBase(ColumnFamilyHandle var1, RocksIterator var2, ReadOptions var3) {
      RocksIterator var4 = new RocksIterator(
         var2.parent_, iteratorWithBase(this.nativeHandle_, var1.nativeHandle_, var2.nativeHandle_, var3 == null ? 0L : var3.nativeHandle_)
      );
      var2.disOwnNativeHandle();
      return var4;
   }

   public RocksIterator newIteratorWithBase(RocksIterator var1) {
      return this.newIteratorWithBase(var1.parent_.getDefaultColumnFamily(), var1, null);
   }

   public RocksIterator newIteratorWithBase(RocksIterator var1, ReadOptions var2) {
      return this.newIteratorWithBase(var1.parent_.getDefaultColumnFamily(), var1, var2);
   }

   public byte[] getFromBatch(ColumnFamilyHandle var1, DBOptions var2, byte[] var3) throws RocksDBException {
      return getFromBatch(this.nativeHandle_, var2.nativeHandle_, var3, var3.length, var1.nativeHandle_);
   }

   public byte[] getFromBatch(DBOptions var1, byte[] var2) throws RocksDBException {
      return getFromBatch(this.nativeHandle_, var1.nativeHandle_, var2, var2.length);
   }

   public byte[] getFromBatchAndDB(RocksDB var1, ColumnFamilyHandle var2, ReadOptions var3, byte[] var4) throws RocksDBException {
      return getFromBatchAndDB(this.nativeHandle_, var1.nativeHandle_, var3.nativeHandle_, var4, var4.length, var2.nativeHandle_);
   }

   public byte[] getFromBatchAndDB(RocksDB var1, ReadOptions var2, byte[] var3) throws RocksDBException {
      return getFromBatchAndDB(this.nativeHandle_, var1.nativeHandle_, var2.nativeHandle_, var3, var3.length);
   }

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   @Override
   final int count0(long var1) {
      return count0Jni(var1);
   }

   private static native int count0Jni(long var0);

   @Override
   final void put(long var1, byte[] var3, int var4, byte[] var5, int var6) {
      putJni(var1, var3, var4, var5, var6);
   }

   private static native void putJni(long var0, byte[] var2, int var3, byte[] var4, int var5);

   @Override
   final void put(long var1, byte[] var3, int var4, byte[] var5, int var6, long var7) {
      putJni(var1, var3, var4, var5, var6, var7);
   }

   private static native void putJni(long var0, byte[] var2, int var3, byte[] var4, int var5, long var6);

   @Override
   final void putDirect(long var1, ByteBuffer var3, int var4, int var5, ByteBuffer var6, int var7, int var8, long var9) {
      putDirectJni(var1, var3, var4, var5, var6, var7, var8, var9);
   }

   private static native void putDirectJni(long var0, ByteBuffer var2, int var3, int var4, ByteBuffer var5, int var6, int var7, long var8);

   @Override
   final void merge(long var1, byte[] var3, int var4, byte[] var5, int var6) {
      mergeJni(var1, var3, var4, var5, var6);
   }

   private static native void mergeJni(long var0, byte[] var2, int var3, byte[] var4, int var5);

   @Override
   final void merge(long var1, byte[] var3, int var4, byte[] var5, int var6, long var7) {
      mergeJni(var1, var3, var4, var5, var6, var7);
   }

   private static native void mergeJni(long var0, byte[] var2, int var3, byte[] var4, int var5, long var6);

   @Override
   final void delete(long var1, byte[] var3, int var4) throws RocksDBException {
      deleteJni(var1, var3, var4);
   }

   private static native void deleteJni(long var0, byte[] var2, int var3) throws RocksDBException;

   @Override
   final void delete(long var1, byte[] var3, int var4, long var5) throws RocksDBException {
      deleteJni(var1, var3, var4, var5);
   }

   private static native void deleteJni(long var0, byte[] var2, int var3, long var4) throws RocksDBException;

   @Override
   final void singleDelete(long var1, byte[] var3, int var4) throws RocksDBException {
      singleDeleteJni(var1, var3, var4);
   }

   private static native void singleDeleteJni(long var0, byte[] var2, int var3) throws RocksDBException;

   @Override
   final void singleDelete(long var1, byte[] var3, int var4, long var5) throws RocksDBException {
      singleDeleteJni(var1, var3, var4, var5);
   }

   private static native void singleDeleteJni(long var0, byte[] var2, int var3, long var4) throws RocksDBException;

   @Override
   final void deleteDirect(long var1, ByteBuffer var3, int var4, int var5, long var6) throws RocksDBException {
      deleteDirectJni(var1, var3, var4, var5, var6);
   }

   private static native void deleteDirectJni(long var0, ByteBuffer var2, int var3, int var4, long var5) throws RocksDBException;

   @Override
   final void deleteRange(long var1, byte[] var3, int var4, byte[] var5, int var6) {
      deleteRangeJni(var1, var3, var4, var5, var6);
   }

   private static native void deleteRangeJni(long var0, byte[] var2, int var3, byte[] var4, int var5);

   @Override
   final void deleteRange(long var1, byte[] var3, int var4, byte[] var5, int var6, long var7) {
      deleteRangeJni(var1, var3, var4, var5, var6, var7);
   }

   private static native void deleteRangeJni(long var0, byte[] var2, int var3, byte[] var4, int var5, long var6);

   @Override
   final void putLogData(long var1, byte[] var3, int var4) throws RocksDBException {
      putLogDataJni(var1, var3, var4);
   }

   private static native void putLogDataJni(long var0, byte[] var2, int var3) throws RocksDBException;

   @Override
   final void clear0(long var1) {
      clear0Jni(var1);
   }

   private static native void clear0Jni(long var0);

   @Override
   final void setSavePoint0(long var1) {
      setSavePoint0Jni(var1);
   }

   private static native void setSavePoint0Jni(long var0);

   @Override
   final void rollbackToSavePoint0(long var1) {
      rollbackToSavePoint0Jni(var1);
   }

   private static native void rollbackToSavePoint0Jni(long var0);

   @Override
   final void popSavePoint(long var1) throws RocksDBException {
      popSavePointJni(var1);
   }

   private static native void popSavePointJni(long var0) throws RocksDBException;

   @Override
   final void setMaxBytes(long var1, long var3) {
      setMaxBytesJni(var1, var3);
   }

   private static native void setMaxBytesJni(long var0, long var2);

   @Override
   final WriteBatch getWriteBatch(long var1) {
      return getWriteBatchJni(var1);
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
