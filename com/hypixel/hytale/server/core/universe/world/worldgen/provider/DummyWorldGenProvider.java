/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.worldgen.provider;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.universe.world.worldgen.GeneratedBlockChunk;
import com.hypixel.hytale.server.core.universe.world.worldgen.GeneratedBlockStateChunk;
import com.hypixel.hytale.server.core.universe.world.worldgen.GeneratedChunk;
import com.hypixel.hytale.server.core.universe.world.worldgen.GeneratedEntityChunk;
import com.hypixel.hytale.server.core.universe.world.worldgen.IWorldGen;
import com.hypixel.hytale.server.core.universe.world.worldgen.WorldGenTimingsCollector;
import com.hypixel.hytale.server.core.universe.world.worldgen.provider.IWorldGenProvider;
import java.util.concurrent.CompletableFuture;
import java.util.function.LongPredicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DummyWorldGenProvider
implements IWorldGenProvider {
    public static final String ID = "Dummy";
    public static final BuilderCodec<DummyWorldGenProvider> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(DummyWorldGenProvider.class, DummyWorldGenProvider::new).documentation("A dummy world generation provider that places a single layer of unknown blocks in each chunk.")).build();

    @Override
    @Nonnull
    public IWorldGen getGenerator() {
        return new DummyWorldGen();
    }

    @Nonnull
    public String toString() {
        return "DummyWorldGenProvider{}";
    }

    private static class DummyWorldGen
    implements IWorldGen {
        @Override
        @Nullable
        public WorldGenTimingsCollector getTimings() {
            return null;
        }

        @Override
        @Nonnull
        public Transform[] getSpawnPoints(int seed) {
            return new Transform[]{new Transform(0.0, 1.0, 0.0)};
        }

        @Override
        @Nonnull
        public CompletableFuture<GeneratedChunk> generate(int seed, long index, int cx, int cz, LongPredicate stillNeeded) {
            GeneratedBlockChunk chunk = new GeneratedBlockChunk(index, cx, cz);
            for (int x = 0; x < 32; ++x) {
                for (int z = 0; z < 32; ++z) {
                    chunk.setBlock(x, 0, z, 1, 0, 0);
                }
            }
            return CompletableFuture.completedFuture(new GeneratedChunk(chunk, new GeneratedBlockStateChunk(), new GeneratedEntityChunk(), GeneratedChunk.makeSections()));
        }
    }
}

