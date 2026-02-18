/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.context;

import com.google.gson.JsonObject;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.procedurallib.file.AssetPath;
import com.hypixel.hytale.procedurallib.file.FileIO;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.server.worldgen.loader.context.BiomeFileContext;
import com.hypixel.hytale.server.worldgen.loader.context.FileLoadingContext;
import com.hypixel.hytale.server.worldgen.loader.context.ZoneFileContext;
import com.hypixel.hytale.server.worldgen.prefab.PrefabCategory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class FileContextLoader {
    private static final Comparator<AssetPath> ZONES_ORDER = AssetPath.COMPARATOR;
    private static final Comparator<AssetPath> BIOME_ORDER = Comparator.comparing(BiomeFileContext::getBiomeType).thenComparing(AssetPath.COMPARATOR);
    private static final UnaryOperator<AssetPath> DISABLED_FILE = FileContextLoader::getDisabledFilePath;
    private static final Predicate<AssetPath> ZONE_FILE_MATCHER = FileContextLoader::isValidZoneFile;
    private static final Predicate<AssetPath> BIOME_FILE_MATCHER = FileContextLoader::isValidBiomeFile;
    private final Path dataFolder;
    private final Set<String> zoneRequirement;

    public FileContextLoader(Path dataFolder, Set<String> zoneRequirement) {
        this.dataFolder = dataFolder;
        this.zoneRequirement = zoneRequirement;
    }

    @Nonnull
    public FileLoadingContext load() {
        FileLoadingContext context = new FileLoadingContext(this.dataFolder);
        Path zonesFolder = this.dataFolder.resolve("Zones");
        try {
            List<AssetPath> files = FileIO.list(zonesFolder, ZONE_FILE_MATCHER, DISABLED_FILE);
            files.sort(ZONES_ORDER);
            for (AssetPath path : files) {
                String zoneName = path.getFileName();
                if (!this.zoneRequirement.contains(zoneName)) continue;
                ZoneFileContext zone = FileContextLoader.loadZoneContext(zoneName, path.filepath(), context);
                context.getZones().register(zoneName, zone);
            }
        }
        catch (IOException e) {
            ((HytaleLogger.Api)HytaleLogger.getLogger().at(Level.SEVERE).withCause(e)).log("Failed to load zones");
        }
        try {
            FileContextLoader.validateZones(context, this.zoneRequirement);
        }
        catch (Error e) {
            throw new Error("Failed to validate zones!", e);
        }
        FileContextLoader.loadPrefabCategories(this.dataFolder, context);
        return context;
    }

    protected static void loadPrefabCategories(@Nonnull Path folder, @Nonnull FileLoadingContext context) {
        AssetPath path = FileIO.resolve(folder.resolve("PrefabCategories.json"));
        if (!FileIO.exists(path)) {
            return;
        }
        try {
            JsonObject json = FileIO.load(path, JsonLoader.JSON_OBJ_LOADER);
            PrefabCategory.parse(json, context.getPrefabCategories()::register);
        }
        catch (IOException e) {
            throw new Error("Failed to open Categories.json", e);
        }
    }

    @Nonnull
    protected static ZoneFileContext loadZoneContext(String name, @Nonnull Path folder, @Nonnull FileLoadingContext context) {
        try {
            ZoneFileContext zone = context.createZone(name, folder);
            List<AssetPath> files = FileIO.list(folder, BIOME_FILE_MATCHER, DISABLED_FILE);
            files.sort(BIOME_ORDER);
            for (AssetPath path : files) {
                BiomeFileContext.Type type = BiomeFileContext.getBiomeType(path);
                String biomeName = FileContextLoader.parseName(path, type);
                BiomeFileContext biome = zone.createBiome(biomeName, path.filepath(), type);
                zone.getBiomes(type).register(biomeName, biome);
            }
            return zone;
        }
        catch (IOException e) {
            throw new Error(String.format("Failed to list files in: %s", folder), e);
        }
    }

    @Nonnull
    protected static AssetPath getDisabledFilePath(@Nonnull AssetPath path) {
        String filename = path.getFileName();
        if (filename.startsWith("!")) {
            return path.rename(filename.substring(1));
        }
        return path;
    }

    protected static boolean isValidZoneFile(@Nonnull AssetPath path) {
        return Files.isDirectory(path.filepath(), new LinkOption[0]) && Files.exists(path.filepath().resolve("Zone.json"), new LinkOption[0]);
    }

    protected static boolean isValidBiomeFile(@Nonnull AssetPath path) {
        if (Files.isDirectory(path.filepath(), new LinkOption[0])) {
            return false;
        }
        String filename = path.getFileName();
        for (BiomeFileContext.Type type : BiomeFileContext.Type.values()) {
            if (!filename.endsWith(type.getSuffix()) || !filename.startsWith(type.getPrefix())) continue;
            return true;
        }
        return false;
    }

    protected static void validateZones(@Nonnull FileLoadingContext context, @Nonnull Set<String> zoneRequirement) throws Error {
        for (String key : zoneRequirement) {
            context.getZones().get(key);
        }
    }

    @Nonnull
    private static String parseName(@Nonnull AssetPath path, @Nonnull BiomeFileContext.Type type) {
        String filename = path.getFileName();
        int start = type.getPrefix().length();
        int end = filename.length() - type.getSuffix().length();
        return filename.substring(start, end);
    }

    public static interface Constants {
        public static final int ZONE_SEARCH_DEPTH = 1;
        public static final int BIOME_SEARCH_DEPTH = 1;
        public static final String IDENTIFIER_DISABLE = "!";
        public static final String INFO_ZONE_IS_DISABLED = "Zone \"%s\" is disabled. Remove \"!\" from folder name to enable it.";
        public static final String ERROR_LIST_FILES = "Failed to list files in: %s";
        public static final String ERROR_ZONE_VALIDATION = "Failed to validate zones!";
    }
}

