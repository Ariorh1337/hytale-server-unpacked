package org.rocksdb;

public enum TxnDBWritePolicy {
   WRITE_COMMITTED((byte)0),
   WRITE_PREPARED((byte)1),
   WRITE_UNPREPARED((byte)2);

   private final byte value;

   TxnDBWritePolicy(final byte nullxx) {
      this.value = nullxx;
   }

   public byte getValue() {
      return this.value;
   }

   public static TxnDBWritePolicy getTxnDBWritePolicy(byte var0) {
      for (TxnDBWritePolicy var4 : values()) {
         if (var4.getValue() == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Illegal value provided for TxnDBWritePolicy.");
   }
}
