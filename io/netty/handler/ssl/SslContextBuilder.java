/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.CipherSuiteFilter;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.IdentityCipherSuiteFilter;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextOption;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.SslUtils;
import io.netty.handler.ssl.util.KeyManagerFactoryWrapper;
import io.netty.handler.ssl.util.TrustManagerFactoryWrapper;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public final class SslContextBuilder {
    private static final Map.Entry[] EMPTY_ENTRIES = new Map.Entry[0];
    private final boolean forServer;
    private SslProvider provider;
    private Provider sslContextProvider;
    private X509Certificate[] trustCertCollection;
    private TrustManagerFactory trustManagerFactory;
    private X509Certificate[] keyCertChain;
    private PrivateKey key;
    private String keyPassword;
    private KeyManagerFactory keyManagerFactory;
    private Iterable<String> ciphers;
    private CipherSuiteFilter cipherFilter = IdentityCipherSuiteFilter.INSTANCE;
    private ApplicationProtocolConfig apn;
    private long sessionCacheSize;
    private long sessionTimeout;
    private ClientAuth clientAuth = ClientAuth.NONE;
    private String[] protocols;
    private boolean startTls;
    private boolean enableOcsp;
    private SecureRandom secureRandom;
    private String keyStoreType = KeyStore.getDefaultType();
    private String endpointIdentificationAlgorithm;
    private final Map<SslContextOption<?>, Object> options = new HashMap();
    private final List<SNIServerName> serverNames;

    public static SslContextBuilder forClient() {
        return new SslContextBuilder(false);
    }

    public static SslContextBuilder forServer(File keyCertChainFile, File keyFile) {
        return new SslContextBuilder(true).keyManager(keyCertChainFile, keyFile);
    }

    public static SslContextBuilder forServer(InputStream keyCertChainInputStream, InputStream keyInputStream) {
        return new SslContextBuilder(true).keyManager(keyCertChainInputStream, keyInputStream);
    }

    public static SslContextBuilder forServer(PrivateKey key, X509Certificate ... keyCertChain) {
        return new SslContextBuilder(true).keyManager(key, keyCertChain);
    }

    public static SslContextBuilder forServer(PrivateKey key, Iterable<? extends X509Certificate> keyCertChain) {
        return SslContextBuilder.forServer(key, SslContextBuilder.toArray(keyCertChain, EmptyArrays.EMPTY_X509_CERTIFICATES));
    }

    public static SslContextBuilder forServer(File keyCertChainFile, File keyFile, String keyPassword) {
        return new SslContextBuilder(true).keyManager(keyCertChainFile, keyFile, keyPassword);
    }

    public static SslContextBuilder forServer(InputStream keyCertChainInputStream, InputStream keyInputStream, String keyPassword) {
        return new SslContextBuilder(true).keyManager(keyCertChainInputStream, keyInputStream, keyPassword);
    }

    public static SslContextBuilder forServer(PrivateKey key, String keyPassword, X509Certificate ... keyCertChain) {
        return new SslContextBuilder(true).keyManager(key, keyPassword, keyCertChain);
    }

    public static SslContextBuilder forServer(PrivateKey key, String keyPassword, Iterable<? extends X509Certificate> keyCertChain) {
        return SslContextBuilder.forServer(key, keyPassword, SslContextBuilder.toArray(keyCertChain, EmptyArrays.EMPTY_X509_CERTIFICATES));
    }

    public static SslContextBuilder forServer(KeyManagerFactory keyManagerFactory) {
        return new SslContextBuilder(true).keyManager(keyManagerFactory);
    }

    public static SslContextBuilder forServer(KeyManager keyManager) {
        return new SslContextBuilder(true).keyManager(keyManager);
    }

    private SslContextBuilder(boolean forServer) {
        this.forServer = forServer;
        if (!forServer) {
            this.endpointIdentificationAlgorithm = SslUtils.defaultEndpointVerificationAlgorithm;
        }
        this.serverNames = forServer ? null : new ArrayList(2);
    }

    public <T> SslContextBuilder option(SslContextOption<T> option, T value) {
        if (value == null) {
            this.options.remove(option);
        } else {
            this.options.put(option, value);
        }
        return this;
    }

    public SslContextBuilder sslProvider(SslProvider provider) {
        this.provider = provider;
        return this;
    }

    public SslContextBuilder keyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
        return this;
    }

    public SslContextBuilder sslContextProvider(Provider sslContextProvider) {
        this.sslContextProvider = sslContextProvider;
        return this;
    }

    public SslContextBuilder trustManager(File trustCertCollectionFile) {
        try {
            return this.trustManager(SslContext.toX509Certificates(trustCertCollectionFile));
        }
        catch (Exception e) {
            throw new IllegalArgumentException("File does not contain valid certificates: " + trustCertCollectionFile, e);
        }
    }

    public SslContextBuilder trustManager(InputStream trustCertCollectionInputStream) {
        try {
            return this.trustManager(SslContext.toX509Certificates(trustCertCollectionInputStream));
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Input stream does not contain valid certificates.", e);
        }
    }

    public SslContextBuilder trustManager(X509Certificate ... trustCertCollection) {
        this.trustCertCollection = trustCertCollection != null ? (X509Certificate[])trustCertCollection.clone() : null;
        this.trustManagerFactory = null;
        return this;
    }

    public SslContextBuilder trustManager(Iterable<? extends X509Certificate> trustCertCollection) {
        return this.trustManager(SslContextBuilder.toArray(trustCertCollection, EmptyArrays.EMPTY_X509_CERTIFICATES));
    }

    public SslContextBuilder trustManager(TrustManagerFactory trustManagerFactory) {
        this.trustCertCollection = null;
        this.trustManagerFactory = trustManagerFactory;
        return this;
    }

    public SslContextBuilder trustManager(TrustManager trustManager) {
        this.trustManagerFactory = trustManager != null ? new TrustManagerFactoryWrapper(trustManager) : null;
        this.trustCertCollection = null;
        return this;
    }

    public SslContextBuilder keyManager(File keyCertChainFile, File keyFile) {
        return this.keyManager(keyCertChainFile, keyFile, null);
    }

    public SslContextBuilder keyManager(InputStream keyCertChainInputStream, InputStream keyInputStream) {
        return this.keyManager(keyCertChainInputStream, keyInputStream, null);
    }

    public SslContextBuilder keyManager(PrivateKey key, X509Certificate ... keyCertChain) {
        return this.keyManager(key, (String)null, keyCertChain);
    }

    public SslContextBuilder keyManager(PrivateKey key, Iterable<? extends X509Certificate> keyCertChain) {
        return this.keyManager(key, SslContextBuilder.toArray(keyCertChain, EmptyArrays.EMPTY_X509_CERTIFICATES));
    }

    public SslContextBuilder keyManager(File keyCertChainFile, File keyFile, String keyPassword) {
        PrivateKey key;
        X509Certificate[] keyCertChain;
        try {
            keyCertChain = SslContext.toX509Certificates(keyCertChainFile);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("File does not contain valid certificates: " + keyCertChainFile, e);
        }
        try {
            key = SslContext.toPrivateKey(keyFile, keyPassword);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("File does not contain valid private key: " + keyFile, e);
        }
        return this.keyManager(key, keyPassword, keyCertChain);
    }

    public SslContextBuilder keyManager(InputStream keyCertChainInputStream, InputStream keyInputStream, String keyPassword) {
        PrivateKey key;
        X509Certificate[] keyCertChain;
        try {
            keyCertChain = SslContext.toX509Certificates(keyCertChainInputStream);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Input stream not contain valid certificates.", e);
        }
        try {
            key = SslContext.toPrivateKey(keyInputStream, keyPassword);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Input stream does not contain valid private key.", e);
        }
        return this.keyManager(key, keyPassword, keyCertChain);
    }

    public SslContextBuilder keyManager(PrivateKey key, String keyPassword, X509Certificate ... keyCertChain) {
        if (this.forServer) {
            ObjectUtil.checkNonEmpty(keyCertChain, "keyCertChain");
            ObjectUtil.checkNotNull(key, "key required for servers");
        }
        if (keyCertChain == null || keyCertChain.length == 0) {
            this.keyCertChain = null;
        } else {
            for (X509Certificate cert : keyCertChain) {
                ObjectUtil.checkNotNullWithIAE(cert, "cert");
            }
            this.keyCertChain = (X509Certificate[])keyCertChain.clone();
        }
        this.key = key;
        this.keyPassword = keyPassword;
        this.keyManagerFactory = null;
        return this;
    }

    public SslContextBuilder keyManager(PrivateKey key, String keyPassword, Iterable<? extends X509Certificate> keyCertChain) {
        return this.keyManager(key, keyPassword, SslContextBuilder.toArray(keyCertChain, EmptyArrays.EMPTY_X509_CERTIFICATES));
    }

    public SslContextBuilder keyManager(KeyManagerFactory keyManagerFactory) {
        if (this.forServer) {
            ObjectUtil.checkNotNull(keyManagerFactory, "keyManagerFactory required for servers");
        }
        this.keyCertChain = null;
        this.key = null;
        this.keyPassword = null;
        this.keyManagerFactory = keyManagerFactory;
        return this;
    }

    public SslContextBuilder keyManager(KeyManager keyManager) {
        if (this.forServer) {
            ObjectUtil.checkNotNull(keyManager, "keyManager required for servers");
        }
        this.keyManagerFactory = keyManager != null ? new KeyManagerFactoryWrapper(keyManager) : null;
        this.keyCertChain = null;
        this.key = null;
        this.keyPassword = null;
        return this;
    }

    public SslContextBuilder ciphers(Iterable<String> ciphers) {
        return this.ciphers(ciphers, IdentityCipherSuiteFilter.INSTANCE);
    }

    public SslContextBuilder ciphers(Iterable<String> ciphers, CipherSuiteFilter cipherFilter) {
        this.cipherFilter = ObjectUtil.checkNotNull(cipherFilter, "cipherFilter");
        this.ciphers = ciphers;
        return this;
    }

    public SslContextBuilder applicationProtocolConfig(ApplicationProtocolConfig apn) {
        this.apn = apn;
        return this;
    }

    public SslContextBuilder sessionCacheSize(long sessionCacheSize) {
        this.sessionCacheSize = sessionCacheSize;
        return this;
    }

    public SslContextBuilder sessionTimeout(long sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
        return this;
    }

    public SslContextBuilder clientAuth(ClientAuth clientAuth) {
        this.clientAuth = ObjectUtil.checkNotNull(clientAuth, "clientAuth");
        return this;
    }

    public SslContextBuilder protocols(String ... protocols) {
        this.protocols = protocols == null ? null : (String[])protocols.clone();
        return this;
    }

    public SslContextBuilder protocols(Iterable<String> protocols) {
        return this.protocols(SslContextBuilder.toArray(protocols, EmptyArrays.EMPTY_STRINGS));
    }

    public SslContextBuilder startTls(boolean startTls) {
        this.startTls = startTls;
        return this;
    }

    public SslContextBuilder enableOcsp(boolean enableOcsp) {
        this.enableOcsp = enableOcsp;
        return this;
    }

    public SslContextBuilder secureRandom(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
        return this;
    }

    public SslContextBuilder endpointIdentificationAlgorithm(String algorithm) {
        this.endpointIdentificationAlgorithm = algorithm;
        return this;
    }

    public SslContextBuilder serverName(SNIServerName serverName) {
        if (this.forServer) {
            throw new UnsupportedOperationException("Cannot add Server Name Indication extension, because this is a server context builder.");
        }
        ObjectUtil.checkNotNull(serverName, "serverName");
        if (!(serverName instanceof SNIHostName)) {
            throw new IllegalArgumentException("Only SNIHostName is supported. The given SNIServerName type was " + serverName.getClass().getName());
        }
        this.serverNames.add(serverName);
        return this;
    }

    public SslContext build() throws SSLException {
        if (this.forServer) {
            return SslContext.newServerContextInternal(this.provider, this.sslContextProvider, this.trustCertCollection, this.trustManagerFactory, this.keyCertChain, this.key, this.keyPassword, this.keyManagerFactory, this.ciphers, this.cipherFilter, this.apn, this.sessionCacheSize, this.sessionTimeout, this.clientAuth, this.protocols, this.startTls, this.enableOcsp, this.secureRandom, this.keyStoreType, SslContextBuilder.toArray(this.options.entrySet(), EMPTY_ENTRIES));
        }
        return SslContext.newClientContextInternal(this.provider, this.sslContextProvider, this.trustCertCollection, this.trustManagerFactory, this.keyCertChain, this.key, this.keyPassword, this.keyManagerFactory, this.ciphers, this.cipherFilter, this.apn, this.protocols, this.sessionCacheSize, this.sessionTimeout, this.enableOcsp, this.secureRandom, this.keyStoreType, this.endpointIdentificationAlgorithm, this.serverNames, SslContextBuilder.toArray(this.options.entrySet(), EMPTY_ENTRIES));
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

