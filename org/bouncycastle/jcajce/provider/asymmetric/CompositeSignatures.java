/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.CompositeIndex;
import org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class CompositeSignatures {
    private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.";
    private static final Map<String, String> compositesAttributes = new HashMap<String, String>();

    static {
        compositesAttributes.put("SupportedKeyClasses", "org.bouncycastle.jcajce.CompositePublicKey|org.bouncycastle.jcajce.CompositePrivateKey");
        compositesAttributes.put("SupportedKeyFormats", "PKCS#8|X.509");
    }

    public static class Mappings
    extends AsymmetricAlgorithmProvider {
        @Override
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Signature.COMPOSITE", "org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.SignatureSpi$COMPOSITE");
            for (ASN1ObjectIdentifier aSN1ObjectIdentifier : CompositeIndex.getSupportedIdentifiers()) {
                String string = CompositeIndex.getAlgorithmName(aSN1ObjectIdentifier);
                String string2 = string.replace('-', '_');
                configurableProvider.addAlgorithm("Alg.Alias.KeyFactory", aSN1ObjectIdentifier, "COMPOSITE");
                configurableProvider.addAlgorithm("Alg.Alias.KeyFactory." + string, "COMPOSITE");
                configurableProvider.addAlgorithm("KeyPairGenerator." + string, "org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.KeyPairGeneratorSpi$" + string2);
                configurableProvider.addAlgorithm("Alg.Alias.KeyPairGenerator", aSN1ObjectIdentifier, string);
                configurableProvider.addAlgorithm("Signature." + string, "org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.SignatureSpi$" + string2);
                configurableProvider.addAlgorithm("Alg.Alias.Signature", aSN1ObjectIdentifier, string);
                configurableProvider.addAlgorithm("Signature." + string + "-PREHASH", "org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.SignatureSpi$" + string2 + "_PREHASH");
                configurableProvider.addKeyInfoConverter(aSN1ObjectIdentifier, new KeyFactorySpi());
            }
        }
    }
}

