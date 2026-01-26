/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.BoringSSLAsyncPrivateKeyMethod;
import io.netty.handler.codec.quic.BoringSSLKeylessPrivateKey;
import io.netty.handler.codec.quic.QuicSslContext;
import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Objects;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.ManagerFactoryParameters;
import org.jetbrains.annotations.Nullable;

public final class BoringSSLKeylessManagerFactory
extends KeyManagerFactory {
    final BoringSSLAsyncPrivateKeyMethod privateKeyMethod;

    private BoringSSLKeylessManagerFactory(KeyManagerFactory keyManagerFactory, BoringSSLAsyncPrivateKeyMethod privateKeyMethod) {
        super(new KeylessManagerFactorySpi(keyManagerFactory), keyManagerFactory.getProvider(), keyManagerFactory.getAlgorithm());
        this.privateKeyMethod = Objects.requireNonNull(privateKeyMethod, "privateKeyMethod");
    }

    public static BoringSSLKeylessManagerFactory newKeyless(BoringSSLAsyncPrivateKeyMethod privateKeyMethod, File chain) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        try (InputStream chainInputStream = Files.newInputStream(chain.toPath(), new OpenOption[0]);){
            BoringSSLKeylessManagerFactory boringSSLKeylessManagerFactory = BoringSSLKeylessManagerFactory.newKeyless(privateKeyMethod, chainInputStream);
            return boringSSLKeylessManagerFactory;
        }
    }

    public static BoringSSLKeylessManagerFactory newKeyless(BoringSSLAsyncPrivateKeyMethod privateKeyMethod, InputStream chain) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        return BoringSSLKeylessManagerFactory.newKeyless(privateKeyMethod, QuicSslContext.toX509Certificates0(chain));
    }

    public static BoringSSLKeylessManagerFactory newKeyless(BoringSSLAsyncPrivateKeyMethod privateKeyMethod, X509Certificate ... certificateChain) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        ObjectUtil.checkNotNull(certificateChain, "certificateChain");
        KeylessKeyStore store = new KeylessKeyStore((X509Certificate[])certificateChain.clone());
        store.load(null, null);
        BoringSSLKeylessManagerFactory factory = new BoringSSLKeylessManagerFactory(KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()), privateKeyMethod);
        factory.init(store, null);
        return factory;
    }

    private static final class KeylessKeyStore
    extends KeyStore {
        private static final String ALIAS = "key";

        private KeylessKeyStore(final X509Certificate[] certificateChain) {
            super(new KeyStoreSpi(){
                private final Date creationDate = new Date();

                @Override
                @Nullable
                public Key engineGetKey(String alias, char[] password) {
                    if (this.engineContainsAlias(alias)) {
                        return BoringSSLKeylessPrivateKey.INSTANCE;
                    }
                    return null;
                }

                @Override
                public Certificate @Nullable [] engineGetCertificateChain(String alias) {
                    return this.engineContainsAlias(alias) ? (Certificate[])certificateChain.clone() : null;
                }

                @Override
                @Nullable
                public Certificate engineGetCertificate(String alias) {
                    return this.engineContainsAlias(alias) ? certificateChain[0] : null;
                }

                @Override
                @Nullable
                public Date engineGetCreationDate(String alias) {
                    return this.engineContainsAlias(alias) ? this.creationDate : null;
                }

                @Override
                public void engineSetKeyEntry(String alias, Key key, char[] password, Certificate[] chain) throws KeyStoreException {
                    throw new KeyStoreException("Not supported");
                }

                @Override
                public void engineSetKeyEntry(String alias, byte[] key, Certificate[] chain) throws KeyStoreException {
                    throw new KeyStoreException("Not supported");
                }

                @Override
                public void engineSetCertificateEntry(String alias, Certificate cert) throws KeyStoreException {
                    throw new KeyStoreException("Not supported");
                }

                @Override
                public void engineDeleteEntry(String alias) throws KeyStoreException {
                    throw new KeyStoreException("Not supported");
                }

                @Override
                public Enumeration<String> engineAliases() {
                    return Collections.enumeration(Collections.singleton(KeylessKeyStore.ALIAS));
                }

                @Override
                public boolean engineContainsAlias(String alias) {
                    return KeylessKeyStore.ALIAS.equals(alias);
                }

                @Override
                public int engineSize() {
                    return 1;
                }

                @Override
                public boolean engineIsKeyEntry(String alias) {
                    return this.engineContainsAlias(alias);
                }

                @Override
                public boolean engineIsCertificateEntry(String alias) {
                    return this.engineContainsAlias(alias);
                }

                @Override
                @Nullable
                public String engineGetCertificateAlias(Certificate cert) {
                    if (cert instanceof X509Certificate) {
                        for (X509Certificate x509Certificate : certificateChain) {
                            if (!x509Certificate.equals(cert)) continue;
                            return KeylessKeyStore.ALIAS;
                        }
                    }
                    return null;
                }

                @Override
                public void engineStore(OutputStream stream, char[] password) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void engineLoad(@Nullable InputStream stream, char @Nullable [] password) {
                    if (stream != null && password != null) {
                        throw new UnsupportedOperationException();
                    }
                }
            }, null, "keyless");
        }
    }

    private static final class KeylessManagerFactorySpi
    extends KeyManagerFactorySpi {
        private final KeyManagerFactory keyManagerFactory;

        KeylessManagerFactorySpi(KeyManagerFactory keyManagerFactory) {
            this.keyManagerFactory = Objects.requireNonNull(keyManagerFactory, "keyManagerFactory");
        }

        @Override
        protected void engineInit(KeyStore ks, char[] password) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
            this.keyManagerFactory.init(ks, password);
        }

        @Override
        protected void engineInit(ManagerFactoryParameters spec) {
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        protected KeyManager[] engineGetKeyManagers() {
            return this.keyManagerFactory.getKeyManagers();
        }
    }
}

