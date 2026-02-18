/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.Arrays;
import java.util.List;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.DBOptions;
import org.rocksdb.OptimisticTransactionOptions;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.Transaction;
import org.rocksdb.TransactionalDB;
import org.rocksdb.WriteOptions;

public class OptimisticTransactionDB
extends RocksDB
implements TransactionalDB<OptimisticTransactionOptions> {
    private OptimisticTransactionDB(long l) {
        super(l);
    }

    public static OptimisticTransactionDB open(Options options, String string) throws RocksDBException {
        OptimisticTransactionDB optimisticTransactionDB = new OptimisticTransactionDB(OptimisticTransactionDB.open(options.nativeHandle_, string));
        optimisticTransactionDB.storeOptionsInstance(options);
        optimisticTransactionDB.storeDefaultColumnFamilyHandle(optimisticTransactionDB.makeDefaultColumnFamilyHandle());
        return optimisticTransactionDB;
    }

    public static OptimisticTransactionDB open(DBOptions dBOptions, String string, List<ColumnFamilyDescriptor> list, List<ColumnFamilyHandle> list2) throws RocksDBException {
        Object object;
        int n = -1;
        byte[][] byArrayArray = new byte[list.size()][];
        long[] lArray = new long[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            object = list.get(i);
            byArrayArray[i] = ((ColumnFamilyDescriptor)object).getName();
            lArray[i] = ((ColumnFamilyDescriptor)object).getOptions().nativeHandle_;
            if (!Arrays.equals(((ColumnFamilyDescriptor)object).getName(), RocksDB.DEFAULT_COLUMN_FAMILY)) continue;
            n = i;
        }
        if (n < 0) {
            throw new IllegalArgumentException("You must provide the default column family in your columnFamilyDescriptors");
        }
        long[] lArray2 = OptimisticTransactionDB.open(dBOptions.nativeHandle_, string, byArrayArray, lArray);
        object = new OptimisticTransactionDB(lArray2[0]);
        ((RocksDB)object).storeOptionsInstance(dBOptions);
        for (int i = 1; i < lArray2.length; ++i) {
            list2.add(new ColumnFamilyHandle((RocksDB)object, lArray2[i]));
        }
        ((OptimisticTransactionDB)object).ownedColumnFamilyHandles.addAll(list2);
        ((RocksDB)object).storeDefaultColumnFamilyHandle(list2.get(n));
        return object;
    }

    @Override
    public void closeE() throws RocksDBException {
        if (this.owningHandle_.compareAndSet(true, false)) {
            try {
                OptimisticTransactionDB.closeDatabase(this.nativeHandle_);
            }
            finally {
                this.disposeInternal();
            }
        }
    }

    @Override
    public void close() {
        for (ColumnFamilyHandle columnFamilyHandle : this.ownedColumnFamilyHandles) {
            columnFamilyHandle.close();
        }
        this.ownedColumnFamilyHandles.clear();
        if (this.owningHandle_.compareAndSet(true, false)) {
            try {
                OptimisticTransactionDB.closeDatabase(this.nativeHandle_);
            }
            catch (RocksDBException rocksDBException) {
            }
            finally {
                this.disposeInternal();
            }
        }
    }

    @Override
    public Transaction beginTransaction(WriteOptions writeOptions) {
        return new Transaction(this, OptimisticTransactionDB.beginTransaction(this.nativeHandle_, writeOptions.nativeHandle_));
    }

    @Override
    public Transaction beginTransaction(WriteOptions writeOptions, OptimisticTransactionOptions optimisticTransactionOptions) {
        return new Transaction(this, OptimisticTransactionDB.beginTransaction(this.nativeHandle_, writeOptions.nativeHandle_, optimisticTransactionOptions.nativeHandle_));
    }

    @Override
    public Transaction beginTransaction(WriteOptions writeOptions, Transaction transaction) {
        long l = OptimisticTransactionDB.beginTransaction_withOld(this.nativeHandle_, writeOptions.nativeHandle_, transaction.nativeHandle_);
        assert (l == transaction.nativeHandle_);
        return transaction;
    }

    @Override
    public Transaction beginTransaction(WriteOptions writeOptions, OptimisticTransactionOptions optimisticTransactionOptions, Transaction transaction) {
        long l = OptimisticTransactionDB.beginTransaction_withOld(this.nativeHandle_, writeOptions.nativeHandle_, optimisticTransactionOptions.nativeHandle_, transaction.nativeHandle_);
        assert (l == transaction.nativeHandle_);
        return transaction;
    }

    public RocksDB getBaseDB() {
        RocksDB rocksDB = new RocksDB(OptimisticTransactionDB.getBaseDB(this.nativeHandle_));
        rocksDB.disOwnNativeHandle();
        return rocksDB;
    }

    @Override
    protected final void disposeInternal(long l) {
        OptimisticTransactionDB.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    protected static native long open(long var0, String var2) throws RocksDBException;

    protected static native long[] open(long var0, String var2, byte[][] var3, long[] var4);

    private static native void closeDatabase(long var0) throws RocksDBException;

    private static native long beginTransaction(long var0, long var2);

    private static native long beginTransaction(long var0, long var2, long var4);

    private static native long beginTransaction_withOld(long var0, long var2, long var4);

    private static native long beginTransaction_withOld(long var0, long var2, long var4, long var6);

    private static native long getBaseDB(long var0);
}

