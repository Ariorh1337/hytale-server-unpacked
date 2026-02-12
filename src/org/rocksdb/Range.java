package org.rocksdb;

public class Range {
   final Slice start;
   final Slice limit;

   public Range(Slice var1, Slice var2) {
      this.start = var1;
      this.limit = var2;
   }
}
