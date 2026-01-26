/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.DefaultMaxMessagesRecvByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.util.internal.ObjectUtil;

public class FixedRecvByteBufAllocator
extends DefaultMaxMessagesRecvByteBufAllocator {
    private final int bufferSize;

    public FixedRecvByteBufAllocator(int bufferSize) {
        ObjectUtil.checkPositive(bufferSize, "bufferSize");
        this.bufferSize = bufferSize;
    }

    @Override
    public RecvByteBufAllocator.Handle newHandle() {
        return new HandleImpl(this.bufferSize);
    }

    @Override
    public FixedRecvByteBufAllocator respectMaybeMoreData(boolean respectMaybeMoreData) {
        super.respectMaybeMoreData(respectMaybeMoreData);
        return this;
    }

    private final class HandleImpl
    extends DefaultMaxMessagesRecvByteBufAllocator.MaxMessageHandle {
        private final int bufferSize;

        HandleImpl(int bufferSize) {
            this.bufferSize = bufferSize;
        }

        @Override
        public int guess() {
            return this.bufferSize;
        }
    }
}

