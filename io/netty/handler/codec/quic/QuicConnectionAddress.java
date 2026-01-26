/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.quic.Quic;
import io.netty.handler.codec.quic.QuicConnectionIdGenerator;
import io.netty.handler.codec.quic.Quiche;
import io.netty.util.internal.EmptyArrays;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Objects;

public final class QuicConnectionAddress
extends SocketAddress {
    static final QuicConnectionAddress NULL_LEN = new QuicConnectionAddress(EmptyArrays.EMPTY_BYTES);
    public static final QuicConnectionAddress EPHEMERAL = new QuicConnectionAddress(null, false);
    private final String toStr;
    private final ByteBuffer connId;

    public QuicConnectionAddress(byte[] connId) {
        this(ByteBuffer.wrap((byte[])connId.clone()), true);
    }

    public QuicConnectionAddress(ByteBuffer connId) {
        this(connId.duplicate(), true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private QuicConnectionAddress(ByteBuffer connId, boolean validate) {
        Quic.ensureAvailability();
        if (validate && connId.remaining() > Quiche.QUICHE_MAX_CONN_ID_LEN) {
            throw new IllegalArgumentException("Connection ID can only be of max length " + Quiche.QUICHE_MAX_CONN_ID_LEN);
        }
        if (connId == null) {
            this.connId = null;
            this.toStr = "QuicConnectionAddress{EPHEMERAL}";
        } else {
            this.connId = connId.asReadOnlyBuffer().duplicate();
            ByteBuf buffer = Unpooled.wrappedBuffer(connId);
            try {
                this.toStr = "QuicConnectionAddress{connId=" + ByteBufUtil.hexDump(buffer) + '}';
            }
            finally {
                buffer.release();
            }
        }
    }

    public String toString() {
        return this.toStr;
    }

    public int hashCode() {
        if (this == EPHEMERAL) {
            return System.identityHashCode(EPHEMERAL);
        }
        return Objects.hash(this.connId);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof QuicConnectionAddress)) {
            return false;
        }
        QuicConnectionAddress address = (QuicConnectionAddress)obj;
        if (obj == this) {
            return true;
        }
        return this.connId.equals(address.connId);
    }

    ByteBuffer id() {
        if (this.connId == null) {
            return ByteBuffer.allocate(0);
        }
        return this.connId.duplicate();
    }

    public static QuicConnectionAddress random(int length) {
        return new QuicConnectionAddress(QuicConnectionIdGenerator.randomGenerator().newId(length));
    }

    public static QuicConnectionAddress random() {
        return QuicConnectionAddress.random(Quiche.QUICHE_MAX_CONN_ID_LEN);
    }
}

