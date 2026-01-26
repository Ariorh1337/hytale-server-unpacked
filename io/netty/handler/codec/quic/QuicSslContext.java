/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.quic.QuicSslEngine;
import io.netty.handler.codec.quic.QuicSslSessionContext;
import io.netty.handler.ssl.SslContext;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public abstract class QuicSslContext
extends SslContext {
    @Override
    public abstract QuicSslEngine newEngine(ByteBufAllocator var1);

    @Override
    public abstract QuicSslEngine newEngine(ByteBufAllocator var1, String var2, int var3);

    @Override
    public abstract QuicSslSessionContext sessionContext();

    static X509Certificate[] toX509Certificates0(InputStream stream) throws CertificateException {
        return SslContext.toX509Certificates(stream);
    }
}

