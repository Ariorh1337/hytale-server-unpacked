package org.rocksdb;

public enum RateLimiterMode {
   READS_ONLY((byte)0),
   WRITES_ONLY((byte)1),
   ALL_IO((byte)2);

   private final byte value;

   RateLimiterMode(final byte nullxx) {
      this.value = nullxx;
   }

   public byte getValue() {
      return this.value;
   }

   public static RateLimiterMode getRateLimiterMode(byte var0) {
      for (RateLimiterMode var4 : values()) {
         if (var4.getValue() == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Illegal value provided for RateLimiterMode.");
   }
}
