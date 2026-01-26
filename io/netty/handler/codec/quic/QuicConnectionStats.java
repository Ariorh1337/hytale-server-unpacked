/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

public interface QuicConnectionStats {
    public long recv();

    public long sent();

    public long lost();

    public long retrans();

    public long sentBytes();

    public long recvBytes();

    public long lostBytes();

    public long streamRetransBytes();

    public long pathsCount();
}

