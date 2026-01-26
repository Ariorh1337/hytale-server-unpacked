/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.SslSessionTicketKey;
import javax.net.ssl.SSLSessionContext;

public interface QuicSslSessionContext
extends SSLSessionContext {
    public void setTicketKeys(SslSessionTicketKey ... var1);
}

