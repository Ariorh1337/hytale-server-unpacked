/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.DefaultMaxMessagesRecvByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.util.internal.AdaptiveCalculator;
import io.netty.util.internal.ObjectUtil;

public class AdaptiveRecvByteBufAllocator
extends DefaultMaxMessagesRecvByteBufAllocator {
    public static final int DEFAULT_MINIMUM = 64;
    public static final int DEFAULT_INITIAL = 2048;
    public static final int DEFAULT_MAXIMUM = 65536;
    @Deprecated
    public static final AdaptiveRecvByteBufAllocator DEFAULT = new AdaptiveRecvByteBufAllocator();
    private final int minimum;
    private final int initial;
    private final int maximum;

    public AdaptiveRecvByteBufAllocator() {
        this(64, 2048, 65536);
    }

    public AdaptiveRecvByteBufAllocator(int minimum, int initial, int maximum) {
        ObjectUtil.checkPositive(minimum, "minimum");
        if (initial < minimum) {
            throw new IllegalArgumentException("initial: " + initial);
        }
        if (maximum < initial) {
            throw new IllegalArgumentException("maximum: " + maximum);
        }
        this.minimum = minimum;
        this.initial = initial;
        this.maximum = maximum;
    }

    @Override
    public RecvByteBufAllocator.Handle newHandle() {
        return new HandleImpl(this.minimum, this.initial, this.maximum);
    }

    @Override
    public AdaptiveRecvByteBufAllocator respectMaybeMoreData(boolean respectMaybeMoreData) {
        super.respectMaybeMoreData(respectMaybeMoreData);
        return this;
    }

    private final class HandleImpl
    extends DefaultMaxMessagesRecvByteBufAllocator.MaxMessageHandle {
        private final AdaptiveCalculator calculator;

        HandleImpl(int minimum, int initial, int maximum) {
            this.calculator = new AdaptiveCalculator(minimum, initial, maximum);
        }

        @Override
        public void lastBytesRead(int bytes) {
            if (bytes == this.attemptedBytesRead()) {
                this.calculator.record(bytes);
            }
            super.lastBytesRead(bytes);
        }

        @Override
        public int guess() {
            return this.calculator.nextSize();
        }

        @Override
        public void readComplete() {
            this.calculator.record(this.totalBytesRead());
        }
    }
}

