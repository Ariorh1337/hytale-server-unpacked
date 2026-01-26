/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.BoringSSLKeylog;
import io.netty.handler.codec.quic.QuicheQuicSslEngine;
import io.netty.handler.codec.quic.QuicheQuicSslEngineMap;

final class BoringSSLKeylogCallback {
    private final QuicheQuicSslEngineMap engineMap;
    private final BoringSSLKeylog keylog;

    BoringSSLKeylogCallback(QuicheQuicSslEngineMap engineMap, BoringSSLKeylog keylog) {
        this.engineMap = engineMap;
        this.keylog = keylog;
    }

    void logKey(long ssl, String key) {
        QuicheQuicSslEngine engine = this.engineMap.get(ssl);
        if (engine != null) {
            this.keylog.logKey(engine, key);
        }
    }
}

