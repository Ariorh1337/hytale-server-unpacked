package org.bouncycastle.pqc.crypto.hqc;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class HQCKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private SecureRandom random;
   private HQCParameters parameters;

   @Override
   public void init(KeyGenerationParameters var1) {
      this.random = var1.getRandom();
      this.parameters = ((HQCKeyGenerationParameters)var1).getParameters();
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      byte[] var1 = new byte[this.parameters.getPublicKeyBytes()];
      byte[] var2 = new byte[this.parameters.getSecretKeyBytes()];
      this.parameters.getEngine().genKeyPair(var1, var2, this.random);
      HQCPublicKeyParameters var3 = new HQCPublicKeyParameters(this.parameters, var1);
      HQCPrivateKeyParameters var4 = new HQCPrivateKeyParameters(this.parameters, var2);
      return new AsymmetricCipherKeyPair(var3, var4);
   }
}
