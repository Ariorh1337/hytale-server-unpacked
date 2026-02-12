package org.rocksdb;

public class BackupInfo {
   private final int backupId_;
   private final long timestamp_;
   private final long size_;
   private final int numberFiles_;
   private final String app_metadata_;

   BackupInfo(int var1, long var2, long var4, int var6, String var7) {
      this.backupId_ = var1;
      this.timestamp_ = var2;
      this.size_ = var4;
      this.numberFiles_ = var6;
      this.app_metadata_ = var7;
   }

   public int backupId() {
      return this.backupId_;
   }

   public long timestamp() {
      return this.timestamp_;
   }

   public long size() {
      return this.size_;
   }

   public int numberFiles() {
      return this.numberFiles_;
   }

   public String appMetadata() {
      return this.app_metadata_;
   }
}
