/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.nio.ByteBuffer;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDBException;
import org.rocksdb.WriteBatch;

public interface WriteBatchInterface {
    public int count();

    public void put(byte[] var1, byte[] var2) throws RocksDBException;

    public void put(ColumnFamilyHandle var1, byte[] var2, byte[] var3) throws RocksDBException;

    public void put(ByteBuffer var1, ByteBuffer var2) throws RocksDBException;

    public void put(ColumnFamilyHandle var1, ByteBuffer var2, ByteBuffer var3) throws RocksDBException;

    public void merge(byte[] var1, byte[] var2) throws RocksDBException;

    public void merge(ColumnFamilyHandle var1, byte[] var2, byte[] var3) throws RocksDBException;

    public void delete(byte[] var1) throws RocksDBException;

    public void delete(ColumnFamilyHandle var1, byte[] var2) throws RocksDBException;

    public void delete(ByteBuffer var1) throws RocksDBException;

    public void delete(ColumnFamilyHandle var1, ByteBuffer var2) throws RocksDBException;

    public void singleDelete(byte[] var1) throws RocksDBException;

    public void singleDelete(ColumnFamilyHandle var1, byte[] var2) throws RocksDBException;

    public void deleteRange(byte[] var1, byte[] var2) throws RocksDBException;

    public void deleteRange(ColumnFamilyHandle var1, byte[] var2, byte[] var3) throws RocksDBException;

    public void putLogData(byte[] var1) throws RocksDBException;

    public void clear();

    public void setSavePoint();

    public void rollbackToSavePoint() throws RocksDBException;

    public void popSavePoint() throws RocksDBException;

    public void setMaxBytes(long var1);

    public WriteBatch getWriteBatch();
}

