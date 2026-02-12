package org.rocksdb;

import java.nio.ByteBuffer;

public interface WriteBatchInterface {
   int count();

   void put(byte[] var1, byte[] var2) throws RocksDBException;

   void put(ColumnFamilyHandle var1, byte[] var2, byte[] var3) throws RocksDBException;

   void put(ByteBuffer var1, ByteBuffer var2) throws RocksDBException;

   void put(ColumnFamilyHandle var1, ByteBuffer var2, ByteBuffer var3) throws RocksDBException;

   void merge(byte[] var1, byte[] var2) throws RocksDBException;

   void merge(ColumnFamilyHandle var1, byte[] var2, byte[] var3) throws RocksDBException;

   void delete(byte[] var1) throws RocksDBException;

   void delete(ColumnFamilyHandle var1, byte[] var2) throws RocksDBException;

   void delete(ByteBuffer var1) throws RocksDBException;

   void delete(ColumnFamilyHandle var1, ByteBuffer var2) throws RocksDBException;

   void singleDelete(byte[] var1) throws RocksDBException;

   void singleDelete(ColumnFamilyHandle var1, byte[] var2) throws RocksDBException;

   void deleteRange(byte[] var1, byte[] var2) throws RocksDBException;

   void deleteRange(ColumnFamilyHandle var1, byte[] var2, byte[] var3) throws RocksDBException;

   void putLogData(byte[] var1) throws RocksDBException;

   void clear();

   void setSavePoint();

   void rollbackToSavePoint() throws RocksDBException;

   void popSavePoint() throws RocksDBException;

   void setMaxBytes(long var1);

   WriteBatch getWriteBatch();
}
