/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http3;

import io.netty.handler.codec.http3.Http3Headers;
import io.netty.handler.codec.http3.Http3PushStreamFrame;
import io.netty.handler.codec.http3.Http3RequestStreamFrame;

public interface Http3HeadersFrame
extends Http3RequestStreamFrame,
Http3PushStreamFrame {
    @Override
    default public long type() {
        return 1L;
    }

    public Http3Headers headers();
}

