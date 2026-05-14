package org.rocksdb;

public enum IndexShorteningMode {
   kNoShortening((byte)0),
   kShortenSeparators((byte)1),
   kShortenSeparatorsAndSuccessor((byte)2);

   private final byte value;

   IndexShorteningMode(final byte nullxx) {
      this.value = nullxx;
   }

   byte getValue() {
      return this.value;
   }
}
