/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import io.sentry.IContinuousProfiler;
import io.sentry.ProfileLifecycle;
import io.sentry.TracesSampler;
import io.sentry.protocol.SentryId;
import org.jetbrains.annotations.NotNull;

public final class NoOpContinuousProfiler
implements IContinuousProfiler {
    private static final NoOpContinuousProfiler instance = new NoOpContinuousProfiler();

    private NoOpContinuousProfiler() {
    }

    public static NoOpContinuousProfiler getInstance() {
        return instance;
    }

    @Override
    public void stopProfiler(@NotNull ProfileLifecycle profileLifecycle) {
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void startProfiler(@NotNull ProfileLifecycle profileLifecycle, @NotNull TracesSampler tracesSampler) {
    }

    @Override
    public void close(boolean isTerminating) {
    }

    @Override
    public void reevaluateSampling() {
    }

    @Override
    @NotNull
    public SentryId getProfilerId() {
        return SentryId.EMPTY_ID;
    }

    @Override
    @NotNull
    public SentryId getChunkId() {
        return SentryId.EMPTY_ID;
    }
}

