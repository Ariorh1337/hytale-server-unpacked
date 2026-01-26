/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import java.net.InetSocketAddress;

public interface QuicTokenHandler {
    public boolean writeToken(ByteBuf var1, ByteBuf var2, InetSocketAddress var3);

    public int validateToken(ByteBuf var1, InetSocketAddress var2);

    public int maxTokenLength();
}

