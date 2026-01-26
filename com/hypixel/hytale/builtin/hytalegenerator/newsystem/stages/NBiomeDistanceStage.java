/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.newsystem.stages;

import com.hypixel.hytale.builtin.hytalegenerator.biome.BiomeType;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.framework.math.Calculator;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.GridUtils;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.NBufferBundle;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NCountedPixelBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NPixelBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NSimplePixelBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type.NBufferType;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type.NParametrizedBufferType;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.stages.NStage;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.views.NPixelBufferView;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NBiomeDistanceStage
implements NStage {
    private static final double ORIGIN_REACH = 1.0;
    private static final double BUFFER_DIAGONAL_VOXEL_GRID = Math.sqrt(NPixelBuffer.SIZE.x * NPixelBuffer.SIZE.x + NPixelBuffer.SIZE.z * NPixelBuffer.SIZE.z);
    public static final double DEFAULT_DISTANCE_TO_BIOME_EDGE = Double.MAX_VALUE;
    public static final Class<NCountedPixelBuffer> biomeBufferClass = NCountedPixelBuffer.class;
    public static final Class<BiomeType> biomeTypeClass = BiomeType.class;
    public static final Class<NSimplePixelBuffer> biomeDistanceBufferClass = NSimplePixelBuffer.class;
    public static final Class<BiomeDistanceEntries> biomeDistanceClass = BiomeDistanceEntries.class;
    private final NParametrizedBufferType biomeInputBufferType;
    private final NParametrizedBufferType biomeDistanceOutputBufferType;
    private final String stageName;
    private final double maxDistance_voxelGrid;
    private final int maxDistance_bufferGrid;
    private final Bounds3i inputBounds_bufferGrid;

    public NBiomeDistanceStage(@Nonnull String stageName, @Nonnull NParametrizedBufferType biomeInputBufferType, @Nonnull NParametrizedBufferType biomeDistanceOutputBufferType, double maxDistance_voxelGrid) {
        assert (maxDistance_voxelGrid >= 0.0);
        this.stageName = stageName;
        this.biomeInputBufferType = biomeInputBufferType;
        this.biomeDistanceOutputBufferType = biomeDistanceOutputBufferType;
        this.maxDistance_voxelGrid = maxDistance_voxelGrid;
        this.maxDistance_bufferGrid = GridUtils.toBufferDistanceInclusive_fromVoxelDistance((int)Math.ceil(maxDistance_voxelGrid));
        Bounds3i inputBounds_voxelGrid = GridUtils.createBounds_fromRadius_originVoxelInclusive((int)Math.ceil(maxDistance_voxelGrid));
        Bounds3i bufferColumnBounds_voxelGrid = GridUtils.createColumnBounds_voxelGrid(new Vector3i(), 0, 1);
        inputBounds_voxelGrid.stack(bufferColumnBounds_voxelGrid);
        this.inputBounds_bufferGrid = GridUtils.createBufferBoundsInclusive_fromVoxelBounds(inputBounds_voxelGrid);
        GridUtils.setBoundsYToWorldHeight_bufferGrid(this.inputBounds_bufferGrid);
    }

    @Override
    public void run(@Nonnull NStage.Context context) {
        NBufferBundle.Access.View biomeAccess = context.bufferAccess.get(this.biomeInputBufferType);
        NPixelBufferView<BiomeType> biomeSpace = new NPixelBufferView<BiomeType>(biomeAccess, biomeTypeClass);
        NBufferBundle.Access.View biomeDistanceAccess = context.bufferAccess.get(this.biomeDistanceOutputBufferType);
        NPixelBufferView<BiomeDistanceEntries> biomeDistanceSpace = new NPixelBufferView<BiomeDistanceEntries>(biomeDistanceAccess, biomeDistanceClass);
        Vector3i position_voxelGrid = new Vector3i();
        position_voxelGrid.x = biomeDistanceSpace.minX();
        while (position_voxelGrid.x < biomeDistanceSpace.maxX()) {
            position_voxelGrid.z = biomeDistanceSpace.minZ();
            while (position_voxelGrid.z < biomeDistanceSpace.maxZ()) {
                BiomeDistanceEntries distanceEntries = this.createDistanceTracker(biomeAccess, biomeSpace, position_voxelGrid);
                biomeDistanceSpace.set(distanceEntries, position_voxelGrid);
                ++position_voxelGrid.z;
            }
            ++position_voxelGrid.x;
        }
    }

    @Nonnull
    private BiomeDistanceEntries createDistanceTracker(@Nonnull NBufferBundle.Access.View biomeAccess, @Nonnull NPixelBufferView<BiomeType> biomeSpace, @Nonnull Vector3i targetPosition_voxelGrid) {
        BiomeDistanceCounter counter = new BiomeDistanceCounter();
        Vector3i position_bufferGrid = new Vector3i();
        Bounds3i scanBounds_voxelGrid = GridUtils.createBounds_fromRadius_originVoxelInclusive((int)Math.ceil(this.maxDistance_voxelGrid));
        scanBounds_voxelGrid.offset(targetPosition_voxelGrid);
        Bounds3i scanBounds_bufferGrid = GridUtils.createBufferBoundsInclusive_fromVoxelBounds(scanBounds_voxelGrid);
        position_bufferGrid.x = scanBounds_bufferGrid.min.x;
        while (position_bufferGrid.x < scanBounds_bufferGrid.max.x) {
            position_bufferGrid.z = scanBounds_bufferGrid.min.z;
            while (position_bufferGrid.z < scanBounds_bufferGrid.max.z) {
                double distanceToBuffer_voxelGrid = NBiomeDistanceStage.distanceToBuffer_voxelGrid(targetPosition_voxelGrid, position_bufferGrid);
                if (!((distanceToBuffer_voxelGrid = Math.max(distanceToBuffer_voxelGrid - 1.0, 0.0)) > this.maxDistance_voxelGrid)) {
                    NCountedPixelBuffer biomeBuffer = (NCountedPixelBuffer)biomeAccess.getBuffer(position_bufferGrid).buffer();
                    List<BiomeType> uniqueBiomeTypes = biomeBuffer.getUniqueEntries();
                    assert (!uniqueBiomeTypes.isEmpty());
                    if (!NBiomeDistanceStage.allBiomesAreCountedAndFarther(counter, uniqueBiomeTypes, distanceToBuffer_voxelGrid)) {
                        if (uniqueBiomeTypes.size() == 1) {
                            if (!(distanceToBuffer_voxelGrid > this.maxDistance_voxelGrid)) {
                                counter.accountFor(uniqueBiomeTypes.getFirst(), distanceToBuffer_voxelGrid);
                            }
                        } else {
                            Bounds3i bufferBounds_voxelGrid = GridUtils.createColumnBounds_voxelGrid(position_bufferGrid, 0, 1);
                            Vector3i columnPosition_voxelGrid = new Vector3i();
                            columnPosition_voxelGrid.x = bufferBounds_voxelGrid.min.x;
                            while (columnPosition_voxelGrid.x < bufferBounds_voxelGrid.max.x) {
                                columnPosition_voxelGrid.z = bufferBounds_voxelGrid.min.z;
                                while (columnPosition_voxelGrid.z < bufferBounds_voxelGrid.max.z) {
                                    double distanceToColumn_voxelGrid = Calculator.distance(columnPosition_voxelGrid.x, columnPosition_voxelGrid.z, targetPosition_voxelGrid.x, targetPosition_voxelGrid.z);
                                    if (!((distanceToColumn_voxelGrid = Math.max(distanceToColumn_voxelGrid - 1.0, 0.0)) > this.maxDistance_voxelGrid)) {
                                        BiomeType biomeType = biomeSpace.getContent(columnPosition_voxelGrid);
                                        assert (biomeType != null);
                                        counter.accountFor(biomeType, distanceToColumn_voxelGrid);
                                    }
                                    ++columnPosition_voxelGrid.z;
                                }
                                ++columnPosition_voxelGrid.x;
                            }
                        }
                    }
                }
                ++position_bufferGrid.z;
            }
            ++position_bufferGrid.x;
        }
        return new BiomeDistanceEntries(counter.entries);
    }

    @Override
    @Nonnull
    public Map<NBufferType, Bounds3i> getInputTypesAndBounds_bufferGrid() {
        return Map.of(this.biomeInputBufferType, this.inputBounds_bufferGrid);
    }

    @Override
    @Nonnull
    public List<NBufferType> getOutputTypes() {
        return List.of(this.biomeDistanceOutputBufferType);
    }

    @Override
    @Nonnull
    public String getName() {
        return this.stageName;
    }

    public static double distanceToBuffer_voxelGrid(@Nonnull Vector3i position_voxelGrid, @Nonnull Vector3i position_bufferGrid) {
        assert ((double)position_voxelGrid.y == 0.0);
        assert ((double)position_bufferGrid.y == 0.0);
        Vector3i bufferAtPosition_bufferGrid = position_voxelGrid.clone();
        GridUtils.toBufferGrid_fromVoxelGrid(bufferAtPosition_bufferGrid);
        if (bufferAtPosition_bufferGrid.x == position_bufferGrid.x && bufferAtPosition_bufferGrid.z == position_bufferGrid.z) {
            return 0.0;
        }
        int cornerShift = NCountedPixelBuffer.SIZE_VOXEL_GRID.x - 1;
        Vector3i corner00 = position_bufferGrid.clone();
        GridUtils.toVoxelGrid_fromBufferGrid(corner00);
        Vector3i corner01 = new Vector3i(corner00);
        corner01.z += cornerShift;
        if (position_voxelGrid.x >= corner00.x && position_voxelGrid.x <= corner00.x + cornerShift) {
            return Math.min(Math.abs(position_voxelGrid.z - corner00.z), Math.abs(position_voxelGrid.z - corner01.z));
        }
        Vector3i corner10 = new Vector3i(corner00);
        corner10.x += cornerShift;
        if (position_voxelGrid.z >= corner00.z && position_voxelGrid.z <= corner00.z + cornerShift) {
            return Math.min(Math.abs(position_voxelGrid.x - corner00.x), Math.abs(position_voxelGrid.x - corner10.x));
        }
        if (position_voxelGrid.x < corner00.x && position_voxelGrid.z < corner00.z) {
            return position_voxelGrid.distanceTo(corner00);
        }
        if (position_voxelGrid.x < corner01.x && position_voxelGrid.z > corner01.z) {
            return position_voxelGrid.distanceTo(corner01);
        }
        if (position_voxelGrid.x > corner10.x && position_voxelGrid.z < corner10.z) {
            return position_voxelGrid.distanceTo(corner10);
        }
        Vector3i corner11 = new Vector3i(corner10.x, 0, corner01.z);
        return position_voxelGrid.distanceTo(corner11);
    }

    private static boolean allBiomesAreCountedAndFarther(@Nonnull BiomeDistanceCounter counter, @Nonnull List<BiomeType> uniqueBiomes, double distanceToBuffer_voxelGrid) {
        for (BiomeType biomeType : uniqueBiomes) {
            if (!counter.isCloserThanCounted(biomeType, distanceToBuffer_voxelGrid)) continue;
            return false;
        }
        return true;
    }

    public static class BiomeDistanceEntries {
        public final List<BiomeDistanceEntry> entries;

        public BiomeDistanceEntries(@Nonnull List<BiomeDistanceEntry> entries) {
            this.entries = entries;
        }

        public double distanceToClosestOtherBiome(@Nonnull BiomeType thisBiome) {
            double smallestDistance = Double.MAX_VALUE;
            for (BiomeDistanceEntry entry : this.entries) {
                if (entry.biomeType == thisBiome) continue;
                smallestDistance = Math.min(smallestDistance, entry.distance_voxelGrid);
            }
            return smallestDistance;
        }
    }

    private static class BiomeDistanceCounter {
        @Nonnull
        final List<BiomeDistanceEntry> entries = new ArrayList<BiomeDistanceEntry>(3);
        @Nullable
        BiomeDistanceEntry cachedEntry = null;

        BiomeDistanceCounter() {
        }

        boolean isCloserThanCounted(@Nonnull BiomeType biomeType, double distance_voxelGrid) {
            for (BiomeDistanceEntry entry : this.entries) {
                if (entry.biomeType != biomeType) continue;
                return distance_voxelGrid < entry.distance_voxelGrid;
            }
            return true;
        }

        void accountFor(@Nonnull BiomeType biomeType, double distance_voxelGrid) {
            if (this.cachedEntry != null && this.cachedEntry.biomeType == biomeType) {
                if (this.cachedEntry.distance_voxelGrid <= distance_voxelGrid) {
                    return;
                }
                this.cachedEntry.distance_voxelGrid = distance_voxelGrid;
                return;
            }
            for (BiomeDistanceEntry entry : this.entries) {
                if (entry.biomeType != biomeType) continue;
                this.cachedEntry = entry;
                if (entry.distance_voxelGrid <= distance_voxelGrid) {
                    return;
                }
                entry.distance_voxelGrid = distance_voxelGrid;
                return;
            }
            BiomeDistanceEntry entry = new BiomeDistanceEntry();
            entry.biomeType = biomeType;
            entry.distance_voxelGrid = distance_voxelGrid;
            this.entries.add(entry);
            this.cachedEntry = entry;
        }
    }

    public static class BiomeDistanceEntry {
        public BiomeType biomeType;
        public double distance_voxelGrid;
    }
}

