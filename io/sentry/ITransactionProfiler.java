/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.ITransaction;
import io.sentry.PerformanceCollectionData;
import io.sentry.ProfilingTraceData;
import io.sentry.SentryOptions;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public interface ITransactionProfiler {
    public boolean isRunning();

    public void start();

    public void bindTransaction(@NotNull ITransaction var1);

    @Nullable
    public ProfilingTraceData onTransactionFinish(@NotNull ITransaction var1, @Nullable List<PerformanceCollectionData> var2, @NotNull SentryOptions var3);

    public void close();
}

