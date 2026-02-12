package org.rocksdb;

import java.util.Objects;

public class MemTableInfo {
   private final String columnFamilyName;
   private final long firstSeqno;
   private final long earliestSeqno;
   private final long numEntries;
   private final long numDeletes;

   MemTableInfo(String var1, long var2, long var4, long var6, long var8) {
      this.columnFamilyName = var1;
      this.firstSeqno = var2;
      this.earliestSeqno = var4;
      this.numEntries = var6;
      this.numDeletes = var8;
   }

   public String getColumnFamilyName() {
      return this.columnFamilyName;
   }

   public long getFirstSeqno() {
      return this.firstSeqno;
   }

   public long getEarliestSeqno() {
      return this.earliestSeqno;
   }

   public long getNumEntries() {
      return this.numEntries;
   }

   public long getNumDeletes() {
      return this.numDeletes;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         MemTableInfo var2 = (MemTableInfo)var1;
         return this.firstSeqno == var2.firstSeqno
            && this.earliestSeqno == var2.earliestSeqno
            && this.numEntries == var2.numEntries
            && this.numDeletes == var2.numDeletes
            && Objects.equals(this.columnFamilyName, var2.columnFamilyName);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.columnFamilyName, this.firstSeqno, this.earliestSeqno, this.numEntries, this.numDeletes);
   }

   @Override
   public String toString() {
      return "MemTableInfo{columnFamilyName='"
         + this.columnFamilyName
         + '\''
         + ", firstSeqno="
         + this.firstSeqno
         + ", earliestSeqno="
         + this.earliestSeqno
         + ", numEntries="
         + this.numEntries
         + ", numDeletes="
         + this.numDeletes
         + '}';
   }
}
