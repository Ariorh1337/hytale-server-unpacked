package org.rocksdb;

import java.util.Objects;

public class ExternalFileIngestionInfo {
   private final String columnFamilyName;
   private final String externalFilePath;
   private final String internalFilePath;
   private final long globalSeqno;
   private final TableProperties tableProperties;

   ExternalFileIngestionInfo(String var1, String var2, String var3, long var4, TableProperties var6) {
      this.columnFamilyName = var1;
      this.externalFilePath = var2;
      this.internalFilePath = var3;
      this.globalSeqno = var4;
      this.tableProperties = var6;
   }

   public String getColumnFamilyName() {
      return this.columnFamilyName;
   }

   public String getExternalFilePath() {
      return this.externalFilePath;
   }

   public String getInternalFilePath() {
      return this.internalFilePath;
   }

   public long getGlobalSeqno() {
      return this.globalSeqno;
   }

   public TableProperties getTableProperties() {
      return this.tableProperties;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         ExternalFileIngestionInfo var2 = (ExternalFileIngestionInfo)var1;
         return this.globalSeqno == var2.globalSeqno
            && Objects.equals(this.columnFamilyName, var2.columnFamilyName)
            && Objects.equals(this.externalFilePath, var2.externalFilePath)
            && Objects.equals(this.internalFilePath, var2.internalFilePath)
            && Objects.equals(this.tableProperties, var2.tableProperties);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.columnFamilyName, this.externalFilePath, this.internalFilePath, this.globalSeqno, this.tableProperties);
   }

   @Override
   public String toString() {
      return "ExternalFileIngestionInfo{columnFamilyName='"
         + this.columnFamilyName
         + "', externalFilePath='"
         + this.externalFilePath
         + "', internalFilePath='"
         + this.internalFilePath
         + "', globalSeqno="
         + this.globalSeqno
         + ", tableProperties="
         + this.tableProperties
         + "}";
   }
}
