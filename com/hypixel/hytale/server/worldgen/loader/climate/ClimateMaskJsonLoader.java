/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.climate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hypixel.hytale.procedurallib.json.CoordinateRandomizerJsonLoader;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.random.CoordinateRandomizer;
import com.hypixel.hytale.procedurallib.random.ICoordinateRandomizer;
import com.hypixel.hytale.server.worldgen.climate.ClimateGraph;
import com.hypixel.hytale.server.worldgen.climate.ClimateMaskProvider;
import com.hypixel.hytale.server.worldgen.climate.ClimateNoise;
import com.hypixel.hytale.server.worldgen.climate.UniqueClimateGenerator;
import com.hypixel.hytale.server.worldgen.loader.climate.ClimateGraphJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.climate.ClimateNoiseJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.climate.UniqueClimateGeneratorJsonLoader;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ClimateMaskJsonLoader<K extends SeedResource>
extends JsonLoader<K, ClimateMaskProvider> {
    public ClimateMaskJsonLoader(SeedString<K> seed, Path dataFolder, Path maskFile) {
        super(seed, dataFolder, ClimateMaskJsonLoader.loadMaskFileJson(maskFile));
    }

    @Override
    @Nullable
    public ClimateMaskProvider load() {
        return new ClimateMaskProvider(this.loadRandomizer(), this.loadClimateNoise(), this.loadClimateGraph(), this.loadUniqueClimateGenerator());
    }

    @Nonnull
    protected ICoordinateRandomizer loadRandomizer() {
        if (this.has("Randomizer")) {
            return new CoordinateRandomizerJsonLoader(this.seed, this.dataFolder, this.get("Randomizer")).load();
        }
        return CoordinateRandomizer.EMPTY_RANDOMIZER;
    }

    @Nonnull
    protected ClimateNoise loadClimateNoise() {
        return new ClimateNoiseJsonLoader(this.seed, this.dataFolder, this.mustGetObject("Noise", null)).load();
    }

    @Nonnull
    protected ClimateGraph loadClimateGraph() {
        return new ClimateGraphJsonLoader(this.seed, this.dataFolder, this.mustGetObject("Climate", null)).load();
    }

    @Nonnull
    protected UniqueClimateGenerator loadUniqueClimateGenerator() {
        return new UniqueClimateGeneratorJsonLoader(this.seed, this.dataFolder, this.mustGetArray("UniqueZones", Constants.DEFAULT_UNIQUE)).load();
    }

    protected static JsonElement loadMaskFileJson(Path file) {
        JsonElement jsonElement;
        block8: {
            BufferedReader reader = Files.newBufferedReader(file);
            try {
                jsonElement = JsonParser.parseReader(reader);
                if (reader == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (reader != null) {
                        try {
                            reader.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    throw new Error("Failed to load Mask.json", e);
                }
            }
            reader.close();
        }
        return jsonElement;
    }

    public static interface Constants {
        public static final String KEY_RANDOMIZER = "Randomizer";
        public static final String KEY_UNIQUE_ZONES = "UniqueZones";
        public static final JsonArray DEFAULT_UNIQUE = new JsonArray();
    }
}

