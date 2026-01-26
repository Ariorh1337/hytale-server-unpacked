/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.nio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.AbstractNioChannel;
import io.netty.channel.nio.NioIoOps;
import java.io.IOException;
import java.net.PortUnreachableException;
import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNioMessageChannel
extends AbstractNioChannel {
    boolean inputShutdown;

    protected AbstractNioMessageChannel(Channel parent, SelectableChannel ch, int readInterestOp) {
        super(parent, ch, readInterestOp);
    }

    protected AbstractNioMessageChannel(Channel parent, SelectableChannel ch, NioIoOps readOps) {
        super(parent, ch, readOps);
    }

    @Override
    protected AbstractNioChannel.AbstractNioUnsafe newUnsafe() {
        return new NioMessageUnsafe();
    }

    @Override
    protected void doBeginRead() throws Exception {
        if (this.inputShutdown) {
            return;
        }
        super.doBeginRead();
    }

    protected boolean continueReading(RecvByteBufAllocator.Handle allocHandle) {
        return allocHandle.continueReading();
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        Object msg;
        int maxMessagesPerWrite = this.maxMessagesPerWrite();
        while (maxMessagesPerWrite > 0 && (msg = in.current()) != null) {
            try {
                boolean done = false;
                for (int i = this.config().getWriteSpinCount() - 1; i >= 0; --i) {
                    if (!this.doWriteMessage(msg, in)) continue;
                    done = true;
                    break;
                }
                if (!done) break;
                --maxMessagesPerWrite;
                in.remove();
            }
            catch (Exception e) {
                if (this.continueOnWriteError()) {
                    --maxMessagesPerWrite;
                    in.remove(e);
                    continue;
                }
                throw e;
            }
        }
        if (in.isEmpty()) {
            this.removeAndSubmit(NioIoOps.WRITE);
        } else {
            this.addAndSubmit(NioIoOps.WRITE);
        }
    }

    protected boolean continueOnWriteError() {
        return false;
    }

    protected boolean closeOnReadError(Throwable cause) {
        if (!this.isActive()) {
            return true;
        }
        if (cause instanceof PortUnreachableException) {
            return false;
        }
        if (cause instanceof IOException) {
            return !(this instanceof ServerChannel);
        }
        return true;
    }

    protected abstract int doReadMessages(List<Object> var1) throws Exception;

    protected abstract boolean doWriteMessage(Object var1, ChannelOutboundBuffer var2) throws Exception;

    private final class NioMessageUnsafe
    extends AbstractNioChannel.AbstractNioUnsafe {
        private final List<Object> readBuf;

        private NioMessageUnsafe() {
            super(AbstractNioMessageChannel.this);
            this.readBuf = new ArrayList<Object>();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void read() {
            assert (AbstractNioMessageChannel.this.eventLoop().inEventLoop());
            ChannelConfig config = AbstractNioMessageChannel.this.config();
            ChannelPipeline pipeline = AbstractNioMessageChannel.this.pipeline();
            RecvByteBufAllocator.Handle allocHandle = AbstractNioMessageChannel.this.unsafe().recvBufAllocHandle();
            allocHandle.reset(config);
            boolean closed = false;
            Throwable exception = null;
            try {
                try {
                    int localRead;
                    while ((localRead = AbstractNioMessageChannel.this.doReadMessages(this.readBuf)) != 0) {
                        if (localRead < 0) {
                            closed = true;
                        } else {
                            allocHandle.incMessagesRead(localRead);
                            if (AbstractNioMessageChannel.this.continueReading(allocHandle)) continue;
                        }
                        break;
                    }
                }
                catch (Throwable t) {
                    exception = t;
                }
                int size = this.readBuf.size();
                for (int i = 0; i < size; ++i) {
                    AbstractNioMessageChannel.this.readPending = false;
                    pipeline.fireChannelRead(this.readBuf.get(i));
                }
                this.readBuf.clear();
                allocHandle.readComplete();
                pipeline.fireChannelReadComplete();
                if (exception != null) {
                    closed = AbstractNioMessageChannel.this.closeOnReadError(exception);
                    pipeline.fireExceptionCaught(exception);
                }
                if (closed) {
                    AbstractNioMessageChannel.this.inputShutdown = true;
                    if (AbstractNioMessageChannel.this.isOpen()) {
                        this.close(this.voidPromise());
                    }
                }
            }
            finally {
                if (!AbstractNioMessageChannel.this.readPending && !config.isAutoRead()) {
                    this.removeReadOp();
                }
            }
        }
    }
}

