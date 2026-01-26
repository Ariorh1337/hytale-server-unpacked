/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.Ticker;
import java.util.concurrent.TimeUnit;

public interface MockTicker
extends Ticker {
    @Override
    default public long initialNanoTime() {
        return 0L;
    }

    public void advance(long var1, TimeUnit var3);

    default public void advanceMillis(long amountMillis) {
        this.advance(amountMillis, TimeUnit.MILLISECONDS);
    }
}

