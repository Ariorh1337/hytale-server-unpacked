/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.json;

import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.NoiseFunction;
import com.hypixel.hytale.procedurallib.NoiseType;
import com.hypixel.hytale.procedurallib.json.BranchNoiseJsonLoader;
import com.hypixel.hytale.procedurallib.json.CellNoiseJsonLoader;
import com.hypixel.hytale.procedurallib.json.ConstantNoiseJsonLoader;
import com.hypixel.hytale.procedurallib.json.DistanceNoiseJsonLoader;
import com.hypixel.hytale.procedurallib.json.GridNoiseJsonLoader;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.MeshNoiseJsonLoader;
import com.hypixel.hytale.procedurallib.json.OldSimplexNoiseJsonLoader;
import com.hypixel.hytale.procedurallib.json.PerlinNoiseJsonLoader;
import com.hypixel.hytale.procedurallib.json.PointNoiseJsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.json.SimplexNoiseJsonLoader;
import com.hypixel.hytale.procedurallib.json.ValueNoiseJsonLoader;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public enum NoiseTypeJson {
    CELL(NoiseType.CELL, CellNoiseJsonLoader.class),
    CONSTANT(NoiseType.CONSTANT, ConstantNoiseJsonLoader.class),
    DISTANCE(NoiseType.DISTANCE, DistanceNoiseJsonLoader.class),
    PERLIN(NoiseType.PERLIN, PerlinNoiseJsonLoader.class),
    SIMPLEX(NoiseType.SIMPLEX, SimplexNoiseJsonLoader.class),
    OLD_SIMPLEX(NoiseType.OLD_SIMPLEX, OldSimplexNoiseJsonLoader.class),
    VALUE(NoiseType.VALUE, ValueNoiseJsonLoader.class),
    MESH(NoiseType.MESH, MeshNoiseJsonLoader.class),
    GRID(NoiseType.GRID, GridNoiseJsonLoader.class),
    BRANCH(NoiseType.BRANCH, BranchNoiseJsonLoader.class),
    POINT(NoiseType.POINT, PointNoiseJsonLoader.class);

    private final NoiseType noiseType;
    @Nonnull
    private final Constructor constructor;

    private <T extends JsonLoader<?, NoiseFunction>> NoiseTypeJson(NoiseType noiseType, Class<T> loaderClass) {
        this.noiseType = noiseType;
        try {
            this.constructor = loaderClass.getConstructor(SeedString.class, Path.class, JsonElement.class);
            this.constructor.setAccessible(true);
        }
        catch (NoSuchMethodException e) {
            throw new Error(String.format("Could not find loader constructor for %s. NoiseType: %s", new Object[]{loaderClass.getName(), noiseType}), e);
        }
    }

    @Nonnull
    public <K extends SeedResource> JsonLoader<K, NoiseFunction> newLoader(SeedString<K> seed, Path dataFolder, JsonElement json) {
        try {
            return (JsonLoader)this.constructor.newInstance(seed, dataFolder, json);
        }
        catch (Exception e) {
            throw new Error(String.format("Failed to execute loader constructor! NoiseType: %s", new Object[]{this.noiseType}), e);
        }
    }

    public static interface Constants {
        public static final String ERROR_NO_CONSTRUCTOR = "Could not find loader constructor for %s. NoiseType: %s";
        public static final String ERROR_FAILED_CONSTRUCTOR = "Failed to execute loader constructor! NoiseType: %s";
    }
}

