/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.FileRegion;
import io.netty.channel.MessageSizeEstimator;
import io.netty.util.internal.ObjectUtil;

public final class DefaultMessageSizeEstimator
implements MessageSizeEstimator {
    public static final MessageSizeEstimator DEFAULT = new DefaultMessageSizeEstimator(8);
    private final MessageSizeEstimator.Handle handle;

    public DefaultMessageSizeEstimator(int unknownSize) {
        ObjectUtil.checkPositiveOrZero(unknownSize, "unknownSize");
        this.handle = new HandleImpl(unknownSize);
    }

    @Override
    public MessageSizeEstimator.Handle newHandle() {
        return this.handle;
    }

    private static final class HandleImpl
    implements MessageSizeEstimator.Handle {
        private final int unknownSize;

        private HandleImpl(int unknownSize) {
            this.unknownSize = unknownSize;
        }

        @Override
        public int size(Object msg) {
            if (msg instanceof ByteBuf) {
                return ((ByteBuf)msg).readableBytes();
            }
            if (msg instanceof ByteBufHolder) {
                return ((ByteBufHolder)msg).content().readableBytes();
            }
            if (msg instanceof FileRegion) {
                return 0;
            }
            return this.unknownSize;
        }
    }
}

