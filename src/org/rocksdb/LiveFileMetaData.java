package org.rocksdb;

public class LiveFileMetaData extends SstFileMetaData {
   private final byte[] columnFamilyName;
   private final int level;

   private LiveFileMetaData(
      byte[] var1,
      int var2,
      String var3,
      String var4,
      long var5,
      long var7,
      long var9,
      byte[] var11,
      byte[] var12,
      long var13,
      boolean var15,
      long var16,
      long var18,
      byte[] var20
   ) {
      super(var3, var4, var5, var7, var9, var11, var12, var13, var15, var16, var18, var20);
      this.columnFamilyName = var1;
      this.level = var2;
   }

   public byte[] columnFamilyName() {
      return this.columnFamilyName;
   }

   public int level() {
      return this.level;
   }

   public long newLiveFileMetaDataHandle() {
      return this.newLiveFileMetaDataHandle(
         this.columnFamilyName(),
         this.columnFamilyName().length,
         this.level(),
         this.fileName(),
         this.path(),
         this.size(),
         this.smallestSeqno(),
         this.largestSeqno(),
         this.smallestKey(),
         this.smallestKey().length,
         this.largestKey(),
         this.largestKey().length,
         this.numReadsSampled(),
         this.beingCompacted(),
         this.numEntries(),
         this.numDeletions()
      );
   }

   private native long newLiveFileMetaDataHandle(
      byte[] var1,
      int var2,
      int var3,
      String var4,
      String var5,
      long var6,
      long var8,
      long var10,
      byte[] var12,
      int var13,
      byte[] var14,
      int var15,
      long var16,
      boolean var18,
      long var19,
      long var21
   );
}
