/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package io.sentry.backpressure;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface IBackpressureMonitor {
    public void start();

    public int getDownsampleFactor();

    public void close();
}

