/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.cache;

import com.hypixel.hytale.server.worldgen.cache.ExtendedCoordinateCache;
import com.hypixel.hytale.server.worldgen.cave.Cave;
import com.hypixel.hytale.server.worldgen.cave.CaveType;
import com.hypixel.hytale.server.worldgen.chunk.ChunkGenerator;
import javax.annotation.Nonnull;

public class CaveGeneratorCache
extends ExtendedCoordinateCache<CaveType, Cave> {
    public CaveGeneratorCache(@Nonnull CaveFunction caveFunction, int maxSize, long expireAfterSeconds) {
        super(caveFunction::compute, null, maxSize, expireAfterSeconds);
    }

    @Override
    @Nonnull
    protected ExtendedCoordinateCache.ExtendedCoordinateKey<CaveType> localKey() {
        return ChunkGenerator.getResource().cacheCaveCoordinateKey;
    }

    @FunctionalInterface
    public static interface CaveFunction {
        public Cave compute(CaveType var1, int var2, int var3, int var4);
    }
}

