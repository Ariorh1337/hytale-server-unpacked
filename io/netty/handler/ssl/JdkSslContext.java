/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.CipherSuiteFilter;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.IdentityCipherSuiteFilter;
import io.netty.handler.ssl.JdkAlpnApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkDefaultApplicationProtocolNegotiator;
import io.netty.handler.ssl.ResumptionController;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslUtils;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSessionContext;

public class JdkSslContext
extends SslContext {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(JdkSslContext.class);
    static final String PROTOCOL = "TLS";
    private static final String[] DEFAULT_PROTOCOLS;
    private static final List<String> DEFAULT_CIPHERS;
    private static final List<String> DEFAULT_CIPHERS_NON_TLSV13;
    private static final Set<String> SUPPORTED_CIPHERS;
    private static final Set<String> SUPPORTED_CIPHERS_NON_TLSV13;
    private static final Provider DEFAULT_PROVIDER;
    private final String[] protocols;
    private final String[] cipherSuites;
    private final List<String> unmodifiableCipherSuites;
    private final JdkApplicationProtocolNegotiator apn;
    private final ClientAuth clientAuth;
    private final SSLContext sslContext;
    private final boolean isClient;
    private final String endpointIdentificationAlgorithm;
    private final List<SNIServerName> serverNames;

    private static String[] defaultProtocols(SSLContext context, SSLEngine engine) {
        String[] supportedProtocols = context.getDefaultSSLParameters().getProtocols();
        HashSet<String> supportedProtocolsSet = new HashSet<String>(supportedProtocols.length);
        Collections.addAll(supportedProtocolsSet, supportedProtocols);
        ArrayList<String> protocols = new ArrayList<String>();
        SslUtils.addIfSupported(supportedProtocolsSet, protocols, "TLSv1.3", "TLSv1.2", "TLSv1.1", "TLSv1");
        if (!protocols.isEmpty()) {
            return protocols.toArray(EmptyArrays.EMPTY_STRINGS);
        }
        return engine.getEnabledProtocols();
    }

    private static Set<String> supportedCiphers(SSLEngine engine) {
        String[] supportedCiphers = engine.getSupportedCipherSuites();
        LinkedHashSet<String> supportedCiphersSet = new LinkedHashSet<String>(supportedCiphers.length);
        for (int i = 0; i < supportedCiphers.length; ++i) {
            String supportedCipher = supportedCiphers[i];
            supportedCiphersSet.add(supportedCipher);
            if (!supportedCipher.startsWith("SSL_")) continue;
            String tlsPrefixedCipherName = "TLS_" + supportedCipher.substring("SSL_".length());
            try {
                engine.setEnabledCipherSuites(new String[]{tlsPrefixedCipherName});
                supportedCiphersSet.add(tlsPrefixedCipherName);
                continue;
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        return supportedCiphersSet;
    }

    private static List<String> defaultCiphers(SSLEngine engine, Set<String> supportedCiphers) {
        ArrayList<String> ciphers = new ArrayList<String>();
        SslUtils.addIfSupported(supportedCiphers, ciphers, SslUtils.DEFAULT_CIPHER_SUITES);
        SslUtils.useFallbackCiphersIfDefaultIsEmpty(ciphers, engine.getEnabledCipherSuites());
        return ciphers;
    }

    private static boolean isTlsV13Supported(String[] protocols) {
        for (String protocol : protocols) {
            if (!"TLSv1.3".equals(protocol)) continue;
            return true;
        }
        return false;
    }

    @Deprecated
    public JdkSslContext(SSLContext sslContext, boolean isClient, ClientAuth clientAuth) {
        this(sslContext, isClient, null, (CipherSuiteFilter)IdentityCipherSuiteFilter.INSTANCE, JdkDefaultApplicationProtocolNegotiator.INSTANCE, clientAuth, null, false);
    }

    @Deprecated
    public JdkSslContext(SSLContext sslContext, boolean isClient, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, ClientAuth clientAuth) {
        this(sslContext, isClient, ciphers, cipherFilter, apn, clientAuth, null, false);
    }

    public JdkSslContext(SSLContext sslContext, boolean isClient, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, ClientAuth clientAuth, String[] protocols, boolean startTls) {
        this(sslContext, isClient, ciphers, cipherFilter, JdkSslContext.toNegotiator(apn, !isClient), clientAuth, protocols == null ? null : (String[])protocols.clone(), startTls);
    }

    JdkSslContext(SSLContext sslContext, boolean isClient, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, JdkApplicationProtocolNegotiator apn, ClientAuth clientAuth, String[] protocols, boolean startTls) {
        this(sslContext, isClient, ciphers, cipherFilter, apn, clientAuth, protocols, startTls, null, null, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    JdkSslContext(SSLContext sslContext, boolean isClient, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, JdkApplicationProtocolNegotiator apn, ClientAuth clientAuth, String[] protocols, boolean startTls, String endpointIdentificationAlgorithm, List<SNIServerName> serverNames, ResumptionController resumptionController) {
        super(startTls, resumptionController);
        List<String> defaultCiphers;
        Set<String> supportedCiphers;
        this.apn = ObjectUtil.checkNotNull(apn, "apn");
        this.clientAuth = ObjectUtil.checkNotNull(clientAuth, "clientAuth");
        this.sslContext = ObjectUtil.checkNotNull(sslContext, "sslContext");
        this.endpointIdentificationAlgorithm = endpointIdentificationAlgorithm;
        this.serverNames = serverNames;
        if (DEFAULT_PROVIDER.equals(sslContext.getProvider())) {
            String[] stringArray = this.protocols = protocols == null ? DEFAULT_PROTOCOLS : protocols;
            if (JdkSslContext.isTlsV13Supported(this.protocols)) {
                supportedCiphers = SUPPORTED_CIPHERS;
                defaultCiphers = DEFAULT_CIPHERS;
            } else {
                supportedCiphers = SUPPORTED_CIPHERS_NON_TLSV13;
                defaultCiphers = DEFAULT_CIPHERS_NON_TLSV13;
            }
        } else {
            SSLEngine engine = sslContext.createSSLEngine();
            try {
                this.protocols = protocols == null ? JdkSslContext.defaultProtocols(sslContext, engine) : protocols;
                supportedCiphers = JdkSslContext.supportedCiphers(engine);
                defaultCiphers = JdkSslContext.defaultCiphers(engine, supportedCiphers);
                if (!JdkSslContext.isTlsV13Supported(this.protocols)) {
                    for (String cipher : SslUtils.DEFAULT_TLSV13_CIPHER_SUITES) {
                        supportedCiphers.remove(cipher);
                        defaultCiphers.remove(cipher);
                    }
                }
            }
            finally {
                ReferenceCountUtil.release(engine);
            }
        }
        this.cipherSuites = ObjectUtil.checkNotNull(cipherFilter, "cipherFilter").filterCipherSuites(ciphers, defaultCiphers, supportedCiphers);
        this.unmodifiableCipherSuites = Collections.unmodifiableList(Arrays.asList(this.cipherSuites));
        this.isClient = isClient;
    }

    public final SSLContext context() {
        return this.sslContext;
    }

    @Override
    public final boolean isClient() {
        return this.isClient;
    }

    @Override
    public final SSLSessionContext sessionContext() {
        if (this.isServer()) {
            return this.context().getServerSessionContext();
        }
        return this.context().getClientSessionContext();
    }

    @Override
    public final List<String> cipherSuites() {
        return this.unmodifiableCipherSuites;
    }

    @Override
    public final SSLEngine newEngine(ByteBufAllocator alloc) {
        return this.configureAndWrapEngine(this.context().createSSLEngine(), alloc);
    }

    @Override
    public final SSLEngine newEngine(ByteBufAllocator alloc, String peerHost, int peerPort) {
        return this.configureAndWrapEngine(this.context().createSSLEngine(peerHost, peerPort), alloc);
    }

    private SSLEngine configureAndWrapEngine(SSLEngine engine, ByteBufAllocator alloc) {
        engine.setEnabledCipherSuites(this.cipherSuites);
        engine.setEnabledProtocols(this.protocols);
        engine.setUseClientMode(this.isClient());
        if (this.isServer()) {
            switch (this.clientAuth) {
                case OPTIONAL: {
                    engine.setWantClientAuth(true);
                    break;
                }
                case REQUIRE: {
                    engine.setNeedClientAuth(true);
                    break;
                }
                case NONE: {
                    break;
                }
                default: {
                    throw new Error("Unexpected auth " + (Object)((Object)this.clientAuth));
                }
            }
        }
        this.configureSSLParameters(engine);
        JdkApplicationProtocolNegotiator.SslEngineWrapperFactory factory = this.apn.wrapperFactory();
        if (factory instanceof JdkApplicationProtocolNegotiator.AllocatorAwareSslEngineWrapperFactory) {
            return ((JdkApplicationProtocolNegotiator.AllocatorAwareSslEngineWrapperFactory)factory).wrapSslEngine(engine, alloc, this.apn, this.isServer());
        }
        return factory.wrapSslEngine(engine, this.apn, this.isServer());
    }

    private void configureSSLParameters(SSLEngine engine) {
        SSLParameters params = engine.getSSLParameters();
        params.setEndpointIdentificationAlgorithm(this.endpointIdentificationAlgorithm);
        if (this.serverNames != null && !this.serverNames.isEmpty()) {
            params.setServerNames(this.serverNames);
        }
        engine.setSSLParameters(params);
    }

    @Override
    public final JdkApplicationProtocolNegotiator applicationProtocolNegotiator() {
        return this.apn;
    }

    static JdkApplicationProtocolNegotiator toNegotiator(ApplicationProtocolConfig config, boolean isServer) {
        if (config == null) {
            return JdkDefaultApplicationProtocolNegotiator.INSTANCE;
        }
        switch (config.protocol()) {
            case NONE: {
                return JdkDefaultApplicationProtocolNegotiator.INSTANCE;
            }
            case ALPN: {
                if (isServer) {
                    switch (config.selectorFailureBehavior()) {
                        case FATAL_ALERT: {
                            return new JdkAlpnApplicationProtocolNegotiator(true, config.supportedProtocols());
                        }
                        case NO_ADVERTISE: {
                            return new JdkAlpnApplicationProtocolNegotiator(false, config.supportedProtocols());
                        }
                    }
                    throw new UnsupportedOperationException("JDK provider does not support " + (Object)((Object)config.selectorFailureBehavior()) + " failure behavior");
                }
                switch (config.selectedListenerFailureBehavior()) {
                    case ACCEPT: {
                        return new JdkAlpnApplicationProtocolNegotiator(false, config.supportedProtocols());
                    }
                    case FATAL_ALERT: {
                        return new JdkAlpnApplicationProtocolNegotiator(true, config.supportedProtocols());
                    }
                }
                throw new UnsupportedOperationException("JDK provider does not support " + (Object)((Object)config.selectedListenerFailureBehavior()) + " failure behavior");
            }
        }
        throw new UnsupportedOperationException("JDK provider does not support " + (Object)((Object)config.protocol()) + " protocol");
    }

    static KeyManagerFactory buildKeyManagerFactory(File certChainFile, File keyFile, String keyPassword, KeyManagerFactory kmf, String keyStore) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, CertificateException, KeyException, IOException {
        String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX509";
        }
        return JdkSslContext.buildKeyManagerFactory(certChainFile, algorithm, keyFile, keyPassword, kmf, keyStore);
    }

    @Deprecated
    protected static KeyManagerFactory buildKeyManagerFactory(File certChainFile, File keyFile, String keyPassword, KeyManagerFactory kmf) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, CertificateException, KeyException, IOException {
        return JdkSslContext.buildKeyManagerFactory(certChainFile, keyFile, keyPassword, kmf, KeyStore.getDefaultType());
    }

    static KeyManagerFactory buildKeyManagerFactory(File certChainFile, String keyAlgorithm, File keyFile, String keyPassword, KeyManagerFactory kmf, String keyStore) throws KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, IOException, CertificateException, KeyException, UnrecoverableKeyException {
        return JdkSslContext.buildKeyManagerFactory(JdkSslContext.toX509Certificates(certChainFile), keyAlgorithm, JdkSslContext.toPrivateKey(keyFile, keyPassword), keyPassword, kmf, keyStore);
    }

    @Deprecated
    protected static KeyManagerFactory buildKeyManagerFactory(File certChainFile, String keyAlgorithm, File keyFile, String keyPassword, KeyManagerFactory kmf) throws KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, IOException, CertificateException, KeyException, UnrecoverableKeyException {
        return JdkSslContext.buildKeyManagerFactory(JdkSslContext.toX509Certificates(certChainFile), keyAlgorithm, JdkSslContext.toPrivateKey(keyFile, keyPassword), keyPassword, kmf, KeyStore.getDefaultType());
    }

    static {
        Defaults defaults = new Defaults();
        defaults.init();
        DEFAULT_PROVIDER = defaults.defaultProvider;
        DEFAULT_PROTOCOLS = defaults.defaultProtocols;
        SUPPORTED_CIPHERS = defaults.supportedCiphers;
        DEFAULT_CIPHERS = defaults.defaultCiphers;
        DEFAULT_CIPHERS_NON_TLSV13 = defaults.defaultCiphersNonTLSv13;
        SUPPORTED_CIPHERS_NON_TLSV13 = defaults.supportedCiphersNonTLSv13;
        if (logger.isDebugEnabled()) {
            logger.debug("Default protocols (JDK): {} ", (Object)Arrays.asList(DEFAULT_PROTOCOLS));
            logger.debug("Default cipher suites (JDK): {}", (Object)DEFAULT_CIPHERS);
        }
    }

    private static final class Defaults {
        String[] defaultProtocols;
        List<String> defaultCiphers;
        List<String> defaultCiphersNonTLSv13;
        Set<String> supportedCiphers;
        Set<String> supportedCiphersNonTLSv13;
        Provider defaultProvider;

        private Defaults() {
        }

        void init() {
            SSLContext context;
            try {
                context = SSLContext.getInstance(JdkSslContext.PROTOCOL);
                context.init(null, null, null);
            }
            catch (Exception e) {
                throw new Error("failed to initialize the default SSL context", e);
            }
            this.defaultProvider = context.getProvider();
            SSLEngine engine = context.createSSLEngine();
            this.defaultProtocols = JdkSslContext.defaultProtocols(context, engine);
            this.supportedCiphers = Collections.unmodifiableSet(JdkSslContext.supportedCiphers(engine));
            this.defaultCiphers = Collections.unmodifiableList(JdkSslContext.defaultCiphers(engine, this.supportedCiphers));
            ArrayList<String> ciphersNonTLSv13 = new ArrayList<String>(this.defaultCiphers);
            ciphersNonTLSv13.removeAll(Arrays.asList(SslUtils.DEFAULT_TLSV13_CIPHER_SUITES));
            this.defaultCiphersNonTLSv13 = Collections.unmodifiableList(ciphersNonTLSv13);
            LinkedHashSet<String> suppertedCiphersNonTLSv13 = new LinkedHashSet<String>(this.supportedCiphers);
            suppertedCiphersNonTLSv13.removeAll(Arrays.asList(SslUtils.DEFAULT_TLSV13_CIPHER_SUITES));
            this.supportedCiphersNonTLSv13 = Collections.unmodifiableSet(suppertedCiphersNonTLSv13);
        }
    }
}

