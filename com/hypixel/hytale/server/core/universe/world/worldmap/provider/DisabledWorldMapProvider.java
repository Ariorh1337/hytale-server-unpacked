/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.worldmap.provider;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.map.WorldMap;
import com.hypixel.hytale.server.core.universe.world.worldmap.IWorldMap;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapLoadException;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapSettings;
import com.hypixel.hytale.server.core.universe.world.worldmap.provider.IWorldMapProvider;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public class DisabledWorldMapProvider
implements IWorldMapProvider {
    public static final String ID = "Disabled";
    public static final BuilderCodec<DisabledWorldMapProvider> CODEC = BuilderCodec.builder(DisabledWorldMapProvider.class, DisabledWorldMapProvider::new).build();

    @Override
    @Nonnull
    public IWorldMap getGenerator(World world) throws WorldMapLoadException {
        return DisabledWorldMap.INSTANCE;
    }

    @Nonnull
    public String toString() {
        return "DisabledWorldMapProvider{}";
    }

    static class DisabledWorldMap
    implements IWorldMap {
        public static final IWorldMap INSTANCE = new DisabledWorldMap();

        DisabledWorldMap() {
        }

        @Override
        @Nonnull
        public WorldMapSettings getWorldMapSettings() {
            return WorldMapSettings.DISABLED;
        }

        @Override
        @Nonnull
        public CompletableFuture<WorldMap> generate(World world, int imageWidth, int imageHeight, LongSet chunksToGenerate) {
            return CompletableFuture.completedFuture(new WorldMap(0));
        }

        @Override
        @Nonnull
        public CompletableFuture<Map<String, MapMarker>> generatePointsOfInterest(World world) {
            return CompletableFuture.completedFuture(Collections.emptyMap());
        }
    }
}

