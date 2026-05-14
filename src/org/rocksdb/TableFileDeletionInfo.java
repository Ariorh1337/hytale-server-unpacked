package org.rocksdb;

import java.util.Objects;

public class TableFileDeletionInfo {
   private final String dbName;
   private final String filePath;
   private final int jobId;
   private final Status status;

   TableFileDeletionInfo(String var1, String var2, int var3, Status var4) {
      this.dbName = var1;
      this.filePath = var2;
      this.jobId = var3;
      this.status = var4;
   }

   public String getDbName() {
      return this.dbName;
   }

   public String getFilePath() {
      return this.filePath;
   }

   public int getJobId() {
      return this.jobId;
   }

   public Status getStatus() {
      return this.status;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         TableFileDeletionInfo var2 = (TableFileDeletionInfo)var1;
         return this.jobId == var2.jobId
            && Objects.equals(this.dbName, var2.dbName)
            && Objects.equals(this.filePath, var2.filePath)
            && Objects.equals(this.status, var2.status);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.dbName, this.filePath, this.jobId, this.status);
   }

   @Override
   public String toString() {
      return "TableFileDeletionInfo{dbName='" + this.dbName + "', filePath='" + this.filePath + "', jobId=" + this.jobId + ", status=" + this.status + "}";
   }
}
