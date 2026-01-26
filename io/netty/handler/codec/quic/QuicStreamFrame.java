/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.quic.DefaultQuicStreamFrame;

public interface QuicStreamFrame
extends ByteBufHolder {
    public static final QuicStreamFrame EMPTY_FIN = new QuicStreamFrame(){

        @Override
        public boolean hasFin() {
            return true;
        }

        @Override
        public QuicStreamFrame copy() {
            return this;
        }

        @Override
        public QuicStreamFrame duplicate() {
            return this;
        }

        @Override
        public QuicStreamFrame retainedDuplicate() {
            return this;
        }

        @Override
        public QuicStreamFrame replace(ByteBuf content) {
            return new DefaultQuicStreamFrame(content, this.hasFin());
        }

        @Override
        public QuicStreamFrame retain() {
            return this;
        }

        @Override
        public QuicStreamFrame retain(int increment) {
            return this;
        }

        @Override
        public QuicStreamFrame touch() {
            return this;
        }

        @Override
        public QuicStreamFrame touch(Object hint) {
            return this;
        }

        @Override
        public ByteBuf content() {
            return Unpooled.EMPTY_BUFFER;
        }

        @Override
        public int refCnt() {
            return 1;
        }

        @Override
        public boolean release() {
            return false;
        }

        @Override
        public boolean release(int decrement) {
            return false;
        }
    };

    public boolean hasFin();

    @Override
    public QuicStreamFrame copy();

    @Override
    public QuicStreamFrame duplicate();

    @Override
    public QuicStreamFrame retainedDuplicate();

    @Override
    public QuicStreamFrame replace(ByteBuf var1);

    @Override
    public QuicStreamFrame retain();

    @Override
    public QuicStreamFrame retain(int var1);

    @Override
    public QuicStreamFrame touch();

    @Override
    public QuicStreamFrame touch(Object var1);
}

