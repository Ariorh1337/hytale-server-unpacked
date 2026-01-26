/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.container;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.condition.ICoordinateCondition;
import com.hypixel.hytale.procedurallib.json.DoubleRangeJsonLoader;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.NoiseMaskConditionJsonLoader;
import com.hypixel.hytale.procedurallib.json.NoisePropertyJsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.property.NoiseProperty;
import com.hypixel.hytale.procedurallib.supplier.DoubleRange;
import com.hypixel.hytale.procedurallib.supplier.DoubleRangeNoiseSupplier;
import com.hypixel.hytale.procedurallib.supplier.IDoubleCoordinateSupplier;
import com.hypixel.hytale.procedurallib.supplier.IDoubleRange;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.container.LayerContainer;
import com.hypixel.hytale.server.worldgen.loader.util.NoiseBlockArrayJsonLoader;
import com.hypixel.hytale.server.worldgen.util.ConstantNoiseProperty;
import com.hypixel.hytale.server.worldgen.util.NoiseBlockArray;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LayerContainerJsonLoader
extends JsonLoader<SeedStringResource, LayerContainer> {
    public LayerContainerJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json) {
        super(seed.append(".LayerContainer"), dataFolder, json);
    }

    @Override
    @Nonnull
    public LayerContainer load() {
        return new LayerContainer(this.loadDefault(), this.loadDefaultEnvironment(), this.loadStaticLayers(), this.loadDynamicLayers());
    }

    protected int loadDefault() {
        if (!this.has("Default")) {
            throw new IllegalArgumentException("Could not find default material. Keyword: Default");
        }
        String blockName = this.get("Default").getAsString();
        int index = BlockType.getAssetMap().getIndex(blockName);
        if (index == Integer.MIN_VALUE) {
            throw new Error(String.format("Default block for LayerContainer could not be found! BlockType: %s", blockName));
        }
        return index;
    }

    protected int loadDefaultEnvironment() {
        int environment = Integer.MIN_VALUE;
        if (this.has("Environment")) {
            String environmentId = this.get("Environment").getAsString();
            environment = Environment.getAssetMap().getIndex(environmentId);
            if (environment == Integer.MIN_VALUE) {
                throw new Error(String.format("Error while looking up environment \"%s\"!", environmentId));
            }
        }
        return environment;
    }

    @Nonnull
    protected LayerContainer.StaticLayer[] loadStaticLayers() {
        if (this.has("Static")) {
            JsonArray array = this.get("Static").getAsJsonArray();
            LayerContainer.StaticLayer[] layers = new LayerContainer.StaticLayer[array.size()];
            for (int i = 0; i < layers.length; ++i) {
                try {
                    layers[i] = new StaticLayerJsonLoader(this.seed.append("-" + i), this.dataFolder, array.get(i)).load();
                    continue;
                }
                catch (Throwable e) {
                    throw new Error(String.format("Error while loading StaticLayer #%s", i), e);
                }
            }
            return layers;
        }
        return new LayerContainer.StaticLayer[0];
    }

    @Nonnull
    protected LayerContainer.DynamicLayer[] loadDynamicLayers() {
        if (this.has("Dynamic")) {
            JsonArray array = this.get("Dynamic").getAsJsonArray();
            LayerContainer.DynamicLayer[] layers = new LayerContainer.DynamicLayer[array.size()];
            for (int i = 0; i < layers.length; ++i) {
                try {
                    layers[i] = new DynamicLayerJsonLoader(this.seed.append("-" + i), this.dataFolder, array.get(i)).load();
                    continue;
                }
                catch (Throwable e) {
                    throw new Error(String.format("Error while loading DynamicLayer #%s", i), e);
                }
            }
            return layers;
        }
        return new LayerContainer.DynamicLayer[0];
    }

    public static interface Constants {
        public static final String KEY_DEFAULT = "Default";
        public static final String KEY_DYNAMIC = "Dynamic";
        public static final String KEY_STATIC = "Static";
        public static final String KEY_ENTRY_ENTRIES = "Entries";
        public static final String KEY_ENTRY_BLOCKS = "Blocks";
        public static final String KEY_ENTRY_NOISE_MASK = "NoiseMask";
        public static final String KEY_ENTRY_DYNAMIC_OFFSET = "Offset";
        public static final String KEY_ENTRY_DYNAMIC_OFFSET_NOISE = "OffsetNoise";
        public static final String KEY_ENTRY_STATIC_MIN = "Min";
        public static final String KEY_ENTRY_STATIC_MIN_NOISE = "MinNoise";
        public static final String KEY_ENTRY_STATIC_MAX = "Max";
        public static final String KEY_ENTRY_STATIC_MAX_NOISE = "MaxNoise";
        public static final String KEY_ENVIRONMENT = "Environment";
        public static final String ERROR_NO_DEFAULT = "Could not find default material. Keyword: Default";
        public static final String ERROR_DEFAULT_INVALID = "Default block for LayerContainer could not be found! BlockType: %s";
        public static final String ERROR_FAIL_DYNAMIC_LAYER = "Error while loading DynamicLayer #%s";
        public static final String ERROR_FAIL_STATIC_LAYER = "Error while loading StaticLayer #%s";
        public static final String ERROR_NO_BLOCKS = "Could not find block data for layer entry. Keyword: Blocks";
        public static final String ERROR_UNKOWN_STATIC = "Unknown type for static Layer";
        public static final String ERROR_UNKOWN_DYNAMIC = "Unknown type for dynamic Layer";
        public static final String ERROR_FAIL_DYNAMIC_ENTRY = "Error while loading DynamicLayerEntry #%s";
        public static final String ERROR_FAIL_STATIC_ENTRY = "Error while loading StaticLayerEntry #%s";
        public static final String ERROR_STATIC_NO_MIN = "Could not find minimum of static layer entry.";
        public static final String ERROR_STATIC_NO_MAX = "Could not find maximum of static layer entry.";
    }

    protected static class StaticLayerJsonLoader
    extends JsonLoader<SeedStringResource, LayerContainer.StaticLayer> {
        public StaticLayerJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json) {
            super(seed.append(".StaticLayer"), dataFolder, json);
        }

        @Override
        @Nonnull
        public LayerContainer.StaticLayer load() {
            return new LayerContainer.StaticLayer(this.loadEntries(), this.loadMapCondition(), this.loadEnvironment());
        }

        @Nonnull
        protected ICoordinateCondition loadMapCondition() {
            return new NoiseMaskConditionJsonLoader(this.seed, this.dataFolder, this.get("NoiseMask")).load();
        }

        @Nonnull
        protected LayerContainer.StaticLayerEntry[] loadEntries() {
            if (this.json == null || this.json.isJsonNull()) {
                return new LayerContainer.StaticLayerEntry[0];
            }
            if (this.json.isJsonObject()) {
                if (this.has("Entries")) {
                    JsonArray array = this.get("Entries").getAsJsonArray();
                    LayerContainer.StaticLayerEntry[] entries = new LayerContainer.StaticLayerEntry[array.size()];
                    for (int i = 0; i < entries.length; ++i) {
                        try {
                            entries[i] = new StaticLayerEntryJsonLoader(this.seed.append("-" + i), this.dataFolder, array.get(i)).load();
                            continue;
                        }
                        catch (Throwable e) {
                            throw new Error(String.format("Error while loading StaticLayerEntry #%s", i), e);
                        }
                    }
                    return entries;
                }
                try {
                    return new LayerContainer.StaticLayerEntry[]{new StaticLayerEntryJsonLoader(this.seed, this.dataFolder, this.json).load()};
                }
                catch (Throwable e) {
                    throw new Error(String.format("Error while loading StaticLayerEntry #%s", 0), e);
                }
            }
            throw new Error("Unknown type for static Layer");
        }

        protected int loadEnvironment() {
            int environment = Integer.MIN_VALUE;
            if (this.has("Environment")) {
                String environmentId = this.get("Environment").getAsString();
                environment = Environment.getAssetMap().getIndex(environmentId);
                if (environment == Integer.MIN_VALUE) {
                    throw new Error(String.format("Error while looking up environment \"%s\"!", environmentId));
                }
            }
            return environment;
        }

        protected static class StaticLayerEntryJsonLoader
        extends LayerEntryJsonLoader<LayerContainer.StaticLayerEntry> {
            public StaticLayerEntryJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json) {
                super(seed.append(".StaticLayerEntry"), dataFolder, json);
            }

            @Override
            @Nonnull
            public LayerContainer.StaticLayerEntry load() {
                return new LayerContainer.StaticLayerEntry(this.loadBlocks(), this.loadMapCondition(), this.loadMin(), this.loadMax());
            }

            @Nonnull
            protected IDoubleCoordinateSupplier loadMin() {
                if (!this.has("Min")) {
                    throw new IllegalArgumentException("Could not find minimum of static layer entry.");
                }
                IDoubleRange array = new DoubleRangeJsonLoader(this.seed, this.dataFolder, this.get("Min"), 0.0).load();
                NoiseProperty minNoise = this.loadMinNoise();
                return new DoubleRangeNoiseSupplier(array, minNoise);
            }

            @Nonnull
            protected IDoubleCoordinateSupplier loadMax() {
                if (!this.has("Max")) {
                    throw new IllegalArgumentException("Could not find maximum of static layer entry.");
                }
                IDoubleRange array = new DoubleRangeJsonLoader(this.seed, this.dataFolder, this.get("Max"), 0.0).load();
                NoiseProperty maxNoise = this.loadMaxNoise();
                return new DoubleRangeNoiseSupplier(array, maxNoise);
            }

            @Nullable
            protected NoiseProperty loadMinNoise() {
                NoiseProperty minNoise = ConstantNoiseProperty.DEFAULT_ZERO;
                if (this.has("MinNoise")) {
                    minNoise = new NoisePropertyJsonLoader(this.seed, this.dataFolder, this.get("MinNoise")).load();
                }
                return minNoise;
            }

            @Nullable
            protected NoiseProperty loadMaxNoise() {
                NoiseProperty maxNoise = ConstantNoiseProperty.DEFAULT_ZERO;
                if (this.has("MaxNoise")) {
                    maxNoise = new NoisePropertyJsonLoader(this.seed, this.dataFolder, this.get("MaxNoise")).load();
                }
                return maxNoise;
            }
        }
    }

    protected static class DynamicLayerJsonLoader
    extends JsonLoader<SeedStringResource, LayerContainer.DynamicLayer> {
        public DynamicLayerJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json) {
            super(seed.append(".DynamicLayer"), dataFolder, json);
        }

        @Override
        @Nonnull
        public LayerContainer.DynamicLayer load() {
            return new LayerContainer.DynamicLayer(this.loadEntries(), this.loadMapCondition(), this.loadEnvironment(), this.loadOffset());
        }

        @Nonnull
        protected ICoordinateCondition loadMapCondition() {
            return new NoiseMaskConditionJsonLoader(this.seed, this.dataFolder, this.get("NoiseMask")).load();
        }

        @Nonnull
        protected IDoubleCoordinateSupplier loadOffset() {
            IDoubleRange offset = DoubleRange.ZERO;
            NoiseProperty offsetNoise = ConstantNoiseProperty.DEFAULT_ZERO;
            if (this.has("Offset")) {
                offset = new DoubleRangeJsonLoader(this.seed, this.dataFolder, this.get("Offset")).load();
                if (this.has("OffsetNoise")) {
                    offsetNoise = new NoisePropertyJsonLoader(this.seed, this.dataFolder, this.get("OffsetNoise")).load();
                }
            }
            return new DoubleRangeNoiseSupplier(offset, offsetNoise);
        }

        @Nonnull
        protected LayerContainer.DynamicLayerEntry[] loadEntries() {
            if (this.json == null || this.json.isJsonNull()) {
                return new LayerContainer.DynamicLayerEntry[0];
            }
            if (this.json.isJsonObject()) {
                if (this.has("Entries")) {
                    JsonArray array = this.get("Entries").getAsJsonArray();
                    LayerContainer.DynamicLayerEntry[] entries = new LayerContainer.DynamicLayerEntry[array.size()];
                    for (int i = 0; i < entries.length; ++i) {
                        try {
                            entries[i] = new DynamicLayerEntryJsonLoader(this.seed.append("-" + i), this.dataFolder, array.get(i)).load();
                            continue;
                        }
                        catch (Throwable e) {
                            throw new Error(String.format("Error while loading DynamicLayerEntry #%s", i), e);
                        }
                    }
                    return entries;
                }
                try {
                    return new LayerContainer.DynamicLayerEntry[]{new DynamicLayerEntryJsonLoader(this.seed, this.dataFolder, this.json).load()};
                }
                catch (Throwable e) {
                    throw new Error(String.format("Error while loading DynamicLayerEntry #%s", 0), e);
                }
            }
            throw new Error("Unknown type for dynamic Layer");
        }

        protected int loadEnvironment() {
            int environment = Integer.MIN_VALUE;
            if (this.has("Environment")) {
                String environmentId = this.get("Environment").getAsString();
                environment = Environment.getAssetMap().getIndex(environmentId);
                if (environment == Integer.MIN_VALUE) {
                    throw new Error(String.format("Error while looking up environment \"%s\"!", environmentId));
                }
            }
            return environment;
        }

        protected static class DynamicLayerEntryJsonLoader
        extends LayerEntryJsonLoader<LayerContainer.DynamicLayerEntry> {
            public DynamicLayerEntryJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json) {
                super(seed, dataFolder, json);
            }

            @Override
            @Nonnull
            public LayerContainer.DynamicLayerEntry load() {
                return new LayerContainer.DynamicLayerEntry(this.loadBlocks(), this.loadMapCondition());
            }
        }
    }

    protected static abstract class LayerEntryJsonLoader<T extends LayerContainer.LayerEntry>
    extends JsonLoader<SeedStringResource, T> {
        public LayerEntryJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json) {
            super(seed.append(".LayerEntry"), dataFolder, json);
        }

        @Nonnull
        protected NoiseBlockArray loadBlocks() {
            if (!this.has("Blocks")) {
                return NoiseBlockArray.EMPTY;
            }
            return new NoiseBlockArrayJsonLoader(this.seed, this.dataFolder, this.get("Blocks")).load();
        }

        @Nonnull
        protected ICoordinateCondition loadMapCondition() {
            return new NoiseMaskConditionJsonLoader(this.seed, this.dataFolder, this.get("NoiseMask")).load();
        }
    }
}

