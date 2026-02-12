package org.rocksdb;

public enum EncodingType {
   kPlain((byte)0),
   kPrefix((byte)1);

   private final byte value_;

   public byte getValue() {
      return this.value_;
   }

   EncodingType(byte var3) {
      this.value_ = var3;
   }
}
