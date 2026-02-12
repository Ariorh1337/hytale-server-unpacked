package org.rocksdb;

public enum BackgroundErrorReason {
   FLUSH((byte)0),
   COMPACTION((byte)1),
   WRITE_CALLBACK((byte)2),
   MEMTABLE((byte)3);

   private final byte value;

   BackgroundErrorReason(byte var3) {
      this.value = var3;
   }

   byte getValue() {
      return this.value;
   }

   static BackgroundErrorReason fromValue(byte var0) {
      for (BackgroundErrorReason var4 : values()) {
         if (var4.value == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Illegal value provided for BackgroundErrorReason: " + var0);
   }
}
