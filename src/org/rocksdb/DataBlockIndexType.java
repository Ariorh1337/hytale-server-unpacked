package org.rocksdb;

public enum DataBlockIndexType {
   kDataBlockBinarySearch((byte)0),
   kDataBlockBinaryAndHash((byte)1);

   private final byte value;

   DataBlockIndexType(final byte nullxx) {
      this.value = nullxx;
   }

   byte getValue() {
      return this.value;
   }
}
