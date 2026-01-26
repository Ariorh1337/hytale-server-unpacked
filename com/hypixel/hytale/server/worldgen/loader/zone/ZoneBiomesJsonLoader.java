/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.zone;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.hypixel.hytale.common.map.IWeightedMap;
import com.hypixel.hytale.common.map.WeightedMap;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.biome.TileBiome;
import com.hypixel.hytale.server.worldgen.loader.biome.TileBiomeJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.context.BiomeFileContext;
import com.hypixel.hytale.server.worldgen.loader.context.ZoneFileContext;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import javax.annotation.Nonnull;

public class ZoneBiomesJsonLoader
extends JsonLoader<SeedStringResource, IWeightedMap<TileBiome>> {
    protected final ZoneFileContext zoneContext;

    public ZoneBiomesJsonLoader(SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json, ZoneFileContext zone) {
        super(seed, dataFolder, json);
        this.zoneContext = zone;
    }

    @Override
    public IWeightedMap<TileBiome> load() {
        WeightedMap.Builder<TileBiome> builder = WeightedMap.builder(TileBiome.EMPTY_ARRAY);
        for (Map.Entry<String, BiomeFileContext> entry : this.zoneContext.getTileBiomes()) {
            TileBiome biome = this.loadBiome(entry.getValue());
            builder.put(biome, biome.getWeight());
        }
        if (builder.size() <= 0) {
            throw new IllegalArgumentException("Could not find any tile biomes for this zone!");
        }
        return builder.build();
    }

    @Nonnull
    protected TileBiome loadBiome(@Nonnull BiomeFileContext biomeContext) {
        TileBiome tileBiome;
        JsonReader reader = new JsonReader(Files.newBufferedReader(biomeContext.getPath()));
        try {
            JsonElement biomeJson = JsonParser.parseReader(reader);
            tileBiome = new TileBiomeJsonLoader(this.seed, this.dataFolder, biomeJson, biomeContext).load();
        }
        catch (Throwable throwable) {
            try {
                try {
                    reader.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (Throwable e) {
                throw new Error(String.format("Error while loading tile biome \"%s\" from \"%s\"", biomeContext.getName(), biomeContext.getPath().toString()), e);
            }
        }
        reader.close();
        return tileBiome;
    }

    public static interface Constants {
        public static final String ERROR_BIOME_FILES_NULL = "Biome files error occured.";
        public static final String ERROR_BIOME_FAILED = "Error while loading tile biome \"%s\" from \"%s\"";
        public static final String ERROR_NO_TILE_BIOMES = "Could not find any tile biomes for this zone!";
        public static final String FILE_TILE_PREFIX = "Tile.";
        public static final String FILE_TILE_SUFFIX = ".json";
    }
}

