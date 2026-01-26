/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http3;

import io.netty.handler.codec.http3.Http3ControlStreamFrame;

public interface Http3MaxPushIdFrame
extends Http3ControlStreamFrame {
    @Override
    default public long type() {
        return 13L;
    }

    public long id();
}

