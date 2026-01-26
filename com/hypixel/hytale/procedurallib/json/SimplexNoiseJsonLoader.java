/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.json;

import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.NoiseFunction;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.logic.SimplexNoise;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public class SimplexNoiseJsonLoader<K extends SeedResource>
extends JsonLoader<K, NoiseFunction> {
    public SimplexNoiseJsonLoader(@Nonnull SeedString<K> seed, Path dataFolder, JsonElement json) {
        super(seed.append(".SimplexNoise"), dataFolder, json);
    }

    @Override
    @Nonnull
    public NoiseFunction load() {
        return SimplexNoise.INSTANCE;
    }
}

