/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.newsystem.stages;

import com.hypixel.hytale.builtin.hytalegenerator.biome.BiomeType;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.material.MaterialCache;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.GridUtils;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.NStagedChunkGenerator;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.NBufferBundle;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NCountedPixelBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NSimplePixelBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NVoxelBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type.NBufferType;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type.NParametrizedBufferType;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.containers.FloatContainer3d;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.stages.NBiomeDistanceStage;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.stages.NStage;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.views.NPixelBufferView;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.views.NVoxelBufferView;
import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public class NTerrainStage
implements NStage {
    public static final double DEFAULT_BACKGROUND_DENSITY = 0.0;
    public static final double ORIGIN_REACH = 1.0;
    public static final double ORIGIN_REACH_HALF = 0.5;
    public static final double QUARTER_PI = 0.7853981633974483;
    public static final Class<NCountedPixelBuffer> biomeBufferClass = NCountedPixelBuffer.class;
    public static final Class<BiomeType> biomeClass = BiomeType.class;
    public static final Class<NSimplePixelBuffer> biomeDistanceBufferClass = NSimplePixelBuffer.class;
    public static final Class<NBiomeDistanceStage.BiomeDistanceEntries> biomeDistanceClass = NBiomeDistanceStage.BiomeDistanceEntries.class;
    public static final Class<NVoxelBuffer> materialBufferClass = NVoxelBuffer.class;
    public static final Class<Material> materialClass = Material.class;
    private final NParametrizedBufferType biomeInputBufferType;
    private final NParametrizedBufferType biomeDistanceInputBufferType;
    private final NParametrizedBufferType materialOutputBufferType;
    private final Bounds3i inputBounds_bufferGrid;
    private final String stageName;
    private final int maxInterpolationRadius_voxelGrid;
    private final MaterialCache materialCache;
    private final WorkerIndexer.Data<FloatContainer3d> densityContainers;

    public NTerrainStage(@Nonnull String stageName, @Nonnull NParametrizedBufferType biomeInputBufferType, @Nonnull NParametrizedBufferType biomeDistanceInputBufferType, @Nonnull NParametrizedBufferType materialOutputBufferType, int maxInterpolationRadius_voxelGrid, @Nonnull MaterialCache materialCache, @Nonnull WorkerIndexer workerIndexer) {
        assert (biomeInputBufferType.isValidType(biomeBufferClass, biomeClass));
        assert (biomeDistanceInputBufferType.isValidType(biomeDistanceBufferClass, biomeDistanceClass));
        assert (materialOutputBufferType.isValidType(materialBufferClass, materialClass));
        assert (maxInterpolationRadius_voxelGrid >= 0);
        this.biomeInputBufferType = biomeInputBufferType;
        this.biomeDistanceInputBufferType = biomeDistanceInputBufferType;
        this.materialOutputBufferType = materialOutputBufferType;
        this.stageName = stageName;
        this.maxInterpolationRadius_voxelGrid = maxInterpolationRadius_voxelGrid;
        this.materialCache = materialCache;
        this.densityContainers = new WorkerIndexer.Data<FloatContainer3d>(workerIndexer.getWorkerCount(), () -> new FloatContainer3d(NStagedChunkGenerator.SINGLE_BUFFER_TILE_BOUNDS_BUFFER_GRID, 0.0f));
        this.inputBounds_bufferGrid = GridUtils.createColumnBounds_bufferGrid(new Vector3i(), 0, 40);
        this.inputBounds_bufferGrid.min.subtract(Vector3i.ALL_ONES);
        this.inputBounds_bufferGrid.max.add(Vector3i.ALL_ONES);
        GridUtils.setBoundsYToWorldHeight_bufferGrid(this.inputBounds_bufferGrid);
    }

    @Override
    public void run(@Nonnull NStage.Context context) {
        NBufferBundle.Access.View biomeAccess = context.bufferAccess.get(this.biomeInputBufferType);
        NPixelBufferView<BiomeType> biomeSpace = new NPixelBufferView<BiomeType>(biomeAccess, biomeClass);
        NBufferBundle.Access.View biomeDistanceAccess = context.bufferAccess.get(this.biomeDistanceInputBufferType);
        NPixelBufferView<NBiomeDistanceStage.BiomeDistanceEntries> biomeDistanceSpace = new NPixelBufferView<NBiomeDistanceStage.BiomeDistanceEntries>(biomeDistanceAccess, biomeDistanceClass);
        NBufferBundle.Access.View materialAccess = context.bufferAccess.get(this.materialOutputBufferType);
        NVoxelBufferView<Material> materialSpace = new NVoxelBufferView<Material>(materialAccess, materialClass);
        Bounds3i outputBounds_voxelGrid = materialSpace.getBounds();
        FloatContainer3d densityContainer = this.densityContainers.get(context.workerId);
        densityContainer.moveMinTo(outputBounds_voxelGrid.min);
        this.generateDensity(densityContainer, biomeSpace, biomeDistanceSpace, context.workerId);
        this.generateMaterials(biomeSpace, biomeDistanceSpace, densityContainer, materialSpace, context.workerId);
    }

    @Override
    @Nonnull
    public Map<NBufferType, Bounds3i> getInputTypesAndBounds_bufferGrid() {
        HashMap<NBufferType, Bounds3i> map = new HashMap<NBufferType, Bounds3i>();
        map.put(this.biomeInputBufferType, this.inputBounds_bufferGrid);
        map.put(this.biomeDistanceInputBufferType, this.inputBounds_bufferGrid);
        return map;
    }

    @Override
    @Nonnull
    public List<NBufferType> getOutputTypes() {
        return List.of(this.materialOutputBufferType);
    }

    @Override
    @Nonnull
    public String getName() {
        return this.stageName;
    }

    private void generateDensity(@Nonnull FloatContainer3d densityBuffer, @Nonnull NPixelBufferView<BiomeType> biomeSpace, @Nonnull NPixelBufferView<NBiomeDistanceStage.BiomeDistanceEntries> distanceSpace, @Nonnull WorkerIndexer.Id workerId) {
        Bounds3i bounds_voxelGrid = densityBuffer.getBounds_voxelGrid();
        Vector3i position_voxelGrid = new Vector3i(bounds_voxelGrid.min);
        Density.Context densityContext = new Density.Context();
        densityContext.position = position_voxelGrid.toVector3d();
        densityContext.workerId = workerId;
        position_voxelGrid.x = bounds_voxelGrid.min.x;
        while (position_voxelGrid.x < bounds_voxelGrid.max.x) {
            densityContext.position.x = position_voxelGrid.x;
            position_voxelGrid.z = bounds_voxelGrid.min.z;
            while (position_voxelGrid.z < bounds_voxelGrid.max.z) {
                densityContext.position.z = position_voxelGrid.z;
                position_voxelGrid.y = 0;
                position_voxelGrid.dropHash();
                BiomeType biomeAtOrigin = biomeSpace.getContent(position_voxelGrid);
                NBiomeDistanceStage.BiomeDistanceEntries biomeDistances = distanceSpace.getContent(position_voxelGrid);
                BiomeWeights biomeWeights = NTerrainStage.createWeights(biomeDistances, biomeAtOrigin, this.maxInterpolationRadius_voxelGrid);
                densityContext.distanceToBiomeEdge = biomeDistances.distanceToClosestOtherBiome(biomeAtOrigin);
                boolean isFirstBiome = true;
                for (BiomeWeights.Entry biomeWeight : biomeWeights.entries) {
                    Density density = biomeWeight.biomeType.getTerrainDensity();
                    if (isFirstBiome) {
                        position_voxelGrid.y = bounds_voxelGrid.min.y;
                        while (position_voxelGrid.y < bounds_voxelGrid.max.y) {
                            position_voxelGrid.dropHash();
                            densityContext.position.y = position_voxelGrid.y;
                            float densityValue = (float)density.process(densityContext);
                            float scaledDensityValue = densityValue * biomeWeight.weight;
                            densityBuffer.set(position_voxelGrid, scaledDensityValue);
                            ++position_voxelGrid.y;
                        }
                    }
                    if (!isFirstBiome) {
                        position_voxelGrid.y = bounds_voxelGrid.min.y;
                        while (position_voxelGrid.y < bounds_voxelGrid.max.y) {
                            position_voxelGrid.dropHash();
                            densityContext.position.y = position_voxelGrid.y;
                            float bufferDensityValue = densityBuffer.get(position_voxelGrid);
                            float densityValue = (float)density.process(densityContext);
                            float scaledDensityValue = densityValue * biomeWeight.weight;
                            densityBuffer.set(position_voxelGrid, bufferDensityValue + scaledDensityValue);
                            ++position_voxelGrid.y;
                        }
                    }
                    isFirstBiome = false;
                }
                ++position_voxelGrid.z;
            }
            ++position_voxelGrid.x;
        }
    }

    private float getOrGenerateDensity(@Nonnull Vector3i position_voxelGrid, @Nonnull FloatContainer3d densityBuffer, @Nonnull NPixelBufferView<BiomeType> biomeSpace, @Nonnull NPixelBufferView<NBiomeDistanceStage.BiomeDistanceEntries> distanceSpace, @Nonnull WorkerIndexer.Id workerId) {
        if (densityBuffer.getBounds_voxelGrid().contains(position_voxelGrid)) {
            return densityBuffer.get(position_voxelGrid);
        }
        return this.generateDensity(position_voxelGrid, biomeSpace, distanceSpace, workerId);
    }

    private float generateDensity(@Nonnull Vector3i position_voxelGrid, @Nonnull NPixelBufferView<BiomeType> biomeSpace, @Nonnull NPixelBufferView<NBiomeDistanceStage.BiomeDistanceEntries> distanceSpace, @Nonnull WorkerIndexer.Id workerId) {
        if (!distanceSpace.isInsideSpace(position_voxelGrid.x, 0, position_voxelGrid.z)) {
            return 0.0f;
        }
        Density.Context densityContext = new Density.Context();
        densityContext.position = position_voxelGrid.toVector3d();
        densityContext.workerId = workerId;
        BiomeType biomeAtOrigin = biomeSpace.getContent(position_voxelGrid.x, 0, position_voxelGrid.z);
        NBiomeDistanceStage.BiomeDistanceEntries biomeDistances = distanceSpace.getContent(position_voxelGrid.x, 0, position_voxelGrid.z);
        BiomeWeights biomeWeights = NTerrainStage.createWeights(biomeDistances, biomeAtOrigin, this.maxInterpolationRadius_voxelGrid);
        float densityResult = 0.0f;
        for (BiomeWeights.Entry biomeWeight : biomeWeights.entries) {
            Density density = biomeWeight.biomeType.getTerrainDensity();
            float densityValue = (float)density.process(densityContext);
            float scaledDensityValue = densityValue * biomeWeight.weight;
            densityResult += scaledDensityValue;
        }
        return densityResult;
    }

    private void generateMaterials(@Nonnull NPixelBufferView<BiomeType> biomeSpace, @Nonnull NPixelBufferView<NBiomeDistanceStage.BiomeDistanceEntries> distanceSpace, @Nonnull FloatContainer3d densityBuffer, @Nonnull NVoxelBufferView<Material> materialSpace, @Nonnull WorkerIndexer.Id workerId) {
        Bounds3i bounds_voxelGrid = materialSpace.getBounds();
        Vector3i position_voxelGrid = new Vector3i();
        position_voxelGrid.x = bounds_voxelGrid.min.x;
        while (position_voxelGrid.x < bounds_voxelGrid.max.x) {
            position_voxelGrid.z = bounds_voxelGrid.min.z;
            while (position_voxelGrid.z < bounds_voxelGrid.max.z) {
                position_voxelGrid.y = bounds_voxelGrid.min.y;
                BiomeType biome = biomeSpace.getContent(position_voxelGrid.x, 0, position_voxelGrid.z);
                MaterialProvider<Material> materialProvider = biome.getMaterialProvider();
                ColumnData columnData = new ColumnData(this, bounds_voxelGrid.min.y, bounds_voxelGrid.max.y, position_voxelGrid.x, position_voxelGrid.z, densityBuffer, materialProvider);
                double distanceToOtherBiome_voxelGrid = distanceSpace.getContent(position_voxelGrid).distanceToClosestOtherBiome(biome);
                position_voxelGrid.y = bounds_voxelGrid.min.y;
                while (position_voxelGrid.y < bounds_voxelGrid.max.y) {
                    int i = position_voxelGrid.y - bounds_voxelGrid.min.y;
                    MaterialProvider.Context context = new MaterialProvider.Context(position_voxelGrid, 0.0, columnData.depthIntoFloor[i], columnData.depthIntoCeiling[i], columnData.spaceAboveFloor[i], columnData.spaceBelowCeiling[i], workerId, (position, id) -> this.getOrGenerateDensity(position, densityBuffer, biomeSpace, distanceSpace, workerId), distanceToOtherBiome_voxelGrid);
                    Material material = columnData.materialProvider.getVoxelTypeAt(context);
                    if (material != null) {
                        materialSpace.set(material, position_voxelGrid.x, position_voxelGrid.y, position_voxelGrid.z);
                    } else {
                        materialSpace.set(this.materialCache.EMPTY, position_voxelGrid.x, position_voxelGrid.y, position_voxelGrid.z);
                    }
                    ++position_voxelGrid.y;
                }
                ++position_voxelGrid.z;
            }
            ++position_voxelGrid.x;
        }
    }

    @Nonnull
    private static BiomeWeights createWeights(@Nonnull NBiomeDistanceStage.BiomeDistanceEntries distances, @Nonnull BiomeType biomeAtOrigin, double interpolationRange) {
        double circleRadius = interpolationRange + 0.5;
        BiomeWeights biomeWeights = new BiomeWeights();
        int originIndex = 0;
        double smallestNonOriginDistance = Double.MAX_VALUE;
        double total = 0.0;
        for (int i = 0; i < distances.entries.size(); ++i) {
            NBiomeDistanceStage.BiomeDistanceEntry distanceEntry = distances.entries.get(i);
            BiomeWeights.Entry weightEntry = new BiomeWeights.Entry();
            if (distanceEntry.distance_voxelGrid >= interpolationRange) continue;
            if (distanceEntry.biomeType == biomeAtOrigin) {
                originIndex = biomeWeights.entries.size();
            } else if (distanceEntry.distance_voxelGrid < smallestNonOriginDistance) {
                smallestNonOriginDistance = distanceEntry.distance_voxelGrid;
            }
            weightEntry.biomeType = distanceEntry.biomeType;
            weightEntry.weight = (float)NTerrainStage.areaUnderCircleCurve(distanceEntry.distance_voxelGrid, circleRadius, circleRadius);
            biomeWeights.entries.add(weightEntry);
            total += (double)weightEntry.weight;
        }
        if (biomeWeights.entries.size() > 0) {
            BiomeWeights.Entry originWeightEntry = biomeWeights.entries.get(originIndex);
            double maxX = 0.5 + smallestNonOriginDistance;
            double originExtraWeight = NTerrainStage.areaUnderCircleCurve(0.0, maxX, circleRadius);
            originWeightEntry.weight = (float)((double)originWeightEntry.weight + originExtraWeight);
            total += originExtraWeight;
        }
        for (BiomeWeights.Entry entry : biomeWeights.entries) {
            entry.weight /= (float)total;
        }
        return biomeWeights;
    }

    private static double areaUnderCircleCurve(double maxX) {
        if (maxX < 0.0) {
            return 0.0;
        }
        if (maxX > 1.0) {
            return 0.7853981633974483;
        }
        return 0.5 * (maxX * Math.sqrt(1.0 - maxX * maxX) + Math.asin(maxX));
    }

    private static double areaUnderCircleCurve(double minX, double maxX, double circleRadius) {
        assert (circleRadius >= 0.0);
        assert (minX <= maxX);
        return circleRadius * circleRadius * (NTerrainStage.areaUnderCircleCurve(maxX /= circleRadius) - NTerrainStage.areaUnderCircleCurve(minX /= circleRadius));
    }

    private static class BiomeWeights {
        List<Entry> entries = new ArrayList<Entry>(3);

        BiomeWeights() {
        }

        static class Entry {
            BiomeType biomeType;
            float weight;

            Entry() {
            }
        }
    }

    private class ColumnData {
        int worldX;
        int worldZ;
        MaterialProvider<Material> materialProvider;
        int topExclusive;
        int bottom;
        int arrayLength;
        int[] depthIntoFloor;
        int[] spaceBelowCeiling;
        int[] depthIntoCeiling;
        int[] spaceAboveFloor;
        int top;
        FloatContainer3d densityBuffer;

        private ColumnData(NTerrainStage nTerrainStage, int bottom, int topExclusive, int worldX, @Nonnull int worldZ, @Nonnull FloatContainer3d densityBuffer, MaterialProvider<Material> materialProvider) {
            int i;
            int y;
            this.topExclusive = topExclusive;
            this.bottom = bottom;
            this.worldX = worldX;
            this.worldZ = worldZ;
            this.arrayLength = topExclusive - bottom;
            this.depthIntoFloor = new int[this.arrayLength];
            this.spaceBelowCeiling = new int[this.arrayLength];
            this.depthIntoCeiling = new int[this.arrayLength];
            this.spaceAboveFloor = new int[this.arrayLength];
            this.top = topExclusive - 1;
            this.densityBuffer = densityBuffer;
            this.materialProvider = materialProvider;
            Vector3i position = new Vector3i(worldX, 0, worldZ);
            Vector3i positionAbove = new Vector3i(worldX, 0, worldZ);
            Vector3i positionBelow = new Vector3i(worldX, 0, worldZ);
            for (y = this.top; y >= bottom; --y) {
                boolean solidity;
                position.y = y;
                positionAbove.y = y + 1;
                positionBelow.y = y - 1;
                i = y - bottom;
                float density = densityBuffer.get(position);
                boolean bl = solidity = (double)density > 0.0;
                if (y == this.top) {
                    this.depthIntoFloor[i] = solidity ? 1 : 0;
                    this.spaceAboveFloor[i] = 0x3FFFFFFF;
                    continue;
                }
                if (solidity) {
                    this.depthIntoFloor[i] = this.depthIntoFloor[i + 1] + 1;
                    this.spaceAboveFloor[i] = this.spaceAboveFloor[i + 1];
                    continue;
                }
                this.depthIntoFloor[i] = 0;
                this.spaceAboveFloor[i] = (double)densityBuffer.get(positionAbove) > 0.0 ? 0 : this.spaceAboveFloor[i + 1] + 1;
            }
            for (y = bottom; y <= this.top; ++y) {
                boolean solidity;
                i = y - bottom;
                double density = densityBuffer.get(position);
                boolean bl = solidity = density > 0.0;
                if (y == bottom) {
                    this.depthIntoCeiling[i] = solidity ? 1 : 0;
                    this.spaceBelowCeiling[i] = Integer.MAX_VALUE;
                    continue;
                }
                if (solidity) {
                    this.depthIntoCeiling[i] = this.depthIntoCeiling[i - 1] + 1;
                    this.spaceBelowCeiling[i] = this.spaceBelowCeiling[i - 1];
                    continue;
                }
                this.depthIntoCeiling[i] = 0;
                this.spaceBelowCeiling[i] = (double)densityBuffer.get(positionBelow) > 0.0 ? 0 : this.spaceBelowCeiling[i - 1] + 1;
            }
        }
    }
}

