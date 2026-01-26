/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.props;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.conveyor.stagedconveyor.ContextDependency;
import com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace.VoxelSpace;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
import com.hypixel.hytale.builtin.hytalegenerator.props.ScanResult;
import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class UnionProp
extends Prop {
    @Nonnull
    private final List<Prop> props;
    @Nonnull
    private final ContextDependency contextDependency;
    @Nonnull
    private final Bounds3i readBounds_voxelGrid;
    @Nonnull
    private final Bounds3i writeBounds_voxelGrid;

    public UnionProp(@Nonnull List<Prop> propChain) {
        this.props = new ArrayList<Prop>(propChain);
        this.readBounds_voxelGrid = new Bounds3i();
        this.writeBounds_voxelGrid = new Bounds3i();
        Vector3i writeRange = new Vector3i();
        Vector3i readRange = new Vector3i();
        for (Prop prop : propChain) {
            writeRange = Vector3i.max(writeRange, prop.getContextDependency().getWriteRange());
            readRange = Vector3i.max(readRange, prop.getContextDependency().getReadRange());
            this.readBounds_voxelGrid.encompass(prop.getReadBounds_voxelGrid());
            this.writeBounds_voxelGrid.encompass(prop.getWriteBounds_voxelGrid());
        }
        this.contextDependency = new ContextDependency(readRange, writeRange);
    }

    @Override
    @Nonnull
    public ScanResult scan(@Nonnull Vector3i position, @Nonnull VoxelSpace<Material> materialSpace, @Nonnull WorkerIndexer.Id id) {
        ChainedScanResult scanResult = new ChainedScanResult();
        scanResult.scanResults = new ArrayList<ScanResult>(this.props.size());
        for (Prop prop : this.props) {
            scanResult.scanResults.add(prop.scan(position, materialSpace, id));
        }
        return scanResult;
    }

    @Override
    public void place(@Nonnull Prop.Context context) {
        List<ScanResult> scanResults = ChainedScanResult.cast((ScanResult)context.scanResult).scanResults;
        for (int i = 0; i < this.props.size(); ++i) {
            Prop prop = this.props.get(i);
            Prop.Context childContext = new Prop.Context(context);
            childContext.scanResult = scanResults.get(i);
            prop.place(childContext);
        }
    }

    @Override
    @Nonnull
    public ContextDependency getContextDependency() {
        return this.contextDependency.clone();
    }

    @Override
    @NonNullDecl
    public Bounds3i getReadBounds_voxelGrid() {
        return this.readBounds_voxelGrid;
    }

    @Override
    @Nonnull
    public Bounds3i getWriteBounds_voxelGrid() {
        return this.writeBounds_voxelGrid;
    }

    private static class ChainedScanResult
    implements ScanResult {
        List<ScanResult> scanResults;

        private ChainedScanResult() {
        }

        @Nonnull
        public static ChainedScanResult cast(ScanResult scanResult) {
            if (!(scanResult instanceof ChainedScanResult)) {
                throw new IllegalArgumentException("The provided ScanResult isn't compatible with this prop.");
            }
            return (ChainedScanResult)scanResult;
        }

        @Override
        public boolean isNegative() {
            return this.scanResults == null || this.scanResults.isEmpty();
        }
    }
}

