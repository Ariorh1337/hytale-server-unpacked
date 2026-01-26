/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.prefab.selection.standard;

import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentRegistry;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.Axis;
import com.hypixel.hytale.math.block.BlockUtil;
import com.hypixel.hytale.math.matrix.Matrix4d;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.metrics.MetricProvider;
import com.hypixel.hytale.metrics.MetricResults;
import com.hypixel.hytale.metrics.MetricsRegistry;
import com.hypixel.hytale.protocol.Opacity;
import com.hypixel.hytale.protocol.packets.interface_.BlockChange;
import com.hypixel.hytale.protocol.packets.interface_.EditorBlocksChange;
import com.hypixel.hytale.protocol.packets.interface_.EditorSelection;
import com.hypixel.hytale.protocol.packets.interface_.FluidChange;
import com.hypixel.hytale.server.core.asset.type.blockhitbox.BlockBoundingBoxes;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.StateData;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.VariantRotation;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.asset.type.fluid.FluidTicker;
import com.hypixel.hytale.server.core.blocktype.component.BlockPhysics;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.FromPrefab;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.prefab.event.PrefabPlaceEntityEvent;
import com.hypixel.hytale.server.core.prefab.selection.mask.BlockMask;
import com.hypixel.hytale.server.core.prefab.selection.standard.FeedbackConsumer;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockRotationUtil;
import com.hypixel.hytale.server.core.universe.world.chunk.ChunkColumn;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.FluidSection;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.FillerBlockUtil;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;

public class BlockSelection
implements NetworkSerializable<EditorBlocksChange>,
MetricProvider {
    public static final Consumer<Ref<EntityStore>> DEFAULT_ENTITY_CONSUMER = ref -> {};
    public static final MetricsRegistry<BlockSelection> METRICS_REGISTRY = new MetricsRegistry<BlockSelection>().register("BlocksLock", selection -> selection.blocksLock.toString(), Codec.STRING).register("EntitiesLock", selection -> selection.entitiesLock.toString(), Codec.STRING).register("Position", selection -> new Vector3i(selection.x, selection.y, selection.z), Vector3i.CODEC).register("Anchor", selection -> new Vector3i(selection.anchorX, selection.anchorY, selection.anchorZ), Vector3i.CODEC).register("Min", BlockSelection::getSelectionMin, Vector3i.CODEC).register("Max", BlockSelection::getSelectionMax, Vector3i.CODEC).register("BlockCount", BlockSelection::getBlockCount, Codec.INTEGER).register("EntityCount", BlockSelection::getEntityCount, Codec.INTEGER);
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private int x;
    private int y;
    private int z;
    private int anchorX;
    private int anchorY;
    private int anchorZ;
    private int prefabId = -1;
    @Nonnull
    private Vector3i min = Vector3i.ZERO;
    @Nonnull
    private Vector3i max = Vector3i.ZERO;
    @Nonnull
    private final Long2ObjectMap<BlockHolder> blocks;
    @Nonnull
    private final Long2ObjectMap<FluidHolder> fluids;
    @Nonnull
    private final List<Holder<EntityStore>> entities;
    private final ReentrantReadWriteLock blocksLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock entitiesLock = new ReentrantReadWriteLock();

    public BlockSelection() {
        this.blocks = new Long2ObjectOpenHashMap<BlockHolder>();
        this.fluids = new Long2ObjectOpenHashMap<FluidHolder>();
        this.entities = new ObjectArrayList<Holder<EntityStore>>();
    }

    public BlockSelection(int initialBlockCapacity, int initialEntityCapacity) {
        this.blocks = new Long2ObjectOpenHashMap<BlockHolder>(initialBlockCapacity);
        this.fluids = new Long2ObjectOpenHashMap<FluidHolder>(initialBlockCapacity);
        this.entities = new ObjectArrayList<Holder<EntityStore>>(initialEntityCapacity);
    }

    public BlockSelection(@Nonnull BlockSelection other) {
        if (other == this) {
            throw new IllegalArgumentException("Cannot duplicate a BlockSelection with this method! Use clone()!");
        }
        this.blocks = new Long2ObjectOpenHashMap<BlockHolder>(other.getBlockCount());
        this.fluids = new Long2ObjectOpenHashMap<FluidHolder>(other.getFluidCount());
        this.entities = new ObjectArrayList<Holder<EntityStore>>(other.getEntityCount());
        this.copyPropertiesFrom(other);
        this.add(other);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public int getAnchorX() {
        return this.anchorX;
    }

    public int getAnchorY() {
        return this.anchorY;
    }

    public int getAnchorZ() {
        return this.anchorZ;
    }

    @Nonnull
    public Vector3i getSelectionMin() {
        return this.min.clone();
    }

    @Nonnull
    public Vector3i getSelectionMax() {
        return this.max.clone();
    }

    public boolean hasSelectionBounds() {
        return !this.min.equals(Vector3i.ZERO) || !this.max.equals(Vector3i.ZERO);
    }

    public int getBlockCount() {
        this.blocksLock.readLock().lock();
        try {
            int n = this.blocks.size();
            return n;
        }
        finally {
            this.blocksLock.readLock().unlock();
        }
    }

    public int getFluidCount() {
        this.blocksLock.readLock().lock();
        try {
            int n = this.fluids.size();
            return n;
        }
        finally {
            this.blocksLock.readLock().unlock();
        }
    }

    public int getSelectionVolume() {
        int xLength = this.max.x - this.min.x;
        int yLength = this.max.y - this.min.y;
        int zLength = this.max.z - this.min.z;
        return xLength * yLength & zLength;
    }

    public int getEntityCount() {
        this.entitiesLock.readLock().lock();
        try {
            int n = this.entities.size();
            return n;
        }
        finally {
            this.entitiesLock.readLock().unlock();
        }
    }

    public void setPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setAnchorAtWorldPos(int anchorX, int anchorY, int anchorZ) {
        this.setAnchor(anchorX - this.x, anchorY - this.y, anchorZ - this.z);
    }

    public void setAnchor(int anchorX, int anchorY, int anchorZ) {
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.anchorZ = anchorZ;
    }

    public void setSelectionArea(@Nonnull Vector3i min, @Nonnull Vector3i max) {
        this.min = Vector3i.min(min, max);
        this.max = Vector3i.max(min, max);
    }

    public void setPrefabId(int id) {
        this.prefabId = id;
    }

    public void copyPropertiesFrom(@Nonnull BlockSelection other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.anchorX = other.anchorX;
        this.anchorY = other.anchorY;
        this.anchorZ = other.anchorZ;
        this.min = other.min.clone();
        this.max = other.max.clone();
    }

    public boolean canPlace(@Nonnull World world, @Nonnull Vector3i position, @Nullable IntList mask) {
        return this.compare((x1, y1, z1, block) -> {
            int blockZ;
            int blockY;
            int blockX = x1 + position.getX() - this.anchorX;
            int blockId = world.getBlock(blockX, blockY = y1 + position.getY() - this.anchorY, blockZ = z1 + position.getZ() - this.anchorZ);
            return blockId == 0 || mask == null || mask.contains(blockId);
        });
    }

    public boolean matches(@Nonnull World world, @Nonnull Vector3i position) {
        return this.compare((x1, y1, z1, block) -> {
            int blockZ;
            int blockY;
            int blockX = x1 + position.getX() - this.anchorX;
            int blockId = world.getBlock(blockX, blockY = y1 + position.getY() - this.anchorY, blockZ = z1 + position.getZ() - this.anchorZ);
            return block.blockId == blockId;
        });
    }

    public boolean compare(@Nonnull BlockComparingIterator iterator) {
        for (Long2ObjectMap.Entry entry : this.blocks.long2ObjectEntrySet()) {
            int z1;
            int y1;
            long packed = entry.getLongKey();
            BlockHolder value = (BlockHolder)entry.getValue();
            int x1 = BlockUtil.unpackX(packed);
            if (iterator.test(x1, y1 = BlockUtil.unpackY(packed), z1 = BlockUtil.unpackZ(packed), value)) continue;
            return false;
        }
        return true;
    }

    public boolean hasBlockAtWorldPos(int x, int y, int z) {
        return this.hasBlockAtLocalPos(x - this.x, y - this.y, z - this.z);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean hasBlockAtLocalPos(int x, int y, int z) {
        this.blocksLock.readLock().lock();
        try {
            boolean bl = this.blocks.containsKey(BlockUtil.pack(x, y, z));
            return bl;
        }
        finally {
            this.blocksLock.readLock().unlock();
        }
    }

    public int getBlockAtWorldPos(int x, int y, int z) {
        return this.getBlockAtLocalPos(x - this.x, y - this.y, z - this.z);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int getBlockAtLocalPos(int x, int y, int z) {
        this.blocksLock.readLock().lock();
        try {
            BlockHolder blockHolder = (BlockHolder)this.blocks.get(BlockUtil.pack(x, y, z));
            if (blockHolder == null) {
                int n = Integer.MIN_VALUE;
                return n;
            }
            int n = blockHolder.blockId();
            return n;
        }
        finally {
            this.blocksLock.readLock().unlock();
        }
    }

    public BlockHolder getBlockHolderAtWorldPos(int x, int y, int z) {
        return this.getBlockHolderAtLocalPos(x - this.x, y - this.y, z - this.z);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private BlockHolder getBlockHolderAtLocalPos(int x, int y, int z) {
        this.blocksLock.readLock().lock();
        try {
            BlockHolder blockHolder = (BlockHolder)this.blocks.get(BlockUtil.pack(x, y, z));
            return blockHolder;
        }
        finally {
            this.blocksLock.readLock().unlock();
        }
    }

    public int getFluidAtWorldPos(int x, int y, int z) {
        return this.getFluidAtLocalPos(x - this.x, y - this.y, z - this.z);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int getFluidAtLocalPos(int x, int y, int z) {
        this.blocksLock.readLock().lock();
        try {
            FluidHolder fluidStore = (FluidHolder)this.fluids.get(BlockUtil.pack(x, y, z));
            if (fluidStore == null) {
                int n = Integer.MIN_VALUE;
                return n;
            }
            int n = fluidStore.fluidId();
            return n;
        }
        finally {
            this.blocksLock.readLock().unlock();
        }
    }

    public byte getFluidLevelAtWorldPos(int x, int y, int z) {
        return this.getFluidLevelAtLocalPos(x - this.x, y - this.y, z - this.z);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private byte getFluidLevelAtLocalPos(int x, int y, int z) {
        this.blocksLock.readLock().lock();
        try {
            FluidHolder fluidStore = (FluidHolder)this.fluids.get(BlockUtil.pack(x, y, z));
            if (fluidStore == null) {
                byte by = 0;
                return by;
            }
            byte by = fluidStore.fluidLevel();
            return by;
        }
        finally {
            this.blocksLock.readLock().unlock();
        }
    }

    public int getSupportValueAtWorldPos(int x, int y, int z) {
        return this.getSupportValueAtLocalPos(x - this.x, y - this.y, z - this.z);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int getSupportValueAtLocalPos(int x, int y, int z) {
        this.blocksLock.readLock().lock();
        try {
            BlockHolder blockHolder = (BlockHolder)this.blocks.get(BlockUtil.pack(x, y, z));
            if (blockHolder == null) {
                int n = 0;
                return n;
            }
            int n = blockHolder.supportValue();
            return n;
        }
        finally {
            this.blocksLock.readLock().unlock();
        }
    }

    @Nullable
    public Holder<ChunkStore> getStateAtWorldPos(int x, int y, int z) {
        return this.getStateAtLocalPos(x - this.x, y - this.y, z - this.z);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    private Holder<ChunkStore> getStateAtLocalPos(int x, int y, int z) {
        this.blocksLock.readLock().lock();
        try {
            BlockHolder blockHolder = (BlockHolder)this.blocks.get(BlockUtil.pack(x, y, z));
            if (blockHolder == null) {
                Holder<ChunkStore> holder = null;
                return holder;
            }
            Holder<ChunkStore> holder = blockHolder.holder();
            Object object = holder != null ? holder.clone() : null;
            return object;
        }
        finally {
            this.blocksLock.readLock().unlock();
        }
    }

    public void forEachBlock(@Nonnull BlockIterator iterator) {
        this.blocksLock.readLock().lock();
        try {
            Long2ObjectMaps.fastForEach(this.blocks, e -> {
                long packed = e.getLongKey();
                BlockHolder block = (BlockHolder)e.getValue();
                int x1 = BlockUtil.unpackX(packed);
                int y1 = BlockUtil.unpackY(packed);
                int z1 = BlockUtil.unpackZ(packed);
                iterator.accept(x1, y1, z1, block);
            });
        }
        finally {
            this.blocksLock.readLock().unlock();
        }
    }

    public void forEachFluid(@Nonnull FluidIterator iterator) {
        this.blocksLock.readLock().lock();
        try {
            Long2ObjectMaps.fastForEach(this.fluids, e -> {
                long packed = e.getLongKey();
                FluidHolder block = (FluidHolder)e.getValue();
                int x1 = BlockUtil.unpackX(packed);
                int y1 = BlockUtil.unpackY(packed);
                int z1 = BlockUtil.unpackZ(packed);
                iterator.accept(x1, y1, z1, block.fluidId(), block.fluidLevel());
            });
        }
        finally {
            this.blocksLock.readLock().unlock();
        }
    }

    public void forEachEntity(Consumer<Holder<EntityStore>> consumer) {
        this.entitiesLock.readLock().lock();
        try {
            this.entities.forEach(consumer);
        }
        finally {
            this.entitiesLock.readLock().unlock();
        }
    }

    public void copyFromAtWorld(int x, int y, int z, @Nonnull WorldChunk other, @Nullable BlockPhysics blockPhysics) {
        this.addBlockAtWorldPos(x, y, z, other.getBlock(x, y, z), other.getRotationIndex(x, y, z), other.getFiller(x, y, z), blockPhysics != null ? blockPhysics.get(x, y, z) : 0, other.getBlockComponentHolder(x, y, z));
        this.addFluidAtWorldPos(x, y, z, other.getFluidId(x, y, z), other.getFluidLevel(x, y, z));
    }

    public void addEmptyAtWorldPos(int x, int y, int z) {
        this.addBlockAtWorldPos(x, y, z, 0, 0, 0, 0);
        this.addFluidAtWorldPos(x, y, z, 0, (byte)0);
    }

    public void addBlockAtWorldPos(int x, int y, int z, int block, int rotation, int filler, int supportValue) {
        this.addBlockAtWorldPos(x, y, z, block, rotation, filler, supportValue, null);
    }

    public void addBlockAtWorldPos(int x, int y, int z, int block, int rotation, int filler, int supportValue, Holder<ChunkStore> state) {
        this.addBlockAtLocalPos(x - this.x, y - this.y, z - this.z, block, rotation, filler, supportValue, state);
    }

    public void addBlockAtLocalPos(int x, int y, int z, int block, int rotation, int filler, int supportValue) {
        this.addBlockAtLocalPos(x, y, z, block, rotation, filler, supportValue, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addBlockAtLocalPos(int x, int y, int z, int block, int rotation, int filler, int supportValue, Holder<ChunkStore> state) {
        this.blocksLock.writeLock().lock();
        try {
            this.addBlock0(x, y, z, block, rotation, filler, supportValue, state);
        }
        finally {
            this.blocksLock.writeLock().unlock();
        }
    }

    private void addBlock0(int x, int y, int z, int block, int rotation, int filler, int supportValue, Holder<ChunkStore> state) {
        this.blocks.put(BlockUtil.pack(x, y, z), new BlockHolder(block, rotation, filler, supportValue, state));
    }

    private void addBlock0(int x, int y, int z, @Nonnull BlockHolder block) {
        this.blocks.put(BlockUtil.pack(x, y, z), block.cloneBlockHolder());
    }

    public void addFluidAtWorldPos(int x, int y, int z, int fluidId, byte fluidLevel) {
        this.addFluidAtLocalPos(x - this.x, y - this.y, z - this.z, fluidId, fluidLevel);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addFluidAtLocalPos(int x, int y, int z, int fluidId, byte fluidLevel) {
        this.blocksLock.writeLock().lock();
        try {
            this.addFluid0(x, y, z, fluidId, fluidLevel);
        }
        finally {
            this.blocksLock.writeLock().unlock();
        }
    }

    private void addFluid0(int x, int y, int z, int fluidId, byte fluidLevel) {
        this.fluids.put(BlockUtil.pack(x, y, z), new FluidHolder(fluidId, fluidLevel));
    }

    private void addEntity0(Holder<EntityStore> holder) {
        this.entities.add(holder);
    }

    public void reserializeBlockStates(ChunkStore store, boolean destructive) {
        this.blocksLock.writeLock().lock();
        try {
            this.blocks.replaceAll((k, b) -> {
                Holder<ChunkStore> holder = b.holder();
                if (holder == null && b.filler == 0) {
                    StateData state;
                    BlockType blockType = BlockType.getAssetMap().getAsset(b.blockId);
                    if (blockType == null) {
                        return b;
                    }
                    if (blockType.getBlockEntity() != null) {
                        holder = blockType.getBlockEntity().clone();
                    }
                    if ((state = blockType.getState()) != null && state.getId() != null) {
                        Vector3i position = new Vector3i(BlockUtil.unpackX(k), BlockUtil.unpackY(k), BlockUtil.unpackZ(k));
                        Object codec = BlockState.CODEC.getCodecFor(state.getId());
                        if (codec == null) {
                            return b;
                        }
                        BlockState blockState = (BlockState)codec.decode(new BsonDocument());
                        if (blockState == null) {
                            return b;
                        }
                        blockState.setPosition(null, position);
                        holder = blockState.toHolder();
                    }
                }
                if (holder != null && b.filler != 0) {
                    return new BlockHolder(b.blockId(), b.rotation(), b.filler(), b.supportValue(), null);
                }
                if (holder == null) {
                    return b;
                }
                try {
                    BlockModule.MigrationSystem system;
                    ComponentRegistry<ChunkStore> registry = ChunkStore.REGISTRY;
                    ComponentRegistry.Data<ChunkStore> data = registry.getData();
                    SystemType<ChunkStore, BlockModule.MigrationSystem> systemType = BlockModule.get().getMigrationSystemType();
                    BitSet systemIndexes = data.getSystemIndexesForType(systemType);
                    int systemIndex = -1;
                    while ((systemIndex = systemIndexes.nextSetBit(systemIndex + 1)) >= 0) {
                        system = data.getSystem(systemIndex, systemType);
                        if (!system.test(registry, holder.getArchetype())) continue;
                        system.onEntityAdd(holder, AddReason.LOAD, store.getStore());
                    }
                    systemIndex = -1;
                    while ((systemIndex = systemIndexes.nextSetBit(systemIndex + 1)) >= 0) {
                        system = data.getSystem(systemIndex, systemType);
                        if (!system.test(registry, holder.getArchetype())) continue;
                        system.onEntityRemoved(holder, RemoveReason.UNLOAD, store.getStore());
                    }
                    if (destructive) {
                        holder.tryRemoveComponent(registry.getUnknownComponentType());
                    }
                    if (!holder.hasSerializableComponents(data)) {
                        return new BlockHolder(b.blockId(), b.rotation(), b.filler(), b.supportValue(), null);
                    }
                    return new BlockHolder(b.blockId(), b.rotation(), b.filler(), b.supportValue(), (Holder<ChunkStore>)holder.clone());
                }
                catch (Throwable e) {
                    throw new RuntimeException("Failed to read block state: " + String.valueOf(b), e);
                }
            });
        }
        finally {
            this.blocksLock.writeLock().unlock();
        }
    }

    public void addEntityFromWorld(@Nonnull Holder<EntityStore> entityHolder) {
        TransformComponent transformComponent = entityHolder.getComponent(TransformComponent.getComponentType());
        assert (transformComponent != null);
        transformComponent.getPosition().subtract(this.x, this.y, this.z);
        this.addEntityHolderRaw(entityHolder);
    }

    public void addEntityHolderRaw(Holder<EntityStore> entityHolder) {
        this.entitiesLock.writeLock().lock();
        try {
            this.entities.add(entityHolder);
        }
        finally {
            this.entitiesLock.writeLock().unlock();
        }
    }

    public void sortEntitiesByPosition() {
        this.entitiesLock.writeLock().lock();
        try {
            ComponentType<EntityStore, TransformComponent> transformType = TransformComponent.getComponentType();
            this.entities.sort((a, b) -> {
                TransformComponent ta = (TransformComponent)a.getComponent(transformType);
                TransformComponent tb = (TransformComponent)b.getComponent(transformType);
                if (ta == null && tb == null) {
                    return 0;
                }
                if (ta == null) {
                    return 1;
                }
                if (tb == null) {
                    return -1;
                }
                Vector3d pa = ta.getPosition();
                Vector3d pb = tb.getPosition();
                if (pa == null && pb == null) {
                    return 0;
                }
                if (pa == null) {
                    return 1;
                }
                if (pb == null) {
                    return -1;
                }
                int cmp = Double.compare(pa.getX(), pb.getX());
                if (cmp != 0) {
                    return cmp;
                }
                cmp = Double.compare(pa.getY(), pb.getY());
                if (cmp != 0) {
                    return cmp;
                }
                return Double.compare(pa.getZ(), pb.getZ());
            });
        }
        finally {
            this.entitiesLock.writeLock().unlock();
        }
    }

    public void placeNoReturn(@Nonnull World world, Vector3i position, ComponentAccessor<EntityStore> componentAccessor) {
        this.placeNoReturn(null, null, FeedbackConsumer.DEFAULT, world, position, null, componentAccessor);
    }

    public void placeNoReturn(String feedbackKey, CommandSender feedback, @Nonnull World outerWorld, ComponentAccessor<EntityStore> componentAccessor) {
        this.placeNoReturn(feedbackKey, feedback, FeedbackConsumer.DEFAULT, outerWorld, Vector3i.ZERO, null, componentAccessor);
    }

    public void placeNoReturn(String feedbackKey, CommandSender feedback, @Nonnull FeedbackConsumer feedbackConsumer, @Nonnull World outerWorld, ComponentAccessor<EntityStore> componentAccessor) {
        this.placeNoReturn(feedbackKey, feedback, feedbackConsumer, outerWorld, Vector3i.ZERO, null, componentAccessor);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void placeNoReturn(@Nullable String feedbackKey, @Nullable CommandSender feedback, @Nonnull FeedbackConsumer feedbackConsumer, @Nonnull World outerWorld, @Nullable Vector3i position, @Nullable BlockMask blockMask, ComponentAccessor<EntityStore> componentAccessor) {
        IntUnaryOperator xConvert = position != null && position.getX() != 0 ? localX -> localX + this.x + position.getX() - this.anchorX : localX -> localX + this.x - this.anchorX;
        IntUnaryOperator yConvert = position != null && position.getY() != 0 ? localY -> localY + this.y + position.getY() - this.anchorY : localY -> localY + this.y - this.anchorY;
        IntUnaryOperator zConvert = position != null && position.getZ() != 0 ? localZ -> localZ + this.z + position.getZ() - this.anchorZ : localZ -> localZ + this.z - this.anchorZ;
        LongOpenHashSet dirtyChunks = new LongOpenHashSet();
        this.blocksLock.readLock().lock();
        try {
            BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
            int totalBlocks = this.blocks.size();
            AtomicInteger counter = new AtomicInteger();
            outerWorld.getBlockBulkRelative(this.blocks, xConvert, yConvert, zConvert, (world, blockHolder, chunkIndex, chunk, blockX, blockY, blockZ, localX, localY, localZ) -> {
                int newBlockId = blockHolder.blockId();
                Holder<ChunkStore> holder = blockHolder.holder();
                this.placeBlockNoReturn(feedbackKey, feedback, feedbackConsumer, outerWorld, blockMask, dirtyChunks, assetMap, totalBlocks, counter.incrementAndGet(), chunkIndex, chunk, blockX, blockY, blockZ, newBlockId, blockHolder.rotation(), blockHolder.filler(), (Holder<ChunkStore>)(holder != null ? holder.clone() : null), componentAccessor);
            });
            outerWorld.getBlockBulkRelative(this.fluids, xConvert, yConvert, zConvert, (world, fluidStore, chunkIndex, chunk, blockX, blockY, blockZ, localX, localY, localZ) -> this.placeFluidNoReturn(feedbackKey, feedback, feedbackConsumer, outerWorld, blockMask, dirtyChunks, assetMap, totalBlocks, counter.incrementAndGet(), chunkIndex, chunk, blockX, blockY, blockZ, fluidStore.fluidId, fluidStore.fluidLevel, componentAccessor));
        }
        finally {
            this.blocksLock.readLock().unlock();
        }
        dirtyChunks.forEach(value -> outerWorld.getChunkLighting().invalidateLightInChunk(outerWorld.getChunkIfInMemory(value)));
        this.placeEntities(outerWorld, position);
        dirtyChunks.forEach(value -> outerWorld.getNotificationHandler().updateChunk(value));
    }

    private void placeBlockNoReturn(String feedbackKey, CommandSender feedback, @Nonnull FeedbackConsumer feedbackConsumer, @Nonnull World outerWorld, @Nullable BlockMask blockMask, @Nonnull LongSet dirtyChunks, @Nonnull BlockTypeAssetMap<String, BlockType> assetMap, int totalBlocks, int counter, long chunkIndex, @Nonnull WorldChunk chunk, int blockX, int blockY, int blockZ, int newBlockId, int newRotation, int newFiller, Holder<ChunkStore> holder, ComponentAccessor<EntityStore> componentAccessor) {
        if (blockY < 0 || blockY >= 320) {
            return;
        }
        int oldBlockId = chunk.getBlock(blockX, blockY, blockZ);
        if (blockMask != null && blockMask.isExcluded(outerWorld, blockX, blockY, blockZ, this.min, this.max, oldBlockId)) {
            return;
        }
        BlockChunk blockChunk = chunk.getBlockChunk();
        if (blockChunk.setBlock(blockX, blockY, blockZ, newBlockId, newRotation, newFiller)) {
            short height;
            BlockType newBlockType = assetMap.getAsset(newBlockId);
            if (newBlockType != null && FluidTicker.isFullySolid(newBlockType)) {
                this.clearFluidAtPosition(outerWorld, chunk, blockX, blockY, blockZ);
            }
            if ((height = blockChunk.getHeight(blockX, blockZ)) <= blockY) {
                if (height == blockY && newBlockId == 0) {
                    blockChunk.updateHeight(blockX, blockZ, (short)blockY);
                } else if (height < blockY && newBlockId != 0 && newBlockType != null && newBlockType.getOpacity() != Opacity.Transparent) {
                    blockChunk.setHeight(blockX, blockZ, (short)blockY);
                }
            }
        }
        chunk.setState(blockX, blockY, blockZ, holder);
        dirtyChunks.add(chunkIndex);
        feedbackConsumer.accept(feedbackKey, totalBlocks, counter, feedback, componentAccessor);
    }

    private void placeFluidNoReturn(String feedbackKey, CommandSender feedback, @Nonnull FeedbackConsumer feedbackConsumer, @Nonnull World outerWorld, BlockMask blockMask, @Nonnull LongSet dirtyChunks, BlockTypeAssetMap<String, BlockType> assetMap, int totalBlocks, int counter, long chunkIndex, @Nonnull WorldChunk chunk, int blockX, int blockY, int blockZ, int newFluidId, byte newFluidLevel, ComponentAccessor<EntityStore> componentAccessor) {
        if (blockY < 0 || blockY >= 320) {
            return;
        }
        int sectionY = ChunkUtil.chunkCoordinate(blockY);
        Store<ChunkStore> store = outerWorld.getChunkStore().getStore();
        ChunkColumn column = store.getComponent(chunk.getReference(), ChunkColumn.getComponentType());
        Ref<ChunkStore> section = column.getSection(sectionY);
        FluidSection fluidSection = store.ensureAndGetComponent(section, FluidSection.getComponentType());
        fluidSection.setFluid(blockX, blockY, blockZ, newFluidId, newFluidLevel);
        dirtyChunks.add(chunkIndex);
        feedbackConsumer.accept(feedbackKey, totalBlocks, counter, feedback, componentAccessor);
    }

    private void clearFluidAtPosition(@Nonnull World world, @Nonnull WorldChunk chunk, int blockX, int blockY, int blockZ) {
        Ref<ChunkStore> ref = chunk.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }
        Store<ChunkStore> store = world.getChunkStore().getStore();
        ChunkColumn column = store.getComponent(ref, ChunkColumn.getComponentType());
        if (column == null) {
            return;
        }
        Ref<ChunkStore> section = column.getSection(ChunkUtil.chunkCoordinate(blockY));
        if (section == null) {
            return;
        }
        FluidSection fluidSection = store.getComponent(section, FluidSection.getComponentType());
        if (fluidSection != null) {
            fluidSection.setFluid(blockX, blockY, blockZ, 0, (byte)0);
        }
    }

    @Nonnull
    public BlockSelection place(CommandSender feedback, @Nonnull World outerWorld) {
        return this.place(feedback, outerWorld, Vector3i.ZERO, null);
    }

    @Nonnull
    public BlockSelection place(CommandSender feedback, @Nonnull World outerWorld, BlockMask blockMask) {
        return this.place(feedback, outerWorld, Vector3i.ZERO, blockMask);
    }

    @Nonnull
    public BlockSelection place(CommandSender feedback, @Nonnull World outerWorld, Vector3i position, BlockMask blockMask) {
        return this.place(feedback, outerWorld, position, blockMask, DEFAULT_ENTITY_CONSUMER);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    public BlockSelection place(CommandSender feedback, @Nonnull World outerWorld, @Nullable Vector3i position, @Nullable BlockMask blockMask, @Nonnull Consumer<Ref<EntityStore>> entityConsumer) {
        BlockSelection before = new BlockSelection(this.getBlockCount(), 0);
        before.setAnchor(this.anchorX, this.anchorY, this.anchorZ);
        before.setPosition(this.x, this.y, this.z);
        IntUnaryOperator xConvert = position != null && position.getX() != 0 ? localX -> localX + this.x + position.getX() - this.anchorX : localX -> localX + this.x - this.anchorX;
        IntUnaryOperator yConvert = position != null && position.getY() != 0 ? localY -> localY + this.y + position.getY() - this.anchorY : localY -> localY + this.y - this.anchorY;
        IntUnaryOperator zConvert = position != null && position.getZ() != 0 ? localZ -> localZ + this.z + position.getZ() - this.anchorZ : localZ -> localZ + this.z - this.anchorZ;
        LongOpenHashSet dirtyChunks = new LongOpenHashSet();
        this.blocksLock.readLock().lock();
        try {
            BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
            outerWorld.getBlockBulkRelative(this.blocks, xConvert, yConvert, zConvert, (world, blockHolder, chunkIndex, chunk, blockX, blockY, blockZ, localX, localY, localZ) -> {
                Holder<ChunkStore> holder = blockHolder.holder();
                this.placeBlock(feedback, outerWorld, blockMask, before, dirtyChunks, assetMap, chunkIndex, chunk, blockX, blockY, blockZ, localX, localY, localZ, blockHolder.blockId(), blockHolder.rotation(), blockHolder.filler(), (Holder<ChunkStore>)(holder != null ? holder.clone() : null), blockHolder.supportValue());
            });
            IndexedLookupTableAssetMap<String, Fluid> fluidMap = Fluid.getAssetMap();
            outerWorld.getBlockBulkRelative(this.fluids, xConvert, yConvert, zConvert, (world, fluidStore, chunkIndex, chunk, blockX, blockY, blockZ, localX, localY, localZ) -> this.placeFluid(feedback, outerWorld, before, dirtyChunks, fluidMap, chunkIndex, chunk, blockX, blockY, blockZ, localX, localY, localZ, fluidStore.fluidId, fluidStore.fluidLevel));
        }
        finally {
            this.blocksLock.readLock().unlock();
        }
        dirtyChunks.forEach(value -> outerWorld.getChunkLighting().invalidateLightInChunk(outerWorld.getChunkIfInMemory(value)));
        this.placeEntities(outerWorld, position, entityConsumer);
        dirtyChunks.forEach(value -> outerWorld.getNotificationHandler().updateChunk(value));
        return before;
    }

    private void placeBlock(CommandSender feedback, @Nonnull World outerWorld, @Nullable BlockMask blockMask, @Nonnull BlockSelection before, @Nonnull LongSet dirtyChunks, @Nonnull BlockTypeAssetMap<String, BlockType> assetMap, long chunkIndex, @Nonnull WorldChunk chunk, int blockX, int blockY, int blockZ, int localX, int localY, int localZ, int newBlockId, int newRotation, int newFiller, Holder<ChunkStore> holder, int newSupportValue) {
        if (blockY < 0 || blockY >= 320) {
            return;
        }
        Store<ChunkStore> chunkStore = chunk.getWorld().getChunkStore().getStore();
        ChunkColumn chunkColumn = chunkStore.getComponent(chunk.getReference(), ChunkColumn.getComponentType());
        Ref<ChunkStore> section = chunkColumn.getSection(ChunkUtil.chunkCoordinate(blockY));
        BlockSection blockSection = chunkStore.getComponent(section, BlockSection.getComponentType());
        int oldBlockId = chunk.getBlock(blockX, blockY, blockZ);
        if (blockMask != null && blockMask.isExcluded(outerWorld, blockX, blockY, blockZ, this.min, this.max, oldBlockId)) {
            return;
        }
        BlockPhysics blockPhysics = section != null ? chunkStore.getComponent(section, BlockPhysics.getComponentType()) : null;
        int supportValue = blockPhysics != null ? blockPhysics.get(blockX, blockY, blockZ) : 0;
        int filler = blockSection.getFiller(blockX, blockY, blockZ);
        int rotation = blockSection.getRotationIndex(blockX, blockY, blockZ);
        before.addBlockAtLocalPos(localX, localY, localZ, oldBlockId, rotation, filler, supportValue, chunk.getBlockComponentHolder(blockX, blockY, blockZ));
        BlockChunk blockChunk = chunk.getBlockChunk();
        if (blockChunk.setBlock(blockX, blockY, blockZ, newBlockId, newRotation, newFiller)) {
            short height;
            BlockType newBlockType = assetMap.getAsset(newBlockId);
            if (newBlockType != null && FluidTicker.isFullySolid(newBlockType)) {
                this.clearFluidAtPosition(outerWorld, chunk, blockX, blockY, blockZ);
            }
            if ((height = blockChunk.getHeight(blockX, blockZ)) <= blockY) {
                if (height == blockY && newBlockId == 0) {
                    blockChunk.updateHeight(blockX, blockZ, (short)blockY);
                } else if (height < blockY && newBlockId != 0 && newBlockType.getOpacity() != Opacity.Transparent) {
                    blockChunk.setHeight(blockX, blockZ, (short)blockY);
                }
            }
            if (newSupportValue != supportValue) {
                if (newSupportValue != 0) {
                    if (blockPhysics == null) {
                        blockPhysics = chunkStore.ensureAndGetComponent(section, BlockPhysics.getComponentType());
                    }
                    blockPhysics.set(blockX, blockY, blockZ, newSupportValue);
                } else if (blockPhysics != null) {
                    blockPhysics.set(blockX, blockY, blockZ, 0);
                }
            }
        }
        chunk.setState(blockX, blockY, blockZ, holder);
        dirtyChunks.add(chunkIndex);
    }

    private void placeFluid(CommandSender feedback, @Nonnull World outerWorld, @Nonnull BlockSelection before, @Nonnull LongSet dirtyChunks, IndexedLookupTableAssetMap<String, Fluid> assetMap, long chunkIndex, @Nonnull WorldChunk chunk, int blockX, int blockY, int blockZ, int localX, int localY, int localZ, int newFluidId, byte newFluidLevel) {
        if (blockY < 0 || blockY >= 320) {
            return;
        }
        int sectionY = ChunkUtil.chunkCoordinate(blockY);
        Store<ChunkStore> store = outerWorld.getChunkStore().getStore();
        ChunkColumn column = store.getComponent(chunk.getReference(), ChunkColumn.getComponentType());
        Ref<ChunkStore> section = column.getSection(sectionY);
        FluidSection fluidSection = store.ensureAndGetComponent(section, FluidSection.getComponentType());
        int oldFluidId = fluidSection.getFluidId(blockX, blockY, blockZ);
        byte oldFluidLevel = fluidSection.getFluidLevel(blockX, blockY, blockZ);
        before.addFluidAtLocalPos(localX, localY, localZ, oldFluidId, oldFluidLevel);
        fluidSection.setFluid(blockX, blockY, blockZ, newFluidId, newFluidLevel);
        dirtyChunks.add(chunkIndex);
    }

    private void placeEntities(@Nonnull World world, @Nonnull Vector3i pos) {
        this.placeEntities(world, pos, DEFAULT_ENTITY_CONSUMER);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void placeEntities(@Nonnull World world, @Nonnull Vector3i pos, @Nonnull Consumer<Ref<EntityStore>> entityConsumer) {
        this.entitiesLock.readLock().lock();
        try {
            for (Holder<EntityStore> entityHolder : this.entities) {
                Ref<EntityStore> entity = this.placeEntity(world, (Holder<EntityStore>)entityHolder.clone(), pos, this.prefabId);
                if (entity == null) {
                    LOGGER.at(Level.WARNING).log("Failed to spawn entity in world %s! Data: %s", (Object)world.getName(), entityHolder);
                    continue;
                }
                entityConsumer.accept(entity);
            }
        }
        finally {
            this.entitiesLock.readLock().unlock();
        }
    }

    @Nonnull
    private Ref<EntityStore> placeEntity(@Nonnull World world, @Nonnull Holder<EntityStore> entityHolder, @Nonnull Vector3i pos, int prefabId) {
        TransformComponent transformComponent = entityHolder.getComponent(TransformComponent.getComponentType());
        assert (transformComponent != null);
        transformComponent.getPosition().add(this.x + pos.getX() - this.anchorX, this.y + pos.getY() - this.anchorY, this.z + pos.getZ() - this.anchorZ);
        Store<EntityStore> store = world.getEntityStore().getStore();
        PrefabPlaceEntityEvent prefabPlaceEntityEvent = new PrefabPlaceEntityEvent(prefabId, entityHolder);
        store.invoke(prefabPlaceEntityEvent);
        entityHolder.addComponent(FromPrefab.getComponentType(), FromPrefab.INSTANCE);
        Ref<EntityStore> entityRef = new Ref<EntityStore>(store);
        world.execute(() -> store.addEntity(entityHolder, entityRef, AddReason.LOAD));
        return entityRef;
    }

    @Nonnull
    public BlockSelection rotate(@Nonnull Axis axis, int angle) {
        BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
        BlockSelection selection = new BlockSelection(this.getBlockCount(), this.getEntityCount());
        selection.copyPropertiesFrom(this);
        Vector3i mutable = new Vector3i(0, 0, 0);
        Rotation rotation = Rotation.ofDegrees(angle);
        this.forEachBlock((x1, y1, z1, block) -> {
            RotationTuple rotatedRotation;
            mutable.assign(x1 - this.anchorX, y1 - this.anchorY, z1 - this.anchorZ);
            axis.rotate(mutable, angle);
            int blockId = block.blockId;
            Holder<ChunkStore> holder = block.holder;
            RotationTuple blockRotation = RotationTuple.get(block.rotation);
            switch (axis) {
                default: {
                    throw new MatchException(null, null);
                }
                case X: {
                    RotationTuple rotationTuple = RotationTuple.of(blockRotation.yaw(), blockRotation.pitch().add(rotation), blockRotation.roll());
                    break;
                }
                case Y: {
                    RotationTuple rotationTuple = RotationTuple.of(blockRotation.yaw().add(rotation), blockRotation.pitch(), blockRotation.roll());
                    break;
                }
                case Z: {
                    RotationTuple rotationTuple = rotatedRotation = RotationTuple.of(blockRotation.yaw(), blockRotation.pitch(), blockRotation.roll().add(rotation));
                }
            }
            if (rotatedRotation == null) {
                rotatedRotation = blockRotation;
            }
            int rotatedFiller = BlockRotationUtil.getRotatedFiller(block.filler, axis, rotation);
            selection.addBlock0(mutable.getX() + this.anchorX, mutable.getY() + this.anchorY, mutable.getZ() + this.anchorZ, blockId, rotatedRotation.index(), rotatedFiller, block.supportValue(), (Holder<ChunkStore>)(holder != null ? holder.clone() : null));
        });
        this.forEachEntity(entityHolder -> {
            Object copy = entityHolder.clone();
            TransformComponent transformComponent = ((Holder)copy).getComponent(TransformComponent.getComponentType());
            assert (transformComponent != null);
            Vector3d position = transformComponent.getPosition();
            HeadRotation headRotationComponent = ((Holder)copy).getComponent(HeadRotation.getComponentType());
            position.subtract(this.anchorX, this.anchorY, this.anchorZ).subtract(0.5, 0.0, 0.5);
            axis.rotate(position, angle);
            position.add(this.anchorX, this.anchorY, this.anchorZ).add(0.5, 0.0, 0.5);
            transformComponent.getRotation().addRotationOnAxis(axis, angle);
            if (headRotationComponent != null) {
                headRotationComponent.getRotation().addRotationOnAxis(axis, angle);
            }
            selection.addEntity0((Holder<EntityStore>)copy);
        });
        return selection;
    }

    @Nonnull
    public BlockSelection rotate(@Nonnull Axis axis, int angle, @Nonnull Vector3f originOfRotation) {
        BlockSelection selection = new BlockSelection(this.getBlockCount(), this.getEntityCount());
        selection.copyPropertiesFrom(this);
        Vector3d mutable = new Vector3d(0.0, 0.0, 0.0);
        Rotation rotation = Rotation.ofDegrees(angle);
        Vector3f finalOriginOfRotation = originOfRotation.clone().subtract(this.x, this.y, this.z);
        this.forEachBlock((x1, y1, z1, block) -> {
            RotationTuple rotatedRotation;
            mutable.assign((float)x1 - finalOriginOfRotation.x, (float)y1 - finalOriginOfRotation.y, (float)z1 - finalOriginOfRotation.z);
            axis.rotate(mutable, angle);
            int blockId = block.blockId;
            Holder<ChunkStore> holder = block.holder;
            int supportValue = block.supportValue();
            RotationTuple blockRotation = RotationTuple.get(block.rotation);
            switch (axis) {
                default: {
                    throw new MatchException(null, null);
                }
                case X: {
                    RotationTuple rotationTuple = RotationTuple.of(blockRotation.yaw(), blockRotation.pitch().add(rotation), blockRotation.roll());
                    break;
                }
                case Y: {
                    RotationTuple rotationTuple = RotationTuple.of(blockRotation.yaw().add(rotation), blockRotation.pitch(), blockRotation.roll());
                    break;
                }
                case Z: {
                    RotationTuple rotationTuple = rotatedRotation = RotationTuple.of(blockRotation.yaw(), blockRotation.pitch(), blockRotation.roll().add(rotation));
                }
            }
            if (rotatedRotation == null) {
                rotatedRotation = blockRotation;
            }
            int rotatedFiller = BlockRotationUtil.getRotatedFiller(block.filler, axis, rotation);
            selection.addBlock0((int)(mutable.getX() + (double)finalOriginOfRotation.x), (int)(mutable.getY() + (double)finalOriginOfRotation.z), (int)(mutable.getZ() + (double)finalOriginOfRotation.z), blockId, rotatedRotation.index(), rotatedFiller, supportValue, (Holder<ChunkStore>)(holder != null ? holder.clone() : null));
        });
        this.forEachEntity(entityHolder -> {
            Object copy = entityHolder.clone();
            TransformComponent transformComponent = ((Holder)copy).getComponent(TransformComponent.getComponentType());
            assert (transformComponent != null);
            Vector3d position = transformComponent.getPosition();
            HeadRotation headRotationComponent = ((Holder)copy).getComponent(HeadRotation.getComponentType());
            position.subtract(this.anchorX, this.anchorY, this.anchorZ).subtract(0.5, 0.0, 0.5);
            axis.rotate(position, angle);
            position.add(this.anchorX, this.anchorY, this.anchorZ).add(0.5, 0.0, 0.5);
            transformComponent.getRotation().addRotationOnAxis(axis, angle);
            if (headRotationComponent != null) {
                headRotationComponent.getRotation().addRotationOnAxis(axis, angle);
            }
            selection.addEntity0((Holder<EntityStore>)copy);
        });
        return selection;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    public BlockSelection rotateArbitrary(float yawDegrees, float pitchDegrees, float rollDegrees) {
        int[][] corners;
        double pitchRad = Math.toRadians(pitchDegrees);
        double yawRad = Math.toRadians(yawDegrees);
        double rollRad = Math.toRadians(rollDegrees);
        Matrix4d rotation = new Matrix4d();
        rotation.setRotateEuler(pitchRad, yawRad, rollRad);
        Matrix4d inverse = new Matrix4d(rotation);
        inverse.invert();
        Vector3d tempVec = new Vector3d();
        int destMinX = Integer.MAX_VALUE;
        int destMinY = Integer.MAX_VALUE;
        int destMinZ = Integer.MAX_VALUE;
        int destMaxX = Integer.MIN_VALUE;
        int destMaxY = Integer.MIN_VALUE;
        int destMaxZ = Integer.MIN_VALUE;
        int srcMinX = Integer.MAX_VALUE;
        int srcMinY = Integer.MAX_VALUE;
        int srcMinZ = Integer.MAX_VALUE;
        int srcMaxX = Integer.MIN_VALUE;
        int srcMaxY = Integer.MIN_VALUE;
        int srcMaxZ = Integer.MIN_VALUE;
        this.blocksLock.readLock().lock();
        try {
            for (Long2ObjectMap.Entry entry : this.blocks.long2ObjectEntrySet()) {
                long packed = entry.getLongKey();
                int bx = BlockUtil.unpackX(packed) - this.anchorX;
                int by = BlockUtil.unpackY(packed) - this.anchorY;
                int bz = BlockUtil.unpackZ(packed) - this.anchorZ;
                srcMinX = Math.min(srcMinX, bx);
                srcMinY = Math.min(srcMinY, by);
                srcMinZ = Math.min(srcMinZ, bz);
                srcMaxX = Math.max(srcMaxX, bx);
                srcMaxY = Math.max(srcMaxY, by);
                srcMaxZ = Math.max(srcMaxZ, bz);
            }
        }
        finally {
            this.blocksLock.readLock().unlock();
        }
        if (srcMinX == Integer.MAX_VALUE) {
            BlockSelection selection = new BlockSelection(0, this.getEntityCount());
            selection.copyPropertiesFrom(this);
            return selection;
        }
        for (int[] corner : corners = new int[][]{{srcMinX, srcMinY, srcMinZ}, {srcMaxX, srcMinY, srcMinZ}, {srcMinX, srcMaxY, srcMinZ}, {srcMaxX, srcMaxY, srcMinZ}, {srcMinX, srcMinY, srcMaxZ}, {srcMaxX, srcMinY, srcMaxZ}, {srcMinX, srcMaxY, srcMaxZ}, {srcMaxX, srcMaxY, srcMaxZ}}) {
            tempVec.assign(corner[0], corner[1], corner[2]);
            rotation.multiplyDirection(tempVec);
            int rx = MathUtil.floor(tempVec.x);
            int ry = MathUtil.floor(tempVec.y);
            int rz = MathUtil.floor(tempVec.z);
            destMinX = Math.min(destMinX, rx);
            destMinY = Math.min(destMinY, ry);
            destMinZ = Math.min(destMinZ, rz);
            destMaxX = Math.max(destMaxX, rx + 1);
            destMaxY = Math.max(destMaxY, ry + 1);
            destMaxZ = Math.max(destMaxZ, rz + 1);
        }
        BlockSelection blockSelection = new BlockSelection(this.getBlockCount(), this.getEntityCount());
        blockSelection.copyPropertiesFrom(this);
        Rotation snappedYaw = Rotation.ofDegrees(Math.round(yawDegrees / 90.0f) * 90);
        Rotation snappedPitch = Rotation.ofDegrees(Math.round(pitchDegrees / 90.0f) * 90);
        Rotation snappedRoll = Rotation.ofDegrees(Math.round(rollDegrees / 90.0f) * 90);
        this.blocksLock.readLock().lock();
        try {
            long packedSource;
            int sz;
            int sy;
            int sx;
            int dz;
            int dy;
            int dx;
            for (dx = destMinX; dx <= destMaxX; ++dx) {
                for (dy = destMinY; dy <= destMaxY; ++dy) {
                    for (dz = destMinZ; dz <= destMaxZ; ++dz) {
                        int rotatedFiller;
                        tempVec.assign(dx, dy, dz);
                        inverse.multiplyDirection(tempVec);
                        sx = (int)Math.round(tempVec.x);
                        sy = (int)Math.round(tempVec.y);
                        sz = (int)Math.round(tempVec.z);
                        packedSource = BlockUtil.pack(sx + this.anchorX, sy + this.anchorY, sz + this.anchorZ);
                        BlockHolder block = (BlockHolder)this.blocks.get(packedSource);
                        if (block == null) continue;
                        RotationTuple blockRotation = RotationTuple.get(block.rotation());
                        RotationTuple rotatedRotation = RotationTuple.of(blockRotation.yaw().add(snappedYaw), blockRotation.pitch().add(snappedPitch), blockRotation.roll().add(snappedRoll));
                        if (rotatedRotation == null) {
                            rotatedRotation = blockRotation;
                        }
                        if ((rotatedFiller = block.filler()) != 0) {
                            int fillerX = FillerBlockUtil.unpackX(rotatedFiller);
                            int fillerY = FillerBlockUtil.unpackY(rotatedFiller);
                            int fillerZ = FillerBlockUtil.unpackZ(rotatedFiller);
                            tempVec.assign(fillerX, fillerY, fillerZ);
                            rotation.multiplyDirection(tempVec);
                            rotatedFiller = FillerBlockUtil.pack((int)Math.round(tempVec.x), (int)Math.round(tempVec.y), (int)Math.round(tempVec.z));
                        }
                        Holder<ChunkStore> holder = block.holder();
                        blockSelection.addBlock0(dx + this.anchorX, dy + this.anchorY, dz + this.anchorZ, block.blockId(), rotatedRotation.index(), rotatedFiller, block.supportValue(), (Holder<ChunkStore>)(holder != null ? holder.clone() : null));
                    }
                }
            }
            for (dx = destMinX; dx <= destMaxX; ++dx) {
                for (dy = destMinY; dy <= destMaxY; ++dy) {
                    for (dz = destMinZ; dz <= destMaxZ; ++dz) {
                        tempVec.assign(dx, dy, dz);
                        inverse.multiplyDirection(tempVec);
                        sx = (int)Math.round(tempVec.x);
                        sy = (int)Math.round(tempVec.y);
                        sz = (int)Math.round(tempVec.z);
                        packedSource = BlockUtil.pack(sx + this.anchorX, sy + this.anchorY, sz + this.anchorZ);
                        FluidHolder fluid = (FluidHolder)this.fluids.get(packedSource);
                        if (fluid == null) continue;
                        blockSelection.addFluid0(dx + this.anchorX, dy + this.anchorY, dz + this.anchorZ, fluid.fluidId(), fluid.fluidLevel());
                    }
                }
            }
        }
        finally {
            this.blocksLock.readLock().unlock();
        }
        float yawRadF = (float)yawRad;
        float pitchRadF = (float)pitchRad;
        float rollRadF = (float)rollRad;
        this.forEachEntity(entityHolder -> {
            Object copy = entityHolder.clone();
            TransformComponent transformComponent = ((Holder)copy).getComponent(TransformComponent.getComponentType());
            assert (transformComponent != null);
            Vector3d position = transformComponent.getPosition();
            HeadRotation headRotationComp = ((Holder)copy).getComponent(HeadRotation.getComponentType());
            position.subtract(this.anchorX, this.anchorY, this.anchorZ).subtract(0.5, 0.0, 0.5);
            rotation.multiplyDirection(position);
            position.add(this.anchorX, this.anchorY, this.anchorZ).add(0.5, 0.0, 0.5);
            Vector3f bodyRotation = transformComponent.getRotation();
            bodyRotation.addPitch(pitchRadF);
            bodyRotation.addYaw(yawRadF);
            bodyRotation.addRoll(rollRadF);
            if (headRotationComp != null) {
                Vector3f headRot = headRotationComp.getRotation();
                headRot.addPitch(pitchRadF);
                headRot.addYaw(yawRadF);
                headRot.addRoll(rollRadF);
            }
            selection.addEntity0((Holder<EntityStore>)copy);
        });
        return blockSelection;
    }

    @Nonnull
    public BlockSelection flip(@Nonnull Axis axis) {
        BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
        BlockSelection selection = new BlockSelection(this.getBlockCount(), this.getEntityCount());
        selection.copyPropertiesFrom(this);
        Vector3i mutable = new Vector3i(0, 0, 0);
        this.forEachBlock((x1, y1, z1, block) -> {
            mutable.assign(x1 - this.anchorX, y1 - this.anchorY, z1 - this.anchorZ);
            axis.flip(mutable);
            int blockId = block.blockId;
            Holder<ChunkStore> holder = block.holder;
            int supportValue = block.supportValue();
            int filler = block.filler;
            BlockType blockType = (BlockType)assetMap.getAsset(blockId);
            VariantRotation variantRotation = blockType.getVariantRotation();
            if (variantRotation == VariantRotation.None) {
                selection.addBlock0(mutable.getX() + this.anchorX, mutable.getY() + this.anchorY, mutable.getZ() + this.anchorZ, block);
                return;
            }
            RotationTuple blockRotation = RotationTuple.get(block.rotation);
            RotationTuple rotatedRotation = BlockRotationUtil.getFlipped(blockRotation, blockType.getFlipType(), axis, variantRotation);
            if (rotatedRotation != null) {
                rotatedRotation = blockRotation;
            }
            int rotatedFiller = BlockRotationUtil.getFlippedFiller(filler, axis);
            selection.addBlock0(mutable.getX() + this.anchorX, mutable.getY() + this.anchorY, mutable.getZ() + this.anchorZ, blockId, rotatedRotation.index(), rotatedFiller, supportValue, (Holder<ChunkStore>)(holder != null ? holder.clone() : null));
        });
        this.forEachEntity(entityHolder -> {
            Object copy = entityHolder.clone();
            HeadRotation headRotationComponent = ((Holder)copy).getComponent(HeadRotation.getComponentType());
            assert (headRotationComponent != null);
            Vector3f headRotation = headRotationComponent.getRotation();
            TransformComponent transformComponent = ((Holder)copy).getComponent(TransformComponent.getComponentType());
            assert (transformComponent != null);
            Vector3d position = transformComponent.getPosition();
            Vector3f bodyRotation = transformComponent.getRotation();
            position.subtract(this.anchorX, this.anchorY, this.anchorZ).subtract(0.5, 0.0, 0.5);
            axis.flip(position);
            position.add(this.anchorX, this.anchorY, this.anchorZ).add(0.5, 0.0, 0.5);
            axis.flipRotation(bodyRotation);
            axis.flipRotation(headRotation);
            selection.addEntity0((Holder<EntityStore>)copy);
        });
        return selection;
    }

    @Nonnull
    public BlockSelection relativize() {
        return this.relativize(this.anchorX, this.anchorY, this.anchorZ);
    }

    @Nonnull
    public BlockSelection relativize(int originX, int originY, int originZ) {
        if (originX == 0 && originY == 0 && originZ == 0) {
            return this.cloneSelection();
        }
        BlockSelection selection = new BlockSelection(this.getBlockCount(), this.getEntityCount());
        selection.setAnchor(this.anchorX - originX, this.anchorY - originY, this.anchorZ - originZ);
        selection.setPosition(this.x - originX, this.y - originY, this.z - originZ);
        selection.setSelectionArea(this.min.clone().subtract(originX, originY, originZ), this.max.clone().subtract(originX, originY, originZ));
        this.forEachBlock((x, y, z, block) -> selection.addBlock0(x - originX, y - originY, z - originZ, block));
        this.forEachEntity(holder -> {
            Object copy = holder.clone();
            TransformComponent transformComponent = ((Holder)copy).getComponent(TransformComponent.getComponentType());
            assert (transformComponent != null);
            transformComponent.getPosition().subtract(originX, originY, originZ);
            selection.addEntity0((Holder<EntityStore>)copy);
        });
        return selection;
    }

    @Nonnull
    public BlockSelection cloneSelection() {
        BlockSelection selection = new BlockSelection(this.getBlockCount(), this.getEntityCount());
        selection.copyPropertiesFrom(this);
        this.blocksLock.readLock().lock();
        try {
            Long2ObjectMaps.fastForEach(this.blocks, entry -> selection.blocks.put(entry.getLongKey(), ((BlockHolder)entry.getValue()).cloneBlockHolder()));
            selection.fluids.putAll(this.fluids);
        }
        finally {
            this.blocksLock.readLock().unlock();
        }
        this.entitiesLock.readLock().lock();
        try {
            this.entities.forEach(holder -> selection.entities.add((Holder<EntityStore>)holder.clone()));
        }
        finally {
            this.entitiesLock.readLock().unlock();
        }
        return selection;
    }

    public void add(@Nonnull BlockSelection other) {
        this.entitiesLock.writeLock().lock();
        try {
            other.forEachEntity(holder -> {
                Object copy = holder.clone();
                TransformComponent transformComponent = ((Holder)copy).getComponent(TransformComponent.getComponentType());
                assert (transformComponent != null);
                transformComponent.getPosition().add(other.x, other.y, other.z).subtract(this.x, this.y, this.z);
                this.addEntity0((Holder<EntityStore>)copy);
            });
        }
        finally {
            this.entitiesLock.writeLock().unlock();
        }
        this.blocksLock.writeLock().lock();
        try {
            other.forEachBlock((x1, y1, z1, block) -> this.addBlock0(x1 + other.x - this.x, y1 + other.y - this.y, z1 + other.z - this.z, block));
            other.forEachFluid((x1, y1, z1, fluidId, fluidLevel) -> this.addFluid0(x1 + other.x - this.x, y1 + other.y - this.y, z1 + other.z - this.z, fluidId, fluidLevel));
        }
        finally {
            this.blocksLock.writeLock().unlock();
        }
    }

    @Override
    @Nonnull
    public MetricResults toMetricResults() {
        return METRICS_REGISTRY.toMetricResults(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nonnull
    public EditorBlocksChange toPacket() {
        EditorBlocksChange packet = new EditorBlocksChange();
        this.blocksLock.readLock().lock();
        try {
            int blockCount = this.getBlockCount();
            ObjectArrayList blockList = new ObjectArrayList(blockCount);
            this.forEachBlock((x1, y1, z1, block) -> {
                if (block.filler != 0) {
                    return;
                }
                blockList.add(new BlockChange(x1 - this.anchorX, y1 - this.anchorY, z1 - this.anchorZ, block.blockId, (byte)block.rotation));
            });
            ObjectArrayList fluidList = new ObjectArrayList();
            this.forEachFluid((x1, y1, z1, fluidId, fluidLevel) -> {
                if (fluidId != 0) {
                    fluidList.add(new FluidChange(x1 - this.anchorX, y1 - this.anchorY, z1 - this.anchorZ, fluidId, fluidLevel));
                }
            });
            packet.blocksChange = (BlockChange[])blockList.toArray(BlockChange[]::new);
            packet.fluidsChange = (FluidChange[])fluidList.toArray(FluidChange[]::new);
            packet.advancedPreview = true;
            packet.blocksCount = blockCount;
        }
        finally {
            this.blocksLock.readLock().unlock();
        }
        return packet;
    }

    @Nonnull
    public EditorBlocksChange toSelectionPacket() {
        EditorBlocksChange packet = new EditorBlocksChange();
        EditorSelection selection = new EditorSelection();
        if (this.min != null) {
            selection.minX = this.min.getX();
            selection.minY = this.min.getY();
            selection.minZ = this.min.getZ();
        }
        if (this.max != null) {
            selection.maxX = this.max.getX();
            selection.maxY = this.max.getY();
            selection.maxZ = this.max.getZ();
        }
        packet.selection = selection;
        return packet;
    }

    @Nonnull
    public EditorBlocksChange toPacketWithSelection() {
        EditorBlocksChange packet = this.toPacket();
        if (this.min != null && this.max != null) {
            EditorSelection selection = new EditorSelection();
            selection.minX = this.min.getX();
            selection.minY = this.min.getY();
            selection.minZ = this.min.getZ();
            selection.maxX = this.max.getX();
            selection.maxY = this.max.getY();
            selection.maxZ = this.max.getZ();
            packet.selection = selection;
        }
        return packet;
    }

    public void tryFixFiller(boolean allowDestructive) {
        LongOpenHashSet blockPositions;
        this.blocksLock.readLock().lock();
        try {
            blockPositions = new LongOpenHashSet(this.blocks.keySet());
        }
        finally {
            this.blocksLock.readLock().unlock();
        }
        BlockTypeAssetMap<String, BlockType> blockTypeAssetMap = BlockType.getAssetMap();
        IndexedLookupTableAssetMap<String, BlockBoundingBoxes> hitboxAssetMap = BlockBoundingBoxes.getAssetMap();
        LongIterator it = blockPositions.iterator();
        while (it.hasNext()) {
            BlockType blockType;
            int blockId;
            int z;
            int y;
            long packed = it.nextLong();
            int x = BlockUtil.unpackX(packed);
            BlockHolder blockHolder = this.getBlockHolderAtLocalPos(x, y = BlockUtil.unpackY(packed), z = BlockUtil.unpackZ(packed));
            if (blockHolder == null || (blockId = blockHolder.blockId) == 0 || (blockType = blockTypeAssetMap.getAsset(blockId)) == null) continue;
            String id = blockType.getId();
            if (blockHolder.filler != 0) {
                int fillerX = FillerBlockUtil.unpackX(blockHolder.filler);
                int fillerY = FillerBlockUtil.unpackY(blockHolder.filler);
                int fillerZ = FillerBlockUtil.unpackZ(blockHolder.filler);
                BlockHolder baseBlockHolder = this.getBlockHolderAtLocalPos(x - fillerX, y - fillerY, z - fillerZ);
                BlockType baseBlock = blockTypeAssetMap.getAsset(baseBlockHolder.blockId);
                if (baseBlock == null) {
                    this.addBlockAtLocalPos(x, y, z, 0, 0, 0, 0);
                    continue;
                }
                String baseId = baseBlock.getId();
                BlockBoundingBoxes hitbox = hitboxAssetMap.getAsset(baseBlock.getHitboxTypeIndex());
                if (hitbox == null || id.equals(baseId) && baseBlockHolder.rotation == blockHolder.rotation && hitbox.get(blockHolder.rotation).getBoundingBox().containsBlock(fillerX, fillerY, fillerZ)) continue;
                this.addBlockAtLocalPos(x, y, z, 0, 0, 0, 0);
                continue;
            }
            BlockBoundingBoxes hitbox = hitboxAssetMap.getAsset(blockType.getHitboxTypeIndex());
            if (hitbox == null || !hitbox.protrudesUnitBox()) continue;
            FillerBlockUtil.forEachFillerBlock(hitbox.get(blockHolder.rotation), (x1, y1, z1) -> {
                if (x1 == 0 && y1 == 0 && z1 == 0) {
                    return;
                }
                int worldX = x + x1;
                int worldY = y + y1;
                int worldZ = z + z1;
                BlockHolder fillerBlockHolder = this.getBlockHolderAtLocalPos(worldX, worldY, worldZ);
                BlockType fillerBlock = (BlockType)blockTypeAssetMap.getAsset(fillerBlockHolder.blockId);
                int filler = FillerBlockUtil.pack(x1, y1, z1);
                if (fillerBlock == null || !fillerBlock.getId().equals(id) || filler != fillerBlockHolder.filler) {
                    if (!allowDestructive && fillerBlockHolder.blockId != 0) {
                        throw new IllegalArgumentException("Cannot replace " + fillerBlock.getId() + " with " + blockType.getId() + " in order to repair filler\n at " + worldX + ", " + worldY + ", " + worldZ + "\n base " + x + ", " + y + ", " + z);
                    }
                    this.addBlockAtLocalPos(worldX, worldY, worldZ, blockId, blockHolder.rotation, filler, 0);
                }
            });
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reserializeEntities(@Nonnull Store<EntityStore> store, boolean destructive) throws IOException {
        this.entitiesLock.writeLock().lock();
        try {
            Holder<EntityStore> holder;
            int i;
            EntityModule.MigrationSystem system;
            if (this.entities.isEmpty()) {
                return;
            }
            ComponentRegistry<EntityStore> registry = EntityStore.REGISTRY;
            ComponentRegistry.Data<EntityStore> data = registry.getData();
            SystemType<EntityStore, EntityModule.MigrationSystem> systemType = EntityModule.get().getMigrationSystemType();
            BitSet systemIndexes = data.getSystemIndexesForType(systemType);
            int systemIndex = -1;
            while ((systemIndex = systemIndexes.nextSetBit(systemIndex + 1)) >= 0) {
                system = data.getSystem(systemIndex, systemType);
                for (i = 0; i < this.entities.size(); ++i) {
                    holder = this.entities.get(i);
                    if (!system.test(registry, holder.getArchetype())) continue;
                    system.onEntityAdd(holder, AddReason.LOAD, store);
                }
            }
            systemIndex = -1;
            while ((systemIndex = systemIndexes.nextSetBit(systemIndex + 1)) >= 0) {
                system = data.getSystem(systemIndex, systemType);
                for (i = 0; i < this.entities.size(); ++i) {
                    holder = this.entities.get(i);
                    if (!system.test(registry, holder.getArchetype())) continue;
                    system.onEntityRemoved(holder, RemoveReason.UNLOAD, store);
                }
            }
            if (destructive) {
                for (int i2 = 0; i2 < this.entities.size(); ++i2) {
                    Holder<EntityStore> holder2 = this.entities.get(i2);
                    holder2.tryRemoveComponent(registry.getUnknownComponentType());
                }
            }
        }
        finally {
            this.entitiesLock.writeLock().unlock();
        }
    }

    @Nonnull
    public String toString() {
        return "BlockSelection{blocksLock=" + String.valueOf(this.blocksLock) + ", x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", originX=" + this.anchorX + ", originY=" + this.anchorY + ", originZ=" + this.anchorZ + ", min=" + String.valueOf(this.min) + ", max=" + String.valueOf(this.max) + "}";
    }

    @FunctionalInterface
    public static interface BlockComparingIterator {
        public boolean test(int var1, int var2, int var3, BlockHolder var4);
    }

    public record BlockHolder(int blockId, int rotation, int filler, int supportValue, Holder<ChunkStore> holder) {
        @Nonnull
        public BlockHolder cloneBlockHolder() {
            if (this.holder == null) {
                return this;
            }
            return new BlockHolder(this.blockId, this.rotation, this.filler, this.supportValue, (Holder<ChunkStore>)this.holder.clone());
        }
    }

    public record FluidHolder(int fluidId, byte fluidLevel) {
    }

    @FunctionalInterface
    public static interface BlockIterator {
        public void accept(int var1, int var2, int var3, BlockHolder var4);
    }

    @FunctionalInterface
    public static interface FluidIterator {
        public void accept(int var1, int var2, int var3, int var4, byte var5);
    }

    public static enum FallbackMode {
        PASS_THOUGH,
        COPY;

    }
}

