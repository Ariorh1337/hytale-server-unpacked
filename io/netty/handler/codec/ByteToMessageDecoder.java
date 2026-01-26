/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.handler.codec.CodecOutputList;
import io.netty.handler.codec.DecoderException;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public abstract class ByteToMessageDecoder
extends ChannelInboundHandlerAdapter {
    public static final Cumulator MERGE_CUMULATOR = new Cumulator(){

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ByteBuf cumulate(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf in) {
            if (cumulation == in) {
                in.release();
                return cumulation;
            }
            if (!cumulation.isReadable() && in.isContiguous()) {
                cumulation.release();
                return in;
            }
            try {
                int required = in.readableBytes();
                if (required > cumulation.maxWritableBytes() || required > cumulation.maxFastWritableBytes() && cumulation.refCnt() > 1 || cumulation.isReadOnly()) {
                    ByteBuf byteBuf = ByteToMessageDecoder.expandCumulation(alloc, cumulation, in);
                    return byteBuf;
                }
                cumulation.writeBytes(in, in.readerIndex(), required);
                in.readerIndex(in.writerIndex());
                ByteBuf byteBuf = cumulation;
                return byteBuf;
            }
            finally {
                in.release();
            }
        }
    };
    public static final Cumulator COMPOSITE_CUMULATOR = new Cumulator(){

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ByteBuf cumulate(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf in) {
            if (cumulation == in) {
                in.release();
                return cumulation;
            }
            if (!cumulation.isReadable()) {
                cumulation.release();
                return in;
            }
            CompositeByteBuf composite = null;
            try {
                if (cumulation instanceof CompositeByteBuf && cumulation.refCnt() == 1) {
                    composite = (CompositeByteBuf)cumulation;
                    if (composite.writerIndex() != composite.capacity()) {
                        composite.capacity(composite.writerIndex());
                    }
                } else {
                    composite = alloc.compositeBuffer(Integer.MAX_VALUE).addFlattenedComponents(true, cumulation);
                }
                composite.addFlattenedComponents(true, in);
                in = null;
                CompositeByteBuf compositeByteBuf = composite;
                return compositeByteBuf;
            }
            finally {
                if (in != null) {
                    in.release();
                    if (composite != null && composite != cumulation) {
                        composite.release();
                    }
                }
            }
        }
    };
    private static final byte STATE_INIT = 0;
    private static final byte STATE_CALLING_CHILD_DECODE = 1;
    private static final byte STATE_HANDLER_REMOVED_PENDING = 2;
    private Queue<Object> inputMessages;
    ByteBuf cumulation;
    private Cumulator cumulator = MERGE_CUMULATOR;
    private boolean singleDecode;
    private boolean first;
    private boolean firedChannelRead;
    private boolean selfFiredChannelRead;
    private byte decodeState = 0;
    private int discardAfterReads = 16;
    private int numReads;

    protected ByteToMessageDecoder() {
        this.ensureNotSharable();
    }

    public void setSingleDecode(boolean singleDecode) {
        this.singleDecode = singleDecode;
    }

    public boolean isSingleDecode() {
        return this.singleDecode;
    }

    public void setCumulator(Cumulator cumulator) {
        this.cumulator = ObjectUtil.checkNotNull(cumulator, "cumulator");
    }

    public void setDiscardAfterReads(int discardAfterReads) {
        ObjectUtil.checkPositive(discardAfterReads, "discardAfterReads");
        this.discardAfterReads = discardAfterReads;
    }

    protected int actualReadableBytes() {
        return this.internalBuffer().readableBytes();
    }

    protected ByteBuf internalBuffer() {
        if (this.cumulation != null) {
            return this.cumulation;
        }
        return Unpooled.EMPTY_BUFFER;
    }

    @Override
    public final void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if (this.decodeState == 1) {
            this.decodeState = (byte)2;
            return;
        }
        ByteBuf buf = this.cumulation;
        if (buf != null) {
            this.cumulation = null;
            this.numReads = 0;
            int readable = buf.readableBytes();
            if (readable > 0) {
                ctx.fireChannelRead(buf);
                ctx.fireChannelReadComplete();
            } else {
                buf.release();
            }
        }
        this.handlerRemoved0(ctx);
    }

    protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object input) throws Exception {
        if (this.decodeState == 0) {
            do {
                if (input instanceof ByteBuf) {
                    this.selfFiredChannelRead = true;
                    CodecOutputList out = CodecOutputList.newInstance();
                    try {
                        this.first = this.cumulation == null;
                        this.cumulation = this.cumulator.cumulate(ctx.alloc(), this.first ? Unpooled.EMPTY_BUFFER : this.cumulation, (ByteBuf)input);
                        this.callDecode(ctx, this.cumulation, out);
                    }
                    catch (DecoderException e) {
                        throw e;
                    }
                    catch (Exception e) {
                        throw new DecoderException(e);
                    }
                    finally {
                        try {
                            if (this.cumulation != null && !this.cumulation.isReadable()) {
                                this.numReads = 0;
                                try {
                                    this.cumulation.release();
                                }
                                catch (IllegalReferenceCountException e) {
                                    throw new IllegalReferenceCountException(this.getClass().getSimpleName() + "#decode() might have released its input buffer, or passed it down the pipeline without a retain() call, which is not allowed.", e);
                                }
                                this.cumulation = null;
                            } else if (++this.numReads >= this.discardAfterReads) {
                                this.numReads = 0;
                                this.discardSomeReadBytes();
                            }
                            int size = out.size();
                            this.firedChannelRead |= out.insertSinceRecycled();
                            ByteToMessageDecoder.fireChannelRead(ctx, out, size);
                        }
                        finally {
                            out.recycle();
                        }
                    }
                }
                ctx.fireChannelRead(input);
            } while (this.inputMessages != null && (input = this.inputMessages.poll()) != null);
        } else {
            if (this.inputMessages == null) {
                this.inputMessages = new ArrayDeque<Object>(2);
            }
            this.inputMessages.offer(input);
        }
    }

    static void fireChannelRead(ChannelHandlerContext ctx, List<Object> msgs, int numElements) {
        if (msgs instanceof CodecOutputList) {
            ByteToMessageDecoder.fireChannelRead(ctx, (CodecOutputList)msgs, numElements);
        } else {
            for (int i = 0; i < numElements; ++i) {
                ctx.fireChannelRead(msgs.get(i));
            }
        }
    }

    static void fireChannelRead(ChannelHandlerContext ctx, CodecOutputList msgs, int numElements) {
        for (int i = 0; i < numElements; ++i) {
            ctx.fireChannelRead(msgs.getUnsafe(i));
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        this.numReads = 0;
        this.discardSomeReadBytes();
        if (this.selfFiredChannelRead && !this.firedChannelRead && !ctx.channel().config().isAutoRead()) {
            ctx.read();
        }
        this.firedChannelRead = false;
        this.selfFiredChannelRead = false;
        ctx.fireChannelReadComplete();
    }

    protected final void discardSomeReadBytes() {
        if (this.cumulation != null && !this.first && this.cumulation.refCnt() == 1) {
            this.cumulation.discardSomeReadBytes();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.channelInputClosed(ctx, true);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof ChannelInputShutdownEvent) {
            this.channelInputClosed(ctx, false);
        }
        super.userEventTriggered(ctx, evt);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void channelInputClosed(ChannelHandlerContext ctx, boolean callChannelInactive) {
        CodecOutputList out = CodecOutputList.newInstance();
        try {
            this.channelInputClosed(ctx, out);
        }
        catch (DecoderException e) {
            throw e;
        }
        catch (Exception e) {
            throw new DecoderException(e);
        }
        finally {
            try {
                if (this.cumulation != null) {
                    this.cumulation.release();
                    this.cumulation = null;
                }
                int size = out.size();
                ByteToMessageDecoder.fireChannelRead(ctx, out, size);
                if (size > 0) {
                    ctx.fireChannelReadComplete();
                }
                if (callChannelInactive) {
                    ctx.fireChannelInactive();
                }
            }
            finally {
                out.recycle();
            }
        }
    }

    void channelInputClosed(ChannelHandlerContext ctx, List<Object> out) throws Exception {
        if (this.cumulation != null) {
            this.callDecode(ctx, this.cumulation, out);
            if (!ctx.isRemoved()) {
                ByteBuf buffer = this.cumulation == null ? Unpooled.EMPTY_BUFFER : this.cumulation;
                this.decodeLast(ctx, buffer, out);
            }
        } else {
            this.decodeLast(ctx, Unpooled.EMPTY_BUFFER, out);
        }
    }

    protected void callDecode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        try {
            while (in.isReadable()) {
                int outSize = out.size();
                if (outSize > 0) {
                    ByteToMessageDecoder.fireChannelRead(ctx, out, outSize);
                    out.clear();
                    if (ctx.isRemoved()) break;
                }
                int oldInputLength = in.readableBytes();
                this.decodeRemovalReentryProtection(ctx, in, out);
                if (!ctx.isRemoved()) {
                    if (out.isEmpty()) {
                        if (oldInputLength != in.readableBytes()) continue;
                    } else {
                        if (oldInputLength == in.readableBytes()) {
                            throw new DecoderException(StringUtil.simpleClassName(this.getClass()) + ".decode() did not read anything but decoded a message.");
                        }
                        if (!this.isSingleDecode()) continue;
                    }
                }
                break;
            }
        }
        catch (DecoderException e) {
            throw e;
        }
        catch (Exception cause) {
            throw new DecoderException(cause);
        }
    }

    protected abstract void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final void decodeRemovalReentryProtection(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        this.decodeState = 1;
        try {
            this.decode(ctx, in, out);
        }
        catch (Throwable throwable) {
            if (this.inputMessages == null || this.inputMessages.isEmpty()) {
                boolean removePending = this.decodeState == 2;
                this.decodeState = 0;
                if (removePending) {
                    ByteToMessageDecoder.fireChannelRead(ctx, out, out.size());
                    out.clear();
                    this.handlerRemoved(ctx);
                }
            }
            throw throwable;
        }
        if (this.inputMessages == null || this.inputMessages.isEmpty()) {
            boolean removePending = this.decodeState == 2;
            this.decodeState = 0;
            if (removePending) {
                ByteToMessageDecoder.fireChannelRead(ctx, out, out.size());
                out.clear();
                this.handlerRemoved(ctx);
            }
        }
    }

    protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.isReadable()) {
            this.decodeRemovalReentryProtection(ctx, in, out);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static ByteBuf expandCumulation(ByteBufAllocator alloc, ByteBuf oldCumulation, ByteBuf in) {
        ByteBuf newCumulation;
        int oldBytes = oldCumulation.readableBytes();
        int newBytes = in.readableBytes();
        int totalBytes = oldBytes + newBytes;
        ByteBuf toRelease = newCumulation = alloc.buffer(alloc.calculateNewCapacity(totalBytes, Integer.MAX_VALUE));
        try {
            newCumulation.setBytes(0, oldCumulation, oldCumulation.readerIndex(), oldBytes).setBytes(oldBytes, in, in.readerIndex(), newBytes).writerIndex(totalBytes);
            in.readerIndex(in.writerIndex());
            toRelease = oldCumulation;
            ByteBuf byteBuf = newCumulation;
            return byteBuf;
        }
        finally {
            toRelease.release();
        }
    }

    public static interface Cumulator {
        public ByteBuf cumulate(ByteBufAllocator var1, ByteBuf var2, ByteBuf var3);
    }
}

