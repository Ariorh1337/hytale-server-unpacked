/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.Arrays;
import java.util.List;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.DBOptions;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

public class TtlDB
extends RocksDB {
    public static TtlDB open(Options options, String string) throws RocksDBException {
        return TtlDB.open(options, string, 0, false);
    }

    public static TtlDB open(Options options, String string, int n, boolean bl) throws RocksDBException {
        TtlDB ttlDB = new TtlDB(TtlDB.open(options.nativeHandle_, string, n, bl));
        ttlDB.storeOptionsInstance(options);
        ttlDB.storeDefaultColumnFamilyHandle(ttlDB.makeDefaultColumnFamilyHandle());
        return ttlDB;
    }

    public static TtlDB open(DBOptions dBOptions, String string, List<ColumnFamilyDescriptor> list, List<ColumnFamilyHandle> list2, List<Integer> list3, boolean bl) throws RocksDBException {
        if (list.size() != list3.size()) {
            throw new IllegalArgumentException("There must be a ttl value per column family handle.");
        }
        int n = -1;
        byte[][] byArrayArray = new byte[list.size()][];
        long[] lArray = new long[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            ColumnFamilyDescriptor columnFamilyDescriptor = list.get(i);
            byArrayArray[i] = columnFamilyDescriptor.getName();
            lArray[i] = columnFamilyDescriptor.getOptions().nativeHandle_;
            if (!Arrays.equals(columnFamilyDescriptor.getName(), RocksDB.DEFAULT_COLUMN_FAMILY)) continue;
            n = i;
        }
        if (n < 0) {
            throw new IllegalArgumentException("You must provide the default column family in your columnFamilyDescriptors");
        }
        int[] nArray = new int[list3.size()];
        for (int i = 0; i < list3.size(); ++i) {
            nArray[i] = list3.get(i);
        }
        long[] lArray2 = TtlDB.openCF(dBOptions.nativeHandle_, string, byArrayArray, lArray, nArray, bl);
        TtlDB ttlDB = new TtlDB(lArray2[0]);
        for (int i = 1; i < lArray2.length; ++i) {
            list2.add(new ColumnFamilyHandle(ttlDB, lArray2[i]));
        }
        ttlDB.storeOptionsInstance(dBOptions);
        ttlDB.ownedColumnFamilyHandles.addAll(list2);
        ttlDB.storeDefaultColumnFamilyHandle(list2.get(n));
        return ttlDB;
    }

    @Override
    public void closeE() throws RocksDBException {
        if (this.owningHandle_.compareAndSet(true, false)) {
            try {
                TtlDB.closeDatabase(this.nativeHandle_);
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
                TtlDB.closeDatabase(this.nativeHandle_);
            }
            catch (RocksDBException rocksDBException) {
            }
            finally {
                this.disposeInternal();
            }
        }
    }

    public ColumnFamilyHandle createColumnFamilyWithTtl(ColumnFamilyDescriptor columnFamilyDescriptor, int n) throws RocksDBException {
        return new ColumnFamilyHandle(this, TtlDB.createColumnFamilyWithTtl(this.nativeHandle_, columnFamilyDescriptor.getName(), columnFamilyDescriptor.getOptions().nativeHandle_, n));
    }

    protected TtlDB(long l) {
        super(l);
    }

    @Override
    protected void disposeInternal(long l) {
        TtlDB.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    private static native long open(long var0, String var2, int var3, boolean var4) throws RocksDBException;

    private static native long[] openCF(long var0, String var2, byte[][] var3, long[] var4, int[] var5, boolean var6) throws RocksDBException;

    private static native long createColumnFamilyWithTtl(long var0, byte[] var2, long var3, int var5) throws RocksDBException;

    private static native void closeDatabase(long var0) throws RocksDBException;
}

