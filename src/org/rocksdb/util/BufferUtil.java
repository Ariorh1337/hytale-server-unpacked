package org.rocksdb.util;

public class BufferUtil {
   public static void CheckBounds(int var0, int var1, int var2) {
      if ((var0 | var1 | var0 + var1 | var2 - (var0 + var1)) < 0) {
         throw new IndexOutOfBoundsException(String.format("offset(%d), len(%d), size(%d)", var0, var1, var2));
      }
   }
}
