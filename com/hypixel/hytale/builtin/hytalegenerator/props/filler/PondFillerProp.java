/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.props.filler;

import com.hypixel.hytale.builtin.hytalegenerator.MaterialSet;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.SpaceSize;
import com.hypixel.hytale.builtin.hytalegenerator.conveyor.stagedconveyor.ContextDependency;
import com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace.ArrayVoxelSpace;
import com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace.VoxelSpace;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
import com.hypixel.hytale.builtin.hytalegenerator.props.filler.FillerPropScanResult;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.Scanner;
import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class PondFillerProp
extends Prop {
    private static final int TRAVERSED = 1;
    private static final int LEAKS = 16;
    private static final int SOLID = 256;
    private static final int STACKED = 4096;
    private final Vector3i boundingMin;
    private final Vector3i boundingMax;
    private final MaterialProvider<Material> filledMaterialProvider;
    private final MaterialSet solidSet;
    private final Scanner scanner;
    private final Pattern pattern;
    private final ContextDependency contextDependency;
    private final Bounds3i readBounds_voxelGrid;
    private final Bounds3i writeBounds_voxelGrid;

    public PondFillerProp(@Nonnull Vector3i boundingMin, @Nonnull Vector3i boundingMax, @Nonnull MaterialSet solidSet, @Nonnull MaterialProvider<Material> filledMaterialProvider, @Nonnull Scanner scanner, @Nonnull Pattern pattern) {
        this.boundingMin = boundingMin.clone();
        this.boundingMax = boundingMax.clone();
        this.solidSet = solidSet;
        this.filledMaterialProvider = filledMaterialProvider;
        this.scanner = scanner;
        this.pattern = pattern;
        SpaceSize boundingSpace = new SpaceSize(boundingMin, boundingMax);
        boundingSpace = SpaceSize.stack(boundingSpace, scanner.readSpaceWith(pattern));
        SpaceSize.stack(scanner.readSpaceWith(pattern), boundingSpace);
        Vector3i range = boundingSpace.getRange();
        this.contextDependency = new ContextDependency(range, range);
        this.readBounds_voxelGrid = this.contextDependency.getReadBounds_voxelGrid();
        this.writeBounds_voxelGrid = this.contextDependency.getWriteBounds_voxelGrid();
    }

    @Override
    public FillerPropScanResult scan(@Nonnull Vector3i position, @Nonnull VoxelSpace<Material> materialSpace, @Nonnull WorkerIndexer.Id id) {
        Scanner.Context scannerContext = new Scanner.Context(position, this.pattern, materialSpace, id);
        List<Vector3i> scanResults = this.scanner.scan(scannerContext);
        if (scanResults.size() == 1) {
            List<Vector3i> resultList = this.renderFluidBlocks(scanResults.getFirst(), materialSpace);
            return new FillerPropScanResult(resultList);
        }
        ArrayList<Vector3i> resultList = new ArrayList<Vector3i>();
        for (Vector3i scanPosition : scanResults) {
            List<Vector3i> renderResult = this.renderFluidBlocks(scanPosition, materialSpace);
            resultList.addAll(renderResult);
        }
        return new FillerPropScanResult(resultList);
    }

    private List<Vector3i> renderFluidBlocks(@Nonnull Vector3i origin, @Nonnull VoxelSpace<Material> materialSpace) {
        int z;
        int x;
        int contextMaterialHash;
        Material material;
        Vector3i min = this.boundingMin.clone().add(origin);
        Vector3i max = this.boundingMax.clone().add(origin);
        min = Vector3i.max(min, new Vector3i(materialSpace.minX(), materialSpace.minY(), materialSpace.minZ()));
        max = Vector3i.min(max, new Vector3i(materialSpace.maxX(), materialSpace.maxY(), materialSpace.maxZ()));
        ArrayVoxelSpace<Integer> mask = new ArrayVoxelSpace<Integer>(max.x - min.x, max.y - min.y, max.z - min.z);
        mask.setOrigin(-min.x, -min.y, -min.z);
        mask.set(0);
        int y = min.y;
        for (int x2 = min.x; x2 < max.x; ++x2) {
            for (int z2 = min.z; z2 < max.z; ++z2) {
                material = materialSpace.getContent(x2, y, z2);
                contextMaterialHash = material.hashMaterialIds();
                int maskValue = 1;
                if (this.solidSet.test(contextMaterialHash)) {
                    mask.set(maskValue |= 0x100, x2, y, z2);
                    continue;
                }
                mask.set(maskValue |= 0x10, x2, y, z2);
            }
        }
        for (y = min.y + 1; y < max.y; ++y) {
            int underY = y - 1;
            for (x = min.x; x < max.x; ++x) {
                for (z = min.z; z < max.z; ++z) {
                    if (PondFillerProp.isTraversed((Integer)mask.getContent(x, y, z))) continue;
                    int maskValueUnder = (Integer)mask.getContent(x, underY, z);
                    material = materialSpace.getContent(x, y, z);
                    contextMaterialHash = material.hashMaterialIds();
                    if (this.solidSet.test(contextMaterialHash)) {
                        int maskValue = 0;
                        maskValue |= 1;
                        mask.set(maskValue |= 0x100, x, y, z);
                        continue;
                    }
                    if (!PondFillerProp.isLeaks(maskValueUnder) && x != min.x && x != max.x - 1 && z != min.z && z != max.z - 1) continue;
                    ArrayDeque<Vector3i> stack = new ArrayDeque<Vector3i>();
                    stack.push(new Vector3i(x, y, z));
                    mask.set(4096, x, y, z);
                    while (!stack.isEmpty()) {
                        int poppedMaskValue;
                        Vector3i poppedPos = (Vector3i)stack.pop();
                        int maskValue = (Integer)mask.getContent(poppedPos.x, poppedPos.y, poppedPos.z);
                        mask.set(maskValue |= 0x10, poppedPos.x, poppedPos.y, poppedPos.z);
                        --poppedPos.x;
                        if (mask.isInsideSpace(poppedPos.x, poppedPos.y, poppedPos.z) && !PondFillerProp.isStacked(poppedMaskValue = ((Integer)mask.getContent(poppedPos.x, poppedPos.y, poppedPos.z)).intValue()) && !this.solidSet.test(contextMaterialHash = (material = materialSpace.getContent(poppedPos.x, poppedPos.y, poppedPos.z)).hashMaterialIds())) {
                            stack.push(poppedPos.clone());
                            mask.set(0x1000 | poppedMaskValue, poppedPos.x, poppedPos.y, poppedPos.z);
                        }
                        poppedPos.x += 2;
                        if (mask.isInsideSpace(poppedPos.x, poppedPos.y, poppedPos.z) && !PondFillerProp.isStacked(poppedMaskValue = ((Integer)mask.getContent(poppedPos.x, poppedPos.y, poppedPos.z)).intValue()) && !this.solidSet.test(contextMaterialHash = (material = materialSpace.getContent(poppedPos.x, poppedPos.y, poppedPos.z)).hashMaterialIds())) {
                            stack.push(poppedPos.clone());
                            mask.set(0x1000 | poppedMaskValue, poppedPos.x, poppedPos.y, poppedPos.z);
                        }
                        --poppedPos.x;
                        --poppedPos.z;
                        if (mask.isInsideSpace(poppedPos.x, poppedPos.y, poppedPos.z) && !PondFillerProp.isStacked(poppedMaskValue = ((Integer)mask.getContent(poppedPos.x, poppedPos.y, poppedPos.z)).intValue()) && !this.solidSet.test(contextMaterialHash = (material = materialSpace.getContent(poppedPos.x, y, poppedPos.z)).hashMaterialIds())) {
                            stack.push(poppedPos.clone());
                            mask.set(0x1000 | poppedMaskValue, poppedPos.x, poppedPos.y, poppedPos.z);
                        }
                        poppedPos.z += 2;
                        if (mask.isInsideSpace(poppedPos.x, poppedPos.y, poppedPos.z) && !PondFillerProp.isStacked(poppedMaskValue = ((Integer)mask.getContent(poppedPos.x, poppedPos.y, poppedPos.z)).intValue()) && !this.solidSet.test(contextMaterialHash = (material = materialSpace.getContent(poppedPos.x, poppedPos.y, poppedPos.z)).hashMaterialIds())) {
                            stack.push(poppedPos.clone());
                            mask.set(0x1000 | poppedMaskValue, poppedPos.x, poppedPos.y, poppedPos.z);
                        }
                        --poppedPos.z;
                    }
                }
            }
        }
        ArrayList<Vector3i> fluidBlocks = new ArrayList<Vector3i>();
        for (y = mask.minY() + 1; y < mask.maxY(); ++y) {
            for (x = mask.minX() + 1; x < mask.maxX() - 1; ++x) {
                for (z = mask.minZ() + 1; z < mask.maxZ() - 1; ++z) {
                    int maskValue = (Integer)mask.getContent(x, y, z);
                    if (PondFillerProp.isSolid(maskValue) || PondFillerProp.isLeaks(maskValue)) continue;
                    fluidBlocks.add(new Vector3i(x, y, z));
                }
            }
        }
        return fluidBlocks;
    }

    @Override
    public void place(@Nonnull Prop.Context context) {
        List<Vector3i> fluidBlocks = FillerPropScanResult.cast(context.scanResult).getFluidBlocks();
        if (fluidBlocks == null) {
            return;
        }
        for (Vector3i position : fluidBlocks) {
            MaterialProvider.Context materialsContext;
            Material material;
            if (!context.materialSpace.isInsideSpace(position.x, position.y, position.z) || (material = this.filledMaterialProvider.getVoxelTypeAt(materialsContext = new MaterialProvider.Context(position, 0.0, 0, 0, 0, 0, context.workerId, null, context.distanceFromBiomeEdge))) == null) continue;
            context.materialSpace.set(material, position.x, position.y, position.z);
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

    private static boolean isTraversed(int maskValue) {
        return (maskValue & 1) == 1;
    }

    private static boolean isLeaks(int maskValue) {
        return (maskValue & 0x10) == 16;
    }

    private static boolean isSolid(int maskValue) {
        return (maskValue & 0x100) == 256;
    }

    private static boolean isStacked(int maskValue) {
        return (maskValue & 0x1000) == 4096;
    }
}

