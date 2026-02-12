package org.rocksdb;

public class HashLinkedListMemTableConfig extends MemTableConfig {
   public static final long DEFAULT_BUCKET_COUNT = 50000L;
   public static final long DEFAULT_HUGE_PAGE_TLB_SIZE = 0L;
   public static final int DEFAULT_BUCKET_ENTRIES_LOG_THRES = 4096;
   public static final boolean DEFAULT_IF_LOG_BUCKET_DIST_WHEN_FLUSH = true;
   public static final int DEFAUL_THRESHOLD_USE_SKIPLIST = 256;
   private long bucketCount_ = 50000L;
   private long hugePageTlbSize_ = 0L;
   private int bucketEntriesLoggingThreshold_ = 4096;
   private boolean ifLogBucketDistWhenFlush_ = true;
   private int thresholdUseSkiplist_ = 256;

   public HashLinkedListMemTableConfig setBucketCount(long var1) {
      this.bucketCount_ = var1;
      return this;
   }

   public long bucketCount() {
      return this.bucketCount_;
   }

   public HashLinkedListMemTableConfig setHugePageTlbSize(long var1) {
      this.hugePageTlbSize_ = var1;
      return this;
   }

   public long hugePageTlbSize() {
      return this.hugePageTlbSize_;
   }

   public HashLinkedListMemTableConfig setBucketEntriesLoggingThreshold(int var1) {
      this.bucketEntriesLoggingThreshold_ = var1;
      return this;
   }

   public int bucketEntriesLoggingThreshold() {
      return this.bucketEntriesLoggingThreshold_;
   }

   public HashLinkedListMemTableConfig setIfLogBucketDistWhenFlush(boolean var1) {
      this.ifLogBucketDistWhenFlush_ = var1;
      return this;
   }

   public boolean ifLogBucketDistWhenFlush() {
      return this.ifLogBucketDistWhenFlush_;
   }

   public HashLinkedListMemTableConfig setThresholdUseSkiplist(int var1) {
      this.thresholdUseSkiplist_ = var1;
      return this;
   }

   public int thresholdUseSkiplist() {
      return this.thresholdUseSkiplist_;
   }

   @Override
   protected long newMemTableFactoryHandle() {
      return newMemTableFactoryHandle(
         this.bucketCount_, this.hugePageTlbSize_, this.bucketEntriesLoggingThreshold_, this.ifLogBucketDistWhenFlush_, this.thresholdUseSkiplist_
      );
   }

   private static native long newMemTableFactoryHandle(long var0, long var2, int var4, boolean var5, int var6) throws IllegalArgumentException;
}
