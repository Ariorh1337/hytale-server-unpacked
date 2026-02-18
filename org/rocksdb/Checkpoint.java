/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.ExportImportFilesMetaData;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksObject;

public class Checkpoint
extends RocksObject {
    public static Checkpoint create(RocksDB rocksDB) {
        if (rocksDB == null) {
            throw new IllegalArgumentException("RocksDB instance shall not be null.");
        }
        if (!rocksDB.isOwningHandle()) {
            throw new IllegalStateException("RocksDB instance must be initialized.");
        }
        return new Checkpoint(rocksDB);
    }

    public void createCheckpoint(String string) throws RocksDBException {
        Checkpoint.createCheckpoint(this.nativeHandle_, string);
    }

    public ExportImportFilesMetaData exportColumnFamily(ColumnFamilyHandle columnFamilyHandle, String string) throws RocksDBException {
        return new ExportImportFilesMetaData(this.exportColumnFamily(this.nativeHandle_, columnFamilyHandle.nativeHandle_, string));
    }

    private Checkpoint(RocksDB rocksDB) {
        super(Checkpoint.newCheckpoint(rocksDB.nativeHandle_));
    }

    private static native long newCheckpoint(long var0);

    @Override
    protected final void disposeInternal(long l) {
        Checkpoint.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    private static native void createCheckpoint(long var0, String var2) throws RocksDBException;

    private native long exportColumnFamily(long var1, long var3, String var5) throws RocksDBException;
}

