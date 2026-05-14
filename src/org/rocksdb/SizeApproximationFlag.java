package org.rocksdb;

public enum SizeApproximationFlag {
   NONE((byte)0),
   INCLUDE_MEMTABLES((byte)1),
   INCLUDE_FILES((byte)2),
   INCLUDE_BLOB_FILES((byte)4);

   private final byte value;

   SizeApproximationFlag(final byte nullxx) {
      this.value = nullxx;
   }

   byte getValue() {
      return this.value;
   }
}
