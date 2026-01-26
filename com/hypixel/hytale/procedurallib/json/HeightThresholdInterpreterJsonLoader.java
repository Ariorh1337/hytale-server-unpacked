/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.json;

import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.condition.IHeightThresholdInterpreter;
import com.hypixel.hytale.procedurallib.json.BasicHeightThresholdInterpreterJsonLoader;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.NoiseHeightThresholdInterpreterJsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public class HeightThresholdInterpreterJsonLoader<K extends SeedResource>
extends JsonLoader<K, IHeightThresholdInterpreter> {
    protected final int length;

    public HeightThresholdInterpreterJsonLoader(@Nonnull SeedString<K> seed, Path dataFolder, JsonElement json, int length) {
        super(seed.append(".HeightThresholdInterpreter"), dataFolder, json);
        this.length = length;
    }

    @Override
    @Nonnull
    public IHeightThresholdInterpreter load() {
        if (NoiseHeightThresholdInterpreterJsonLoader.shouldHandle(this.json.getAsJsonObject())) {
            return new NoiseHeightThresholdInterpreterJsonLoader(this.seed, this.dataFolder, this.json, this.length).load();
        }
        return new BasicHeightThresholdInterpreterJsonLoader(this.seed, this.dataFolder, this.json, this.length).load();
    }
}

