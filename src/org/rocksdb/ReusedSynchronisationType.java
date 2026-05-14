package org.rocksdb;

public enum ReusedSynchronisationType {
   MUTEX((byte)0),
   ADAPTIVE_MUTEX((byte)1),
   THREAD_LOCAL((byte)2);

   private final byte value;

   ReusedSynchronisationType(final byte nullxx) {
      this.value = nullxx;
   }

   public byte getValue() {
      return this.value;
   }

   public static ReusedSynchronisationType getReusedSynchronisationType(byte var0) {
      for (ReusedSynchronisationType var4 : values()) {
         if (var4.getValue() == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Illegal value provided for ReusedSynchronisationType.");
   }
}
