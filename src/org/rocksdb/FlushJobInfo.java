package org.rocksdb;

import java.util.Objects;

public class FlushJobInfo {
   private final long columnFamilyId;
   private final String columnFamilyName;
   private final String filePath;
   private final long threadId;
   private final int jobId;
   private final boolean triggeredWritesSlowdown;
   private final boolean triggeredWritesStop;
   private final long smallestSeqno;
   private final long largestSeqno;
   private final TableProperties tableProperties;
   private final FlushReason flushReason;

   FlushJobInfo(long var1, String var3, String var4, long var5, int var7, boolean var8, boolean var9, long var10, long var12, TableProperties var14, byte var15) {
      this.columnFamilyId = var1;
      this.columnFamilyName = var3;
      this.filePath = var4;
      this.threadId = var5;
      this.jobId = var7;
      this.triggeredWritesSlowdown = var8;
      this.triggeredWritesStop = var9;
      this.smallestSeqno = var10;
      this.largestSeqno = var12;
      this.tableProperties = var14;
      this.flushReason = FlushReason.fromValue(var15);
   }

   public long getColumnFamilyId() {
      return this.columnFamilyId;
   }

   public String getColumnFamilyName() {
      return this.columnFamilyName;
   }

   public String getFilePath() {
      return this.filePath;
   }

   public long getThreadId() {
      return this.threadId;
   }

   public int getJobId() {
      return this.jobId;
   }

   public boolean isTriggeredWritesSlowdown() {
      return this.triggeredWritesSlowdown;
   }

   public boolean isTriggeredWritesStop() {
      return this.triggeredWritesStop;
   }

   public long getSmallestSeqno() {
      return this.smallestSeqno;
   }

   public long getLargestSeqno() {
      return this.largestSeqno;
   }

   public TableProperties getTableProperties() {
      return this.tableProperties;
   }

   public FlushReason getFlushReason() {
      return this.flushReason;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         FlushJobInfo var2 = (FlushJobInfo)var1;
         return this.columnFamilyId == var2.columnFamilyId
            && this.threadId == var2.threadId
            && this.jobId == var2.jobId
            && this.triggeredWritesSlowdown == var2.triggeredWritesSlowdown
            && this.triggeredWritesStop == var2.triggeredWritesStop
            && this.smallestSeqno == var2.smallestSeqno
            && this.largestSeqno == var2.largestSeqno
            && Objects.equals(this.columnFamilyName, var2.columnFamilyName)
            && Objects.equals(this.filePath, var2.filePath)
            && Objects.equals(this.tableProperties, var2.tableProperties)
            && this.flushReason == var2.flushReason;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(
         this.columnFamilyId,
         this.columnFamilyName,
         this.filePath,
         this.threadId,
         this.jobId,
         this.triggeredWritesSlowdown,
         this.triggeredWritesStop,
         this.smallestSeqno,
         this.largestSeqno,
         this.tableProperties,
         this.flushReason
      );
   }

   @Override
   public String toString() {
      return "FlushJobInfo{columnFamilyId="
         + this.columnFamilyId
         + ", columnFamilyName='"
         + this.columnFamilyName
         + '\''
         + ", filePath='"
         + this.filePath
         + '\''
         + ", threadId="
         + this.threadId
         + ", jobId="
         + this.jobId
         + ", triggeredWritesSlowdown="
         + this.triggeredWritesSlowdown
         + ", triggeredWritesStop="
         + this.triggeredWritesStop
         + ", smallestSeqno="
         + this.smallestSeqno
         + ", largestSeqno="
         + this.largestSeqno
         + ", tableProperties="
         + this.tableProperties
         + ", flushReason="
         + this.flushReason
         + '}';
   }
}
