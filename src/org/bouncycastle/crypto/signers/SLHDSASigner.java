package org.bouncycastle.crypto.signers;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithContext;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.SLHDSAParameters;
import org.bouncycastle.crypto.params.SLHDSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.SLHDSAPublicKeyParameters;
import org.bouncycastle.crypto.signers.slhdsa.SLHDSAEngine;
import org.bouncycastle.pqc.crypto.MessageSigner;

public class SLHDSASigner implements MessageSigner {
   private static final byte[] DEFAULT_PREFIX = new byte[]{0, 0};
   private byte[] msgPrefix;
   private byte[] optRand;
   private SLHDSAPublicKeyParameters pubKey;
   private SLHDSAPrivateKeyParameters privKey;
   private SecureRandom random;
   private byte[] pkSeed;
   private byte[] pkRoot;
   private byte[] skSeed;
   private byte[] skPrf;

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (var2 instanceof ParametersWithContext) {
         ParametersWithContext var3 = (ParametersWithContext)var2;
         var2 = var3.getParameters();
         int var4 = var3.getContextLength();
         if (var4 > 255) {
            throw new IllegalArgumentException("context too long");
         }

         this.msgPrefix = new byte[2 + var4];
         this.msgPrefix[0] = 0;
         this.msgPrefix[1] = (byte)var4;
         var3.copyContextTo(this.msgPrefix, 2, var4);
      } else {
         this.msgPrefix = DEFAULT_PREFIX;
      }

      SLHDSAParameters var5;
      if (var1) {
         this.pubKey = null;
         if (var2 instanceof ParametersWithRandom) {
            ParametersWithRandom var6 = (ParametersWithRandom)var2;
            this.privKey = (SLHDSAPrivateKeyParameters)var6.getParameters();
            this.random = var6.getRandom();
         } else {
            this.privKey = (SLHDSAPrivateKeyParameters)var2;
            this.random = null;
         }

         this.skSeed = this.privKey.getSeed();
         this.skPrf = this.privKey.getPrf();
         this.pkSeed = this.privKey.getPublicSeed();
         this.pkRoot = this.privKey.getRoot();
         var5 = this.privKey.getParameters();
         this.optRand = new byte[var5.getN()];
      } else {
         this.pubKey = (SLHDSAPublicKeyParameters)var2;
         this.privKey = null;
         this.random = null;
         this.skSeed = null;
         this.skPrf = null;
         this.pkSeed = this.pubKey.getSeed();
         this.pkRoot = this.pubKey.getRoot();
         var5 = this.pubKey.getParameters();
      }

      if (var5.isPreHash()) {
         throw new IllegalArgumentException("\"pure\" slh-dsa must use non pre-hash parameters");
      }
   }

   @Override
   public byte[] generateSignature(byte[] var1) {
      if (this.random != null) {
         this.random.nextBytes(this.optRand);
      } else {
         System.arraycopy(this.privKey.getPublicSeed(), 0, this.optRand, 0, this.optRand.length);
      }

      return SLHDSAEngine.internalGenerateSignature(
         this.privKey.getParameters(), this.skSeed, this.skPrf, this.pkSeed, this.pkRoot, this.msgPrefix, var1, this.optRand
      );
   }

   @Override
   public boolean verifySignature(byte[] var1, byte[] var2) {
      return SLHDSAEngine.internalVerifySignature(this.pubKey.getParameters(), this.pkSeed, this.pkRoot, this.msgPrefix, var1, var2);
   }

   protected boolean internalVerifySignature(byte[] var1, byte[] var2) {
      return SLHDSAEngine.internalVerifySignature(this.pubKey.getParameters(), this.pkSeed, this.pkRoot, null, var1, var2);
   }

   protected byte[] internalGenerateSignature(byte[] var1, byte[] var2) {
      return SLHDSAEngine.internalGenerateSignature(this.privKey.getParameters(), this.skSeed, this.skPrf, this.pkSeed, this.pkRoot, null, var1, var2);
   }
}
