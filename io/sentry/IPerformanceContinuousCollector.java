/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import io.sentry.IPerformanceCollector;
import io.sentry.ISpan;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public interface IPerformanceContinuousCollector
extends IPerformanceCollector {
    public void onSpanStarted(@NotNull ISpan var1);

    public void onSpanFinished(@NotNull ISpan var1);

    public void clear();
}

