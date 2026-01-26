/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.zone;

import com.hypixel.hytale.server.worldgen.zone.ZonePatternGenerator;
import com.hypixel.hytale.server.worldgen.zone.ZonePatternProvider;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ZonePatternGeneratorCache {
    protected final Function<Integer, ZonePatternGenerator> compute;
    protected final Map<Integer, ZonePatternGenerator> cache = new ConcurrentHashMap<Integer, ZonePatternGenerator>();

    public ZonePatternGeneratorCache(ZonePatternProvider provider) {
        this.compute = provider::createGenerator;
    }

    public ZonePatternGenerator get(int seed) {
        try {
            return this.cache.computeIfAbsent(seed, this.compute);
        }
        catch (Exception e) {
            throw new Error("Failed to receive UniquePrefabEntry for " + seed, e);
        }
    }
}

