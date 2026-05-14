package org.rocksdb;

public enum WalFileType {
   kArchivedLogFile((byte)0),
   kAliveLogFile((byte)1);

   private final byte value;

   WalFileType(final byte nullxx) {
      this.value = nullxx;
   }

   byte getValue() {
      return this.value;
   }

   static WalFileType fromValue(byte var0) {
      for (WalFileType var4 : values()) {
         if (var4.value == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Illegal value provided for WalFileType: " + var0);
   }
}
