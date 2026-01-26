/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.quic.QuicConnectionIdGenerator;
import io.netty.handler.codec.quic.QuicException;
import io.netty.handler.codec.quic.QuicHeaderParser;
import io.netty.handler.codec.quic.SecureRandomQuicConnectionIdGenerator;
import io.netty.util.internal.ObjectUtil;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.Nullable;

public abstract class QuicCodecDispatcher
extends ChannelInboundHandlerAdapter {
    private static final int MAX_LOCAL_CONNECTION_ID_LENGTH = 20;
    private final List<ChannelHandlerContextDispatcher> contextList = new CopyOnWriteArrayList<ChannelHandlerContextDispatcher>();
    private final int localConnectionIdLength;

    protected QuicCodecDispatcher() {
        this(20);
    }

    protected QuicCodecDispatcher(int localConnectionIdLength) {
        this.localConnectionIdLength = ObjectUtil.checkInRange(localConnectionIdLength, 10, 20, "localConnectionIdLength");
    }

    @Override
    public final boolean isSharable() {
        return true;
    }

    @Override
    public final void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        ChannelHandlerContextDispatcher ctxDispatcher = new ChannelHandlerContextDispatcher(ctx);
        this.contextList.add(ctxDispatcher);
        int idx = this.contextList.indexOf(ctxDispatcher);
        try {
            QuicConnectionIdGenerator idGenerator = this.newIdGenerator((short)idx);
            this.initChannel(ctx.channel(), this.localConnectionIdLength, idGenerator);
        }
        catch (Exception e) {
            this.contextList.set(idx, null);
            throw e;
        }
    }

    @Override
    public final void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        for (int idx = 0; idx < this.contextList.size(); ++idx) {
            ChannelHandlerContextDispatcher ctxDispatcher = this.contextList.get(idx);
            if (ctxDispatcher == null || !ctxDispatcher.ctx.equals(ctx)) continue;
            this.contextList.set(idx, null);
            break;
        }
    }

    @Override
    public final void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        DatagramPacket packet = (DatagramPacket)msg;
        ByteBuf connectionId = QuicCodecDispatcher.getDestinationConnectionId((ByteBuf)packet.content(), this.localConnectionIdLength);
        if (connectionId != null) {
            ChannelHandlerContextDispatcher selectedCtx;
            int idx = this.decodeIndex(connectionId);
            if (this.contextList.size() > idx && (selectedCtx = this.contextList.get(idx)) != null) {
                selectedCtx.fireChannelRead(msg);
                return;
            }
        }
        ctx.fireChannelRead(msg);
    }

    @Override
    public final void channelReadComplete(ChannelHandlerContext ctx) {
        boolean dispatchForOwnContextAlready = false;
        for (int i = 0; i < this.contextList.size(); ++i) {
            boolean fired;
            ChannelHandlerContextDispatcher ctxDispatcher = this.contextList.get(i);
            if (ctxDispatcher == null || !(fired = ctxDispatcher.fireChannelReadCompleteIfNeeded()) || dispatchForOwnContextAlready) continue;
            dispatchForOwnContextAlready = ctx.equals(ctxDispatcher.ctx);
        }
        if (!dispatchForOwnContextAlready) {
            ctx.fireChannelReadComplete();
        }
    }

    protected abstract void initChannel(Channel var1, int var2, QuicConnectionIdGenerator var3) throws Exception;

    protected int decodeIndex(ByteBuf connectionId) {
        return QuicCodecDispatcher.decodeIdx(connectionId);
    }

    @Nullable
    static ByteBuf getDestinationConnectionId(ByteBuf buffer, int localConnectionIdLength) throws QuicException {
        if (buffer.readableBytes() > 1) {
            int offset = buffer.readerIndex();
            boolean shortHeader = QuicCodecDispatcher.hasShortHeader(buffer);
            ++offset;
            if (shortHeader) {
                return QuicHeaderParser.sliceCid(buffer, offset, localConnectionIdLength);
            }
        }
        return null;
    }

    static boolean hasShortHeader(ByteBuf buffer) {
        return QuicHeaderParser.hasShortHeader(buffer.getByte(buffer.readerIndex()));
    }

    static int decodeIdx(ByteBuf connectionId) {
        if (connectionId.readableBytes() >= 2) {
            return connectionId.getUnsignedShort(connectionId.readerIndex());
        }
        return -1;
    }

    static ByteBuffer encodeIdx(ByteBuffer buffer, int idx) {
        ByteBuffer b = ByteBuffer.allocate(buffer.capacity() + 2);
        b.putShort((short)idx).put(buffer).flip();
        return b;
    }

    protected QuicConnectionIdGenerator newIdGenerator(int idx) {
        return new IndexAwareQuicConnectionIdGenerator(idx, SecureRandomQuicConnectionIdGenerator.INSTANCE);
    }

    private static final class ChannelHandlerContextDispatcher
    extends AtomicBoolean {
        private final ChannelHandlerContext ctx;

        ChannelHandlerContextDispatcher(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        void fireChannelRead(Object msg) {
            this.ctx.fireChannelRead(msg);
            this.set(true);
        }

        boolean fireChannelReadCompleteIfNeeded() {
            if (this.getAndSet(false)) {
                this.ctx.fireChannelReadComplete();
                return true;
            }
            return false;
        }
    }

    private static final class IndexAwareQuicConnectionIdGenerator
    implements QuicConnectionIdGenerator {
        private final int idx;
        private final QuicConnectionIdGenerator idGenerator;

        IndexAwareQuicConnectionIdGenerator(int idx, QuicConnectionIdGenerator idGenerator) {
            this.idx = idx;
            this.idGenerator = idGenerator;
        }

        @Override
        public ByteBuffer newId(int length) {
            if (length > 2) {
                return QuicCodecDispatcher.encodeIdx(this.idGenerator.newId(length - 2), this.idx);
            }
            return this.idGenerator.newId(length);
        }

        @Override
        public ByteBuffer newId(ByteBuffer input, int length) {
            if (length > 2) {
                return QuicCodecDispatcher.encodeIdx(this.idGenerator.newId(input, length - 2), this.idx);
            }
            return this.idGenerator.newId(input, length);
        }

        @Override
        public ByteBuffer newId(ByteBuffer scid, ByteBuffer dcid, int length) {
            if (length > 2) {
                return QuicCodecDispatcher.encodeIdx(this.idGenerator.newId(scid, dcid, length - 2), this.idx);
            }
            return this.idGenerator.newId(scid, dcid, length);
        }

        @Override
        public int maxConnectionIdLength() {
            return this.idGenerator.maxConnectionIdLength();
        }

        @Override
        public boolean isIdempotent() {
            return false;
        }
    }
}

