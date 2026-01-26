/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.ITransaction;
import io.sentry.ITransactionProfiler;
import io.sentry.PerformanceCollectionData;
import io.sentry.ProfilingTraceData;
import io.sentry.SentryOptions;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NoOpTransactionProfiler
implements ITransactionProfiler {
    private static final NoOpTransactionProfiler instance = new NoOpTransactionProfiler();

    private NoOpTransactionProfiler() {
    }

    public static NoOpTransactionProfiler getInstance() {
        return instance;
    }

    @Override
    public void start() {
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void bindTransaction(@NotNull ITransaction transaction) {
    }

    @Override
    @Nullable
    public ProfilingTraceData onTransactionFinish(@NotNull ITransaction transaction, @Nullable List<PerformanceCollectionData> performanceCollectionData, @NotNull SentryOptions options) {
        return null;
    }

    @Override
    public void close() {
    }
}

