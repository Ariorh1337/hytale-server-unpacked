/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.cave;

import com.google.gson.JsonElement;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.procedurallib.condition.DefaultCoordinateCondition;
import com.hypixel.hytale.procedurallib.condition.HeightThresholdCoordinateCondition;
import com.hypixel.hytale.procedurallib.condition.ICoordinateCondition;
import com.hypixel.hytale.procedurallib.condition.IHeightThresholdInterpreter;
import com.hypixel.hytale.procedurallib.json.DoubleRangeJsonLoader;
import com.hypixel.hytale.procedurallib.json.FloatRangeJsonLoader;
import com.hypixel.hytale.procedurallib.json.HeightThresholdInterpreterJsonLoader;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.NoiseMaskConditionJsonLoader;
import com.hypixel.hytale.procedurallib.json.NoisePropertyJsonLoader;
import com.hypixel.hytale.procedurallib.json.PointGeneratorJsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.logic.point.IPointGenerator;
import com.hypixel.hytale.procedurallib.property.NoiseProperty;
import com.hypixel.hytale.procedurallib.supplier.IDoubleRange;
import com.hypixel.hytale.procedurallib.supplier.IFloatRange;
import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.cave.CaveBiomeMaskFlags;
import com.hypixel.hytale.server.worldgen.cave.CaveNodeType;
import com.hypixel.hytale.server.worldgen.cave.CaveType;
import com.hypixel.hytale.server.worldgen.loader.cave.CaveBiomeMaskJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.cave.CaveNodeTypeStorage;
import com.hypixel.hytale.server.worldgen.loader.cave.FluidLevelJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.context.ZoneFileContext;
import com.hypixel.hytale.server.worldgen.loader.prefab.BlockPlacementMaskJsonLoader;
import com.hypixel.hytale.server.worldgen.util.ConstantNoiseProperty;
import com.hypixel.hytale.server.worldgen.util.condition.BlockMaskCondition;
import com.hypixel.hytale.server.worldgen.util.condition.flag.Int2FlagsCondition;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CaveTypeJsonLoader
extends JsonLoader<SeedStringResource, CaveType> {
    protected final Path caveFolder;
    protected final String name;
    protected final ZoneFileContext zoneContext;

    public CaveTypeJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json, Path caveFolder, String name, ZoneFileContext zoneContext) {
        super(seed.append(".CaveType"), dataFolder, json);
        this.caveFolder = caveFolder;
        this.name = name;
        this.zoneContext = zoneContext;
    }

    @Override
    @Nonnull
    public CaveType load() {
        IPointGenerator pointGenerator = this.loadEntryPointGenerator();
        return new CaveType(this.name, this.loadEntryNodeType(), this.loadYaw(), this.loadPitch(), this.loadDepth(), this.loadHeightFactors(), pointGenerator, this.loadBiomeMask(), this.loadBlockMask(), this.loadMapCondition(), this.loadHeightCondition(), this.loadFixedEntryHeight(), this.loadFixedEntryHeightNoise(), this.loadFluidLevel(), this.loadEnvironment(), this.loadSurfaceLimited(), this.loadSubmerge(), this.loadMaximumSize(pointGenerator));
    }

    @Nonnull
    protected IFloatRange loadYaw() {
        return new FloatRangeJsonLoader(this.seed, this.dataFolder, this.get("Yaw"), -180.0f, 180.0f, deg -> deg * ((float)Math.PI / 180)).load();
    }

    @Nonnull
    protected IFloatRange loadPitch() {
        return new FloatRangeJsonLoader(this.seed, this.dataFolder, this.get("Pitch"), -15.0f, deg -> deg * ((float)Math.PI / 180)).load();
    }

    @Nonnull
    protected IFloatRange loadDepth() {
        return new FloatRangeJsonLoader(this.seed, this.dataFolder, this.get("Depth"), 80.0f).load();
    }

    @Nullable
    protected IHeightThresholdInterpreter loadHeightFactors() {
        return new HeightThresholdInterpreterJsonLoader(this.seed, this.dataFolder, this.get("HeightRadiusFactor"), 320).load();
    }

    @Nonnull
    protected CaveNodeType loadEntryNodeType() {
        JsonElement entry = this.get("Entry");
        if (entry == null) {
            throw new IllegalArgumentException("\"Entry\" is not defined. Define an entry node type");
        }
        CaveNodeTypeStorage caveNodeTypeStorage = new CaveNodeTypeStorage(this.seed, this.dataFolder, this.caveFolder, this.zoneContext);
        if (entry.isJsonObject()) {
            String entryNodeTypeString = ((SeedStringResource)this.seed.get()).getUniqueName("CaveType#");
            return caveNodeTypeStorage.loadCaveNodeType(entryNodeTypeString, entry.getAsJsonObject());
        }
        if (entry.isJsonPrimitive() && entry.getAsJsonPrimitive().isString()) {
            String entryNodeTypeString = entry.getAsString();
            return caveNodeTypeStorage.loadCaveNodeType(entryNodeTypeString);
        }
        throw CaveTypeJsonLoader.error("Invalid entry node type definition! Expected String or JsonObject: " + String.valueOf(entry), new Object[0]);
    }

    @Nonnull
    protected ICoordinateCondition loadHeightCondition() {
        ICoordinateCondition heightCondition = DefaultCoordinateCondition.DEFAULT_TRUE;
        if (this.has("HeightThreshold")) {
            IHeightThresholdInterpreter interpreter = new HeightThresholdInterpreterJsonLoader(this.seed, this.dataFolder, this.get("HeightThreshold"), 320).load();
            heightCondition = new HeightThresholdCoordinateCondition(interpreter);
        }
        return heightCondition;
    }

    @Nullable
    protected IPointGenerator loadEntryPointGenerator() {
        if (!this.has("EntryPoints")) {
            throw new IllegalArgumentException("\"EntryPoints\" is not defined, no spawn information for caves available");
        }
        return new PointGeneratorJsonLoader(this.seed, this.dataFolder, this.get("EntryPoints")).load();
    }

    @Nonnull
    protected Int2FlagsCondition loadBiomeMask() {
        Int2FlagsCondition mask = CaveBiomeMaskFlags.DEFAULT_ALLOW;
        if (this.has("BiomeMask")) {
            ZoneFileContext context = this.zoneContext.matchContext(this.json, "BiomeMask");
            mask = new CaveBiomeMaskJsonLoader(this.seed, this.dataFolder, this.get("BiomeMask"), context).load();
        }
        return mask;
    }

    @Nullable
    protected BlockMaskCondition loadBlockMask() {
        BlockMaskCondition placementConfiguration = BlockMaskCondition.DEFAULT_TRUE;
        if (this.has("BlockMask")) {
            placementConfiguration = new BlockPlacementMaskJsonLoader(this.seed, this.dataFolder, this.getRaw("BlockMask")).load();
        }
        return placementConfiguration;
    }

    @Nonnull
    protected ICoordinateCondition loadMapCondition() {
        return new NoiseMaskConditionJsonLoader(this.seed, this.dataFolder, this.get("NoiseMask")).load();
    }

    @Nullable
    protected IDoubleRange loadFixedEntryHeight() {
        IDoubleRange fixedEntryHeight = null;
        if (this.has("FixedEntryHeight")) {
            fixedEntryHeight = new DoubleRangeJsonLoader(this.seed, this.dataFolder, this.get("FixedEntryHeight"), 0.0).load();
        }
        return fixedEntryHeight;
    }

    @Nullable
    protected NoiseProperty loadFixedEntryHeightNoise() {
        NoiseProperty maxNoise = ConstantNoiseProperty.DEFAULT_ZERO;
        if (this.has("FixedEntryHeightNoise")) {
            maxNoise = new NoisePropertyJsonLoader(this.seed, this.dataFolder, this.get("FixedEntryHeightNoise")).load();
        }
        return maxNoise;
    }

    @Nonnull
    protected CaveType.FluidLevel loadFluidLevel() {
        CaveType.FluidLevel fluidLevel = CaveType.FluidLevel.EMPTY;
        if (this.has("FluidLevel")) {
            fluidLevel = new FluidLevelJsonLoader(this.seed, this.dataFolder, this.get("FluidLevel")).load();
        }
        return fluidLevel;
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

    protected boolean loadSurfaceLimited() {
        return !this.has("SurfaceLimited") || this.get("SurfaceLimited").getAsBoolean();
    }

    protected boolean loadSubmerge() {
        return this.mustGetBool("Submerge", Constants.DEFAULT_SUBMERGE);
    }

    protected double loadMaximumSize(@Nonnull IPointGenerator pointGenerator) {
        return this.has("MaximumSize") ? (double)this.get("MaximumSize").getAsLong() : (double)MathUtil.fastFloor(pointGenerator.getInterval());
    }

    public static interface Constants {
        public static final String KEY_YAW = "Yaw";
        public static final String KEY_PITCH = "Pitch";
        public static final String KEY_DEPTH = "Depth";
        public static final String KEY_HEIGHT_RADIUS_FACTOR = "HeightRadiusFactor";
        public static final String KEY_ENTRY = "Entry";
        public static final String KEY_ENTRY_POINTS = "EntryPoints";
        public static final String KEY_HEIGHT_THRESHOLDS = "HeightThreshold";
        public static final String KEY_BIOME_MASK = "BiomeMask";
        public static final String KEY_BLOCK_MASK = "BlockMask";
        public static final String KEY_NOISE_MASK = "NoiseMask";
        public static final String KEY_FIXED_ENTRY_HEIGHT = "FixedEntryHeight";
        public static final String KEY_FIXED_ENTRY_HEIGHT_NOISE = "FixedEntryHeightNoise";
        public static final String KEY_FLUID_LEVEL = "FluidLevel";
        public static final String KEY_SURFACE_LIMITTED = "SurfaceLimited";
        public static final String KEY_SUBMERGE = "Submerge";
        public static final String KEY_MAXIMUM_SIZE = "MaximumSize";
        public static final String KEY_ENVIRONMENT = "Environment";
        public static final Boolean DEFAULT_SUBMERGE = Boolean.FALSE;
        public static final String ERROR_NO_ENTRY = "\"Entry\" is not defined. Define an entry node type";
        public static final String ERROR_NO_ENTRY_POINTS = "\"EntryPoints\" is not defined, no spawn information for caves available";
        public static final String ERROR_LOADING_ENVIRONMENT = "Error while looking up environment \"%s\"!";
    }
}

