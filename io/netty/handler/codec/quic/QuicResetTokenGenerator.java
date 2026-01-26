/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.HmacSignQuicResetTokenGenerator;
import java.nio.ByteBuffer;

public interface QuicResetTokenGenerator {
    public ByteBuffer newResetToken(ByteBuffer var1);

    public static QuicResetTokenGenerator signGenerator() {
        return HmacSignQuicResetTokenGenerator.INSTANCE;
    }
}

