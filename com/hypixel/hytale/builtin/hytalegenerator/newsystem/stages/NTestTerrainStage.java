/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.newsystem.stages;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.material.SolidMaterial;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.NBufferBundle;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NVoxelBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type.NBufferType;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type.NParametrizedBufferType;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.stages.NStage;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.views.NVoxelBufferView;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.procedurallib.logic.SimplexNoise;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public class NTestTerrainStage
implements NStage {
    private static final Class<NVoxelBuffer> bufferClass = NVoxelBuffer.class;
    private static final Class<SolidMaterial> solidMaterialClass = SolidMaterial.class;
    private final NParametrizedBufferType outputBufferType;
    private final SolidMaterial ground;
    private final SolidMaterial empty;

    public NTestTerrainStage(@Nonnull NBufferType outputBufferType, @Nonnull SolidMaterial groundMaterial, @Nonnull SolidMaterial emptyMaterial) {
        assert (outputBufferType instanceof NParametrizedBufferType);
        this.outputBufferType = (NParametrizedBufferType)outputBufferType;
        assert (this.outputBufferType.isValidType(bufferClass, solidMaterialClass));
        this.ground = groundMaterial;
        this.empty = emptyMaterial;
    }

    @Override
    public void run(@Nonnull NStage.Context context) {
        NBufferBundle.Access.View access = context.bufferAccess.get(this.outputBufferType);
        NVoxelBufferView<SolidMaterial> materialBuffer = new NVoxelBufferView<SolidMaterial>(access, solidMaterialClass);
        SimplexNoise noise = SimplexNoise.INSTANCE;
        Vector3i position = new Vector3i();
        position.x = materialBuffer.minX();
        while (position.x < materialBuffer.maxX()) {
            position.z = materialBuffer.minZ();
            while (position.z < materialBuffer.maxZ()) {
                position.y = materialBuffer.minY();
                while (position.y < materialBuffer.maxY()) {
                    Vector3d noisePosition = position.toVector3d();
                    noisePosition.scale(0.05);
                    double noiseValue = noise.get(1, 0, noisePosition.x, noisePosition.y, noisePosition.z);
                    if (position.y < 130 || noiseValue > 0.0 && position.y < 150) {
                        materialBuffer.set(this.ground, position);
                    } else {
                        materialBuffer.set(this.empty, position);
                    }
                    ++position.y;
                }
                ++position.z;
            }
            ++position.x;
        }
    }

    @Override
    @Nonnull
    public Map<NBufferType, Bounds3i> getInputTypesAndBounds_bufferGrid() {
        return Map.of();
    }

    @Override
    @Nonnull
    public List<NBufferType> getOutputTypes() {
        return List.of(this.outputBufferType);
    }

    @Override
    @Nonnull
    public String getName() {
        return "TestTerrainStage";
    }
}

