package org.rocksdb;

import java.util.List;

public class BackupEngine extends RocksObject implements AutoCloseable {
   protected BackupEngine(long var1) {
      super(var1);
   }

   public static BackupEngine open(Env var0, BackupEngineOptions var1) throws RocksDBException {
      return new BackupEngine(open(var0.nativeHandle_, var1.nativeHandle_));
   }

   public void createNewBackup(RocksDB var1) throws RocksDBException {
      this.createNewBackup(var1, false);
   }

   public void createNewBackup(RocksDB var1, boolean var2) throws RocksDBException {
      assert this.isOwningHandle();
      createNewBackup(this.nativeHandle_, var1.nativeHandle_, var2);
   }

   public void createNewBackupWithMetadata(RocksDB var1, String var2, boolean var3) throws RocksDBException {
      assert this.isOwningHandle();
      createNewBackupWithMetadata(this.nativeHandle_, var1.nativeHandle_, var2, var3);
   }

   public List<BackupInfo> getBackupInfo() {
      assert this.isOwningHandle();
      return getBackupInfo(this.nativeHandle_);
   }

   public int[] getCorruptedBackups() {
      assert this.isOwningHandle();
      return getCorruptedBackups(this.nativeHandle_);
   }

   public void garbageCollect() throws RocksDBException {
      assert this.isOwningHandle();
      garbageCollect(this.nativeHandle_);
   }

   public void purgeOldBackups(int var1) throws RocksDBException {
      assert this.isOwningHandle();
      purgeOldBackups(this.nativeHandle_, var1);
   }

   public void deleteBackup(int var1) throws RocksDBException {
      assert this.isOwningHandle();
      deleteBackup(this.nativeHandle_, var1);
   }

   public void restoreDbFromBackup(int var1, String var2, String var3, RestoreOptions var4) throws RocksDBException {
      assert this.isOwningHandle();
      restoreDbFromBackup(this.nativeHandle_, var1, var2, var3, var4.nativeHandle_);
   }

   public void restoreDbFromLatestBackup(String var1, String var2, RestoreOptions var3) throws RocksDBException {
      assert this.isOwningHandle();
      restoreDbFromLatestBackup(this.nativeHandle_, var1, var2, var3.nativeHandle_);
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
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);
}
