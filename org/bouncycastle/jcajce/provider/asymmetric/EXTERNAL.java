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
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.bc.ExternalValue;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.ExternalPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;

public class EXTERNAL {
    private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.EXTERNAL";
    private static final Map<String, String> externalAttributes = new HashMap<String, String>();
    private static AsymmetricKeyInfoConverter baseConverter;

    static {
        externalAttributes.put("SupportedKeyClasses", "org.bouncycastle.jcajce.ExternalPublicKey");
        externalAttributes.put("SupportedKeyFormats", "X.509");
    }

    private static class ExternalKeyInfoConverter
    implements AsymmetricKeyInfoConverter {
        private final ConfigurableProvider provider;

        public ExternalKeyInfoConverter(ConfigurableProvider configurableProvider) {
            this.provider = configurableProvider;
        }

        @Override
        public PrivateKey generatePrivate(PrivateKeyInfo privateKeyInfo) throws IOException {
            throw new UnsupportedOperationException("no support for private key");
        }

        @Override
        public PublicKey generatePublic(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
            ExternalValue externalValue = ExternalValue.getInstance(subjectPublicKeyInfo.parsePublicKey());
            return new ExternalPublicKey(externalValue);
        }
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
            configurableProvider.addAlgorithm("KeyFactory.EXTERNAL", "org.bouncycastle.jcajce.provider.asymmetric.EXTERNAL$KeyFactory");
            configurableProvider.addAlgorithm("KeyFactory." + BCObjectIdentifiers.external_value, "org.bouncycastle.jcajce.provider.asymmetric.EXTERNAL$KeyFactory");
            configurableProvider.addAlgorithm("KeyFactory.OID." + BCObjectIdentifiers.external_value, "org.bouncycastle.jcajce.provider.asymmetric.EXTERNAL$KeyFactory");
            baseConverter = new ExternalKeyInfoConverter(configurableProvider);
            configurableProvider.addKeyInfoConverter(BCObjectIdentifiers.external_value, baseConverter);
        }
    }
}

