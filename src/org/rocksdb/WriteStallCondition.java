package org.rocksdb;

public enum WriteStallCondition {
   DELAYED((byte)0),
   STOPPED((byte)1),
   NORMAL((byte)2);

   private final byte value;

   WriteStallCondition(final byte nullxx) {
      this.value = nullxx;
   }

   byte getValue() {
      return this.value;
   }

   static WriteStallCondition fromValue(byte var0) {
      for (WriteStallCondition var4 : values()) {
         if (var4.value == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Illegal value provided for WriteStallCondition: " + var0);
   }
}
