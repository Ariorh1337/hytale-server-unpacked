package org.bouncycastle.pqc.crypto.slhdsa;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithContext;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;

@Deprecated
public class SLHDSASigner implements MessageSigner {
   private static final byte[] DEFAULT_PREFIX = new byte[]{0, 0};
   private byte[] msgPrefix;
   private byte[] optRand;
   private SLHDSAPublicKeyParameters pubKey;
   private SLHDSAPrivateKeyParameters privKey;
   private SecureRandom random;

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

         var5 = this.privKey.getParameters();
         this.optRand = new byte[var5.getN()];
      } else {
         this.pubKey = (SLHDSAPublicKeyParameters)var2;
         this.privKey = null;
         this.random = null;
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
         System.arraycopy(this.privKey.pk.seed, 0, this.optRand, 0, this.optRand.length);
      }

      return SLHDSAEngine.internalGenerateSignature(this.privKey, this.msgPrefix, var1, this.optRand);
   }

   @Override
   public boolean verifySignature(byte[] var1, byte[] var2) {
      return SLHDSAEngine.internalVerifySignature(this.pubKey, this.msgPrefix, var1, var2);
   }

   protected boolean internalVerifySignature(byte[] var1, byte[] var2) {
      return SLHDSAEngine.internalVerifySignature(this.pubKey, null, var1, var2);
   }

   protected byte[] internalGenerateSignature(byte[] var1, byte[] var2) {
      return SLHDSAEngine.internalGenerateSignature(this.privKey, null, var1, var2);
   }
}
