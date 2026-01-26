/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.biome;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.condition.ConstantIntCondition;
import com.hypixel.hytale.procedurallib.condition.IIntCondition;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.loader.context.BiomeFileContext;
import com.hypixel.hytale.server.worldgen.loader.context.FileContext;
import com.hypixel.hytale.server.worldgen.loader.context.FileLoadingContext;
import com.hypixel.hytale.server.worldgen.loader.context.ZoneFileContext;
import com.hypixel.hytale.server.worldgen.loader.util.FileMaskCache;
import com.hypixel.hytale.server.worldgen.util.condition.IntConditionBuilder;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.nio.file.Path;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BiomeMaskJsonLoader
extends JsonLoader<SeedStringResource, IIntCondition> {
    private final ZoneFileContext zoneContext;
    @Nullable
    private String fileName = null;

    public BiomeMaskJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json, String maskName, ZoneFileContext zoneContext) {
        super(seed.append(".BiomeMask-" + maskName), dataFolder, json);
        this.zoneContext = zoneContext;
    }

    @Override
    @Nullable
    public IIntCondition load() {
        IIntCondition mask;
        FileMaskCache<IIntCondition> biomeMaskRegistry = ((SeedStringResource)this.seed.get()).getBiomeMaskRegistry();
        if (this.fileName != null && (mask = biomeMaskRegistry.getIfPresentFileMask(this.fileName)) != null) {
            return mask;
        }
        mask = this.loadMask();
        if (this.fileName != null) {
            biomeMaskRegistry.putFileMask(this.fileName, mask);
        }
        return mask;
    }

    protected IIntCondition loadMask() {
        IIntCondition mask = ConstantIntCondition.DEFAULT_TRUE;
        if (this.json.isJsonArray()) {
            IntConditionBuilder builder = new IntConditionBuilder(IntOpenHashSet::new, -1);
            JsonArray array = this.json.getAsJsonArray();
            for (int i = 0; i < array.size(); ++i) {
                JsonElement element = array.get(i);
                String rule = element.getAsString();
                this.parseRule(rule, builder);
            }
            mask = builder.buildOrDefault(ConstantIntCondition.DEFAULT_TRUE);
        }
        return mask;
    }

    protected void parseRule(@Nonnull String rule, @Nonnull IntConditionBuilder builder) {
        boolean result;
        int zoneMarker = rule.indexOf(46);
        int typeMarker = rule.indexOf(35);
        ZoneFileContext zone = BiomeMaskJsonLoader.parseZone(rule, zoneMarker, this.zoneContext);
        String biomeName = BiomeMaskJsonLoader.parseBiomeName(rule, zoneMarker, typeMarker);
        BiomeFileContext.Type biomeType = BiomeMaskJsonLoader.parseBiomeType(rule, typeMarker + 1);
        if (biomeType == null) {
            result = BiomeMaskJsonLoader.collectBiomes(zone.getTileBiomes(), biomeName, builder);
            result |= BiomeMaskJsonLoader.collectBiomes(zone.getCustomBiomes(), biomeName, builder);
        } else {
            result = BiomeMaskJsonLoader.collectBiomes(zone.getBiomes(biomeType), biomeName, builder);
        }
        if (!result) {
            throw new Error(String.format("Failed to parse BiomeMask rule '%s'. Unable to find a %s called %s in %s", rule, BiomeMaskJsonLoader.getDisplayName(biomeType), biomeName, zone.getName()));
        }
    }

    @Override
    protected JsonElement loadFileConstructor(String filePath) {
        this.fileName = filePath;
        return ((SeedStringResource)this.seed.get()).getBiomeMaskRegistry().cachedFile(filePath, x$0 -> super.loadFileConstructor((String)x$0));
    }

    private static boolean collectBiomes(@Nonnull FileContext.Registry<BiomeFileContext> registry, @Nonnull String biomeName, @Nonnull IntConditionBuilder builder) {
        if (biomeName.equals("*")) {
            for (Map.Entry<String, BiomeFileContext> entry : registry) {
                builder.add(entry.getValue().getId());
            }
            return true;
        }
        if (registry.contains(biomeName)) {
            BiomeFileContext biome = registry.get(biomeName);
            builder.add(biome.getId());
            return true;
        }
        return false;
    }

    @Nonnull
    private static ZoneFileContext parseZone(@Nonnull String rule, int marker, @Nonnull ZoneFileContext context) {
        if (marker <= 0) {
            return context;
        }
        String zoneName = rule.substring(0, marker);
        return ((FileLoadingContext)context.getParentContext()).getZones().get(zoneName);
    }

    @Nullable
    private static BiomeFileContext.Type parseBiomeType(@Nonnull String rule, int marker) {
        if (marker <= 0) {
            return null;
        }
        String typeName = rule.substring(marker);
        return BiomeFileContext.Type.valueOf(typeName);
    }

    @Nonnull
    private static String parseBiomeName(@Nonnull String rule, int zoneMarker, int typeMarker) {
        int nameEnd;
        int nameStart = zoneMarker + 1;
        int n = nameEnd = typeMarker > zoneMarker ? typeMarker : rule.length();
        if (nameStart == nameEnd) {
            return "*";
        }
        return rule.substring(nameStart, nameEnd);
    }

    @Nonnull
    private static String getDisplayName(@Nullable BiomeFileContext.Type type) {
        return type == null ? "Biome" : type.getDisplayName();
    }

    public static interface Constants {
        public static final char ZONE_MARKER = '.';
        public static final char TYPE_MARKER = '#';
        public static final int NULL_BIOME_ID = -1;
        public static final String WILDCARD_BIOME_NAME = "*";
        public static final String BIOME_TYPE_ANY_DISPLAY_NAME = "Biome";
        public static final String ERROR_PARSE_RULE = "Failed to parse BiomeMask rule '%s'. Unable to find a %s called %s in %s";
    }
}

