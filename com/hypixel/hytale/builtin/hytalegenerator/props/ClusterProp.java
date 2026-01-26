/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.props;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.conveyor.stagedconveyor.ContextDependency;
import com.hypixel.hytale.builtin.hytalegenerator.datastructures.WeightedMap;
import com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace.VoxelSpace;
import com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace.WindowVoxelSpace;
import com.hypixel.hytale.builtin.hytalegenerator.framework.math.Calculator;
import com.hypixel.hytale.builtin.hytalegenerator.framework.math.SeedGenerator;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.views.EntityContainer;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.builtin.hytalegenerator.props.PositionListScanResult;
import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
import com.hypixel.hytale.builtin.hytalegenerator.props.ScanResult;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.Scanner;
import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import com.hypixel.hytale.math.util.FastRandom;
import com.hypixel.hytale.math.vector.Vector3i;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import java.util.List;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ClusterProp
extends Prop {
    private final Double2DoubleFunction weightCurve;
    private final SeedGenerator seedGenerator;
    private final WeightedMap<Prop> propWeightedMap;
    private final int range;
    private final ContextDependency contextDependency;
    private final Pattern pattern;
    private final Scanner scanner;
    private final Bounds3i readBounds_voxelGrid;
    private final Bounds3i writeBounds_voxelGrid;

    public ClusterProp(int range, @Nonnull Double2DoubleFunction weightCurve, int seed, @Nonnull WeightedMap<Prop> propWeightedMap, @Nonnull Pattern pattern, @Nonnull Scanner scanner) {
        if (range < 0) {
            throw new IllegalArgumentException("negative range");
        }
        this.range = range;
        this.seedGenerator = new SeedGenerator(seed);
        this.weightCurve = weightCurve;
        this.pattern = pattern;
        this.scanner = scanner;
        this.propWeightedMap = new WeightedMap();
        propWeightedMap.forEach((prop, weight) -> {
            ContextDependency contextDependency = prop.getContextDependency();
            Vector3i readRange = contextDependency.getReadRange();
            Vector3i writeRange = contextDependency.getWriteRange();
            if (readRange.x > 0 || readRange.z > 0 || writeRange.x > 0 || writeRange.z > 0) {
                return;
            }
            this.propWeightedMap.add((Prop)prop, propWeightedMap.get((Prop)prop));
        });
        Vector3i readRange = scanner.readSpaceWith(pattern).getRange();
        this.readBounds_voxelGrid = scanner.readSpaceWith(pattern).toBounds3i();
        Vector3i writeRange = new Vector3i(range + readRange.x, 0, range + readRange.z);
        this.contextDependency = new ContextDependency(readRange, writeRange);
        this.writeBounds_voxelGrid = this.contextDependency.getWriteBounds_voxelGrid();
    }

    @Override
    public PositionListScanResult scan(@Nonnull Vector3i position, @Nonnull VoxelSpace<Material> materialSpace, @Nonnull WorkerIndexer.Id id) {
        Scanner.Context scannerContext = new Scanner.Context(position, this.pattern, materialSpace, id);
        List<Vector3i> validPositions = this.scanner.scan(scannerContext);
        return new PositionListScanResult(validPositions);
    }

    @Override
    public void place(@Nonnull Prop.Context context) {
        List<Vector3i> positions = PositionListScanResult.cast(context.scanResult).getPositions();
        if (positions == null) {
            return;
        }
        for (Vector3i position : positions) {
            this.place(position, context.materialSpace, context.entityBuffer, context.workerId, context.distanceFromBiomeEdge);
        }
    }

    private void place(@Nonnull Vector3i position, @Nonnull VoxelSpace<Material> materialSpace, @Nonnull EntityContainer entityBuffer, @Nonnull WorkerIndexer.Id id, double distanceFromBiomeEdge) {
        WindowVoxelSpace<Material> columnSpace = new WindowVoxelSpace<Material>(materialSpace);
        FastRandom random = new FastRandom(this.seedGenerator.seedAt(position.x, position.z));
        for (int x = position.x - this.range; x < position.x + this.range; ++x) {
            for (int z = position.z - this.range; z < position.z + this.range; ++z) {
                double distance = Calculator.distance(x, z, position.x, position.z);
                double density = this.weightCurve.get(distance);
                if (random.nextDouble() > density) continue;
                Prop pickedProp = this.propWeightedMap.pick(random);
                if (!materialSpace.isInsideSpace(x, materialSpace.minY(), z)) continue;
                columnSpace.setWindow(x, materialSpace.minY(), z, x + 1, materialSpace.maxY(), z + 1);
                ScanResult propScanResult = pickedProp.scan(new Vector3i(x, position.y, z), columnSpace, id);
                Prop.Context childContext = new Prop.Context(propScanResult, columnSpace, entityBuffer, id, distanceFromBiomeEdge);
                pickedProp.place(childContext);
            }
        }
    }

    @Override
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
}

