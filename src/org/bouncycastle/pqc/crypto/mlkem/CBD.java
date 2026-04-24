package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.util.Pack;

class CBD {
   static void eta2(Poly var0, byte[] var1) {
      for (int var2 = 0; var2 < 32; var2++) {
         int var3 = Pack.littleEndianToInt(var1, 4 * var2);
         int var4 = var3 & 1431655765;
         var4 += var3 >>> 1 & 1431655765;

         for (int var5 = 0; var5 < 8; var5++) {
            short var6 = (short)(var4 >>> 4 * var5 + 0 & 3);
            short var7 = (short)(var4 >>> 4 * var5 + 2 & 3);
            var0.setCoeffIndex(8 * var2 + var5, (short)(var6 - var7));
         }
      }
   }

   static void eta3(Poly var0, byte[] var1) {
      for (int var2 = 0; var2 < 64; var2++) {
         int var3 = Pack.littleEndianToInt24(var1, 3 * var2);
         int var4 = var3 & 2396745;
         var4 += var3 >>> 1 & 2396745;
         var4 += var3 >>> 2 & 2396745;

         for (int var5 = 0; var5 < 4; var5++) {
            short var6 = (short)(var4 >>> 6 * var5 + 0 & 7);
            short var7 = (short)(var4 >>> 6 * var5 + 3 & 7);
            var0.setCoeffIndex(4 * var2 + var5, (short)(var6 - var7));
         }
      }
   }
}
