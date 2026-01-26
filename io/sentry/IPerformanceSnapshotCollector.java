/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import io.sentry.IPerformanceCollector;
import io.sentry.PerformanceCollectionData;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public interface IPerformanceSnapshotCollector
extends IPerformanceCollector {
    public void setup();

    public void collect(@NotNull PerformanceCollectionData var1);
}

