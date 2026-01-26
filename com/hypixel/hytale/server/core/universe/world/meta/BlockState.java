/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.meta;

import com.google.common.flogger.StackSize;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.lookup.ACodecMapCodec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.StateData;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.meta.BlockStateModule;
import com.hypixel.hytale.server.core.universe.world.meta.state.exceptions.NoSuchBlockStateException;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;

@Deprecated(forRemoval=true)
public abstract class BlockState
implements Component<ChunkStore> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public static final CodecMapCodec<BlockState> CODEC = new CodecMapCodec("Type");
    public static final BuilderCodec<BlockState> BASE_CODEC = ((BuilderCodec.Builder)BuilderCodec.abstractBuilder(BlockState.class).addField(new KeyedCodec<Vector3i>("Position", Vector3i.CODEC), (entity, o) -> {
        entity.position = o;
    }, entity -> {
        if (entity.position == null || Vector3i.ZERO.equals(entity.position)) {
            return null;
        }
        return entity.position;
    })).build();
    public static final KeyedCodec<String> TYPE_STRUCTURE = new KeyedCodec<String>("Type", Codec.STRING);
    public static final String OPEN_WINDOW = "OpenWindow";
    public static final String CLOSE_WINDOW = "CloseWindow";
    final AtomicBoolean initialized = new AtomicBoolean(false);
    @Nullable
    private WorldChunk chunk;
    private Vector3i position;
    protected Ref<ChunkStore> reference;

    public void setReference(Ref<ChunkStore> reference) {
        if (this.reference != null && this.reference.isValid()) {
            throw new IllegalArgumentException("Entity already has a valid EntityReference: " + String.valueOf(this.reference) + " new reference " + String.valueOf(reference));
        }
        this.reference = reference;
    }

    public Ref<ChunkStore> getReference() {
        return this.reference;
    }

    public void unloadFromWorld() {
        if (this.reference != null && this.reference.isValid()) {
            throw new IllegalArgumentException("Tried to unlock used block state");
        }
        this.chunk = null;
    }

    public boolean initialize(BlockType blockType) {
        return true;
    }

    public void onUnload() {
    }

    public void validateInitialized() {
        if (!this.initialized.get()) {
            throw new IllegalArgumentException(String.valueOf(this));
        }
    }

    public int getIndex() {
        return ChunkUtil.indexBlockInColumn(this.position.x, this.position.y, this.position.z);
    }

    public void setPosition(WorldChunk chunk, @Nullable Vector3i position) {
        this.chunk = chunk;
        if (position != null) {
            position.assign(position.getX() & 0x1F, position.getY(), position.getZ() & 0x1F);
            if (position.equals(Vector3i.ZERO)) {
                ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withStackTrace(StackSize.FULL)).log("BlockState position set to (0,0,0): %s", this);
            }
            this.position = position;
        }
    }

    public void setPosition(@Nonnull Vector3i position) {
        position.assign(position.getX() & 0x1F, position.getY(), position.getZ() & 0x1F);
        if (position.equals(Vector3i.ZERO)) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withStackTrace(StackSize.FULL)).log("BlockState position set to (0,0,0): %s", this);
        }
        this.position = position;
    }

    @Nonnull
    public Vector3i getPosition() {
        return this.position.clone();
    }

    public Vector3i __internal_getPosition() {
        return this.position;
    }

    public void clearPositionForSerialization() {
        this.position = null;
    }

    public int getBlockX() {
        return this.chunk.getX() << 5 | this.position.getX();
    }

    public int getBlockY() {
        return this.position.y;
    }

    public int getBlockZ() {
        return this.chunk.getZ() << 5 | this.position.getZ();
    }

    @Nonnull
    public Vector3i getBlockPosition() {
        return new Vector3i(this.getBlockX(), this.getBlockY(), this.getBlockZ());
    }

    @Nonnull
    public Vector3d getCenteredBlockPosition() {
        BlockType blockType = this.getBlockType();
        Vector3d blockCenter = new Vector3d(0.0, 0.0, 0.0);
        blockType.getBlockCenter(this.getRotationIndex(), blockCenter);
        return blockCenter.add(this.getBlockX(), this.getBlockY(), this.getBlockZ());
    }

    @Nullable
    public WorldChunk getChunk() {
        return this.chunk;
    }

    @Nullable
    public BlockType getBlockType() {
        return this.getChunk().getBlockType(this.position);
    }

    public int getRotationIndex() {
        return this.getChunk().getRotationIndex(this.position.x, this.position.y, this.position.z);
    }

    public void invalidate() {
    }

    public void markNeedsSave() {
        this.getChunk().markNeedsSaving();
    }

    public BsonDocument saveToDocument() {
        return CODEC.encode(this).asDocument();
    }

    @Override
    @Nullable
    public Component<ChunkStore> clone() {
        BsonDocument document = CODEC.encode(this, ExtraInfo.THREAD_LOCAL.get()).asDocument();
        return (Component)CODEC.decode(document, ExtraInfo.THREAD_LOCAL.get());
    }

    @Nonnull
    public Holder<ChunkStore> toHolder() {
        if (this.reference != null && this.reference.isValid() && this.chunk != null) {
            Holder<ChunkStore> holder = ChunkStore.REGISTRY.newHolder();
            Store<ChunkStore> componentStore = this.chunk.getWorld().getChunkStore().getStore();
            Archetype<ChunkStore> archetype = componentStore.getArchetype(this.reference);
            for (int i = archetype.getMinIndex(); i < archetype.length(); ++i) {
                ComponentType<ChunkStore, ?> componentType = archetype.get(i);
                if (componentType == null) continue;
                holder.addComponent(componentType, componentStore.getComponent(this.reference, componentType));
            }
            return holder;
        }
        Holder<ChunkStore> holder = ChunkStore.REGISTRY.newHolder();
        ComponentType<ChunkStore, ?> componentType = BlockStateModule.get().getComponentType(this.getClass());
        if (componentType == null) {
            throw new IllegalArgumentException("Unable to find component type for: " + String.valueOf(this));
        }
        holder.addComponent(componentType, this);
        return holder;
    }

    @Nullable
    public static BlockState load(BsonDocument doc, @Nonnull WorldChunk chunk, @Nonnull Vector3i pos) throws NoSuchBlockStateException {
        return BlockState.load(doc, chunk, pos, chunk.getBlockType(pos.getX(), pos.getY(), pos.getZ()));
    }

    @Nullable
    public static BlockState load(BsonDocument doc, @Nullable WorldChunk chunk, Vector3i pos, BlockType blockType) throws NoSuchBlockStateException {
        BlockState blockState;
        try {
            blockState = (BlockState)CODEC.decode(doc);
        }
        catch (ACodecMapCodec.UnknownIdException e) {
            throw new NoSuchBlockStateException(e);
        }
        blockState.setPosition(chunk, pos);
        if (chunk != null) {
            if (!blockState.initialize(blockType)) {
                return null;
            }
            blockState.initialized.set(true);
        }
        return blockState;
    }

    @Nullable
    @Deprecated
    public static BlockState ensureState(@Nonnull WorldChunk worldChunk, int x, int y, int z) {
        BlockType blockType = worldChunk.getBlockType(x, y, z);
        if (blockType == null || blockType.isUnknown()) {
            return null;
        }
        StateData state = blockType.getState();
        if (state == null || state.getId() == null) {
            return null;
        }
        Vector3i position = new Vector3i(x, y, z);
        BlockState blockState = BlockStateModule.get().createBlockState(state.getId(), worldChunk, position, blockType);
        if (blockState != null) {
            worldChunk.setState(x, y, z, blockState);
        }
        return blockState;
    }

    @Deprecated
    public static BlockState getBlockState(@Nullable Ref<ChunkStore> reference, @Nonnull ComponentAccessor<ChunkStore> componentAccessor) {
        if (reference == null) {
            return null;
        }
        ComponentType componentType = BlockState.findComponentType(componentAccessor.getArchetype(reference), BlockState.class);
        if (componentType == null) {
            return null;
        }
        return (BlockState)componentAccessor.getComponent(reference, componentType);
    }

    @Nullable
    @Deprecated
    public static BlockState getBlockState(int index, @Nonnull ArchetypeChunk<ChunkStore> archetypeChunk) {
        ComponentType componentType = BlockState.findComponentType(archetypeChunk.getArchetype(), BlockState.class);
        if (componentType == null) {
            return null;
        }
        return (BlockState)archetypeChunk.getComponent(index, componentType);
    }

    @Nullable
    @Deprecated
    public static BlockState getBlockState(@Nonnull Holder<ChunkStore> holder) {
        ComponentType componentType = BlockState.findComponentType(holder.getArchetype(), BlockState.class);
        if (componentType == null) {
            return null;
        }
        return (BlockState)holder.getComponent(componentType);
    }

    @Nullable
    private static <C extends Component<ChunkStore>, T extends C> ComponentType<ChunkStore, T> findComponentType(@Nonnull Archetype<ChunkStore> archetype, @Nonnull Class<C> entityClass) {
        for (int i = archetype.getMinIndex(); i < archetype.length(); ++i) {
            ComponentType<ChunkStore, ?> componentType = archetype.get(i);
            if (componentType == null || !entityClass.isAssignableFrom(componentType.getTypeClass())) continue;
            return componentType;
        }
        return null;
    }
}

