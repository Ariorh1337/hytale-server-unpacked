/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider;

import java.security.KeyFactorySpi;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.pqc.jcajce.provider.ntruprime.NTRULPRimeKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.ntruprime.SNTRUPrimeKeyFactorySpi;

public class NTRUPrime {
    private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.ntruprime.";

    public static class Mappings
    extends AsymmetricAlgorithmProvider {
        @Override
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.NTRULPRIME", "org.bouncycastle.pqc.jcajce.provider.ntruprime.NTRULPRimeKeyFactorySpi");
            configurableProvider.addAlgorithm("KeyPairGenerator.NTRULPRIME", "org.bouncycastle.pqc.jcajce.provider.ntruprime.NTRULPRimeKeyPairGeneratorSpi");
            configurableProvider.addAlgorithm("KeyGenerator.NTRULPRIME", "org.bouncycastle.pqc.jcajce.provider.ntruprime.NTRULPRimeKeyGeneratorSpi");
            KeyFactorySpi keyFactorySpi = new NTRULPRimeKeyFactorySpi();
            configurableProvider.addAlgorithm("Cipher.NTRULPRIME", "org.bouncycastle.pqc.jcajce.provider.ntruprime.NTRULPRimeCipherSpi$Base");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher." + BCObjectIdentifiers.pqc_kem_ntrulprime, "NTRU");
            this.registerOid(configurableProvider, BCObjectIdentifiers.ntrulpr653, "NTRULPRIME", (AsymmetricKeyInfoConverter)((Object)keyFactorySpi));
            this.registerOid(configurableProvider, BCObjectIdentifiers.ntrulpr761, "NTRULPRIME", (AsymmetricKeyInfoConverter)((Object)keyFactorySpi));
            this.registerOid(configurableProvider, BCObjectIdentifiers.ntrulpr857, "NTRULPRIME", (AsymmetricKeyInfoConverter)((Object)keyFactorySpi));
            this.registerOid(configurableProvider, BCObjectIdentifiers.ntrulpr953, "NTRULPRIME", (AsymmetricKeyInfoConverter)((Object)keyFactorySpi));
            this.registerOid(configurableProvider, BCObjectIdentifiers.ntrulpr1013, "NTRULPRIME", (AsymmetricKeyInfoConverter)((Object)keyFactorySpi));
            this.registerOid(configurableProvider, BCObjectIdentifiers.ntrulpr1277, "NTRULPRIME", (AsymmetricKeyInfoConverter)((Object)keyFactorySpi));
            configurableProvider.addAlgorithm("KeyFactory.SNTRUPRIME", "org.bouncycastle.pqc.jcajce.provider.ntruprime.SNTRUPrimeKeyFactorySpi");
            configurableProvider.addAlgorithm("KeyPairGenerator.SNTRUPRIME", "org.bouncycastle.pqc.jcajce.provider.ntruprime.SNTRUPrimeKeyPairGeneratorSpi");
            configurableProvider.addAlgorithm("KeyGenerator.SNTRUPRIME", "org.bouncycastle.pqc.jcajce.provider.ntruprime.SNTRUPrimeKeyGeneratorSpi");
            keyFactorySpi = new SNTRUPrimeKeyFactorySpi();
            configurableProvider.addAlgorithm("Cipher.SNTRUPRIME", "org.bouncycastle.pqc.jcajce.provider.ntruprime.SNTRUPrimeCipherSpi$Base");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher." + BCObjectIdentifiers.pqc_kem_sntruprime, "NTRU");
            this.registerOid(configurableProvider, BCObjectIdentifiers.sntrup653, "SNTRUPRIME", (AsymmetricKeyInfoConverter)((Object)keyFactorySpi));
            this.registerOid(configurableProvider, BCObjectIdentifiers.sntrup761, "SNTRUPRIME", (AsymmetricKeyInfoConverter)((Object)keyFactorySpi));
            this.registerOid(configurableProvider, BCObjectIdentifiers.sntrup857, "SNTRUPRIME", (AsymmetricKeyInfoConverter)((Object)keyFactorySpi));
            this.registerOid(configurableProvider, BCObjectIdentifiers.sntrup953, "SNTRUPRIME", (AsymmetricKeyInfoConverter)((Object)keyFactorySpi));
            this.registerOid(configurableProvider, BCObjectIdentifiers.sntrup1013, "SNTRUPRIME", (AsymmetricKeyInfoConverter)((Object)keyFactorySpi));
            this.registerOid(configurableProvider, BCObjectIdentifiers.sntrup1277, "SNTRUPRIME", (AsymmetricKeyInfoConverter)((Object)keyFactorySpi));
        }
    }
}

