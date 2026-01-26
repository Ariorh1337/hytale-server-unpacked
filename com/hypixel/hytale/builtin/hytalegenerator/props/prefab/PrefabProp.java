/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.props.prefab;

import com.hypixel.hytale.builtin.hytalegenerator.BlockMask;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.SpaceSize;
import com.hypixel.hytale.builtin.hytalegenerator.conveyor.stagedconveyor.ContextDependency;
import com.hypixel.hytale.builtin.hytalegenerator.datastructures.WeightedMap;
import com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace.ArrayVoxelSpace;
import com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace.VoxelSpace;
import com.hypixel.hytale.builtin.hytalegenerator.framework.math.Calculator;
import com.hypixel.hytale.builtin.hytalegenerator.framework.math.SeedGenerator;
import com.hypixel.hytale.builtin.hytalegenerator.material.FluidMaterial;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.material.MaterialCache;
import com.hypixel.hytale.builtin.hytalegenerator.material.SolidMaterial;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.views.EntityContainer;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
import com.hypixel.hytale.builtin.hytalegenerator.props.ScanResult;
import com.hypixel.hytale.builtin.hytalegenerator.props.directionality.Directionality;
import com.hypixel.hytale.builtin.hytalegenerator.props.directionality.RotatedPosition;
import com.hypixel.hytale.builtin.hytalegenerator.props.directionality.RotatedPositionsScanResult;
import com.hypixel.hytale.builtin.hytalegenerator.props.directionality.StaticDirectionality;
import com.hypixel.hytale.builtin.hytalegenerator.props.entity.EntityPlacementData;
import com.hypixel.hytale.builtin.hytalegenerator.props.prefab.MoldingDirection;
import com.hypixel.hytale.builtin.hytalegenerator.props.prefab.PrefabMoldingConfiguration;
import com.hypixel.hytale.builtin.hytalegenerator.props.prefab.PropPrefabUtil;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.OriginScanner;
import com.hypixel.hytale.builtin.hytalegenerator.scanners.Scanner;
import com.hypixel.hytale.builtin.hytalegenerator.seed.SeedBox;
import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import com.hypixel.hytale.common.util.ExceptionUtil;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.prefab.PrefabRotation;
import com.hypixel.hytale.server.core.prefab.selection.buffer.PrefabBufferCall;
import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.IPrefabBuffer;
import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.PrefabBuffer;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class PrefabProp
extends Prop {
    private final WeightedMap<List<PrefabBuffer>> prefabPool;
    private final Scanner scanner;
    private ContextDependency contextDependency;
    private final MaterialCache materialCache;
    private final SeedGenerator seedGenerator;
    private final BlockMask materialMask;
    private final Directionality directionality;
    private final Bounds3i readBounds_voxelGrid;
    private final Bounds3i writeBounds_voxelGrid;
    private final Bounds3i prefabBounds_voxelGrid;
    private final List<PrefabProp> childProps;
    private final List<RotatedPosition> childPositions;
    private final Function<String, List<PrefabBuffer>> childPrefabLoader;
    private final Scanner moldingScanner;
    private final Pattern moldingPattern;
    private final MoldingDirection moldingDirection;
    private final boolean moldChildren;
    private final int prefabId = this.hashCode();
    private boolean loadEntities;

    public PrefabProp(@Nonnull WeightedMap<List<PrefabBuffer>> prefabPool, @Nonnull Scanner scanner, @Nonnull Directionality directionality, @Nonnull MaterialCache materialCache, @Nonnull BlockMask materialMask, @Nonnull PrefabMoldingConfiguration prefabMoldingConfiguration, @Nullable Function<String, List<PrefabBuffer>> childPrefabLoader, @Nonnull SeedBox seedBox, boolean loadEntities) {
        this.prefabPool = prefabPool;
        this.scanner = scanner;
        this.directionality = directionality;
        this.materialCache = materialCache;
        this.seedGenerator = new SeedGenerator(seedBox.createSupplier().get().intValue());
        this.materialMask = materialMask;
        this.loadEntities = loadEntities;
        this.childProps = new ArrayList<PrefabProp>();
        this.childPositions = new ArrayList<RotatedPosition>();
        this.childPrefabLoader = childPrefabLoader == null ? s -> null : childPrefabLoader;
        this.moldingScanner = prefabMoldingConfiguration.moldingScanner;
        this.moldingPattern = prefabMoldingConfiguration.moldingPattern;
        this.moldingDirection = prefabMoldingConfiguration.moldingDirection;
        this.moldChildren = prefabMoldingConfiguration.moldChildren;
        this.contextDependency = new ContextDependency();
        Vector3i readRange = directionality.getReadRangeWith(scanner);
        for (List<PrefabBuffer> prefabList : prefabPool.allElements()) {
            if (prefabList.isEmpty()) {
                throw new IllegalArgumentException("prefab pool contains empty list");
            }
            for (PrefabBuffer prefab : prefabList) {
                if (prefab == null) {
                    throw new IllegalArgumentException("prefab pool contains list with null element");
                }
                PrefabBuffer.PrefabBufferAccessor prefabAccess = prefab.newAccess();
                PrefabBuffer.ChildPrefab[] childPrefabs = prefabAccess.getChildPrefabs();
                int childId = 0;
                for (PrefabBuffer.ChildPrefab child : childPrefabs) {
                    RotatedPosition childPosition = new RotatedPosition(child.getX(), child.getY(), child.getZ(), child.getRotation());
                    String childPath = child.getPath().replace('.', '/');
                    childPath = childPath.replace("*", "");
                    List<PrefabBuffer> childPrefabBuffers = this.childPrefabLoader.apply(childPath);
                    WeightedMap<List<PrefabBuffer>> weightedChildPrefabs = new WeightedMap<List<PrefabBuffer>>();
                    weightedChildPrefabs.add(childPrefabBuffers, 1.0);
                    StaticDirectionality childDirectionality = new StaticDirectionality(child.getRotation(), Pattern.yesPattern());
                    PrefabProp childProp = new PrefabProp(weightedChildPrefabs, OriginScanner.getInstance(), childDirectionality, materialCache, materialMask, this.moldChildren ? prefabMoldingConfiguration : PrefabMoldingConfiguration.none(), childPrefabLoader, seedBox.child(String.valueOf(childId++)), loadEntities);
                    this.childProps.add(childProp);
                    this.childPositions.add(childPosition);
                }
                Vector3i writeRange = this.getWriteRange(prefabAccess);
                for (int i = 0; i < this.childPositions.size(); ++i) {
                    PrefabProp child = this.childProps.get(i);
                    Vector3i position = this.childPositions.get(i).toVector3i();
                    Vector3i childWriteRange = child.getContextDependency().getWriteRange();
                    int maxRange = Calculator.max(position.x, position.y, position.z);
                    writeRange.x = Math.max(writeRange.x, maxRange += Calculator.max(childWriteRange.x, childWriteRange.y, childWriteRange.z));
                    writeRange.y = Math.max(writeRange.y, maxRange);
                    writeRange.z = Math.max(writeRange.z, maxRange);
                }
                ContextDependency contextDependency = new ContextDependency(readRange, writeRange);
                this.contextDependency = ContextDependency.mostOf(this.contextDependency, contextDependency);
                prefabAccess.release();
            }
        }
        this.readBounds_voxelGrid = this.contextDependency.getReadBounds_voxelGrid();
        this.writeBounds_voxelGrid = this.contextDependency.getWriteBounds_voxelGrid();
        this.prefabBounds_voxelGrid = new Bounds3i();
        this.prefabBounds_voxelGrid.min.assign(this.contextDependency.getWriteRange()).scale(-1);
        this.prefabBounds_voxelGrid.max.assign(this.contextDependency.getWriteRange()).add(Vector3i.ALL_ONES);
    }

    private Vector3i getWriteRange(PrefabBuffer.PrefabBufferAccessor prefabAccess) {
        SpaceSize space = new SpaceSize();
        for (PrefabRotation rotation : this.directionality.getPossibleRotations()) {
            Vector3i max = PropPrefabUtil.getMax(prefabAccess, rotation);
            max.add(1, 1, 1);
            Vector3i min = PropPrefabUtil.getMin(prefabAccess, rotation);
            space = SpaceSize.merge(space, new SpaceSize(min, max));
        }
        space = SpaceSize.stack(space, this.scanner.readSpaceWith(this.directionality.getGeneralPattern()));
        return space.getRange();
    }

    @Override
    public ScanResult scan(@Nonnull Vector3i position, @Nonnull VoxelSpace<Material> materialSpace, @Nonnull WorkerIndexer.Id id) {
        Scanner.Context scannerContext = new Scanner.Context(position, this.directionality.getGeneralPattern(), materialSpace, id);
        List<Vector3i> validPositions = this.scanner.scan(scannerContext);
        Vector3i patternPosition = new Vector3i();
        Pattern.Context patternContext = new Pattern.Context(patternPosition, materialSpace, id);
        RotatedPositionsScanResult scanResult = new RotatedPositionsScanResult(new ArrayList<RotatedPosition>());
        for (Vector3i validPosition : validPositions) {
            patternPosition.assign(validPosition);
            PrefabRotation rotation = this.directionality.getRotationAt(patternContext);
            if (rotation == null) continue;
            scanResult.positions.add(new RotatedPosition(validPosition.x, validPosition.y, validPosition.z, rotation));
        }
        return scanResult;
    }

    @Override
    public void place(@Nonnull Prop.Context context) {
        if (this.prefabPool.size() == 0) {
            return;
        }
        List<RotatedPosition> positions = RotatedPositionsScanResult.cast((ScanResult)context.scanResult).positions;
        if (positions == null) {
            return;
        }
        Bounds3i writeSpaceBounds_voxelGrid = context.materialSpace.getBounds();
        for (RotatedPosition position : positions) {
            Bounds3i localPrefabWriteBounds_voxelGrid = this.prefabBounds_voxelGrid.clone().offset(position.toVector3i());
            if (!localPrefabWriteBounds_voxelGrid.intersects(writeSpaceBounds_voxelGrid)) continue;
            this.place(position, context.materialSpace, context.entityBuffer, context.workerId);
        }
    }

    private PrefabBuffer pickPrefab(Random rand) {
        List<PrefabBuffer> list = this.prefabPool.pick(rand);
        int randomIndex = rand.nextInt(list.size());
        return list.get(randomIndex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void place(RotatedPosition position, @Nonnull VoxelSpace<Material> materialSpace, @Nonnull EntityContainer entityBuffer, @Nonnull WorkerIndexer.Id id) {
        Random random = new Random(this.seedGenerator.seedAt(position.x, position.y, position.z));
        PrefabBufferCall callInstance = new PrefabBufferCall(random, position.rotation);
        PrefabBuffer prefab = this.pickPrefab(random);
        PrefabBuffer.PrefabBufferAccessor prefabAccess = prefab.newAccess();
        ArrayVoxelSpace<Integer> moldingOffsets = null;
        if (this.moldingDirection != MoldingDirection.NONE) {
            int prefabMinX = prefabAccess.getMinX(position.rotation);
            int prefabMinZ = prefabAccess.getMinZ(position.rotation);
            int prefabMaxX = prefabAccess.getMaxX(position.rotation);
            int prefabMaxZ = prefabAccess.getMaxZ(position.rotation);
            int prefabSizeX = prefabMaxX - prefabMinX;
            int prefabSizeZ = prefabMaxZ - prefabMinZ;
            moldingOffsets = new ArrayVoxelSpace<Integer>(prefabSizeX, 1, prefabSizeZ);
            moldingOffsets.setOrigin(-position.x - prefabMinX, 0, -position.z - prefabMinZ);
            if (this.moldingDirection == MoldingDirection.DOWN || this.moldingDirection == MoldingDirection.UP) {
                Vector3i pointer = new Vector3i(0, position.y, 0);
                Scanner.Context scannerContext = new Scanner.Context(pointer, this.moldingPattern, materialSpace, id);
                pointer.x = moldingOffsets.minX();
                while (pointer.x < moldingOffsets.maxX()) {
                    pointer.z = moldingOffsets.minZ();
                    while (pointer.z < moldingOffsets.maxZ()) {
                        Integer offset;
                        List<Vector3i> scanResult = this.moldingScanner.scan(scannerContext);
                        Integer n = offset = scanResult.isEmpty() ? null : Integer.valueOf(scanResult.getFirst().y - position.y);
                        if (offset != null && this.moldingDirection == MoldingDirection.UP) {
                            offset = offset - 1;
                        }
                        moldingOffsets.set(offset, pointer.x, 0, pointer.z);
                        ++pointer.z;
                    }
                    ++pointer.x;
                }
            }
        }
        try {
            Vector3i prefabPositionVector = position.toVector3i();
            ArrayVoxelSpace<Integer> moldingOffsetsFinal = moldingOffsets;
            prefabAccess.forEach(IPrefabBuffer.iterateAllColumns(), (x, y, z, blockId, holder, support, rotation, filler, call, fluidId, fluidLevel) -> {
                Material worldMaterial;
                int worldMaterialHash;
                FluidMaterial fluid;
                int worldX = position.x + x;
                int worldY = position.y + y;
                int worldZ = position.z + z;
                if (!materialSpace.isInsideSpace(worldX, worldY, worldZ)) {
                    return;
                }
                SolidMaterial solid = this.materialCache.getSolidMaterial(blockId, support, rotation, filler, (Holder<ChunkStore>)(holder != null ? holder.clone() : null));
                Material material = this.materialCache.getMaterial(solid, fluid = this.materialCache.getFluidMaterial(fluidId, (byte)fluidLevel));
                int materialHash = material.hashMaterialIds();
                if (!this.materialMask.canPlace(materialHash)) {
                    return;
                }
                if (this.moldingDirection == MoldingDirection.DOWN || this.moldingDirection == MoldingDirection.UP) {
                    Integer offset = null;
                    if (moldingOffsetsFinal.isInsideSpace(worldX, 0, worldZ)) {
                        offset = (Integer)moldingOffsetsFinal.getContent(worldX, 0, worldZ);
                    }
                    if (offset == null) {
                        return;
                    }
                    worldY += offset.intValue();
                }
                if (!this.materialMask.canReplace(materialHash, worldMaterialHash = (worldMaterial = (Material)materialSpace.getContent(worldX, worldY, worldZ)).hashMaterialIds())) {
                    return;
                }
                materialSpace.set(material, worldX, worldY, worldZ);
            }, (cx, cz, entityWrappers, buffer) -> {
                if (!this.loadEntities) {
                    return;
                }
                if (entityWrappers == null) {
                    return;
                }
                for (int i = 0; i < entityWrappers.length; ++i) {
                    Object entityClone;
                    TransformComponent transformComp = entityWrappers[i].getComponent(TransformComponent.getComponentType());
                    if (transformComp == null) continue;
                    Vector3d entityPosition = transformComp.getPosition().clone();
                    buffer.rotation.rotate(entityPosition);
                    Vector3d entityWorldPosition = entityPosition.add(prefabPositionVector);
                    if (!entityBuffer.isInsideBuffer((int)entityWorldPosition.x, (int)entityWorldPosition.y, (int)entityWorldPosition.z) || (transformComp = ((Holder)(entityClone = entityWrappers[i].clone())).getComponent(TransformComponent.getComponentType())) == null) continue;
                    entityPosition = transformComp.getPosition();
                    entityPosition.x = entityWorldPosition.x;
                    entityPosition.y = entityWorldPosition.y;
                    entityPosition.z = entityWorldPosition.z;
                    if (!materialSpace.isInsideSpace((int)Math.floor(entityPosition.x), (int)Math.floor(entityPosition.y), (int)Math.floor(entityPosition.z))) {
                        return;
                    }
                    EntityPlacementData placementData = new EntityPlacementData(new Vector3i(), PrefabRotation.ROTATION_0, (Holder<EntityStore>)entityClone, this.prefabId);
                    entityBuffer.addEntity(placementData);
                }
            }, (x, y, z, path, fitHeightmap, inheritSeed, inheritHeightCondition, weights, rotation, t) -> {}, callInstance);
        }
        catch (Exception e) {
            Object msg = "Couldn't place prefab prop.";
            msg = (String)msg + "\n";
            msg = (String)msg + ExceptionUtil.toStringWithStack(e);
            ((HytaleLogger.Api)HytaleLogger.getLogger().atWarning()).log((String)msg);
        }
        finally {
            prefabAccess.release();
        }
        for (int i = 0; i < this.childProps.size(); ++i) {
            PrefabProp prop = this.childProps.get(i);
            RotatedPosition childPosition = this.childPositions.get(i).getRelativeTo(position);
            Vector3i rotatedChildPositionVec = new Vector3i(childPosition.x, childPosition.y, childPosition.z);
            position.rotation.rotate(rotatedChildPositionVec);
            if (moldingOffsets != null && moldingOffsets.isInsideSpace(childPosition.x, 0, childPosition.z)) {
                Integer offset = (Integer)moldingOffsets.getContent(childPosition.x, 0, childPosition.z);
                if (offset == null) continue;
                int y2 = childPosition.y + offset;
                childPosition = new RotatedPosition(childPosition.x, y2, childPosition.z, childPosition.rotation);
            }
            prop.place(childPosition, materialSpace, entityBuffer, id);
        }
    }

    @Override
    public ContextDependency getContextDependency() {
        return this.contextDependency;
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

