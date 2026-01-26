/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyFactorySpi;

public class SPHINCSPlus {
    private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.sphincsplus.";

    public static class Mappings
    extends AsymmetricAlgorithmProvider {
        @Override
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.SPHINCSPLUS", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyFactorySpi");
            configurableProvider.addAlgorithm("KeyPairGenerator.SPHINCSPLUS", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi");
            configurableProvider.addAlgorithm("Alg.Alias.KeyFactory.SPHINCS+", "SPHINCSPLUS");
            configurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.SPHINCS+", "SPHINCSPLUS");
            this.addKeyPairGeneratorAlgorithm(configurableProvider, "SPHINCS+-SHA2-128S", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi$Sha2_128s", BCObjectIdentifiers.sphincsPlus_sha2_128s);
            this.addKeyPairGeneratorAlgorithm(configurableProvider, "SPHINCS+-SHA2-128F", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi$Sha2_128f", BCObjectIdentifiers.sphincsPlus_sha2_128f);
            this.addKeyPairGeneratorAlgorithm(configurableProvider, "SPHINCS+-SHA2-192S", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi$Sha2_192s", BCObjectIdentifiers.sphincsPlus_sha2_192s);
            this.addKeyPairGeneratorAlgorithm(configurableProvider, "SPHINCS+-SHA2-192F", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi$Sha2_192f", BCObjectIdentifiers.sphincsPlus_sha2_192f);
            this.addKeyPairGeneratorAlgorithm(configurableProvider, "SPHINCS+-SHA2-256S", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi$Sha2_256s", BCObjectIdentifiers.sphincsPlus_sha2_256s);
            this.addKeyPairGeneratorAlgorithm(configurableProvider, "SPHINCS+-SHA2-256F", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi$Sha2_256f", BCObjectIdentifiers.sphincsPlus_sha2_256f);
            this.addKeyPairGeneratorAlgorithm(configurableProvider, "SPHINCS+-SHAKE-128S", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi$Shake_128s", BCObjectIdentifiers.sphincsPlus_shake_128s);
            this.addKeyPairGeneratorAlgorithm(configurableProvider, "SPHINCS+-SHAKE-128F", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi$Shake_128f", BCObjectIdentifiers.sphincsPlus_shake_128f);
            this.addKeyPairGeneratorAlgorithm(configurableProvider, "SPHINCS+-SHAKE-192S", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi$Shake_192s", BCObjectIdentifiers.sphincsPlus_shake_192s);
            this.addKeyPairGeneratorAlgorithm(configurableProvider, "SPHINCS+-SHAKE-192F", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi$Shake_192f", BCObjectIdentifiers.sphincsPlus_shake_192f);
            this.addKeyPairGeneratorAlgorithm(configurableProvider, "SPHINCS+-SHAKE-256S", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi$Shake_256s", BCObjectIdentifiers.sphincsPlus_shake_256s);
            this.addKeyPairGeneratorAlgorithm(configurableProvider, "SPHINCS+-SHAKE-256F", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyPairGeneratorSpi$Shake_256f", BCObjectIdentifiers.sphincsPlus_shake_256f);
            this.addSignatureAlgorithm(configurableProvider, "SPHINCSPLUS", "org.bouncycastle.pqc.jcajce.provider.sphincsplus.SignatureSpi$Direct", BCObjectIdentifiers.sphincsPlus);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_sha2_128f_r3);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_shake_128s_r3);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_shake_128f_r3);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_haraka_128s_r3);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_haraka_128f_r3);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_sha2_192s_r3);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_sha2_192f_r3);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_shake_192s_r3);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_shake_192f_r3);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_haraka_192s_r3);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_haraka_192f_r3);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_sha2_256s_r3);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_sha2_256f_r3);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_shake_256s_r3);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_shake_256f_r3);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_haraka_256s_r3);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_haraka_256f_r3);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_sha2_128s_r3_simple);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_sha2_128f_r3_simple);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_shake_128s_r3_simple);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_shake_128f_r3_simple);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_haraka_128s_r3_simple);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_haraka_128f_r3_simple);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_sha2_192s_r3_simple);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_sha2_192f_r3_simple);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_shake_192s_r3_simple);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_shake_192f_r3_simple);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_haraka_192s_r3_simple);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_haraka_192f_r3_simple);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_sha2_256s_r3_simple);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_sha2_256f_r3_simple);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_shake_256s_r3_simple);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_shake_256f_r3_simple);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_haraka_256s_r3_simple);
            this.addSignatureAlias(configurableProvider, "SPHINCSPLUS", BCObjectIdentifiers.sphincsPlus_haraka_256f_r3_simple);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.SPHINCS+", "SPHINCSPLUS");
            SPHINCSPlusKeyFactorySpi sPHINCSPlusKeyFactorySpi = new SPHINCSPlusKeyFactorySpi();
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_sha2_128s_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_sha2_128f_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_shake_128s_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_shake_128f_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_haraka_128s_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_haraka_128f_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_sha2_192s_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_sha2_192f_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_shake_192s_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_shake_192f_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_haraka_192s_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_haraka_192f_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_sha2_256s_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_sha2_256f_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_shake_256s_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_shake_256f_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_haraka_256s_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_haraka_256f_r3, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_sha2_128s_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_sha2_128f_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_shake_128s_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_shake_128f_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_haraka_128s_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_haraka_128f_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_sha2_192s_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_sha2_192f_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_shake_192s_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_shake_192f_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_haraka_192s_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_haraka_192f_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_sha2_256s_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_sha2_256f_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_shake_256s_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_shake_256f_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_haraka_256s_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
            this.registerKeyFactoryOid(configurableProvider, BCObjectIdentifiers.sphincsPlus_haraka_256f_r3_simple, "SPHINCSPLUS", sPHINCSPlusKeyFactorySpi);
        }
    }
}

