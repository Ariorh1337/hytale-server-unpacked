/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.QuicClientSessionCache;
import io.netty.handler.codec.quic.QuicheQuicSslEngine;
import io.netty.handler.codec.quic.QuicheQuicSslEngineMap;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.Nullable;

final class BoringSSLSessionCallback {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(BoringSSLSessionCallback.class);
    private final QuicClientSessionCache sessionCache;
    private final QuicheQuicSslEngineMap engineMap;

    BoringSSLSessionCallback(QuicheQuicSslEngineMap engineMap, @Nullable QuicClientSessionCache sessionCache) {
        this.engineMap = engineMap;
        this.sessionCache = sessionCache;
    }

    void newSession(long ssl, long creationTime, long timeout, byte[] session, boolean isSingleUse, byte @Nullable [] peerParams) {
        byte[] quicSession;
        if (this.sessionCache == null) {
            return;
        }
        QuicheQuicSslEngine engine = this.engineMap.get(ssl);
        if (engine == null) {
            logger.warn("engine is null ssl: {}", (Object)ssl);
            return;
        }
        if (peerParams == null) {
            peerParams = EmptyArrays.EMPTY_BYTES;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("ssl: {}, session: {}, peerParams: {}", ssl, Arrays.toString(session), Arrays.toString(peerParams));
        }
        if ((quicSession = BoringSSLSessionCallback.toQuicheQuicSession(session, peerParams)) != null) {
            logger.debug("save session host={}, port={}", (Object)engine.getSession().getPeerHost(), (Object)engine.getSession().getPeerPort());
            this.sessionCache.saveSession(engine.getSession().getPeerHost(), engine.getSession().getPeerPort(), TimeUnit.SECONDS.toMillis(creationTime), TimeUnit.SECONDS.toMillis(timeout), quicSession, isSingleUse);
        }
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private static byte @Nullable [] toQuicheQuicSession(byte @Nullable [] sslSession, byte @Nullable [] peerParams) {
        if (sslSession != null && peerParams != null) {
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();){
                byte[] byArray;
                try (DataOutputStream dos = new DataOutputStream(bos);){
                    dos.writeLong(sslSession.length);
                    dos.write(sslSession);
                    dos.writeLong(peerParams.length);
                    dos.write(peerParams);
                    byArray = bos.toByteArray();
                }
                return byArray;
            }
            catch (IOException e) {
                return null;
            }
        }
        return null;
    }
}

