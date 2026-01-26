/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.BoringSSLKeylog;
import io.netty.handler.codec.quic.BoringSSLLoggingKeylog;
import io.netty.handler.codec.quic.QuicSslContext;
import io.netty.handler.codec.quic.QuicheQuicSslContext;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContextOption;
import io.netty.handler.ssl.util.KeyManagerFactoryWrapper;
import io.netty.handler.ssl.util.TrustManagerFactoryWrapper;
import io.netty.util.Mapping;
import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.net.Socket;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedKeyManager;
import org.jetbrains.annotations.Nullable;

public final class QuicSslContextBuilder {
    private static final X509ExtendedKeyManager SNI_KEYMANAGER = new X509ExtendedKeyManager(){
        private final X509Certificate[] emptyCerts = new X509Certificate[0];
        private final String[] emptyStrings = new String[0];

        @Override
        public String[] getClientAliases(String keyType, Principal[] issuers) {
            return this.emptyStrings;
        }

        @Override
        @Nullable
        public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
            return null;
        }

        @Override
        public String[] getServerAliases(String keyType, Principal[] issuers) {
            return this.emptyStrings;
        }

        @Override
        @Nullable
        public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
            return null;
        }

        @Override
        public X509Certificate[] getCertificateChain(String alias) {
            return this.emptyCerts;
        }

        @Override
        @Nullable
        public PrivateKey getPrivateKey(String alias) {
            return null;
        }
    };
    private static final Map.Entry[] EMPTY_ENTRIES = new Map.Entry[0];
    private final boolean forServer;
    private final Map<SslContextOption<?>, Object> options = new HashMap();
    private TrustManagerFactory trustManagerFactory;
    private String keyPassword;
    private KeyManagerFactory keyManagerFactory;
    private long sessionCacheSize = 20480L;
    private long sessionTimeout = 300L;
    private ClientAuth clientAuth = ClientAuth.NONE;
    private String[] applicationProtocols;
    private Boolean earlyData;
    private BoringSSLKeylog keylog;
    private Mapping<? super String, ? extends QuicSslContext> mapping;

    public static QuicSslContextBuilder forClient() {
        return new QuicSslContextBuilder(false);
    }

    public static QuicSslContextBuilder forServer(File keyFile, @Nullable String keyPassword, File certChainFile) {
        return new QuicSslContextBuilder(true).keyManager(keyFile, keyPassword, certChainFile);
    }

    public static QuicSslContextBuilder forServer(PrivateKey key, @Nullable String keyPassword, X509Certificate ... certChain) {
        return new QuicSslContextBuilder(true).keyManager(key, keyPassword, certChain);
    }

    public static QuicSslContextBuilder forServer(KeyManagerFactory keyManagerFactory, @Nullable String password) {
        return new QuicSslContextBuilder(true).keyManager(keyManagerFactory, password);
    }

    public static QuicSslContextBuilder forServer(KeyManager keyManager, @Nullable String keyPassword) {
        return new QuicSslContextBuilder(true).keyManager(keyManager, keyPassword);
    }

    public static QuicSslContext buildForServerWithSni(Mapping<? super String, ? extends QuicSslContext> mapping) {
        return QuicSslContextBuilder.forServer(SNI_KEYMANAGER, null).sni(mapping).build();
    }

    private QuicSslContextBuilder(boolean forServer) {
        this.forServer = forServer;
    }

    private QuicSslContextBuilder sni(Mapping<? super String, ? extends QuicSslContext> mapping) {
        this.mapping = ObjectUtil.checkNotNull(mapping, "mapping");
        return this;
    }

    public <T> QuicSslContextBuilder option(SslContextOption<T> option, T value) {
        if (value == null) {
            this.options.remove(option);
        } else {
            this.options.put(option, value);
        }
        return this;
    }

    public QuicSslContextBuilder earlyData(boolean enabled) {
        this.earlyData = enabled;
        return this;
    }

    public QuicSslContextBuilder keylog(boolean enabled) {
        this.keylog(enabled ? BoringSSLLoggingKeylog.INSTANCE : null);
        return this;
    }

    public QuicSslContextBuilder keylog(@Nullable BoringSSLKeylog keylog) {
        this.keylog = keylog;
        return this;
    }

    public QuicSslContextBuilder trustManager(@Nullable File trustCertCollectionFile) {
        try {
            return this.trustManager(QuicheQuicSslContext.toX509Certificates0(trustCertCollectionFile));
        }
        catch (Exception e) {
            throw new IllegalArgumentException("File does not contain valid certificates: " + trustCertCollectionFile, e);
        }
    }

    public QuicSslContextBuilder trustManager(X509Certificate ... trustCertCollection) {
        try {
            return this.trustManager(QuicheQuicSslContext.buildTrustManagerFactory0(trustCertCollection));
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public QuicSslContextBuilder trustManager(@Nullable TrustManagerFactory trustManagerFactory) {
        this.trustManagerFactory = trustManagerFactory;
        return this;
    }

    public QuicSslContextBuilder trustManager(TrustManager trustManager) {
        return this.trustManager(new TrustManagerFactoryWrapper(trustManager));
    }

    public QuicSslContextBuilder keyManager(@Nullable File keyFile, @Nullable String keyPassword, @Nullable File keyCertChainFile) {
        PrivateKey key;
        X509Certificate[] keyCertChain;
        try {
            keyCertChain = QuicheQuicSslContext.toX509Certificates0(keyCertChainFile);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("File does not contain valid certificates: " + keyCertChainFile, e);
        }
        try {
            key = QuicheQuicSslContext.toPrivateKey0(keyFile, keyPassword);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("File does not contain valid private key: " + keyFile, e);
        }
        return this.keyManager(key, keyPassword, keyCertChain);
    }

    public QuicSslContextBuilder keyManager(@Nullable PrivateKey key, @Nullable String keyPassword, X509Certificate ... certChain) {
        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null);
            char[] pass = keyPassword == null ? new char[]{} : keyPassword.toCharArray();
            ks.setKeyEntry("alias", key, pass, certChain);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(ks, pass);
            return this.keyManager(keyManagerFactory, keyPassword);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public QuicSslContextBuilder keyManager(@Nullable KeyManagerFactory keyManagerFactory, @Nullable String keyPassword) {
        this.keyPassword = keyPassword;
        this.keyManagerFactory = keyManagerFactory;
        return this;
    }

    public QuicSslContextBuilder keyManager(KeyManager keyManager, @Nullable String password) {
        return this.keyManager(new KeyManagerFactoryWrapper(keyManager), password);
    }

    public QuicSslContextBuilder applicationProtocols(String ... applicationProtocols) {
        this.applicationProtocols = applicationProtocols;
        return this;
    }

    public QuicSslContextBuilder sessionCacheSize(long sessionCacheSize) {
        this.sessionCacheSize = sessionCacheSize;
        return this;
    }

    public QuicSslContextBuilder sessionTimeout(long sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
        return this;
    }

    public QuicSslContextBuilder clientAuth(ClientAuth clientAuth) {
        if (!this.forServer) {
            throw new UnsupportedOperationException("Only supported for server");
        }
        this.clientAuth = ObjectUtil.checkNotNull(clientAuth, "clientAuth");
        return this;
    }

    public QuicSslContext build() {
        if (this.forServer) {
            return new QuicheQuicSslContext(true, this.sessionTimeout, this.sessionCacheSize, this.clientAuth, this.trustManagerFactory, this.keyManagerFactory, this.keyPassword, this.mapping, this.earlyData, this.keylog, this.applicationProtocols, QuicSslContextBuilder.toArray(this.options.entrySet(), EMPTY_ENTRIES));
        }
        return new QuicheQuicSslContext(false, this.sessionTimeout, this.sessionCacheSize, this.clientAuth, this.trustManagerFactory, this.keyManagerFactory, this.keyPassword, this.mapping, this.earlyData, this.keylog, this.applicationProtocols, QuicSslContextBuilder.toArray(this.options.entrySet(), EMPTY_ENTRIES));
    }

    private static <T> T[] toArray(Iterable<? extends T> iterable, T[] prototype) {
        if (iterable == null) {
            return null;
        }
        ArrayList<T> list = new ArrayList<T>();
        for (T element : iterable) {
            list.add(element);
        }
        return list.toArray(prototype);
    }
}

