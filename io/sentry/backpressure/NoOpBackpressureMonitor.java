/*
 * Decompiled with CFR 0.152.
 */
package io.sentry.backpressure;

import io.sentry.backpressure.IBackpressureMonitor;

public final class NoOpBackpressureMonitor
implements IBackpressureMonitor {
    private static final NoOpBackpressureMonitor instance = new NoOpBackpressureMonitor();

    private NoOpBackpressureMonitor() {
    }

    public static NoOpBackpressureMonitor getInstance() {
        return instance;
    }

    @Override
    public void start() {
    }

    @Override
    public int getDownsampleFactor() {
        return 0;
    }

    @Override
    public void close() {
    }
}

