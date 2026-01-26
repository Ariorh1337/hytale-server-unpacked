/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import java.net.InetSocketAddress;

public interface QuicConnectionPathStats {
    public InetSocketAddress localAddress();

    public InetSocketAddress peerAddress();

    public long validationState();

    public boolean active();

    public long recv();

    public long sent();

    public long lost();

    public long retrans();

    public long rtt();

    public long cwnd();

    public long sentBytes();

    public long recvBytes();

    public long lostBytes();

    public long streamRetransBytes();

    public long pmtu();

    public long deliveryRate();
}

