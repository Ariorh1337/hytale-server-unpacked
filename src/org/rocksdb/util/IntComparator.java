package org.rocksdb.util;

import java.nio.ByteBuffer;
import org.rocksdb.AbstractComparator;
import org.rocksdb.ComparatorOptions;

public final class IntComparator extends AbstractComparator {
   public IntComparator(ComparatorOptions var1) {
      super(var1);
   }

   @Override
   public String name() {
      return "rocksdb.java.IntComparator";
   }

   @Override
   public int compare(ByteBuffer var1, ByteBuffer var2) {
      return this.compareIntKeys(var1, var2);
   }

   private int compareIntKeys(ByteBuffer var1, ByteBuffer var2) {
      int var3 = var1.getInt();
      int var4 = var2.getInt();
      long var5 = (long)var3 - var4;
      int var7;
      if (var5 < -2147483648L) {
         var7 = Integer.MIN_VALUE;
      } else if (var5 > 2147483647L) {
         var7 = Integer.MAX_VALUE;
      } else {
         var7 = (int)var5;
      }

      return var7;
   }
}
