/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.CodecOutputList;
import io.netty.handler.codec.DecoderException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class MessageToMessageDecoder<I>
extends ChannelInboundHandlerAdapter {
    private final TypeParameterMatcher matcher;
    private boolean decodeCalled;
    private boolean messageProduced;

    protected MessageToMessageDecoder() {
        this.matcher = TypeParameterMatcher.find(this, MessageToMessageDecoder.class, "I");
    }

    protected MessageToMessageDecoder(Class<? extends I> inboundMessageType) {
        this.matcher = TypeParameterMatcher.get(inboundMessageType);
    }

    public boolean acceptInboundMessage(Object msg) throws Exception {
        return this.matcher.match(msg);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        CodecOutputList out;
        block17: {
            this.decodeCalled = true;
            out = CodecOutputList.newInstance();
            try {
                if (this.acceptInboundMessage(msg)) {
                    Object cast = msg;
                    try {
                        this.decode(ctx, cast, out);
                        break block17;
                    }
                    finally {
                        ReferenceCountUtil.release(cast);
                    }
                }
                out.add(msg);
            }
            catch (DecoderException e) {
                try {
                    throw e;
                    catch (Exception e2) {
                        throw new DecoderException(e2);
                    }
                }
                catch (Throwable throwable) {
                    try {
                        int size = out.size();
                        this.messageProduced |= size > 0;
                        for (int i = 0; i < size; ++i) {
                            ctx.fireChannelRead(out.getUnsafe(i));
                        }
                        throw throwable;
                    }
                    finally {
                        out.recycle();
                    }
                }
            }
        }
        try {
            int size = out.size();
            this.messageProduced |= size > 0;
            for (int i = 0; i < size; ++i) {
                ctx.fireChannelRead(out.getUnsafe(i));
            }
            return;
        }
        finally {
            out.recycle();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (!this.isSharable()) {
            if (this.decodeCalled && !this.messageProduced && !ctx.channel().config().isAutoRead()) {
                ctx.read();
            }
            this.decodeCalled = false;
            this.messageProduced = false;
        }
        ctx.fireChannelReadComplete();
    }

    protected abstract void decode(ChannelHandlerContext var1, I var2, List<Object> var3) throws Exception;
}

