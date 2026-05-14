package org.rocksdb;

import java.nio.ByteBuffer;

public abstract class AbstractWriteBatch extends RocksObject implements WriteBatchInterface {
   protected AbstractWriteBatch(long var1) {
      super(var1);
   }

   @Override
   public int count() {
      return this.count0(this.nativeHandle_);
   }

   @Override
   public void put(byte[] var1, byte[] var2) throws RocksDBException {
      this.put(this.nativeHandle_, var1, var1.length, var2, var2.length);
   }

   @Override
   public void put(ColumnFamilyHandle var1, byte[] var2, byte[] var3) throws RocksDBException {
      this.put(this.nativeHandle_, var2, var2.length, var3, var3.length, var1.nativeHandle_);
   }

   @Override
   public void merge(byte[] var1, byte[] var2) throws RocksDBException {
      this.merge(this.nativeHandle_, var1, var1.length, var2, var2.length);
   }

   @Override
   public void merge(ColumnFamilyHandle var1, byte[] var2, byte[] var3) throws RocksDBException {
      this.merge(this.nativeHandle_, var2, var2.length, var3, var3.length, var1.nativeHandle_);
   }

   @Override
   public void put(ByteBuffer var1, ByteBuffer var2) throws RocksDBException {
      assert var1.isDirect() && var2.isDirect();
      this.putDirect(this.nativeHandle_, var1, var1.position(), var1.remaining(), var2, var2.position(), var2.remaining(), 0L);
      var1.position(var1.limit());
      var2.position(var2.limit());
   }

   @Override
   public void put(ColumnFamilyHandle var1, ByteBuffer var2, ByteBuffer var3) throws RocksDBException {
      assert var2.isDirect() && var3.isDirect();
      this.putDirect(this.nativeHandle_, var2, var2.position(), var2.remaining(), var3, var3.position(), var3.remaining(), var1.nativeHandle_);
      var2.position(var2.limit());
      var3.position(var3.limit());
   }

   @Override
   public void delete(byte[] var1) throws RocksDBException {
      this.delete(this.nativeHandle_, var1, var1.length);
   }

   @Override
   public void delete(ColumnFamilyHandle var1, byte[] var2) throws RocksDBException {
      this.delete(this.nativeHandle_, var2, var2.length, var1.nativeHandle_);
   }

   @Override
   public void delete(ByteBuffer var1) throws RocksDBException {
      this.deleteDirect(this.nativeHandle_, var1, var1.position(), var1.remaining(), 0L);
      var1.position(var1.limit());
   }

   @Override
   public void delete(ColumnFamilyHandle var1, ByteBuffer var2) throws RocksDBException {
      this.deleteDirect(this.nativeHandle_, var2, var2.position(), var2.remaining(), var1.nativeHandle_);
      var2.position(var2.limit());
   }

   @Override
   public void singleDelete(byte[] var1) throws RocksDBException {
      this.singleDelete(this.nativeHandle_, var1, var1.length);
   }

   @Override
   public void singleDelete(ColumnFamilyHandle var1, byte[] var2) throws RocksDBException {
      this.singleDelete(this.nativeHandle_, var2, var2.length, var1.nativeHandle_);
   }

   @Override
   public void deleteRange(byte[] var1, byte[] var2) throws RocksDBException {
      this.deleteRange(this.nativeHandle_, var1, var1.length, var2, var2.length);
   }

   @Override
   public void deleteRange(ColumnFamilyHandle var1, byte[] var2, byte[] var3) throws RocksDBException {
      this.deleteRange(this.nativeHandle_, var2, var2.length, var3, var3.length, var1.nativeHandle_);
   }

   @Override
   public void putLogData(byte[] var1) throws RocksDBException {
      this.putLogData(this.nativeHandle_, var1, var1.length);
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
   public void setMaxBytes(long var1) {
      this.setMaxBytes(this.nativeHandle_, var1);
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
