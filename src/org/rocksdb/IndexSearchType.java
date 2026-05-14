package org.rocksdb;

public enum IndexSearchType {
   kBinary((byte)0),
   kInterpolation((byte)1),
   kAuto((byte)2);

   private final byte value;

   IndexSearchType(final byte nullxx) {
      this.value = nullxx;
   }

   byte getValue() {
      return this.value;
   }
}
