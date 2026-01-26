/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.jcajce.provider.picnic.PicnicKeyFactorySpi;

public class Picnic {
    private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.picnic.";

    public static class Mappings
    extends AsymmetricAlgorithmProvider {
        @Override
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.PICNIC", "org.bouncycastle.pqc.jcajce.provider.picnic.PicnicKeyFactorySpi");
            configurableProvider.addAlgorithm("KeyPairGenerator.PICNIC", "org.bouncycastle.pqc.jcajce.provider.picnic.PicnicKeyPairGeneratorSpi");
            this.addSignatureAlgorithm(configurableProvider, "PICNIC", "org.bouncycastle.pqc.jcajce.provider.picnic.SignatureSpi$Base", BCObjectIdentifiers.picnic_signature);
            this.addSignatureAlgorithm(configurableProvider, "SHAKE256", "PICNIC", "org.bouncycastle.pqc.jcajce.provider.picnic.SignatureSpi$withShake256", BCObjectIdentifiers.picnic_with_shake256);
            this.addSignatureAlgorithm(configurableProvider, "SHA512", "PICNIC", "org.bouncycastle.pqc.jcajce.provider.picnic.SignatureSpi$withSha512", BCObjectIdentifiers.picnic_with_sha512);
            this.addSignatureAlgorithm(configurableProvider, "SHA3-512", "PICNIC", "org.bouncycastle.pqc.jcajce.provider.picnic.SignatureSpi$withSha3512", BCObjectIdentifiers.picnic_with_sha3_512);
            PicnicKeyFactorySpi picnicKeyFactorySpi = new PicnicKeyFactorySpi();
            this.registerOid(configurableProvider, BCObjectIdentifiers.picnic_key, "Picnic", picnicKeyFactorySpi);
            this.registerOid(configurableProvider, BCObjectIdentifiers.picnicl1fs, "Picnic", picnicKeyFactorySpi);
            this.registerOid(configurableProvider, BCObjectIdentifiers.picnicl1ur, "Picnic", picnicKeyFactorySpi);
            this.registerOid(configurableProvider, BCObjectIdentifiers.picnicl3fs, "Picnic", picnicKeyFactorySpi);
            this.registerOid(configurableProvider, BCObjectIdentifiers.picnicl3ur, "Picnic", picnicKeyFactorySpi);
            this.registerOid(configurableProvider, BCObjectIdentifiers.picnicl5fs, "Picnic", picnicKeyFactorySpi);
            this.registerOid(configurableProvider, BCObjectIdentifiers.picnicl5ur, "Picnic", picnicKeyFactorySpi);
            this.registerOid(configurableProvider, BCObjectIdentifiers.picnic3l1, "Picnic", picnicKeyFactorySpi);
            this.registerOid(configurableProvider, BCObjectIdentifiers.picnic3l3, "Picnic", picnicKeyFactorySpi);
            this.registerOid(configurableProvider, BCObjectIdentifiers.picnic3l5, "Picnic", picnicKeyFactorySpi);
            this.registerOid(configurableProvider, BCObjectIdentifiers.picnicl1full, "Picnic", picnicKeyFactorySpi);
            this.registerOid(configurableProvider, BCObjectIdentifiers.picnicl3full, "Picnic", picnicKeyFactorySpi);
            this.registerOid(configurableProvider, BCObjectIdentifiers.picnicl5full, "Picnic", picnicKeyFactorySpi);
        }
    }
}

