package org.bouncycastle.pqc.crypto.frodo;

import java.security.SecureRandom;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;
import org.bouncycastle.util.Pack;

class FrodoEngine {
   static final int nbar = 8;
   private static final int mbar = 8;
   private static final int len_seedA = 128;
   private static final int len_z = 128;
   private static final int len_chi = 16;
   private static final int len_seedA_bytes = 16;
   private static final int len_z_bytes = 16;
   private static final int len_chi_bytes = 2;
   private final int D;
   private final int q;
   private final int n;
   private final int B;
   private final int len_sk_bytes;
   private final int len_pk_bytes;
   private final int len_ct_bytes;
   private final short[] T_chi;
   private final int len_mu;
   private final int len_seedSE;
   private final int len_s;
   private final int len_k;
   private final int len_pkh;
   private final int len_ss;
   private final int len_mu_bytes;
   private final int len_seedSE_bytes;
   private final int len_s_bytes;
   private final int len_k_bytes;
   private final int len_pkh_bytes;
   private final int len_ss_bytes;
   private final Xof digest;
   private final FrodoMatrixGenerator gen;

   public int getCipherTextSize() {
      return this.len_ct_bytes;
   }

   public int getSessionKeySize() {
      return this.len_ss_bytes;
   }

   public int getPrivateKeySize() {
      return this.len_sk_bytes;
   }

   public int getPublicKeySize() {
      return this.len_pk_bytes;
   }

   public FrodoEngine(int var1, int var2, int var3, short[] var4, Xof var5, FrodoMatrixGenerator var6) {
      this.n = var1;
      this.D = var2;
      this.q = 1 << var2;
      this.B = var3;
      this.len_mu = var3 * 8 * 8;
      this.len_seedSE = this.len_mu;
      this.len_s = this.len_mu;
      this.len_k = this.len_mu;
      this.len_pkh = this.len_mu;
      this.len_ss = this.len_mu;
      this.len_mu_bytes = this.len_mu / 8;
      this.len_seedSE_bytes = this.len_seedSE / 8;
      this.len_s_bytes = this.len_s / 8;
      this.len_k_bytes = this.len_k / 8;
      this.len_pkh_bytes = this.len_pkh / 8;
      this.len_ss_bytes = this.len_ss / 8;
      this.len_ct_bytes = var2 * var1 * 8 / 8 + var2 * 8 * 8 / 8;
      this.len_pk_bytes = 16 + var2 * var1 * 8 / 8;
      this.len_sk_bytes = this.len_s_bytes + this.len_pk_bytes + 2 * var1 * 8 + this.len_pkh_bytes;
      this.T_chi = var4;
      this.digest = var5;
      this.gen = var6;
   }

   private short[] sample_matrix(short[] var1, int var2, int var3, int var4) {
      short[] var5 = new short[var3 * var4];
      Noise.sample(this.T_chi, var1, var2, var5);
      return var5;
   }

   private short[] matrix_transpose(short[] var1, int var2, int var3) {
      short[] var4 = new short[var2 * var3];

      for (int var5 = 0; var5 < var3; var5++) {
         for (int var6 = 0; var6 < var2; var6++) {
            var4[var5 * var2 + var6] = var1[var6 * var3 + var5];
         }
      }

      return var4;
   }

   private short[] matrix_mul(short[] var1, int var2, int var3, short[] var4, int var5) {
      int var6 = this.q - 1;
      short[] var7 = new short[var2 * var5];

      for (int var8 = 0; var8 < var2; var8++) {
         for (int var9 = 0; var9 < var5; var9++) {
            int var10 = 0;

            for (int var11 = 0; var11 < var3; var11++) {
               var10 += var1[var8 * var3 + var11] * var4[var11 * var5 + var9];
            }

            var7[var8 * var5 + var9] = (short)(var10 & var6);
         }
      }

      return var7;
   }

   private short[] matrix_add(short[] var1, short[] var2, int var3, int var4) {
      int var5 = this.q - 1;
      short[] var6 = new short[var3 * var4];

      for (int var7 = 0; var7 < var3; var7++) {
         for (int var8 = 0; var8 < var4; var8++) {
            var6[var7 * var4 + var8] = (short)(var1[var7 * var4 + var8] + var2[var7 * var4 + var8] & var5);
         }
      }

      return var6;
   }

   private byte[] pack(short[] var1) {
      int var2 = var1.length;
      byte[] var3 = new byte[this.D * var2 / 8];
      short var4 = 0;
      short var5 = 0;
      short var6 = 0;
      byte var7 = 0;

      while (var4 < var3.length && (var5 < var2 || var5 == var2 && var7 > 0)) {
         byte var8 = 0;

         while (true) {
            if (var8 < 8) {
               int var9 = Math.min(8 - var8, var7);
               short var10 = (short)((1 << var9) - 1);
               byte var11 = (byte)(var6 >> var7 - var9 & var10);
               var3[var4] = (byte)(var3[var4] + (var11 << 8 - var8 - var9));
               var8 = (byte)(var8 + var9);
               var7 = (byte)(var7 - var9);
               if (var7 != 0) {
                  continue;
               }

               if (var5 < var2) {
                  var6 = var1[var5];
                  var7 = (byte)this.D;
                  var5++;
                  continue;
               }
            }

            if (var8 == 8) {
               var4++;
            }
            break;
         }
      }

      return var3;
   }

   public void kem_keypair(byte[] var1, byte[] var2, SecureRandom var3) {
      byte[] var4 = new byte[this.len_s_bytes + this.len_seedSE_bytes + 16];
      var3.nextBytes(var4);
      byte[] var5 = Arrays.copyOfRange(var4, 0, this.len_s_bytes);
      byte[] var6 = Arrays.copyOfRange(var4, this.len_s_bytes, this.len_s_bytes + this.len_seedSE_bytes);
      byte[] var7 = Arrays.copyOfRange(var4, this.len_s_bytes + this.len_seedSE_bytes, this.len_s_bytes + this.len_seedSE_bytes + 16);
      byte[] var8 = new byte[16];
      this.digest.update(var7, 0, var7.length);
      this.digest.doFinal(var8, 0, var8.length);
      short[] var9 = this.gen.genMatrix(var8, 0, var8.length);
      byte[] var10 = new byte[2 * this.n * 8 * 2];
      this.digest.update((byte)95);
      this.digest.update(var6, 0, var6.length);
      this.digest.doFinal(var10, 0, var10.length);
      short[] var11 = Pack.littleEndianToShort(var10, 0, var10.length / 2);
      short[] var12 = this.sample_matrix(var11, 0, 8, this.n);
      short[] var13 = this.matrix_transpose(var12, 8, this.n);
      short[] var14 = this.sample_matrix(var11, this.n * 8, this.n, 8);
      short[] var15 = this.matrix_add(this.matrix_mul(var9, this.n, this.n, var13, 8), var14, this.n, 8);
      byte[] var16 = this.pack(var15);
      System.arraycopy(var8, 0, var1, 0, 16);
      System.arraycopy(var16, 0, var1, 16, this.len_pk_bytes - 16);
      byte[] var17 = new byte[this.len_pkh_bytes];
      this.digest.update(var1, 0, var1.length);
      this.digest.doFinal(var17, 0, var17.length);
      System.arraycopy(var5, 0, var2, 0, this.len_s_bytes);
      System.arraycopy(var1, 0, var2, this.len_s_bytes, this.len_pk_bytes);
      Pack.shortToLittleEndian(var12, var2, this.len_s_bytes + this.len_pk_bytes);
      System.arraycopy(var17, 0, var2, this.len_sk_bytes - this.len_pkh_bytes, this.len_pkh_bytes);
   }

   private short[] unpack(byte[] var1, int var2, int var3) {
      short[] var4 = new short[var2 * var3];
      short var5 = 0;
      short var6 = 0;
      byte var7 = 0;
      byte var8 = 0;

      while (var5 < var4.length && (var6 < var1.length || var6 == var1.length && var8 > 0)) {
         byte var9 = 0;

         while (true) {
            if (var9 < this.D) {
               int var10 = Math.min(this.D - var9, var8);
               short var11 = (short)((1 << var10) - 1 & 65535);
               byte var12 = (byte)((var7 & 255) >>> (var8 & 255) - var10 & var11 & 65535 & 0xFF);
               var4[var5] = (short)((var4[var5] & '\uffff') + ((var12 & 255) << this.D - (var9 & 255) - var10) & 65535);
               var9 = (byte)(var9 + var10);
               var8 = (byte)(var8 - var10);
               var7 = (byte)(var7 & ~(var11 << var8));
               if (var8 != 0) {
                  continue;
               }

               if (var6 < var1.length) {
                  var7 = var1[var6];
                  var8 = 8;
                  var6++;
                  continue;
               }
            }

            if (var9 == this.D) {
               var5++;
            }
            break;
         }
      }

      return var4;
   }

   private short[] encode(byte[] var1) {
      int var2 = 0;
      int var3 = 0;
      short[] var4 = new short[64];

      for (int var5 = 0; var5 < 8; var5++) {
         for (int var6 = 0; var6 < 8; var6++) {
            int var7 = 0;

            for (int var8 = 0; var8 < this.B; var8++) {
               var7 += (var1[var2] >>> var3 & 1) << var8;
               var2 += ++var3 >>> 3;
               var3 &= 7;
            }

            var4[var5 * 8 + var6] = (short)(var7 * (this.q / (1 << this.B)));
         }
      }

      return var4;
   }

   public void kem_enc(byte[] var1, byte[] var2, byte[] var3, SecureRandom var4) {
      byte[] var5 = Arrays.copyOfRange(var3, 16, this.len_pk_bytes);
      byte[] var6 = new byte[this.len_mu_bytes];
      var4.nextBytes(var6);
      byte[] var7 = new byte[this.len_pkh_bytes];
      this.digest.update(var3, 0, this.len_pk_bytes);
      this.digest.doFinal(var7, 0, this.len_pkh_bytes);
      byte[] var8 = new byte[this.len_seedSE + this.len_k];
      this.digest.update(var7, 0, this.len_pkh_bytes);
      this.digest.update(var6, 0, this.len_mu_bytes);
      this.digest.doFinal(var8, 0, this.len_seedSE_bytes + this.len_k_bytes);
      byte[] var9 = Arrays.copyOfRange(var8, 0, this.len_seedSE_bytes);
      byte[] var10 = Arrays.copyOfRange(var8, this.len_seedSE_bytes, this.len_seedSE_bytes + this.len_k_bytes);
      byte[] var11 = new byte[(16 * this.n + 64) * 2];
      this.digest.update((byte)-106);
      this.digest.update(var9, 0, var9.length);
      this.digest.doFinal(var11, 0, var11.length);
      short[] var12 = Pack.littleEndianToShort(var11, 0, var11.length / 2);
      short[] var13 = this.sample_matrix(var12, 0, 8, this.n);
      short[] var14 = this.sample_matrix(var12, 8 * this.n, 8, this.n);
      short[] var15 = this.gen.genMatrix(var3, 0, 16);
      short[] var16 = this.matrix_add(this.matrix_mul(var13, 8, this.n, var15, this.n), var14, 8, this.n);
      byte[] var17 = this.pack(var16);
      short[] var18 = this.sample_matrix(var12, 16 * this.n, 8, 8);
      short[] var19 = this.unpack(var5, this.n, 8);
      short[] var20 = this.matrix_add(this.matrix_mul(var13, 8, this.n, var19, 8), var18, 8, 8);
      short[] var21 = this.encode(var6);
      short[] var22 = this.matrix_add(var20, var21, 8, 8);
      byte[] var23 = this.pack(var22);
      System.arraycopy(var17, 0, var1, 0, var17.length);
      System.arraycopy(var23, 0, var1, var17.length, this.len_ct_bytes - var17.length);
      this.digest.update(var1, 0, this.len_ct_bytes);
      this.digest.update(var10, 0, this.len_k_bytes);
      this.digest.doFinal(var2, 0, this.len_s_bytes);
   }

   private short[] matrix_sub(short[] var1, short[] var2, int var3, int var4) {
      int var5 = this.q - 1;
      short[] var6 = new short[var3 * var4];

      for (int var7 = 0; var7 < var3; var7++) {
         for (int var8 = 0; var8 < var4; var8++) {
            var6[var7 * var4 + var8] = (short)(var1[var7 * var4 + var8] - var2[var7 * var4 + var8] & var5);
         }
      }

      return var6;
   }

   private byte[] decode(short[] var1) {
      int var2 = 0;
      byte var3 = 8;
      byte var4 = 8;
      short var5 = (short)((1 << this.B) - 1);
      short var6 = (short)((1 << this.D) - 1);
      byte[] var7 = new byte[var3 * this.B];

      for (int var8 = 0; var8 < var4; var8++) {
         long var9 = 0L;

         for (int var11 = 0; var11 < var3; var11++) {
            short var12 = (short)((var1[var2] & var6) + (1 << this.D - this.B - 1) >> this.D - this.B);
            var9 |= (long)(var12 & var5) << this.B * var11;
            var2++;
         }

         for (int var13 = 0; var13 < this.B; var13++) {
            var7[var8 * this.B + var13] = (byte)(var9 >> 8 * var13 & 255L);
         }
      }

      return var7;
   }

   public void kem_dec(byte[] var1, byte[] var2, byte[] var3) {
      int var4 = 0;
      int var5 = 8 * this.n * this.D / 8;
      byte[] var6 = Arrays.copyOfRange(var2, var4, var4 + var5);
      var4 += var5;
      var5 = 64 * this.D / 8;
      byte[] var7 = Arrays.copyOfRange(var2, var4, var4 + var5);
      var4 = this.len_s_bytes + 16;
      var5 = this.D * this.n * 8 / 8;
      byte[] var8 = Arrays.copyOfRange(var3, var4, var4 + var5);
      var4 += var5;
      var5 = this.n * 8 * 16 / 8;
      byte[] var9 = Arrays.copyOfRange(var3, var4, var4 + var5);
      short[] var10 = new short[8 * this.n];

      for (int var11 = 0; var11 < 8; var11++) {
         for (int var12 = 0; var12 < this.n; var12++) {
            var10[var11 * this.n + var12] = Pack.littleEndianToShort(var9, var11 * this.n * 2 + var12 * 2);
         }
      }

      short[] var39 = this.matrix_transpose(var10, 8, this.n);
      var4 += var5;
      var5 = this.len_pkh_bytes;
      byte[] var40 = Arrays.copyOfRange(var3, var4, var4 + var5);
      short[] var13 = this.unpack(var6, 8, this.n);
      short[] var14 = this.unpack(var7, 8, 8);
      short[] var15 = this.matrix_mul(var13, 8, this.n, var39, 8);
      short[] var16 = this.matrix_sub(var14, var15, 8, 8);
      byte[] var17 = this.decode(var16);
      byte[] var18 = new byte[this.len_seedSE_bytes + this.len_k_bytes];
      this.digest.update(var40, 0, this.len_pkh_bytes);
      this.digest.update(var17, 0, this.len_mu_bytes);
      this.digest.doFinal(var18, 0, this.len_seedSE_bytes + this.len_k_bytes);
      byte[] var19 = Arrays.copyOfRange(var18, this.len_seedSE_bytes, this.len_seedSE_bytes + this.len_k_bytes);
      byte[] var20 = new byte[(16 * this.n + 64) * 2];
      this.digest.update((byte)-106);
      this.digest.update(var18, 0, this.len_seedSE_bytes);
      this.digest.doFinal(var20, 0, var20.length);
      short[] var21 = Pack.littleEndianToShort(var20, 0, var20.length / 2);
      short[] var22 = this.sample_matrix(var21, 0, 8, this.n);
      short[] var23 = this.sample_matrix(var21, 8 * this.n, 8, this.n);
      short[] var24 = this.gen.genMatrix(var3, this.len_s_bytes, 16);
      short[] var25 = this.matrix_add(this.matrix_mul(var22, 8, this.n, var24, this.n), var23, 8, this.n);
      short[] var26 = this.sample_matrix(var21, 16 * this.n, 8, 8);
      short[] var27 = this.unpack(var8, this.n, 8);
      short[] var28 = this.matrix_add(this.matrix_mul(var22, 8, this.n, var27, 8), var26, 8, 8);
      short[] var29 = this.matrix_add(var28, this.encode(var17), 8, 8);
      int var30 = ctverify(var13, var14, var25, var29);
      Bytes.cmov(var19.length, ~var30, var3, var19);
      this.digest.update(var6, 0, var6.length);
      this.digest.update(var7, 0, var7.length);
      this.digest.update(var19, 0, var19.length);
      this.digest.doFinal(var1, 0, this.len_ss_bytes);
   }

   private static int ctverify(short[] var0, short[] var1, short[] var2, short[] var3) {
      int var4 = 0;

      for (int var5 = 0; var5 < var0.length; var5++) {
         var4 |= var0[var5] ^ var2[var5];
      }

      for (int var6 = 0; var6 < var1.length; var6++) {
         var4 |= var1[var6] ^ var3[var6];
      }

      return Nat.czero(var4);
   }
}
