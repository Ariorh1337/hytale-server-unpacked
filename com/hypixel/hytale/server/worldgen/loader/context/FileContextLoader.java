/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.context;

import com.google.gson.JsonParser;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.worldgen.loader.context.BiomeFileContext;
import com.hypixel.hytale.server.worldgen.loader.context.FileLoadingContext;
import com.hypixel.hytale.server.worldgen.loader.context.ZoneFileContext;
import com.hypixel.hytale.server.worldgen.prefab.PrefabCategory;
import com.hypixel.hytale.server.worldgen.util.LogUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.logging.Level;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public class FileContextLoader {
    private static final Comparator<Path> ZONES_ORDER = Comparator.comparing(Path::getFileName);
    private static final Comparator<Path> BIOME_ORDER = Comparator.comparing(BiomeFileContext::getBiomeType).thenComparing(Path::getFileName);
    private static final BiPredicate<Path, BasicFileAttributes> ZONE_FILTER = (path, attributes) -> Files.isDirectory(path, new LinkOption[0]);
    private static final BiPredicate<Path, BasicFileAttributes> BIOME_FILTER = (path, attributes) -> FileContextLoader.isValidBiomeFile(path);
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
        try (Stream<Path> stream = Files.find(zonesFolder, 1, ZONE_FILTER, new FileVisitOption[0]);){
            stream.sorted(ZONES_ORDER).forEach(path -> {
                String zoneName = path.getFileName().toString();
                if (zoneName.startsWith("!")) {
                    LogUtil.getLogger().at(Level.INFO).log("Zone \"%s\" is disabled. Remove \"!\" from folder name to enable it.", zoneName);
                    return;
                }
                if (!this.zoneRequirement.contains(zoneName)) {
                    return;
                }
                ZoneFileContext zone = FileContextLoader.loadZoneContext(zoneName, path, context);
                context.getZones().register(zoneName, zone);
            });
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
        Path path = folder.resolve("PrefabCategories.json");
        if (!Files.exists(path, new LinkOption[0])) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(path);){
            PrefabCategory.parse(JsonParser.parseReader(reader), context.getPrefabCategories()::register);
        }
        catch (IOException e) {
            throw new Error("Failed to open Categories.json", e);
        }
    }

    @Nonnull
    protected static ZoneFileContext loadZoneContext(String name, @Nonnull Path folder, @Nonnull FileLoadingContext context) {
        ZoneFileContext zoneFileContext;
        block8: {
            Stream<Path> stream = Files.find(folder, 1, BIOME_FILTER, new FileVisitOption[0]);
            try {
                ZoneFileContext zone = context.createZone(name, folder);
                stream.sorted(BIOME_ORDER).forEach(path -> {
                    BiomeFileContext.Type type = BiomeFileContext.getBiomeType(path);
                    String biomeName = FileContextLoader.parseName(path, type);
                    BiomeFileContext biome = zone.createBiome(biomeName, (Path)path, type);
                    zone.getBiomes(type).register(biomeName, biome);
                });
                zoneFileContext = zone;
                if (stream == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (stream != null) {
                        try {
                            stream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    throw new Error(String.format("Failed to list files in: %s", folder), e);
                }
            }
            stream.close();
        }
        return zoneFileContext;
    }

    protected static int compareBiomePaths(@Nonnull Path a, @Nonnull Path b) {
        BiomeFileContext.Type typeB;
        BiomeFileContext.Type typeA = BiomeFileContext.getBiomeType(a);
        int result = typeA.compareTo(typeB = BiomeFileContext.getBiomeType(b));
        if (result != 0) {
            return result;
        }
        return a.getFileName().compareTo(b.getFileName());
    }

    protected static boolean isValidBiomeFile(@Nonnull Path path) {
        if (Files.isDirectory(path, new LinkOption[0])) {
            return false;
        }
        String filename = path.getFileName().toString();
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
    private static String parseName(@Nonnull Path path, @Nonnull BiomeFileContext.Type type) {
        String filename = path.getFileName().toString();
        int start = type.getPrefix().length();
        int end = filename.length() - type.getSuffix().length();
        return filename.substring(start, end);
    }

    public static interface Constants {
        public static final int ZONE_SEARCH_DEPTH = 1;
        public static final int BIOME_SEARCH_DEPTH = 1;
        public static final String IDENTIFIER_DISABLE_ZONE = "!";
        public static final String INFO_ZONE_IS_DISABLED = "Zone \"%s\" is disabled. Remove \"!\" from folder name to enable it.";
        public static final String ERROR_LIST_FILES = "Failed to list files in: %s";
        public static final String ERROR_ZONE_VALIDATION = "Failed to validate zones!";
    }
}

