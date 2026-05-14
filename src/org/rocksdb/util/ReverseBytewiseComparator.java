package org.rocksdb.util;

import java.nio.ByteBuffer;
import org.rocksdb.AbstractComparator;
import org.rocksdb.ComparatorOptions;

public final class ReverseBytewiseComparator extends AbstractComparator {
   public ReverseBytewiseComparator(ComparatorOptions var1) {
      super(var1);
   }

   @Override
   public String name() {
      return "rocksdb.java.ReverseBytewiseComparator";
   }

   @Override
   public int compare(ByteBuffer var1, ByteBuffer var2) {
      return -BytewiseComparator._compare(var1, var2);
   }

   @Override
   public void findShortestSeparator(ByteBuffer var1, ByteBuffer var2) {
      int var3 = Math.min(var1.remaining(), var2.remaining());
      int var4 = 0;

      while (var4 < var3 && var1.get(var4) == var2.get(var4)) {
         var4++;
      }

      assert var4 <= var3;
      if (var4 != var3) {
         int var5 = var1.get(var4) & 255;
         int var6 = var2.get(var4) & 255;
         if (var5 > var6 && var4 < var1.remaining() - 1) {
            var1.limit(var4 + 1);
            assert BytewiseComparator._compare(var1.duplicate(), var2.duplicate()) > 0;
         }
      }
   }
}
