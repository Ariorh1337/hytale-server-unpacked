/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.props;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.conveyor.stagedconveyor.ContextDependency;
import com.hypixel.hytale.builtin.hytalegenerator.datastructures.WeightedMap;
import com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace.VoxelSpace;
import com.hypixel.hytale.builtin.hytalegenerator.framework.math.SeedGenerator;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
import com.hypixel.hytale.builtin.hytalegenerator.props.ScanResult;
import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.Random;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class WeightedProp
extends Prop {
    @Nonnull
    private final WeightedMap<Prop> props;
    @Nonnull
    private final ContextDependency contextDependency;
    @Nonnull
    private final Bounds3i readBounds_voxelGrid;
    @Nonnull
    private final Bounds3i writeBounds_voxelGrid;
    @Nonnull
    private final SeedGenerator seedGenerator;

    public WeightedProp(@Nonnull WeightedMap<Prop> props, int seed) {
        this.props = new WeightedMap<Prop>(props);
        this.readBounds_voxelGrid = new Bounds3i();
        this.writeBounds_voxelGrid = new Bounds3i();
        this.seedGenerator = new SeedGenerator(seed);
        Vector3i writeRange = new Vector3i();
        Vector3i readRange = new Vector3i();
        for (Prop prop : this.props.allElements()) {
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
        if (this.props.size() == 0) {
            return new PickedScanResult();
        }
        Random rand = new Random(this.seedGenerator.seedAt(position.x, position.y, position.z));
        Prop pickedProp = this.props.pick(rand);
        ScanResult scanResult = pickedProp.scan(position, materialSpace, id);
        PickedScanResult pickedScanResult = new PickedScanResult();
        pickedScanResult.prop = pickedProp;
        pickedScanResult.scanResult = scanResult;
        return pickedScanResult;
    }

    @Override
    public void place(@Nonnull Prop.Context context) {
        if (context.scanResult.isNegative()) {
            return;
        }
        PickedScanResult pickedScanResult = PickedScanResult.cast(context.scanResult);
        Prop.Context childContext = new Prop.Context(context);
        childContext.scanResult = pickedScanResult.scanResult;
        pickedScanResult.prop.place(childContext);
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

    private static class PickedScanResult
    implements ScanResult {
        ScanResult scanResult;
        Prop prop;

        private PickedScanResult() {
        }

        @Nonnull
        public static PickedScanResult cast(ScanResult scanResult) {
            if (!(scanResult instanceof PickedScanResult)) {
                throw new IllegalArgumentException("The provided ScanResult isn't compatible with this prop.");
            }
            return (PickedScanResult)scanResult;
        }

        @Override
        public boolean isNegative() {
            return this.scanResult == null || this.scanResult.isNegative();
        }
    }
}

