/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.farming;

import com.hypixel.hytale.builtin.adventure.farming.FarmingPlugin;
import com.hypixel.hytale.builtin.adventure.farming.FarmingUtil;
import com.hypixel.hytale.builtin.adventure.farming.component.CoopResidentComponent;
import com.hypixel.hytale.builtin.adventure.farming.config.FarmingCoopAsset;
import com.hypixel.hytale.builtin.adventure.farming.config.stages.BlockStateFarmingStageData;
import com.hypixel.hytale.builtin.adventure.farming.config.stages.BlockTypeFarmingStageData;
import com.hypixel.hytale.builtin.adventure.farming.states.CoopBlock;
import com.hypixel.hytale.builtin.adventure.farming.states.FarmingBlock;
import com.hypixel.hytale.builtin.adventure.farming.states.FarmingBlockState;
import com.hypixel.hytale.builtin.adventure.farming.states.TilledSoilBlock;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.Rangef;
import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.farming.FarmingData;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.farming.FarmingStageData;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.ChunkColumn;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import java.lang.runtime.SwitchBootstraps;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FarmingSystems {
    private static boolean hasCropAbove(@Nonnull BlockChunk blockChunk, int x, int y, int z) {
        if (y + 1 >= 320) {
            return false;
        }
        BlockSection aboveBlockSection = blockChunk.getSectionAtBlockY(y + 1);
        if (aboveBlockSection == null) {
            return false;
        }
        BlockType aboveBlockType = BlockType.getAssetMap().getAsset(aboveBlockSection.get(x, y + 1, z));
        if (aboveBlockType == null) {
            return false;
        }
        FarmingData farmingConfig = aboveBlockType.getFarming();
        return farmingConfig != null && farmingConfig.getStages() != null;
    }

    private static boolean updateSoilDecayTime(@Nonnull CommandBuffer<ChunkStore> commandBuffer, @Nonnull TilledSoilBlock soilBlock, @Nullable BlockType blockType) {
        if (blockType == null) {
            return false;
        }
        FarmingData farming = blockType.getFarming();
        if (farming == null || farming.getSoilConfig() == null) {
            return false;
        }
        FarmingData.SoilConfig soilConfig = farming.getSoilConfig();
        Rangef range = soilConfig.getLifetime();
        if (range == null) {
            return false;
        }
        double baseDuration = (double)range.min + (double)(range.max - range.min) * ThreadLocalRandom.current().nextDouble();
        Instant currentTime = commandBuffer.getExternalData().getWorld().getEntityStore().getStore().getResource(WorldTimeResource.getResourceType()).getGameTime();
        Instant endTime = currentTime.plus(Math.round(baseDuration), ChronoUnit.SECONDS);
        soilBlock.setDecayTime(endTime);
        return true;
    }

    @Deprecated(forRemoval=true)
    public static class MigrateFarming
    extends BlockModule.MigrationSystem {
        @Override
        public void onEntityAdd(@Nonnull Holder<ChunkStore> holder, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store) {
            FarmingBlockState oldState = holder.getComponent(FarmingPlugin.get().getFarmingBlockStateComponentType());
            FarmingBlock farming = new FarmingBlock();
            farming.setGrowthProgress(oldState.getCurrentFarmingStageIndex());
            farming.setCurrentStageSet(oldState.getCurrentFarmingStageSetName());
            farming.setSpreadRate(oldState.getSpreadRate());
            holder.putComponent(FarmingBlock.getComponentType(), farming);
            holder.removeComponent(FarmingPlugin.get().getFarmingBlockStateComponentType());
        }

        @Override
        public void onEntityRemoved(@Nonnull Holder<ChunkStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store) {
        }

        @Override
        @Nullable
        public Query<ChunkStore> getQuery() {
            return FarmingPlugin.get().getFarmingBlockStateComponentType();
        }
    }

    public static class CoopResidentTicking
    extends EntityTickingSystem<EntityStore> {
        @Nonnull
        private final ComponentType<EntityStore, CoopResidentComponent> coopResidentComponentType;

        public CoopResidentTicking(@Nonnull ComponentType<EntityStore, CoopResidentComponent> coopResidentComponentType) {
            this.coopResidentComponentType = coopResidentComponentType;
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return this.coopResidentComponentType;
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            CoopResidentComponent coopResidentComponent = archetypeChunk.getComponent(index, this.coopResidentComponentType);
            assert (coopResidentComponent != null);
            if (coopResidentComponent.getMarkedForDespawn()) {
                commandBuffer.removeEntity(archetypeChunk.getReferenceTo(index), RemoveReason.REMOVE);
            }
        }
    }

    public static class CoopResidentEntitySystem
    extends RefSystem<EntityStore> {
        @Nonnull
        private final ComponentType<EntityStore, CoopResidentComponent> coopResidentComponentType;
        @Nonnull
        private final ComponentType<EntityStore, UUIDComponent> uuidComponentType;

        public CoopResidentEntitySystem(@Nonnull ComponentType<EntityStore, CoopResidentComponent> coopResidentComponentType, @Nonnull ComponentType<EntityStore, UUIDComponent> uuidComponentType) {
            this.coopResidentComponentType = coopResidentComponentType;
            this.uuidComponentType = uuidComponentType;
        }

        @Override
        @Nonnull
        public Query<EntityStore> getQuery() {
            return this.coopResidentComponentType;
        }

        @Override
        public void onEntityAdded(@Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        }

        @Override
        public void onEntityRemove(@Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
            long chunkIndex;
            if (reason == RemoveReason.UNLOAD) {
                return;
            }
            UUIDComponent uuidComponent = commandBuffer.getComponent(ref, this.uuidComponentType);
            if (uuidComponent == null) {
                return;
            }
            UUID uuid = uuidComponent.getUuid();
            CoopResidentComponent coopResidentComponent = commandBuffer.getComponent(ref, this.coopResidentComponentType);
            if (coopResidentComponent == null) {
                return;
            }
            Vector3i coopPosition = coopResidentComponent.getCoopLocation();
            World world = commandBuffer.getExternalData().getWorld();
            WorldChunk worldChunkComponent = world.getChunkIfLoaded(chunkIndex = ChunkUtil.indexChunkFromBlock(coopPosition.x, coopPosition.z));
            if (worldChunkComponent == null) {
                return;
            }
            Ref<ChunkStore> chunkRef = world.getChunkStore().getChunkReference(chunkIndex);
            if (chunkRef == null || !chunkRef.isValid()) {
                return;
            }
            Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
            ChunkColumn chunkColumnComponent = chunkStore.getComponent(chunkRef, ChunkColumn.getComponentType());
            if (chunkColumnComponent == null) {
                return;
            }
            BlockChunk blockChunkComponent = chunkStore.getComponent(chunkRef, BlockChunk.getComponentType());
            if (blockChunkComponent == null) {
                return;
            }
            Ref<ChunkStore> sectionRef = chunkColumnComponent.getSection(ChunkUtil.chunkCoordinate(coopPosition.y));
            if (sectionRef == null || !sectionRef.isValid()) {
                return;
            }
            BlockComponentChunk blockComponentChunk = chunkStore.getComponent(chunkRef, BlockComponentChunk.getComponentType());
            if (blockComponentChunk == null) {
                return;
            }
            int blockIndexColumn = ChunkUtil.indexBlockInColumn(coopPosition.x, coopPosition.y, coopPosition.z);
            Ref<ChunkStore> coopEntityReference = blockComponentChunk.getEntityReference(blockIndexColumn);
            if (coopEntityReference == null) {
                return;
            }
            CoopBlock coop = chunkStore.getComponent(coopEntityReference, CoopBlock.getComponentType());
            if (coop == null) {
                return;
            }
            coop.handleResidentDespawn(uuid);
        }
    }

    public static class OnCoopAdded
    extends RefSystem<ChunkStore> {
        @Nonnull
        private final ComponentType<ChunkStore, BlockModule.BlockStateInfo> blockStateInfoComponentType;
        @Nonnull
        private final ComponentType<ChunkStore, CoopBlock> coopBlockComponentType;
        @Nonnull
        private final Query<ChunkStore> query;

        public OnCoopAdded(@Nonnull ComponentType<ChunkStore, BlockModule.BlockStateInfo> blockStateInfoComponentType, @Nonnull ComponentType<ChunkStore, CoopBlock> coopBlockComponentType) {
            this.blockStateInfoComponentType = blockStateInfoComponentType;
            this.coopBlockComponentType = coopBlockComponentType;
            this.query = Query.and(blockStateInfoComponentType, coopBlockComponentType);
        }

        @Override
        public void onEntityAdded(@Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
            CoopBlock coopBlockComponent = commandBuffer.getComponent(ref, this.coopBlockComponentType);
            assert (coopBlockComponent != null);
            BlockModule.BlockStateInfo blockStateInfoComponent = commandBuffer.getComponent(ref, this.blockStateInfoComponentType);
            assert (blockStateInfoComponent != null);
            World world = commandBuffer.getExternalData().getWorld();
            Store<EntityStore> entityStore = world.getEntityStore().getStore();
            WorldTimeResource worldTimeResource = entityStore.getResource(WorldTimeResource.getResourceType());
            int x = ChunkUtil.xFromBlockInColumn(blockStateInfoComponent.getIndex());
            int y = ChunkUtil.yFromBlockInColumn(blockStateInfoComponent.getIndex());
            int z = ChunkUtil.zFromBlockInColumn(blockStateInfoComponent.getIndex());
            Ref<ChunkStore> chunkRef = blockStateInfoComponent.getChunkRef();
            if (!chunkRef.isValid()) {
                return;
            }
            BlockChunk blockChunkComponent = commandBuffer.getComponent(chunkRef, BlockChunk.getComponentType());
            if (blockChunkComponent == null) {
                return;
            }
            BlockSection blockSection = blockChunkComponent.getSectionAtBlockY(y);
            blockSection.scheduleTick(ChunkUtil.indexBlock(x, y, z), coopBlockComponent.getNextScheduledTick(worldTimeResource));
        }

        @Override
        public void onEntityRemove(@Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
            if (reason == RemoveReason.UNLOAD) {
                return;
            }
            CoopBlock coopBlockComponent = commandBuffer.getComponent(ref, this.coopBlockComponentType);
            assert (coopBlockComponent != null);
            BlockModule.BlockStateInfo blockStateInfoComponent = commandBuffer.getComponent(ref, this.blockStateInfoComponentType);
            assert (blockStateInfoComponent != null);
            int index = blockStateInfoComponent.getIndex();
            int x = ChunkUtil.xFromBlockInColumn(index);
            int y = ChunkUtil.yFromBlockInColumn(index);
            int z = ChunkUtil.zFromBlockInColumn(index);
            BlockChunk blockChunkComponent = commandBuffer.getComponent(blockStateInfoComponent.getChunkRef(), BlockChunk.getComponentType());
            assert (blockChunkComponent != null);
            ChunkColumn chunkColumnComponent = commandBuffer.getComponent(blockStateInfoComponent.getChunkRef(), ChunkColumn.getComponentType());
            assert (chunkColumnComponent != null);
            Ref<ChunkStore> sectionRef = chunkColumnComponent.getSection(ChunkUtil.chunkCoordinate(y));
            assert (sectionRef != null);
            BlockSection blockSectionComponent = commandBuffer.getComponent(sectionRef, BlockSection.getComponentType());
            assert (blockSectionComponent != null);
            ChunkSection chunkSectionComponent = commandBuffer.getComponent(sectionRef, ChunkSection.getComponentType());
            assert (chunkSectionComponent != null);
            int worldX = ChunkUtil.worldCoordFromLocalCoord(chunkSectionComponent.getX(), x);
            int worldY = ChunkUtil.worldCoordFromLocalCoord(chunkSectionComponent.getY(), y);
            int worldZ = ChunkUtil.worldCoordFromLocalCoord(chunkSectionComponent.getZ(), z);
            World world = commandBuffer.getExternalData().getWorld();
            Store<EntityStore> entityStore = world.getEntityStore().getStore();
            WorldTimeResource worldTimeResource = entityStore.getResource(WorldTimeResource.getResourceType());
            coopBlockComponent.handleBlockBroken(world, worldTimeResource, entityStore, worldX, worldY, worldZ);
        }

        @Override
        @Nonnull
        public Query<ChunkStore> getQuery() {
            return this.query;
        }
    }

    public static class Ticking
    extends EntityTickingSystem<ChunkStore> {
        @Nonnull
        private final ComponentType<ChunkStore, BlockSection> blockSectionComponentType;
        @Nonnull
        private final ComponentType<ChunkStore, ChunkSection> chunkSectionComponentType;
        @Nonnull
        private final ComponentType<ChunkStore, FarmingBlock> farmingBlockComponentType;
        @Nonnull
        private final ComponentType<ChunkStore, TilledSoilBlock> tilledSoilBlockComponentType;
        @Nonnull
        private final ComponentType<ChunkStore, CoopBlock> coopBlockComponentType;
        @Nonnull
        private final Query<ChunkStore> query;

        public Ticking(@Nonnull ComponentType<ChunkStore, BlockSection> blockSectionComponentType, @Nonnull ComponentType<ChunkStore, ChunkSection> chunkSectionComponentType, @Nonnull ComponentType<ChunkStore, FarmingBlock> farmingBlockComponentType, @Nonnull ComponentType<ChunkStore, TilledSoilBlock> tilledSoilBlockComponentType, @Nonnull ComponentType<ChunkStore, CoopBlock> coopBlockComponentType) {
            this.blockSectionComponentType = blockSectionComponentType;
            this.chunkSectionComponentType = chunkSectionComponentType;
            this.farmingBlockComponentType = farmingBlockComponentType;
            this.tilledSoilBlockComponentType = tilledSoilBlockComponentType;
            this.coopBlockComponentType = coopBlockComponentType;
            this.query = Query.and(blockSectionComponentType, chunkSectionComponentType);
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<ChunkStore> archetypeChunk, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
            BlockSection blockSectionComponent = archetypeChunk.getComponent(index, this.blockSectionComponentType);
            assert (blockSectionComponent != null);
            if (blockSectionComponent.getTickingBlocksCountCopy() == 0) {
                return;
            }
            ChunkSection chunkSectionComponent = archetypeChunk.getComponent(index, this.chunkSectionComponentType);
            assert (chunkSectionComponent != null);
            Ref<ChunkStore> chunkColumnRef = chunkSectionComponent.getChunkColumnReference();
            if (chunkColumnRef == null || !chunkColumnRef.isValid()) {
                return;
            }
            BlockComponentChunk blockComponentChunk = commandBuffer.getComponent(chunkColumnRef, BlockComponentChunk.getComponentType());
            assert (blockComponentChunk != null);
            Ref<ChunkStore> ref = archetypeChunk.getReferenceTo(index);
            BlockChunk blockChunk = commandBuffer.getComponent(chunkColumnRef, BlockChunk.getComponentType());
            assert (blockChunk != null);
            blockSectionComponent.forEachTicking(blockComponentChunk, commandBuffer, chunkSectionComponent.getY(), (blockComponentChunk1, commandBuffer1, localX, localY, localZ, blockId) -> {
                Ref<ChunkStore> blockRef = blockComponentChunk1.getEntityReference(ChunkUtil.indexBlockInColumn(localX, localY, localZ));
                if (blockRef == null) {
                    return BlockTickStrategy.IGNORED;
                }
                FarmingBlock farmingBlockComp = commandBuffer1.getComponent(blockRef, this.farmingBlockComponentType);
                if (farmingBlockComp != null) {
                    FarmingUtil.tickFarming(commandBuffer1, blockChunk, blockSectionComponent, ref, blockRef, farmingBlockComp, localX, localY, localZ, false);
                    return BlockTickStrategy.SLEEP;
                }
                TilledSoilBlock tilledSoilBlockComp = commandBuffer1.getComponent(blockRef, this.tilledSoilBlockComponentType);
                if (tilledSoilBlockComp != null) {
                    Ticking.tickSoil(commandBuffer1, blockRef, tilledSoilBlockComp);
                    return BlockTickStrategy.SLEEP;
                }
                CoopBlock coopBlockComp = commandBuffer1.getComponent(blockRef, this.coopBlockComponentType);
                if (coopBlockComp != null) {
                    Ticking.tickCoop(commandBuffer1, blockRef, coopBlockComp);
                    return BlockTickStrategy.SLEEP;
                }
                return BlockTickStrategy.IGNORED;
            });
        }

        private static void tickSoil(@Nonnull CommandBuffer<ChunkStore> commandBuffer, @Nonnull Ref<ChunkStore> blockRef, @Nonnull TilledSoilBlock soilBlock) {
            String targetBlock;
            BlockModule.BlockStateInfo blockStateInfoComponent = commandBuffer.getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());
            assert (blockStateInfoComponent != null);
            int index = blockStateInfoComponent.getIndex();
            int x = ChunkUtil.xFromBlockInColumn(index);
            int y = ChunkUtil.yFromBlockInColumn(index);
            int z = ChunkUtil.zFromBlockInColumn(index);
            if (y >= 320) {
                return;
            }
            Ref<ChunkStore> chunkRef = blockStateInfoComponent.getChunkRef();
            if (!chunkRef.isValid()) {
                return;
            }
            BlockChunk blockChunkComponent = commandBuffer.getComponent(chunkRef, BlockChunk.getComponentType());
            if (blockChunkComponent == null) {
                return;
            }
            BlockSection blockSection = blockChunkComponent.getSectionAtBlockY(y);
            boolean hasCrop = FarmingSystems.hasCropAbove(blockChunkComponent, x, y, z);
            int blockId = blockSection.get(x, y, z);
            BlockType blockTypeAsset = BlockType.getAssetMap().getAsset(blockId);
            if (blockTypeAsset == null) {
                return;
            }
            Instant currentTime = commandBuffer.getExternalData().getWorld().getEntityStore().getStore().getResource(WorldTimeResource.getResourceType()).getGameTime();
            Instant decayTime = soilBlock.getDecayTime();
            if (soilBlock.isPlanted() && !hasCrop) {
                if (!FarmingSystems.updateSoilDecayTime(commandBuffer, soilBlock, blockTypeAsset)) {
                    return;
                }
                if (decayTime != null) {
                    blockSection.scheduleTick(ChunkUtil.indexBlock(x, y, z), decayTime);
                }
            } else if (!soilBlock.isPlanted() && !hasCrop) {
                if (decayTime == null || !decayTime.isAfter(currentTime)) {
                    if (blockTypeAsset.getFarming() == null || blockTypeAsset.getFarming().getSoilConfig() == null) {
                        return;
                    }
                    FarmingData.SoilConfig soilConfig = blockTypeAsset.getFarming().getSoilConfig();
                    String targetBlock2 = soilConfig.getTargetBlock();
                    if (targetBlock2 == null) {
                        return;
                    }
                    int targetBlockId = BlockType.getAssetMap().getIndex(targetBlock2);
                    if (targetBlockId == Integer.MIN_VALUE) {
                        return;
                    }
                    BlockType targetBlockType = BlockType.getAssetMap().getAsset(targetBlockId);
                    int rotationIndex = blockSection.getRotationIndex(x, y, z);
                    WorldChunk worldChunkComponent = commandBuffer.getComponent(chunkRef, WorldChunk.getComponentType());
                    assert (worldChunkComponent != null);
                    commandBuffer.run(_store -> worldChunkComponent.setBlock(x, y, z, targetBlockId, targetBlockType, rotationIndex, 0, 0));
                    return;
                }
            } else if (hasCrop) {
                soilBlock.setDecayTime(null);
            }
            if ((targetBlock = soilBlock.computeBlockType(currentTime, blockTypeAsset)) != null && !targetBlock.equals(blockTypeAsset.getId())) {
                WorldChunk worldChunkComponent = commandBuffer.getComponent(chunkRef, WorldChunk.getComponentType());
                assert (worldChunkComponent != null);
                int rotationIndex = blockSection.getRotationIndex(x, y, z);
                int targetBlockId = BlockType.getAssetMap().getIndex(targetBlock);
                BlockType targetBlockType = BlockType.getAssetMap().getAsset(targetBlockId);
                commandBuffer.run(_store -> worldChunkComponent.setBlock(x, y, z, targetBlockId, targetBlockType, rotationIndex, 0, 2));
            }
            soilBlock.setPlanted(hasCrop);
        }

        private static void tickCoop(@Nonnull CommandBuffer<ChunkStore> commandBuffer, @Nonnull Ref<ChunkStore> blockRef, @Nonnull CoopBlock coopBlock) {
            BlockModule.BlockStateInfo blockStateInfoComponent = commandBuffer.getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());
            assert (blockStateInfoComponent != null);
            Store<EntityStore> store = commandBuffer.getExternalData().getWorld().getEntityStore().getStore();
            WorldTimeResource worldTimeResource = store.getResource(WorldTimeResource.getResourceType());
            FarmingCoopAsset coopAsset = coopBlock.getCoopAsset();
            if (coopAsset == null) {
                return;
            }
            int index = blockStateInfoComponent.getIndex();
            int x = ChunkUtil.xFromBlockInColumn(index);
            int y = ChunkUtil.yFromBlockInColumn(index);
            int z = ChunkUtil.zFromBlockInColumn(index);
            BlockChunk blockChunkComponent = commandBuffer.getComponent(blockStateInfoComponent.getChunkRef(), BlockChunk.getComponentType());
            assert (blockChunkComponent != null);
            ChunkColumn chunkColumnComponent = commandBuffer.getComponent(blockStateInfoComponent.getChunkRef(), ChunkColumn.getComponentType());
            assert (chunkColumnComponent != null);
            Ref<ChunkStore> sectionRef = chunkColumnComponent.getSection(ChunkUtil.chunkCoordinate(y));
            assert (sectionRef != null);
            BlockSection blockSectionComponent = commandBuffer.getComponent(sectionRef, BlockSection.getComponentType());
            assert (blockSectionComponent != null);
            ChunkSection chunkSectionComponent = commandBuffer.getComponent(sectionRef, ChunkSection.getComponentType());
            assert (chunkSectionComponent != null);
            int worldX = ChunkUtil.worldCoordFromLocalCoord(chunkSectionComponent.getX(), x);
            int worldY = ChunkUtil.worldCoordFromLocalCoord(chunkSectionComponent.getY(), y);
            int worldZ = ChunkUtil.worldCoordFromLocalCoord(chunkSectionComponent.getZ(), z);
            World world = commandBuffer.getExternalData().getWorld();
            long chunkIndex = ChunkUtil.indexChunkFromBlock(worldX, worldZ);
            WorldChunk chunk = world.getChunkIfInMemory(chunkIndex);
            double blockRotation = chunk.getRotation(worldX, worldY, worldZ).yaw().getRadians();
            Vector3d spawnOffset = new Vector3d().assign(coopAsset.getResidentSpawnOffset()).rotateY((float)blockRotation);
            Vector3i coopLocation = new Vector3i(worldX, worldY, worldZ);
            boolean tryCapture = coopAsset.getCaptureWildNPCsInRange();
            float captureRange = coopAsset.getWildCaptureRadius();
            if (tryCapture && captureRange >= 0.0f) {
                world.execute(() -> {
                    List<Ref<EntityStore>> entities = TargetUtil.getAllEntitiesInSphere(coopLocation.toVector3d(), captureRange, store);
                    for (Ref<EntityStore> entity : entities) {
                        coopBlock.tryPutWildResidentFromWild(store, entity, worldTimeResource, coopLocation);
                    }
                });
            }
            if (coopBlock.shouldResidentsBeInCoop(worldTimeResource)) {
                world.execute(() -> coopBlock.ensureNoResidentsInWorld(store));
            } else {
                world.execute(() -> {
                    coopBlock.ensureSpawnResidentsInWorld(world, store, coopLocation.toVector3d(), spawnOffset);
                    coopBlock.generateProduceToInventory(worldTimeResource);
                    Vector3i blockPos = new Vector3i(worldX, worldY, worldZ);
                    BlockType currentBlockType = world.getBlockType(blockPos);
                    assert (currentBlockType != null);
                    chunk.setBlockInteractionState(blockPos, currentBlockType, coopBlock.hasProduce() ? "Produce_Ready" : "default");
                });
            }
            Instant nextTickInstant = coopBlock.getNextScheduledTick(worldTimeResource);
            if (nextTickInstant != null) {
                blockSectionComponent.scheduleTick(ChunkUtil.indexBlock(x, y, z), nextTickInstant);
            }
        }

        @Override
        @Nonnull
        public Query<ChunkStore> getQuery() {
            return this.query;
        }
    }

    public static class OnFarmBlockAdded
    extends RefSystem<ChunkStore> {
        @Nonnull
        private final ComponentType<ChunkStore, BlockModule.BlockStateInfo> blockStateInfoComponentType;
        @Nonnull
        private final ComponentType<ChunkStore, FarmingBlock> farmingBlockComponentType;
        @Nonnull
        private final Query<ChunkStore> query;

        public OnFarmBlockAdded(@Nonnull ComponentType<ChunkStore, BlockModule.BlockStateInfo> blockStateInfoComponentType, @Nonnull ComponentType<ChunkStore, FarmingBlock> farmingBlockComponentType) {
            this.blockStateInfoComponentType = blockStateInfoComponentType;
            this.farmingBlockComponentType = farmingBlockComponentType;
            this.query = Query.and(blockStateInfoComponentType, farmingBlockComponentType);
        }

        @Override
        public void onEntityAdded(@Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
            int index;
            FarmingBlock farmingBlockComponent = commandBuffer.getComponent(ref, this.farmingBlockComponentType);
            assert (farmingBlockComponent != null);
            BlockModule.BlockStateInfo blockStateInfoComponent = commandBuffer.getComponent(ref, this.blockStateInfoComponentType);
            assert (blockStateInfoComponent != null);
            Ref<ChunkStore> chunkRef = blockStateInfoComponent.getChunkRef();
            if (!chunkRef.isValid()) {
                return;
            }
            BlockChunk blockChunkComponent = commandBuffer.getComponent(chunkRef, BlockChunk.getComponentType());
            assert (blockChunkComponent != null);
            World world = store.getExternalData().getWorld();
            Store<EntityStore> entityStore = world.getEntityStore().getStore();
            WorldTimeResource worldTimeResource = entityStore.getResource(WorldTimeResource.getResourceType());
            if (farmingBlockComponent.getLastTickGameTime() == null) {
                FarmingStageData[] stages;
                index = blockStateInfoComponent.getIndex();
                int blockId = blockChunkComponent.getBlock(ChunkUtil.xFromBlockInColumn(index), ChunkUtil.yFromBlockInColumn(index), ChunkUtil.zFromBlockInColumn(index));
                BlockType blockType = BlockType.getAssetMap().getAsset(blockId);
                if (blockType == null) {
                    return;
                }
                FarmingData blockTypeFarming = blockType.getFarming();
                if (blockTypeFarming == null) {
                    return;
                }
                String startingStageSet = blockTypeFarming.getStartingStageSet();
                farmingBlockComponent.setCurrentStageSet(startingStageSet);
                farmingBlockComponent.setLastTickGameTime(worldTimeResource.getGameTime());
                blockChunkComponent.markNeedsSaving();
                Map<String, FarmingStageData[]> farmingStages = blockTypeFarming.getStages();
                if (farmingStages != null && (stages = farmingStages.get(startingStageSet)) != null && stages.length > 0) {
                    boolean found = false;
                    block4: for (int i = 0; i < stages.length; ++i) {
                        FarmingStageData farmingStageData;
                        FarmingStageData stage = stages[i];
                        Objects.requireNonNull(stage);
                        int n = 0;
                        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{BlockTypeFarmingStageData.class, BlockStateFarmingStageData.class}, (Object)farmingStageData, n)) {
                            case 0: {
                                BlockTypeFarmingStageData data = (BlockTypeFarmingStageData)farmingStageData;
                                if (!data.getBlock().equals(blockType.getId())) continue block4;
                                farmingBlockComponent.setGrowthProgress(i);
                                found = true;
                                continue block4;
                            }
                            case 1: {
                                BlockStateFarmingStageData data = (BlockStateFarmingStageData)farmingStageData;
                                BlockType stateBlockType = blockType.getBlockForState(data.getState());
                                if (stateBlockType == null || !stateBlockType.getId().equals(blockType.getId())) continue block4;
                                farmingBlockComponent.setGrowthProgress(i);
                                found = true;
                                continue block4;
                            }
                        }
                    }
                    if (!found) {
                        ChunkColumn chunkColumnComponent = commandBuffer.getComponent(chunkRef, ChunkColumn.getComponentType());
                        assert (chunkColumnComponent != null);
                        int chunkCoordinate = ChunkUtil.chunkCoordinate(ChunkUtil.yFromBlockInColumn(index));
                        Ref<ChunkStore> sectionRef = chunkColumnComponent.getSection(chunkCoordinate);
                        if (sectionRef != null && sectionRef.isValid()) {
                            stages[0].apply(commandBuffer, sectionRef, ref, ChunkUtil.xFromBlockInColumn(index), ChunkUtil.yFromBlockInColumn(index), ChunkUtil.zFromBlockInColumn(index), null);
                        }
                    }
                }
            }
            if (farmingBlockComponent.getLastTickGameTime() == null) {
                farmingBlockComponent.setLastTickGameTime(worldTimeResource.getGameTime());
                blockChunkComponent.markNeedsSaving();
            }
            index = blockStateInfoComponent.getIndex();
            int x = ChunkUtil.xFromBlockInColumn(index);
            int y = ChunkUtil.yFromBlockInColumn(index);
            int z = ChunkUtil.zFromBlockInColumn(index);
            BlockComponentChunk blockComponentChunk = commandBuffer.getComponent(chunkRef, BlockComponentChunk.getComponentType());
            assert (blockComponentChunk != null);
            ChunkColumn chunkColumnComponent = commandBuffer.getComponent(chunkRef, ChunkColumn.getComponentType());
            assert (chunkColumnComponent != null);
            Ref<ChunkStore> section = chunkColumnComponent.getSection(ChunkUtil.chunkCoordinate(y));
            if (section == null) {
                return;
            }
            BlockSection blockSectionComponent = commandBuffer.getComponent(section, BlockSection.getComponentType());
            assert (blockSectionComponent != null);
            FarmingUtil.tickFarming(commandBuffer, blockChunkComponent, blockSectionComponent, section, ref, farmingBlockComponent, x, y, z, true);
        }

        @Override
        public void onEntityRemove(@Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
        }

        @Override
        @Nonnull
        public Query<ChunkStore> getQuery() {
            return this.query;
        }
    }

    public static class OnSoilAdded
    extends RefSystem<ChunkStore> {
        @Nonnull
        private final ComponentType<ChunkStore, BlockModule.BlockStateInfo> blockStateInfoComponentType;
        @Nonnull
        private final ComponentType<ChunkStore, TilledSoilBlock> tilledSoilBlockComponentType;
        @Nonnull
        private final Query<ChunkStore> query;

        public OnSoilAdded(@Nonnull ComponentType<ChunkStore, BlockModule.BlockStateInfo> blockStateInfoComponentType, @Nonnull ComponentType<ChunkStore, TilledSoilBlock> tilledSoilBlockComponentType) {
            this.blockStateInfoComponentType = blockStateInfoComponentType;
            this.tilledSoilBlockComponentType = tilledSoilBlockComponentType;
            this.query = Query.and(blockStateInfoComponentType, tilledSoilBlockComponentType);
        }

        @Override
        public void onEntityAdded(@Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
            TilledSoilBlock soilComponent = commandBuffer.getComponent(ref, this.tilledSoilBlockComponentType);
            assert (soilComponent != null);
            BlockModule.BlockStateInfo blockStateInfoComponent = commandBuffer.getComponent(ref, this.blockStateInfoComponentType);
            assert (blockStateInfoComponent != null);
            Ref<ChunkStore> chunkRef = blockStateInfoComponent.getChunkRef();
            if (!chunkRef.isValid()) {
                return;
            }
            if (!soilComponent.isPlanted()) {
                int index = blockStateInfoComponent.getIndex();
                int x = ChunkUtil.xFromBlockInColumn(index);
                int y = ChunkUtil.yFromBlockInColumn(index);
                int z = ChunkUtil.zFromBlockInColumn(index);
                BlockChunk blockChunkComponent = commandBuffer.getComponent(chunkRef, BlockChunk.getComponentType());
                assert (blockChunkComponent != null);
                BlockSection blockSection = blockChunkComponent.getSectionAtBlockY(y);
                Instant decayTime = soilComponent.getDecayTime();
                if (decayTime == null) {
                    int blockId = blockSection.get(x, y, z);
                    BlockType blockType = BlockType.getAssetMap().getAsset(blockId);
                    FarmingSystems.updateSoilDecayTime(commandBuffer, soilComponent, blockType);
                }
                if (decayTime == null) {
                    return;
                }
                blockSection.scheduleTick(ChunkUtil.indexBlock(x, y, z), decayTime);
            }
        }

        @Override
        public void onEntityRemove(@Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
        }

        @Override
        @Nonnull
        public Query<ChunkStore> getQuery() {
            return this.query;
        }
    }
}

