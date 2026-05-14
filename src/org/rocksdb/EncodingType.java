package org.rocksdb;

public enum EncodingType {
   kPlain((byte)0),
   kPrefix((byte)1);

   private final byte value_;

   public byte getValue() {
      return this.value_;
   }

   EncodingType(final byte nullxx) {
      this.value_ = nullxx;
   }
}
