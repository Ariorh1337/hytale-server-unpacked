/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.jcraft.jzlib.Inflater
 *  com.jcraft.jzlib.JZlib
 */
package io.netty.handler.codec.compression;

import com.jcraft.jzlib.Inflater;
import com.jcraft.jzlib.JZlib;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.compression.ZlibDecoder;
import io.netty.handler.codec.compression.ZlibUtil;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public class JZlibDecoder
extends ZlibDecoder {
    private final Inflater z = new Inflater();
    private byte[] dictionary;
    private boolean needsRead;
    private volatile boolean finished;

    @Deprecated
    public JZlibDecoder() {
        this(ZlibWrapper.ZLIB, 0);
    }

    public JZlibDecoder(int maxAllocation) {
        this(ZlibWrapper.ZLIB, maxAllocation);
    }

    @Deprecated
    public JZlibDecoder(ZlibWrapper wrapper) {
        this(wrapper, 0);
    }

    public JZlibDecoder(ZlibWrapper wrapper, int maxAllocation) {
        super(maxAllocation);
        ObjectUtil.checkNotNull(wrapper, "wrapper");
        int resultCode = this.z.init(ZlibUtil.convertWrapperType(wrapper));
        if (resultCode != 0) {
            ZlibUtil.fail(this.z, "initialization failure", resultCode);
        }
    }

    @Deprecated
    public JZlibDecoder(byte[] dictionary) {
        this(dictionary, 0);
    }

    public JZlibDecoder(byte[] dictionary, int maxAllocation) {
        super(maxAllocation);
        this.dictionary = ObjectUtil.checkNotNull(dictionary, "dictionary");
        int resultCode = this.z.inflateInit(JZlib.W_ZLIB);
        if (resultCode != 0) {
            ZlibUtil.fail(this.z, "initialization failure", resultCode);
        }
    }

    @Override
    public boolean isClosed() {
        return this.finished;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        this.needsRead = true;
        if (this.finished) {
            in.skipBytes(in.readableBytes());
            return;
        }
        int inputLength = in.readableBytes();
        if (inputLength == 0) {
            return;
        }
        try {
            this.z.avail_in = inputLength;
            if (in.hasArray()) {
                this.z.next_in = in.array();
                this.z.next_in_index = in.arrayOffset() + in.readerIndex();
            } else {
                byte[] array = new byte[inputLength];
                in.getBytes(in.readerIndex(), array);
                this.z.next_in = array;
                this.z.next_in_index = 0;
            }
            int oldNextInIndex = this.z.next_in_index;
            ByteBuf decompressed = this.prepareDecompressBuffer(ctx, null, inputLength << 1);
            block12: while (true) {
                block13: while (true) {
                    decompressed = this.prepareDecompressBuffer(ctx, decompressed, this.z.avail_in << 1);
                    this.z.avail_out = decompressed.writableBytes();
                    this.z.next_out = decompressed.array();
                    int oldNextOutIndex = this.z.next_out_index = decompressed.arrayOffset() + decompressed.writerIndex();
                    int resultCode = this.z.inflate(2);
                    int outputLength = this.z.next_out_index - oldNextOutIndex;
                    if (outputLength > 0) {
                        decompressed.writerIndex(decompressed.writerIndex() + outputLength);
                        if (this.maxAllocation == 0) {
                            ByteBuf buffer = decompressed;
                            decompressed = null;
                            this.needsRead = false;
                            ctx.fireChannelRead(buffer);
                        }
                    }
                    switch (resultCode) {
                        case 2: {
                            if (this.dictionary == null) {
                                ZlibUtil.fail(this.z, "decompression failure", resultCode);
                                continue block12;
                            }
                            resultCode = this.z.inflateSetDictionary(this.dictionary, this.dictionary.length);
                            if (resultCode == 0) continue block12;
                            ZlibUtil.fail(this.z, "failed to set the dictionary", resultCode);
                            continue block12;
                        }
                        case 1: {
                            this.finished = true;
                            this.z.inflateEnd();
                            return;
                        }
                        case 0: {
                            continue block12;
                        }
                        case -5: {
                            if (this.z.avail_in <= 0) return;
                            continue block12;
                        }
                        default: {
                            ZlibUtil.fail(this.z, "decompression failure", resultCode);
                            continue block13;
                        }
                    }
                    break;
                }
                break;
            }
            finally {
                in.skipBytes(this.z.next_in_index - oldNextInIndex);
                if (decompressed != null) {
                    if (decompressed.isReadable()) {
                        this.needsRead = false;
                        ctx.fireChannelRead(decompressed);
                    } else {
                        decompressed.release();
                    }
                }
            }
        }
        finally {
            this.z.next_in = null;
            this.z.next_out = null;
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        this.discardSomeReadBytes();
        if (this.needsRead && !ctx.channel().config().isAutoRead()) {
            ctx.read();
        }
        ctx.fireChannelReadComplete();
    }

    @Override
    protected void decompressionBufferExhausted(ByteBuf buffer) {
        this.finished = true;
    }
}

