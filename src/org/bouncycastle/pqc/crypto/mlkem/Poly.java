package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.crypto.Xof;

class Poly {
   private final short[] coeffs = new short[256];

   short getCoeffIndex(int var1) {
      return this.coeffs[var1];
   }

   short[] getCoeffs() {
      return this.coeffs;
   }

   void setCoeffIndex(int var1, short var2) {
      this.coeffs[var1] = var2;
   }

   void polyNtt() {
      Ntt.ntt(this.coeffs);
      this.reduce();
   }

   void polyInverseNttToMont() {
      Ntt.invNtt(this.coeffs);
   }

   void reduce() {
      for (int var1 = 0; var1 < 256; var1++) {
         this.coeffs[var1] = Reduce.barrettReduce(this.coeffs[var1]);
      }
   }

   static void baseMultMontgomery(Poly var0, Poly var1, Poly var2) {
      for (int var3 = 0; var3 < 64; var3++) {
         Ntt.baseMult(
            var0.coeffs,
            4 * var3,
            var1.getCoeffIndex(4 * var3),
            var1.getCoeffIndex(4 * var3 + 1),
            var2.getCoeffIndex(4 * var3),
            var2.getCoeffIndex(4 * var3 + 1),
            Ntt.ZETAS[64 + var3]
         );
         Ntt.baseMult(
            var0.coeffs,
            4 * var3 + 2,
            var1.getCoeffIndex(4 * var3 + 2),
            var1.getCoeffIndex(4 * var3 + 3),
            var2.getCoeffIndex(4 * var3 + 2),
            var2.getCoeffIndex(4 * var3 + 3),
            (short)(-1 * Ntt.ZETAS[64 + var3])
         );
      }
   }

   void add(Poly var1) {
      for (int var2 = 0; var2 < 256; var2++) {
         this.coeffs[var2] = (short)(this.coeffs[var2] + var1.coeffs[var2]);
      }
   }

   void convertToMont() {
      for (int var1 = 0; var1 < 256; var1++) {
         this.setCoeffIndex(var1, Reduce.montgomeryReduce(this.getCoeffIndex(var1) * 1353));
      }
   }

   byte[] compressPoly128() {
      byte[] var1 = new byte[8];
      byte[] var2 = new byte[128];
      byte var3 = 0;
      this.condSubQ();

      for (int var4 = 0; var4 < 32; var4++) {
         for (int var5 = 0; var5 < 8; var5++) {
            int var6 = this.getCoeffIndex(8 * var4 + var5);
            var6 <<= 4;
            var6 += 1665;
            var6 *= 80635;
            var6 >>= 28;
            var6 &= 15;
            var1[var5] = (byte)var6;
         }

         var2[var3 + 0] = (byte)(var1[0] | var1[1] << 4);
         var2[var3 + 1] = (byte)(var1[2] | var1[3] << 4);
         var2[var3 + 2] = (byte)(var1[4] | var1[5] << 4);
         var2[var3 + 3] = (byte)(var1[6] | var1[7] << 4);
         var3 += 4;
      }

      return var2;
   }

   byte[] compressPoly160() {
      byte[] var1 = new byte[8];
      byte[] var2 = new byte[160];
      byte var3 = 0;
      this.condSubQ();

      for (int var4 = 0; var4 < 32; var4++) {
         for (int var5 = 0; var5 < 8; var5++) {
            int var6 = this.getCoeffIndex(8 * var4 + var5);
            var6 <<= 5;
            var6 += 1664;
            var6 *= 40318;
            var6 >>= 27;
            var6 &= 31;
            var1[var5] = (byte)var6;
         }

         var2[var3 + 0] = (byte)(var1[0] >> 0 | var1[1] << 5);
         var2[var3 + 1] = (byte)(var1[1] >> 3 | var1[2] << 2 | var1[3] << 7);
         var2[var3 + 2] = (byte)(var1[3] >> 1 | var1[4] << 4);
         var2[var3 + 3] = (byte)(var1[4] >> 4 | var1[5] << 1 | var1[6] << 6);
         var2[var3 + 4] = (byte)(var1[6] >> 2 | var1[7] << 3);
         var3 += 5;
      }

      return var2;
   }

   void decompressPoly128(byte[] var1, int var2) {
      int var3 = var2;

      for (int var4 = 0; var4 < 128; var4++) {
         this.setCoeffIndex(2 * var4 + 0, (short)((short)(var1[var3] & 0xFF & 15) * 3329 + 8 >> 4));
         this.setCoeffIndex(2 * var4 + 1, (short)((short)((var1[var3] & 255) >> 4) * 3329 + 8 >> 4));
         var3++;
      }
   }

   void decompressPoly160(byte[] var1, int var2) {
      int var3 = var2;
      byte[] var4 = new byte[8];

      for (int var5 = 0; var5 < 32; var5++) {
         var4[0] = (byte)((var1[var3 + 0] & 255) >> 0);
         var4[1] = (byte)((var1[var3 + 0] & 255) >> 5 | (var1[var3 + 1] & 255) << 3);
         var4[2] = (byte)((var1[var3 + 1] & 255) >> 2);
         var4[3] = (byte)((var1[var3 + 1] & 255) >> 7 | (var1[var3 + 2] & 255) << 1);
         var4[4] = (byte)((var1[var3 + 2] & 255) >> 4 | (var1[var3 + 3] & 255) << 4);
         var4[5] = (byte)((var1[var3 + 3] & 255) >> 1);
         var4[6] = (byte)((var1[var3 + 3] & 255) >> 6 | (var1[var3 + 4] & 255) << 2);
         var4[7] = (byte)((var1[var3 + 4] & 255) >> 3);
         var3 += 5;

         for (int var6 = 0; var6 < 8; var6++) {
            this.setCoeffIndex(8 * var5 + var6, (short)((var4[var6] & 31) * 3329 + 16 >> 5));
         }
      }
   }

   void toBytes(byte[] var1, int var2) {
      this.condSubQ();

      for (int var3 = 0; var3 < 128; var3++) {
         short var4 = this.coeffs[2 * var3 + 0];
         short var5 = this.coeffs[2 * var3 + 1];
         var1[var2 + 3 * var3 + 0] = (byte)(var4 >> 0);
         var1[var2 + 3 * var3 + 1] = (byte)(var4 >> 8 | var5 << 4);
         var1[var2 + 3 * var3 + 2] = (byte)(var5 >> 4);
      }
   }

   void fromBytes(byte[] var1, int var2) {
      for (int var3 = 0; var3 < 128; var3++) {
         int var4 = var2 + 3 * var3;
         int var5 = var1[var4 + 0] & 255;
         int var6 = var1[var4 + 1] & 255;
         int var7 = var1[var4 + 2] & 255;
         this.coeffs[2 * var3 + 0] = (short)((var5 >> 0 | var6 << 8) & 4095);
         this.coeffs[2 * var3 + 1] = (short)((var6 >> 4 | var7 << 4) & 4095);
      }
   }

   void toMsg(byte[] var1) {
      short var2 = 832;
      int var3 = 3329 - var2;
      this.condSubQ();

      for (int var4 = 0; var4 < 32; var4++) {
         var1[var4] = 0;

         for (int var5 = 0; var5 < 8; var5++) {
            short var6 = this.getCoeffIndex(8 * var4 + var5);
            int var7 = (var2 - var6 & var6 - var3) >>> 31;
            var1[var4] |= (byte)(var7 << var5);
         }
      }
   }

   void fromMsg(byte[] var1, int var2) {
      for (int var3 = 0; var3 < 32; var3++) {
         int var4 = var1[var2 + var3] & 255;

         for (int var5 = 0; var5 < 8; var5++) {
            short var6 = (short)(-(var4 >> var5 & 1));
            this.setCoeffIndex(8 * var3 + var5, (short)(var6 & 1665));
         }
      }
   }

   void condSubQ() {
      for (int var1 = 0; var1 < 256; var1++) {
         this.coeffs[var1] = Reduce.condSubQ(this.coeffs[var1]);
      }
   }

   void getNoiseEta2(Xof var1, byte[] var2, int var3, byte var4) {
      byte[] var5 = new byte[128];
      prf(var1, var2, var3, var4, var5);
      CBD.eta2(this, var5);
   }

   void getNoiseEta3(Xof var1, byte[] var2, int var3, byte var4) {
      byte[] var5 = new byte[192];
      prf(var1, var2, var3, var4, var5);
      CBD.eta3(this, var5);
   }

   private static void prf(Xof var0, byte[] var1, int var2, byte var3, byte[] var4) {
      var0.update(var1, var2, 32);
      var0.update(var3);
      var0.doFinal(var4, 0, var4.length);
   }

   void subtract(Poly var1) {
      for (int var2 = 0; var2 < 256; var2++) {
         this.coeffs[var2] = (short)(var1.coeffs[var2] - this.coeffs[var2]);
      }
   }

   static int checkModulus(byte[] var0, int var1) {
      int var2 = -1;

      for (int var3 = 0; var3 < 128; var3++) {
         int var4 = var0[var1 + 3 * var3 + 0] & 255;
         int var5 = var0[var1 + 3 * var3 + 1] & 255;
         int var6 = var0[var1 + 3 * var3 + 2] & 255;
         short var7 = (short)((var4 >> 0 | var5 << 8) & 4095);
         short var8 = (short)((var5 >> 4 | var6 << 4) & 4095);
         var2 &= Reduce.checkModulus(var7);
         var2 &= Reduce.checkModulus(var8);
      }

      return var2;
   }
}
