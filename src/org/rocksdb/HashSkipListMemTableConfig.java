package org.rocksdb;

public class HashSkipListMemTableConfig extends MemTableConfig {
   public static final int DEFAULT_BUCKET_COUNT = 1000000;
   public static final int DEFAULT_BRANCHING_FACTOR = 4;
   public static final int DEFAULT_HEIGHT = 4;
   private long bucketCount_ = 1000000L;
   private int branchingFactor_ = 4;
   private int height_ = 4;

   public HashSkipListMemTableConfig setBucketCount(long var1) {
      this.bucketCount_ = var1;
      return this;
   }

   public long bucketCount() {
      return this.bucketCount_;
   }

   public HashSkipListMemTableConfig setHeight(int var1) {
      this.height_ = var1;
      return this;
   }

   public int height() {
      return this.height_;
   }

   public HashSkipListMemTableConfig setBranchingFactor(int var1) {
      this.branchingFactor_ = var1;
      return this;
   }

   public int branchingFactor() {
      return this.branchingFactor_;
   }

   @Override
   protected long newMemTableFactoryHandle() {
      return newMemTableFactoryHandle(this.bucketCount_, this.height_, this.branchingFactor_);
   }

   private static native long newMemTableFactoryHandle(long var0, int var2, int var3) throws IllegalArgumentException;
}
