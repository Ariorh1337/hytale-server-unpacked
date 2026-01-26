/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.metrics;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.metrics.MetricProvider;
import com.hypixel.hytale.metrics.MetricsRegistry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import javax.annotation.Nonnull;
import org.bson.BsonValue;

public class ExecutorMetricsRegistry<T extends ExecutorMetric>
extends MetricsRegistry<T> {
    @Override
    public BsonValue encode(@Nonnull T t, ExtraInfo extraInfo) {
        if (t.isInThread()) {
            return super.encode(t, extraInfo);
        }
        return CompletableFuture.supplyAsync(() -> super.encode(t, extraInfo), t).join();
    }

    @Override
    public <R extends MetricProvider> ExecutorMetricsRegistry<T> register(String id, @Nonnull Function<T, R> func) {
        return (ExecutorMetricsRegistry)super.register(id, func);
    }

    @Override
    public <R> ExecutorMetricsRegistry<T> register(String id, Function<T, R> func, Codec<R> codec) {
        return (ExecutorMetricsRegistry)super.register(id, func, codec);
    }

    @Override
    public ExecutorMetricsRegistry<T> register(String id, MetricsRegistry<Void> metricsRegistry) {
        return (ExecutorMetricsRegistry)super.register(id, metricsRegistry);
    }

    @Override
    public <R> ExecutorMetricsRegistry<T> register(String id, Function<T, R> func, Function<R, MetricsRegistry<R>> codecFunc) {
        return (ExecutorMetricsRegistry)super.register(id, func, codecFunc);
    }

    public static interface ExecutorMetric
    extends Executor {
        public boolean isInThread();
    }
}

