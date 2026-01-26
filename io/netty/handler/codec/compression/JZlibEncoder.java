/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.jcraft.jzlib.Deflater
 *  com.jcraft.jzlib.JZlib
 */
package io.netty.handler.codec.compression;

import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.JZlib;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.compression.ZlibEncoder;
import io.netty.handler.codec.compression.ZlibUtil;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.PromiseNotifier;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class JZlibEncoder
extends ZlibEncoder {
    private final int wrapperOverhead;
    private final Deflater z = new Deflater();
    private volatile boolean finished;
    private volatile ChannelHandlerContext ctx;
    private static final int THREAD_POOL_DELAY_SECONDS = 10;

    public JZlibEncoder() {
        this(6);
    }

    public JZlibEncoder(int compressionLevel) {
        this(ZlibWrapper.ZLIB, compressionLevel);
    }

    public JZlibEncoder(ZlibWrapper wrapper) {
        this(wrapper, 6);
    }

    public JZlibEncoder(ZlibWrapper wrapper, int compressionLevel) {
        this(wrapper, compressionLevel, 15, 8);
    }

    public JZlibEncoder(ZlibWrapper wrapper, int compressionLevel, int windowBits, int memLevel) {
        ObjectUtil.checkInRange(compressionLevel, 0, 9, "compressionLevel");
        ObjectUtil.checkInRange(windowBits, 9, 15, "windowBits");
        ObjectUtil.checkInRange(memLevel, 1, 9, "memLevel");
        ObjectUtil.checkNotNull(wrapper, "wrapper");
        if (wrapper == ZlibWrapper.ZLIB_OR_NONE) {
            throw new IllegalArgumentException("wrapper '" + (Object)((Object)ZlibWrapper.ZLIB_OR_NONE) + "' is not allowed for compression.");
        }
        int resultCode = this.z.init(compressionLevel, windowBits, memLevel, ZlibUtil.convertWrapperType(wrapper));
        if (resultCode != 0) {
            ZlibUtil.fail(this.z, "initialization failure", resultCode);
        }
        this.wrapperOverhead = ZlibUtil.wrapperOverhead(wrapper);
    }

    public JZlibEncoder(byte[] dictionary) {
        this(6, dictionary);
    }

    public JZlibEncoder(int compressionLevel, byte[] dictionary) {
        this(compressionLevel, 15, 8, dictionary);
    }

    public JZlibEncoder(int compressionLevel, int windowBits, int memLevel, byte[] dictionary) {
        ObjectUtil.checkInRange(compressionLevel, 0, 9, "compressionLevel");
        ObjectUtil.checkInRange(windowBits, 9, 15, "windowBits");
        ObjectUtil.checkInRange(memLevel, 1, 9, "memLevel");
        ObjectUtil.checkNotNull(dictionary, "dictionary");
        int resultCode = this.z.deflateInit(compressionLevel, windowBits, memLevel, JZlib.W_ZLIB);
        if (resultCode != 0) {
            ZlibUtil.fail(this.z, "initialization failure", resultCode);
        } else {
            resultCode = this.z.deflateSetDictionary(dictionary, dictionary.length);
            if (resultCode != 0) {
                ZlibUtil.fail(this.z, "failed to set the dictionary", resultCode);
            }
        }
        this.wrapperOverhead = ZlibUtil.wrapperOverhead(ZlibWrapper.ZLIB);
    }

    @Override
    public ChannelFuture close() {
        return this.close(this.ctx().channel().newPromise());
    }

    @Override
    public ChannelFuture close(final ChannelPromise promise) {
        ChannelHandlerContext ctx = this.ctx();
        EventExecutor executor = ctx.executor();
        if (executor.inEventLoop()) {
            return this.finishEncode(ctx, promise);
        }
        final ChannelPromise p = ctx.newPromise();
        executor.execute(new Runnable(){

            @Override
            public void run() {
                ChannelFuture f = JZlibEncoder.this.finishEncode(JZlibEncoder.this.ctx(), p);
                PromiseNotifier.cascade(f, promise);
            }
        });
        return p;
    }

    private ChannelHandlerContext ctx() {
        ChannelHandlerContext ctx = this.ctx;
        if (ctx == null) {
            throw new IllegalStateException("not added to a pipeline");
        }
        return ctx;
    }

    @Override
    public boolean isClosed() {
        return this.finished;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        if (this.finished) {
            out.writeBytes(in);
            return;
        }
        int inputLength = in.readableBytes();
        if (inputLength == 0) {
            return;
        }
        try {
            int outputLength;
            int resultCode;
            boolean inHasArray = in.hasArray();
            this.z.avail_in = inputLength;
            if (inHasArray) {
                this.z.next_in = in.array();
                this.z.next_in_index = in.arrayOffset() + in.readerIndex();
            } else {
                byte[] array = new byte[inputLength];
                in.getBytes(in.readerIndex(), array);
                this.z.next_in = array;
                this.z.next_in_index = 0;
            }
            int oldNextInIndex = this.z.next_in_index;
            int maxOutputLength = (int)Math.ceil((double)inputLength * 1.001) + 12 + this.wrapperOverhead;
            out.ensureWritable(maxOutputLength);
            this.z.avail_out = maxOutputLength;
            this.z.next_out = out.array();
            int oldNextOutIndex = this.z.next_out_index = out.arrayOffset() + out.writerIndex();
            try {
                resultCode = this.z.deflate(2);
            }
            finally {
                in.skipBytes(this.z.next_in_index - oldNextInIndex);
            }
            if (resultCode != 0) {
                ZlibUtil.fail(this.z, "compression failure", resultCode);
            }
            if ((outputLength = this.z.next_out_index - oldNextOutIndex) > 0) {
                out.writerIndex(out.writerIndex() + outputLength);
            }
        }
        finally {
            this.z.next_in = null;
            this.z.next_out = null;
        }
    }

    @Override
    public void close(final ChannelHandlerContext ctx, final ChannelPromise promise) {
        ChannelFuture f = this.finishEncode(ctx, ctx.newPromise());
        if (!f.isDone()) {
            ScheduledFuture future = ctx.executor().schedule(new Runnable(){

                @Override
                public void run() {
                    if (!promise.isDone()) {
                        ctx.close(promise);
                    }
                }
            }, 10L, TimeUnit.SECONDS);
            f.addListener((GenericFutureListener)new ChannelFutureListener((Future)((Object)future), promise, ctx){
                final /* synthetic */ Future val$future;
                final /* synthetic */ ChannelPromise val$promise;
                final /* synthetic */ ChannelHandlerContext val$ctx;
                {
                    this.val$future = future;
                    this.val$promise = channelPromise;
                    this.val$ctx = channelHandlerContext;
                }

                @Override
                public void operationComplete(ChannelFuture f) {
                    this.val$future.cancel(true);
                    if (!this.val$promise.isDone()) {
                        this.val$ctx.close(this.val$promise);
                    }
                }
            });
        } else {
            ctx.close(promise);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ChannelFuture finishEncode(ChannelHandlerContext ctx, ChannelPromise promise) {
        ByteBuf footer;
        if (this.finished) {
            promise.setSuccess();
            return promise;
        }
        this.finished = true;
        try {
            this.z.next_in = EmptyArrays.EMPTY_BYTES;
            this.z.next_in_index = 0;
            this.z.avail_in = 0;
            byte[] out = new byte[32];
            this.z.next_out = out;
            this.z.next_out_index = 0;
            this.z.avail_out = out.length;
            int resultCode = this.z.deflate(4);
            if (resultCode != 0 && resultCode != 1) {
                promise.setFailure(ZlibUtil.deflaterException(this.z, "compression failure", resultCode));
                ChannelPromise channelPromise = promise;
                return channelPromise;
            }
            footer = this.z.next_out_index != 0 ? Unpooled.wrappedBuffer(out, 0, this.z.next_out_index) : Unpooled.EMPTY_BUFFER;
        }
        finally {
            this.z.deflateEnd();
            this.z.next_in = null;
            this.z.next_out = null;
        }
        return ctx.writeAndFlush(footer, promise);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }
}

