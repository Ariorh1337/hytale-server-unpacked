/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.HmacSignQuicConnectionIdGenerator;
import io.netty.handler.codec.quic.SecureRandomQuicConnectionIdGenerator;
import java.nio.ByteBuffer;

public interface QuicConnectionIdGenerator {
    public ByteBuffer newId(int var1);

    public ByteBuffer newId(ByteBuffer var1, int var2);

    default public ByteBuffer newId(ByteBuffer scid, ByteBuffer dcid, int length) {
        return this.newId(dcid, length);
    }

    public int maxConnectionIdLength();

    public boolean isIdempotent();

    public static QuicConnectionIdGenerator randomGenerator() {
        return SecureRandomQuicConnectionIdGenerator.INSTANCE;
    }

    public static QuicConnectionIdGenerator signGenerator() {
        return HmacSignQuicConnectionIdGenerator.INSTANCE;
    }
}

