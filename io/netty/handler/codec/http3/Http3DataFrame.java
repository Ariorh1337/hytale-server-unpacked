/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.http3.Http3PushStreamFrame;
import io.netty.handler.codec.http3.Http3RequestStreamFrame;

public interface Http3DataFrame
extends ByteBufHolder,
Http3RequestStreamFrame,
Http3PushStreamFrame {
    @Override
    default public long type() {
        return 0L;
    }

    @Override
    public Http3DataFrame copy();

    @Override
    public Http3DataFrame duplicate();

    @Override
    public Http3DataFrame retainedDuplicate();

    @Override
    public Http3DataFrame replace(ByteBuf var1);

    @Override
    public Http3DataFrame retain();

    @Override
    public Http3DataFrame retain(int var1);

    @Override
    public Http3DataFrame touch();

    @Override
    public Http3DataFrame touch(Object var1);
}

