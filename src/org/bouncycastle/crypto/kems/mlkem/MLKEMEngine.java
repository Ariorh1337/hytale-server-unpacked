package org.bouncycastle.crypto.kems.mlkem;

import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.params.MLKEMParameters;
import org.bouncycastle.crypto.params.MLKEMPrivateKeyParameters;
import org.bouncycastle.crypto.params.MLKEMPublicKeyParameters;
import org.bouncycastle.util.Arrays;

public class MLKEMEngine {
   public static final int SymBytes = 32;
   public static final int SeedBytes = 64;
   private final MLKEMIndCpa indCpa;
   static final int N = 256;
   static final int Q = 3329;
   static final int Qinv = 62209;
   static final int SharedSecretBytes = 32;
   static final int PolyBytes = 384;
   static final int Eta2 = 2;
   private final int K;
   private final int PolyVecBytes;
   private final int PolyCompressedBytes;
   private final int PolyVecCompressedBytes;
   private final int Eta1;
   private final int IndCpaPublicKeyBytes;
   private final int IndCpaSecretKeyBytes;
   private final int SecretKeyBytes;
   private final int CipherTextBytes;
   private static final MLKEMEngine[] engines = new MLKEMEngine[]{
      new MLKEMEngine(MLKEMParameters.ml_kem_512.getK()),
      new MLKEMEngine(MLKEMParameters.ml_kem_768.getK()),
      new MLKEMEngine(MLKEMParameters.ml_kem_1024.getK())
   };

   public static MLKEMEngine getInstance(MLKEMParameters var0) {
      return engines[var0.getK() - 2];
   }

   private MLKEMEngine(int var1) {
      this.K = var1;
      switch (var1) {
         case 2:
            this.Eta1 = 3;
            this.PolyCompressedBytes = 128;
            this.PolyVecCompressedBytes = var1 * 320;
            break;
         case 3:
            this.Eta1 = 2;
            this.PolyCompressedBytes = 128;
            this.PolyVecCompressedBytes = var1 * 320;
            break;
         case 4:
            this.Eta1 = 2;
            this.PolyCompressedBytes = 160;
            this.PolyVecCompressedBytes = var1 * 352;
            break;
         default:
            throw new IllegalArgumentException("K: " + var1 + " is not supported for ML-KEM");
      }

      this.PolyVecBytes = var1 * 384;
      this.IndCpaPublicKeyBytes = this.PolyVecBytes + 32;
      this.IndCpaSecretKeyBytes = this.PolyVecBytes;
      this.CipherTextBytes = this.PolyVecCompressedBytes + this.PolyCompressedBytes;
      this.SecretKeyBytes = this.IndCpaSecretKeyBytes + this.IndCpaPublicKeyBytes + 64;
      this.indCpa = new MLKEMIndCpa(this);
   }

   public int getCipherTextBytes() {
      return this.CipherTextBytes;
   }

   int getSecretKeyBytes() {
      return this.SecretKeyBytes;
   }

   public int getIndCpaPublicKeyBytes() {
      return this.IndCpaPublicKeyBytes;
   }

   public int getIndCpaSecretKeyBytes() {
      return this.IndCpaSecretKeyBytes;
   }

   int getPublicKeyBytes() {
      return this.getIndCpaPublicKeyBytes();
   }

   int getPolyCompressedBytes() {
      return this.PolyCompressedBytes;
   }

   int getK() {
      return this.K;
   }

   public int getPolyVecBytes() {
      return this.PolyVecBytes;
   }

   int getPolyVecCompressedBytes() {
      return this.PolyVecCompressedBytes;
   }

   int getEta1() {
      return this.Eta1;
   }

   public boolean checkModulus(byte[] var1) {
      return PolyVec.checkModulus(this, var1) < 0;
   }

   public boolean checkPrivateKey(byte[] var1) {
      int var2 = this.getK();
      int var3 = var2 * 384;
      int var4 = var2 * 768;
      if (var4 + 96 != var1.length) {
         throw new IllegalArgumentException("'encoding' has invalid length");
      }

      byte[] var5 = new byte[32];
      hash_H(var1, var3, var3 + 32, var5, 0);
      return Arrays.constantTimeAreEqual(32, var5, 0, var1, var4 + 32);
   }

   public byte[][] generateKemKeyPair(SecureRandom var1) {
      byte[] var2 = new byte[32];
      byte[] var3 = new byte[32];
      var1.nextBytes(var2);
      var1.nextBytes(var3);
      return this.generateKemKeyPairInternal(var2, var3);
   }

   public byte[][] generateKemKeyPairInternal(byte[] var1, byte[] var2) {
      byte[][] var3 = this.indCpa.generateKeyPair(var1);
      byte[] var4 = new byte[this.IndCpaSecretKeyBytes];
      System.arraycopy(var3[1], 0, var4, 0, this.IndCpaSecretKeyBytes);
      byte[] var5 = new byte[32];
      hash_H(var3[0], 0, var3[0].length, var5, 0);
      byte[] var6 = new byte[this.IndCpaPublicKeyBytes];
      System.arraycopy(var3[0], 0, var6, 0, this.IndCpaPublicKeyBytes);
      return new byte[][]{
         Arrays.copyOfRange(var6, 0, var6.length - 32),
         Arrays.copyOfRange(var6, var6.length - 32, var6.length),
         var4,
         var5,
         var2,
         Arrays.concatenate(var1, var2)
      };
   }

   static void hash_G(byte[] var0, byte[] var1) {
      implDigest(new SHA3Digest(512), var0, 0, var0.length, var1, 0);
   }

   private static void hash_H(byte[] var0, int var1, int var2, byte[] var3, int var4) {
      implDigest(new SHA3Digest(256), var0, var1, var2, var3, var4);
   }

   private static void implDigest(SHA3Digest var0, byte[] var1, int var2, int var3, byte[] var4, int var5) {
      var0.update(var1, var2, var3);
      var0.doFinal(var4, var5);
   }

   public byte[][] kemEncrypt(MLKEMPublicKeyParameters var1, byte[] var2) {
      byte[] var3 = var1.getEncoded();
      byte[] var4 = new byte[64];
      byte[] var5 = new byte[64];
      System.arraycopy(var2, 0, var4, 0, 32);
      hash_H(var3, 0, var3.length, var4, 32);
      hash_G(var4, var5);
      byte[] var6 = this.indCpa.encrypt(var3, 0, var4, 0, var5, 32);
      byte[] var7 = new byte[32];
      System.arraycopy(var5, 0, var7, 0, var7.length);
      return new byte[][]{var7, var6};
   }

   public byte[] kemDecrypt(MLKEMPrivateKeyParameters var1, byte[] var2) {
      byte[] var3 = var1.getEncoded();
      byte[] var4 = new byte[64];
      this.indCpa.decrypt(var3, var2, var4);
      System.arraycopy(var3, this.SecretKeyBytes - 64, var4, 32, 32);
      byte[] var5 = new byte[64];
      hash_G(var4, var5);
      int var6 = this.IndCpaSecretKeyBytes;
      byte[] var7 = this.indCpa.encrypt(var3, var6, var4, 0, var5, 32);
      int var8 = this.constantTimeZeroOnEqual(var2, var7);
      byte[] var9 = new byte[32];
      SHAKEDigest var10 = new SHAKEDigest(256);
      var10.update(var3, this.SecretKeyBytes - 32, 32);
      var10.update(var2, 0, this.CipherTextBytes);
      var10.doFinal(var9, 0, 32);
      this.cmov(var5, var9, 32, var8);
      return Arrays.copyOfRange(var5, 0, 32);
   }

   private void cmov(byte[] var1, byte[] var2, int var3, int var4) {
      int var5 = 0 - var4 >> 24;

      for (int var6 = 0; var6 != var3; var6++) {
         var1[var6] = (byte)(var2[var6] & var5 | var1[var6] & ~var5);
      }
   }

   private int constantTimeZeroOnEqual(byte[] var1, byte[] var2) {
      int var3 = var2.length ^ var1.length;

      for (int var4 = 0; var4 != var2.length; var4++) {
         var3 |= var1[var4] ^ var2[var4];
      }

      return var3 & 0xFF;
   }
}
