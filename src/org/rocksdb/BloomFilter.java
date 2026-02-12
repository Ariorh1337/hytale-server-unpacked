package org.rocksdb;

import java.util.Objects;

public class BloomFilter extends Filter {
   private static final double DEFAULT_BITS_PER_KEY = 10.0;
   private final double bitsPerKey;

   public BloomFilter() {
      this(10.0);
   }

   public BloomFilter(double var1) {
      this(createNewBloomFilter(var1), var1);
   }

   BloomFilter(long var1, double var3) {
      super(var1);
      this.bitsPerKey = var3;
   }

   public BloomFilter(double var1, boolean var3) {
      this(var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 != null && this.getClass() == var1.getClass() ? this.bitsPerKey == ((BloomFilter)var1).bitsPerKey : false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.bitsPerKey);
   }

   private static native long createNewBloomFilter(double var0);
}
