/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.ApplicationProtocolNegotiator;
import io.netty.handler.ssl.BouncyCastlePemReader;
import io.netty.handler.ssl.CipherSuiteFilter;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.IdentityCipherSuiteFilter;
import io.netty.handler.ssl.JdkSslClientContext;
import io.netty.handler.ssl.JdkSslServerContext;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.OpenSslClientContext;
import io.netty.handler.ssl.OpenSslServerContext;
import io.netty.handler.ssl.PemReader;
import io.netty.handler.ssl.ReferenceCountedOpenSslClientContext;
import io.netty.handler.ssl.ReferenceCountedOpenSslServerContext;
import io.netty.handler.ssl.ResumptionController;
import io.netty.handler.ssl.SslContextOption;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.SslUtils;
import io.netty.handler.ssl.util.BouncyCastleUtil;
import io.netty.util.AttributeMap;
import io.netty.util.DefaultAttributeMap;
import io.netty.util.concurrent.ImmediateExecutor;
import io.netty.util.internal.EmptyArrays;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManagerFactory;

public abstract class SslContext {
    static final String ALIAS = "key";
    static final CertificateFactory X509_CERT_FACTORY;
    private final boolean startTls;
    private final AttributeMap attributes = new DefaultAttributeMap();
    final ResumptionController resumptionController;
    private static final String OID_PKCS5_PBES2 = "1.2.840.113549.1.5.13";
    private static final String PBES2 = "PBES2";

    public static SslProvider defaultServerProvider() {
        return SslContext.defaultProvider();
    }

    public static SslProvider defaultClientProvider() {
        return SslContext.defaultProvider();
    }

    private static SslProvider defaultProvider() {
        if (OpenSsl.isAvailable()) {
            return SslProvider.OPENSSL;
        }
        return SslProvider.JDK;
    }

    @Deprecated
    public static SslContext newServerContext(File certChainFile, File keyFile) throws SSLException {
        return SslContext.newServerContext(certChainFile, keyFile, null);
    }

    @Deprecated
    public static SslContext newServerContext(File certChainFile, File keyFile, String keyPassword) throws SSLException {
        return SslContext.newServerContext(null, certChainFile, keyFile, keyPassword);
    }

    @Deprecated
    public static SslContext newServerContext(File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        return SslContext.newServerContext(null, certChainFile, keyFile, keyPassword, ciphers, nextProtocols, sessionCacheSize, sessionTimeout);
    }

    @Deprecated
    public static SslContext newServerContext(File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        return SslContext.newServerContext(null, certChainFile, keyFile, keyPassword, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout);
    }

    @Deprecated
    public static SslContext newServerContext(SslProvider provider, File certChainFile, File keyFile) throws SSLException {
        return SslContext.newServerContext(provider, certChainFile, keyFile, null);
    }

    @Deprecated
    public static SslContext newServerContext(SslProvider provider, File certChainFile, File keyFile, String keyPassword) throws SSLException {
        return SslContext.newServerContext(provider, certChainFile, keyFile, keyPassword, null, IdentityCipherSuiteFilter.INSTANCE, null, 0L, 0L);
    }

    @Deprecated
    public static SslContext newServerContext(SslProvider provider, File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        return SslContext.newServerContext(provider, certChainFile, keyFile, keyPassword, ciphers, IdentityCipherSuiteFilter.INSTANCE, SslContext.toApplicationProtocolConfig(nextProtocols), sessionCacheSize, sessionTimeout);
    }

    @Deprecated
    public static SslContext newServerContext(SslProvider provider, File certChainFile, File keyFile, String keyPassword, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        return SslContext.newServerContext(provider, null, trustManagerFactory, certChainFile, keyFile, keyPassword, null, ciphers, IdentityCipherSuiteFilter.INSTANCE, SslContext.toApplicationProtocolConfig(nextProtocols), sessionCacheSize, sessionTimeout);
    }

    @Deprecated
    public static SslContext newServerContext(SslProvider provider, File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        return SslContext.newServerContext(provider, null, null, certChainFile, keyFile, keyPassword, null, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout, KeyStore.getDefaultType());
    }

    @Deprecated
    public static SslContext newServerContext(SslProvider provider, File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, File keyCertChainFile, File keyFile, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        return SslContext.newServerContext(provider, trustCertCollectionFile, trustManagerFactory, keyCertChainFile, keyFile, keyPassword, keyManagerFactory, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout, KeyStore.getDefaultType());
    }

    static SslContext newServerContext(SslProvider provider, File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, File keyCertChainFile, File keyFile, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout, String keyStore) throws SSLException {
        try {
            return SslContext.newServerContextInternal(provider, null, SslContext.toX509Certificates(trustCertCollectionFile), trustManagerFactory, SslContext.toX509Certificates(keyCertChainFile), SslContext.toPrivateKey(keyFile, keyPassword), keyPassword, keyManagerFactory, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout, ClientAuth.NONE, null, false, false, null, keyStore, new Map.Entry[0]);
        }
        catch (Exception e) {
            if (e instanceof SSLException) {
                throw (SSLException)e;
            }
            throw new SSLException("failed to initialize the server-side SSL context", e);
        }
    }

    static SslContext newServerContextInternal(SslProvider provider, Provider sslContextProvider, X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout, ClientAuth clientAuth, String[] protocols, boolean startTls, boolean enableOcsp, SecureRandom secureRandom, String keyStoreType, Map.Entry<SslContextOption<?>, Object> ... ctxOptions) throws SSLException {
        if (provider == null) {
            provider = SslContext.defaultServerProvider();
        }
        ResumptionController resumptionController = new ResumptionController();
        switch (provider) {
            case JDK: {
                if (enableOcsp) {
                    throw new IllegalArgumentException("OCSP is not supported with this SslProvider: " + (Object)((Object)provider));
                }
                return new JdkSslServerContext(sslContextProvider, trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout, clientAuth, protocols, startTls, secureRandom, keyStoreType, resumptionController);
            }
            case OPENSSL: {
                SslContext.verifyNullSslContextProvider(provider, sslContextProvider);
                return new OpenSslServerContext(trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout, clientAuth, protocols, startTls, enableOcsp, keyStoreType, resumptionController, ctxOptions);
            }
            case OPENSSL_REFCNT: {
                SslContext.verifyNullSslContextProvider(provider, sslContextProvider);
                return new ReferenceCountedOpenSslServerContext(trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout, clientAuth, protocols, startTls, enableOcsp, keyStoreType, resumptionController, ctxOptions);
            }
        }
        throw new Error("Unexpected provider: " + (Object)((Object)provider));
    }

    private static void verifyNullSslContextProvider(SslProvider provider, Provider sslContextProvider) {
        if (sslContextProvider != null) {
            throw new IllegalArgumentException("Java Security Provider unsupported for SslProvider: " + (Object)((Object)provider));
        }
    }

    @Deprecated
    public static SslContext newClientContext() throws SSLException {
        return SslContext.newClientContext(null, null, null);
    }

    @Deprecated
    public static SslContext newClientContext(File certChainFile) throws SSLException {
        return SslContext.newClientContext(null, certChainFile);
    }

    @Deprecated
    public static SslContext newClientContext(TrustManagerFactory trustManagerFactory) throws SSLException {
        return SslContext.newClientContext(null, null, trustManagerFactory);
    }

    @Deprecated
    public static SslContext newClientContext(File certChainFile, TrustManagerFactory trustManagerFactory) throws SSLException {
        return SslContext.newClientContext(null, certChainFile, trustManagerFactory);
    }

    @Deprecated
    public static SslContext newClientContext(File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        return SslContext.newClientContext(null, certChainFile, trustManagerFactory, ciphers, nextProtocols, sessionCacheSize, sessionTimeout);
    }

    @Deprecated
    public static SslContext newClientContext(File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        return SslContext.newClientContext(null, certChainFile, trustManagerFactory, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout);
    }

    @Deprecated
    public static SslContext newClientContext(SslProvider provider) throws SSLException {
        return SslContext.newClientContext(provider, null, null);
    }

    @Deprecated
    public static SslContext newClientContext(SslProvider provider, File certChainFile) throws SSLException {
        return SslContext.newClientContext(provider, certChainFile, null);
    }

    @Deprecated
    public static SslContext newClientContext(SslProvider provider, TrustManagerFactory trustManagerFactory) throws SSLException {
        return SslContext.newClientContext(provider, null, trustManagerFactory);
    }

    @Deprecated
    public static SslContext newClientContext(SslProvider provider, File certChainFile, TrustManagerFactory trustManagerFactory) throws SSLException {
        return SslContext.newClientContext(provider, certChainFile, trustManagerFactory, null, IdentityCipherSuiteFilter.INSTANCE, null, 0L, 0L);
    }

    @Deprecated
    public static SslContext newClientContext(SslProvider provider, File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        return SslContext.newClientContext(provider, certChainFile, trustManagerFactory, null, null, null, null, ciphers, IdentityCipherSuiteFilter.INSTANCE, SslContext.toApplicationProtocolConfig(nextProtocols), sessionCacheSize, sessionTimeout);
    }

    @Deprecated
    public static SslContext newClientContext(SslProvider provider, File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        return SslContext.newClientContext(provider, certChainFile, trustManagerFactory, null, null, null, null, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout);
    }

    @Deprecated
    public static SslContext newClientContext(SslProvider provider, File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, File keyCertChainFile, File keyFile, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        try {
            return SslContext.newClientContextInternal(provider, null, SslContext.toX509Certificates(trustCertCollectionFile), trustManagerFactory, SslContext.toX509Certificates(keyCertChainFile), SslContext.toPrivateKey(keyFile, keyPassword), keyPassword, keyManagerFactory, ciphers, cipherFilter, apn, null, sessionCacheSize, sessionTimeout, false, null, KeyStore.getDefaultType(), SslUtils.defaultEndpointVerificationAlgorithm, Collections.emptyList(), new Map.Entry[0]);
        }
        catch (Exception e) {
            if (e instanceof SSLException) {
                throw (SSLException)e;
            }
            throw new SSLException("failed to initialize the client-side SSL context", e);
        }
    }

    static SslContext newClientContextInternal(SslProvider provider, Provider sslContextProvider, X509Certificate[] trustCert, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, String[] protocols, long sessionCacheSize, long sessionTimeout, boolean enableOcsp, SecureRandom secureRandom, String keyStoreType, String endpointIdentificationAlgorithm, List<SNIServerName> serverNames, Map.Entry<SslContextOption<?>, Object> ... options) throws SSLException {
        if (provider == null) {
            provider = SslContext.defaultClientProvider();
        }
        ResumptionController resumptionController = new ResumptionController();
        switch (provider) {
            case JDK: {
                if (enableOcsp) {
                    throw new IllegalArgumentException("OCSP is not supported with this SslProvider: " + (Object)((Object)provider));
                }
                return new JdkSslClientContext(sslContextProvider, trustCert, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, ciphers, cipherFilter, apn, protocols, sessionCacheSize, sessionTimeout, secureRandom, keyStoreType, endpointIdentificationAlgorithm, serverNames, resumptionController);
            }
            case OPENSSL: {
                SslContext.verifyNullSslContextProvider(provider, sslContextProvider);
                OpenSsl.ensureAvailability();
                return new OpenSslClientContext(trustCert, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, ciphers, cipherFilter, apn, protocols, sessionCacheSize, sessionTimeout, enableOcsp, keyStoreType, endpointIdentificationAlgorithm, serverNames, resumptionController, options);
            }
            case OPENSSL_REFCNT: {
                SslContext.verifyNullSslContextProvider(provider, sslContextProvider);
                OpenSsl.ensureAvailability();
                return new ReferenceCountedOpenSslClientContext(trustCert, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, ciphers, cipherFilter, apn, protocols, sessionCacheSize, sessionTimeout, enableOcsp, keyStoreType, endpointIdentificationAlgorithm, serverNames, resumptionController, options);
            }
        }
        throw new Error("Unexpected provider: " + (Object)((Object)provider));
    }

    static ApplicationProtocolConfig toApplicationProtocolConfig(Iterable<String> nextProtocols) {
        ApplicationProtocolConfig apn = nextProtocols == null ? ApplicationProtocolConfig.DISABLED : new ApplicationProtocolConfig(ApplicationProtocolConfig.Protocol.NPN_AND_ALPN, ApplicationProtocolConfig.SelectorFailureBehavior.CHOOSE_MY_LAST_PROTOCOL, ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT, nextProtocols);
        return apn;
    }

    protected SslContext() {
        this(false);
    }

    protected SslContext(boolean startTls) {
        this(startTls, null);
    }

    SslContext(boolean startTls, ResumptionController resumptionController) {
        this.startTls = startTls;
        this.resumptionController = resumptionController;
    }

    public final AttributeMap attributes() {
        return this.attributes;
    }

    public final boolean isServer() {
        return !this.isClient();
    }

    public abstract boolean isClient();

    public abstract List<String> cipherSuites();

    public long sessionCacheSize() {
        return this.sessionContext().getSessionCacheSize();
    }

    public long sessionTimeout() {
        return this.sessionContext().getSessionTimeout();
    }

    @Deprecated
    public final List<String> nextProtocols() {
        return this.applicationProtocolNegotiator().protocols();
    }

    public abstract ApplicationProtocolNegotiator applicationProtocolNegotiator();

    public abstract SSLEngine newEngine(ByteBufAllocator var1);

    public abstract SSLEngine newEngine(ByteBufAllocator var1, String var2, int var3);

    public abstract SSLSessionContext sessionContext();

    public final SslHandler newHandler(ByteBufAllocator alloc) {
        return this.newHandler(alloc, this.startTls);
    }

    protected SslHandler newHandler(ByteBufAllocator alloc, boolean startTls) {
        return new SslHandler(this.newEngine(alloc), startTls, ImmediateExecutor.INSTANCE, this.resumptionController);
    }

    public SslHandler newHandler(ByteBufAllocator alloc, Executor delegatedTaskExecutor) {
        return this.newHandler(alloc, this.startTls, delegatedTaskExecutor);
    }

    protected SslHandler newHandler(ByteBufAllocator alloc, boolean startTls, Executor executor) {
        return new SslHandler(this.newEngine(alloc), startTls, executor, this.resumptionController);
    }

    public final SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort) {
        return this.newHandler(alloc, peerHost, peerPort, this.startTls);
    }

    protected SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort, boolean startTls) {
        return new SslHandler(this.newEngine(alloc, peerHost, peerPort), startTls, ImmediateExecutor.INSTANCE, this.resumptionController);
    }

    public SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort, Executor delegatedTaskExecutor) {
        return this.newHandler(alloc, peerHost, peerPort, this.startTls, delegatedTaskExecutor);
    }

    protected SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort, boolean startTls, Executor delegatedTaskExecutor) {
        return new SslHandler(this.newEngine(alloc, peerHost, peerPort), startTls, delegatedTaskExecutor, this.resumptionController);
    }

    @Deprecated
    protected static PKCS8EncodedKeySpec generateKeySpec(char[] password, byte[] key) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException {
        if (password == null) {
            return new PKCS8EncodedKeySpec(key);
        }
        EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(key);
        String pbeAlgorithm = SslContext.getPBEAlgorithm(encryptedPrivateKeyInfo);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(pbeAlgorithm);
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
        SecretKey pbeKey = keyFactory.generateSecret(pbeKeySpec);
        Cipher cipher = Cipher.getInstance(pbeAlgorithm);
        cipher.init(2, (Key)pbeKey, encryptedPrivateKeyInfo.getAlgParameters());
        return encryptedPrivateKeyInfo.getKeySpec(cipher);
    }

    private static String getPBEAlgorithm(EncryptedPrivateKeyInfo encryptedPrivateKeyInfo) {
        AlgorithmParameters parameters = encryptedPrivateKeyInfo.getAlgParameters();
        String algName = encryptedPrivateKeyInfo.getAlgName();
        if (parameters != null && (OID_PKCS5_PBES2.equals(algName) || PBES2.equals(algName))) {
            return parameters.toString();
        }
        return encryptedPrivateKeyInfo.getAlgName();
    }

    protected static KeyStore buildKeyStore(X509Certificate[] certChain, PrivateKey key, char[] keyPasswordChars, String keyStoreType) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        if (keyStoreType == null) {
            keyStoreType = KeyStore.getDefaultType();
        }
        KeyStore ks = KeyStore.getInstance(keyStoreType);
        ks.load(null, null);
        ks.setKeyEntry(ALIAS, key, keyPasswordChars, certChain);
        return ks;
    }

    protected static PrivateKey toPrivateKey(File keyFile, String keyPassword) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, KeyException, IOException {
        return SslContext.toPrivateKey(keyFile, keyPassword, true);
    }

    static PrivateKey toPrivateKey(File keyFile, String keyPassword, boolean tryBouncyCastle) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, KeyException, IOException {
        PrivateKey pk;
        if (keyFile == null) {
            return null;
        }
        if (tryBouncyCastle && BouncyCastleUtil.isBcPkixAvailable() && (pk = BouncyCastlePemReader.getPrivateKey(keyFile, keyPassword)) != null) {
            return pk;
        }
        return SslContext.getPrivateKeyFromByteBuffer(PemReader.readPrivateKey(keyFile), keyPassword);
    }

    protected static PrivateKey toPrivateKey(InputStream keyInputStream, String keyPassword) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, KeyException, IOException {
        if (keyInputStream == null) {
            return null;
        }
        if (BouncyCastleUtil.isBcPkixAvailable()) {
            if (!keyInputStream.markSupported()) {
                keyInputStream = new BufferedInputStream(keyInputStream);
            }
            keyInputStream.mark(0x100000);
            PrivateKey pk = BouncyCastlePemReader.getPrivateKey(keyInputStream, keyPassword);
            if (pk != null) {
                return pk;
            }
            keyInputStream.reset();
        }
        return SslContext.getPrivateKeyFromByteBuffer(PemReader.readPrivateKey(keyInputStream), keyPassword);
    }

    private static PrivateKey getPrivateKeyFromByteBuffer(ByteBuf encodedKeyBuf, String keyPassword) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, KeyException, IOException {
        byte[] encodedKey = new byte[encodedKeyBuf.readableBytes()];
        encodedKeyBuf.readBytes(encodedKey).release();
        PKCS8EncodedKeySpec encodedKeySpec = SslContext.generateKeySpec(keyPassword == null ? null : keyPassword.toCharArray(), encodedKey);
        try {
            return KeyFactory.getInstance("RSA").generatePrivate(encodedKeySpec);
        }
        catch (InvalidKeySpecException ignore) {
            try {
                return KeyFactory.getInstance("DSA").generatePrivate(encodedKeySpec);
            }
            catch (InvalidKeySpecException ignore2) {
                try {
                    return KeyFactory.getInstance("EC").generatePrivate(encodedKeySpec);
                }
                catch (InvalidKeySpecException e) {
                    throw new InvalidKeySpecException("Neither RSA, DSA nor EC worked", e);
                }
            }
        }
    }

    @Deprecated
    protected static TrustManagerFactory buildTrustManagerFactory(File certChainFile, TrustManagerFactory trustManagerFactory) throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
        return SslContext.buildTrustManagerFactory(certChainFile, trustManagerFactory, null);
    }

    protected static TrustManagerFactory buildTrustManagerFactory(File certChainFile, TrustManagerFactory trustManagerFactory, String keyType) throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
        X509Certificate[] x509Certs = SslContext.toX509Certificates(certChainFile);
        return SslContext.buildTrustManagerFactory(x509Certs, trustManagerFactory, keyType);
    }

    protected static X509Certificate[] toX509Certificates(File file) throws CertificateException {
        if (file == null) {
            return null;
        }
        return SslContext.getCertificatesFromBuffers(PemReader.readCertificates(file));
    }

    protected static X509Certificate[] toX509Certificates(InputStream in) throws CertificateException {
        if (in == null) {
            return null;
        }
        return SslContext.getCertificatesFromBuffers(PemReader.readCertificates(in));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static X509Certificate[] getCertificatesFromBuffers(ByteBuf[] certs) throws CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate[] x509Certs = new X509Certificate[certs.length];
        try {
            for (int i = 0; i < certs.length; ++i) {
                try (ByteBufInputStream is = new ByteBufInputStream(certs[i], false);){
                    x509Certs[i] = (X509Certificate)cf.generateCertificate(is);
                    continue;
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        finally {
            for (ByteBuf buf : certs) {
                buf.release();
            }
        }
        return x509Certs;
    }

    protected static TrustManagerFactory buildTrustManagerFactory(X509Certificate[] certCollection, TrustManagerFactory trustManagerFactory, String keyStoreType) throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
        if (keyStoreType == null) {
            keyStoreType = KeyStore.getDefaultType();
        }
        KeyStore ks = KeyStore.getInstance(keyStoreType);
        ks.load(null, null);
        int i = 1;
        for (X509Certificate cert : certCollection) {
            String alias = Integer.toString(i);
            ks.setCertificateEntry(alias, cert);
            ++i;
        }
        if (trustManagerFactory == null) {
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        }
        trustManagerFactory.init(ks);
        return trustManagerFactory;
    }

    static PrivateKey toPrivateKeyInternal(File keyFile, String keyPassword) throws SSLException {
        try {
            return SslContext.toPrivateKey(keyFile, keyPassword);
        }
        catch (Exception e) {
            throw new SSLException(e);
        }
    }

    static X509Certificate[] toX509CertificatesInternal(File file) throws SSLException {
        try {
            return SslContext.toX509Certificates(file);
        }
        catch (CertificateException e) {
            throw new SSLException(e);
        }
    }

    protected static KeyManagerFactory buildKeyManagerFactory(X509Certificate[] certChainFile, String keyAlgorithm, PrivateKey key, String keyPassword, KeyManagerFactory kmf, String keyStore) throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException, UnrecoverableKeyException {
        if (keyAlgorithm == null) {
            keyAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
        }
        char[] keyPasswordChars = SslContext.keyStorePassword(keyPassword);
        KeyStore ks = SslContext.buildKeyStore(certChainFile, key, keyPasswordChars, keyStore);
        return SslContext.buildKeyManagerFactory(ks, keyAlgorithm, keyPasswordChars, kmf);
    }

    static KeyManagerFactory buildKeyManagerFactory(KeyStore ks, String keyAlgorithm, char[] keyPasswordChars, KeyManagerFactory kmf) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        if (kmf == null) {
            if (keyAlgorithm == null) {
                keyAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
            }
            kmf = KeyManagerFactory.getInstance(keyAlgorithm);
        }
        kmf.init(ks, keyPasswordChars);
        return kmf;
    }

    static char[] keyStorePassword(String keyPassword) {
        return keyPassword == null ? EmptyArrays.EMPTY_CHARS : keyPassword.toCharArray();
    }

    static {
        try {
            X509_CERT_FACTORY = CertificateFactory.getInstance("X.509");
        }
        catch (CertificateException e) {
            throw new IllegalStateException("unable to instance X.509 CertificateFactory", e);
        }
    }
}

