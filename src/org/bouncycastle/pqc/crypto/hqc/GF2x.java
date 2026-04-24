package org.bouncycastle.pqc.crypto.hqc;

import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

class GF2x {
   private final int bits;
   private final int size;
   private final int sizeExt;

   GF2x(int var1) {
      if ((var1 & -65535) != 1) {
         throw new IllegalArgumentException();
      }

      this.bits = var1;
      this.size = Utils.getByte64SizeFromBitSize(var1);
      this.sizeExt = this.size * 2;
   }

   void addTo(long[] var1, long[] var2) {
      Nat.xorTo64(this.size, var1, var2);
   }

   void clear(long[] var1) {
      Nat.zero64(this.size, var1);
   }

   long[] create() {
      return new long[this.size];
   }

   long[] createExt() {
      return new long[this.sizeExt];
   }

   long equalTo(long[] var1, long[] var2) {
      return Nat.equalTo64(this.size, var1, var2);
   }

   void mul(long[] var1, long[] var2, long[] var3) {
      long[] var4 = this.createExt();
      long[] var5 = new long[this.size << 4];
      this.karatsuba(this.size, var1, 0, var2, 0, var4, 0, var5, 0);
      this.reduce(var4, var3);
   }

   void random(Shake256RandomGenerator var1, long[] var2) {
      byte[] var3 = new byte[this.size << 3];
      var1.xofGetBytes(var3, Utils.getByteSizeFromBitSize(this.bits));
      Pack.littleEndianToLong(var3, 0, var2);
      var2[this.size - 1] = var2[this.size - 1] & (1L << (this.bits & 63)) - 1L;
   }

   private static void baseMul(int var0, long[] var1, int var2, long[] var3, int var4, long[] var5, int var6) {
      int var7 = var0 * 2;
      Arrays.fill(var5, var6, var6 + var7, 0L);
      long[] var8 = new long[16];

      for (int var9 = 0; var9 < var0; var9++) {
         implMulwAcc(var8, var1[var2 + var9], var3[var4 + var9], var5, var6 + (var9 << 1));
      }

      long var17 = var5[var6];
      long var11 = var5[var6 + 1];

      for (int var13 = 1; var13 < var0; var13++) {
         var17 ^= var5[var6 + (var13 << 1)];
         var5[var6 + var13] = var17 ^ var11;
         var11 ^= var5[var6 + (var13 << 1) + 1];
      }

      Nat.xor64(var0, var5, var6, var17 ^ var11, var5, var6 + var0);
      int var18 = var0 - 1;

      for (int var14 = 1; var14 < var18 * 2; var14++) {
         int var15 = Math.min(var18, var14);

         for (int var16 = var14 - var15; var16 < var15; var15--) {
            implMulwAcc(var8, var1[var2 + var16] ^ var1[var2 + var15], var3[var4 + var16] ^ var3[var4 + var15], var5, var6 + var14);
            var16++;
         }
      }
   }

   private void karatsuba(int var1, long[] var2, int var3, long[] var4, int var5, long[] var6, int var7, long[] var8, int var9) {
      byte var10 = 12;
      if (var1 < var10) {
         baseMul(var1, var2, var3, var4, var5, var6, var7);
      } else {
         int var11 = var1 >> 1;
         int var12 = var1 - var11;
         int var13 = var1 << 1;
         int var14 = var11 << 1;
         int var15 = var12 << 1;
         int var16 = var9 + var13;
         int var17 = var16 + var13;
         int var18 = var17 + var13;
         int var19 = var18 + var1;
         int var20 = var9 + (var1 << 3);
         this.karatsuba(var11, var2, var3, var4, var5, var8, var9, var8, var20);
         this.karatsuba(var12, var2, var3 + var11, var4, var5 + var11, var8, var16, var8, var20);

         for (int var21 = 0; var21 < var12; var21++) {
            long var22 = var21 < var11 ? var2[var3 + var21] : 0L;
            long var24 = var21 < var11 ? var4[var5 + var21] : 0L;
            var8[var18 + var21] = var22 ^ var2[var3 + var11 + var21];
            var8[var19 + var21] = var24 ^ var4[var5 + var11 + var21];
         }

         this.karatsuba(var12, var8, var18, var8, var19, var8, var17, var8, var20);
         System.arraycopy(var8, var9, var6, var7, var14);
         System.arraycopy(var8, var16, var6, var7 + var14, var15);

         for (int var26 = 0; var26 < 2 * var12; var26++) {
            long var27 = var26 < var14 ? var8[var9 + var26] : 0L;
            long var28 = var26 < var15 ? var8[var16 + var26] : 0L;
            var6[var7 + var11 + var26] = var6[var7 + var11 + var26] ^ var8[var17 + var26] ^ var27 ^ var28;
         }
      }
   }

   private void reduce(long[] var1, long[] var2) {
      int var3 = this.bits & 63;
      int var4 = 64 - var3;
      long var5 = -1L >>> var4;
      Nat.shiftUpBits64(this.size, var1, this.size, var4, var1[this.size - 1], var2, 0);
      this.addTo(var1, var2);
      var2[this.size - 1] = var2[this.size - 1] & var5;
   }

   private static void implMulwAcc(long[] var0, long var1, long var3, long[] var5, int var6) {
      long var7 = 0L;
      long var9 = var1;
      long var11 = var3;
      var0[1] = var3;

      for (byte var13 = 2; var13 < 16; var13 += 2) {
         var0[var13] = var0[var13 >>> 1] << 1;
         var0[var13 + 1] = var0[var13] ^ var3;
         var9 = (var9 & -72340172838076674L) >>> 1;
         var7 ^= var9 & var11 >> 63;
         var11 <<= 1;
      }

      int var19 = (int)var1;
      long var16 = var0[var19 & 15] ^ var0[var19 >>> 4 & 15] << 4;
      byte var18 = 56;

      do {
         var19 = (int)(var1 >>> var18);
         long var14 = var0[var19 & 15] ^ var0[var19 >>> 4 & 15] << 4;
         var16 ^= var14 << var18;
         var7 ^= var14 >>> -var18;
         var18 -= 8;
      } while (var18 > 0);

      var5[var6] ^= var16;
      var5[var6 + 1] = var5[var6 + 1] ^ var7;
   }
}
