package org.rocksdb;

import java.nio.Buffer;
import java.nio.ByteBuffer;

class AbstractComparatorJniBridge {
   private static int compareInternal(AbstractComparator var0, ByteBuffer var1, int var2, ByteBuffer var3, int var4) {
      if (var2 != -1) {
         ((Buffer)var1).mark();
         ((Buffer)var1).limit(var2);
      }

      if (var4 != -1) {
         ((Buffer)var3).mark();
         ((Buffer)var3).limit(var4);
      }

      int var5 = var0.compare(var1, var3);
      if (var2 != -1) {
         ((Buffer)var1).reset();
      }

      if (var4 != -1) {
         ((Buffer)var3).reset();
      }

      return var5;
   }

   private static int findShortestSeparatorInternal(AbstractComparator var0, ByteBuffer var1, int var2, ByteBuffer var3, int var4) {
      if (var2 != -1) {
         ((Buffer)var1).limit(var2);
      }

      if (var4 != -1) {
         ((Buffer)var3).limit(var4);
      }

      var0.findShortestSeparator(var1, var3);
      return var1.remaining();
   }

   private static int findShortSuccessorInternal(AbstractComparator var0, ByteBuffer var1, int var2) {
      if (var2 != -1) {
         ((Buffer)var1).limit(var2);
      }

      var0.findShortSuccessor(var1);
      return var1.remaining();
   }
}
