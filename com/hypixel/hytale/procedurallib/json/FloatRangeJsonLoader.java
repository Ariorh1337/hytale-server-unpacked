/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.supplier.FloatRange;
import com.hypixel.hytale.procedurallib.supplier.IFloatRange;
import java.nio.file.Path;
import java.util.Objects;
import javax.annotation.Nonnull;

public class FloatRangeJsonLoader<K extends SeedResource>
extends JsonLoader<K, IFloatRange> {
    protected final float default1;
    protected final float default2;
    @Nonnull
    protected final FloatToFloatFunction function;

    public FloatRangeJsonLoader(@Nonnull SeedString<K> seed, Path dataFolder, JsonElement json) {
        this(seed, dataFolder, json, 0.0f, d -> d);
    }

    public FloatRangeJsonLoader(@Nonnull SeedString<K> seed, Path dataFolder, JsonElement json, FloatToFloatFunction function) {
        this(seed, dataFolder, json, 0.0f, function);
    }

    public FloatRangeJsonLoader(@Nonnull SeedString<K> seed, Path dataFolder, JsonElement json, float default1) {
        this(seed, dataFolder, json, default1, default1, d -> d);
    }

    public FloatRangeJsonLoader(@Nonnull SeedString<K> seed, Path dataFolder, JsonElement json, float default1, FloatToFloatFunction function) {
        this(seed, dataFolder, json, default1, default1, function);
    }

    public FloatRangeJsonLoader(@Nonnull SeedString<K> seed, Path dataFolder, JsonElement json, float default1, float default2) {
        this(seed, dataFolder, json, default1, default2, d -> d);
    }

    public FloatRangeJsonLoader(@Nonnull SeedString<K> seed, Path dataFolder, JsonElement json, float default1, float default2, FloatToFloatFunction function) {
        super(seed.append(".DoubleRange"), dataFolder, json);
        this.default1 = default1;
        this.default2 = default2;
        this.function = Objects.requireNonNull(function);
    }

    @Override
    @Nonnull
    public IFloatRange load() {
        if (this.json == null || this.json.isJsonNull()) {
            if (this.default1 == this.default2) {
                return new FloatRange.Constant(this.function.get(this.default1));
            }
            return new FloatRange.Normal(this.function.get(this.default1), this.function.get(this.default2));
        }
        if (this.json.isJsonArray()) {
            JsonArray array = this.json.getAsJsonArray();
            if (array.size() != 1 && array.size() != 2) {
                throw new IllegalStateException(String.format("Range array contains %s values. Only 1 or 2 entries are allowed.", array.size()));
            }
            if (array.size() == 1) {
                return new FloatRange.Constant(this.function.get(array.get(0).getAsFloat()));
            }
            return new FloatRange.Normal(this.function.get(array.get(0).getAsFloat()), this.function.get(array.get(1).getAsFloat()));
        }
        if (this.json.isJsonObject()) {
            if (!this.has("Min")) {
                throw new IllegalStateException("Minimum value of range is not defined. Keyword: Min");
            }
            if (!this.has("Max")) {
                throw new IllegalStateException("Maximum value of range is not defined. Keyword: Max");
            }
            float min = this.get("Min").getAsFloat();
            float max = this.get("Max").getAsFloat();
            return new FloatRange.Normal(this.function.get(min), this.function.get(max));
        }
        return new FloatRange.Constant(this.function.get(this.json.getAsFloat()));
    }

    public static interface Constants {
        public static final String KEY_MIN = "Min";
        public static final String KEY_MAX = "Max";
        public static final String ERROR_ARRAY_SIZE = "Range array contains %s values. Only 1 or 2 entries are allowed.";
        public static final String ERROR_NO_MIN = "Minimum value of range is not defined. Keyword: Min";
        public static final String ERROR_NO_MAX = "Maximum value of range is not defined. Keyword: Max";
    }

    @FunctionalInterface
    public static interface FloatToFloatFunction {
        public float get(float var1);
    }
}

