package org.rocksdb;

public enum ReadTier {
   READ_ALL_TIER((byte)0),
   BLOCK_CACHE_TIER((byte)1),
   PERSISTED_TIER((byte)2),
   MEMTABLE_TIER((byte)3);

   private final byte value;

   ReadTier(byte var3) {
      this.value = var3;
   }

   public byte getValue() {
      return this.value;
   }

   public static ReadTier getReadTier(byte var0) {
      for (ReadTier var4 : values()) {
         if (var4.getValue() == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Illegal value provided for ReadTier.");
   }
}
