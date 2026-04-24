package org.bouncycastle.jcajce.provider.kdf;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.jcajce.util.SpiUtil;

public class PBKDF2 {
   private static final String PREFIX = "org.bouncycastle.jcajce.provider.kdf.pbkdf2.";

   public static class Mappings extends AlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         if (SpiUtil.hasKDF()) {
            var1.addAlgorithm("KDF.PBKDF2", "org.bouncycastle.jcajce.provider.kdf.pbkdf2.PBKDF2Spi$PBKDF2withUTF8");
            var1.addAlgorithm("Alg.Alias.KDF.PBKDF2WITHHMACSHA1", "PBKDF2");
            var1.addAlgorithm("Alg.Alias.KDF.PBKDF2WITHHMACSHA1ANDUTF8", "PBKDF2");
            var1.addAlgorithm("KDF", PKCSObjectIdentifiers.id_PBKDF2, "org.bouncycastle.jcajce.provider.kdf.pbkdf2.PBKDF2Spi$PBKDF2withUTF8");
            var1.addAlgorithm("KDF.PBKDF2WITHASCII", "org.bouncycastle.jcajce.provider.kdf.pbkdf2.PBKDF2Spi$PBKDF2with8BIT");
            var1.addAlgorithm("Alg.Alias.KDF.PBKDF2WITH8BIT", "PBKDF2WITHASCII");
            var1.addAlgorithm("Alg.Alias.KDF.PBKDF2WITHHMACSHA1AND8BIT", "PBKDF2WITHASCII");
            var1.addAlgorithm("KDF.PBKDF2WITHHMACSHA224", "org.bouncycastle.jcajce.provider.kdf.pbkdf2.PBKDF2Spi$PBKDF2withSHA224");
            var1.addAlgorithm("KDF.PBKDF2WITHHMACSHA256", "org.bouncycastle.jcajce.provider.kdf.pbkdf2.PBKDF2Spi$PBKDF2withSHA256");
            var1.addAlgorithm("KDF.PBKDF2WITHHMACSHA384", "org.bouncycastle.jcajce.provider.kdf.pbkdf2.PBKDF2Spi$PBKDF2withSHA384");
            var1.addAlgorithm("KDF.PBKDF2WITHHMACSHA512", "org.bouncycastle.jcajce.provider.kdf.pbkdf2.PBKDF2Spi$PBKDF2withSHA512");
            var1.addAlgorithm("KDF.PBKDF2WITHHMACSHA512-224", "org.bouncycastle.jcajce.provider.kdf.pbkdf2.PBKDF2Spi$PBKDF2withSHA512_224");
            var1.addAlgorithm("KDF.PBKDF2WITHHMACSHA512-256", "org.bouncycastle.jcajce.provider.kdf.pbkdf2.PBKDF2Spi$PBKDF2withSHA512_256");
            var1.addAlgorithm("KDF.PBKDF2WITHHMACSHA3-224", "org.bouncycastle.jcajce.provider.kdf.pbkdf2.PBKDF2Spi$PBKDF2withSHA3_224");
            var1.addAlgorithm("KDF.PBKDF2WITHHMACSHA3-256", "org.bouncycastle.jcajce.provider.kdf.pbkdf2.PBKDF2Spi$PBKDF2withSHA3_256");
            var1.addAlgorithm("KDF.PBKDF2WITHHMACSHA3-384", "org.bouncycastle.jcajce.provider.kdf.pbkdf2.PBKDF2Spi$PBKDF2withSHA3_384");
            var1.addAlgorithm("KDF.PBKDF2WITHHMACSHA3-512", "org.bouncycastle.jcajce.provider.kdf.pbkdf2.PBKDF2Spi$PBKDF2withSHA3_512");
            var1.addAlgorithm("KDF.PBKDF2WITHHMACGOST3411", "org.bouncycastle.jcajce.provider.kdf.pbkdf2.PBKDF2Spi$PBKDF2withGOST3411");
            var1.addAlgorithm("KDF.PBKDF2WITHHMACSM3", "org.bouncycastle.jcajce.provider.kdf.pbkdf2.PBKDF2Spi$PBKDF2withSM3");
         }
      }
   }
}
