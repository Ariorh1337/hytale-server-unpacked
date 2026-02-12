package org.rocksdb;

public enum MemoryUsageType {
   kMemTableTotal((byte)0),
   kMemTableUnFlushed((byte)1),
   kTableReadersTotal((byte)2),
   kCacheTotal((byte)3),
   kNumUsageTypes((byte)4);

   private final byte value_;

   public byte getValue() {
      return this.value_;
   }

   public static MemoryUsageType getMemoryUsageType(byte var0) {
      for (MemoryUsageType var4 : values()) {
         if (var4.getValue() == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Illegal value provided for MemoryUsageType.");
   }

   MemoryUsageType(byte var3) {
      this.value_ = var3;
   }
}
