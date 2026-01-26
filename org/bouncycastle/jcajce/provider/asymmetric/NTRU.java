/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.jcajce.provider.ntru.NTRUKeyFactorySpi;

public class NTRU {
    private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.ntru.";

    public static class Mappings
    extends AsymmetricAlgorithmProvider {
        @Override
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.NTRU", "org.bouncycastle.pqc.jcajce.provider.ntru.NTRUKeyFactorySpi");
            configurableProvider.addAlgorithm("KeyPairGenerator.NTRU", "org.bouncycastle.pqc.jcajce.provider.ntru.NTRUKeyPairGeneratorSpi");
            configurableProvider.addAlgorithm("KeyGenerator.NTRU", "org.bouncycastle.pqc.jcajce.provider.ntru.NTRUKeyGeneratorSpi");
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator." + BCObjectIdentifiers.pqc_kem_ntru, "NTRU");
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator." + BCObjectIdentifiers.ntruhps2048509, "NTRU");
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator." + BCObjectIdentifiers.ntruhps2048677, "NTRU");
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator." + BCObjectIdentifiers.ntruhps4096821, "NTRU");
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator." + BCObjectIdentifiers.ntruhps40961229, "NTRU");
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator." + BCObjectIdentifiers.ntruhrss701, "NTRU");
            configurableProvider.addAlgorithm("Alg.Alias.KeyGenerator." + BCObjectIdentifiers.ntruhrss1373, "NTRU");
            NTRUKeyFactorySpi nTRUKeyFactorySpi = new NTRUKeyFactorySpi();
            configurableProvider.addAlgorithm("Cipher.NTRU", "org.bouncycastle.pqc.jcajce.provider.ntru.NTRUCipherSpi$Base");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher." + BCObjectIdentifiers.pqc_kem_ntru, "NTRU");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher." + BCObjectIdentifiers.ntruhps2048509, "NTRU");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher." + BCObjectIdentifiers.ntruhps2048677, "NTRU");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher." + BCObjectIdentifiers.ntruhps4096821, "NTRU");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher." + BCObjectIdentifiers.ntruhps40961229, "NTRU");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher." + BCObjectIdentifiers.ntruhrss701, "NTRU");
            configurableProvider.addAlgorithm("Alg.Alias.Cipher." + BCObjectIdentifiers.ntruhrss1373, "NTRU");
            this.registerOid(configurableProvider, BCObjectIdentifiers.pqc_kem_ntru, "NTRU", nTRUKeyFactorySpi);
            this.registerOid(configurableProvider, BCObjectIdentifiers.ntruhps2048509, "NTRU", nTRUKeyFactorySpi);
            this.registerOid(configurableProvider, BCObjectIdentifiers.ntruhps2048677, "NTRU", nTRUKeyFactorySpi);
            this.registerOid(configurableProvider, BCObjectIdentifiers.ntruhps4096821, "NTRU", nTRUKeyFactorySpi);
            this.registerOid(configurableProvider, BCObjectIdentifiers.ntruhps40961229, "NTRU", nTRUKeyFactorySpi);
            this.registerOid(configurableProvider, BCObjectIdentifiers.ntruhrss701, "NTRU", nTRUKeyFactorySpi);
            this.registerOid(configurableProvider, BCObjectIdentifiers.ntruhrss1373, "NTRU", nTRUKeyFactorySpi);
        }
    }
}

