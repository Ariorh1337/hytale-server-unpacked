package org.rocksdb;

public enum ReusedSynchronisationType {
   MUTEX((byte)0),
   ADAPTIVE_MUTEX((byte)1),
   THREAD_LOCAL((byte)2);

   private final byte value;

   ReusedSynchronisationType(byte var3) {
      this.value = var3;
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
