/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.ISpan;
import io.sentry.ITransaction;
import io.sentry.PerformanceCollectionData;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CompositePerformanceCollector {
    public void start(@NotNull ITransaction var1);

    public void start(@NotNull String var1);

    public void onSpanStarted(@NotNull ISpan var1);

    public void onSpanFinished(@NotNull ISpan var1);

    @Nullable
    public List<PerformanceCollectionData> stop(@NotNull ITransaction var1);

    @Nullable
    public List<PerformanceCollectionData> stop(@NotNull String var1);

    @ApiStatus.Internal
    public void close();
}

