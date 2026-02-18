/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.DBOptions;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.Transaction;
import org.rocksdb.TransactionDBOptions;
import org.rocksdb.TransactionOptions;
import org.rocksdb.TransactionalDB;
import org.rocksdb.WriteOptions;

public class TransactionDB
extends RocksDB
implements TransactionalDB<TransactionOptions> {
    private TransactionDBOptions transactionDbOptions_;

    private TransactionDB(long l) {
        super(l);
    }

    public static TransactionDB open(Options options, TransactionDBOptions transactionDBOptions, String string) throws RocksDBException {
        TransactionDB transactionDB = new TransactionDB(TransactionDB.open(options.nativeHandle_, transactionDBOptions.nativeHandle_, string));
        transactionDB.storeOptionsInstance(options);
        transactionDB.storeTransactionDbOptions(transactionDBOptions);
        transactionDB.storeDefaultColumnFamilyHandle(transactionDB.makeDefaultColumnFamilyHandle());
        return transactionDB;
    }

    public static TransactionDB open(DBOptions dBOptions, TransactionDBOptions transactionDBOptions, String string, List<ColumnFamilyDescriptor> list, List<ColumnFamilyHandle> list2) throws RocksDBException {
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
        long[] lArray2 = TransactionDB.open(dBOptions.nativeHandle_, transactionDBOptions.nativeHandle_, string, byArrayArray, lArray);
        object = new TransactionDB(lArray2[0]);
        ((RocksDB)object).storeOptionsInstance(dBOptions);
        super.storeTransactionDbOptions(transactionDBOptions);
        for (int i = 1; i < lArray2.length; ++i) {
            list2.add(new ColumnFamilyHandle((RocksDB)object, lArray2[i]));
        }
        ((TransactionDB)object).ownedColumnFamilyHandles.addAll(list2);
        ((RocksDB)object).storeDefaultColumnFamilyHandle(list2.get(n));
        return object;
    }

    @Override
    public void closeE() throws RocksDBException {
        if (this.owningHandle_.compareAndSet(true, false)) {
            try {
                TransactionDB.closeDatabase(this.nativeHandle_);
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
                TransactionDB.closeDatabase(this.nativeHandle_);
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
        return new Transaction(this, TransactionDB.beginTransaction(this.nativeHandle_, writeOptions.nativeHandle_));
    }

    @Override
    public Transaction beginTransaction(WriteOptions writeOptions, TransactionOptions transactionOptions) {
        return new Transaction(this, TransactionDB.beginTransaction(this.nativeHandle_, writeOptions.nativeHandle_, transactionOptions.nativeHandle_));
    }

    @Override
    public Transaction beginTransaction(WriteOptions writeOptions, Transaction transaction) {
        long l = TransactionDB.beginTransaction_withOld(this.nativeHandle_, writeOptions.nativeHandle_, transaction.nativeHandle_);
        assert (l == transaction.nativeHandle_);
        return transaction;
    }

    @Override
    public Transaction beginTransaction(WriteOptions writeOptions, TransactionOptions transactionOptions, Transaction transaction) {
        long l = TransactionDB.beginTransaction_withOld(this.nativeHandle_, writeOptions.nativeHandle_, transactionOptions.nativeHandle_, transaction.nativeHandle_);
        assert (l == transaction.nativeHandle_);
        return transaction;
    }

    public Transaction getTransactionByName(String string) {
        long l = TransactionDB.getTransactionByName(this.nativeHandle_, string);
        if (l == 0L) {
            return null;
        }
        Transaction transaction = new Transaction(this, l);
        transaction.disOwnNativeHandle();
        return transaction;
    }

    public List<Transaction> getAllPreparedTransactions() {
        long[] lArray = TransactionDB.getAllPreparedTransactions(this.nativeHandle_);
        ArrayList<Transaction> arrayList = new ArrayList<Transaction>();
        for (long l : lArray) {
            Transaction transaction = new Transaction(this, l);
            transaction.disOwnNativeHandle();
            arrayList.add(transaction);
        }
        return arrayList;
    }

    public Map<Long, KeyLockInfo> getLockStatusData() {
        return TransactionDB.getLockStatusData(this.nativeHandle_);
    }

    private DeadlockInfo newDeadlockInfo(long l, long l2, String string, boolean bl) {
        return new DeadlockInfo(l, l2, string, bl);
    }

    public DeadlockPath[] getDeadlockInfoBuffer() {
        return TransactionDB.getDeadlockInfoBuffer(this.nativeHandle_);
    }

    public void setDeadlockInfoBufferSize(int n) {
        TransactionDB.setDeadlockInfoBufferSize(this.nativeHandle_, n);
    }

    private void storeTransactionDbOptions(TransactionDBOptions transactionDBOptions) {
        this.transactionDbOptions_ = transactionDBOptions;
    }

    @Override
    protected final void disposeInternal(long l) {
        TransactionDB.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    private static native long open(long var0, long var2, String var4) throws RocksDBException;

    private static native long[] open(long var0, long var2, String var4, byte[][] var5, long[] var6);

    private static native void closeDatabase(long var0) throws RocksDBException;

    private static native long beginTransaction(long var0, long var2);

    private static native long beginTransaction(long var0, long var2, long var4);

    private static native long beginTransaction_withOld(long var0, long var2, long var4);

    private static native long beginTransaction_withOld(long var0, long var2, long var4, long var6);

    private static native long getTransactionByName(long var0, String var2);

    private static native long[] getAllPreparedTransactions(long var0);

    private static native Map<Long, KeyLockInfo> getLockStatusData(long var0);

    private static native DeadlockPath[] getDeadlockInfoBuffer(long var0);

    private static native void setDeadlockInfoBufferSize(long var0, int var2);

    public static class DeadlockPath {
        final DeadlockInfo[] path;
        final boolean limitExceeded;

        public DeadlockPath(DeadlockInfo[] deadlockInfoArray, boolean bl) {
            this.path = deadlockInfoArray;
            this.limitExceeded = bl;
        }

        public boolean isEmpty() {
            return this.path.length == 0 && !this.limitExceeded;
        }
    }

    public static class DeadlockInfo {
        private final long transactionID;
        private final long columnFamilyId;
        private final String waitingKey;
        private final boolean exclusive;

        private DeadlockInfo(long l, long l2, String string, boolean bl) {
            this.transactionID = l;
            this.columnFamilyId = l2;
            this.waitingKey = string;
            this.exclusive = bl;
        }

        public long getTransactionID() {
            return this.transactionID;
        }

        public long getColumnFamilyId() {
            return this.columnFamilyId;
        }

        public String getWaitingKey() {
            return this.waitingKey;
        }

        public boolean isExclusive() {
            return this.exclusive;
        }
    }

    public static class KeyLockInfo {
        private final String key;
        private final long[] transactionIDs;
        private final boolean exclusive;

        public KeyLockInfo(String string, long[] lArray, boolean bl) {
            this.key = string;
            this.transactionIDs = lArray;
            this.exclusive = bl;
        }

        public String getKey() {
            return this.key;
        }

        public long[] getTransactionIDs() {
            return this.transactionIDs;
        }

        public boolean isExclusive() {
            return this.exclusive;
        }
    }
}

