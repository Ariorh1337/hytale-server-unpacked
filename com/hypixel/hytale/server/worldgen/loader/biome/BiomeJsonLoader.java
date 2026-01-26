/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.biome;

import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.condition.IHeightThresholdInterpreter;
import com.hypixel.hytale.procedurallib.json.HeightThresholdInterpreterJsonLoader;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.NoisePropertyJsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.property.NoiseProperty;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.biome.Biome;
import com.hypixel.hytale.server.worldgen.biome.BiomeInterpolation;
import com.hypixel.hytale.server.worldgen.container.CoverContainer;
import com.hypixel.hytale.server.worldgen.container.EnvironmentContainer;
import com.hypixel.hytale.server.worldgen.container.FadeContainer;
import com.hypixel.hytale.server.worldgen.container.LayerContainer;
import com.hypixel.hytale.server.worldgen.container.PrefabContainer;
import com.hypixel.hytale.server.worldgen.container.TintContainer;
import com.hypixel.hytale.server.worldgen.container.WaterContainer;
import com.hypixel.hytale.server.worldgen.loader.biome.BiomeInterpolationJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.container.CoverContainerJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.container.EnvironmentContainerJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.container.FadeContainerJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.container.LayerContainerJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.container.PrefabContainerJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.container.TintContainerJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.container.WaterContainerJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.context.BiomeFileContext;
import com.hypixel.hytale.server.worldgen.loader.context.FileLoadingContext;
import com.hypixel.hytale.server.worldgen.loader.context.ZoneFileContext;
import java.nio.file.Path;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BiomeJsonLoader
extends JsonLoader<SeedStringResource, Biome> {
    private static final Pattern COLOR_PREFIX_PATTERN = Pattern.compile("0x|#");
    protected final BiomeFileContext biomeContext;
    protected final FileLoadingContext fileContext;

    public BiomeJsonLoader(SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json, BiomeFileContext biomeContext) {
        super(seed, dataFolder, json);
        this.biomeContext = biomeContext;
        this.fileContext = (FileLoadingContext)((ZoneFileContext)biomeContext.getParentContext()).getParentContext();
    }

    @Nonnull
    protected IHeightThresholdInterpreter loadTerrainHeightThreshold() {
        try {
            return new HeightThresholdInterpreterJsonLoader(this.seed, this.dataFolder, this.get("TerrainHeightThreshold"), 320).load();
        }
        catch (Throwable e) {
            throw new Error("Failed to load height threshold container", e);
        }
    }

    @Nonnull
    protected CoverContainer loadCoverContainer() {
        try {
            return new CoverContainerJsonLoader(this.seed, this.dataFolder, this.get("Covers")).load();
        }
        catch (Throwable e) {
            throw new Error("Failed to load cover container", e);
        }
    }

    @Nonnull
    protected FadeContainer loadFadeContainer() {
        try {
            return new FadeContainerJsonLoader(this.seed, this.dataFolder, this.get("Fade")).load();
        }
        catch (Throwable e) {
            throw new Error("Failed to load fade container", e);
        }
    }

    @Nonnull
    protected LayerContainer loadLayerContainers() {
        try {
            if (!this.has("Layers")) {
                throw new IllegalArgumentException("LayerContainer is not defined in Biome!");
            }
            return new LayerContainerJsonLoader(this.seed, this.dataFolder, this.get("Layers")).load();
        }
        catch (Throwable e) {
            throw new Error("Failed to load layer container", e);
        }
    }

    @Nullable
    protected PrefabContainer loadPrefabContainer() {
        try {
            PrefabContainer prefabContainer = null;
            if (this.has("Prefabs")) {
                prefabContainer = new PrefabContainerJsonLoader(this.seed, this.dataFolder, this.get("Prefabs"), this.fileContext).load();
            }
            return prefabContainer;
        }
        catch (Throwable e) {
            throw new Error("Failed to load prefab container", e);
        }
    }

    @Nonnull
    protected TintContainer loadTintContainer() {
        try {
            return new TintContainerJsonLoader(this.seed, this.dataFolder, this.get("Tint")).load();
        }
        catch (Throwable e) {
            throw new Error("Failed to load tint container", e);
        }
    }

    @Nonnull
    protected EnvironmentContainer loadEnvironmentContainer() {
        try {
            return new EnvironmentContainerJsonLoader(this.seed, this.dataFolder, this.get("Environment")).load();
        }
        catch (Throwable e) {
            throw new Error("Failed to load environment container", e);
        }
    }

    @Nonnull
    protected WaterContainer loadWaterContainer() {
        try {
            return new WaterContainerJsonLoader(this.seed, this.dataFolder, this.get("Water")).load();
        }
        catch (Throwable e) {
            throw new Error("Failed to load water container", e);
        }
    }

    @Nullable
    protected NoiseProperty loadHeightmapNoise() {
        NoiseProperty heightmapNoise = null;
        if (this.has("HeightmapNoise")) {
            heightmapNoise = new NoisePropertyJsonLoader(this.seed, this.dataFolder, this.get("HeightmapNoise")).load();
        }
        return heightmapNoise;
    }

    protected int loadColor() {
        int rgb = 0xFF0000;
        if (this.has("MapColor")) {
            rgb = BiomeJsonLoader.getColor(this.get("MapColor").getAsString());
        }
        return rgb;
    }

    @Nullable
    protected BiomeInterpolation loadInterpolation() {
        BiomeInterpolation interpolation = BiomeInterpolation.DEFAULT;
        if (this.has("Interpolation")) {
            interpolation = new BiomeInterpolationJsonLoader(this.seed, this.dataFolder, this.get("Interpolation"), (ZoneFileContext)this.biomeContext.getParentContext()).load();
        }
        return interpolation;
    }

    protected static int getColor(@Nonnull String string) {
        String tintString = COLOR_PREFIX_PATTERN.matcher(string).replaceFirst("");
        return Integer.parseInt(tintString, 16);
    }

    public static interface Constants {
        public static final String KEY_TERRAIN_HEIGHT_THRESHOLD = "TerrainHeightThreshold";
        public static final String KEY_COVERS = "Covers";
        public static final String KEY_LAYERS = "Layers";
        public static final String KEY_PREFABS = "Prefabs";
        public static final String KEY_FADE = "Fade";
        public static final String KEY_TINT = "Tint";
        public static final String KEY_ENVIRONMENT = "Environment";
        public static final String KEY_WATER = "Water";
        public static final String KEY_HEIGHTMAP_NOISE = "HeightmapNoise";
        public static final String KEY_INTERPOLATION = "Interpolation";
        public static final String KEY_MAP_COLOR = "MapColor";
        public static final String ERROR_NO_LAYER_CONTAINER = "LayerContainer is not defined in Biome!";
        public static final String ERROR_COVER_CONTAINER = "Failed to load cover container";
        public static final String ERROR_HEIGHT_CONTAINER = "Failed to load height threshold container";
        public static final String ERROR_LAYER_CONTAINER = "Failed to load layer container";
        public static final String ERROR_WATER_CONTAINER = "Failed to load water container";
        public static final String ERROR_TINT_CONTAINER = "Failed to load tint container";
        public static final String ERROR_FADE_CONTAINER = "Failed to load fade container";
        public static final String ERROR_ENVIRONMENT_CONTAINER = "Failed to load environment container";
        public static final String ERROR_PREFAB_CONTAINER = "Failed to load prefab container";
    }
}

