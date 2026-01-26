/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import io.sentry.ProfileLifecycle;
import io.sentry.TracesSampler;
import io.sentry.protocol.SentryId;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public interface IContinuousProfiler {
    public boolean isRunning();

    public void startProfiler(@NotNull ProfileLifecycle var1, @NotNull TracesSampler var2);

    public void stopProfiler(@NotNull ProfileLifecycle var1);

    public void close(boolean var1);

    public void reevaluateSampling();

    @NotNull
    public SentryId getProfilerId();

    @NotNull
    public SentryId getChunkId();
}

