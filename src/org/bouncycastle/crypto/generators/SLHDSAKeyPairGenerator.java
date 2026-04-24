package org.bouncycastle.crypto.generators;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.SLHDSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.SLHDSAParameters;
import org.bouncycastle.crypto.signers.slhdsa.SLHDSAEngine;

public class SLHDSAKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private SecureRandom random;
   private SLHDSAParameters parameters;

   @Override
   public void init(KeyGenerationParameters var1) {
      this.random = var1.getRandom();
      this.parameters = ((SLHDSAKeyGenerationParameters)var1).getParameters();
   }

   public AsymmetricCipherKeyPair internalGenerateKeyPair(byte[] var1, byte[] var2, byte[] var3) {
      return SLHDSAEngine.implGenerateKeyPair(this.parameters, var1, var2, var3);
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      byte[] var1 = this.sec_rand(this.parameters.getN());
      byte[] var2 = this.sec_rand(this.parameters.getN());
      byte[] var3 = this.sec_rand(this.parameters.getN());
      return SLHDSAEngine.implGenerateKeyPair(this.parameters, var1, var2, var3);
   }

   private byte[] sec_rand(int var1) {
      byte[] var2 = new byte[var1];
      this.random.nextBytes(var2);
      return var2;
   }
}
