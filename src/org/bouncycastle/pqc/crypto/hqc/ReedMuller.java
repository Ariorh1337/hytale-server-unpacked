package org.bouncycastle.pqc.crypto.hqc;

class ReedMuller {
   static void encodeSub(ReedMuller.Codeword var0, int var1) {
      int var2 = Bit0Mask(var1 >> 7);
      var2 ^= Bit0Mask(var1) & -1431655766;
      var2 ^= Bit0Mask(var1 >> 1) & -858993460;
      var2 ^= Bit0Mask(var1 >> 2) & -252645136;
      var2 ^= Bit0Mask(var1 >> 3) & -16711936;
      var2 ^= Bit0Mask(var1 >> 4) & -65536;
      var0.type32[0] = var2;
      var2 ^= Bit0Mask(var1 >> 5);
      var0.type32[1] = var2;
      var2 ^= Bit0Mask(var1 >> 6);
      var0.type32[3] = var2;
      var2 ^= Bit0Mask(var1 >> 5);
      var0.type32[2] = var2;
   }

   private static void hadamardTransform(int[] var0, int[] var1) {
      for (int var2 = 0; var2 < 7; var2++) {
         for (int var3 = 0; var3 < 64; var3++) {
            int var4 = var0[2 * var3];
            int var5 = var0[2 * var3 + 1];
            var1[var3] = var4 + var5;
            var1[var3 + 64] = var4 - var5;
         }

         int[] var6 = var0;
         var0 = var1;
         var1 = var6;
      }
   }

   private static void expandThenSum(int[] var0, ReedMuller.Codeword[] var1, int var2, int var3) {
      for (int var4 = 0; var4 < 4; var4++) {
         for (int var5 = 0; var5 < 32; var5++) {
            var0[var4 * 32 + var5] = var1[var2].type32[var4] >> var5 & 1;
         }
      }

      for (int var8 = 1; var8 < var3; var8++) {
         int[] var9 = var1[var2 + var8].type32;

         for (int var6 = 0; var6 < 4; var6++) {
            for (int var7 = 0; var7 < 32; var7++) {
               var0[var6 * 32 + var7] = var0[var6 * 32 + var7] + (var9[var6] >> var7 & 1);
            }
         }
      }
   }

   private static int findPeaks(int[] var0) {
      int var1 = 0;
      byte var2 = 0;
      byte var3 = 0;

      for (int var4 = 0; var4 < 128; var4++) {
         int var5 = var0[var4];
         int var6 = var5 > 0 ? -1 : 0;
         int var7 = var6 & var5 | ~var6 & -var5;
         var2 = (byte)(var7 > var1 ? var5 : var2);
         var3 = (byte)(var7 > var1 ? var4 : var3);
         var1 = Math.max(var7, var1);
      }

      int var8 = var2 > 0 ? 1 : 0;
      return var3 | 128 * var8;
   }

   private static int Bit0Mask(int var0) {
      return -(var0 & 1);
   }

   public static void encode(long[] var0, byte[] var1, int var2, int var3) {
      ReedMuller.Codeword[] var4 = new ReedMuller.Codeword[var2 * var3];

      for (int var5 = 0; var5 < var4.length; var5++) {
         var4[var5] = new ReedMuller.Codeword();
      }

      for (int var8 = 0; var8 < var2; var8++) {
         int var6 = var8 * var3;
         encodeSub(var4[var6], var1[var8]);

         for (int var7 = 1; var7 < var3; var7++) {
            var4[var6 + var7] = var4[var6];
         }
      }

      copyCodeword(var0, var4);
   }

   public static void decode(byte[] var0, long[] var1, int var2, int var3) {
      ReedMuller.Codeword[] var4 = new ReedMuller.Codeword[var1.length / 2];
      int[] var5 = new int[var1.length * 2];
      Utils.fromLongArrayToByte32Array(var5, var1);

      for (int var6 = 0; var6 < var4.length; var6++) {
         var4[var6] = new ReedMuller.Codeword();
         System.arraycopy(var5, var6 * 4, var4[var6].type32, 0, 4);
      }

      int[] var9 = new int[128];
      int[] var7 = new int[128];

      for (int var8 = 0; var8 < var2; var8++) {
         expandThenSum(var9, var4, var8 * var3, var3);
         hadamardTransform(var9, var7);
         var7[0] -= 64 * var3;
         var0[var8] = (byte)findPeaks(var7);
      }

      copyCodeword(var1, var4);
   }

   private static void copyCodeword(long[] var0, ReedMuller.Codeword[] var1) {
      int[] var2 = new int[var1.length * 4];
      byte var3 = 0;

      for (int var4 = 0; var4 < var1.length; var4++) {
         System.arraycopy(var1[var4].type32, 0, var2, var3, 4);
         var3 += 4;
      }

      Utils.fromByte32ArrayToLongArray(var0, var2);
   }

   static class Codeword {
      int[] type32 = new int[4];
      int[] type8 = new int[16];

      public Codeword() {
      }
   }
}
