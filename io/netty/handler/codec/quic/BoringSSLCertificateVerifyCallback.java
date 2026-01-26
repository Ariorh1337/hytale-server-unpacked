/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.BoringSSL;
import io.netty.handler.codec.quic.QuicheQuicSslEngine;
import io.netty.handler.codec.quic.QuicheQuicSslEngineMap;
import io.netty.handler.ssl.OpenSslCertificateException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateRevokedException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;
import org.jetbrains.annotations.Nullable;

final class BoringSSLCertificateVerifyCallback {
    private static final boolean TRY_USING_EXTENDED_TRUST_MANAGER;
    private final QuicheQuicSslEngineMap engineMap;
    private final X509TrustManager manager;

    BoringSSLCertificateVerifyCallback(QuicheQuicSslEngineMap engineMap, @Nullable X509TrustManager manager) {
        this.engineMap = engineMap;
        this.manager = manager;
    }

    int verify(long ssl, byte[][] x509, String authAlgorithm) {
        QuicheQuicSslEngine engine = this.engineMap.get(ssl);
        if (engine == null) {
            return BoringSSL.X509_V_ERR_UNSPECIFIED;
        }
        if (this.manager == null) {
            this.engineMap.remove(ssl);
            return BoringSSL.X509_V_ERR_UNSPECIFIED;
        }
        X509Certificate[] peerCerts = BoringSSL.certificates(x509);
        try {
            if (engine.getUseClientMode()) {
                if (TRY_USING_EXTENDED_TRUST_MANAGER && this.manager instanceof X509ExtendedTrustManager) {
                    ((X509ExtendedTrustManager)this.manager).checkServerTrusted(peerCerts, authAlgorithm, engine);
                } else {
                    this.manager.checkServerTrusted(peerCerts, authAlgorithm);
                }
            } else if (TRY_USING_EXTENDED_TRUST_MANAGER && this.manager instanceof X509ExtendedTrustManager) {
                ((X509ExtendedTrustManager)this.manager).checkClientTrusted(peerCerts, authAlgorithm, engine);
            } else {
                this.manager.checkClientTrusted(peerCerts, authAlgorithm);
            }
            return BoringSSL.X509_V_OK;
        }
        catch (Throwable cause) {
            this.engineMap.remove(ssl);
            if (cause instanceof OpenSslCertificateException) {
                return ((OpenSslCertificateException)cause).errorCode();
            }
            if (cause instanceof CertificateExpiredException) {
                return BoringSSL.X509_V_ERR_CERT_HAS_EXPIRED;
            }
            if (cause instanceof CertificateNotYetValidException) {
                return BoringSSL.X509_V_ERR_CERT_NOT_YET_VALID;
            }
            return BoringSSLCertificateVerifyCallback.translateToError(cause);
        }
    }

    private static int translateToError(Throwable cause) {
        if (cause instanceof CertificateRevokedException) {
            return BoringSSL.X509_V_ERR_CERT_REVOKED;
        }
        for (Throwable wrapped = cause.getCause(); wrapped != null; wrapped = wrapped.getCause()) {
            if (!(wrapped instanceof CertPathValidatorException)) continue;
            CertPathValidatorException ex = (CertPathValidatorException)wrapped;
            CertPathValidatorException.Reason reason = ex.getReason();
            if (reason == CertPathValidatorException.BasicReason.EXPIRED) {
                return BoringSSL.X509_V_ERR_CERT_HAS_EXPIRED;
            }
            if (reason == CertPathValidatorException.BasicReason.NOT_YET_VALID) {
                return BoringSSL.X509_V_ERR_CERT_NOT_YET_VALID;
            }
            if (reason != CertPathValidatorException.BasicReason.REVOKED) continue;
            return BoringSSL.X509_V_ERR_CERT_REVOKED;
        }
        return BoringSSL.X509_V_ERR_UNSPECIFIED;
    }

    static {
        boolean tryUsingExtendedTrustManager;
        try {
            Class.forName(X509ExtendedTrustManager.class.getName());
            tryUsingExtendedTrustManager = true;
        }
        catch (Throwable cause) {
            tryUsingExtendedTrustManager = false;
        }
        TRY_USING_EXTENDED_TRUST_MANAGER = tryUsingExtendedTrustManager;
    }
}

