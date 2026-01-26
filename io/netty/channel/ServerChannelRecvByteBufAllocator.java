/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.DefaultMaxMessagesRecvByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;

public final class ServerChannelRecvByteBufAllocator
extends DefaultMaxMessagesRecvByteBufAllocator {
    public ServerChannelRecvByteBufAllocator() {
        super(1, true);
    }

    @Override
    public RecvByteBufAllocator.Handle newHandle() {
        return new DefaultMaxMessagesRecvByteBufAllocator.MaxMessageHandle(){

            @Override
            public int guess() {
                return 128;
            }
        };
    }
}

