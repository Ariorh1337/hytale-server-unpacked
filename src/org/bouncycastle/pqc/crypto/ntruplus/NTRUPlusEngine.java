package org.bouncycastle.pqc.crypto.ntruplus;

import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;

class NTRUPlusEngine {
   private static final short QINV = 12929;
   private static final short OMEGA = -886;
   private static final short RINV = -682;
   private static final short RSQ = 867;
   private static final short Q = 3457;
   private static final short Q_HALF = 1728;
   private static final short QPlus1_Half = 1729;
   private static final short QMinus1_Half = 1728;
   private static final short V = 19412;
   private static final byte hash_f_domain = 0;
   private static final byte hash_g_domain = 1;
   private static final byte hash_h_domain = 2;
   static final int SSBytes = 32;
   private final int n;
   private final int halfN;
   private final int quarterN;
   private final int eighthN;
   private final int blockSize;
   private final int doubleBlockSize;
   private final int zetaOffset;
   public short polyBytes;
   public short[] zetas;
   private final NTRUPlusParameters params;
   private final SHAKEDigest shakeDigest = new SHAKEDigest(256);

   public NTRUPlusEngine(NTRUPlusParameters var1) {
      this.params = var1;
      this.n = var1.getN();
      this.halfN = this.n >> 1;
      this.quarterN = this.n >> 2;
      this.eighthN = this.n >> 3;
      this.blockSize = this.n == 864 ? 3 : 4;
      this.doubleBlockSize = this.blockSize << 1;
      this.zetaOffset = var1.getZetasOffset();
      this.polyBytes = (short)var1.getPublicKeyBytes();
      this.zetas = var1.getZetas();
   }

   public int genf_derand(short[] var1, short[] var2, byte[] var3) {
      byte[] var4 = new byte[this.quarterN];
      this.shake256(var4, 0, var4.length, var3, 32);
      this.poly_cbd1(var1, var4, 0);
      this.poly_triple(var1, var1);
      var1[0]++;
      this.poly_ntt(var1);
      return this.poly_baseinv(var2, var1);
   }

   private void poly_cbd1(short[] var1, byte[] var2, int var3) {
      int var4 = 0;

      for (byte var5 = 0; var4 < this.eighthN; var5 += 8) {
         int var6 = var2[var3 + var4] & 255;
         int var7 = var2[var3 + var4 + this.eighthN] & 255;

         for (int var8 = 0; var8 < 8; var8++) {
            var1[var5 + var8] = (short)((var6 & 1) - (var7 & 1));
            var6 >>= 1;
            var7 >>= 1;
         }

         var4++;
      }
   }

   public void poly_triple(short[] var1, short[] var2) {
      for (int var3 = 0; var3 < this.n; var3++) {
         var1[var3] = (short)(3 * var2[var3]);
      }
   }

   private void poly_ntt(short[] var1) {
      int var7 = 1;
      short var5 = this.zetas[var7++];
      int var8 = 0;

      for (int var9 = this.halfN; var8 < this.halfN; var9++) {
         short var2 = this.fqmul(var5, var1[var9]);
         var1[var9] = (short)(var1[var8] + var1[var9] - var2);
         var1[var8] += var2;
         var8++;
      }

      var8 = this.params.getBaseStep();
      int var24 = this.params.getMinStep();

      for (int var10 = this.n / 6; var10 >= var8 << 1; var10 /= 3) {
         int var11 = var10 << 1;
         int var12 = var11 + var10;

         for (int var13 = 0; var13 < this.n; var13 += var12) {
            var5 = this.zetas[var7++];
            short var6 = this.zetas[var7++];
            int var14 = var13;
            int var15 = var13 + var10;

            for (int var16 = var13 + var11; var14 < var13 + var10; var16++) {
               short var17 = this.fqmul(var5, var1[var15]);
               short var3 = this.fqmul(var6, var1[var16]);
               short var4 = this.fqmul((short)-886, (short)(var17 - var3));
               var1[var16] = (short)(var1[var14] - var17 - var4);
               var1[var15] = (short)(var1[var14] - var3 + var4);
               var1[var14] = (short)(var1[var14] + var17 + var3);
               var14++;
               var15++;
            }
         }
      }

      for (int var25 = var8; var25 >= var24; var25 >>= 1) {
         for (int var26 = 0; var26 < this.n; var26 += var25 << 1) {
            var5 = this.zetas[var7++];
            int var27 = var26;

            for (int var28 = var26 + var25; var27 < var26 + var25; var28++) {
               short var18 = this.fqmul(var5, var1[var28]);
               var1[var28] = this.barrett_reduce((short)(var1[var27] - var18));
               var1[var27] = this.barrett_reduce((short)(var1[var27] + var18));
               var27++;
            }
         }
      }
   }

   public short fqmul(short var1, short var2) {
      return this.montgomery_reduce(var1 * var2);
   }

   public short montgomery_reduce(int var1) {
      return (short)(var1 - (short)(var1 * 12929) * 3457 >> 16);
   }

   public short barrett_reduce(short var1) {
      return (short)(var1 - (19412 * var1 + 33554432 >> 26) * 3457);
   }

   private int poly_baseinv(short[] var1, short[] var2) {
      if (this.n == 864) {
         int var3 = 0;
         byte var4 = 0;

         for (int var5 = this.zetaOffset; var3 < this.n / 6; var5++) {
            if (this.baseinv3(var1, var4, var2, var4, this.zetas[var5]) == 1) {
               Arrays.fill(var1, (short)0);
               return 1;
            }

            if (this.baseinv3(var1, var4 + 3, var2, var4 + 3, (short)(-this.zetas[var5])) == 1) {
               Arrays.fill(var1, (short)0);
               return 1;
            }

            var3++;
            var4 += 6;
         }
      } else {
         int var6 = 0;
         byte var7 = 0;

         for (int var8 = this.zetaOffset; var6 < this.eighthN; var8++) {
            if (this.baseinv(var1, var7, var2, var7, this.zetas[var8]) == 1) {
               Arrays.fill(var1, (short)0);
               return 1;
            }

            if (this.baseinv(var1, var7 + 4, var2, var7 + 4, (short)(-this.zetas[var8])) == 1) {
               Arrays.fill(var1, (short)0);
               return 1;
            }

            var6++;
            var7 += 8;
         }
      }

      return 0;
   }

   private int baseinv3(short[] var1, int var2, short[] var3, int var4, short var5) {
      short var6 = var3[var4];
      short var7 = var3[var4 + 1];
      short var8 = var3[var4 + 2];
      short var9 = this.montgomery_reduce(var7 * var8);
      short var10 = this.montgomery_reduce(var8 * var8);
      short var11 = this.montgomery_reduce(var7 * var7 - var6 * var8);
      var9 = this.montgomery_reduce(var6 * var6 - var9 * var5);
      var10 = this.montgomery_reduce(var10 * var5 - var6 * var7);
      short var12 = this.montgomery_reduce(var11 * var7 + var10 * var8);
      var12 = this.montgomery_reduce(var12 * var5 + var9 * var6);
      if (var12 == 0) {
         return 1;
      }

      var12 = this.fqinv(var12);
      var12 = this.montgomery_reduce(var12 * -682);
      var1[var2] = this.montgomery_reduce(var9 * var12);
      var1[var2 + 1] = this.montgomery_reduce(var10 * var12);
      var1[var2 + 2] = this.montgomery_reduce(var11 * var12);
      return 0;
   }

   public int baseinv(short[] var1, int var2, short[] var3, int var4, short var5) {
      short var6 = var3[var4];
      short var7 = var3[var4 + 1];
      short var8 = var3[var4 + 2];
      short var9 = var3[var4 + 3];
      short var10 = this.montgomery_reduce(var8 * var8 - 2 * var7 * var9);
      short var11 = this.montgomery_reduce(var9 * var9);
      var10 = this.montgomery_reduce(var6 * var6 + var10 * var5);
      var11 = this.montgomery_reduce(var7 * var7 + var11 * var5 - 2 * var6 * var8);
      short var12 = this.montgomery_reduce(var11 * var5);
      short var13 = this.montgomery_reduce(var10 * var10 - var11 * var12);
      if (var13 == 0) {
         return 1;
      }

      short var14 = this.montgomery_reduce(var6 * var10 + var8 * var12);
      short var15 = this.montgomery_reduce(var9 * var12 + var7 * var10);
      short var16 = this.montgomery_reduce(var8 * var10 + var6 * var11);
      short var17 = this.montgomery_reduce(var7 * var11 + var9 * var10);
      var13 = this.fqinv(var13);
      var13 = this.montgomery_reduce(var13 * -682);
      var1[var2] = this.montgomery_reduce(var14 * var13);
      var1[var2 + 1] = (short)(-this.montgomery_reduce(var15 * var13));
      var1[var2 + 2] = this.montgomery_reduce(var16 * var13);
      var1[var2 + 3] = (short)(-this.montgomery_reduce(var17 * var13));
      return 0;
   }

   public void shake256(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      this.shakeDigest.update(var4, 0, var5);
      this.shakeDigest.doFinal(var1, var2, var3);
   }

   public short fqinv(short var1) {
      short var2 = this.fqmul(var1, var1);
      short var3 = this.fqmul(var2, var2);
      var3 = this.fqmul(var3, var3);
      short var4 = this.fqmul(var3, var3);
      var2 = this.fqmul(var2, var3);
      var3 = this.fqmul(var2, var4);
      var3 = this.fqmul(var3, var3);
      var3 = this.fqmul(var3, var1);
      var2 = this.fqmul(var2, var3);
      var3 = this.fqmul(var3, var3);
      var3 = this.fqmul(var3, var3);
      var3 = this.fqmul(var3, var3);
      var3 = this.fqmul(var3, var3);
      var3 = this.fqmul(var3, var3);
      var3 = this.fqmul(var3, var3);
      return this.fqmul(var3, var2);
   }

   private void poly_basemul(short[] var1, short[] var2, short[] var3) {
      for (int var4 = 0; var4 < this.n / this.doubleBlockSize; var4++) {
         this.basemul(
            var1, this.doubleBlockSize * var4, var2, this.doubleBlockSize * var4, var3, this.doubleBlockSize * var4, this.zetas[this.zetaOffset + var4]
         );
         this.basemul(
            var1,
            this.doubleBlockSize * var4 + this.blockSize,
            var2,
            this.doubleBlockSize * var4 + this.blockSize,
            var3,
            this.doubleBlockSize * var4 + this.blockSize,
            (short)(-this.zetas[this.zetaOffset + var4])
         );
      }
   }

   public void poly_tobytes(byte[] var1, int var2, short[] var3) {
      int var6 = 0;
      int var7 = 0;
      int var8 = var2;

      while (var6 < this.halfN) {
         int var4 = var3[var7++];
         var4 += var4 >> 15 & 3457;
         int var5 = var3[var7++];
         var5 += var5 >> 15 & 3457;
         var1[var8++] = (byte)var4;
         var1[var8++] = (byte)(var4 >> 8 | var5 << 4);
         var1[var8++] = (byte)(var5 >> 4);
         var6++;
      }
   }

   public int geng_derand(short[] var1, short[] var2, byte[] var3) {
      byte[] var4 = new byte[this.quarterN];
      this.shake256(var4, 0, var4.length, var3, 32);
      this.poly_cbd1(var1, var4, 0);
      this.poly_triple(var1, var1);
      this.poly_ntt(var1);
      return this.poly_baseinv(var2, var1);
   }

   public void crypto_kem_keypair_derand(byte[] var1, byte[] var2, short[] var3, short[] var4, short[] var5, short[] var6) {
      short[] var7 = new short[this.n];
      short[] var8 = new short[this.n];
      this.poly_basemul(var7, var5, var4);
      this.poly_basemul(var8, var3, var6);
      this.poly_tobytes(var1, 0, var7);
      this.poly_tobytes(var2, 0, var3);
      this.poly_tobytes(var2, this.polyBytes, var8);
      this.shake256(var2, this.polyBytes << 1, 32, (byte)0, var1, 0, this.polyBytes);
   }

   private void poly_sotp_encode(short[] var1, byte[] var2, byte[] var3) {
      Bytes.xorTo(this.eighthN, var2, var3);
      this.poly_cbd1(var1, var3, 0);
   }

   private void poly_frombytes(short[] var1, byte[] var2, int var3) {
      int var4 = 0;
      int var5 = var3;
      int var6 = 0;

      while (var4 < this.halfN) {
         var1[var6++] = (short)((var2[var5] & 0xFF | (var2[var5 + 1] & 255) << 8) & 4095);
         var1[var6++] = (short)(((var2[var5 + 1] & 255) >> 4 | (var2[var5 + 2] & 255) << 4) & 4095);
         var4++;
         var5 += 3;
      }
   }

   private void poly_basemul_add(short[] var1, short[] var2, short[] var3, short[] var4) {
      int var5 = 0;
      int var6 = 0;

      for (int var7 = this.zetaOffset; var5 < this.n / this.doubleBlockSize; var7++) {
         this.basemul_add(var1, var6, var2, var6, var3, var6, var4, var6, this.zetas[var7], this.blockSize);
         var6 += this.blockSize;
         this.basemul_add(var1, var6, var2, var6, var3, var6, var4, var6, (short)(-this.zetas[var7]), this.blockSize);
         var6 += this.blockSize;
         var5++;
      }
   }

   private void basemul_add(short[] var1, int var2, short[] var3, int var4, short[] var5, int var6, short[] var7, int var8, short var9, int var10) {
      this.multiplyCore(var1, var2, var3, var4, var5, var6, var9, var10);
      this.finalizeWithAddition(var1, var2, var7, var8, var10);
   }

   private void basemul(short[] var1, int var2, short[] var3, int var4, short[] var5, int var6, short var7) {
      this.multiplyCore(var1, var2, var3, var4, var5, var6, var7, this.blockSize);
      this.finalizeMultiplication(var1, var2, this.blockSize);
   }

   private void multiplyCore(short[] var1, int var2, short[] var3, int var4, short[] var5, int var6, short var7, int var8) {
      short var9 = var3[var4];
      short var10 = var3[var4 + 1];
      short var11 = var3[var4 + 2];
      short var12 = var5[var6];
      short var13 = var5[var6 + 1];
      short var14 = var5[var6 + 2];
      if (var8 == 4) {
         short var16 = var3[var4 + 3];
         short var17 = var5[var6 + 3];
         int var15 = var10 * var17 + var11 * var14 + var16 * var13;
         var1[var2] = this.montgomery_reduce(var15);
         var15 = var11 * var17 + var16 * var14;
         var1[var2 + 1] = this.montgomery_reduce(var15);
         var15 = var16 * var17;
         int var20 = this.montgomery_reduce(var15);
         var20 = var20 * var7 + var9 * var14 + var10 * var13 + var11 * var12;
         var1[var2 + 2] = this.montgomery_reduce(var20);
         var20 = var9 * var17 + var10 * var14 + var11 * var13 + var16 * var12;
         var1[var2 + 3] = this.montgomery_reduce(var20);
      } else {
         int var23 = var11 * var13 + var10 * var14;
         var1[var2] = this.montgomery_reduce(var23);
         var23 = var11 * var14;
         var1[var2 + 1] = this.montgomery_reduce(var23);
         var23 = var11 * var12 + var10 * var13 + var9 * var14;
         var1[var2 + 2] = this.montgomery_reduce(var23);
      }

      int var26 = var1[var2] * var7 + var9 * var12;
      var1[var2] = this.montgomery_reduce(var26);
      var26 = var1[var2 + 1] * var7 + var9 * var13 + var10 * var12;
      var1[var2 + 1] = this.montgomery_reduce(var26);
   }

   private void finalizeWithAddition(short[] var1, int var2, short[] var3, int var4, int var5) {
      int var6 = 65536;

      for (int var7 = 0; var7 < var5; var7++) {
         int var8 = var3[var4++] * var6 + var1[var2] * 867;
         var1[var2++] = this.montgomery_reduce(var8);
      }
   }

   private void finalizeMultiplication(short[] var1, int var2, int var3) {
      for (int var4 = 0; var4 < var3; var4++) {
         var1[var2] = this.montgomery_reduce(var1[var2++] * 867);
      }
   }

   public void crypto_kem_enc_derand(byte[] var1, int var2, byte[] var3, int var4, byte[] var5, int var6, byte[] var7, int var8) {
      byte[] var9 = new byte[this.eighthN + 32];
      byte[] var10 = new byte[32 + this.quarterN];
      byte[] var11 = new byte[this.polyBytes];
      short[] var12 = new short[this.n];
      short[] var13 = new short[this.n];
      short[] var14 = new short[this.n];
      short[] var15 = new short[this.n];
      System.arraycopy(var7, var8, var9, 0, this.eighthN);
      this.shake256(var9, this.eighthN, 32, (byte)0, var5, var6, this.polyBytes);
      this.shake256(var10, 0, var10.length, (byte)2, var9, 0, var9.length);
      this.poly_cbd1(var14, var10, 32);
      this.poly_ntt(var14);
      this.poly_tobytes(var11, 0, var14);
      this.shake256(var11, 0, this.quarterN, (byte)1, var11, 0, this.polyBytes);
      this.poly_sotp_encode(var15, var9, var11);
      this.poly_ntt(var15);
      this.poly_frombytes(var13, var5, var6);
      this.poly_basemul_add(var12, var13, var14, var15);
      this.poly_tobytes(var1, var2, var12);
      System.arraycopy(var10, 0, var3, var4, 32);
   }

   private void shake256(byte[] var1, int var2, int var3, byte var4, byte[] var5, int var6, int var7) {
      this.shakeDigest.update(var4);
      this.shakeDigest.update(var5, var6, var7);
      this.shakeDigest.doFinal(var1, var2, var3);
   }

   private void poly_invntt(short[] var1) {
      short var7;
      short var8;
      int var9;
      if (this.n == 768) {
         var7 = -811;
         var8 = -1622;
         var9 = 191;
      } else {
         var7 = -1693;
         var8 = 71;
         var9 = 287;
      }

      int var10 = this.params.getMinStep();

      int var11;
      for (var11 = this.params.getBaseStep(); var10 <= var11; var10 <<= 1) {
         for (int var12 = 0; var12 < this.n; var12 += var10 << 1) {
            short var5 = this.zetas[var9--];
            int var13 = var12;

            for (int var14 = var12 + var10; var13 < var12 + var10; var14++) {
               short var2 = var1[var14];
               var1[var14] = this.fqmul(var5, (short)(var2 - var1[var13]));
               var1[var13] = this.barrett_reduce((short)(var1[var13] + var2));
               var13++;
            }
         }
      }

      for (int var23 = var11 << 1; var23 <= this.n / 6; var23 *= 3) {
         int var25 = var23 << 1;

         for (int var26 = 0; var26 < this.n; var26 += 3 * var23) {
            short var6 = this.zetas[var9--];
            short var21 = this.zetas[var9--];
            int var15 = var26;
            int var16 = var26 + var23;

            for (int var17 = var26 + var25; var15 < var26 + var23; var17++) {
               short var18 = this.fqmul((short)-886, (short)(var1[var16] - var1[var15]));
               short var3 = this.fqmul(var21, (short)(var1[var17] - var1[var15] + var18));
               short var4 = this.fqmul(var6, (short)(var1[var17] - var1[var16] - var18));
               var1[var15] = this.barrett_reduce((short)(var1[var15] + var1[var16] + var1[var17]));
               var1[var16] = var3;
               var1[var17] = var4;
               var15++;
               var16++;
            }
         }
      }

      for (int var24 = 0; var24 < this.halfN; var24++) {
         short var19 = (short)(var1[var24] + var1[var24 + this.halfN]);
         short var20 = this.fqmul((short)-1665, (short)(var1[var24] - var1[var24 + this.halfN]));
         var1[var24] = this.fqmul(var7, (short)(var19 - var20));
         var1[var24 + this.halfN] = this.fqmul(var8, var20);
      }
   }

   private void poly_crepmod3(short[] var1, short[] var2) {
      for (int var3 = 0; var3 < this.n; var3++) {
         var1[var3] = this.crepmod3(var2[var3]);
      }
   }

   private short crepmod3(short var1) {
      var1 = (short)(var1 + (short)((var1 >> 15 & 3457) - 1729));
      var1 = (short)(var1 + (short)((var1 >> 15 & 3457) - 1728));
      short var2 = (short)(10923 * var1 + 16384 >> 15);
      var2 = (short)(var2 * 3);
      return (short)(var1 - var2);
   }

   private void poly_sub(short[] var1, short[] var2, short[] var3) {
      for (int var4 = 0; var4 < this.n; var4++) {
         var1[var4] = (short)(var2[var4] - var3[var4]);
      }
   }

   private int poly_sotp_decode(byte[] var1, short[] var2, byte[] var3) {
      int var4 = 0;

      for (int var6 = 0; var6 < this.eighthN; var6++) {
         int var7 = var3[var6] & 255;
         int var8 = var3[var6 + this.eighthN] & 255;
         byte var9 = 0;

         for (int var10 = 0; var10 < 8; var10++) {
            int var11 = var8 & 1;
            var11 += var2[8 * var6 + var10];
            var4 |= var11;
            var11 = (var11 ^ var7) & 1;
            var9 ^= (byte)(var11 << var10);
            var7 >>= 1;
            var8 >>= 1;
         }

         var1[var6] = var9;
      }

      var4 >>= 1;
      var4 = -var4 >> 31;
      byte var5 = (byte)(var4 - 1);

      for (int var14 = 0; var14 < this.eighthN; var14++) {
         var1[var14] &= var5;
      }

      return var4;
   }

   private int verify(byte[] var1, byte[] var2, int var3) {
      int var4 = 0;

      for (int var5 = 0; var5 < var3; var5++) {
         var4 |= (var1[var5] ^ var2[var5]) & 0xFF;
      }

      return var4 != 0 ? 1 : 0;
   }

   public void crypto_kem_dec(byte[] var1, int var2, byte[] var3, int var4, byte[] var5, int var6) {
      byte[] var7 = new byte[this.eighthN + 32];
      byte[] var8 = new byte[this.polyBytes];
      byte[] var9 = new byte[this.polyBytes];
      byte[] var10 = new byte[this.polyBytes + 32];
      short[] var12 = new short[this.n];
      short[] var13 = new short[this.n];
      short[] var14 = new short[this.n];
      short[] var15 = new short[this.n];
      short[] var16 = new short[this.n];
      short[] var17 = new short[this.n];
      short[] var18 = new short[this.n];
      this.poly_frombytes(var12, var3, var4);
      this.poly_frombytes(var13, var5, var6);
      this.poly_frombytes(var14, var5, var6 + this.polyBytes);
      this.poly_basemul(var17, var12, var13);
      this.poly_invntt(var17);
      this.poly_crepmod3(var17, var17);
      System.arraycopy(var17, 0, var18, 0, this.n);
      this.poly_ntt(var18);
      this.poly_sub(var12, var12, var18);
      this.poly_basemul(var16, var12, var14);
      this.poly_tobytes(var8, 0, var16);
      this.shake256(var9, 0, this.quarterN, (byte)1, var8, 0, this.polyBytes);
      int var11 = this.poly_sotp_decode(var7, var17, var9);
      System.arraycopy(var5, var6 + 2 * this.polyBytes, var7, this.eighthN, 32);
      this.shake256(var10, 0, var10.length, (byte)2, var7, 0, var7.length);
      this.poly_cbd1(var15, var10, 32);
      this.poly_ntt(var15);
      this.poly_tobytes(var9, 0, var15);
      var11 |= this.verify(var8, var9, this.polyBytes);
      cmov(var1, var10, var2, 32, var11);
   }

   static void cmov(byte[] var0, byte[] var1, int var2, int var3, int var4) {
      var4 = var4 - 1 & 0xFF;

      for (int var5 = 0; var5 < var3; var5++) {
         var0[var5] = (byte)(var0[var5] ^ var4 & (var1[var5 + var2] ^ var0[var5]));
      }
   }
}
