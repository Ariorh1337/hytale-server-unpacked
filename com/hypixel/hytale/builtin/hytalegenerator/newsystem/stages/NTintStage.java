/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.newsystem.stages;

import com.hypixel.hytale.builtin.hytalegenerator.Registry;
import com.hypixel.hytale.builtin.hytalegenerator.biome.Biome;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.GridUtils;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.NBufferBundle;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NCountedPixelBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NSimplePixelBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type.NBufferType;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type.NParametrizedBufferType;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.stages.NStage;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.views.NPixelBufferView;
import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import com.hypixel.hytale.builtin.hytalegenerator.tintproviders.TintProvider;
import com.hypixel.hytale.builtin.hytalegenerator.worldstructure.WorldStructure;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public class NTintStage
implements NStage {
    @Nonnull
    public static final Class<NCountedPixelBuffer> biomeBufferClass = NCountedPixelBuffer.class;
    @Nonnull
    public static final Class<Integer> biomeClass = Integer.class;
    @Nonnull
    public static final Class<NSimplePixelBuffer> tintBufferClass = NSimplePixelBuffer.class;
    @Nonnull
    public static final Class<Integer> tintClass = Integer.class;
    @Nonnull
    private final NParametrizedBufferType biomeInputBufferType;
    @Nonnull
    private final NParametrizedBufferType tintOutputBufferType;
    @Nonnull
    private final Bounds3i inputBounds_bufferGrid;
    @Nonnull
    private final String stageName;
    @Nonnull
    private final WorkerIndexer.Data<WorldStructure> worldStructure_workerData;

    public NTintStage(@Nonnull String stageName, @Nonnull NParametrizedBufferType biomeInputBufferType, @Nonnull NParametrizedBufferType tintOutputBufferType, @Nonnull WorkerIndexer.Data<WorldStructure> worldStructure_workerData) {
        assert (biomeInputBufferType.isValidType(biomeBufferClass, biomeClass));
        assert (tintOutputBufferType.isValidType(tintBufferClass, tintClass));
        this.biomeInputBufferType = biomeInputBufferType;
        this.tintOutputBufferType = tintOutputBufferType;
        this.stageName = stageName;
        this.worldStructure_workerData = worldStructure_workerData;
        this.inputBounds_bufferGrid = GridUtils.createUnitBounds3i(Vector3i.ZERO);
    }

    @Override
    public void run(@Nonnull NStage.Context context) {
        NBufferBundle.Access.View biomeAccess = context.bufferAccess.get(this.biomeInputBufferType);
        NPixelBufferView<Integer> biomeSpace = new NPixelBufferView<Integer>(biomeAccess, biomeClass);
        NBufferBundle.Access.View tintAccess = context.bufferAccess.get(this.tintOutputBufferType);
        NPixelBufferView<Integer> tintSpace = new NPixelBufferView<Integer>(tintAccess, tintClass);
        Bounds3i outputBounds_voxelGrid = tintSpace.getBounds();
        Registry<Biome> biomeRegistry = this.worldStructure_workerData.get(context.workerId).getBiomeRegistry();
        Vector3i position_voxelGrid = new Vector3i(outputBounds_voxelGrid.min);
        position_voxelGrid.setY(0);
        TintProvider.Context tintContext = new TintProvider.Context(position_voxelGrid, context.workerId);
        position_voxelGrid.x = outputBounds_voxelGrid.min.x;
        while (position_voxelGrid.x < outputBounds_voxelGrid.max.x) {
            position_voxelGrid.z = outputBounds_voxelGrid.min.z;
            while (position_voxelGrid.z < outputBounds_voxelGrid.max.z) {
                Integer biomeId = biomeSpace.getContent(position_voxelGrid.x, 0, position_voxelGrid.z);
                assert (biomeId != null);
                Biome biome = biomeRegistry.getObject(biomeId);
                assert (biome != null);
                TintProvider tintProvider = biome.getTintProvider();
                TintProvider.Result tintResult = tintProvider.getValue(tintContext);
                if (!tintResult.hasValue) {
                    tintSpace.set(TintProvider.DEFAULT_TINT, position_voxelGrid);
                } else {
                    tintSpace.set(tintResult.tint, position_voxelGrid);
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
        return List.of(this.tintOutputBufferType);
    }

    @Override
    @Nonnull
    public String getName() {
        return this.stageName;
    }
}

