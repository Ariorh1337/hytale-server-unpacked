/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.List;
import org.rocksdb.BackupEngineOptions;
import org.rocksdb.BackupInfo;
import org.rocksdb.Env;
import org.rocksdb.RestoreOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksObject;

public class BackupEngine
extends RocksObject
implements AutoCloseable {
    protected BackupEngine(long l) {
        super(l);
    }

    public static BackupEngine open(Env env, BackupEngineOptions backupEngineOptions) throws RocksDBException {
        return new BackupEngine(BackupEngine.open(env.nativeHandle_, backupEngineOptions.nativeHandle_));
    }

    public void createNewBackup(RocksDB rocksDB) throws RocksDBException {
        this.createNewBackup(rocksDB, false);
    }

    public void createNewBackup(RocksDB rocksDB, boolean bl) throws RocksDBException {
        assert (this.isOwningHandle());
        BackupEngine.createNewBackup(this.nativeHandle_, rocksDB.nativeHandle_, bl);
    }

    public void createNewBackupWithMetadata(RocksDB rocksDB, String string, boolean bl) throws RocksDBException {
        assert (this.isOwningHandle());
        BackupEngine.createNewBackupWithMetadata(this.nativeHandle_, rocksDB.nativeHandle_, string, bl);
    }

    public List<BackupInfo> getBackupInfo() {
        assert (this.isOwningHandle());
        return BackupEngine.getBackupInfo(this.nativeHandle_);
    }

    public int[] getCorruptedBackups() {
        assert (this.isOwningHandle());
        return BackupEngine.getCorruptedBackups(this.nativeHandle_);
    }

    public void garbageCollect() throws RocksDBException {
        assert (this.isOwningHandle());
        BackupEngine.garbageCollect(this.nativeHandle_);
    }

    public void purgeOldBackups(int n) throws RocksDBException {
        assert (this.isOwningHandle());
        BackupEngine.purgeOldBackups(this.nativeHandle_, n);
    }

    public void deleteBackup(int n) throws RocksDBException {
        assert (this.isOwningHandle());
        BackupEngine.deleteBackup(this.nativeHandle_, n);
    }

    public void restoreDbFromBackup(int n, String string, String string2, RestoreOptions restoreOptions) throws RocksDBException {
        assert (this.isOwningHandle());
        BackupEngine.restoreDbFromBackup(this.nativeHandle_, n, string, string2, restoreOptions.nativeHandle_);
    }

    public void restoreDbFromLatestBackup(String string, String string2, RestoreOptions restoreOptions) throws RocksDBException {
        assert (this.isOwningHandle());
        BackupEngine.restoreDbFromLatestBackup(this.nativeHandle_, string, string2, restoreOptions.nativeHandle_);
    }

    private static native long open(long var0, long var2) throws RocksDBException;

    private static native void createNewBackup(long var0, long var2, boolean var4) throws RocksDBException;

    private static native void createNewBackupWithMetadata(long var0, long var2, String var4, boolean var5) throws RocksDBException;

    private static native List<BackupInfo> getBackupInfo(long var0);

    private static native int[] getCorruptedBackups(long var0);

    private static native void garbageCollect(long var0) throws RocksDBException;

    private static native void purgeOldBackups(long var0, int var2) throws RocksDBException;

    private static native void deleteBackup(long var0, int var2) throws RocksDBException;

    private static native void restoreDbFromBackup(long var0, int var2, String var3, String var4, long var5) throws RocksDBException;

    private static native void restoreDbFromLatestBackup(long var0, String var2, String var3, long var4) throws RocksDBException;

    @Override
    protected final void disposeInternal(long l) {
        BackupEngine.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);
}

