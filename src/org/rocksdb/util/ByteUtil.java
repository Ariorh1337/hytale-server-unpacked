package org.rocksdb.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ByteUtil {
   public static byte[] bytes(String var0) {
      return var0.getBytes(StandardCharsets.UTF_8);
   }

   public static int memcmp(ByteBuffer var0, ByteBuffer var1, int var2) {
      for (int var3 = 0; var3 < var2; var3++) {
         int var4 = var0.get(var3) & 255;
         int var5 = var1.get(var3) & 255;
         if (var4 != var5) {
            return var4 - var5;
         }
      }

      return 0;
   }
}
