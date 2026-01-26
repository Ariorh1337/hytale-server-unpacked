/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.worldgen.provider;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
import com.hypixel.hytale.server.core.asset.util.ColorParseUtil;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.universe.world.worldgen.GeneratedBlockChunk;
import com.hypixel.hytale.server.core.universe.world.worldgen.GeneratedBlockStateChunk;
import com.hypixel.hytale.server.core.universe.world.worldgen.GeneratedChunk;
import com.hypixel.hytale.server.core.universe.world.worldgen.GeneratedEntityChunk;
import com.hypixel.hytale.server.core.universe.world.worldgen.IWorldGen;
import com.hypixel.hytale.server.core.universe.world.worldgen.WorldGenLoadException;
import com.hypixel.hytale.server.core.universe.world.worldgen.WorldGenTimingsCollector;
import com.hypixel.hytale.server.core.universe.world.worldgen.provider.IWorldGenProvider;
import java.util.concurrent.CompletableFuture;
import java.util.function.LongPredicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VoidWorldGenProvider
implements IWorldGenProvider {
    public static final String ID = "Void";
    public static final BuilderCodec<VoidWorldGenProvider> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(VoidWorldGenProvider.class, VoidWorldGenProvider::new).documentation("A world generation provider that does not generate any blocks.")).append(new KeyedCodec<Color>("Tint", ProtocolCodecs.COLOR), (config, o) -> {
        config.tint = o;
    }, config -> config.tint).documentation("The tint to set for all chunks that are generated.").add()).append(new KeyedCodec<String>("Environment", Codec.STRING), (config, s) -> {
        config.environment = s;
    }, config -> config.environment).documentation("The environment to set for every column in generated chunks.").addValidator(Environment.VALIDATOR_CACHE.getValidator()).add()).build();
    private Color tint;
    private String environment;

    public VoidWorldGenProvider() {
    }

    public VoidWorldGenProvider(Color tint, String environment) {
        this.tint = tint;
        this.environment = environment;
    }

    @Override
    @Nonnull
    public IWorldGen getGenerator() throws WorldGenLoadException {
        int tintId = this.tint == null ? 0 : ColorParseUtil.colorToARGBInt(this.tint);
        String key = this.environment != null ? this.environment : "Default";
        int index = Environment.getAssetMap().getIndex(key);
        if (index == Integer.MIN_VALUE) {
            throw new WorldGenLoadException("Unknown key! " + key);
        }
        return new VoidWorldGen(tintId, index);
    }

    @Nonnull
    public String toString() {
        return "VoidWorldGenProvider{environment='" + this.environment + "'}";
    }

    public static class VoidWorldGen
    implements IWorldGen {
        private final int tintId;
        private final int environmentId;

        public VoidWorldGen() {
            this.tintId = 0;
            this.environmentId = 0;
        }

        public VoidWorldGen(@Nullable Color tint, @Nullable String environment) throws WorldGenLoadException {
            int tintId;
            this.tintId = tintId = tint == null ? 0 : ColorParseUtil.colorToARGBInt(tint);
            String key = environment != null ? environment : "Default";
            int index = Environment.getAssetMap().getIndex(key);
            if (index == Integer.MIN_VALUE) {
                throw new WorldGenLoadException("Unknown key! " + key);
            }
            this.environmentId = index;
        }

        public VoidWorldGen(int tintId, int environmentId) {
            this.tintId = tintId;
            this.environmentId = environmentId;
        }

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
            GeneratedBlockChunk generatedBlockChunk = new GeneratedBlockChunk(index, cx, cz);
            for (int x = 0; x < 32; ++x) {
                for (int z = 0; z < 32; ++z) {
                    if (this.environmentId != 0) {
                        generatedBlockChunk.setEnvironmentColumn(x, z, this.environmentId);
                    }
                    generatedBlockChunk.setTint(x, z, this.tintId);
                }
            }
            return CompletableFuture.completedFuture(new GeneratedChunk(generatedBlockChunk, new GeneratedBlockStateChunk(), new GeneratedEntityChunk(), GeneratedChunk.makeSections()));
        }
    }
}

