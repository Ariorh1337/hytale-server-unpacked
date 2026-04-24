package org.bouncycastle.pqc.crypto.frodo;

abstract class Noise {
   static void sample(short[] var0, short[] var1, int var2, short[] var3) {
      int var4 = 0;

      for (int var5 = var3.length; var4 < var5; var4++) {
         int var6 = 0;
         int var7 = var1[var2 + var4] & '\uffff';
         int var8 = var7 >>> 1;
         int var9 = var7 & 1;

         for (int var10 = 0; var10 < var0.length - 1; var10++) {
            var6 += var0[var10] - var8 >>> 31;
         }

         var6 = (-var9 ^ var6) + var9;
         var3[var4] = (short)var6;
      }
   }
}
