package org.bouncycastle.pqc.crypto.slhdsa;

import java.io.IOException;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.params.ParametersWithContext;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.DigestUtils;

@Deprecated
public class HashSLHDSASigner implements Signer {
   private byte[] msgPrefix;
   private byte[] optRand;
   private SLHDSAPublicKeyParameters pubKey;
   private SLHDSAPrivateKeyParameters privKey;
   private SecureRandom random;
   private Digest digest;

   @Override
   public void init(boolean var1, CipherParameters var2) {
      ParametersWithContext var3 = null;
      if (var2 instanceof ParametersWithContext) {
         var3 = (ParametersWithContext)var2;
         var2 = ((ParametersWithContext)var2).getParameters();
         if (var3.getContextLength() > 255) {
            throw new IllegalArgumentException("context too long");
         }
      }

      SLHDSAParameters var4;
      if (var1) {
         this.pubKey = null;
         if (var2 instanceof ParametersWithRandom) {
            this.privKey = (SLHDSAPrivateKeyParameters)((ParametersWithRandom)var2).getParameters();
            this.random = ((ParametersWithRandom)var2).getRandom();
         } else {
            this.privKey = (SLHDSAPrivateKeyParameters)var2;
            this.random = null;
         }

         var4 = this.privKey.getParameters();
         this.optRand = new byte[var4.getN()];
      } else {
         this.pubKey = (SLHDSAPublicKeyParameters)var2;
         this.privKey = null;
         this.random = null;
         var4 = this.pubKey.getParameters();
      }

      this.initDigest(var4, var3);
   }

   private void initDigest(SLHDSAParameters var1, ParametersWithContext var2) {
      this.digest = createDigest(var1);
      ASN1ObjectIdentifier var3 = DigestUtils.getDigestOid(this.digest.getAlgorithmName());

      byte[] var4;
      try {
         var4 = var3.getEncoded("DER");
      } catch (IOException var6) {
         throw new IllegalStateException("oid encoding failed: " + var6.getMessage());
      }

      int var5 = var2 == null ? 0 : var2.getContextLength();
      this.msgPrefix = new byte[2 + var5 + var4.length];
      this.msgPrefix[0] = 1;
      this.msgPrefix[1] = (byte)var5;
      if (var2 != null) {
         var2.copyContextTo(this.msgPrefix, 2, var5);
      }

      System.arraycopy(var4, 0, this.msgPrefix, 2 + var5, var4.length);
   }

   @Override
   public void update(byte var1) {
      this.digest.update(var1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      this.digest.update(var1, var2, var3);
   }

   @Override
   public byte[] generateSignature() throws CryptoException, DataLengthException {
      byte[] var1 = new byte[this.digest.getDigestSize()];
      this.digest.doFinal(var1, 0);
      if (this.random != null) {
         this.random.nextBytes(this.optRand);
      } else {
         System.arraycopy(this.privKey.pk.seed, 0, this.optRand, 0, this.optRand.length);
      }

      return SLHDSAEngine.internalGenerateSignature(this.privKey, this.msgPrefix, var1, this.optRand);
   }

   @Override
   public boolean verifySignature(byte[] var1) {
      byte[] var2 = new byte[this.digest.getDigestSize()];
      this.digest.doFinal(var2, 0);
      return SLHDSAEngine.internalVerifySignature(this.pubKey, this.msgPrefix, var2, var1);
   }

   @Override
   public void reset() {
      this.digest.reset();
   }

   protected byte[] internalGenerateSignature(byte[] var1, byte[] var2) {
      return SLHDSAEngine.internalGenerateSignature(this.privKey, null, var1, var2);
   }

   protected boolean internalVerifySignature(byte[] var1, byte[] var2) {
      return SLHDSAEngine.internalVerifySignature(this.pubKey, null, var1, var2);
   }

   private static Digest createDigest(SLHDSAParameters var0) {
      switch (var0.getType()) {
         case 0:
            String var1 = var0.getName();
            if (var1.startsWith("sha2")) {
               if (SLHDSAParameters.sha2_128f != var0 && SLHDSAParameters.sha2_128s != var0) {
                  return new SHA512Digest();
               }

               return SHA256Digest.newInstance();
            } else {
               if (SLHDSAParameters.shake_128f != var0 && SLHDSAParameters.shake_128s != var0) {
                  return new SHAKEDigest(256);
               }

               return new SHAKEDigest(128);
            }
         case 1:
            return SHA256Digest.newInstance();
         case 2:
            return new SHA512Digest();
         case 3:
            return new SHAKEDigest(128);
         case 4:
            return new SHAKEDigest(256);
         default:
            throw new IllegalArgumentException("unknown parameters type");
      }
   }
}
