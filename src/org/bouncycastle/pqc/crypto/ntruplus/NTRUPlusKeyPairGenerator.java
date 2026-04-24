package org.bouncycastle.pqc.crypto.ntruplus;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class NTRUPlusKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private NTRUPlusParameters params;
   private SecureRandom random;

   @Override
   public void init(KeyGenerationParameters var1) {
      this.params = ((NTRUPlusKeyGenerationParameters)var1).getParameters();
      this.random = var1.getRandom();
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      byte[] var1 = new byte[this.params.getPublicKeyBytes()];
      byte[] var2 = new byte[this.params.getSecretKeyBytes()];
      NTRUPlusEngine var3 = new NTRUPlusEngine(this.params);
      byte[] var4 = new byte[32];
      int var5 = this.params.getN();
      short[] var6 = new short[var5];
      short[] var7 = new short[var5];
      short[] var8 = new short[var5];
      short[] var9 = new short[var5];

      boolean var10;
      do {
         this.random.nextBytes(var4);
         var10 = var3.genf_derand(var6, var7, var4) == 0;
      } while (!var10);

      boolean var11;
      do {
         this.random.nextBytes(var4);
         var11 = var3.geng_derand(var8, var9, var4) == 0;
      } while (!var11);

      var3.crypto_kem_keypair_derand(var1, var2, var6, var7, var8, var9);
      return new AsymmetricCipherKeyPair(new NTRUPlusPublicKeyParameters(this.params, var1), new NTRUPlusPrivateKeyParameters(this.params, var2));
   }
}
