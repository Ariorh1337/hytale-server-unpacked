/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.CodecOutputList;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.PromiseCombiner;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class MessageToMessageEncoder<I>
extends ChannelOutboundHandlerAdapter {
    private final TypeParameterMatcher matcher;

    protected MessageToMessageEncoder() {
        this.matcher = TypeParameterMatcher.find(this, MessageToMessageEncoder.class, "I");
    }

    protected MessageToMessageEncoder(Class<? extends I> outboundMessageType) {
        this.matcher = TypeParameterMatcher.get(outboundMessageType);
    }

    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return this.matcher.match(msg);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        block21: {
            CodecOutputList out = null;
            try {
                if (this.acceptOutboundMessage(msg)) {
                    out = CodecOutputList.newInstance();
                    Object cast = msg;
                    try {
                        this.encode(ctx, cast, out);
                    }
                    catch (Throwable th) {
                        ReferenceCountUtil.safeRelease(cast);
                        PlatformDependent.throwException(th);
                    }
                    ReferenceCountUtil.release(cast);
                    if (out.isEmpty()) {
                        throw new EncoderException(StringUtil.simpleClassName(this) + " must produce at least one message.");
                    }
                    break block21;
                }
                ctx.write(msg, promise);
            }
            catch (EncoderException e) {
                throw e;
            }
            catch (Throwable t) {
                throw new EncoderException(t);
            }
            finally {
                if (out != null) {
                    try {
                        int sizeMinusOne = out.size() - 1;
                        if (sizeMinusOne == 0) {
                            ctx.write(out.getUnsafe(0), promise);
                        } else if (sizeMinusOne > 0) {
                            if (promise == ctx.voidPromise()) {
                                MessageToMessageEncoder.writeVoidPromise(ctx, out);
                            } else {
                                MessageToMessageEncoder.writePromiseCombiner(ctx, out, promise);
                            }
                        }
                    }
                    finally {
                        out.recycle();
                    }
                }
            }
        }
    }

    private static void writeVoidPromise(ChannelHandlerContext ctx, CodecOutputList out) {
        ChannelPromise voidPromise = ctx.voidPromise();
        for (int i = 0; i < out.size(); ++i) {
            ctx.write(out.getUnsafe(i), voidPromise);
        }
    }

    private static void writePromiseCombiner(ChannelHandlerContext ctx, CodecOutputList out, ChannelPromise promise) {
        PromiseCombiner combiner = new PromiseCombiner(ctx.executor());
        for (int i = 0; i < out.size(); ++i) {
            combiner.add(ctx.write(out.getUnsafe(i)));
        }
        combiner.finish(promise);
    }

    protected abstract void encode(ChannelHandlerContext var1, I var2, List<Object> var3) throws Exception;
}

