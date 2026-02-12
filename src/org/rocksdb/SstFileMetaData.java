package org.rocksdb;

public class SstFileMetaData {
   private final String fileName;
   private final String path;
   private final long size;
   private final long smallestSeqno;
   private final long largestSeqno;
   private final byte[] smallestKey;
   private final byte[] largestKey;
   private final long numReadsSampled;
   private final boolean beingCompacted;
   private final long numEntries;
   private final long numDeletions;
   private final byte[] fileChecksum;

   protected SstFileMetaData(
      String var1, String var2, long var3, long var5, long var7, byte[] var9, byte[] var10, long var11, boolean var13, long var14, long var16, byte[] var18
   ) {
      this.fileName = var1;
      this.path = var2;
      this.size = var3;
      this.smallestSeqno = var5;
      this.largestSeqno = var7;
      this.smallestKey = var9;
      this.largestKey = var10;
      this.numReadsSampled = var11;
      this.beingCompacted = var13;
      this.numEntries = var14;
      this.numDeletions = var16;
      this.fileChecksum = var18;
   }

   public String fileName() {
      return this.fileName;
   }

   public String path() {
      return this.path;
   }

   public long size() {
      return this.size;
   }

   public long smallestSeqno() {
      return this.smallestSeqno;
   }

   public long largestSeqno() {
      return this.largestSeqno;
   }

   public byte[] smallestKey() {
      return this.smallestKey;
   }

   public byte[] largestKey() {
      return this.largestKey;
   }

   public long numReadsSampled() {
      return this.numReadsSampled;
   }

   public boolean beingCompacted() {
      return this.beingCompacted;
   }

   public long numEntries() {
      return this.numEntries;
   }

   public long numDeletions() {
      return this.numDeletions;
   }

   public byte[] fileChecksum() {
      return this.fileChecksum;
   }
}
