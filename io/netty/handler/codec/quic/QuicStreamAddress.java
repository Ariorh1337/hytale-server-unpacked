/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import java.net.SocketAddress;
import java.util.Objects;

public final class QuicStreamAddress
extends SocketAddress {
    private final long streamId;

    public QuicStreamAddress(long streamId) {
        this.streamId = streamId;
    }

    public long streamId() {
        return this.streamId;
    }

    public boolean equals(Object o) {
        if (!(o instanceof QuicStreamAddress)) {
            return false;
        }
        QuicStreamAddress that = (QuicStreamAddress)o;
        return this.streamId == that.streamId;
    }

    public int hashCode() {
        return Objects.hash(this.streamId);
    }

    public String toString() {
        return "QuicStreamAddress{streamId=" + this.streamId + '}';
    }
}

