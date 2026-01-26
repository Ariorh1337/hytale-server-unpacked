/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.subtle;

import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.subtle.EngineWrapper;
import com.google.crypto.tink.subtle.SubtleUtil;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.Mac;

public final class EngineFactory<T_WRAPPER extends EngineWrapper<JcePrimitiveT>, JcePrimitiveT> {
    private final Policy<JcePrimitiveT> policy;
    public static final EngineFactory<EngineWrapper.TCipher, Cipher> CIPHER = new EngineFactory(new EngineWrapper.TCipher());
    public static final EngineFactory<EngineWrapper.TMac, Mac> MAC = new EngineFactory(new EngineWrapper.TMac());
    public static final EngineFactory<EngineWrapper.TSignature, Signature> SIGNATURE = new EngineFactory(new EngineWrapper.TSignature());
    public static final EngineFactory<EngineWrapper.TMessageDigest, MessageDigest> MESSAGE_DIGEST = new EngineFactory(new EngineWrapper.TMessageDigest());
    public static final EngineFactory<EngineWrapper.TKeyAgreement, KeyAgreement> KEY_AGREEMENT = new EngineFactory(new EngineWrapper.TKeyAgreement());
    public static final EngineFactory<EngineWrapper.TKeyPairGenerator, KeyPairGenerator> KEY_PAIR_GENERATOR = new EngineFactory(new EngineWrapper.TKeyPairGenerator());
    public static final EngineFactory<EngineWrapper.TKeyFactory, KeyFactory> KEY_FACTORY = new EngineFactory(new EngineWrapper.TKeyFactory());

    public static List<Provider> toProviderList(String ... providerNames) {
        ArrayList<Provider> providers = new ArrayList<Provider>();
        for (String s : providerNames) {
            Provider p = Security.getProvider(s);
            if (p == null) continue;
            providers.add(p);
        }
        return providers;
    }

    public EngineFactory(T_WRAPPER instanceBuilder) {
        this.policy = TinkFipsUtil.useOnlyFips() ? new FipsPolicy((EngineWrapper)instanceBuilder) : (SubtleUtil.isAndroid() ? new AndroidPolicy((EngineWrapper)instanceBuilder) : new DefaultPolicy((EngineWrapper)instanceBuilder));
    }

    public JcePrimitiveT getInstance(String algorithm) throws GeneralSecurityException {
        return this.policy.getInstance(algorithm);
    }

    JcePrimitiveT getInstance(String algorithm, List<Provider> preferredProviders) throws GeneralSecurityException {
        return this.policy.getInstance(algorithm, preferredProviders);
    }

    private static class FipsPolicy<JcePrimitiveT>
    implements Policy<JcePrimitiveT> {
        private final EngineWrapper<JcePrimitiveT> jceFactory;

        private FipsPolicy(EngineWrapper<JcePrimitiveT> jceFactory) {
            this.jceFactory = jceFactory;
        }

        @Override
        public JcePrimitiveT getInstance(String algorithm) throws GeneralSecurityException {
            List<Provider> conscryptProviders = EngineFactory.toProviderList("GmsCore_OpenSSL", "AndroidOpenSSL", "Conscrypt");
            Exception cause = null;
            for (Provider provider : conscryptProviders) {
                try {
                    return this.jceFactory.getInstance(algorithm, provider);
                }
                catch (Exception e) {
                    if (cause != null) continue;
                    cause = e;
                }
            }
            throw new GeneralSecurityException("No good Provider found.", cause);
        }

        @Override
        public JcePrimitiveT getInstance(String algorithm, List<Provider> preferredProviders) throws GeneralSecurityException {
            return this.getInstance(algorithm);
        }
    }

    private static interface Policy<JcePrimitiveT> {
        public JcePrimitiveT getInstance(String var1) throws GeneralSecurityException;

        public JcePrimitiveT getInstance(String var1, List<Provider> var2) throws GeneralSecurityException;
    }

    private static class AndroidPolicy<JcePrimitiveT>
    implements Policy<JcePrimitiveT> {
        private final EngineWrapper<JcePrimitiveT> jceFactory;

        private AndroidPolicy(EngineWrapper<JcePrimitiveT> jceFactory) {
            this.jceFactory = jceFactory;
        }

        @Override
        public JcePrimitiveT getInstance(String algorithm) throws GeneralSecurityException {
            List<Provider> conscryptProviders = EngineFactory.toProviderList("GmsCore_OpenSSL", "AndroidOpenSSL");
            Exception cause = null;
            for (Provider provider : conscryptProviders) {
                try {
                    return this.jceFactory.getInstance(algorithm, provider);
                }
                catch (Exception e) {
                    if (cause != null) continue;
                    cause = e;
                }
            }
            return this.jceFactory.getInstance(algorithm, null);
        }

        @Override
        public JcePrimitiveT getInstance(String algorithm, List<Provider> preferredProviders) throws GeneralSecurityException {
            return this.getInstance(algorithm);
        }
    }

    private static class DefaultPolicy<JcePrimitiveT>
    implements Policy<JcePrimitiveT> {
        private final EngineWrapper<JcePrimitiveT> jceFactory;

        private DefaultPolicy(EngineWrapper<JcePrimitiveT> jceFactory) {
            this.jceFactory = jceFactory;
        }

        @Override
        public JcePrimitiveT getInstance(String algorithm) throws GeneralSecurityException {
            return this.jceFactory.getInstance(algorithm, null);
        }

        @Override
        public JcePrimitiveT getInstance(String algorithm, List<Provider> preferredProviders) throws GeneralSecurityException {
            for (Provider provider : preferredProviders) {
                try {
                    return this.jceFactory.getInstance(algorithm, provider);
                }
                catch (Exception exception) {
                }
            }
            return this.getInstance(algorithm);
        }
    }
}

