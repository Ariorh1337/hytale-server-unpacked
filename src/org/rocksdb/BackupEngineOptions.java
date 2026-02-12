package org.rocksdb;

import java.io.File;

public class BackupEngineOptions extends RocksObject {
   private Env backupEnv = null;
   private Logger infoLog = null;
   private RateLimiter backupRateLimiter = null;
   private RateLimiter restoreRateLimiter = null;

   public BackupEngineOptions(String var1) {
      super(newBackupEngineOptions(ensureWritableFile(var1)));
   }

   private static String ensureWritableFile(String var0) {
      File var1 = var0 == null ? null : new File(var0);
      if (var1 != null && var1.isDirectory() && var1.canWrite()) {
         return var0;
      } else {
         throw new IllegalArgumentException("Illegal path provided.");
      }
   }

   public String backupDir() {
      assert this.isOwningHandle();
      return backupDir(this.nativeHandle_);
   }

   public BackupEngineOptions setBackupEnv(Env var1) {
      assert this.isOwningHandle();
      setBackupEnv(this.nativeHandle_, var1.nativeHandle_);
      this.backupEnv = var1;
      return this;
   }

   public Env backupEnv() {
      return this.backupEnv;
   }

   public BackupEngineOptions setShareTableFiles(boolean var1) {
      assert this.isOwningHandle();
      setShareTableFiles(this.nativeHandle_, var1);
      return this;
   }

   public boolean shareTableFiles() {
      assert this.isOwningHandle();
      return shareTableFiles(this.nativeHandle_);
   }

   public BackupEngineOptions setInfoLog(Logger var1) {
      assert this.isOwningHandle();
      setInfoLog(this.nativeHandle_, var1.nativeHandle_);
      this.infoLog = var1;
      return this;
   }

   public Logger infoLog() {
      return this.infoLog;
   }

   public BackupEngineOptions setSync(boolean var1) {
      assert this.isOwningHandle();
      setSync(this.nativeHandle_, var1);
      return this;
   }

   public boolean sync() {
      assert this.isOwningHandle();
      return sync(this.nativeHandle_);
   }

   public BackupEngineOptions setDestroyOldData(boolean var1) {
      assert this.isOwningHandle();
      setDestroyOldData(this.nativeHandle_, var1);
      return this;
   }

   public boolean destroyOldData() {
      assert this.isOwningHandle();
      return destroyOldData(this.nativeHandle_);
   }

   public BackupEngineOptions setBackupLogFiles(boolean var1) {
      assert this.isOwningHandle();
      setBackupLogFiles(this.nativeHandle_, var1);
      return this;
   }

   public boolean backupLogFiles() {
      assert this.isOwningHandle();
      return backupLogFiles(this.nativeHandle_);
   }

   public BackupEngineOptions setBackupRateLimit(long var1) {
      assert this.isOwningHandle();
      setBackupRateLimit(this.nativeHandle_, var1 <= 0L ? 0L : var1);
      return this;
   }

   public long backupRateLimit() {
      assert this.isOwningHandle();
      return backupRateLimit(this.nativeHandle_);
   }

   public BackupEngineOptions setBackupRateLimiter(RateLimiter var1) {
      assert this.isOwningHandle();
      setBackupRateLimiter(this.nativeHandle_, var1.nativeHandle_);
      this.backupRateLimiter = var1;
      return this;
   }

   public RateLimiter backupRateLimiter() {
      assert this.isOwningHandle();
      return this.backupRateLimiter;
   }

   public BackupEngineOptions setRestoreRateLimit(long var1) {
      assert this.isOwningHandle();
      setRestoreRateLimit(this.nativeHandle_, var1 <= 0L ? 0L : var1);
      return this;
   }

   public long restoreRateLimit() {
      assert this.isOwningHandle();
      return restoreRateLimit(this.nativeHandle_);
   }

   public BackupEngineOptions setRestoreRateLimiter(RateLimiter var1) {
      assert this.isOwningHandle();
      setRestoreRateLimiter(this.nativeHandle_, var1.nativeHandle_);
      this.restoreRateLimiter = var1;
      return this;
   }

   public RateLimiter restoreRateLimiter() {
      assert this.isOwningHandle();
      return this.restoreRateLimiter;
   }

   public BackupEngineOptions setShareFilesWithChecksum(boolean var1) {
      assert this.isOwningHandle();
      setShareFilesWithChecksum(this.nativeHandle_, var1);
      return this;
   }

   public boolean shareFilesWithChecksum() {
      assert this.isOwningHandle();
      return shareFilesWithChecksum(this.nativeHandle_);
   }

   public BackupEngineOptions setMaxBackgroundOperations(int var1) {
      assert this.isOwningHandle();
      setMaxBackgroundOperations(this.nativeHandle_, var1);
      return this;
   }

   public int maxBackgroundOperations() {
      assert this.isOwningHandle();
      return maxBackgroundOperations(this.nativeHandle_);
   }

   public BackupEngineOptions setCallbackTriggerIntervalSize(long var1) {
      assert this.isOwningHandle();
      setCallbackTriggerIntervalSize(this.nativeHandle_, var1);
      return this;
   }

   public long callbackTriggerIntervalSize() {
      assert this.isOwningHandle();
      return callbackTriggerIntervalSize(this.nativeHandle_);
   }

   private static native long newBackupEngineOptions(String var0);

   private static native String backupDir(long var0);

   private static native void setBackupEnv(long var0, long var2);

   private static native void setShareTableFiles(long var0, boolean var2);

   private static native boolean shareTableFiles(long var0);

   private static native void setInfoLog(long var0, long var2);

   private static native void setSync(long var0, boolean var2);

   private static native boolean sync(long var0);

   private static native void setDestroyOldData(long var0, boolean var2);

   private static native boolean destroyOldData(long var0);

   private static native void setBackupLogFiles(long var0, boolean var2);

   private static native boolean backupLogFiles(long var0);

   private static native void setBackupRateLimit(long var0, long var2);

   private static native long backupRateLimit(long var0);

   private static native void setBackupRateLimiter(long var0, long var2);

   private static native void setRestoreRateLimit(long var0, long var2);

   private static native long restoreRateLimit(long var0);

   private static native void setRestoreRateLimiter(long var0, long var2);

   private static native void setShareFilesWithChecksum(long var0, boolean var2);

   private static native boolean shareFilesWithChecksum(long var0);

   private static native void setMaxBackgroundOperations(long var0, int var2);

   private static native int maxBackgroundOperations(long var0);

   private static native void setCallbackTriggerIntervalSize(long var0, long var2);

   private static native long callbackTriggerIntervalSize(long var0);

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);
}
