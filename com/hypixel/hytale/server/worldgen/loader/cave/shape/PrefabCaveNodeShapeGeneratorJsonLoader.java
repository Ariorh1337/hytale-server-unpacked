/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.cave.shape;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.cave.shape.PrefabCaveNodeShape;
import com.hypixel.hytale.server.worldgen.loader.WorldGenPrefabLoader;
import com.hypixel.hytale.server.worldgen.loader.WorldGenPrefabSupplier;
import com.hypixel.hytale.server.worldgen.loader.cave.shape.CaveNodeShapeGeneratorJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.prefab.BlockPlacementMaskJsonLoader;
import com.hypixel.hytale.server.worldgen.util.condition.BlockMaskCondition;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrefabCaveNodeShapeGeneratorJsonLoader
extends CaveNodeShapeGeneratorJsonLoader {
    public PrefabCaveNodeShapeGeneratorJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json) {
        super(seed.append(".PrefabCaveNodeShapeGenerator"), dataFolder, json);
    }

    @Override
    @Nonnull
    public PrefabCaveNodeShape.PrefabCaveNodeShapeGenerator load() {
        return new PrefabCaveNodeShape.PrefabCaveNodeShapeGenerator(this.loadPrefabs(), this.loadMask());
    }

    @Nonnull
    protected List<WorldGenPrefabSupplier> loadPrefabs() {
        WorldGenPrefabLoader loader = ((SeedStringResource)this.seed.get()).getLoader();
        ArrayList<WorldGenPrefabSupplier> prefabs = new ArrayList<WorldGenPrefabSupplier>();
        JsonElement prefabElement = this.get("Prefab");
        if (prefabElement.isJsonArray()) {
            JsonArray prefabArray = prefabElement.getAsJsonArray();
            for (JsonElement prefabArrayElement : prefabArray) {
                String prefabString = prefabArrayElement.getAsString();
                Collections.addAll(prefabs, loader.get(prefabString));
            }
        } else {
            String prefabString = prefabElement.getAsString();
            Collections.addAll(prefabs, loader.get(prefabString));
        }
        if (prefabs.isEmpty()) {
            throw new IllegalArgumentException("Prefabs are empty! Key: Prefab");
        }
        return prefabs;
    }

    @Nullable
    protected BlockMaskCondition loadMask() {
        BlockMaskCondition configuration = BlockMaskCondition.DEFAULT_TRUE;
        if (this.has("Mask")) {
            configuration = new BlockPlacementMaskJsonLoader(this.seed, this.dataFolder, this.getRaw("Mask")).load();
        }
        return configuration;
    }

    public static interface Constants {
        public static final String KEY_PREFAB = "Prefab";
        public static final String KEY_MASK = "Mask";
    }
}

