package org.bouncycastle.pqc.crypto.mlkem;

class Reduce {
   static short montgomeryReduce(int var0) {
      short var1 = (short)(var0 * 62209);
      int var2 = var1 * 3329;
      var2 = var0 - var2;
      var2 >>= 16;
      return (short)var2;
   }

   static short barrettReduce(short var0) {
      short var1 = 20159;
      short var2 = (short)(var1 * var0 >> 26);
      var2 = (short)(var2 * 3329);
      return (short)(var0 - var2);
   }

   static short condSubQ(short var0) {
      var0 = (short)(var0 - 3329);
      return (short)(var0 + (var0 >> 15 & 3329));
   }

   static int checkModulus(short var0) {
      return var0 - 3329;
   }
}
