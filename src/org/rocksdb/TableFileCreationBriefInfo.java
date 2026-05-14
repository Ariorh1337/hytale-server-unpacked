package org.rocksdb;

import java.util.Objects;

public class TableFileCreationBriefInfo {
   private final String dbName;
   private final String columnFamilyName;
   private final String filePath;
   private final int jobId;
   private final TableFileCreationReason reason;

   protected TableFileCreationBriefInfo(String var1, String var2, String var3, int var4, byte var5) {
      this.dbName = var1;
      this.columnFamilyName = var2;
      this.filePath = var3;
      this.jobId = var4;
      this.reason = TableFileCreationReason.fromValue(var5);
   }

   public String getDbName() {
      return this.dbName;
   }

   public String getColumnFamilyName() {
      return this.columnFamilyName;
   }

   public String getFilePath() {
      return this.filePath;
   }

   public int getJobId() {
      return this.jobId;
   }

   public TableFileCreationReason getReason() {
      return this.reason;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         TableFileCreationBriefInfo var2 = (TableFileCreationBriefInfo)var1;
         return this.jobId == var2.jobId
            && Objects.equals(this.dbName, var2.dbName)
            && Objects.equals(this.columnFamilyName, var2.columnFamilyName)
            && Objects.equals(this.filePath, var2.filePath)
            && this.reason == var2.reason;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.dbName, this.columnFamilyName, this.filePath, this.jobId, this.reason);
   }

   @Override
   public String toString() {
      return "TableFileCreationBriefInfo{dbName='"
         + this.dbName
         + "', columnFamilyName='"
         + this.columnFamilyName
         + "', filePath='"
         + this.filePath
         + "', jobId="
         + this.jobId
         + ", reason="
         + this.reason
         + "}";
   }
}
