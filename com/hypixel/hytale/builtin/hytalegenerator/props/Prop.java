/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.props;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.conveyor.stagedconveyor.ContextDependency;
import com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace.VoxelSpace;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.views.EntityContainer;
import com.hypixel.hytale.builtin.hytalegenerator.props.ScanResult;
import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import com.hypixel.hytale.math.vector.Vector3i;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public abstract class Prop {
    public abstract ScanResult scan(@Nonnull Vector3i var1, @Nonnull VoxelSpace<Material> var2, @Nonnull WorkerIndexer.Id var3);

    public abstract void place(@Nonnull Context var1);

    public abstract ContextDependency getContextDependency();

    @Nonnull
    public abstract Bounds3i getReadBounds_voxelGrid();

    @Nonnull
    public abstract Bounds3i getWriteBounds_voxelGrid();

    @Nonnull
    public static Prop noProp() {
        final ScanResult scanResult = new ScanResult(){

            @Override
            public boolean isNegative() {
                return true;
            }
        };
        final ContextDependency contextDependency = new ContextDependency(new Vector3i(), new Vector3i());
        final Bounds3i zeroBounds_voxelGrid = new Bounds3i();
        return new Prop(){

            @Override
            @Nonnull
            public ScanResult scan(@Nonnull Vector3i position, @Nonnull VoxelSpace<Material> materialSpace, @Nonnull WorkerIndexer.Id id) {
                return scanResult;
            }

            @Override
            public void place(@Nonnull Context context) {
            }

            @Override
            @Nonnull
            public ContextDependency getContextDependency() {
                return contextDependency;
            }

            @Override
            @NonNullDecl
            public Bounds3i getReadBounds_voxelGrid() {
                return zeroBounds_voxelGrid;
            }

            @Override
            @Nonnull
            public Bounds3i getWriteBounds_voxelGrid() {
                return zeroBounds_voxelGrid;
            }
        };
    }

    public static class Context {
        public ScanResult scanResult;
        public VoxelSpace<Material> materialSpace;
        public EntityContainer entityBuffer;
        public WorkerIndexer.Id workerId;
        public double distanceFromBiomeEdge;

        public Context(@Nonnull ScanResult scanResult, @Nonnull VoxelSpace<Material> materialSpace, @Nonnull EntityContainer entityBuffer, WorkerIndexer.Id workerId, double distanceFromBiomeEdge) {
            this.scanResult = scanResult;
            this.materialSpace = materialSpace;
            this.entityBuffer = entityBuffer;
            this.workerId = workerId;
            this.distanceFromBiomeEdge = distanceFromBiomeEdge;
        }

        public Context(@Nonnull Context other) {
            this.scanResult = other.scanResult;
            this.materialSpace = other.materialSpace;
            this.entityBuffer = other.entityBuffer;
            this.workerId = other.workerId;
            this.distanceFromBiomeEdge = other.distanceFromBiomeEdge;
        }
    }
}

