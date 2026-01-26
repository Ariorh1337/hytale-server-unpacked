/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.component.system;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.metrics.MetricResults;

public interface MetricSystem<ECS_TYPE> {
    public MetricResults toMetricResults(Store<ECS_TYPE> var1);
}

