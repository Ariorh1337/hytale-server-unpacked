package org.rocksdb.util;

import java.nio.ByteBuffer;
import org.rocksdb.AbstractComparator;
import org.rocksdb.ComparatorOptions;

public final class BytewiseComparator extends AbstractComparator {
   public BytewiseComparator(ComparatorOptions var1) {
      super(var1);
   }

   @Override
   public String name() {
      return "rocksdb.java.BytewiseComparator";
   }

   @Override
   public int compare(ByteBuffer var1, ByteBuffer var2) {
      return _compare(var1, var2);
   }

   static int _compare(ByteBuffer var0, ByteBuffer var1) {
      assert var0 != null && var1 != null;
      int var2 = var0.remaining() < var1.remaining() ? var0.remaining() : var1.remaining();
      int var3 = ByteUtil.memcmp(var0, var1, var2);
      if (var3 == 0) {
         if (var0.remaining() < var1.remaining()) {
            var3 = -1;
         } else if (var0.remaining() > var1.remaining()) {
            var3 = 1;
         }
      }

      return var3;
   }

   @Override
   public void findShortestSeparator(ByteBuffer var1, ByteBuffer var2) {
      int var3 = Math.min(var1.remaining(), var2.remaining());
      int var4 = 0;

      while (var4 < var3 && var1.get(var4) == var2.get(var4)) {
         var4++;
      }

      if (var4 < var3) {
         int var5 = var1.get(var4) & 255;
         int var6 = var2.get(var4) & 255;
         if (var5 >= var6) {
            return;
         }

         assert var5 < var6;
         if (var4 >= var2.remaining() - 1 && var5 + 1 >= var6) {
            var4++;

            while (var4 < var1.remaining()) {
               if ((var1.get(var4) & 255) < 255) {
                  var1.put(var4, (byte)((var1.get(var4) & 255) + 1));
                  var1.limit(var4 + 1);
                  break;
               }

               var4++;
            }
         } else {
            var1.put(var4, (byte)((var1.get(var4) & 255) + 1));
            var1.limit(var4 + 1);
         }

         assert this.compare(var1.duplicate(), var2.duplicate()) < 0;
      }
   }

   @Override
   public void findShortSuccessor(ByteBuffer var1) {
      int var2 = var1.remaining();

      for (int var3 = 0; var3 < var2; var3++) {
         int var4 = var1.get(var3) & 255;
         if (var4 != 255) {
            var1.put(var3, (byte)(var4 + 1));
            var1.limit(var3 + 1);
            return;
         }
      }
   }
}
