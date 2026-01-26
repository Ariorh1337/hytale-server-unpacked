/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.QuicheQuicSslEngine;
import io.netty.handler.codec.quic.QuicheQuicSslEngineMap;

final class BoringSSLHandshakeCompleteCallback {
    private final QuicheQuicSslEngineMap map;

    BoringSSLHandshakeCompleteCallback(QuicheQuicSslEngineMap map) {
        this.map = map;
    }

    void handshakeComplete(long ssl, byte[] id, String cipher, String protocol, byte[] peerCertificate, byte[][] peerCertificateChain, long creationTime, long timeout, byte[] applicationProtocol, boolean sessionReused) {
        QuicheQuicSslEngine engine = this.map.get(ssl);
        if (engine != null) {
            engine.handshakeFinished(id, cipher, protocol, peerCertificate, peerCertificateChain, creationTime, timeout, applicationProtocol, sessionReused);
        }
    }
}

