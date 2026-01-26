/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.QuicheQuicChannel;
import java.net.SocketAddress;

final class QuicheQuicChannelAddress
extends SocketAddress {
    final QuicheQuicChannel channel;

    QuicheQuicChannelAddress(QuicheQuicChannel channel) {
        this.channel = channel;
    }
}

