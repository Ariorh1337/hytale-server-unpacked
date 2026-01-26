/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.container;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.hypixel.hytale.common.map.IWeightedMap;
import com.hypixel.hytale.common.map.WeightedMap;
import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.procedurallib.condition.ICoordinateCondition;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.NoiseMaskConditionJsonLoader;
import com.hypixel.hytale.procedurallib.json.NoisePropertyJsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.property.NoiseProperty;
import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.container.EnvironmentContainer;
import com.hypixel.hytale.server.worldgen.util.ConstantNoiseProperty;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnvironmentContainerJsonLoader
extends JsonLoader<SeedStringResource, EnvironmentContainer> {
    public EnvironmentContainerJsonLoader(SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json) {
        super(seed, dataFolder, json);
    }

    @Override
    @Nonnull
    public EnvironmentContainer load() {
        return new EnvironmentContainer(this.loadDefault(), this.loadEntries());
    }

    @Nonnull
    protected EnvironmentContainer.DefaultEnvironmentContainerEntry loadDefault() {
        JsonElement element = this.json == null || this.json.isJsonNull() || this.json.isJsonArray() ? null : (this.json.isJsonObject() && this.has("Default") ? this.get("Default") : (this.json.isJsonPrimitive() || this.json.isJsonObject() ? this.json : null));
        return new DefaultEnvironmentContainerEntryLoader(this.seed, this.dataFolder, element).load();
    }

    @Nonnull
    protected EnvironmentContainer.EnvironmentContainerEntry[] loadEntries() {
        if (this.json == null || !this.json.isJsonObject() || !this.has("Entries")) {
            return EnvironmentContainer.EnvironmentContainerEntry.EMPTY_ARRAY;
        }
        JsonArray arr = this.get("Entries").getAsJsonArray();
        if (arr.isEmpty()) {
            return EnvironmentContainer.EnvironmentContainerEntry.EMPTY_ARRAY;
        }
        EnvironmentContainer.EnvironmentContainerEntry[] entries = new EnvironmentContainer.EnvironmentContainerEntry[arr.size()];
        for (int i = 0; i < arr.size(); ++i) {
            try {
                entries[i] = new EnvironmentContainerEntryJsonLoader(this.seed.append(String.format("-%s", i)), this.dataFolder, arr.get(i)).load();
                continue;
            }
            catch (Throwable e) {
                throw new Error(String.format("Failed to load TintContainerEntry #%s", i), e);
            }
        }
        return entries;
    }

    public static interface Constants {
        public static final String KEY_DEFAULT = "Default";
        public static final String KEY_ENTRIES = "Entries";
        public static final String KEY_NAMES = "Names";
        public static final String KEY_WEIGHTS = "Weights";
        public static final String KEY_ENTRY_NOISE = "Noise";
        public static final String KEY_NOISE_MASK = "NoiseMask";
        public static final String ERROR_NAMES_NOT_FOUND = "Could not find names. Keyword: Names";
        public static final String ERROR_WEIGHT_SIZE = "Tint weights array size does not fit color array size.";
        public static final String ERROR_NO_VALUE_NOISE = "Could not find value noise. Keyword: Noise";
        public static final String ERROR_LOADING_ENTRY = "Failed to load TintContainerEntry #%s";
        public static final String SEED_INDEX_SUFFIX = "-%s";
    }

    protected static class DefaultEnvironmentContainerEntryLoader
    extends EnvironmentContainerEntryJsonLoader {
        public DefaultEnvironmentContainerEntryLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json) {
            super(seed, dataFolder, json);
        }

        @Override
        @Nonnull
        public EnvironmentContainer.DefaultEnvironmentContainerEntry load() {
            IWeightedMap<Integer> colorMapping;
            if (this.json == null || this.json.isJsonNull()) {
                WeightedMap.Builder<Integer> builder = WeightedMap.builder(ArrayUtil.EMPTY_INTEGER_ARRAY);
                builder.put(0, 1.0);
                colorMapping = builder.build();
            } else if (this.json.isJsonPrimitive()) {
                WeightedMap.Builder<Integer> builder = WeightedMap.builder(ArrayUtil.EMPTY_INTEGER_ARRAY);
                String key = this.json.getAsString();
                int index = Environment.getAssetMap().getIndex(key);
                if (index == Integer.MIN_VALUE) {
                    throw new IllegalArgumentException("Unknown key! " + key);
                }
                builder.put(index, 1.0);
                colorMapping = builder.build();
            } else {
                colorMapping = this.loadIdMapping();
            }
            return new EnvironmentContainer.DefaultEnvironmentContainerEntry(colorMapping, colorMapping.size() > 1 ? this.loadValueNoise() : ConstantNoiseProperty.DEFAULT_ZERO);
        }
    }

    protected static class EnvironmentContainerEntryJsonLoader
    extends JsonLoader<SeedStringResource, EnvironmentContainer.EnvironmentContainerEntry> {
        public EnvironmentContainerEntryJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json) {
            super(seed.append(".EnvironmentContainer"), dataFolder, json);
        }

        @Override
        @Nonnull
        public EnvironmentContainer.EnvironmentContainerEntry load() {
            IWeightedMap<Integer> colorMapping;
            return new EnvironmentContainer.EnvironmentContainerEntry(colorMapping, (colorMapping = this.loadIdMapping()).size() > 1 ? this.loadValueNoise() : ConstantNoiseProperty.DEFAULT_ZERO, this.loadMapCondition());
        }

        @Nonnull
        protected IWeightedMap<Integer> loadIdMapping() {
            WeightedMap.Builder<Integer> builder = WeightedMap.builder(ArrayUtil.EMPTY_INTEGER_ARRAY);
            if (this.json == null || this.json.isJsonNull()) {
                builder.put(0, 1.0);
            } else if (this.json.isJsonObject()) {
                if (!this.has("Names")) {
                    throw new IllegalArgumentException("Could not find names. Keyword: Names");
                }
                JsonElement colorsElement = this.get("Names");
                if (colorsElement.isJsonArray()) {
                    JsonArray weights;
                    JsonArray names = colorsElement.getAsJsonArray();
                    JsonArray jsonArray = weights = this.has("Weights") ? this.get("Weights").getAsJsonArray() : null;
                    if (weights != null && weights.size() != names.size()) {
                        throw new IllegalArgumentException("Tint weights array size does not fit color array size.");
                    }
                    for (int i = 0; i < names.size(); ++i) {
                        String key = names.get(i).getAsString();
                        int index = Environment.getAssetMap().getIndex(key);
                        if (index == Integer.MIN_VALUE) {
                            throw new IllegalArgumentException("Unknown key! " + key);
                        }
                        double weight = weights == null ? 1.0 : weights.get(i).getAsDouble();
                        builder.put(index, weight);
                    }
                } else {
                    String key = colorsElement.getAsString();
                    int index = Environment.getAssetMap().getIndex(key);
                    if (index == Integer.MIN_VALUE) {
                        throw new IllegalArgumentException("Unknown key! " + key);
                    }
                    builder.put(index, 1.0);
                }
            } else {
                String key = this.json.getAsString();
                int index = Environment.getAssetMap().getIndex(key);
                if (index == Integer.MIN_VALUE) {
                    throw new IllegalArgumentException("Unknown key! " + key);
                }
                builder.put(index, 1.0);
            }
            return builder.build();
        }

        @Nullable
        protected NoiseProperty loadValueNoise() {
            if (!this.has("Noise")) {
                throw new IllegalArgumentException("Could not find value noise. Keyword: Noise");
            }
            return new NoisePropertyJsonLoader(this.seed, this.dataFolder, this.get("Noise")).load();
        }

        @Nonnull
        protected ICoordinateCondition loadMapCondition() {
            return new NoiseMaskConditionJsonLoader(this.seed, this.dataFolder, this.get("NoiseMask")).load();
        }
    }
}

