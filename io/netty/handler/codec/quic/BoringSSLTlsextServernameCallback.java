/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.QuicSslContext;
import io.netty.handler.codec.quic.QuicheQuicSslContext;
import io.netty.handler.codec.quic.QuicheQuicSslEngine;
import io.netty.handler.codec.quic.QuicheQuicSslEngineMap;
import io.netty.util.Mapping;

final class BoringSSLTlsextServernameCallback {
    private final QuicheQuicSslEngineMap engineMap;
    private final Mapping<? super String, ? extends QuicSslContext> mapping;

    BoringSSLTlsextServernameCallback(QuicheQuicSslEngineMap engineMap, Mapping<? super String, ? extends QuicSslContext> mapping) {
        this.engineMap = engineMap;
        this.mapping = mapping;
    }

    long selectCtx(long ssl, String serverName) {
        QuicheQuicSslEngine engine = this.engineMap.get(ssl);
        if (engine == null) {
            return -1L;
        }
        QuicSslContext context = this.mapping.map(serverName);
        if (context == null) {
            return -1L;
        }
        return engine.moveTo(serverName, (QuicheQuicSslContext)context);
    }
}

