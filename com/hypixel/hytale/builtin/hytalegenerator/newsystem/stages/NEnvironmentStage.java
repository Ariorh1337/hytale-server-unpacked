/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.newsystem.stages;

import com.hypixel.hytale.builtin.hytalegenerator.Registry;
import com.hypixel.hytale.builtin.hytalegenerator.biome.Biome;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.environmentproviders.EnvironmentProvider;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.GridUtils;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.NBufferBundle;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NCountedPixelBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NVoxelBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type.NBufferType;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type.NParametrizedBufferType;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.stages.NStage;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.views.NPixelBufferView;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.views.NVoxelBufferView;
import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import com.hypixel.hytale.builtin.hytalegenerator.worldstructure.WorldStructure;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public class NEnvironmentStage
implements NStage {
    @Nonnull
    public static final Class<NCountedPixelBuffer> biomeBufferClass = NCountedPixelBuffer.class;
    @Nonnull
    public static final Class<Integer> biomeTypeClass = Integer.class;
    @Nonnull
    public static final Class<NVoxelBuffer> environmentBufferClass = NVoxelBuffer.class;
    @Nonnull
    public static final Class<Integer> environmentClass = Integer.class;
    @Nonnull
    private final NParametrizedBufferType biomeInputBufferType;
    @Nonnull
    private final NParametrizedBufferType environmentOutputBufferType;
    @Nonnull
    private final Bounds3i inputBounds_bufferGrid;
    @Nonnull
    private final String stageName;
    @Nonnull
    private final WorkerIndexer.Data<WorldStructure> worldStructure_workerData;

    public NEnvironmentStage(@Nonnull String stageName, @Nonnull NParametrizedBufferType biomeInputBufferType, @Nonnull NParametrizedBufferType environmentOutputBufferType, @Nonnull WorkerIndexer.Data<WorldStructure> worldStructure_workerData) {
        assert (biomeInputBufferType.isValidType(biomeBufferClass, biomeTypeClass));
        assert (environmentOutputBufferType.isValidType(environmentBufferClass, environmentClass));
        this.biomeInputBufferType = biomeInputBufferType;
        this.environmentOutputBufferType = environmentOutputBufferType;
        this.stageName = stageName;
        this.worldStructure_workerData = worldStructure_workerData;
        this.inputBounds_bufferGrid = GridUtils.createUnitBounds3i(Vector3i.ZERO);
    }

    @Override
    public void run(@Nonnull NStage.Context context) {
        NBufferBundle.Access.View biomeAccess = context.bufferAccess.get(this.biomeInputBufferType);
        NPixelBufferView<Integer> biomeSpace = new NPixelBufferView<Integer>(biomeAccess, biomeTypeClass);
        NBufferBundle.Access.View environmentAccess = context.bufferAccess.get(this.environmentOutputBufferType);
        NVoxelBufferView<Integer> environmentSpace = new NVoxelBufferView<Integer>(environmentAccess, environmentClass);
        Bounds3i outputBounds_voxelGrid = environmentSpace.getBounds();
        Vector3i position_voxelGrid = new Vector3i(outputBounds_voxelGrid.min);
        EnvironmentProvider.Context environmentContext = new EnvironmentProvider.Context(position_voxelGrid);
        Registry<Biome> biomeRegistry = this.worldStructure_workerData.get(context.workerId).getBiomeRegistry();
        position_voxelGrid.x = outputBounds_voxelGrid.min.x;
        while (position_voxelGrid.x < outputBounds_voxelGrid.max.x) {
            position_voxelGrid.z = outputBounds_voxelGrid.min.z;
            while (position_voxelGrid.z < outputBounds_voxelGrid.max.z) {
                Integer biomeId = biomeSpace.getContent(position_voxelGrid.x, 0, position_voxelGrid.z);
                assert (biomeId != null);
                Biome biome = biomeRegistry.getObject(biomeId);
                assert (biome != null);
                EnvironmentProvider environmentProvider = biome.getEnvironmentProvider();
                position_voxelGrid.y = outputBounds_voxelGrid.min.y;
                while (position_voxelGrid.y < outputBounds_voxelGrid.max.y) {
                    position_voxelGrid.dropHash();
                    int environment = environmentProvider.getValue(environmentContext);
                    environmentSpace.set(environment, position_voxelGrid);
                    ++position_voxelGrid.y;
                }
                ++position_voxelGrid.z;
            }
            ++position_voxelGrid.x;
        }
    }

    @Override
    @Nonnull
    public Map<NBufferType, Bounds3i> getInputTypesAndBounds_bufferGrid() {
        return Map.of(this.biomeInputBufferType, this.inputBounds_bufferGrid);
    }

    @Override
    @Nonnull
    public List<NBufferType> getOutputTypes() {
        return List.of(this.environmentOutputBufferType);
    }

    @Override
    @Nonnull
    public String getName() {
        return this.stageName;
    }
}

