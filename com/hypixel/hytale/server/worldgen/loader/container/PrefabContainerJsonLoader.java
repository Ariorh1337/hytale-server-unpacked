/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.container;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.hypixel.hytale.common.map.IWeightedMap;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.container.PrefabContainer;
import com.hypixel.hytale.server.worldgen.loader.WorldGenPrefabSupplier;
import com.hypixel.hytale.server.worldgen.loader.context.FileLoadingContext;
import com.hypixel.hytale.server.worldgen.loader.prefab.PrefabPatternGeneratorJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.prefab.WeightedPrefabMapJsonLoader;
import com.hypixel.hytale.server.worldgen.prefab.PrefabPatternGenerator;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public class PrefabContainerJsonLoader
extends JsonLoader<SeedStringResource, PrefabContainer> {
    private final FileLoadingContext context;

    public PrefabContainerJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json, FileLoadingContext context) {
        super(seed.append(".PrefabContainer"), dataFolder, json);
        this.context = context;
    }

    @Override
    @Nonnull
    public PrefabContainer load() {
        return new PrefabContainer(this.loadEntries());
    }

    @Nonnull
    protected PrefabContainer.PrefabContainerEntry[] loadEntries() {
        if (this.has("Entries")) {
            JsonArray entryArray = this.get("Entries").getAsJsonArray();
            PrefabContainer.PrefabContainerEntry[] entries = new PrefabContainer.PrefabContainerEntry[entryArray.size()];
            for (int i = 0; i < entries.length; ++i) {
                try {
                    entries[i] = new PrefabContainerEntryJsonLoader(this.seed.append("-" + i), this.dataFolder, entryArray.get(i), this.context).load();
                    continue;
                }
                catch (Throwable e) {
                    throw new Error(String.format("Failed to load prefab container entry #%s.", i), e);
                }
            }
            return entries;
        }
        return new PrefabContainer.PrefabContainerEntry[0];
    }

    public static interface Constants {
        public static final String KEY_ENTRIES = "Entries";
        public static final String KEY_ENTRY_PREFAB = "Prefab";
        public static final String KEY_ENTRY_WEIGHT = "Weight";
        public static final String KEY_ENTRY_PATTERN = "Pattern";
        public static final String KEY_ENVIRONMENT = "Environment";
        public static final String ERROR_FAIL_ENTRY = "Failed to load prefab container entry #%s.";
        public static final String ERROR_LOADING_ENVIRONMENT = "Error while looking up environment \"%s\"!";
        public static final String ERROR_ENTRY_NO_PATTERN = "Could not find prefab pattern. Keyword: Pattern";
    }

    public static class PrefabContainerEntryJsonLoader
    extends JsonLoader<SeedStringResource, PrefabContainer.PrefabContainerEntry> {
        private final FileLoadingContext context;

        public PrefabContainerEntryJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json, FileLoadingContext context) {
            super(seed.append(".PrefabContainerEntry"), dataFolder, json);
            this.context = context;
        }

        @Override
        @Nonnull
        public PrefabContainer.PrefabContainerEntry load() {
            Object prefabs = new WeightedPrefabMapJsonLoader(this.seed, this.dataFolder, this.json, "Prefab", "Weight").load();
            if (!this.has("Pattern")) {
                throw new IllegalArgumentException("Could not find prefab pattern. Keyword: Pattern");
            }
            PrefabPatternGenerator prefabPatternGenerator = new PrefabPatternGeneratorJsonLoader(this.seed, this.dataFolder, this.get("Pattern"), this.context).load();
            return new PrefabContainer.PrefabContainerEntry((IWeightedMap<WorldGenPrefabSupplier>)prefabs, prefabPatternGenerator, this.loadEnvironment());
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
    }
}

