package org.rocksdb;

public enum TableFileCreationReason {
   FLUSH((byte)0),
   COMPACTION((byte)1),
   RECOVERY((byte)2),
   MISC((byte)3);

   private final byte value;

   TableFileCreationReason(byte var3) {
      this.value = var3;
   }

   byte getValue() {
      return this.value;
   }

   static TableFileCreationReason fromValue(byte var0) {
      for (TableFileCreationReason var4 : values()) {
         if (var4.value == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Illegal value provided for TableFileCreationReason: " + var0);
   }
}
