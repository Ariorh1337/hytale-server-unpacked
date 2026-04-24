package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.jcajce.provider.ntruplus.NTRUPlusKeyFactorySpi;

public class NTRUPlus {
   private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.ntruplus.";

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("KeyFactory.NTRUPLUS", "org.bouncycastle.pqc.jcajce.provider.ntruplus.NTRUPlusKeyFactorySpi");
         var1.addAlgorithm("Alg.Alias.KeyFactory.NTRUPLUS", "NTRUPLUS");
         this.addKeyFactoryAlgorithm(
            var1,
            "NTRU+KEM-768",
            "org.bouncycastle.pqc.jcajce.provider.ntruplus.NTRUPlusKeyFactorySpi$NTRUPlus768",
            BCObjectIdentifiers.ntruplus768,
            new NTRUPlusKeyFactorySpi.NTRUPlus768()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "NTRU+KEM-864",
            "org.bouncycastle.pqc.jcajce.provider.ntruplus.NTRUPlusKeyFactorySpi$NTRUPlus864",
            BCObjectIdentifiers.ntruplus864,
            new NTRUPlusKeyFactorySpi.NTRUPlus864()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "NTRU+KEM-1152",
            "org.bouncycastle.pqc.jcajce.provider.ntruplus.NTRUPlusKeyFactorySpi$NTRUPlus1152",
            BCObjectIdentifiers.ntruplus1152,
            new NTRUPlusKeyFactorySpi.NTRUPlus1152()
         );
         var1.addAlgorithm("KeyPairGenerator.NTRUPLUS", "org.bouncycastle.pqc.jcajce.provider.ntruplus.NTRUPlusKeyPairGeneratorSpi");
         var1.addAlgorithm("Alg.Alias.KeyPairGenerator.NTRUPLUS", "NTRUPLUS");
         this.addKeyPairGeneratorAlgorithm(
            var1, "NTRU+KEM-768", "org.bouncycastle.pqc.jcajce.provider.ntruplus.NTRUPlusKeyPairGeneratorSpi$NTRUPlus768", BCObjectIdentifiers.ntruplus768
         );
         this.addKeyPairGeneratorAlgorithm(
            var1, "NTRU+KEM-864", "org.bouncycastle.pqc.jcajce.provider.ntruplus.NTRUPlusKeyPairGeneratorSpi$NTRUPlus864", BCObjectIdentifiers.ntruplus864
         );
         this.addKeyPairGeneratorAlgorithm(
            var1, "NTRU+KEM-1152", "org.bouncycastle.pqc.jcajce.provider.ntruplus.NTRUPlusKeyPairGeneratorSpi$NTRUPlus1152", BCObjectIdentifiers.ntruplus1152
         );
         var1.addAlgorithm("KeyGenerator.NTRUPLUS", "org.bouncycastle.pqc.jcajce.provider.ntruplus.NTRUPlusKeyGeneratorSpi");
         this.addKeyGeneratorAlgorithm(
            var1, "NTRU+KEM-768", "org.bouncycastle.pqc.jcajce.provider.ntruplus.NTRUPLUSKeyGeneratorSpi$NTRUPLUS768", BCObjectIdentifiers.ntruplus768
         );
         this.addKeyGeneratorAlgorithm(
            var1, "NTRU+KEM-864", "org.bouncycastle.pqc.jcajce.provider.ntruplus.NTRUPLUSKeyGeneratorSpi$NTRUPLUS864", BCObjectIdentifiers.ntruplus864
         );
         this.addKeyGeneratorAlgorithm(
            var1, "NTRU+KEM-1152", "org.bouncycastle.pqc.jcajce.provider.ntruplus.NTRUPLUSKeyGeneratorSpi$NTRUPLUS1152", BCObjectIdentifiers.ntruplus1152
         );
         NTRUPlusKeyFactorySpi var2 = new NTRUPlusKeyFactorySpi();
         var1.addAlgorithm("Cipher.NTRUPLUS", "org.bouncycastle.pqc.jcajce.provider.ntruplus.NTRUPlusCipherSpi$Base");
         var1.addAlgorithm("Alg.Alias.Cipher.NTRUPLUS", "NTRUPLUS");
         var1.addAlgorithm("Alg.Alias.Cipher." + BCObjectIdentifiers.pqc_kem_ntruplus, "NTRUPLUS");
         this.addCipherAlgorithm(
            var1, "NTRU+KEM-768", "org.bouncycastle.pqc.jcajce.provider.ntruplus.NTRUPLUSCipherSpi$NTRUPLUS768", BCObjectIdentifiers.ntruplus768
         );
         this.addCipherAlgorithm(
            var1, "NTRU+KEM-864", "org.bouncycastle.pqc.jcajce.provider.ntruplus.NTRUPLUSCipherSpi$NTRUPLUS864", BCObjectIdentifiers.ntruplus864
         );
         this.addCipherAlgorithm(
            var1, "NTRU+KEM-1152", "org.bouncycastle.pqc.jcajce.provider.ntruplus.NTRUPLUSCipherSpi$NTRUPLUS1152", BCObjectIdentifiers.ntruplus1152
         );
         this.registerOid(var1, BCObjectIdentifiers.pqc_kem_ntruplus, "NTRUPLUS", var2);
         var1.addKeyInfoConverter(BCObjectIdentifiers.ntruplus768, var2);
         var1.addKeyInfoConverter(BCObjectIdentifiers.ntruplus864, var2);
         var1.addKeyInfoConverter(BCObjectIdentifiers.ntruplus1152, var2);
      }
   }
}
