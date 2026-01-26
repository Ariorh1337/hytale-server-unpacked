/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.mldsa.MLDSAKeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class MLDSA {
    private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.mldsa.";

    public static class Mappings
    extends AsymmetricAlgorithmProvider {
        @Override
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.ML-DSA", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.MLDSAKeyFactorySpi$Pure");
            configurableProvider.addAlgorithm("KeyPairGenerator.ML-DSA", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.MLDSAKeyPairGeneratorSpi$Pure");
            configurableProvider.addAlgorithm("Alg.Alias.KeyFactory.MLDSA", "ML-DSA");
            configurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.MLDSA", "ML-DSA");
            configurableProvider.addAlgorithm("KeyFactory.HASH-ML-DSA", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.MLDSAKeyFactorySpi$Hash");
            configurableProvider.addAlgorithm("KeyPairGenerator.HASH-ML-DSA", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.MLDSAKeyPairGeneratorSpi$Hash");
            configurableProvider.addAlgorithm("Alg.Alias.KeyFactory.SHA512WITHMLDSA", "HASH-ML-DSA");
            configurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator.SHA512WITHMLDSA", "HASH-ML-DSA");
            this.addKeyFactoryAlgorithm(configurableProvider, "ML-DSA-44", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.MLDSAKeyFactorySpi$MLDSA44", NISTObjectIdentifiers.id_ml_dsa_44, new MLDSAKeyFactorySpi.MLDSA44());
            this.addKeyFactoryAlgorithm(configurableProvider, "ML-DSA-65", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.MLDSAKeyFactorySpi$MLDSA65", NISTObjectIdentifiers.id_ml_dsa_65, new MLDSAKeyFactorySpi.MLDSA65());
            this.addKeyFactoryAlgorithm(configurableProvider, "ML-DSA-87", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.MLDSAKeyFactorySpi$MLDSA87", NISTObjectIdentifiers.id_ml_dsa_87, new MLDSAKeyFactorySpi.MLDSA87());
            this.addKeyFactoryAlgorithm(configurableProvider, "ML-DSA-44-WITH-SHA512", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.MLDSAKeyFactorySpi$HashMLDSA44", NISTObjectIdentifiers.id_hash_ml_dsa_44_with_sha512, new MLDSAKeyFactorySpi.HashMLDSA44());
            this.addKeyFactoryAlgorithm(configurableProvider, "ML-DSA-65-WITH-SHA512", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.MLDSAKeyFactorySpi$HashMLDSA65", NISTObjectIdentifiers.id_hash_ml_dsa_65_with_sha512, new MLDSAKeyFactorySpi.HashMLDSA65());
            this.addKeyFactoryAlgorithm(configurableProvider, "ML-DSA-87-WITH-SHA512", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.MLDSAKeyFactorySpi$HashMLDSA87", NISTObjectIdentifiers.id_hash_ml_dsa_87_with_sha512, new MLDSAKeyFactorySpi.HashMLDSA87());
            this.addKeyPairGeneratorAlgorithm(configurableProvider, "ML-DSA-44", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.MLDSAKeyPairGeneratorSpi$MLDSA44", NISTObjectIdentifiers.id_ml_dsa_44);
            this.addKeyPairGeneratorAlgorithm(configurableProvider, "ML-DSA-65", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.MLDSAKeyPairGeneratorSpi$MLDSA65", NISTObjectIdentifiers.id_ml_dsa_65);
            this.addKeyPairGeneratorAlgorithm(configurableProvider, "ML-DSA-87", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.MLDSAKeyPairGeneratorSpi$MLDSA87", NISTObjectIdentifiers.id_ml_dsa_87);
            this.addKeyPairGeneratorAlgorithm(configurableProvider, "ML-DSA-44-WITH-SHA512", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.MLDSAKeyPairGeneratorSpi$MLDSA44withSHA512", NISTObjectIdentifiers.id_hash_ml_dsa_44_with_sha512);
            this.addKeyPairGeneratorAlgorithm(configurableProvider, "ML-DSA-65-WITH-SHA512", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.MLDSAKeyPairGeneratorSpi$MLDSA65withSHA512", NISTObjectIdentifiers.id_hash_ml_dsa_65_with_sha512);
            this.addKeyPairGeneratorAlgorithm(configurableProvider, "ML-DSA-87-WITH-SHA512", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.MLDSAKeyPairGeneratorSpi$MLDSA87withSHA512", NISTObjectIdentifiers.id_hash_ml_dsa_87_with_sha512);
            this.addSignatureAlgorithm(configurableProvider, "ML-DSA", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.SignatureSpi$MLDSA", (ASN1ObjectIdentifier)null);
            this.addSignatureAlgorithm(configurableProvider, "ML-DSA-44", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.SignatureSpi$MLDSA44", NISTObjectIdentifiers.id_ml_dsa_44);
            this.addSignatureAlgorithm(configurableProvider, "ML-DSA-65", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.SignatureSpi$MLDSA65", NISTObjectIdentifiers.id_ml_dsa_65);
            this.addSignatureAlgorithm(configurableProvider, "ML-DSA-87", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.SignatureSpi$MLDSA87", NISTObjectIdentifiers.id_ml_dsa_87);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.MLDSA", "ML-DSA");
            this.addSignatureAlgorithm(configurableProvider, "ML-DSA-CALCULATE-MU", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.SignatureSpi$MLDSACalcMu", (ASN1ObjectIdentifier)null);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.MLDSA-CALCULATE-MU", "ML-DSA-CALCULATE-MU");
            this.addSignatureAlgorithm(configurableProvider, "ML-DSA-EXTERNAL-MU", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.SignatureSpi$MLDSAExtMu", (ASN1ObjectIdentifier)null);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.MLDSA-EXTERNAL-MU", "ML-DSA-EXTERNAL-MU");
            this.addSignatureAlgorithm(configurableProvider, "HASH-ML-DSA", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.HashSignatureSpi$MLDSA", (ASN1ObjectIdentifier)null);
            this.addSignatureAlgorithm(configurableProvider, "ML-DSA-44-WITH-SHA512", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.HashSignatureSpi$MLDSA44", NISTObjectIdentifiers.id_hash_ml_dsa_44_with_sha512);
            this.addSignatureAlgorithm(configurableProvider, "ML-DSA-65-WITH-SHA512", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.HashSignatureSpi$MLDSA65", NISTObjectIdentifiers.id_hash_ml_dsa_65_with_sha512);
            this.addSignatureAlgorithm(configurableProvider, "ML-DSA-87-WITH-SHA512", "org.bouncycastle.jcajce.provider.asymmetric.mldsa.HashSignatureSpi$MLDSA87", NISTObjectIdentifiers.id_hash_ml_dsa_87_with_sha512);
            configurableProvider.addAlgorithm("Alg.Alias.Signature.SHA512WITHMLDSA", "HASH-ML-DSA");
            configurableProvider.addAlgorithm("Alg.Alias.Signature.SHA512WITHMLDSA44", "ML-DSA-44-WITH-SHA512");
            configurableProvider.addAlgorithm("Alg.Alias.Signature.SHA512WITHMLDSA65", "ML-DSA-65-WITH-SHA512");
            configurableProvider.addAlgorithm("Alg.Alias.Signature.SHA512WITHMLDSA87", "ML-DSA-87-WITH-SHA512");
            MLDSAKeyFactorySpi.Hash hash = new MLDSAKeyFactorySpi.Hash();
            configurableProvider.addKeyInfoConverter(NISTObjectIdentifiers.id_ml_dsa_44, hash);
            configurableProvider.addKeyInfoConverter(NISTObjectIdentifiers.id_ml_dsa_65, hash);
            configurableProvider.addKeyInfoConverter(NISTObjectIdentifiers.id_ml_dsa_87, hash);
            configurableProvider.addKeyInfoConverter(NISTObjectIdentifiers.id_hash_ml_dsa_44_with_sha512, hash);
            configurableProvider.addKeyInfoConverter(NISTObjectIdentifiers.id_hash_ml_dsa_65_with_sha512, hash);
            configurableProvider.addKeyInfoConverter(NISTObjectIdentifiers.id_hash_ml_dsa_87_with_sha512, hash);
        }
    }
}

