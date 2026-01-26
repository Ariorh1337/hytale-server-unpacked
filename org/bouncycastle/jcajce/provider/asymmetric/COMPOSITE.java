/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class COMPOSITE {
    private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.COMPOSITE";
    private static final Map<String, String> compositeAttributes = new HashMap<String, String>();
    private static AsymmetricKeyInfoConverter baseConverter;

    static {
        compositeAttributes.put("SupportedKeyClasses", "org.bouncycastle.jcajce.CompositePublicKey|org.bouncycastle.jcajce.CompositePrivateKey");
        compositeAttributes.put("SupportedKeyFormats", "PKCS#8|X.509");
    }

    public static class KeyFactory
    extends BaseKeyFactorySpi {
        @Override
        protected Key engineTranslateKey(Key key) throws InvalidKeyException {
            try {
                if (key instanceof PrivateKey) {
                    return this.generatePrivate(PrivateKeyInfo.getInstance(key.getEncoded()));
                }
                if (key instanceof PublicKey) {
                    return this.generatePublic(SubjectPublicKeyInfo.getInstance(key.getEncoded()));
                }
            }
            catch (IOException iOException) {
                throw new InvalidKeyException("key could not be parsed: " + iOException.getMessage());
            }
            throw new InvalidKeyException("key not recognized");
        }

        @Override
        public PrivateKey generatePrivate(PrivateKeyInfo privateKeyInfo) throws IOException {
            return baseConverter.generatePrivate(privateKeyInfo);
        }

        @Override
        public PublicKey generatePublic(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
            return baseConverter.generatePublic(subjectPublicKeyInfo);
        }
    }

    public static class Mappings
    extends AsymmetricAlgorithmProvider {
        @Override
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("KeyFactory.COMPOSITE", "org.bouncycastle.jcajce.provider.asymmetric.COMPOSITE$KeyFactory");
            configurableProvider.addAlgorithm("KeyFactory." + MiscObjectIdentifiers.id_alg_composite, "org.bouncycastle.jcajce.provider.asymmetric.COMPOSITE$KeyFactory");
            configurableProvider.addAlgorithm("KeyFactory.OID." + MiscObjectIdentifiers.id_alg_composite, "org.bouncycastle.jcajce.provider.asymmetric.COMPOSITE$KeyFactory");
            configurableProvider.addAlgorithm("KeyFactory." + MiscObjectIdentifiers.id_composite_key, "org.bouncycastle.jcajce.provider.asymmetric.COMPOSITE$KeyFactory");
            configurableProvider.addAlgorithm("KeyFactory.OID." + MiscObjectIdentifiers.id_composite_key, "org.bouncycastle.jcajce.provider.asymmetric.COMPOSITE$KeyFactory");
            baseConverter = new KeyFactorySpi(new ProviderJcaJceHelper((BouncyCastleProvider)configurableProvider));
            configurableProvider.addKeyInfoConverter(MiscObjectIdentifiers.id_alg_composite, baseConverter);
            configurableProvider.addKeyInfoConverter(MiscObjectIdentifiers.id_composite_key, baseConverter);
        }
    }
}

