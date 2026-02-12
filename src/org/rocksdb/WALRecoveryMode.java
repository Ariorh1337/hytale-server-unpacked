package org.rocksdb;

public enum WALRecoveryMode {
   TolerateCorruptedTailRecords((byte)0),
   AbsoluteConsistency((byte)1),
   PointInTimeRecovery((byte)2),
   SkipAnyCorruptedRecords((byte)3);

   private final byte value;

   WALRecoveryMode(byte var3) {
      this.value = var3;
   }

   public byte getValue() {
      return this.value;
   }

   public static WALRecoveryMode getWALRecoveryMode(byte var0) {
      for (WALRecoveryMode var4 : values()) {
         if (var4.getValue() == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Illegal value provided for WALRecoveryMode.");
   }
}
