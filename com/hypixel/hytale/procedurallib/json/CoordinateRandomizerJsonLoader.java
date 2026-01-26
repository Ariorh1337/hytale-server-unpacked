/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hypixel.hytale.procedurallib.json.CoordinateRotatorJsonLoader;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.NoisePropertyJsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.property.NoiseProperty;
import com.hypixel.hytale.procedurallib.random.CoordinateRandomizer;
import com.hypixel.hytale.procedurallib.random.CoordinateRotator;
import com.hypixel.hytale.procedurallib.random.ICoordinateRandomizer;
import com.hypixel.hytale.procedurallib.random.RotatedCoordinateRandomizer;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public class CoordinateRandomizerJsonLoader<K extends SeedResource>
extends JsonLoader<K, ICoordinateRandomizer> {
    public CoordinateRandomizerJsonLoader(SeedString<K> seed, Path dataFolder, JsonElement json) {
        super(seed, dataFolder, json);
    }

    @Override
    @Nonnull
    public ICoordinateRandomizer load() {
        if (this.json == null || this.json.isJsonNull()) {
            return CoordinateRandomizer.EMPTY_RANDOMIZER;
        }
        return this.loadRandomizer();
    }

    @Nonnull
    protected ICoordinateRandomizer loadRandomizer() {
        CoordinateRotator rotation;
        ICoordinateRandomizer randomizer = new CoordinateRandomizer(this.loadGenerators(".X-Noise#%s"), this.loadGenerators(".Y-Noise#%s"), this.loadGenerators(".Z-Noise#%s"));
        if (this.has("Rotate") && (rotation = new CoordinateRotatorJsonLoader(this.seed, this.dataFolder, this.get("Rotate")).load()) != CoordinateRotator.NONE) {
            randomizer = new RotatedCoordinateRandomizer(randomizer, rotation);
        }
        return randomizer;
    }

    @Nonnull
    protected CoordinateRandomizer.AmplitudeNoiseProperty[] loadGenerators(@Nonnull String seedSuffix) {
        JsonArray array = this.get("Generators").getAsJsonArray();
        CoordinateRandomizer.AmplitudeNoiseProperty[] generators = new CoordinateRandomizer.AmplitudeNoiseProperty[array.size()];
        for (int i = 0; i < array.size(); ++i) {
            JsonObject object = array.get(i).getAsJsonObject();
            NoiseProperty property = new NoisePropertyJsonLoader(this.seed.alternateOriginal(String.format(seedSuffix, i)), this.dataFolder, object).load();
            double amplitude = object.get("Amplitude").getAsDouble();
            generators[i] = new CoordinateRandomizer.AmplitudeNoiseProperty(property, amplitude);
        }
        return generators;
    }

    public static interface Constants {
        public static final String KEY_GENERATORS = "Generators";
        public static final String KEY_GENERATORS_AMPLITUDE = "Amplitude";
        public static final String SEED_X_NOISE_SUFFIX = ".X-Noise#%s";
        public static final String SEED_Y_NOISE_SUFFIX = ".Y-Noise#%s";
        public static final String SEED_Z_NOISE_SUFFIX = ".Z-Noise#%s";
    }
}

