/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import io.sentry.IPerformanceSnapshotCollector;
import io.sentry.PerformanceCollectionData;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class JavaMemoryCollector
implements IPerformanceSnapshotCollector {
    @NotNull
    private final Runtime runtime = Runtime.getRuntime();

    @Override
    public void setup() {
    }

    @Override
    public void collect(@NotNull PerformanceCollectionData performanceCollectionData) {
        long usedMemory = this.runtime.totalMemory() - this.runtime.freeMemory();
        performanceCollectionData.setUsedHeapMemory(usedMemory);
    }
}

