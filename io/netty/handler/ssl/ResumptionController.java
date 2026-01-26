/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.JdkSslEngine;
import io.netty.handler.ssl.ResumableX509ExtendedTrustManager;
import java.net.Socket;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

final class ResumptionController {
    private final Set<SSLEngine> confirmedValidations = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
    private final AtomicReference<ResumableX509ExtendedTrustManager> resumableTm = new AtomicReference();

    ResumptionController() {
    }

    public TrustManager wrapIfNeeded(TrustManager tm) {
        if (tm instanceof ResumableX509ExtendedTrustManager) {
            if (!(tm instanceof X509ExtendedTrustManager)) {
                throw new IllegalStateException("ResumableX509ExtendedTrustManager implementation must be a subclass of X509ExtendedTrustManager, found: " + (tm == null ? null : tm.getClass()));
            }
            if (!this.resumableTm.compareAndSet(null, (ResumableX509ExtendedTrustManager)tm)) {
                throw new IllegalStateException("Only one ResumableX509ExtendedTrustManager can be configured for resumed sessions");
            }
            return new X509ExtendedWrapTrustManager((X509ExtendedTrustManager)tm, this.confirmedValidations);
        }
        return tm;
    }

    public void remove(SSLEngine engine) {
        if (this.resumableTm.get() != null) {
            this.confirmedValidations.remove(ResumptionController.unwrapEngine(engine));
        }
    }

    public boolean validateResumeIfNeeded(SSLEngine engine) throws CertificateException, SSLPeerUnverifiedException {
        ResumableX509ExtendedTrustManager tm;
        SSLSession session = engine.getSession();
        boolean valid = session.isValid();
        if (valid && (engine.getUseClientMode() || engine.getNeedClientAuth() || engine.getWantClientAuth()) && (tm = this.resumableTm.get()) != null && !this.confirmedValidations.remove(engine = ResumptionController.unwrapEngine(engine))) {
            Certificate[] peerCertificates;
            try {
                peerCertificates = session.getPeerCertificates();
            }
            catch (SSLPeerUnverifiedException e) {
                if (engine.getUseClientMode() || engine.getNeedClientAuth()) {
                    throw e;
                }
                return false;
            }
            if (engine.getUseClientMode()) {
                tm.resumeServerTrusted(ResumptionController.chainOf(peerCertificates), engine);
            } else {
                tm.resumeClientTrusted(ResumptionController.chainOf(peerCertificates), engine);
            }
            return true;
        }
        return false;
    }

    private static SSLEngine unwrapEngine(SSLEngine engine) {
        if (engine instanceof JdkSslEngine) {
            return ((JdkSslEngine)engine).getWrappedEngine();
        }
        return engine;
    }

    private static X509Certificate[] chainOf(Certificate[] peerCertificates) {
        if (peerCertificates instanceof X509Certificate[]) {
            return (X509Certificate[])peerCertificates;
        }
        X509Certificate[] chain = new X509Certificate[peerCertificates.length];
        for (int i = 0; i < peerCertificates.length; ++i) {
            Certificate cert = peerCertificates[i];
            if (!(cert instanceof X509Certificate) && cert != null) {
                throw new IllegalArgumentException("Only X509Certificates are supported, found: " + cert.getClass());
            }
            chain[i] = (X509Certificate)cert;
        }
        return chain;
    }

    private static final class X509ExtendedWrapTrustManager
    extends X509ExtendedTrustManager {
        private final X509ExtendedTrustManager trustManager;
        private final Set<SSLEngine> confirmedValidations;

        X509ExtendedWrapTrustManager(X509ExtendedTrustManager trustManager, Set<SSLEngine> confirmedValidations) {
            this.trustManager = trustManager;
            this.confirmedValidations = confirmedValidations;
        }

        private static void unsupported() throws CertificateException {
            throw new CertificateException(new UnsupportedOperationException("Resumable trust managers require the SSLEngine parameter"));
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
            X509ExtendedWrapTrustManager.unsupported();
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
            X509ExtendedWrapTrustManager.unsupported();
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            X509ExtendedWrapTrustManager.unsupported();
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            X509ExtendedWrapTrustManager.unsupported();
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
            this.trustManager.checkClientTrusted(chain, authType, engine);
            this.confirmedValidations.add(engine);
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
            this.trustManager.checkServerTrusted(chain, authType, engine);
            this.confirmedValidations.add(engine);
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return this.trustManager.getAcceptedIssuers();
        }
    }
}

