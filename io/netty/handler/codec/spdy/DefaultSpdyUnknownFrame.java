/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.handler.codec.spdy.SpdyUnknownFrame;
import io.netty.util.internal.StringUtil;

public final class DefaultSpdyUnknownFrame
extends DefaultByteBufHolder
implements SpdyUnknownFrame {
    private final int frameType;
    private final byte flags;

    public DefaultSpdyUnknownFrame(int frameType, byte flags, ByteBuf data) {
        super(data);
        this.frameType = frameType;
        this.flags = flags;
    }

    @Override
    public int frameType() {
        return this.frameType;
    }

    @Override
    public byte flags() {
        return this.flags;
    }

    @Override
    public DefaultSpdyUnknownFrame copy() {
        return this.replace(this.content().copy());
    }

    @Override
    public DefaultSpdyUnknownFrame duplicate() {
        return this.replace(this.content().duplicate());
    }

    @Override
    public DefaultSpdyUnknownFrame retainedDuplicate() {
        return this.replace(this.content().retainedDuplicate());
    }

    @Override
    public DefaultSpdyUnknownFrame replace(ByteBuf content) {
        return new DefaultSpdyUnknownFrame(this.frameType, this.flags, content);
    }

    @Override
    public DefaultSpdyUnknownFrame retain() {
        super.retain();
        return this;
    }

    @Override
    public DefaultSpdyUnknownFrame retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public DefaultSpdyUnknownFrame touch() {
        super.touch();
        return this;
    }

    @Override
    public DefaultSpdyUnknownFrame touch(Object hint) {
        super.touch(hint);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DefaultSpdyUnknownFrame)) {
            return false;
        }
        DefaultSpdyUnknownFrame that = (DefaultSpdyUnknownFrame)o;
        return this.frameType == that.frameType && this.flags == that.flags && super.equals(that);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.frameType;
        result = 31 * result + this.flags;
        return result;
    }

    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + "(frameType=" + this.frameType + ", flags=" + this.flags + ", content=" + this.contentToString() + ')';
    }
}

