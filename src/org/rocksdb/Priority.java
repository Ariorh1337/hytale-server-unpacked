package org.rocksdb;

public enum Priority {
   BOTTOM((byte)0),
   LOW((byte)1),
   HIGH((byte)2),
   TOTAL((byte)3);

   private final byte value;

   Priority(byte var3) {
      this.value = var3;
   }

   byte getValue() {
      return this.value;
   }

   static Priority getPriority(byte var0) {
      for (Priority var4 : values()) {
         if (var4.getValue() == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Illegal value provided for Priority.");
   }
}
