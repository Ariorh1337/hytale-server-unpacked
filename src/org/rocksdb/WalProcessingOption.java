package org.rocksdb;

public enum WalProcessingOption {
   CONTINUE_PROCESSING((byte)0),
   IGNORE_CURRENT_RECORD((byte)1),
   STOP_REPLAY((byte)2),
   CORRUPTED_RECORD((byte)3);

   private final byte value;

   WalProcessingOption(final byte nullxx) {
      this.value = nullxx;
   }

   byte getValue() {
      return this.value;
   }

   public static WalProcessingOption fromValue(byte var0) {
      for (WalProcessingOption var4 : values()) {
         if (var4.value == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Illegal value provided for WalProcessingOption: " + var0);
   }
}
