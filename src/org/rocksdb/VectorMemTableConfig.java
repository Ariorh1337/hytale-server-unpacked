package org.rocksdb;

public class VectorMemTableConfig extends MemTableConfig {
   public static final int DEFAULT_RESERVED_SIZE = 0;
   private int reservedSize_ = 0;

   public VectorMemTableConfig setReservedSize(int var1) {
      this.reservedSize_ = var1;
      return this;
   }

   public int reservedSize() {
      return this.reservedSize_;
   }

   @Override
   protected long newMemTableFactoryHandle() {
      return newMemTableFactoryHandle(this.reservedSize_);
   }

   private static native long newMemTableFactoryHandle(long var0) throws IllegalArgumentException;
}
