package org.bouncycastle.pqc.crypto.mlkem;

class PolyVec {
   final Poly[] vec;

   PolyVec(int var1) {
      this.vec = new Poly[var1];

      for (int var2 = 0; var2 < var1; var2++) {
         this.vec[var2] = new Poly();
      }
   }

   Poly getVectorIndex(int var1) {
      return this.vec[var1];
   }

   void polyVecNtt() {
      for (int var1 = 0; var1 < this.vec.length; var1++) {
         this.vec[var1].polyNtt();
      }
   }

   void polyVecInverseNttToMont() {
      for (int var1 = 0; var1 < this.vec.length; var1++) {
         this.vec[var1].polyInverseNttToMont();
      }
   }

   void compressPolyVec(byte[] var1, int var2) {
      int var3 = var2;
      this.condSubQ();
      if (this.vec.length == 4) {
         short[] var4 = new short[8];

         for (int var5 = 0; var5 < this.vec.length; var5++) {
            for (int var6 = 0; var6 < 32; var6++) {
               for (int var7 = 0; var7 < 8; var7++) {
                  long var8 = this.vec[var5].getCoeffIndex(8 * var6 + var7);
                  var8 <<= 11;
                  var8 += 1664L;
                  var8 *= 645084L;
                  var8 >>= 31;
                  var8 &= 2047L;
                  var4[var7] = (short)var8;
               }

               var1[var3 + 0] = (byte)(var4[0] >> 0);
               var1[var3 + 1] = (byte)(var4[0] >> 8 | var4[1] << 3);
               var1[var3 + 2] = (byte)(var4[1] >> 5 | var4[2] << 6);
               var1[var3 + 3] = (byte)(var4[2] >> 2);
               var1[var3 + 4] = (byte)(var4[2] >> 10 | var4[3] << 1);
               var1[var3 + 5] = (byte)(var4[3] >> 7 | var4[4] << 4);
               var1[var3 + 6] = (byte)(var4[4] >> 4 | var4[5] << 7);
               var1[var3 + 7] = (byte)(var4[5] >> 1);
               var1[var3 + 8] = (byte)(var4[5] >> 9 | var4[6] << 2);
               var1[var3 + 9] = (byte)(var4[6] >> 6 | var4[7] << 5);
               var1[var3 + 10] = (byte)(var4[7] >> 3);
               var3 += 11;
            }
         }
      } else {
         short[] var10 = new short[4];

         for (int var11 = 0; var11 < this.vec.length; var11++) {
            for (int var12 = 0; var12 < 64; var12++) {
               for (int var13 = 0; var13 < 4; var13++) {
                  long var19 = this.vec[var11].getCoeffIndex(4 * var12 + var13);
                  var19 <<= 10;
                  var19 += 1665L;
                  var19 *= 1290167L;
                  var19 >>= 32;
                  var19 &= 1023L;
                  var10[var13] = (short)var19;
               }

               var1[var3 + 0] = (byte)(var10[0] >> 0);
               var1[var3 + 1] = (byte)(var10[0] >> 8 | var10[1] << 2);
               var1[var3 + 2] = (byte)(var10[1] >> 6 | var10[2] << 4);
               var1[var3 + 3] = (byte)(var10[2] >> 4 | var10[3] << 6);
               var1[var3 + 4] = (byte)(var10[3] >> 2);
               var3 += 5;
            }
         }
      }
   }

   void decompressPolyVec(byte[] var1, int var2) {
      int var3 = var2;
      if (this.vec.length == 4) {
         short[] var4 = new short[8];

         for (int var5 = 0; var5 < this.vec.length; var5++) {
            for (int var6 = 0; var6 < 32; var6++) {
               var4[0] = (short)((var1[var3] & 255) >> 0 | (short)(var1[var3 + 1] & 0xFF) << 8);
               var4[1] = (short)((var1[var3 + 1] & 255) >> 3 | (short)(var1[var3 + 2] & 0xFF) << 5);
               var4[2] = (short)((var1[var3 + 2] & 255) >> 6 | (short)(var1[var3 + 3] & 0xFF) << 2 | (short)((var1[var3 + 4] & 255) << 10));
               var4[3] = (short)((var1[var3 + 4] & 255) >> 1 | (short)(var1[var3 + 5] & 0xFF) << 7);
               var4[4] = (short)((var1[var3 + 5] & 255) >> 4 | (short)(var1[var3 + 6] & 0xFF) << 4);
               var4[5] = (short)((var1[var3 + 6] & 255) >> 7 | (short)(var1[var3 + 7] & 0xFF) << 1 | (short)((var1[var3 + 8] & 255) << 9));
               var4[6] = (short)((var1[var3 + 8] & 255) >> 2 | (short)(var1[var3 + 9] & 0xFF) << 6);
               var4[7] = (short)((var1[var3 + 9] & 255) >> 5 | (short)(var1[var3 + 10] & 0xFF) << 3);
               var3 += 11;

               for (int var7 = 0; var7 < 8; var7++) {
                  this.vec[var5].setCoeffIndex(8 * var6 + var7, (short)((var4[var7] & 2047) * 3329 + 1024 >> 11));
               }
            }
         }
      } else {
         short[] var8 = new short[4];

         for (int var9 = 0; var9 < this.vec.length; var9++) {
            for (int var10 = 0; var10 < 64; var10++) {
               var8[0] = (short)((var1[var3] & 255) >> 0 | (short)((var1[var3 + 1] & 255) << 8));
               var8[1] = (short)((var1[var3 + 1] & 255) >> 2 | (short)((var1[var3 + 2] & 255) << 6));
               var8[2] = (short)((var1[var3 + 2] & 255) >> 4 | (short)((var1[var3 + 3] & 255) << 4));
               var8[3] = (short)((var1[var3 + 3] & 255) >> 6 | (short)((var1[var3 + 4] & 255) << 2));
               var3 += 5;

               for (int var11 = 0; var11 < 4; var11++) {
                  this.vec[var9].setCoeffIndex(4 * var10 + var11, (short)((var8[var11] & 1023) * 3329 + 512 >> 10));
               }
            }
         }
      }
   }

   static void pointwiseAccountMontgomery(Poly var0, PolyVec var1, PolyVec var2, MLKEMEngine var3) {
      Poly var4 = new Poly();
      Poly.baseMultMontgomery(var0, var1.vec[0], var2.vec[0]);

      for (int var5 = 1; var5 < var3.getK(); var5++) {
         Poly.baseMultMontgomery(var4, var1.vec[var5], var2.vec[var5]);
         var0.add(var4);
      }

      var0.reduce();
   }

   void reducePoly() {
      for (int var1 = 0; var1 < this.vec.length; var1++) {
         this.vec[var1].reduce();
      }
   }

   void addPoly(PolyVec var1) {
      for (int var2 = 0; var2 < this.vec.length; var2++) {
         this.vec[var2].add(var1.vec[var2]);
      }
   }

   void toBytes(byte[] var1, int var2) {
      for (int var3 = 0; var3 < this.vec.length; var3++) {
         this.vec[var3].toBytes(var1, var2 + var3 * 384);
      }
   }

   void fromBytes(byte[] var1, int var2) {
      for (int var3 = 0; var3 < this.vec.length; var3++) {
         this.vec[var3].fromBytes(var1, var2 + var3 * 384);
      }
   }

   private void condSubQ() {
      for (int var1 = 0; var1 < this.vec.length; var1++) {
         this.vec[var1].condSubQ();
      }
   }

   static int checkModulus(MLKEMEngine var0, byte[] var1) {
      int var2 = -1;
      int var3 = 0;

      for (int var4 = var0.getK(); var3 < var4; var3++) {
         var2 &= Poly.checkModulus(var1, var3 * 384);
      }

      return var2;
   }
}
