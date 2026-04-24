package org.bouncycastle.jcajce.provider.kdf;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.util.SpiUtil;

public class SCRYPT {
   private static final String PREFIX = "org.bouncycastle.jcajce.provider.kdf.scrypt.";

   public static class Mappings extends KDFAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         if (SpiUtil.hasKDF()) {
            this.addKDFAlgorithm(var1, "SCRYPT", "org.bouncycastle.jcajce.provider.kdf.scrypt.ScryptSpi$ScryptWithUTF8");
         }
      }
   }
}
