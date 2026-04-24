package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.util.Arrays;

class MLKEMIndCpa {
   private static final int SHAKE128_RATE = 168;
   private static final int NUM_MATRIX_BLOCKS = 3;
   private final MLKEMEngine engine;

   MLKEMIndCpa(MLKEMEngine var1) {
      this.engine = var1;
   }

   byte[][] generateKeyPair(byte[] var1) {
      int var2 = this.engine.getK();
      PolyVec var3 = new PolyVec(var2);
      PolyVec var4 = new PolyVec(var2);
      byte[] var5 = new byte[64];
      MLKEMEngine.hash_G(Arrays.append(var1, (byte)var2), var5);
      PolyVec[] var6 = new PolyVec[var2];

      for (int var7 = 0; var7 < var2; var7++) {
         var6[var7] = new PolyVec(var2);
      }

      this.generateMatrixA(var6, var5, false);
      SHAKEDigest var11 = new SHAKEDigest(256);
      byte var8 = 0;
      if (this.engine.getEta1() == 2) {
         for (int var9 = 0; var9 < var2; var9++) {
            var3.getVectorIndex(var9).getNoiseEta2(var11, var5, 32, var8++);
         }

         for (int var12 = 0; var12 < var2; var12++) {
            var4.getVectorIndex(var12).getNoiseEta2(var11, var5, 32, var8++);
         }
      } else {
         for (int var13 = 0; var13 < var2; var13++) {
            var3.getVectorIndex(var13).getNoiseEta3(var11, var5, 32, var8++);
         }

         for (int var14 = 0; var14 < var2; var14++) {
            var4.getVectorIndex(var14).getNoiseEta3(var11, var5, 32, var8++);
         }
      }

      var3.polyVecNtt();
      var4.polyVecNtt();
      PolyVec var15 = new PolyVec(var2);

      for (int var10 = 0; var10 < var2; var10++) {
         PolyVec.pointwiseAccountMontgomery(var15.getVectorIndex(var10), var6[var10], var3, this.engine);
         var15.getVectorIndex(var10).convertToMont();
      }

      var15.addPoly(var4);
      var15.reducePoly();
      return new byte[][]{this.packPublicKey(var15, var5), this.packSecretKey(var3)};
   }

   void decrypt(byte[] var1, byte[] var2, byte[] var3) {
      int var4 = this.engine.getK();
      PolyVec var5 = new PolyVec(var4);
      PolyVec var6 = new PolyVec(var4);
      Poly var7 = new Poly();
      Poly var8 = new Poly();
      this.unpackCipherText(var5, var7, var2, 0);
      this.unpackSecretKey(var6, var1);
      var5.polyVecNtt();
      PolyVec.pointwiseAccountMontgomery(var8, var6, var5, this.engine);
      var8.polyInverseNttToMont();
      var8.subtract(var7);
      var8.reduce();
      var8.toMsg(var3);
   }

   byte[] encrypt(byte[] var1, int var2, byte[] var3, int var4, byte[] var5, int var6) {
      int var7 = this.engine.getK();
      byte var8 = 0;
      PolyVec var9 = new PolyVec(var7);
      PolyVec var10 = new PolyVec(var7);
      PolyVec var11 = new PolyVec(var7);
      PolyVec var12 = new PolyVec(var7);
      Poly var13 = new Poly();
      Poly var14 = new Poly();
      Poly var15 = new Poly();
      byte[] var16 = this.unpackPublicKey(var10, var1, var2);
      var15.fromMsg(var3, var4);
      PolyVec[] var17 = new PolyVec[this.engine.getK()];

      for (int var18 = 0; var18 < var7; var18++) {
         var17[var18] = new PolyVec(var7);
      }

      this.generateMatrixA(var17, var16, true);
      SHAKEDigest var20 = new SHAKEDigest(256);
      if (this.engine.getEta1() == 2) {
         for (int var19 = 0; var19 < var7; var19++) {
            var9.getVectorIndex(var19).getNoiseEta2(var20, var5, var6, var8++);
         }
      } else {
         for (int var21 = 0; var21 < var7; var21++) {
            var9.getVectorIndex(var21).getNoiseEta3(var20, var5, var6, var8++);
         }
      }

      for (int var22 = 0; var22 < var7; var22++) {
         var11.getVectorIndex(var22).getNoiseEta2(var20, var5, var6, var8++);
      }

      var13.getNoiseEta2(var20, var5, var6, var8);
      var9.polyVecNtt();

      for (int var23 = 0; var23 < var7; var23++) {
         PolyVec.pointwiseAccountMontgomery(var12.getVectorIndex(var23), var17[var23], var9, this.engine);
      }

      PolyVec.pointwiseAccountMontgomery(var14, var10, var9, this.engine);
      var12.polyVecInverseNttToMont();
      var14.polyInverseNttToMont();
      var12.addPoly(var11);
      var14.add(var13);
      var14.add(var15);
      var12.reducePoly();
      var14.reduce();
      return this.packCipherText(var12, var14);
   }

   private byte[] packCipherText(PolyVec var1, Poly var2) {
      int var3 = this.engine.getPolyVecCompressedBytes();
      byte[] var4 = new byte[this.engine.getCipherTextBytes()];
      var1.compressPolyVec(var4, 0);
      byte[] var5;
      if (this.engine.getK() == 4) {
         var5 = var2.compressPoly160();
      } else {
         var5 = var2.compressPoly128();
      }

      System.arraycopy(var5, 0, var4, var3, this.engine.getPolyCompressedBytes());
      return var4;
   }

   private void unpackCipherText(PolyVec var1, Poly var2, byte[] var3, int var4) {
      var1.decompressPolyVec(var3, var4);
      var4 += this.engine.getPolyVecCompressedBytes();
      if (this.engine.getK() == 4) {
         var2.decompressPoly160(var3, var4);
      } else {
         var2.decompressPoly128(var3, var4);
      }
   }

   byte[] packPublicKey(PolyVec var1, byte[] var2) {
      int var3 = this.engine.getPublicKeyBytes();
      int var4 = this.engine.getPolyVecBytes();
      byte[] var5 = new byte[var3];
      var1.toBytes(var5, 0);
      System.arraycopy(var2, 0, var5, var4, 32);
      return var5;
   }

   byte[] unpackPublicKey(PolyVec var1, byte[] var2, int var3) {
      int var4 = this.engine.getPolyVecBytes();
      byte[] var5 = new byte[32];
      var1.fromBytes(var2, var3);
      System.arraycopy(var2, var3 + var4, var5, 0, 32);
      return var5;
   }

   byte[] packSecretKey(PolyVec var1) {
      byte[] var2 = new byte[this.engine.getPolyVecBytes()];
      var1.toBytes(var2, 0);
      return var2;
   }

   void unpackSecretKey(PolyVec var1, byte[] var2) {
      var1.fromBytes(var2, 0);
   }

   void generateMatrixA(PolyVec[] var1, byte[] var2, boolean var3) {
      int var4 = this.engine.getK();
      SHAKEDigest var5 = new SHAKEDigest(128);
      byte[] var6 = new byte[506];

      for (int var7 = 0; var7 < var4; var7++) {
         for (int var8 = 0; var8 < var4; var8++) {
            var5.reset();
            var5.update(var2, 0, 32);
            if (var3) {
               var5.update((byte)var7);
               var5.update((byte)var8);
            } else {
               var5.update((byte)var8);
               var5.update((byte)var7);
            }

            int var9 = 504;
            var5.doOutput(var6, 0, var9);

            for (int var10 = rejectionSampling(var1[var7].getVectorIndex(var8), 0, 256, var6, var9);
               var10 < 256;
               var10 += rejectionSampling(var1[var7].getVectorIndex(var8), var10, 256 - var10, var6, var9)
            ) {
               int var11 = var9 % 3;

               for (int var12 = 0; var12 < var11; var12++) {
                  var6[var12] = var6[var9 - var11 + var12];
               }

               var5.doOutput(var6, var11, 336);
               var9 = var11 + 168;
            }
         }
      }
   }

   private static int rejectionSampling(Poly var0, int var1, int var2, byte[] var3, int var4) {
      short var5 = 3329;
      int var6 = 0;
      byte var7 = 0;

      while (var6 < var2 && var7 + 3 <= var4) {
         short var8 = (short)(((short)(var3[var7 + 0] & 0xFF) >> 0 | (short)(var3[var7 + 1] & 0xFF) << 8) & 4095);
         short var9 = (short)(((short)(var3[var7 + 1] & 0xFF) >> 4 | (short)(var3[var7 + 2] & 0xFF) << 4) & 4095);
         var7 += 3;
         if (var8 < var5) {
            var0.setCoeffIndex(var1 + var6, var8);
            var6++;
         }

         if (var6 < var2 && var9 < var5) {
            var0.setCoeffIndex(var1 + var6, var9);
            var6++;
         }
      }

      return var6;
   }
}
