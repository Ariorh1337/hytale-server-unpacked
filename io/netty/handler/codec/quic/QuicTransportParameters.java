/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

public interface QuicTransportParameters {
    public long maxIdleTimeout();

    public long maxUdpPayloadSize();

    public long initialMaxData();

    public long initialMaxStreamDataBidiLocal();

    public long initialMaxStreamDataBidiRemote();

    public long initialMaxStreamDataUni();

    public long initialMaxStreamsBidi();

    public long initialMaxStreamsUni();

    public long ackDelayExponent();

    public long maxAckDelay();

    public boolean disableActiveMigration();

    public long activeConnIdLimit();

    public long maxDatagramFrameSize();
}

