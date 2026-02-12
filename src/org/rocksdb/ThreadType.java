package org.rocksdb;

public enum ThreadType {
   HIGH_PRIORITY((byte)0),
   LOW_PRIORITY((byte)1),
   USER((byte)2),
   BOTTOM_PRIORITY((byte)3);

   private final byte value;

   ThreadType(byte var3) {
      this.value = var3;
   }

   byte getValue() {
      return this.value;
   }

   static ThreadType fromValue(byte var0) throws IllegalArgumentException {
      for (ThreadType var4 : values()) {
         if (var4.value == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Unknown value for ThreadType: " + var0);
   }
}
