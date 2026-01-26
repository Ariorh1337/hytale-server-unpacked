/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.QuicClientSessionCache;
import io.netty.handler.codec.quic.QuicSslEngine;
import io.netty.handler.codec.quic.QuicheQuicConnection;
import io.netty.handler.codec.quic.QuicheQuicSslContext;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.util.LazyJavaxX509Certificate;
import io.netty.handler.ssl.util.LazyX509Certificate;
import io.netty.util.NetUtil;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import java.nio.ByteBuffer;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.LongFunction;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionBindingEvent;
import javax.net.ssl.SSLSessionBindingListener;
import javax.net.ssl.SSLSessionContext;
import javax.security.cert.X509Certificate;
import org.jetbrains.annotations.Nullable;

final class QuicheQuicSslEngine
extends QuicSslEngine {
    QuicheQuicSslContext ctx;
    private final String peerHost;
    private final int peerPort;
    private final QuicheQuicSslSession session = new QuicheQuicSslSession();
    private volatile Certificate[] localCertificateChain;
    private List<SNIServerName> sniHostNames;
    private boolean handshakeFinished;
    private String applicationProtocol;
    private boolean sessionReused;
    final String tlsHostName;
    volatile QuicheQuicConnection connection;
    volatile Consumer<String> sniSelectedCallback;

    QuicheQuicSslEngine(QuicheQuicSslContext ctx, @Nullable String peerHost, int peerPort) {
        this.ctx = ctx;
        this.peerHost = peerHost;
        this.peerPort = peerPort;
        if (ctx.isClient() && QuicheQuicSslEngine.isValidHostNameForSNI(peerHost)) {
            this.tlsHostName = peerHost;
            this.sniHostNames = Collections.singletonList(new SNIHostName(this.tlsHostName));
        } else {
            this.tlsHostName = null;
        }
    }

    long moveTo(String hostname, QuicheQuicSslContext ctx) {
        this.ctx.remove(this);
        this.ctx = ctx;
        long added = ctx.add(this);
        Consumer<String> sniSelectedCallback = this.sniSelectedCallback;
        if (sniSelectedCallback != null) {
            sniSelectedCallback.accept(hostname);
        }
        return added;
    }

    @Nullable
    QuicheQuicConnection createConnection(LongFunction<Long> connectionCreator) {
        return this.ctx.createConnection(connectionCreator, this);
    }

    void setLocalCertificateChain(Certificate[] localCertificateChain) {
        this.localCertificateChain = localCertificateChain;
    }

    static boolean isValidHostNameForSNI(@Nullable String hostname) {
        return hostname != null && hostname.indexOf(46) > 0 && !hostname.endsWith(".") && !NetUtil.isValidIpV4Address(hostname) && !NetUtil.isValidIpV6Address(hostname);
    }

    @Override
    public SSLParameters getSSLParameters() {
        SSLParameters parameters = super.getSSLParameters();
        parameters.setServerNames(this.sniHostNames);
        return parameters;
    }

    @Override
    public synchronized String getApplicationProtocol() {
        return this.applicationProtocol;
    }

    @Override
    public synchronized String getHandshakeApplicationProtocol() {
        return this.applicationProtocol;
    }

    @Override
    public SSLEngineResult wrap(ByteBuffer[] srcs, int offset, int length, ByteBuffer dst) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts, int offset, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Nullable
    public Runnable getDelegatedTask() {
        return null;
    }

    @Override
    public void closeInbound() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isInboundDone() {
        return false;
    }

    @Override
    public void closeOutbound() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOutboundDone() {
        return false;
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return this.ctx.cipherSuites().toArray(new String[0]);
    }

    @Override
    public String[] getEnabledCipherSuites() {
        return this.getSupportedCipherSuites();
    }

    @Override
    public void setEnabledCipherSuites(String[] suites) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getSupportedProtocols() {
        return new String[]{"TLSv1.3"};
    }

    @Override
    public String[] getEnabledProtocols() {
        return this.getSupportedProtocols();
    }

    @Override
    public void setEnabledProtocols(String[] protocols) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SSLSession getSession() {
        return this.session;
    }

    @Override
    @Nullable
    public SSLSession getHandshakeSession() {
        if (this.handshakeFinished) {
            return null;
        }
        return this.session;
    }

    @Override
    public void beginHandshake() {
    }

    @Override
    public SSLEngineResult.HandshakeStatus getHandshakeStatus() {
        if (this.handshakeFinished) {
            return SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
        }
        return SSLEngineResult.HandshakeStatus.NEED_WRAP;
    }

    @Override
    public void setUseClientMode(boolean clientMode) {
        if (clientMode != this.ctx.isClient()) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean getUseClientMode() {
        return this.ctx.isClient();
    }

    @Override
    public void setNeedClientAuth(boolean b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getNeedClientAuth() {
        return this.ctx.clientAuth == ClientAuth.REQUIRE;
    }

    @Override
    public void setWantClientAuth(boolean b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getWantClientAuth() {
        return this.ctx.clientAuth == ClientAuth.OPTIONAL;
    }

    @Override
    public void setEnableSessionCreation(boolean flag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getEnableSessionCreation() {
        return false;
    }

    synchronized void handshakeFinished(byte[] id, String cipher, String protocol, byte[] peerCertificate, byte[][] peerCertificateChain, long creationTime, long timeout, byte @Nullable [] applicationProtocol, boolean sessionReused) {
        this.applicationProtocol = applicationProtocol == null ? null : new String(applicationProtocol);
        this.session.handshakeFinished(id, cipher, protocol, peerCertificate, peerCertificateChain, creationTime, timeout);
        this.sessionReused = sessionReused;
        this.handshakeFinished = true;
    }

    void removeSessionFromCacheIfInvalid() {
        this.session.removeFromCacheIfInvalid();
    }

    synchronized boolean isSessionReused() {
        return this.sessionReused;
    }

    private final class QuicheQuicSslSession
    implements SSLSession {
        private X509Certificate[] x509PeerCerts;
        private Certificate[] peerCerts;
        private String protocol;
        private String cipher;
        private byte[] id;
        private long creationTime = -1L;
        private long timeout = -1L;
        private boolean invalid;
        private long lastAccessedTime = -1L;
        private Map<String, Object> values;

        private QuicheQuicSslSession() {
        }

        private boolean isEmpty(Object @Nullable [] arr) {
            return arr == null || arr.length == 0;
        }

        private boolean isEmpty(byte @Nullable [] arr) {
            return arr == null || arr.length == 0;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        void handshakeFinished(byte[] id, String cipher, String protocol, byte[] peerCertificate, byte[][] peerCertificateChain, long creationTime, long timeout) {
            QuicheQuicSslEngine quicheQuicSslEngine = QuicheQuicSslEngine.this;
            synchronized (quicheQuicSslEngine) {
                this.initPeerCerts(peerCertificateChain, peerCertificate);
                this.id = id;
                this.cipher = cipher;
                this.protocol = protocol;
                this.creationTime = creationTime * 1000L;
                this.timeout = timeout * 1000L;
                this.lastAccessedTime = System.currentTimeMillis();
            }
        }

        void removeFromCacheIfInvalid() {
            if (!this.isValid()) {
                this.removeFromCache();
            }
        }

        private void removeFromCache() {
            QuicClientSessionCache cache = QuicheQuicSslEngine.this.ctx.getSessionCache();
            if (cache != null) {
                cache.removeSession(this.getPeerHost(), this.getPeerPort());
            }
        }

        private void initPeerCerts(byte[][] chain, byte[] clientCert) {
            if (QuicheQuicSslEngine.this.getUseClientMode()) {
                if (this.isEmpty((Object[])chain)) {
                    this.peerCerts = EmptyArrays.EMPTY_CERTIFICATES;
                    this.x509PeerCerts = EmptyArrays.EMPTY_JAVAX_X509_CERTIFICATES;
                } else {
                    this.peerCerts = new Certificate[chain.length];
                    this.x509PeerCerts = new X509Certificate[chain.length];
                    this.initCerts(chain, 0);
                }
            } else if (this.isEmpty(clientCert)) {
                this.peerCerts = EmptyArrays.EMPTY_CERTIFICATES;
                this.x509PeerCerts = EmptyArrays.EMPTY_JAVAX_X509_CERTIFICATES;
            } else if (this.isEmpty((Object[])chain)) {
                this.peerCerts = new Certificate[]{new LazyX509Certificate(clientCert)};
                this.x509PeerCerts = new X509Certificate[]{new LazyJavaxX509Certificate(clientCert)};
            } else {
                this.peerCerts = new Certificate[chain.length + 1];
                this.x509PeerCerts = new X509Certificate[chain.length + 1];
                this.peerCerts[0] = new LazyX509Certificate(clientCert);
                this.x509PeerCerts[0] = new LazyJavaxX509Certificate(clientCert);
                this.initCerts(chain, 1);
            }
        }

        private void initCerts(byte[][] chain, int startPos) {
            for (int i = 0; i < chain.length; ++i) {
                int certPos = startPos + i;
                this.peerCerts[certPos] = new LazyX509Certificate(chain[i]);
                this.x509PeerCerts[certPos] = new LazyJavaxX509Certificate(chain[i]);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte[] getId() {
            QuicheQuicSslSession quicheQuicSslSession = this;
            synchronized (quicheQuicSslSession) {
                if (this.id == null) {
                    return EmptyArrays.EMPTY_BYTES;
                }
                return (byte[])this.id.clone();
            }
        }

        @Override
        public SSLSessionContext getSessionContext() {
            return QuicheQuicSslEngine.this.ctx.sessionContext();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long getCreationTime() {
            QuicheQuicSslEngine quicheQuicSslEngine = QuicheQuicSslEngine.this;
            synchronized (quicheQuicSslEngine) {
                return this.creationTime;
            }
        }

        @Override
        public long getLastAccessedTime() {
            return this.lastAccessedTime;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void invalidate() {
            boolean removeFromCache;
            QuicheQuicSslSession quicheQuicSslSession = this;
            synchronized (quicheQuicSslSession) {
                removeFromCache = !this.invalid;
                this.invalid = true;
            }
            if (removeFromCache) {
                this.removeFromCache();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isValid() {
            QuicheQuicSslEngine quicheQuicSslEngine = QuicheQuicSslEngine.this;
            synchronized (quicheQuicSslEngine) {
                return !this.invalid && System.currentTimeMillis() - this.timeout < this.creationTime;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void putValue(String name, Object value) {
            Object old;
            ObjectUtil.checkNotNull(name, "name");
            ObjectUtil.checkNotNull(value, "value");
            QuicheQuicSslSession quicheQuicSslSession = this;
            synchronized (quicheQuicSslSession) {
                Map<String, Object> values = this.values;
                if (values == null) {
                    values = this.values = new HashMap<String, Object>(2);
                }
                old = values.put(name, value);
            }
            if (value instanceof SSLSessionBindingListener) {
                ((SSLSessionBindingListener)value).valueBound(this.newSSLSessionBindingEvent(name));
            }
            this.notifyUnbound(old, name);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Nullable
        public Object getValue(String name) {
            ObjectUtil.checkNotNull(name, "name");
            QuicheQuicSslSession quicheQuicSslSession = this;
            synchronized (quicheQuicSslSession) {
                if (this.values == null) {
                    return null;
                }
                return this.values.get(name);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeValue(String name) {
            Object old;
            ObjectUtil.checkNotNull(name, "name");
            QuicheQuicSslSession quicheQuicSslSession = this;
            synchronized (quicheQuicSslSession) {
                Map<String, Object> values = this.values;
                if (values == null) {
                    return;
                }
                old = values.remove(name);
            }
            this.notifyUnbound(old, name);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public String[] getValueNames() {
            QuicheQuicSslSession quicheQuicSslSession = this;
            synchronized (quicheQuicSslSession) {
                Map<String, Object> values = this.values;
                if (values == null || values.isEmpty()) {
                    return EmptyArrays.EMPTY_STRINGS;
                }
                return values.keySet().toArray(new String[0]);
            }
        }

        private SSLSessionBindingEvent newSSLSessionBindingEvent(String name) {
            return new SSLSessionBindingEvent(QuicheQuicSslEngine.this.session, name);
        }

        private void notifyUnbound(@Nullable Object value, String name) {
            if (value instanceof SSLSessionBindingListener) {
                ((SSLSessionBindingListener)value).valueUnbound(this.newSSLSessionBindingEvent(name));
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
            QuicheQuicSslEngine quicheQuicSslEngine = QuicheQuicSslEngine.this;
            synchronized (quicheQuicSslEngine) {
                if (this.isEmpty(this.peerCerts)) {
                    throw new SSLPeerUnverifiedException("peer not verified");
                }
                return (Certificate[])this.peerCerts.clone();
            }
        }

        @Override
        public Certificate @Nullable [] getLocalCertificates() {
            Certificate[] localCerts = QuicheQuicSslEngine.this.localCertificateChain;
            if (localCerts == null) {
                return null;
            }
            return (Certificate[])localCerts.clone();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
            QuicheQuicSslEngine quicheQuicSslEngine = QuicheQuicSslEngine.this;
            synchronized (quicheQuicSslEngine) {
                if (this.isEmpty(this.x509PeerCerts)) {
                    throw new SSLPeerUnverifiedException("peer not verified");
                }
                return (X509Certificate[])this.x509PeerCerts.clone();
            }
        }

        @Override
        public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
            Certificate[] peer = this.getPeerCertificates();
            return ((java.security.cert.X509Certificate)peer[0]).getSubjectX500Principal();
        }

        @Override
        @Nullable
        public Principal getLocalPrincipal() {
            Certificate[] local = QuicheQuicSslEngine.this.localCertificateChain;
            if (local == null || local.length == 0) {
                return null;
            }
            return ((java.security.cert.X509Certificate)local[0]).getIssuerX500Principal();
        }

        @Override
        public String getCipherSuite() {
            return this.cipher;
        }

        @Override
        public String getProtocol() {
            return this.protocol;
        }

        @Override
        @Nullable
        public String getPeerHost() {
            return QuicheQuicSslEngine.this.peerHost;
        }

        @Override
        public int getPeerPort() {
            return QuicheQuicSslEngine.this.peerPort;
        }

        @Override
        public int getPacketBufferSize() {
            return -1;
        }

        @Override
        public int getApplicationBufferSize() {
            return -1;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            QuicheQuicSslSession that = (QuicheQuicSslSession)o;
            return Arrays.equals(this.getId(), that.getId());
        }

        public int hashCode() {
            return Arrays.hashCode(this.getId());
        }
    }
}

