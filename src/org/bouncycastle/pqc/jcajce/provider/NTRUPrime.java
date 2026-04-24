package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.jcajce.util.SpiUtil;
import org.bouncycastle.pqc.jcajce.provider.ntruprime.NTRULPRimeKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.ntruprime.SNTRUPrimeKeyFactorySpi;

public class NTRUPrime {
   private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.ntruprime.";

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("KeyFactory.NTRULPRIME", "org.bouncycastle.pqc.jcajce.provider.ntruprime.NTRULPRimeKeyFactorySpi");
         var1.addAlgorithm("KeyPairGenerator.NTRULPRIME", "org.bouncycastle.pqc.jcajce.provider.ntruprime.NTRULPRimeKeyPairGeneratorSpi");
         var1.addAlgorithm("KeyGenerator.NTRULPRIME", "org.bouncycastle.pqc.jcajce.provider.ntruprime.NTRULPRimeKeyGeneratorSpi");
         NTRULPRimeKeyFactorySpi var2 = new NTRULPRimeKeyFactorySpi();
         this.addCipherAlgorithm(
            var1, "NTRULPRIME", "org.bouncycastle.pqc.jcajce.provider.ntruprime.NTRULPRimeCipherSpi$Base", BCObjectIdentifiers.pqc_kem_ntrulprime
         );
         this.registerOid(var1, BCObjectIdentifiers.ntrulpr653, "NTRULPRIME", var2);
         this.registerOid(var1, BCObjectIdentifiers.ntrulpr761, "NTRULPRIME", var2);
         this.registerOid(var1, BCObjectIdentifiers.ntrulpr857, "NTRULPRIME", var2);
         this.registerOid(var1, BCObjectIdentifiers.ntrulpr953, "NTRULPRIME", var2);
         this.registerOid(var1, BCObjectIdentifiers.ntrulpr1013, "NTRULPRIME", var2);
         this.registerOid(var1, BCObjectIdentifiers.ntrulpr1277, "NTRULPRIME", var2);
         var1.addAlgorithm("KeyFactory.SNTRUPRIME", "org.bouncycastle.pqc.jcajce.provider.ntruprime.SNTRUPrimeKeyFactorySpi");
         var1.addAlgorithm("KeyPairGenerator.SNTRUPRIME", "org.bouncycastle.pqc.jcajce.provider.ntruprime.SNTRUPrimeKeyPairGeneratorSpi");
         var1.addAlgorithm("KeyGenerator.SNTRUPRIME", "org.bouncycastle.pqc.jcajce.provider.ntruprime.SNTRUPrimeKeyGeneratorSpi");
         SNTRUPrimeKeyFactorySpi var3 = new SNTRUPrimeKeyFactorySpi();
         this.addCipherAlgorithm(
            var1, "SNTRUPRIME", "org.bouncycastle.pqc.jcajce.provider.ntruprime.SNTRUPrimeCipherSpi$Base", BCObjectIdentifiers.pqc_kem_sntruprime
         );
         this.registerOid(var1, BCObjectIdentifiers.sntrup653, "SNTRUPRIME", var3);
         this.registerOid(var1, BCObjectIdentifiers.sntrup761, "SNTRUPRIME", var3);
         this.registerOid(var1, BCObjectIdentifiers.sntrup857, "SNTRUPRIME", var3);
         this.registerOid(var1, BCObjectIdentifiers.sntrup953, "SNTRUPRIME", var3);
         this.registerOid(var1, BCObjectIdentifiers.sntrup1013, "SNTRUPRIME", var3);
         this.registerOid(var1, BCObjectIdentifiers.sntrup1277, "SNTRUPRIME", var3);
         if (SpiUtil.hasKEM()) {
            this.addKEMAlgorithm(
               var1, "SNTRUPRIME", "org.bouncycastle.pqc.jcajce.provider.ntruprime.SNTRUPrimeKEMSpi$SNTRUPrime", BCObjectIdentifiers.pqc_kem_sntruprime
            );
         }
      }
   }
}
