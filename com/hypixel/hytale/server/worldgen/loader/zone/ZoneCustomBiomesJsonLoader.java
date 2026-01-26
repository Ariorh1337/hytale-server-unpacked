/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.zone;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.biome.Biome;
import com.hypixel.hytale.server.worldgen.biome.CustomBiome;
import com.hypixel.hytale.server.worldgen.biome.CustomBiomeGenerator;
import com.hypixel.hytale.server.worldgen.loader.biome.CustomBiomeJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.context.BiomeFileContext;
import com.hypixel.hytale.server.worldgen.loader.context.ZoneFileContext;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import javax.annotation.Nonnull;

public class ZoneCustomBiomesJsonLoader
extends JsonLoader<SeedStringResource, CustomBiome[]> {
    private static final Comparator<CustomBiome> PRIORITY_SORTER = (o1, o2) -> Integer.compare(o2.getCustomBiomeGenerator().getPriority(), o1.getCustomBiomeGenerator().getPriority());
    protected final ZoneFileContext zoneContext;
    protected final Biome[] tileBiomes;

    public ZoneCustomBiomesJsonLoader(SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json, ZoneFileContext zoneContext, Biome[] tileBiomes) {
        super(seed, dataFolder, json);
        this.zoneContext = zoneContext;
        this.tileBiomes = tileBiomes;
    }

    @Override
    @Nonnull
    public CustomBiome[] load() {
        int index = 0;
        CustomBiome[] biomes = new CustomBiome[this.zoneContext.getCustomBiomes().size()];
        for (Map.Entry<String, BiomeFileContext> entry : this.zoneContext.getCustomBiomes()) {
            BiomeFileContext biomeContext = entry.getValue();
            try (JsonReader reader = new JsonReader(Files.newBufferedReader(biomeContext.getPath()));){
                JsonElement biomeJson = JsonParser.parseReader(reader);
                CustomBiome biome = new CustomBiomeJsonLoader(this.seed, this.dataFolder, biomeJson, biomeContext, this.tileBiomes).load();
                CustomBiomeGenerator reference = biome.getCustomBiomeGenerator();
                if (reference == null) {
                    throw new NullPointerException(biomeContext.getPath().toAbsolutePath().toString());
                }
                biomes[index++] = biome;
            }
            catch (Throwable e) {
                throw new Error(String.format("Error while loading custom biome \"%s\" from \"%s\"", biomeContext.getName(), biomeContext.getPath().toString()), e);
            }
        }
        Arrays.sort(biomes, PRIORITY_SORTER);
        return biomes;
    }

    public static interface Constants {
        public static final String ERROR_BIOME_FILES_NULL = "Biome files error occured.";
        public static final String ERROR_BIOME_FAILED = "Error while loading custom biome \"%s\" from \"%s\"";
        public static final String ERROR_NO_CUSTOM_GENERATOR = "Could not find custom biome generator for custom biome \"%s\" at \"%s\"";
        public static final String FILE_CUSTOM_PREFIX = "Custom.";
        public static final String FILE_CUSTOM_SUFFIX = ".json";
    }
}

