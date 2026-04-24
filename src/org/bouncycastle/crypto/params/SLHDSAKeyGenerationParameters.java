package org.bouncycastle.crypto.params;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class SLHDSAKeyGenerationParameters extends KeyGenerationParameters {
   private final SLHDSAParameters parameters;

   public SLHDSAKeyGenerationParameters(SecureRandom var1, SLHDSAParameters var2) {
      super(var1, -1);
      this.parameters = var2;
   }

   public SLHDSAParameters getParameters() {
      return this.parameters;
   }
}
