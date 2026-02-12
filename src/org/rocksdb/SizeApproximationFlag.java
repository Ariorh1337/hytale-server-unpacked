package org.rocksdb;

public enum SizeApproximationFlag {
   NONE((byte)0),
   INCLUDE_MEMTABLES((byte)1),
   INCLUDE_FILES((byte)2);

   private final byte value;

   SizeApproximationFlag(byte var3) {
      this.value = var3;
   }

   byte getValue() {
      return this.value;
   }
}
