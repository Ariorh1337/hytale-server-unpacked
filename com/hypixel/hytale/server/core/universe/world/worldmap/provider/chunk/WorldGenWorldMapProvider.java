/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.worldmap.provider.chunk;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.worldgen.IWorldGen;
import com.hypixel.hytale.server.core.universe.world.worldmap.IWorldMap;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapLoadException;
import com.hypixel.hytale.server.core.universe.world.worldmap.provider.IWorldMapProvider;
import com.hypixel.hytale.server.core.universe.world.worldmap.provider.chunk.ChunkWorldMap;
import javax.annotation.Nonnull;

public class WorldGenWorldMapProvider
implements IWorldMapProvider {
    public static final String ID = "WorldGen";
    public static final BuilderCodec<WorldGenWorldMapProvider> CODEC = BuilderCodec.builder(WorldGenWorldMapProvider.class, WorldGenWorldMapProvider::new).build();

    @Override
    public IWorldMap getGenerator(@Nonnull World world) throws WorldMapLoadException {
        IWorldGen generator = world.getChunkStore().getGenerator();
        if (generator instanceof IWorldMapProvider) {
            return ((IWorldMapProvider)((Object)generator)).getGenerator(world);
        }
        return ChunkWorldMap.INSTANCE;
    }

    @Nonnull
    public String toString() {
        return "DisabledWorldMapProvider{}";
    }
}

