/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.spdy.SpdyFrame;

public interface SpdyUnknownFrame
extends SpdyFrame,
ByteBufHolder {
    public int frameType();

    public byte flags();

    @Override
    public SpdyUnknownFrame copy();

    @Override
    public SpdyUnknownFrame duplicate();

    @Override
    public SpdyUnknownFrame retainedDuplicate();

    @Override
    public SpdyUnknownFrame replace(ByteBuf var1);

    @Override
    public SpdyUnknownFrame retain();

    @Override
    public SpdyUnknownFrame retain(int var1);

    @Override
    public SpdyUnknownFrame touch();

    @Override
    public SpdyUnknownFrame touch(Object var1);
}

