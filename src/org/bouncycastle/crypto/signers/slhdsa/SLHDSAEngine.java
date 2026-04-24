package org.bouncycastle.crypto.signers.slhdsa;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.generators.MGF1BytesGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.MGFParameters;
import org.bouncycastle.crypto.params.SLHDSAParameters;
import org.bouncycastle.crypto.params.SLHDSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.SLHDSAPublicKeyParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public abstract class SLHDSAEngine {
   final int N;
   final int WOTS_W;
   final int WOTS_LOGW;
   final int WOTS_LEN;
   final int WOTS_LEN1;
   final int WOTS_LEN2;
   final int D;
   final int A;
   final int K;
   final int H;
   final int H_PRIME;

   protected SLHDSAEngine(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.N = var1;
      if (var2 == 16) {
         this.WOTS_LOGW = 4;
         this.WOTS_LEN1 = 8 * this.N / this.WOTS_LOGW;
         if (this.N <= 8) {
            this.WOTS_LEN2 = 2;
         } else if (this.N <= 136) {
            this.WOTS_LEN2 = 3;
         } else {
            if (this.N > 256) {
               throw new IllegalArgumentException("cannot precompute SPX_WOTS_LEN2 for n outside {2, .., 256}");
            }

            this.WOTS_LEN2 = 4;
         }
      } else {
         if (var2 != 256) {
            throw new IllegalArgumentException("wots_w assumed 16 or 256");
         }

         this.WOTS_LOGW = 8;
         this.WOTS_LEN1 = 8 * this.N / this.WOTS_LOGW;
         if (this.N <= 1) {
            this.WOTS_LEN2 = 1;
         } else {
            if (this.N > 256) {
               throw new IllegalArgumentException("cannot precompute SPX_WOTS_LEN2 for n outside {2, .., 256}");
            }

            this.WOTS_LEN2 = 2;
         }
      }

      this.WOTS_W = var2;
      this.WOTS_LEN = this.WOTS_LEN1 + this.WOTS_LEN2;
      this.D = var3;
      this.A = var4;
      this.K = var5;
      this.H = var6;
      this.H_PRIME = var6 / var3;
   }

   abstract void init(byte[] var1);

   abstract byte[] F(byte[] var1, ADRS var2, byte[] var3);

   abstract byte[] H(byte[] var1, ADRS var2, byte[] var3, byte[] var4);

   abstract IndexedDigest H_msg(byte[] var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5);

   abstract byte[] T_l(byte[] var1, ADRS var2, byte[] var3);

   abstract byte[] PRF(byte[] var1, byte[] var2, ADRS var3);

   abstract byte[] PRF_msg(byte[] var1, byte[] var2, byte[] var3, byte[] var4);

   public static AsymmetricCipherKeyPair implGenerateKeyPair(SLHDSAParameters var0, byte[] var1, byte[] var2, byte[] var3) {
      SLHDSAEngine var4 = var0.getEngine();
      var4.init(var3);
      return new AsymmetricCipherKeyPair(
         new SLHDSAPublicKeyParameters(var0, Arrays.concatenate(var3, (new HT(var4, var1, var3)).htPubKey)),
         new SLHDSAPrivateKeyParameters(var0, var1, var2, var3, (new HT(var4, var1, var3)).htPubKey)
      );
   }

   public static boolean internalVerifySignature(SLHDSAParameters var0, byte[] var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5) {
      SLHDSAEngine var6 = var0.getEngine();
      var6.init(var1);
      ADRS var7 = new ADRS();
      if ((1 + var6.K * (1 + var6.A) + var6.H + var6.D * var6.WOTS_LEN) * var6.N != var5.length) {
         return false;
      }

      SIG var8 = new SIG(var6.N, var6.K, var6.A, var6.D, var6.H_PRIME, var6.WOTS_LEN, var5);
      byte[] var9 = var8.getR();
      SIG_FORS[] var10 = var8.getSIG_FORS();
      SIG_XMSS[] var11 = var8.getSIG_HT();
      IndexedDigest var12 = var6.H_msg(var9, var1, var2, var3, var4);
      byte[] var13 = var12.digest;
      long var14 = var12.idx_tree;
      int var16 = var12.idx_leaf;
      var7.setTypeAndClear(3);
      var7.setLayerAddress(0);
      var7.setTreeAddress(var14);
      var7.setKeyPairAddress(var16);
      byte[] var17 = new Fors(var6).pkFromSig(var10, var13, var1, var7);
      var7.setTypeAndClear(2);
      var7.setLayerAddress(0);
      var7.setTreeAddress(var14);
      var7.setKeyPairAddress(var16);
      HT var18 = new HT(var6, null, var1);
      return var18.verify(var17, var11, var1, var14, var16, var2);
   }

   public static byte[] internalGenerateSignature(
      SLHDSAParameters var0, byte[] var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5, byte[] var6, byte[] var7
   ) {
      SLHDSAEngine var8 = var0.getEngine();
      var8.init(var3);
      Fors var9 = new Fors(var8);
      byte[] var10 = var8.PRF_msg(var2, var7, var5, var6);
      IndexedDigest var11 = var8.H_msg(var10, var3, var4, var5, var6);
      byte[] var12 = var11.digest;
      long var13 = var11.idx_tree;
      int var15 = var11.idx_leaf;
      ADRS var16 = new ADRS();
      var16.setTypeAndClear(3);
      var16.setTreeAddress(var13);
      var16.setKeyPairAddress(var15);
      SIG_FORS[] var17 = var9.sign(var12, var1, var3, var16);
      var16 = new ADRS();
      var16.setTypeAndClear(3);
      var16.setTreeAddress(var13);
      var16.setKeyPairAddress(var15);
      byte[] var18 = var9.pkFromSig(var17, var12, var3, var16);
      ADRS var19 = new ADRS();
      var19.setTypeAndClear(2);
      HT var20 = new HT(var8, var1, var3);
      byte[] var21 = var20.sign(var18, var13, var15);
      byte[][] var22 = new byte[var17.length + 2][];
      var22[0] = var10;

      for (int var23 = 0; var23 != var17.length; var23++) {
         var22[1 + var23] = Arrays.concatenate(var17[var23].sk, Arrays.concatenate(var17[var23].authPath));
      }

      var22[var22.length - 1] = var21;
      return Arrays.concatenate(var22);
   }

   public static class Sha2Engine extends SLHDSAEngine {
      private final HMac treeHMac;
      private final MGF1BytesGenerator mgf1;
      private final byte[] hmacBuf;
      private final Digest msgDigest;
      private final byte[] msgDigestBuf;
      private final int bl;
      private final Digest sha256 = new SHA256Digest();
      private final byte[] sha256Buf = new byte[this.sha256.getDigestSize()];
      private Memoable msgMemo;
      private Memoable sha256Memo;

      public Sha2Engine(int var1, int var2, int var3, int var4, int var5, int var6) {
         super(var1, var2, var3, var4, var5, var6);
         if (var1 == 16) {
            this.msgDigest = new SHA256Digest();
            this.treeHMac = new HMac(new SHA256Digest());
            this.mgf1 = new MGF1BytesGenerator(new SHA256Digest());
            this.bl = 64;
         } else {
            this.msgDigest = new SHA512Digest();
            this.treeHMac = new HMac(new SHA512Digest());
            this.mgf1 = new MGF1BytesGenerator(new SHA512Digest());
            this.bl = 128;
         }

         this.hmacBuf = new byte[this.treeHMac.getMacSize()];
         this.msgDigestBuf = new byte[this.msgDigest.getDigestSize()];
      }

      @Override
      void init(byte[] var1) {
         byte[] var2 = new byte[this.bl];
         this.msgDigest.update(var1, 0, var1.length);
         this.msgDigest.update(var2, 0, this.bl - this.N);
         this.msgMemo = ((Memoable)this.msgDigest).copy();
         this.msgDigest.reset();
         this.sha256.update(var1, 0, var1.length);
         this.sha256.update(var2, 0, 64 - var1.length);
         this.sha256Memo = ((Memoable)this.sha256).copy();
         this.sha256.reset();
      }

      @Override
      public byte[] F(byte[] var1, ADRS var2, byte[] var3) {
         byte[] var4 = this.compressedADRS(var2);
         ((Memoable)this.sha256).reset(this.sha256Memo);
         this.sha256.update(var4, 0, var4.length);
         this.sha256.update(var3, 0, var3.length);
         this.sha256.doFinal(this.sha256Buf, 0);
         return Arrays.copyOfRange(this.sha256Buf, 0, this.N);
      }

      @Override
      public byte[] H(byte[] var1, ADRS var2, byte[] var3, byte[] var4) {
         byte[] var5 = this.compressedADRS(var2);
         ((Memoable)this.msgDigest).reset(this.msgMemo);
         this.msgDigest.update(var5, 0, var5.length);
         this.msgDigest.update(var3, 0, var3.length);
         this.msgDigest.update(var4, 0, var4.length);
         this.msgDigest.doFinal(this.msgDigestBuf, 0);
         return Arrays.copyOfRange(this.msgDigestBuf, 0, this.N);
      }

      @Override
      IndexedDigest H_msg(byte[] var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5) {
         int var6 = (this.A * this.K + 7) / 8;
         int var7 = this.H / this.D;
         int var8 = this.H - var7;
         int var9 = (var7 + 7) / 8;
         int var10 = (var8 + 7) / 8;
         int var11 = var6 + var9 + var10;
         byte[] var12 = new byte[var11];
         byte[] var13 = new byte[this.msgDigest.getDigestSize()];
         this.msgDigest.update(var1, 0, var1.length);
         this.msgDigest.update(var2, 0, var2.length);
         this.msgDigest.update(var3, 0, var3.length);
         if (var4 != null) {
            this.msgDigest.update(var4, 0, var4.length);
         }

         this.msgDigest.update(var5, 0, var5.length);
         this.msgDigest.doFinal(var13, 0);
         var12 = this.bitmask(Arrays.concatenate(var1, var2, var13), var12);
         byte[] var14 = new byte[8];
         System.arraycopy(var12, var6, var14, 8 - var10, var10);
         long var15 = Pack.bigEndianToLong(var14, 0);
         var15 &= -1L >>> 64 - var8;
         byte[] var17 = new byte[4];
         System.arraycopy(var12, var6 + var10, var17, 4 - var9, var9);
         int var18 = Pack.bigEndianToInt(var17, 0);
         var18 &= -1 >>> 32 - var7;
         return new IndexedDigest(var15, var18, Arrays.copyOfRange(var12, 0, var6));
      }

      @Override
      public byte[] T_l(byte[] var1, ADRS var2, byte[] var3) {
         byte[] var4 = this.compressedADRS(var2);
         ((Memoable)this.msgDigest).reset(this.msgMemo);
         this.msgDigest.update(var4, 0, var4.length);
         this.msgDigest.update(var3, 0, var3.length);
         this.msgDigest.doFinal(this.msgDigestBuf, 0);
         return Arrays.copyOfRange(this.msgDigestBuf, 0, this.N);
      }

      @Override
      byte[] PRF(byte[] var1, byte[] var2, ADRS var3) {
         int var4 = var2.length;
         ((Memoable)this.sha256).reset(this.sha256Memo);
         byte[] var5 = this.compressedADRS(var3);
         this.sha256.update(var5, 0, var5.length);
         this.sha256.update(var2, 0, var2.length);
         this.sha256.doFinal(this.sha256Buf, 0);
         return Arrays.copyOfRange(this.sha256Buf, 0, var4);
      }

      @Override
      public byte[] PRF_msg(byte[] var1, byte[] var2, byte[] var3, byte[] var4) {
         this.treeHMac.init(new KeyParameter(var1));
         this.treeHMac.update(var2, 0, var2.length);
         if (var3 != null) {
            this.treeHMac.update(var3, 0, var3.length);
         }

         this.treeHMac.update(var4, 0, var4.length);
         this.treeHMac.doFinal(this.hmacBuf, 0);
         return Arrays.copyOfRange(this.hmacBuf, 0, this.N);
      }

      private byte[] compressedADRS(ADRS var1) {
         byte[] var2 = new byte[22];
         System.arraycopy(var1.value, 3, var2, 0, 1);
         System.arraycopy(var1.value, 8, var2, 1, 8);
         System.arraycopy(var1.value, 19, var2, 9, 1);
         System.arraycopy(var1.value, 20, var2, 10, 12);
         return var2;
      }

      protected byte[] bitmask(byte[] var1, byte[] var2) {
         byte[] var3 = new byte[var2.length];
         this.mgf1.init(new MGFParameters(var1));
         this.mgf1.generateBytes(var3, 0, var3.length);
         Bytes.xorTo(var2.length, var2, var3);
         return var3;
      }

      protected byte[] bitmask(byte[] var1, byte[] var2, byte[] var3) {
         byte[] var4 = new byte[var2.length + var3.length];
         this.mgf1.init(new MGFParameters(var1));
         this.mgf1.generateBytes(var4, 0, var4.length);
         Bytes.xorTo(var2.length, var2, var4);
         Bytes.xorTo(var3.length, var3, 0, var4, var2.length);
         return var4;
      }

      protected byte[] bitmask256(byte[] var1, byte[] var2) {
         byte[] var3 = new byte[var2.length];
         MGF1BytesGenerator var4 = new MGF1BytesGenerator(new SHA256Digest());
         var4.init(new MGFParameters(var1));
         var4.generateBytes(var3, 0, var3.length);
         Bytes.xorTo(var2.length, var2, var3);
         return var3;
      }
   }

   public static class Shake256Engine extends SLHDSAEngine {
      private final Xof treeDigest = new SHAKEDigest(256);
      private final Xof maskDigest = new SHAKEDigest(256);

      public Shake256Engine(int var1, int var2, int var3, int var4, int var5, int var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      @Override
      void init(byte[] var1) {
      }

      @Override
      byte[] F(byte[] var1, ADRS var2, byte[] var3) {
         byte[] var4 = var3;
         byte[] var5 = new byte[this.N];
         this.treeDigest.update(var1, 0, var1.length);
         this.treeDigest.update(var2.value, 0, var2.value.length);
         this.treeDigest.update(var4, 0, var4.length);
         this.treeDigest.doFinal(var5, 0, var5.length);
         return var5;
      }

      @Override
      byte[] H(byte[] var1, ADRS var2, byte[] var3, byte[] var4) {
         byte[] var5 = new byte[this.N];
         this.treeDigest.update(var1, 0, var1.length);
         this.treeDigest.update(var2.value, 0, var2.value.length);
         this.treeDigest.update(var3, 0, var3.length);
         this.treeDigest.update(var4, 0, var4.length);
         this.treeDigest.doFinal(var5, 0, var5.length);
         return var5;
      }

      @Override
      IndexedDigest H_msg(byte[] var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5) {
         int var6 = (this.A * this.K + 7) / 8;
         int var7 = this.H / this.D;
         int var8 = this.H - var7;
         int var9 = (var7 + 7) / 8;
         int var10 = (var8 + 7) / 8;
         int var11 = var6 + var9 + var10;
         byte[] var12 = new byte[var11];
         this.treeDigest.update(var1, 0, var1.length);
         this.treeDigest.update(var2, 0, var2.length);
         this.treeDigest.update(var3, 0, var3.length);
         if (var4 != null) {
            this.treeDigest.update(var4, 0, var4.length);
         }

         this.treeDigest.update(var5, 0, var5.length);
         this.treeDigest.doFinal(var12, 0, var12.length);
         byte[] var13 = new byte[8];
         System.arraycopy(var12, var6, var13, 8 - var10, var10);
         long var14 = Pack.bigEndianToLong(var13, 0);
         var14 &= -1L >>> 64 - var8;
         byte[] var16 = new byte[4];
         System.arraycopy(var12, var6 + var10, var16, 4 - var9, var9);
         int var17 = Pack.bigEndianToInt(var16, 0);
         var17 &= -1 >>> 32 - var7;
         return new IndexedDigest(var14, var17, Arrays.copyOfRange(var12, 0, var6));
      }

      @Override
      byte[] T_l(byte[] var1, ADRS var2, byte[] var3) {
         byte[] var4 = var3;
         byte[] var5 = new byte[this.N];
         this.treeDigest.update(var1, 0, var1.length);
         this.treeDigest.update(var2.value, 0, var2.value.length);
         this.treeDigest.update(var4, 0, var4.length);
         this.treeDigest.doFinal(var5, 0, var5.length);
         return var5;
      }

      @Override
      byte[] PRF(byte[] var1, byte[] var2, ADRS var3) {
         this.treeDigest.update(var1, 0, var1.length);
         this.treeDigest.update(var3.value, 0, var3.value.length);
         this.treeDigest.update(var2, 0, var2.length);
         byte[] var4 = new byte[this.N];
         this.treeDigest.doFinal(var4, 0, this.N);
         return var4;
      }

      @Override
      public byte[] PRF_msg(byte[] var1, byte[] var2, byte[] var3, byte[] var4) {
         this.treeDigest.update(var1, 0, var1.length);
         this.treeDigest.update(var2, 0, var2.length);
         if (var3 != null) {
            this.treeDigest.update(var3, 0, var3.length);
         }

         this.treeDigest.update(var4, 0, var4.length);
         byte[] var5 = new byte[this.N];
         this.treeDigest.doFinal(var5, 0, var5.length);
         return var5;
      }

      protected byte[] bitmask(byte[] var1, ADRS var2, byte[] var3) {
         byte[] var4 = new byte[var3.length];
         this.maskDigest.update(var1, 0, var1.length);
         this.maskDigest.update(var2.value, 0, var2.value.length);
         this.maskDigest.doFinal(var4, 0, var4.length);
         Bytes.xorTo(var3.length, var3, var4);
         return var4;
      }

      protected byte[] bitmask(byte[] var1, ADRS var2, byte[] var3, byte[] var4) {
         byte[] var5 = new byte[var3.length + var4.length];
         this.maskDigest.update(var1, 0, var1.length);
         this.maskDigest.update(var2.value, 0, var2.value.length);
         this.maskDigest.doFinal(var5, 0, var5.length);
         Bytes.xorTo(var3.length, var3, var5);
         Bytes.xorTo(var4.length, var4, 0, var5, var3.length);
         return var5;
      }
   }
}
