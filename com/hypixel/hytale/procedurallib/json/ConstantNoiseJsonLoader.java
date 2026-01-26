/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.json;

import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.NoiseFunction;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.logic.ConstantNoise;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public class ConstantNoiseJsonLoader<K extends SeedResource>
extends JsonLoader<K, NoiseFunction> {
    public ConstantNoiseJsonLoader(@Nonnull SeedString<K> seed, Path dataFolder, JsonElement json) {
        super(seed.append(".ConstantNoise"), dataFolder, json);
    }

    @Override
    @Nonnull
    public NoiseFunction load() {
        return new ConstantNoise(this.loadValue());
    }

    protected double loadValue() {
        return this.has("Value") ? this.get("Value").getAsDouble() : 0.5;
    }

    public static interface Constants {
        public static final String KEY_VALUE = "Value";
        public static final double DEFAULT_VALUE = 0.5;
    }
}

