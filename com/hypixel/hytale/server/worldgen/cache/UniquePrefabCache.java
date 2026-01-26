/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.cache;

import com.hypixel.hytale.server.worldgen.container.UniquePrefabContainer;
import com.hypixel.hytale.server.worldgen.util.cache.SizedTimeoutCache;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UniquePrefabCache {
    @Nonnull
    protected final SizedTimeoutCache<Integer, UniquePrefabContainer.UniquePrefabEntry[]> cache;

    public UniquePrefabCache(@Nonnull UniquePrefabFunction function, int maxSize, long expireAfterSeconds) {
        this.cache = new SizedTimeoutCache<Integer, UniquePrefabContainer.UniquePrefabEntry[]>(expireAfterSeconds, TimeUnit.SECONDS, maxSize, function::get, null);
    }

    @Nullable
    public UniquePrefabContainer.UniquePrefabEntry[] get(int seed) {
        try {
            return this.cache.get(seed);
        }
        catch (Exception e) {
            throw new Error("Failed to receive UniquePrefabEntry for " + seed, e);
        }
    }

    @FunctionalInterface
    public static interface UniquePrefabFunction {
        public UniquePrefabContainer.UniquePrefabEntry[] get(int var1);
    }
}

