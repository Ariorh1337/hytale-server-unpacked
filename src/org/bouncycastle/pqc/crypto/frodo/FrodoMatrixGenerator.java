package org.bouncycastle.pqc.crypto.frodo;

import org.bouncycastle.crypto.MultiBlockCipher;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

abstract class FrodoMatrixGenerator {
   final int n;
   final int q;

   FrodoMatrixGenerator(int var1, int var2) {
      this.n = var1;
      this.q = var2;
   }

   abstract short[] genMatrix(byte[] var1, int var2, int var3);

   static class Aes128MatrixGenerator extends FrodoMatrixGenerator {
      public Aes128MatrixGenerator(int var1, int var2) {
         super(var1, var2);
      }

      @Override
      short[] genMatrix(byte[] var1, int var2, int var3) {
         short[] var4 = new short[this.n * this.n];
         byte[] var5 = new byte[16];
         byte[] var6 = new byte[16];
         MultiBlockCipher var7 = AESEngine.newInstance();
         var7.init(true, new KeyParameter(var1, var2, var3));

         for (int var8 = 0; var8 < this.n; var8++) {
            Pack.shortToLittleEndian((short)var8, var5, 0);

            for (byte var9 = 0; var9 < this.n; var9 += 8) {
               Pack.shortToLittleEndian(var9, var5, 2);
               var7.processBlock(var5, 0, var6, 0);

               for (int var10 = 0; var10 < 8; var10++) {
                  var4[var8 * this.n + var9 + var10] = (short)(Pack.littleEndianToShort(var6, 2 * var10) & this.q - 1);
               }
            }
         }

         return var4;
      }
   }

   static class Shake128MatrixGenerator extends FrodoMatrixGenerator {
      public Shake128MatrixGenerator(int var1, int var2) {
         super(var1, var2);
      }

      @Override
      short[] genMatrix(byte[] var1, int var2, int var3) {
         short[] var4 = new short[this.n * this.n];
         byte[] var5 = new byte[16 * this.n / 8];
         byte[] var6 = new byte[2 + var3];
         System.arraycopy(var1, var2, var6, 2, var3);
         SHAKEDigest var7 = new SHAKEDigest(128);

         for (int var8 = 0; var8 < this.n; var8++) {
            Pack.shortToLittleEndian((short)var8, var6, 0);
            var7.update(var6, 0, var6.length);
            var7.doFinal(var5, 0, var5.length);

            for (int var9 = 0; var9 < this.n; var9++) {
               var4[var8 * this.n + var9] = (short)(Pack.littleEndianToShort(var5, 2 * var9) & this.q - 1);
            }
         }

         return var4;
      }
   }
}
