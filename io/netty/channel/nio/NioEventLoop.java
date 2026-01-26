/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.nio;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopTaskQueueFactory;
import io.netty.channel.IoEventLoopGroup;
import io.netty.channel.IoHandlerFactory;
import io.netty.channel.IoRegistration;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.SingleThreadIoEventLoop;
import io.netty.channel.nio.AbstractNioChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.nio.NioIoHandle;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.nio.NioIoOps;
import io.netty.channel.nio.NioSelectableChannelIoHandle;
import io.netty.channel.nio.NioTask;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;

@Deprecated
public final class NioEventLoop
extends SingleThreadIoEventLoop {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioEventLoop.class);

    NioEventLoop(NioEventLoopGroup parent, Executor executor, IoHandlerFactory ioHandlerFactory, EventLoopTaskQueueFactory taskQueueFactory, EventLoopTaskQueueFactory tailTaskQueueFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        super((IoEventLoopGroup)parent, executor, ioHandlerFactory, NioEventLoop.newTaskQueue(taskQueueFactory), NioEventLoop.newTaskQueue(tailTaskQueueFactory), rejectedExecutionHandler);
    }

    private static Queue<Runnable> newTaskQueue(EventLoopTaskQueueFactory queueFactory) {
        if (queueFactory == null) {
            return NioEventLoop.newTaskQueue0(DEFAULT_MAX_PENDING_TASKS);
        }
        return queueFactory.newTaskQueue(DEFAULT_MAX_PENDING_TASKS);
    }

    public SelectorProvider selectorProvider() {
        return ((NioIoHandler)this.ioHandler()).selectorProvider();
    }

    public void register(final SelectableChannel ch, final int interestOps, NioTask<?> task) {
        ObjectUtil.checkNotNull(ch, "ch");
        if (interestOps == 0) {
            throw new IllegalArgumentException("interestOps must be non-zero.");
        }
        if ((interestOps & ~ch.validOps()) != 0) {
            throw new IllegalArgumentException("invalid interestOps: " + interestOps + "(validOps: " + ch.validOps() + ')');
        }
        ObjectUtil.checkNotNull(task, "task");
        if (this.isShutdown()) {
            throw new IllegalStateException("event loop shut down");
        }
        final NioTask<SelectableChannel> nioTask = task;
        if (this.inEventLoop()) {
            this.register0(ch, interestOps, nioTask);
        } else {
            try {
                this.submit(new Runnable(){

                    @Override
                    public void run() {
                        NioEventLoop.this.register0(ch, interestOps, nioTask);
                    }
                }).sync();
            }
            catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void register0(SelectableChannel ch, int interestOps, final NioTask<SelectableChannel> task) {
        try {
            IoRegistration registration = (IoRegistration)this.register(new NioSelectableChannelIoHandle<SelectableChannel>(ch){

                @Override
                protected void handle(SelectableChannel channel, SelectionKey key) {
                    try {
                        task.channelReady(channel, key);
                    }
                    catch (Exception e) {
                        logger.warn("Unexpected exception while running NioTask.channelReady(...)", e);
                    }
                }

                @Override
                protected void deregister(SelectableChannel channel) {
                    try {
                        task.channelUnregistered(channel, null);
                    }
                    catch (Exception e) {
                        logger.warn("Unexpected exception while running NioTask.channelUnregistered(...)", e);
                    }
                }
            }).get();
            registration.submit(NioIoOps.valueOf(interestOps));
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public int getIoRatio() {
        return 0;
    }

    @Deprecated
    public void setIoRatio(int ioRatio) {
        logger.debug("NioEventLoop.setIoRatio(int) logic was removed, this is a no-op");
    }

    public void rebuildSelector() {
        if (!this.inEventLoop()) {
            this.execute(new Runnable(){

                @Override
                public void run() {
                    ((NioIoHandler)NioEventLoop.this.ioHandler()).rebuildSelector0();
                }
            });
            return;
        }
        ((NioIoHandler)this.ioHandler()).rebuildSelector0();
    }

    @Override
    public int registeredChannels() {
        return ((NioIoHandler)this.ioHandler()).numRegistered();
    }

    @Override
    public Iterator<Channel> registeredChannelsIterator() {
        assert (this.inEventLoop());
        final Set<SelectionKey> keys = ((NioIoHandler)this.ioHandler()).registeredSet();
        if (keys.isEmpty()) {
            return SingleThreadEventLoop.ChannelsReadOnlyIterator.empty();
        }
        return new Iterator<Channel>(){
            final Iterator<SelectionKey> selectionKeyIterator;
            Channel next;
            boolean isDone;
            {
                this.selectionKeyIterator = ObjectUtil.checkNotNull(keys, "selectionKeys").iterator();
            }

            @Override
            public boolean hasNext() {
                if (this.isDone) {
                    return false;
                }
                Channel cur = this.next;
                if (cur == null) {
                    this.next = this.nextOrDone();
                    cur = this.next;
                    return cur != null;
                }
                return true;
            }

            @Override
            public Channel next() {
                if (this.isDone) {
                    throw new NoSuchElementException();
                }
                Channel cur = this.next;
                if (cur == null && (cur = this.nextOrDone()) == null) {
                    throw new NoSuchElementException();
                }
                this.next = this.nextOrDone();
                return cur;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }

            private Channel nextOrDone() {
                Iterator<SelectionKey> it = this.selectionKeyIterator;
                while (it.hasNext()) {
                    NioIoHandle handle;
                    Object attachment;
                    SelectionKey key = it.next();
                    if (!key.isValid() || !((attachment = key.attachment()) instanceof NioIoHandler.DefaultNioRegistration) || !((handle = ((NioIoHandler.DefaultNioRegistration)attachment).handle()) instanceof AbstractNioChannel.AbstractNioUnsafe)) continue;
                    return ((AbstractNioChannel.AbstractNioUnsafe)handle).channel();
                }
                this.isDone = true;
                return null;
            }
        };
    }

    Selector unwrappedSelector() {
        return ((NioIoHandler)this.ioHandler()).unwrappedSelector();
    }
}

