/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyFactorySpi;

public class HQC {
    private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.hqc.";

    public static class Mappings
    extends AsymmetricAlgorithmProvider {
        @Override
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.HQC", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyFactorySpi");
            configurableProvider.addAlgorithm("Alg.Alias.KeyFactory.HQC", "HQC");
            this.addKeyFactoryAlgorithm(configurableProvider, "HQC128", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyFactorySpi$HQC128", BCObjectIdentifiers.hqc128, new HQCKeyFactorySpi.HQC128());
            this.addKeyFactoryAlgorithm(configurableProvider, "HQC192", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyFactorySpi$HQC192", BCObjectIdentifiers.hqc192, new HQCKeyFactorySpi.HQC192());
            this.addKeyFactoryAlgorithm(configurableProvider, "HQC256", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyFactorySpi$HQC256", BCObjectIdentifiers.hqc256, new HQCKeyFactorySpi.HQC256());
            configurableProvider.addAlgorithm("KeyPairGenerator.HQC", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyPairGeneratorSpi");
            configurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.HQC", "HQC");
            this.addKeyPairGeneratorAlgorithm(configurableProvider, "HQC128", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyPairGeneratorSpi$HQC128", BCObjectIdentifiers.hqc128);
            this.addKeyPairGeneratorAlgorithm(configurableProvider, "HQC192", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyPairGeneratorSpi$HQC192", BCObjectIdentifiers.hqc192);
            this.addKeyPairGeneratorAlgorithm(configurableProvider, "HQC256", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyPairGeneratorSpi$HQC256", BCObjectIdentifiers.hqc256);
            configurableProvider.addAlgorithm("KeyGenerator.HQC", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyGeneratorSpi");
            this.addKeyGeneratorAlgorithm(configurableProvider, "HQC128", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyGeneratorSpi$HQC128", BCObjectIdentifiers.hqc128);
            this.addKeyGeneratorAlgorithm(configurableProvider, "HQC192", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyGeneratorSpi$HQC192", BCObjectIdentifiers.hqc192);
            this.addKeyGeneratorAlgorithm(configurableProvider, "HQC256", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyGeneratorSpi$HQC256", BCObjectIdentifiers.hqc256);
            HQCKeyFactorySpi hQCKeyFactorySpi = new HQCKeyFactorySpi();
            configurableProvider.addAlgorithm("Cipher.HQC", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCCipherSpi$Base");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher.HQC", "HQC");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher." + BCObjectIdentifiers.pqc_kem_hqc, "HQC");
            this.addCipherAlgorithm(configurableProvider, "HQC128", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCCipherSpi$HQC128", BCObjectIdentifiers.hqc128);
            this.addCipherAlgorithm(configurableProvider, "HQC192", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCCipherSpi$HQC192", BCObjectIdentifiers.hqc192);
            this.addCipherAlgorithm(configurableProvider, "HQC256", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCCipherSpi$HQC256", BCObjectIdentifiers.hqc256);
            this.registerOid(configurableProvider, BCObjectIdentifiers.pqc_kem_hqc, "HQC", hQCKeyFactorySpi);
            configurableProvider.addKeyInfoConverter(BCObjectIdentifiers.hqc128, hQCKeyFactorySpi);
            configurableProvider.addKeyInfoConverter(BCObjectIdentifiers.hqc192, hQCKeyFactorySpi);
            configurableProvider.addKeyInfoConverter(BCObjectIdentifiers.hqc256, hQCKeyFactorySpi);
        }
    }
}

