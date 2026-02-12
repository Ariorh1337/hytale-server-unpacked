package org.rocksdb;

public enum IndexShorteningMode {
   kNoShortening((byte)0),
   kShortenSeparators((byte)1),
   kShortenSeparatorsAndSuccessor((byte)2);

   private final byte value;

   IndexShorteningMode(byte var3) {
      this.value = var3;
   }

   byte getValue() {
      return this.value;
   }
}
