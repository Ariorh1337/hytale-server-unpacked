package org.bouncycastle.jcajce.provider.kdf;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.util.SpiUtil;

public class HKDF {
   private static final String PREFIX = "org.bouncycastle.jcajce.provider.kdf.hkdf.";

   public static class Mappings extends KDFAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         if (SpiUtil.hasKDF()) {
            this.addKDFAlgorithm(
               var1, "HKDF-SHA256", "org.bouncycastle.jcajce.provider.kdf.hkdf.HKDFSpi$HKDFwithSHA256", PKCSObjectIdentifiers.id_alg_hkdf_with_sha256
            );
            this.addKDFAlgorithm(
               var1, "HKDF-SHA384", "org.bouncycastle.jcajce.provider.kdf.hkdf.HKDFSpi$HKDFwithSHA384", PKCSObjectIdentifiers.id_alg_hkdf_with_sha384
            );
            this.addKDFAlgorithm(
               var1, "HKDF-SHA512", "org.bouncycastle.jcajce.provider.kdf.hkdf.HKDFSpi$HKDFwithSHA512", PKCSObjectIdentifiers.id_alg_hkdf_with_sha512
            );
         }
      }
   }
}
