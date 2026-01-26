/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.server;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.asset.type.blockset.config.BlockSet;
import com.hypixel.hytale.server.core.entity.InteractionChain;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.meta.DynamicMetaStore;
import com.hypixel.hytale.server.core.meta.MetaKey;
import com.hypixel.hytale.server.core.modules.blockset.BlockSetModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Label;
import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.OperationsBuilder;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class RunOnBlockTypesInteraction
extends SimpleInteraction {
    @Nonnull
    public static final BuilderCodec<RunOnBlockTypesInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(RunOnBlockTypesInteraction.class, RunOnBlockTypesInteraction::new, SimpleInteraction.CODEC).documentation("Searches for matching block types within a radius and runs interactions on each found block up to a configured maximum number of blocks.")).appendInherited(new KeyedCodec<Integer>("Range", Codec.INTEGER, true), (interaction, value) -> {
        interaction.range = value;
    }, interaction -> interaction.range, (interaction, parent) -> {
        interaction.range = parent.range;
    }).documentation("The spherical radius to search for matching block types.").addValidator(Validators.greaterThan(0)).add()).appendInherited(new KeyedCodec<T[]>("BlockSets", new ArrayCodec<String>(Codec.STRING, String[]::new), true), (interaction, value) -> {
        interaction.blockSets = value;
    }, interaction -> interaction.blockSets, (interaction, parent) -> {
        interaction.blockSets = parent.blockSets;
    }).documentation("Array of BlockSet IDs to match within the search radius.").addValidator(Validators.nonEmptyArray()).addValidatorLate(() -> BlockSet.VALIDATOR_CACHE.getArrayValidator().late()).add()).appendInherited(new KeyedCodec<Integer>("MaxCount", Codec.INTEGER, true), (interaction, value) -> {
        interaction.maxCount = value;
    }, interaction -> interaction.maxCount, (interaction, parent) -> {
        interaction.maxCount = parent.maxCount;
    }).documentation("Maximum number of block positions to select for running interactions (uses reservoir sampling).").addValidator(Validators.greaterThan(0)).add()).appendInherited(new KeyedCodec("Interactions", RootInteraction.CHILD_ASSET_CODEC, true), (interaction, value) -> {
        interaction.interactions = value;
    }, interaction -> interaction.interactions, (interaction, parent) -> {
        interaction.interactions = parent.interactions;
    }).documentation("The interaction chain to run on each found block. Can be defined inline or as a reference.").addValidatorLate(() -> RootInteraction.VALIDATOR_CACHE.getValidator().late()).add()).build();
    private static final MetaKey<List<InteractionChain>> FORKED_CHAINS = Interaction.META_REGISTRY.registerMetaObject(i -> null);
    private static final MetaKey<Boolean> ANY_SUCCEEDED = Interaction.META_REGISTRY.registerMetaObject(i -> Boolean.FALSE);
    public static final String[] EMPTY_BLOCKSETS = new String[0];
    protected int range;
    @Nonnull
    protected String[] blockSets = EMPTY_BLOCKSETS;
    protected int maxCount;
    @Nullable
    protected String interactions;

    protected RunOnBlockTypesInteraction() {
    }

    @Override
    @Nonnull
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Server;
    }

    @Override
    protected void tick0(boolean firstRun, float time, @NonNullDecl InteractionType type, @Nonnull InteractionContext context, @NonNullDecl CooldownHandler cooldownHandler) {
        DynamicMetaStore<Interaction> instanceStore = context.getInstanceStore();
        if (firstRun) {
            Vector3d position;
            CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
            assert (commandBuffer != null);
            Ref<EntityStore> ref = context.getEntity();
            TransformComponent transformComponent = commandBuffer.getComponent(ref, TransformComponent.getComponentType());
            if (transformComponent == null) {
                context.getState().state = InteractionState.Failed;
                super.tick0(firstRun, time, type, context, cooldownHandler);
                return;
            }
            World world = commandBuffer.getExternalData().getWorld();
            List<Vector3i> selectedPositions = this.searchBlocks(world, position = transformComponent.getPosition());
            if (selectedPositions.isEmpty()) {
                context.getState().state = InteractionState.Failed;
                super.tick0(firstRun, time, type, context, cooldownHandler);
                return;
            }
            if (this.interactions == null) {
                context.getState().state = InteractionState.Failed;
                super.tick0(firstRun, time, type, context, cooldownHandler);
                return;
            }
            RootInteraction rootInteraction = RootInteraction.getRootInteractionOrUnknown(this.interactions);
            ObjectArrayList<InteractionChain> chains = new ObjectArrayList<InteractionChain>(selectedPositions.size());
            for (Vector3i blockPos : selectedPositions) {
                InteractionContext forkedContext = context.duplicate();
                BlockPosition blockPosition = new BlockPosition(blockPos.x, blockPos.y, blockPos.z);
                forkedContext.getMetaStore().putMetaObject(Interaction.TARGET_BLOCK, blockPosition);
                forkedContext.getMetaStore().putMetaObject(Interaction.TARGET_BLOCK_RAW, blockPosition);
                BlockPosition baseBlock = world.getBaseBlock(blockPosition);
                forkedContext.getMetaStore().putMetaObject(Interaction.TARGET_BLOCK, baseBlock);
                InteractionChain chain = context.fork(forkedContext, rootInteraction, false);
                chains.add(chain);
            }
            instanceStore.putMetaObject(FORKED_CHAINS, chains);
            instanceStore.putMetaObject(ANY_SUCCEEDED, Boolean.FALSE);
            context.getState().state = InteractionState.NotFinished;
            return;
        }
        List<InteractionChain> chains = instanceStore.getMetaObject(FORKED_CHAINS);
        if (chains == null || chains.isEmpty()) {
            context.getState().state = InteractionState.Failed;
            super.tick0(firstRun, time, type, context, cooldownHandler);
            return;
        }
        boolean allFinished = true;
        boolean anySucceeded = instanceStore.getMetaObject(ANY_SUCCEEDED);
        for (InteractionChain chain : chains) {
            switch (chain.getServerState()) {
                case NotFinished: {
                    allFinished = false;
                    break;
                }
                case Finished: {
                    anySucceeded = true;
                    break;
                }
            }
        }
        instanceStore.putMetaObject(ANY_SUCCEEDED, anySucceeded);
        if (!allFinished) {
            context.getState().state = InteractionState.NotFinished;
            return;
        }
        context.getState().state = anySucceeded ? InteractionState.Finished : InteractionState.Failed;
        super.tick0(firstRun, time, type, context, cooldownHandler);
    }

    @Override
    protected void simulateTick0(boolean firstRun, float time, @NonNullDecl InteractionType type, @Nonnull InteractionContext context, @NonNullDecl CooldownHandler cooldownHandler) {
    }

    @Override
    public void compile(@Nonnull OperationsBuilder builder) {
        if (this.next == null && this.failed == null) {
            builder.addOperation(this);
            return;
        }
        Label failedLabel = builder.createUnresolvedLabel();
        Label endLabel = builder.createUnresolvedLabel();
        builder.addOperation(this, failedLabel);
        if (this.next != null) {
            Interaction nextInteraction = Interaction.getInteractionOrUnknown(this.next);
            nextInteraction.compile(builder);
        }
        if (this.failed != null) {
            builder.jump(endLabel);
        }
        builder.resolveLabel(failedLabel);
        if (this.failed != null) {
            Interaction failedInteraction = Interaction.getInteractionOrUnknown(this.failed);
            failedInteraction.compile(builder);
        }
        builder.resolveLabel(endLabel);
    }

    @Nonnull
    private List<Vector3i> searchBlocks(@Nonnull World world, @Nonnull Vector3d position) {
        IntList blockIds = this.getBlockIds();
        if (blockIds.isEmpty()) {
            return List.of();
        }
        int originX = MathUtil.floor(position.x);
        int originY = MathUtil.floor(position.y);
        int originZ = MathUtil.floor(position.z);
        int radiusSquared = this.range * this.range;
        BlockSearchConsumer consumer = new BlockSearchConsumer(originX, originY, originZ, radiusSquared, this.maxCount);
        IntOpenHashSet internalIdHolder = new IntOpenHashSet();
        int minY = Math.max(0, originY - this.range);
        int maxY = Math.min(319, originY + this.range);
        for (int x = originX - this.range & 0xFFFFFFE0; x < originX + this.range; x += 32) {
            for (int z = originZ - this.range & 0xFFFFFFE0; z < originZ + this.range; z += 32) {
                WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(x, z));
                if (chunk == null) continue;
                BlockChunk blockChunk = chunk.getBlockChunk();
                for (int y = minY; y < maxY; y += 32) {
                    BlockSection section;
                    int sectionIndex = ChunkUtil.indexSection(y);
                    if (sectionIndex < 0 || sectionIndex >= 10 || (section = blockChunk.getSectionAtIndex(sectionIndex)).isSolidAir() || !section.containsAny(blockIds)) continue;
                    consumer.setSection(x, z, sectionIndex);
                    section.find(blockIds, internalIdHolder, consumer);
                    internalIdHolder.clear();
                }
            }
        }
        return consumer.getPickedPositions();
    }

    @Nonnull
    private IntList getBlockIds() {
        IntArrayList result = new IntArrayList();
        BlockSetModule blockSetModule = BlockSetModule.getInstance();
        Int2ObjectMap<IntSet> blockSetMap = blockSetModule.getBlockSets();
        for (String blockSetName : this.blockSets) {
            IntSet blockIdsInSet;
            int blockSetIndex = BlockSet.getAssetMap().getIndex(blockSetName);
            if (blockSetIndex == Integer.MIN_VALUE || (blockIdsInSet = (IntSet)blockSetMap.get(blockSetIndex)) == null) continue;
            result.addAll(blockIdsInSet);
        }
        return result;
    }

    @Override
    public boolean needsRemoteSync() {
        return true;
    }

    @Override
    @Nonnull
    protected com.hypixel.hytale.protocol.Interaction generatePacket() {
        return new com.hypixel.hytale.protocol.SimpleInteraction();
    }

    @Override
    protected void configurePacket(com.hypixel.hytale.protocol.Interaction packet) {
        super.configurePacket(packet);
        com.hypixel.hytale.protocol.SimpleInteraction p = (com.hypixel.hytale.protocol.SimpleInteraction)packet;
        p.next = Interaction.getInteractionIdOrUnknown(this.next);
        p.failed = Interaction.getInteractionIdOrUnknown(this.failed);
    }

    @Override
    @Nonnull
    public String toString() {
        return "RunOnBlockTypesInteraction{range=" + this.range + ", maxCount=" + this.maxCount + ", interactions='" + this.interactions + "'} " + super.toString();
    }

    private static class BlockSearchConsumer
    implements IntConsumer {
        private final int originX;
        private final int originY;
        private final int originZ;
        private final int radiusSquared;
        private final int maxCount;
        private final List<Vector3i> picked;
        private int seen = 0;
        private int chunkWorldX;
        private int chunkWorldZ;
        private int sectionBaseY;

        BlockSearchConsumer(int originX, int originY, int originZ, int radiusSquared, int maxCount) {
            this.originX = originX;
            this.originY = originY;
            this.originZ = originZ;
            this.radiusSquared = radiusSquared;
            this.maxCount = maxCount;
            this.picked = new ObjectArrayList<Vector3i>(maxCount);
        }

        void setSection(int chunkWorldX, int chunkWorldZ, int sectionIndex) {
            this.chunkWorldX = chunkWorldX;
            this.chunkWorldZ = chunkWorldZ;
            this.sectionBaseY = sectionIndex * 32;
        }

        @Override
        public void accept(int blockIndex) {
            int localZ;
            int worldZ;
            int dz;
            int localY;
            int worldY;
            int dy;
            int localX = ChunkUtil.xFromIndex(blockIndex);
            int worldX = this.chunkWorldX + localX;
            int dx = worldX - this.originX;
            if (dx * dx + (dy = (worldY = this.sectionBaseY + (localY = ChunkUtil.yFromIndex(blockIndex))) - this.originY) * dy + (dz = (worldZ = this.chunkWorldZ + (localZ = ChunkUtil.zFromIndex(blockIndex))) - this.originZ) * dz > this.radiusSquared) {
                return;
            }
            if (this.picked.size() < this.maxCount) {
                this.picked.add(new Vector3i(worldX, worldY, worldZ));
            } else {
                int j = ThreadLocalRandom.current().nextInt(this.seen + 1);
                if (j < this.maxCount) {
                    this.picked.set(j, new Vector3i(worldX, worldY, worldZ));
                }
            }
            ++this.seen;
        }

        @Nonnull
        List<Vector3i> getPickedPositions() {
            return this.picked;
        }
    }
}

