/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.common.map.IWeightedMap;
import com.hypixel.hytale.common.map.WeightedMap;
import com.hypixel.hytale.common.semver.Semver;
import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.common.util.PathUtil;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.FastRandom;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.procedurallib.file.FileIO;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.Loader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.WorldGenConfig;
import com.hypixel.hytale.server.worldgen.chunk.ChunkGenerator;
import com.hypixel.hytale.server.worldgen.chunk.MaskProvider;
import com.hypixel.hytale.server.worldgen.loader.AssetFileSystem;
import com.hypixel.hytale.server.worldgen.loader.MaskProviderJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.ZonesJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.climate.ClimateMaskJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.context.FileContextLoader;
import com.hypixel.hytale.server.worldgen.loader.context.FileLoadingContext;
import com.hypixel.hytale.server.worldgen.loader.zone.ZonePatternProviderJsonLoader;
import com.hypixel.hytale.server.worldgen.prefab.PrefabStoreRoot;
import com.hypixel.hytale.server.worldgen.util.LogUtil;
import com.hypixel.hytale.server.worldgen.zone.Zone;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;

public class ChunkGeneratorJsonLoader
extends Loader<SeedStringResource, ChunkGenerator> {
    @Nonnull
    private final WorldGenConfig config;

    public ChunkGeneratorJsonLoader(@Nonnull SeedString<SeedStringResource> seed, @Nonnull WorldGenConfig config) {
        super(seed, config.path());
        this.config = config;
    }

    @Override
    @Nonnull
    public ChunkGenerator load() {
        Path worldFile = this.dataFolder.resolve("World.json").toAbsolutePath();
        if (!Files.exists(worldFile, new LinkOption[0])) {
            throw new IllegalArgumentException(String.valueOf(worldFile));
        }
        if (!Files.isReadable(worldFile)) {
            throw new IllegalArgumentException(String.valueOf(worldFile));
        }
        JsonObject worldJson = this.loadWorldJson(worldFile);
        Path overrideDataFolder = this.loadOverrideDataFolderPath(worldJson, this.config.path());
        WorldGenConfig config = this.config.withOverride(overrideDataFolder);
        try (AssetFileSystem fs = FileIO.openFileIOSystem(new AssetFileSystem(config));){
            ChunkGeneratorJsonLoader.logAssetPacks(fs.packs());
            Vector2i worldSize = this.loadWorldSize(worldJson);
            Vector2i worldOffset = this.loadWorldOffset(worldJson);
            MaskProvider maskProvider = this.loadMaskProvider(worldJson, worldSize, worldOffset);
            PrefabStoreRoot prefabStore = this.loadPrefabStore(worldJson);
            ((SeedStringResource)this.seed.get()).setPrefabConfig(config, prefabStore);
            ZonePatternProviderJsonLoader loader = this.loadZonePatternGenerator(maskProvider);
            FileLoadingContext loadingContext = new FileContextLoader(overrideDataFolder, loader.loadZoneRequirement()).load();
            Zone[] zones = new ZonesJsonLoader(this.seed, overrideDataFolder, loadingContext).load();
            loader.setZones(zones);
            ChunkGenerator chunkGenerator = new ChunkGenerator(loader.load(), overrideDataFolder);
            return chunkGenerator;
        }
    }

    @Nonnull
    private Path loadOverrideDataFolderPath(@Nonnull JsonObject worldJson, @Nonnull Path dataFolder) {
        if (worldJson.has("OverrideDataFolder")) {
            Path parent;
            Path overrideFolder = dataFolder.resolve(worldJson.get("OverrideDataFolder").getAsString()).normalize();
            if (!overrideFolder.startsWith(parent = dataFolder.getParent()) || !Files.exists(overrideFolder, new LinkOption[0])) {
                throw new Error(String.format("Override folder '%s' must exist within: '%s'", overrideFolder.getFileName(), parent));
            }
            return overrideFolder;
        }
        return dataFolder;
    }

    @Nonnull
    protected JsonObject loadWorldJson(@Nonnull Path file) {
        try {
            return FileIO.load(file, JsonLoader.JSON_OBJ_LOADER);
        }
        catch (Throwable e) {
            throw new Error(String.format("Could not read JSON configuration for world. File: %s", file), e);
        }
    }

    @Nonnull
    protected Vector2i loadWorldSize(@Nonnull JsonObject worldJson) {
        int width = 0;
        int height = 0;
        if (worldJson.has("Width")) {
            width = worldJson.get("Width").getAsInt();
        }
        if (worldJson.has("Height")) {
            height = worldJson.get("Height").getAsInt();
        }
        return new Vector2i(width, height);
    }

    @Nonnull
    protected Vector2i loadWorldOffset(@Nonnull JsonObject worldJson) {
        int offsetX = 0;
        int offsetY = 0;
        if (worldJson.has("OffsetX")) {
            offsetX = worldJson.get("OffsetX").getAsInt();
        }
        if (worldJson.has("OffsetY")) {
            offsetY = worldJson.get("OffsetY").getAsInt();
        }
        return new Vector2i(offsetX, offsetY);
    }

    @Nonnull
    protected MaskProvider loadMaskProvider(@Nonnull JsonObject worldJson, Vector2i worldSize, Vector2i worldOffset) {
        WeightedMap.Builder<String> builder = WeightedMap.builder(ArrayUtil.EMPTY_STRING_ARRAY);
        JsonElement masks = worldJson.get("Masks");
        if (masks == null) {
            builder.put("Mask.png", 1.0);
        } else if (masks.isJsonPrimitive()) {
            builder.put(masks.getAsString(), 1.0);
        } else if (masks.isJsonArray()) {
            JsonArray arr = masks.getAsJsonArray();
            if (arr.isEmpty()) {
                builder.put("Mask.png", 1.0);
            } else {
                for (int i = 0; i < arr.size(); ++i) {
                    builder.put(arr.get(i).getAsString(), 1.0);
                }
            }
        } else if (masks.isJsonObject()) {
            JsonObject obj = masks.getAsJsonObject();
            if (obj.size() == 0) {
                builder.put("Mask.png", 1.0);
            } else {
                for (String key : obj.keySet()) {
                    builder.put(key, obj.get(key).getAsDouble());
                }
            }
        }
        IWeightedMap<String> weightedMap = builder.build();
        String maskName = weightedMap.get(new FastRandom(this.seed.hashCode()));
        Path maskFile = PathUtil.resolvePathWithinDir(this.dataFolder, maskName);
        if (maskFile == null) {
            throw new Error("Invalid mask file path: " + maskName);
        }
        if (maskFile.getFileName().toString().endsWith("Mask.json")) {
            return new ClimateMaskJsonLoader(this.seed, this.dataFolder, maskFile).load();
        }
        return new MaskProviderJsonLoader(this.seed, this.dataFolder, worldJson.get("Randomizer"), maskFile, worldSize, worldOffset).load();
    }

    @Nonnull
    protected PrefabStoreRoot loadPrefabStore(@Nonnull JsonObject worldJson) {
        if (worldJson.has("PrefabStore")) {
            JsonElement storeJson = worldJson.get("PrefabStore");
            if (!storeJson.isJsonPrimitive() || !storeJson.getAsJsonPrimitive().isString()) {
                throw new Error("Expected 'PrefabStore' to be a string");
            }
            String store = storeJson.getAsString();
            try {
                return PrefabStoreRoot.valueOf(store);
            }
            catch (IllegalArgumentException e) {
                throw new Error("Invalid PrefabStore name: " + store, e);
            }
        }
        return PrefabStoreRoot.DEFAULT;
    }

    @Nonnull
    protected ZonePatternProviderJsonLoader loadZonePatternGenerator(MaskProvider maskProvider) {
        Path zoneFile = this.dataFolder.resolve("Zones.json");
        try {
            JsonObject zoneJson = FileIO.load(zoneFile, JsonLoader.JSON_OBJ_LOADER);
            return new ZonePatternProviderJsonLoader(this.seed, this.dataFolder, zoneJson, maskProvider);
        }
        catch (Throwable e) {
            throw new Error(String.format("Failed to read zone configuration file! File: %s", zoneFile.toString()), e);
        }
    }

    protected static void logAssetPacks(@Nonnull List<AssetPack> packs) {
        HytaleLogger.Api logger = (HytaleLogger.Api)LogUtil.getLogger().atInfo();
        Semver unversioned = new Semver(0L, 0L, 0L);
        logger.log("Loading world-gen with the following asset-packs (highest priority first):");
        for (int i = 0; i < packs.size(); ++i) {
            AssetPack pack = packs.get(i);
            String name = pack.getName();
            Semver version = Objects.requireNonNullElse(pack.getManifest().getVersion(), unversioned);
            Path location = pack.getPackLocation();
            logger.log("- [%3d] %s:%s - [%s]", i, name, version, location);
        }
    }

    public static interface Constants {
        public static final String KEY_WIDTH = "Width";
        public static final String KEY_HEIGHT = "Height";
        public static final String KEY_OFFSET_X = "OffsetX";
        public static final String KEY_OFFSET_Y = "OffsetY";
        public static final String KEY_RANDOMIZER = "Randomizer";
        public static final String KEY_MASKS = "Masks";
        public static final String KEY_PREFAB_STORE = "PrefabStore";
        public static final String OVERRIDE_DATA_FOLDER = "OverrideDataFolder";
        public static final String FILE_WORLD_JSON = "World.json";
        public static final String FILE_ZONES_JSON = "Zones.json";
        public static final String FILE_MASK_JSON = "Mask.json";
        public static final String FILE_MASK_PNG = "Mask.png";
        public static final String ERROR_WORLD_FILE_EXIST = "World configuration file does NOT exist! File not found: %s";
        public static final String ERROR_WORLD_FILE_READ = "World configuration file is NOT readable! File: %s";
        public static final String ERROR_WORLD_JSON_CORRUPT = "Could not read JSON configuration for world. File: %s";
        public static final String ERROR_ZONE_FILE = "Failed to read zone configuration file! File: %s";
    }
}

