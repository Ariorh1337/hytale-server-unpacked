/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.chunk.populator;

import com.hypixel.hytale.common.map.IWeightedMap;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.FastRandom;
import com.hypixel.hytale.math.util.HashUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.procedurallib.condition.ConstantIntCondition;
import com.hypixel.hytale.procedurallib.condition.DefaultCoordinateRndCondition;
import com.hypixel.hytale.procedurallib.condition.IBlockFluidCondition;
import com.hypixel.hytale.procedurallib.condition.ICoordinateRndCondition;
import com.hypixel.hytale.server.core.prefab.PrefabRotation;
import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.IPrefabBuffer;
import com.hypixel.hytale.server.worldgen.biome.Biome;
import com.hypixel.hytale.server.worldgen.biome.BiomePatternGenerator;
import com.hypixel.hytale.server.worldgen.biome.CustomBiome;
import com.hypixel.hytale.server.worldgen.biome.TileBiome;
import com.hypixel.hytale.server.worldgen.cache.CoreDataCacheEntry;
import com.hypixel.hytale.server.worldgen.chunk.ChunkGenerator;
import com.hypixel.hytale.server.worldgen.chunk.ChunkGeneratorExecution;
import com.hypixel.hytale.server.worldgen.chunk.ZoneBiomeResult;
import com.hypixel.hytale.server.worldgen.container.CoverContainer;
import com.hypixel.hytale.server.worldgen.container.PrefabContainer;
import com.hypixel.hytale.server.worldgen.container.UniquePrefabContainer;
import com.hypixel.hytale.server.worldgen.container.WaterContainer;
import com.hypixel.hytale.server.worldgen.loader.WorldGenPrefabSupplier;
import com.hypixel.hytale.server.worldgen.prefab.PrefabCategory;
import com.hypixel.hytale.server.worldgen.prefab.PrefabPasteUtil;
import com.hypixel.hytale.server.worldgen.prefab.PrefabPatternGenerator;
import com.hypixel.hytale.server.worldgen.util.BlockFluidEntry;
import com.hypixel.hytale.server.worldgen.util.bounds.IChunkBounds;
import com.hypixel.hytale.server.worldgen.util.condition.BlockMaskCondition;
import com.hypixel.hytale.server.worldgen.zone.Zone;
import com.hypixel.hytale.server.worldgen.zone.ZoneGeneratorResult;
import com.hypixel.hytale.server.worldgen.zone.ZonePatternGenerator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.BitSet;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrefabPopulator {
    private static final UniquePrefabContainer.UniquePrefabEntry[] EMPTY_UNIQUE_PREFABS = new UniquePrefabContainer.UniquePrefabEntry[0];
    private static final int BIOME_SAMPLE_STEP_SIZE = 8;
    private int worldSeed;
    private long prefabSeed;
    private int minPriority = Integer.MAX_VALUE;
    @Nullable
    private Biome biome;
    @Nullable
    private PrefabContainer.PrefabContainerEntry entry;
    @Nullable
    private ChunkGeneratorExecution execution;
    @Nonnull
    private UniquePrefabContainer.UniquePrefabEntry[] uniquePrefabs = EMPTY_UNIQUE_PREFABS;
    private final FastRandom random = new FastRandom(0L);
    private final ObjectArrayList<Biome> biomes = new ObjectArrayList();
    private final ObjectArrayList<Candidate> prefabs = new ObjectArrayList();
    private final BitSet conflicts = new BitSet();

    public static void populate(int seed, @Nonnull ChunkGeneratorExecution execution) {
        ChunkGenerator.getResource().prefabPopulator.run(seed, execution);
    }

    public void run(int seed, @Nonnull ChunkGeneratorExecution execution) {
        this.worldSeed = seed;
        this.minPriority = Integer.MAX_VALUE;
        this.uniquePrefabs = Objects.requireNonNullElse(execution.getChunkGenerator().getUniquePrefabs(seed), EMPTY_UNIQUE_PREFABS);
        this.collectBiomes(seed, execution);
        this.collectPrefabs(seed, execution);
        this.collectConflicts();
        this.generatePrefabs(seed, execution);
        this.generateUniquePrefabs(seed, execution);
        this.biome = null;
        this.entry = null;
        this.execution = null;
        this.uniquePrefabs = EMPTY_UNIQUE_PREFABS;
        this.biomes.clear();
        this.prefabs.clear();
        this.conflicts.clear();
    }

    private void collectBiomes(int seed, ChunkGeneratorExecution execution) {
        for (CoreDataCacheEntry entry : execution.getCoreDataEntries()) {
            Biome biome = entry.zoneBiomeResult.getBiome();
            if (biome.getPrefabContainer() == null) continue;
            this.collectBiome(biome);
        }
        int chunkX = execution.getX();
        int chunkZ = execution.getZ();
        int chunkMinX = ChunkUtil.minBlock(chunkX) - 5;
        int chunkMinZ = ChunkUtil.minBlock(chunkZ) - 5;
        int chunkMaxX = ChunkUtil.maxBlock(chunkX) + 5;
        int chunkMaxZ = ChunkUtil.maxBlock(chunkZ) + 5;
        int extents = execution.getChunkGenerator().getZonePatternProvider().getMaxExtent();
        int regionMinX = chunkMinX - extents;
        int regionMinZ = chunkMinZ - extents;
        int regionMaxX = chunkMaxX + extents;
        int regionMaxZ = chunkMaxZ + extents;
        ZoneGeneratorResult zoneResult = ChunkGenerator.getResource().zoneBiomeResult.zoneResult;
        ZonePatternGenerator zoneGenerator = execution.getChunkGenerator().getZonePatternGenerator(seed);
        for (int z = regionMinZ; z <= regionMaxZ; z += 8) {
            for (int x = regionMinX; x <= regionMaxX; x += 8) {
                TileBiome biome;
                if (x >= chunkMinX && z >= chunkMinZ && x <= chunkMaxX && z <= chunkMaxZ) continue;
                Zone zone = zoneGenerator.generate(seed, x, z, zoneResult).getZone();
                BiomePatternGenerator biomeGenerator = zone.biomePatternGenerator();
                int minX = chunkMinX - biomeGenerator.getExtents();
                int minZ = chunkMinZ - biomeGenerator.getExtents();
                int maxX = chunkMaxX + biomeGenerator.getExtents();
                int maxZ = chunkMaxZ + biomeGenerator.getExtents();
                if (x < minX || z < minZ || x > maxX || z > maxZ || (biome = biomeGenerator.getBiome(seed, x, z)) == null) continue;
                if (biome.getPrefabContainer() != null) {
                    this.collectBiome(biome);
                }
                for (CustomBiome customBiome : biomeGenerator.getCustomBiomes()) {
                    if (customBiome.getPrefabContainer() == null || !customBiome.getCustomBiomeGenerator().isValidParentBiome(biome.getId())) continue;
                    this.collectBiome(customBiome);
                }
            }
        }
    }

    private void collectPrefabs(int seed, ChunkGeneratorExecution execution) {
        for (int i = 0; i < this.biomes.size(); ++i) {
            Biome biome = this.biomes.get(i);
            PrefabContainer container = biome.getPrefabContainer();
            if (container == null) continue;
            int id = 0;
            for (PrefabContainer.PrefabContainerEntry entry : container.getEntries()) {
                this.biome = biome;
                this.entry = entry;
                this.execution = execution;
                this.prefabSeed = HashUtil.hash(seed, biome.getId(), ++id);
                entry.getPrefabPatternGenerator().getGridGenerator().collect(seed, ChunkUtil.minBlock(execution.getX()) - entry.getExtents(), ChunkUtil.minBlock(execution.getZ()) - entry.getExtents(), ChunkUtil.maxBlock(execution.getX()) + entry.getExtents(), ChunkUtil.maxBlock(execution.getZ()) + entry.getExtents(), this::collectPrefab);
            }
        }
    }

    private void generatePrefabs(int seed, @Nonnull ChunkGeneratorExecution execution) {
        for (int i = 0; i < this.prefabs.size(); ++i) {
            if (this.conflicts.get(i)) continue;
            Candidate prefab = this.prefabs.get(i);
            int x = prefab.x;
            int y = prefab.y;
            int z = prefab.z;
            PrefabRotation rotation = prefab.rotation;
            WorldGenPrefabSupplier supplier = prefab.supplier;
            if (!PrefabPopulator.isMatchingChunkBounds(x, z, execution, rotation, supplier.getBounds(prefab.buffer))) continue;
            BlockMaskCondition config = prefab.generator.getPrefabPlacementConfiguration();
            ICoordinateRndCondition heightCondition = prefab.generator.getHeightCondition();
            int environment = prefab.entry.getEnvironmentId();
            boolean fitHeightmap = prefab.generator.isFitHeightmap();
            boolean submerge = prefab.generator.isSubmerge();
            PrefabPopulator.generatePrefabAt(seed, x, z, y, execution, supplier, config, rotation, heightCondition, environment, fitHeightmap, submerge);
        }
    }

    private void generateUniquePrefabs(int seed, @Nonnull ChunkGeneratorExecution execution) {
        for (UniquePrefabContainer.UniquePrefabEntry entry : this.uniquePrefabs) {
            if (!PrefabPopulator.isMatchingChunkBounds(execution, entry.getLowBoundX(), entry.getLowBoundZ(), entry.getHighBoundX(), entry.getHighBoundZ())) continue;
            Vector3i v = entry.getPosition();
            PrefabPopulator.generatePrefabAt(seed, v.getX(), v.getZ(), v.getY(), execution, entry.getPrefabSupplier(), entry.getConfiguration(), entry.getRotation(), DefaultCoordinateRndCondition.DEFAULT_TRUE, entry.getEnvironmentId(), entry.isFitHeightmap(), entry.isSubmerge());
        }
    }

    private void collectBiome(@Nonnull Biome biome) {
        Biome insert = biome;
        for (int i = 0; i < this.biomes.size(); ++i) {
            int id = this.biomes.get(i).getId();
            if (insert.getId() == id) {
                return;
            }
            if (insert.getId() >= id) continue;
            insert = this.biomes.set(i, insert);
        }
        this.biomes.add(insert);
    }

    private void collectPrefab(double px, double pz) {
        Objects.requireNonNull(this.biome);
        Objects.requireNonNull(this.entry);
        Objects.requireNonNull(this.execution);
        int x = (int)MathUtil.fastFloor(px);
        int z = (int)MathUtil.fastFloor(pz);
        this.random.setSeed(HashUtil.hash(this.prefabSeed, x, z) * 1609272495L);
        PrefabPatternGenerator patternGenerator = this.entry.getPrefabPatternGenerator();
        if (!PrefabPopulator.isMatchingNoiseDensity(this.worldSeed, x, z, patternGenerator)) {
            return;
        }
        if (PrefabPopulator.isWithinUniquePrefabExclusionRange(x, z, patternGenerator, this.uniquePrefabs)) {
            return;
        }
        ZoneBiomeResult result = this.execution.getChunkGenerator().getZoneBiomeResultAt(this.worldSeed, x, z);
        if (!PrefabPopulator.isMatchingBiome(this.biome, result)) {
            return;
        }
        IWeightedMap<WorldGenPrefabSupplier> prefabs = this.entry.getPrefabs();
        WorldGenPrefabSupplier supplier = prefabs.get(this.random);
        if (supplier == null) {
            return;
        }
        IPrefabBuffer prefab = supplier.get();
        if (prefab == null) {
            return;
        }
        PrefabRotation rotation = PrefabPopulator.generateRotation(x, z, this.random, patternGenerator);
        int y = PrefabPopulator.getHeight(this.worldSeed, x, z, this.execution, result.getBiome(), patternGenerator, this.random);
        if (!PrefabPopulator.isMatchingHeight(this.worldSeed, x, z, y, this.random, patternGenerator)) {
            return;
        }
        if (!PrefabPopulator.isMatchingParentBlock(this.worldSeed, x, z, y, this.random, result, this.entry)) {
            return;
        }
        PrefabCategory category = patternGenerator.getCategory();
        this.prefabs.add(new Candidate(x, y, z, category.priority(), rotation, prefab, supplier, this.entry, patternGenerator));
        this.minPriority = Math.min(this.minPriority, category.priority());
    }

    private void collectConflicts() {
        for (int i = 0; i < this.prefabs.size(); ++i) {
            Candidate candidate = this.prefabs.get(i);
            int minY = candidate.y + candidate.buffer.getMinY();
            int maxY = candidate.y + candidate.buffer.getMaxY();
            int minX = candidate.x + candidate.buffer.getMinX(candidate.rotation);
            int minZ = candidate.z + candidate.buffer.getMinZ(candidate.rotation);
            int maxX = candidate.x + candidate.buffer.getMaxX(candidate.rotation);
            int maxZ = candidate.z + candidate.buffer.getMaxZ(candidate.rotation);
            if (candidate.priority <= this.minPriority || this.conflicts.get(i)) continue;
            for (int j = 0; j < this.prefabs.size(); ++j) {
                Candidate other = this.prefabs.get(j);
                if (candidate.priority <= other.priority || !PrefabPopulator.intersects(minX, minY, minZ, maxX, maxY, maxZ, other.x + other.buffer.getMinX(other.rotation), other.y + other.buffer.getMinY(), other.z + other.buffer.getMinZ(other.rotation), other.x + other.buffer.getMaxX(other.rotation), other.y + other.buffer.getMaxY(), other.z + other.buffer.getMaxZ(other.rotation))) continue;
                this.conflicts.set(j);
            }
        }
    }

    private static boolean intersects(int minX1, int minY1, int minZ1, int maxX1, int maxY1, int maxZ1, int minX2, int minY2, int minZ2, int maxX2, int maxY2, int maxZ2) {
        return maxX1 >= minX2 && minX1 <= maxX2 && maxY1 >= minY2 && minY1 <= maxY2 && maxZ1 >= minZ2 && minZ1 <= maxZ2;
    }

    private static boolean isWithinUniquePrefabExclusionRange(int x, int z, @Nonnull PrefabPatternGenerator generator, @Nonnull UniquePrefabContainer.UniquePrefabEntry[] uniquePrefabs) {
        long radius = generator.getExclusionRadius();
        if (radius <= 0L) {
            return false;
        }
        long radius2 = radius * radius;
        int priority = generator.getCategory().priority();
        for (UniquePrefabContainer.UniquePrefabEntry unique : uniquePrefabs) {
            long dz;
            long dx;
            if (priority >= unique.getCategory().priority() || (dx = (long)(x - unique.getPosition().getX())) * dx + (dz = (long)(z - unique.getPosition().getZ())) * dz > radius2) continue;
            return true;
        }
        return false;
    }

    private static int getHeight(int seed, int x, int z, @Nonnull ChunkGeneratorExecution execution, @Nonnull Biome biome, @Nonnull PrefabPatternGenerator prefabPatternGenerator, Random random) {
        if (prefabPatternGenerator.isOnWater() && prefabPatternGenerator.isDeepSearch()) {
            height = Integer.MIN_VALUE;
            for (WaterContainer.Entry waterContainer : biome.getWaterContainer().getEntries()) {
                int max = waterContainer.getMax(seed, x, z);
                if (max == Integer.MIN_VALUE || !prefabPatternGenerator.getHeightCondition().eval(seed, x, z, max, random)) continue;
                height = max;
                break;
            }
        } else {
            height = prefabPatternGenerator.isOnWater() ? biome.getWaterContainer().getMaxHeight(seed, x, z) : (prefabPatternGenerator.isDeepSearch() ? execution.getChunkGenerator().generateHeightBetween(seed, x, z, prefabPatternGenerator.getHeightThresholdInterpreter()) : execution.getChunkGenerator().getHeight(seed, x, z));
        }
        return height += prefabPatternGenerator.getDisplacement(seed, x, z);
    }

    private static PrefabRotation generateRotation(int x, int z, @Nonnull Random random, @Nonnull PrefabPatternGenerator patternGenerator) {
        PrefabRotation[] prefabRotations = patternGenerator.getRotations();
        if (prefabRotations == null) {
            prefabRotations = PrefabRotation.VALUES;
        }
        return prefabRotations[random.nextInt(prefabRotations.length)];
    }

    private static void generatePrefabAt(int seed, int x, int z, int height, @Nonnull ChunkGeneratorExecution execution, @Nonnull WorldGenPrefabSupplier supplier, BlockMaskCondition configuration, PrefabRotation rotation, ICoordinateRndCondition heightCondition, int environmentId, boolean fitHeightmap, boolean submerge) {
        int cx = x - ChunkUtil.minBlock(execution.getX());
        int cz = z - ChunkUtil.minBlock(execution.getZ());
        long externalSeed = HashUtil.hash(x, z) * -1058827062L;
        PrefabPasteUtil.PrefabPasteBuffer buffer = ChunkGenerator.getResource().prefabBuffer;
        buffer.setSeed(seed, externalSeed);
        buffer.execution = execution;
        buffer.blockMask = configuration;
        buffer.environmentId = environmentId;
        buffer.fitHeightmap = fitHeightmap;
        buffer.priority = (byte)(submerge ? 41 : 9);
        buffer.spawnCondition = heightCondition;
        if (execution.getChunkGenerator().getBenchmark().isEnabled() && ChunkUtil.isInsideChunkRelative(cx, cz)) {
            ZoneBiomeResult zb = execution.zoneBiomeResult(cx, cz);
            String zoneName = zb.zoneResult.getZone().name();
            String biomeName = zb.biome.getName();
            execution.getChunkGenerator().getBenchmark().registerPrefab(zoneName + "\t" + biomeName + "\t" + supplier.getName());
        }
        PrefabPasteUtil.generate(buffer, rotation, supplier, x, height, z, cx, cz);
    }

    private static boolean isMatchingBiome(Biome biome, @Nonnull ZoneBiomeResult zoneAndBiomeResult) {
        return zoneAndBiomeResult.getBiome() == biome;
    }

    private static boolean isMatchingChunkBounds(int x, int z, @Nonnull ChunkGeneratorExecution execution, @Nonnull PrefabRotation rotation, @Nonnull IChunkBounds bounds) {
        int minX = x + bounds.getLowBoundX(rotation);
        int minZ = z + bounds.getLowBoundZ(rotation);
        int maxX = x + bounds.getHighBoundX(rotation);
        int maxZ = z + bounds.getHighBoundZ(rotation);
        return PrefabPopulator.isMatchingChunkBounds(execution, minX, minZ, maxX, maxZ);
    }

    private static boolean isMatchingChunkBounds(@Nonnull ChunkGeneratorExecution execution, int lowBoundX, int lowBoundZ, int highBoundX, int highBoundZ) {
        return ChunkUtil.maxBlock(execution.getX()) >= lowBoundX && ChunkUtil.minBlock(execution.getX()) <= highBoundX && ChunkUtil.maxBlock(execution.getZ()) >= lowBoundZ && ChunkUtil.minBlock(execution.getZ()) <= highBoundZ;
    }

    private static boolean isMatchingHeight(int seed, int x, int z, int y, Random random, @Nonnull PrefabPatternGenerator prefabPatternGenerator) {
        return prefabPatternGenerator.getHeightCondition().eval(seed, x, z, y, random);
    }

    private static boolean isMatchingNoiseDensity(int seed, int x, int z, @Nonnull PrefabPatternGenerator prefabPatternGenerator) {
        return prefabPatternGenerator.getMapCondition().eval(seed, x, z);
    }

    private static boolean isMatchingParentBlock(int seed, int x, int z, int y, @Nonnull Random random, @Nonnull ZoneBiomeResult zoneAndBiomeResult, @Nonnull PrefabContainer.PrefabContainerEntry containerEntry) {
        IBlockFluidCondition parentCondition = containerEntry.getPrefabPatternGenerator().getParentCondition();
        if (parentCondition == ConstantIntCondition.DEFAULT_TRUE) {
            return true;
        }
        if (parentCondition == ConstantIntCondition.DEFAULT_FALSE) {
            return false;
        }
        BlockFluidEntry groundCover = PrefabPopulator.getCoverInGroundAt(seed, x, z, y, random, zoneAndBiomeResult.getBiome());
        if (!groundCover.equals(BlockFluidEntry.EMPTY) && !parentCondition.eval(groundCover.blockId(), groundCover.fluidId())) {
            return false;
        }
        BlockFluidEntry topBlock = zoneAndBiomeResult.getBiome().getLayerContainer().getTopBlockAt(seed, x, z);
        return parentCondition.eval(topBlock.blockId(), topBlock.fluidId());
    }

    private static BlockFluidEntry getCoverInGroundAt(int seed, int x, int z, int y, @Nonnull Random random, @Nonnull Biome biome) {
        for (CoverContainer.CoverContainerEntry coverContainerEntry : biome.getCoverContainer().getEntries()) {
            CoverContainer.CoverContainerEntry.CoverContainerEntryPart coverEntry;
            if (y >= 320 || !PrefabPopulator.isMatchingCover(seed, x, z, y, random, coverContainerEntry) || (coverEntry = coverContainerEntry.get(random)) == null || coverEntry.getOffset() != -1) continue;
            return coverEntry.getEntry();
        }
        return BlockFluidEntry.EMPTY;
    }

    private static boolean isMatchingCover(int seed, int x, int z, int y, @Nonnull Random random, @Nonnull CoverContainer.CoverContainerEntry coverContainerEntry) {
        return random.nextDouble() < coverContainerEntry.getCoverDensity() && coverContainerEntry.getMapCondition().eval(seed, x, z) && coverContainerEntry.getHeightCondition().eval(seed, x, z, y, random);
    }

    private record Candidate(int x, int y, int z, int priority, PrefabRotation rotation, IPrefabBuffer buffer, WorldGenPrefabSupplier supplier, PrefabContainer.PrefabContainerEntry entry, PrefabPatternGenerator generator) {
    }
}

