package org.rocksdb;

public enum SanityLevel {
   NONE((byte)0),
   LOOSELY_COMPATIBLE((byte)1),
   EXACT_MATCH((byte)-1);

   private final byte value;

   SanityLevel(byte var3) {
      this.value = var3;
   }

   byte getValue() {
      return this.value;
   }

   static SanityLevel fromValue(byte var0) throws IllegalArgumentException {
      for (SanityLevel var4 : values()) {
         if (var4.value == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Unknown value for SanityLevel: " + var0);
   }
}
