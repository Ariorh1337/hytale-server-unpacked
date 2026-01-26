/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http3;

import io.netty.handler.codec.http3.Http3Headers;
import io.netty.handler.codec.http3.Http3RequestStreamFrame;

public interface Http3PushPromiseFrame
extends Http3RequestStreamFrame {
    @Override
    default public long type() {
        return 5L;
    }

    public long id();

    public Http3Headers headers();
}

