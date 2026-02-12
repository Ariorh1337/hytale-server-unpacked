package org.rocksdb;

public enum DataBlockIndexType {
   kDataBlockBinarySearch((byte)0),
   kDataBlockBinaryAndHash((byte)1);

   private final byte value;

   DataBlockIndexType(byte var3) {
      this.value = var3;
   }

   byte getValue() {
      return this.value;
   }
}
