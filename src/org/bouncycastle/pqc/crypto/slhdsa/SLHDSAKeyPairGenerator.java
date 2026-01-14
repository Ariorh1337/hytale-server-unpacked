package org.bouncycastle.pqc.crypto.slhdsa;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class SLHDSAKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private SecureRandom random;
   private SLHDSAParameters parameters;

   @Override
   public void init(KeyGenerationParameters var1) {
      this.random = var1.getRandom();
      this.parameters = ((SLHDSAKeyGenerationParameters)var1).getParameters();
   }

   public AsymmetricCipherKeyPair internalGenerateKeyPair(byte[] var1, byte[] var2, byte[] var3) {
      return this.implGenerateKeyPair(this.parameters.getEngine(), var1, var2, var3);
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      SLHDSAEngine var1 = this.parameters.getEngine();
      byte[] var2 = this.sec_rand(var1.N);
      byte[] var3 = this.sec_rand(var1.N);
      byte[] var4 = this.sec_rand(var1.N);
      return this.implGenerateKeyPair(var1, var2, var3, var4);
   }

   private AsymmetricCipherKeyPair implGenerateKeyPair(SLHDSAEngine var1, byte[] var2, byte[] var3, byte[] var4) {
      SK var5 = new SK(var2, var3);
      var1.init(var4);
      PK var6 = new PK(var4, (new HT(var1, var5.seed, var4)).htPubKey);
      return new AsymmetricCipherKeyPair(new SLHDSAPublicKeyParameters(this.parameters, var6), new SLHDSAPrivateKeyParameters(this.parameters, var5, var6));
   }

   private byte[] sec_rand(int var1) {
      byte[] var2 = new byte[var1];
      this.random.nextBytes(var2);
      return var2;
   }
}
