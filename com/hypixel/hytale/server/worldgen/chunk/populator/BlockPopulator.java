/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.chunk.populator;

import com.hypixel.hytale.math.util.FastRandom;
import com.hypixel.hytale.math.util.HashUtil;
import com.hypixel.hytale.procedurallib.condition.ConstantIntCondition;
import com.hypixel.hytale.procedurallib.condition.IBlockFluidCondition;
import com.hypixel.hytale.server.core.universe.world.worldgen.GeneratedBlockChunk;
import com.hypixel.hytale.server.worldgen.biome.Biome;
import com.hypixel.hytale.server.worldgen.chunk.ChunkGenerator;
import com.hypixel.hytale.server.worldgen.chunk.ChunkGeneratorExecution;
import com.hypixel.hytale.server.worldgen.chunk.HeightThresholdInterpolator;
import com.hypixel.hytale.server.worldgen.container.CoverContainer;
import com.hypixel.hytale.server.worldgen.container.LayerContainer;
import com.hypixel.hytale.server.worldgen.container.WaterContainer;
import com.hypixel.hytale.server.worldgen.util.BlockFluidEntry;
import com.hypixel.hytale.server.worldgen.util.NoiseBlockArray;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Random;
import javax.annotation.Nonnull;

public class BlockPopulator {
    public static void populate(int seed, @Nonnull ChunkGeneratorExecution execution) {
        FastRandom random = new FastRandom(HashUtil.hash(seed, execution.getX(), execution.getZ(), 5647422603192711886L));
        for (int cx = 0; cx < 32; ++cx) {
            for (int cz = 0; cz < 32; ++cz) {
                BlockPopulator.generateBlockColumn(seed, execution, cx, cz, random);
            }
        }
    }

    private static void generateBlockColumn(int seed, @Nonnull ChunkGeneratorExecution execution, int cx, int cz, @Nonnull Random random) {
        int y;
        HeightThresholdInterpolator interpolator = execution.getInterpolator();
        IntList surfaceBlockList = ChunkGenerator.getResource().coverArray;
        Biome biome = execution.zoneBiomeResult(cx, cz).getBiome();
        LayerContainer layerContainer = biome.getLayerContainer();
        int x = execution.globalX(cx);
        int z = execution.globalZ(cz);
        double heightmapNoise = interpolator.getHeightNoise(cx, cz);
        BlockFluidEntry filling = layerContainer.getFilling();
        int fillingEnvironment = layerContainer.getFillingEnvironment();
        int highest = 0;
        int min = interpolator.getLowestNonOne(cx, cz);
        boolean empty = true;
        for (y = interpolator.getHighestNonZero(cx, cz); y >= min; --y) {
            double threshold = interpolator.getHeightThreshold(seed, x, z, y);
            if (threshold > heightmapNoise || threshold == 1.0) {
                if (y > highest) {
                    highest = y;
                }
                execution.setBlock(cx, y, cz, (byte)1, filling, fillingEnvironment);
                if (!empty) continue;
                surfaceBlockList.add(y);
                empty = false;
                continue;
            }
            empty = true;
        }
        if (empty) {
            surfaceBlockList.add(y);
        }
        if (y > highest) {
            highest = y;
        }
        while (y >= 0) {
            execution.setBlock(cx, y, cz, (byte)1, filling);
            --y;
        }
        execution.getChunkGenerator().putHeight(seed, x, z, highest);
        LayerPopulator.generateLayers(seed, execution, cx, cz, x, z, biome, surfaceBlockList);
        BlockPopulator.generateCovers(seed, execution, cx, cz, x, z, random, biome, surfaceBlockList);
        surfaceBlockList.clear();
    }

    private static void generateCovers(int seed, @Nonnull ChunkGeneratorExecution execution, int cx, int cz, int x, int z, @Nonnull Random random, @Nonnull Biome biome, @Nonnull IntList surfaceBlockList) {
        CoverContainer coverContainer = biome.getCoverContainer();
        int size = surfaceBlockList.size();
        if (size == 0) {
            return;
        }
        for (WaterContainer.Entry waterContainer : biome.getWaterContainer().getEntries()) {
            for (CoverContainer.CoverContainerEntry coverContainerEntry : coverContainer.getEntries()) {
                int y;
                if (!coverContainerEntry.isOnWater() || !BlockPopulator.isMatchingCoverColumn(seed, coverContainerEntry, random, x, z) || !BlockPopulator.isMatchingCoverHeight(seed, coverContainerEntry, random, x, y = waterContainer.getMax(seed, x, z) + 1, z) || !BlockPopulator.isMatchingParentCover(execution, coverContainerEntry, cx, cz, y, waterContainer.getBlock(), waterContainer.getFluid())) continue;
                CoverContainer.CoverContainerEntry.CoverContainerEntryPart coverEntry = coverContainerEntry.get(random);
                execution.setBlock(cx, y + coverEntry.getOffset(), cz, (byte)3, coverEntry.getEntry());
                execution.setFluid(cx, y + coverEntry.getOffset(), cz, (byte)3, coverEntry.getEntry().fluidId());
            }
        }
        for (int i = 0; i < size; ++i) {
            int y = surfaceBlockList.getInt(i) + 1;
            for (CoverContainer.CoverContainerEntry coverContainerEntry : coverContainer.getEntries()) {
                if (coverContainerEntry.isOnWater() || !BlockPopulator.isMatchingParentCover(execution, coverContainerEntry, cx, cz, y, 0, 0) || !BlockPopulator.isMatchingCoverColumn(seed, coverContainerEntry, random, x, z) || !BlockPopulator.isMatchingCoverHeight(seed, coverContainerEntry, random, x, y, z)) continue;
                CoverContainer.CoverContainerEntry.CoverContainerEntryPart coverEntry = coverContainerEntry.get(random);
                execution.setBlock(cx, y + coverEntry.getOffset(), cz, (byte)3, coverEntry.getEntry());
                execution.setFluid(cx, y + coverEntry.getOffset(), cz, (byte)3, coverEntry.getEntry().fluidId());
            }
        }
    }

    private static boolean isMatchingParentCover(@Nonnull ChunkGeneratorExecution execution, @Nonnull CoverContainer.CoverContainerEntry coverContainerEntry, int cx, int cz, int y, int defaultId, int defaultFluidId) {
        int fluid;
        if (y <= 0 || y >= 320) {
            return false;
        }
        IBlockFluidCondition parentCondition = coverContainerEntry.getParentCondition();
        if (parentCondition == ConstantIntCondition.DEFAULT_TRUE) {
            return true;
        }
        if (parentCondition == ConstantIntCondition.DEFAULT_FALSE) {
            return false;
        }
        GeneratedBlockChunk chunk = execution.getChunk();
        int block = chunk.getBlock(cx, y - 1, cz);
        if (block == 0) {
            block = defaultId;
        }
        if ((fluid = execution.getFluid(cx, y - 1, cz)) == 0) {
            fluid = defaultFluidId;
        }
        return parentCondition.eval(block, fluid);
    }

    private static boolean isMatchingCoverColumn(int seed, @Nonnull CoverContainer.CoverContainerEntry coverContainerEntry, @Nonnull Random random, int x, int z) {
        return random.nextDouble() < coverContainerEntry.getCoverDensity() && coverContainerEntry.getMapCondition().eval(seed, x, z);
    }

    private static boolean isMatchingCoverHeight(int seed, @Nonnull CoverContainer.CoverContainerEntry coverContainerEntry, Random random, int x, int y, int z) {
        return coverContainerEntry.getHeightCondition().eval(seed, x, z, y, random);
    }

    private static class LayerPopulator {
        private LayerPopulator() {
        }

        static void generateLayers(int seed, @Nonnull ChunkGeneratorExecution execution, int cx, int cz, int x, int z, @Nonnull Biome biome, @Nonnull IntList surfaceBlockList) {
            LayerPopulator.generateStaticLayers(seed, execution, cx, cz, x, z, biome);
            LayerPopulator.generateDynamicLayers(seed, execution, cx, cz, x, z, biome, surfaceBlockList);
        }

        private static void generateDynamicLayers(int seed, @Nonnull ChunkGeneratorExecution execution, int cx, int cz, int x, int z, @Nonnull Biome biome, @Nonnull IntList surfaceBlockList) {
            LayerContainer layers = biome.getLayerContainer();
            int size = surfaceBlockList.size();
            for (int i = 0; i < size; ++i) {
                int surfaceY;
                int y = surfaceY = surfaceBlockList.getInt(i);
                int maxY = surfaceY;
                block1: for (LayerContainer.DynamicLayer layer : layers.getDynamicLayers()) {
                    LayerContainer.DynamicLayerEntry entry = (LayerContainer.DynamicLayerEntry)layer.getActiveEntry(seed, x, z);
                    if (entry == null) continue;
                    int environmentId = layer.getEnvironmentId();
                    maxY = Math.max(maxY, y += layer.getOffset(seed, x, z));
                    NoiseBlockArray blockArray = entry.getBlockArray();
                    for (NoiseBlockArray.Entry blockArrayEntry : blockArray.getEntries()) {
                        int repetitions = blockArrayEntry.getRepetitions(seed, x, z);
                        for (int j = 0; j < repetitions; ++j) {
                            if (y <= surfaceY && execution.getBlock(cx, y, cz) == 0) break block1;
                            execution.setBlock(cx, y, cz, (byte)2, blockArrayEntry.getBlockEntry(), environmentId);
                            execution.setFluid(cx, y, cz, (byte)2, blockArrayEntry.getBlockEntry().fluidId(), environmentId);
                            --y;
                        }
                    }
                }
                if (maxY <= surfaceY) continue;
                surfaceBlockList.set(i, maxY);
            }
        }

        private static void generateStaticLayers(int seed, @Nonnull ChunkGeneratorExecution execution, int cx, int cz, int x, int z, @Nonnull Biome biome) {
            LayerContainer layers = biome.getLayerContainer();
            for (LayerContainer.StaticLayer layer : layers.getStaticLayers()) {
                LayerContainer.StaticLayerEntry entry = (LayerContainer.StaticLayerEntry)layer.getActiveEntry(seed, x, z);
                if (entry == null) continue;
                int environmentId = layer.getEnvironmentId();
                NoiseBlockArray.Entry[] blockEntries = entry.getBlockArray().getEntries();
                int min = Math.max(entry.getMinInt(seed, x, z), 0);
                int max = Math.min(entry.getMaxInt(seed, x, z), 320);
                int layerY = entry.getMaxInt(seed, x, z);
                BlockFluidEntry lastBlock = null;
                for (NoiseBlockArray.Entry blockEntry : blockEntries) {
                    BlockFluidEntry block;
                    int repetitions = blockEntry.getRepetitions(seed, x, z);
                    if (repetitions <= 0) continue;
                    lastBlock = block = blockEntry.getBlockEntry();
                    for (int i = 0; i < repetitions; ++i) {
                        int currentBlock;
                        if ((currentBlock = execution.getBlock(cx, --layerY, cz)) != 0) {
                            execution.setBlock(cx, layerY, cz, (byte)2, block, environmentId);
                            execution.setFluid(cx, layerY, cz, (byte)2, block.fluidId(), environmentId);
                        }
                        if (layerY > min) continue;
                        return;
                    }
                }
                if (blockEntries.length == 0 && environmentId != Integer.MIN_VALUE) {
                    for (int y = max - 1; y >= min; --y) {
                        execution.setEnvironment(cx, y, cz, environmentId);
                    }
                }
                if (lastBlock == null) continue;
                while (layerY > min) {
                    int currentBlock;
                    if ((currentBlock = execution.getBlock(cx, --layerY, cz)) == 0) continue;
                    execution.setBlock(cx, layerY, cz, (byte)2, lastBlock);
                    execution.setFluid(cx, layerY, cz, (byte)2, lastBlock.fluidId());
                }
            }
        }
    }
}

