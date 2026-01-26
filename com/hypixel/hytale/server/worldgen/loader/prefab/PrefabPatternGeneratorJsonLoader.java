/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.prefab;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.condition.ConstantBlockFluidCondition;
import com.hypixel.hytale.procedurallib.condition.DefaultCoordinateRndCondition;
import com.hypixel.hytale.procedurallib.condition.HeightCondition;
import com.hypixel.hytale.procedurallib.condition.IBlockFluidCondition;
import com.hypixel.hytale.procedurallib.condition.ICoordinateCondition;
import com.hypixel.hytale.procedurallib.condition.ICoordinateRndCondition;
import com.hypixel.hytale.procedurallib.condition.IHeightThresholdInterpreter;
import com.hypixel.hytale.procedurallib.json.DoubleRangeJsonLoader;
import com.hypixel.hytale.procedurallib.json.HeightThresholdInterpreterJsonLoader;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.NoiseMaskConditionJsonLoader;
import com.hypixel.hytale.procedurallib.json.PointGeneratorJsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.logic.point.IPointGenerator;
import com.hypixel.hytale.server.core.prefab.PrefabRotation;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.loader.context.FileLoadingContext;
import com.hypixel.hytale.server.worldgen.loader.prefab.BlockPlacementMaskJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.util.ResolvedBlockArrayJsonLoader;
import com.hypixel.hytale.server.worldgen.prefab.PrefabCategory;
import com.hypixel.hytale.server.worldgen.prefab.PrefabPatternGenerator;
import com.hypixel.hytale.server.worldgen.util.LogUtil;
import com.hypixel.hytale.server.worldgen.util.ResolvedBlockArray;
import com.hypixel.hytale.server.worldgen.util.condition.BlockMaskCondition;
import com.hypixel.hytale.server.worldgen.util.condition.HashSetBlockFluidCondition;
import com.hypixel.hytale.server.worldgen.util.function.ConstantCoordinateDoubleSupplier;
import com.hypixel.hytale.server.worldgen.util.function.ICoordinateDoubleSupplier;
import com.hypixel.hytale.server.worldgen.util.function.RandomCoordinateDoubleSupplier;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrefabPatternGeneratorJsonLoader
extends JsonLoader<SeedStringResource, PrefabPatternGenerator> {
    private final FileLoadingContext context;

    public PrefabPatternGeneratorJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json, FileLoadingContext context) {
        super(seed.append(".PrefabPatternGenerator"), dataFolder, json);
        this.context = context;
    }

    @Override
    @Nonnull
    public PrefabPatternGenerator load() {
        IHeightThresholdInterpreter heightThresholds = this.loadHeightThresholds();
        return new PrefabPatternGenerator(this.seed.hashCode(), this.loadCategory(), this.loadPattern(), this.loadHeightCondition(heightThresholds), heightThresholds, this.loadMask(), this.loadMapCondition(), this.loadParent(), this.loadRotations(), this.loadDisplacement(), this.loadFitHeightmap(), this.loadOnWater(), this.loadDeepSearch(heightThresholds), this.loadSubmerge(), this.loadMaxSize(), this.loadExclusionRadius());
    }

    @Nullable
    protected IPointGenerator loadPattern() {
        if (!this.has("GridGenerator")) {
            throw new IllegalArgumentException("Could not find point generator to place prefabs at! Keyword: GridGenerator");
        }
        return new PointGeneratorJsonLoader(this.seed, this.dataFolder, this.get("GridGenerator")).load();
    }

    protected PrefabCategory loadCategory() {
        String category = this.mustGetString("Category", "");
        if (category.isEmpty()) {
            return PrefabCategory.NONE;
        }
        if (!this.context.getPrefabCategories().contains(category)) {
            LogUtil.getLogger().at(Level.WARNING).log("Could not find prefab category: %s, defaulting to None", category);
            return PrefabCategory.NONE;
        }
        return this.context.getPrefabCategories().get(category);
    }

    @Nonnull
    protected IBlockFluidCondition loadParent() {
        IBlockFluidCondition parentMask = ConstantBlockFluidCondition.DEFAULT_TRUE;
        if (this.has("Parent")) {
            ResolvedBlockArray blockArray = new ResolvedBlockArrayJsonLoader(this.seed, this.dataFolder, this.get("Parent")).load();
            LongSet biomeSet = blockArray.getEntrySet();
            parentMask = new HashSetBlockFluidCondition(biomeSet);
        }
        return parentMask;
    }

    @Nullable
    protected IHeightThresholdInterpreter loadHeightThresholds() {
        IHeightThresholdInterpreter heightThreshold = null;
        if (this.has("HeightThreshold")) {
            heightThreshold = new HeightThresholdInterpreterJsonLoader(this.seed, this.dataFolder, this.get("HeightThreshold"), 320).load();
        }
        return heightThreshold;
    }

    @Nonnull
    protected ICoordinateRndCondition loadHeightCondition(@Nullable IHeightThresholdInterpreter thresholdInterpreter) {
        ICoordinateRndCondition heightCondition = DefaultCoordinateRndCondition.DEFAULT_TRUE;
        if (thresholdInterpreter != null) {
            heightCondition = new HeightCondition(thresholdInterpreter);
        }
        return heightCondition;
    }

    @Nonnull
    protected ICoordinateCondition loadMapCondition() {
        return new NoiseMaskConditionJsonLoader(this.seed, this.dataFolder, this.get("NoiseMask")).load();
    }

    @Nullable
    protected BlockMaskCondition loadMask() {
        BlockMaskCondition configuration = BlockMaskCondition.DEFAULT_TRUE;
        if (this.has("Mask")) {
            configuration = new BlockPlacementMaskJsonLoader(this.seed, this.dataFolder, this.getRaw("Mask")).load();
        }
        return configuration;
    }

    @Nullable
    protected PrefabRotation[] loadRotations() {
        PrefabRotation[] prefabRotations = null;
        if (this.has("Rotations")) {
            prefabRotations = PrefabPatternGeneratorJsonLoader.loadRotations(this.get("Rotations"));
        }
        return prefabRotations;
    }

    @Nonnull
    protected ICoordinateDoubleSupplier loadDisplacement() {
        ICoordinateDoubleSupplier supplier = ConstantCoordinateDoubleSupplier.DEFAULT_ZERO;
        if (this.has("Displacement")) {
            supplier = new RandomCoordinateDoubleSupplier(new DoubleRangeJsonLoader(this.seed, this.dataFolder, this.get("Displacement"), 0.0).load());
        }
        return supplier;
    }

    protected boolean loadFitHeightmap() {
        return this.has("FitHeightmap") && this.get("FitHeightmap").getAsBoolean();
    }

    protected boolean loadOnWater() {
        return this.has("OnWater") && this.get("OnWater").getAsBoolean();
    }

    protected boolean loadDeepSearch(@Nonnull IHeightThresholdInterpreter interpreter) {
        boolean deepSearch;
        boolean bl = deepSearch = this.has("DeepSearch") && this.get("DeepSearch").getAsBoolean();
        if (deepSearch && interpreter == null) {
            throw new IllegalArgumentException("DeepSearch is enabled but HeightThreshold is not set!");
        }
        return deepSearch;
    }

    protected boolean loadSubmerge() {
        return this.mustGetBool("Submerge", Constants.DEFAULT_SUBMERGE);
    }

    protected int loadMaxSize() {
        return this.mustGetNumber("MaxSize", Constants.DEFAULT_MAX_SIZE).intValue();
    }

    protected int loadExclusionRadius() {
        return this.mustGetNumber("ExclusionRadius", Constants.DEFAULT_EXCLUSION_RADIUS).intValue();
    }

    @Nullable
    public static PrefabRotation[] loadRotations(@Nullable JsonElement element) {
        if (element == null) {
            return null;
        }
        PrefabRotation[] prefabRotations = null;
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            if (array.size() <= 0) {
                throw new IllegalArgumentException("Array for rotations must be greater than 0 or left away to allow random rotation.");
            }
            prefabRotations = new PrefabRotation[array.size()];
            for (int i = 0; i < prefabRotations.length; ++i) {
                String name = array.get(i).getAsString();
                try {
                    prefabRotations[i] = PrefabRotation.valueOf(name);
                    continue;
                }
                catch (Throwable e) {
                    throw new Error("Could not find rotation \"" + name + "\". Allowed: " + Arrays.toString((Object[])PrefabRotation.VALUES));
                }
            }
        } else if (element.isJsonPrimitive()) {
            prefabRotations = new PrefabRotation[1];
            String name = element.getAsString();
            try {
                prefabRotations[0] = PrefabRotation.valueOf(name);
            }
            catch (Throwable e) {
                throw new Error("Could not find rotation \"" + name + "\". Allowed: " + Arrays.toString((Object[])PrefabRotation.VALUES));
            }
        } else {
            throw new IllegalArgumentException("rotations is not an array nor a string, other types are not supported! Given: " + String.valueOf(element));
        }
        return prefabRotations;
    }

    public static interface Constants {
        public static final String KEY_GRID_GENERATOR = "GridGenerator";
        public static final String KEY_PARENT = "Parent";
        public static final String KEY_HEIGHT_THRESHOLD = "HeightThreshold";
        public static final String KEY_NOISE_MASK = "NoiseMask";
        public static final String KEY_MASK = "Mask";
        public static final String KEY_ROTATIONS = "Rotations";
        public static final String KEY_DISPLACEMENT = "Displacement";
        public static final String KEY_FIT_HEIGHTMAP = "FitHeightmap";
        public static final String KEY_ON_WATER = "OnWater";
        public static final String KEY_DEEP_SEARCH = "DeepSearch";
        public static final String KEY_SUBMERGE = "Submerge";
        public static final String KEY_MAX_SIZE = "MaxSize";
        public static final String KEY_EXCLUSION_RADIUS = "ExclusionRadius";
        public static final String KEY_CATEGORY = "Category";
        public static final String ERROR_NO_GRID_GENERATOR = "Could not find point generator to place prefabs at! Keyword: GridGenerator";
        public static final String ERROR_DEEP_SEARCH = "DeepSearch is enabled but HeightThreshold is not set!";
        public static final Boolean DEFAULT_SUBMERGE = Boolean.FALSE;
        public static final Integer DEFAULT_MAX_SIZE = 5;
        public static final Integer DEFAULT_EXCLUSION_RADIUS = 0;
    }
}

