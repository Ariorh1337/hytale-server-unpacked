package org.rocksdb;

public class SkipListMemTableConfig extends MemTableConfig {
   public static final long DEFAULT_LOOKAHEAD = 0L;
   private long lookahead_ = 0L;

   public SkipListMemTableConfig setLookahead(long var1) {
      this.lookahead_ = var1;
      return this;
   }

   public long lookahead() {
      return this.lookahead_;
   }

   @Override
   protected long newMemTableFactoryHandle() {
      return newMemTableFactoryHandle0(this.lookahead_);
   }

   private static native long newMemTableFactoryHandle0(long var0) throws IllegalArgumentException;
}
