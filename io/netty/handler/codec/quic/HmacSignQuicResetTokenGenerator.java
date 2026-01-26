/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.handler.codec.quic.Hmac;
import io.netty.handler.codec.quic.QuicResetTokenGenerator;
import io.netty.util.internal.ObjectUtil;
import java.nio.ByteBuffer;

final class HmacSignQuicResetTokenGenerator
implements QuicResetTokenGenerator {
    static final QuicResetTokenGenerator INSTANCE = new HmacSignQuicResetTokenGenerator();

    private HmacSignQuicResetTokenGenerator() {
    }

    @Override
    public ByteBuffer newResetToken(ByteBuffer cid) {
        ObjectUtil.checkNotNull(cid, "cid");
        ObjectUtil.checkPositive(cid.remaining(), "cid");
        return Hmac.sign(cid, 16);
    }
}

