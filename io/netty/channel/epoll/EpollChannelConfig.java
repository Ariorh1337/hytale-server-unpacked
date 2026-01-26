/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollMode;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.unix.IntegerUnixChannelOption;
import io.netty.channel.unix.Limits;
import io.netty.channel.unix.RawUnixChannelOption;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

public class EpollChannelConfig
extends DefaultChannelConfig {
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(EpollChannelConfig.class);
    private volatile long maxBytesPerGatheringWrite = Limits.SSIZE_MAX;

    protected EpollChannelConfig(Channel channel) {
        super(EpollChannelConfig.checkAbstractEpollChannel(channel));
    }

    protected EpollChannelConfig(Channel channel, RecvByteBufAllocator recvByteBufAllocator) {
        super(EpollChannelConfig.checkAbstractEpollChannel(channel), recvByteBufAllocator);
    }

    protected LinuxSocket socket() {
        return ((AbstractEpollChannel)this.channel).socket;
    }

    private static Channel checkAbstractEpollChannel(Channel channel) {
        if (!(channel instanceof AbstractEpollChannel)) {
            throw new IllegalArgumentException("channel is not AbstractEpollChannel: " + channel.getClass());
        }
        return channel;
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), EpollChannelOption.EPOLL_MODE);
    }

    @Override
    public <T> T getOption(ChannelOption<T> option) {
        if (option == EpollChannelOption.EPOLL_MODE) {
            return (T)((Object)this.getEpollMode());
        }
        try {
            if (option instanceof IntegerUnixChannelOption) {
                IntegerUnixChannelOption opt = (IntegerUnixChannelOption)option;
                return (T)Integer.valueOf(((AbstractEpollChannel)this.channel).socket.getIntOpt(opt.level(), opt.optname()));
            }
            if (option instanceof RawUnixChannelOption) {
                RawUnixChannelOption opt = (RawUnixChannelOption)option;
                ByteBuffer out = ByteBuffer.allocate(opt.length());
                ((AbstractEpollChannel)this.channel).socket.getRawOpt(opt.level(), opt.optname(), out);
                return (T)out.flip();
            }
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
        return super.getOption(option);
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> option, T value) {
        this.validate(option, value);
        if (option != EpollChannelOption.EPOLL_MODE) {
            try {
                if (option instanceof IntegerUnixChannelOption) {
                    IntegerUnixChannelOption opt = (IntegerUnixChannelOption)option;
                    ((AbstractEpollChannel)this.channel).socket.setIntOpt(opt.level(), opt.optname(), (Integer)value);
                    return true;
                }
                if (option instanceof RawUnixChannelOption) {
                    RawUnixChannelOption opt = (RawUnixChannelOption)option;
                    ((AbstractEpollChannel)this.channel).socket.setRawOpt(opt.level(), opt.optname(), (ByteBuffer)value);
                    return true;
                }
            }
            catch (IOException e) {
                throw new ChannelException(e);
            }
            return super.setOption(option, value);
        }
        this.setEpollMode((EpollMode)((Object)value));
        return true;
    }

    @Override
    public EpollChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        super.setConnectTimeoutMillis(connectTimeoutMillis);
        return this;
    }

    @Override
    @Deprecated
    public EpollChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        super.setMaxMessagesPerRead(maxMessagesPerRead);
        return this;
    }

    @Override
    public EpollChannelConfig setWriteSpinCount(int writeSpinCount) {
        super.setWriteSpinCount(writeSpinCount);
        return this;
    }

    @Override
    public EpollChannelConfig setAllocator(ByteBufAllocator allocator) {
        super.setAllocator(allocator);
        return this;
    }

    @Override
    public EpollChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        if (!(allocator.newHandle() instanceof RecvByteBufAllocator.ExtendedHandle)) {
            throw new IllegalArgumentException("allocator.newHandle() must return an object of type: " + RecvByteBufAllocator.ExtendedHandle.class);
        }
        super.setRecvByteBufAllocator(allocator);
        return this;
    }

    @Override
    public EpollChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }

    @Override
    @Deprecated
    public EpollChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
        return this;
    }

    @Override
    @Deprecated
    public EpollChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
        return this;
    }

    @Override
    public EpollChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark(writeBufferWaterMark);
        return this;
    }

    @Override
    public EpollChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator(estimator);
        return this;
    }

    @Deprecated
    public EpollMode getEpollMode() {
        return EpollMode.LEVEL_TRIGGERED;
    }

    @Deprecated
    public EpollChannelConfig setEpollMode(EpollMode mode) {
        LOGGER.debug("Changing the EpollMode is not supported anymore, this is just a no-op");
        return this;
    }

    private void checkChannelNotRegistered() {
        if (this.channel.isRegistered()) {
            throw new IllegalStateException("EpollMode can only be changed before channel is registered");
        }
    }

    @Override
    protected final void autoReadCleared() {
        ((AbstractEpollChannel)this.channel).clearEpollIn();
    }

    protected final void setMaxBytesPerGatheringWrite(long maxBytesPerGatheringWrite) {
        this.maxBytesPerGatheringWrite = maxBytesPerGatheringWrite;
    }

    protected final long getMaxBytesPerGatheringWrite() {
        return this.maxBytesPerGatheringWrite;
    }
}

