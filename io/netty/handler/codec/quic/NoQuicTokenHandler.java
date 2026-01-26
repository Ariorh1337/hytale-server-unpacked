/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.quic.QuicTokenHandler;
import java.net.InetSocketAddress;

final class NoQuicTokenHandler
implements QuicTokenHandler {
    public static final QuicTokenHandler INSTANCE = new NoQuicTokenHandler();

    private NoQuicTokenHandler() {
    }

    @Override
    public boolean writeToken(ByteBuf out, ByteBuf dcid, InetSocketAddress address) {
        return false;
    }

    @Override
    public int validateToken(ByteBuf token, InetSocketAddress address) {
        return 0;
    }

    @Override
    public int maxTokenLength() {
        return 0;
    }
}

