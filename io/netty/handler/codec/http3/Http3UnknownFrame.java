/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.http3.Http3ControlStreamFrame;
import io.netty.handler.codec.http3.Http3PushStreamFrame;
import io.netty.handler.codec.http3.Http3RequestStreamFrame;

public interface Http3UnknownFrame
extends Http3RequestStreamFrame,
Http3PushStreamFrame,
Http3ControlStreamFrame,
ByteBufHolder {
    default public long length() {
        return this.content().readableBytes();
    }

    @Override
    public Http3UnknownFrame copy();

    @Override
    public Http3UnknownFrame duplicate();

    @Override
    public Http3UnknownFrame retainedDuplicate();

    @Override
    public Http3UnknownFrame replace(ByteBuf var1);

    @Override
    public Http3UnknownFrame retain();

    @Override
    public Http3UnknownFrame retain(int var1);

    @Override
    public Http3UnknownFrame touch();

    @Override
    public Http3UnknownFrame touch(Object var1);
}

