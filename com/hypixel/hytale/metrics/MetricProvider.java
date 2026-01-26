/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.metrics;

import com.hypixel.hytale.metrics.MetricResults;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface MetricProvider {
    @Nullable
    public MetricResults toMetricResults();

    @Nonnull
    public static <T, R> Function<T, MetricProvider> maybe(@Nonnull Function<T, R> func) {
        return t -> {
            Object r = func.apply(t);
            return r instanceof MetricProvider ? (MetricProvider)r : null;
        };
    }
}

