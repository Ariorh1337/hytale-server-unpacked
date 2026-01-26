/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.QuicheQuicSslEngine;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.jetbrains.annotations.Nullable;

final class QuicheQuicSslEngineMap {
    private final ConcurrentMap<Long, QuicheQuicSslEngine> engines = new ConcurrentHashMap<Long, QuicheQuicSslEngine>();

    QuicheQuicSslEngineMap() {
    }

    @Nullable
    QuicheQuicSslEngine get(long ssl) {
        return (QuicheQuicSslEngine)this.engines.get(ssl);
    }

    @Nullable
    QuicheQuicSslEngine remove(long ssl) {
        return (QuicheQuicSslEngine)this.engines.remove(ssl);
    }

    void put(long ssl, QuicheQuicSslEngine engine) {
        this.engines.put(ssl, engine);
    }
}

