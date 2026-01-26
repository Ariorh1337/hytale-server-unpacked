/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.DefaultMockTicker;
import io.netty.util.concurrent.MockTicker;
import io.netty.util.concurrent.SystemTicker;
import java.util.concurrent.TimeUnit;

public interface Ticker {
    public static Ticker systemTicker() {
        return SystemTicker.INSTANCE;
    }

    public static MockTicker newMockTicker() {
        return new DefaultMockTicker();
    }

    public long initialNanoTime();

    public long nanoTime();

    public void sleep(long var1, TimeUnit var3) throws InterruptedException;

    default public void sleepMillis(long delayMillis) throws InterruptedException {
        this.sleep(delayMillis, TimeUnit.MILLISECONDS);
    }
}

