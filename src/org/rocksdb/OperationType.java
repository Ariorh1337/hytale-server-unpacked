package org.rocksdb;

public enum OperationType {
   OP_UNKNOWN((byte)0),
   OP_COMPACTION((byte)1),
   OP_FLUSH((byte)2),
   OP_DBOPEN((byte)3);

   private final byte value;

   OperationType(byte var3) {
      this.value = var3;
   }

   byte getValue() {
      return this.value;
   }

   static OperationType fromValue(byte var0) throws IllegalArgumentException {
      for (OperationType var4 : values()) {
         if (var4.value == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Unknown value for OperationType: " + var0);
   }
}
