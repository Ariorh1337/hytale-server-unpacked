package com.hypixel.hytale.builtin.adventure.farming;

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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
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

   private static boolean updateSoilDecayTime(
      @Nonnull CommandBuffer<ChunkStore> commandBuffer, @Nonnull TilledSoilBlock soilBlock, @Nullable BlockType blockType
   ) {
      if (blockType == null) {
         return false;
      }

      FarmingData farming = blockType.getFarming();
      if (farming != null && farming.getSoilConfig() != null) {
         FarmingData.SoilConfig soilConfig = farming.getSoilConfig();
         Rangef range = soilConfig.getLifetime();
         if (range == null) {
            return false;
         }

         double baseDuration = range.min + (range.max - range.min) * ThreadLocalRandom.current().nextDouble();
         Instant currentTime = commandBuffer.getExternalData()
            .getWorld()
            .getEntityStore()
            .getStore()
            .getResource(WorldTimeResource.getResourceType())
            .getGameTime();
         Instant endTime = currentTime.plus(Math.round(baseDuration), ChronoUnit.SECONDS);
         soilBlock.setDecayTime(endTime);
         return true;
      } else {
         return false;
      }
   }

   public static class CoopResidentEntitySystem extends RefSystem<EntityStore> {
      private static final ComponentType<EntityStore, CoopResidentComponent> COMPONENT_TYPE_COOP_RESIDENT = CoopResidentComponent.getComponentType();

      @Override
      public Query<EntityStore> getQuery() {
         return COMPONENT_TYPE_COOP_RESIDENT;
      }

      @Override
      public void onEntityAdded(
         @Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
      }

      @Override
      public void onEntityRemove(
         @Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         if (reason != RemoveReason.UNLOAD) {
            UUIDComponent uuidComponent = commandBuffer.getComponent(ref, UUIDComponent.getComponentType());
            if (uuidComponent != null) {
               UUID uuid = uuidComponent.getUuid();
               CoopResidentComponent coopResidentComponent = commandBuffer.getComponent(ref, COMPONENT_TYPE_COOP_RESIDENT);
               if (coopResidentComponent != null) {
                  Vector3i coopPosition = coopResidentComponent.getCoopLocation();
                  World world = commandBuffer.getExternalData().getWorld();
                  long chunkIndex = ChunkUtil.indexChunkFromBlock(coopPosition.x, coopPosition.z);
                  WorldChunk worldChunkComponent = world.getChunkIfLoaded(chunkIndex);
                  if (worldChunkComponent != null) {
                     Ref<ChunkStore> chunkRef = world.getChunkStore().getChunkReference(chunkIndex);
                     if (chunkRef != null && chunkRef.isValid()) {
                        Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
                        ChunkColumn chunkColumnComponent = chunkStore.getComponent(chunkRef, ChunkColumn.getComponentType());
                        if (chunkColumnComponent != null) {
                           BlockChunk blockChunkComponent = chunkStore.getComponent(chunkRef, BlockChunk.getComponentType());
                           if (blockChunkComponent != null) {
                              Ref<ChunkStore> sectionRef = chunkColumnComponent.getSection(ChunkUtil.chunkCoordinate(coopPosition.y));
                              if (sectionRef != null && sectionRef.isValid()) {
                                 BlockComponentChunk blockComponentChunk = chunkStore.getComponent(chunkRef, BlockComponentChunk.getComponentType());
                                 if (blockComponentChunk != null) {
                                    int blockIndexColumn = ChunkUtil.indexBlockInColumn(coopPosition.x, coopPosition.y, coopPosition.z);
                                    Ref<ChunkStore> coopEntityReference = blockComponentChunk.getEntityReference(blockIndexColumn);
                                    if (coopEntityReference != null) {
                                       CoopBlock coop = chunkStore.getComponent(coopEntityReference, CoopBlock.getComponentType());
                                       if (coop != null) {
                                          coop.handleResidentDespawn(uuid);
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static class CoopResidentTicking extends EntityTickingSystem<EntityStore> {
      private static final ComponentType<EntityStore, CoopResidentComponent> COMPONENT_TYPE_COOP_RESIDENT = CoopResidentComponent.getComponentType();

      @Override
      public Query<EntityStore> getQuery() {
         return COMPONENT_TYPE_COOP_RESIDENT;
      }

      @Override
      public void tick(
         float dt,
         int index,
         @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         CoopResidentComponent coopResidentComponent = archetypeChunk.getComponent(index, CoopResidentComponent.getComponentType());
         if (coopResidentComponent != null) {
            if (coopResidentComponent.getMarkedForDespawn()) {
               commandBuffer.removeEntity(archetypeChunk.getReferenceTo(index), RemoveReason.REMOVE);
            }
         }
      }
   }

   @Deprecated(forRemoval = true)
   public static class MigrateFarming extends BlockModule.MigrationSystem {
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

      @Nullable
      @Override
      public Query<ChunkStore> getQuery() {
         return FarmingPlugin.get().getFarmingBlockStateComponentType();
      }
   }

   public static class OnCoopAdded extends RefSystem<ChunkStore> {
      @Nonnull
      private static final Query<ChunkStore> QUERY = Query.and(BlockModule.BlockStateInfo.getComponentType(), CoopBlock.getComponentType());

      @Override
      public void onEntityAdded(
         @Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer
      ) {
         CoopBlock coopBlockComponent = commandBuffer.getComponent(ref, CoopBlock.getComponentType());
         assert coopBlockComponent != null;
         BlockModule.BlockStateInfo blockStateInfoComponent = commandBuffer.getComponent(ref, BlockModule.BlockStateInfo.getComponentType());
         assert blockStateInfoComponent != null;
         World world = commandBuffer.getExternalData().getWorld();
         Store<EntityStore> entityStore = world.getEntityStore().getStore();
         WorldTimeResource worldTimeResource = entityStore.getResource(WorldTimeResource.getResourceType());
         int x = ChunkUtil.xFromBlockInColumn(blockStateInfoComponent.getIndex());
         int y = ChunkUtil.yFromBlockInColumn(blockStateInfoComponent.getIndex());
         int z = ChunkUtil.zFromBlockInColumn(blockStateInfoComponent.getIndex());
         Ref<ChunkStore> chunkRef = blockStateInfoComponent.getChunkRef();
         if (chunkRef.isValid()) {
            BlockChunk blockChunkComponent = commandBuffer.getComponent(chunkRef, BlockChunk.getComponentType());
            if (blockChunkComponent != null) {
               BlockSection blockSection = blockChunkComponent.getSectionAtBlockY(y);
               blockSection.scheduleTick(ChunkUtil.indexBlock(x, y, z), coopBlockComponent.getNextScheduledTick(worldTimeResource));
            }
         }
      }

      @Override
      public void onEntityRemove(
         @Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer
      ) {
         if (reason != RemoveReason.UNLOAD) {
            CoopBlock coopBlockComponent = commandBuffer.getComponent(ref, CoopBlock.getComponentType());
            assert coopBlockComponent != null;
            BlockModule.BlockStateInfo blockStateInfoComponent = commandBuffer.getComponent(ref, BlockModule.BlockStateInfo.getComponentType());
            assert blockStateInfoComponent != null;
            int index = blockStateInfoComponent.getIndex();
            int x = ChunkUtil.xFromBlockInColumn(index);
            int y = ChunkUtil.yFromBlockInColumn(index);
            int z = ChunkUtil.zFromBlockInColumn(index);
            BlockChunk blockChunkComponent = commandBuffer.getComponent(blockStateInfoComponent.getChunkRef(), BlockChunk.getComponentType());
            assert blockChunkComponent != null;
            ChunkColumn chunkColumnComponent = commandBuffer.getComponent(blockStateInfoComponent.getChunkRef(), ChunkColumn.getComponentType());
            assert chunkColumnComponent != null;
            Ref<ChunkStore> sectionRef = chunkColumnComponent.getSection(ChunkUtil.chunkCoordinate(y));
            assert sectionRef != null;
            BlockSection blockSectionComponent = commandBuffer.getComponent(sectionRef, BlockSection.getComponentType());
            assert blockSectionComponent != null;
            ChunkSection chunkSectionComponent = commandBuffer.getComponent(sectionRef, ChunkSection.getComponentType());
            assert chunkSectionComponent != null;
            int worldX = ChunkUtil.worldCoordFromLocalCoord(chunkSectionComponent.getX(), x);
            int worldY = ChunkUtil.worldCoordFromLocalCoord(chunkSectionComponent.getY(), y);
            int worldZ = ChunkUtil.worldCoordFromLocalCoord(chunkSectionComponent.getZ(), z);
            World world = commandBuffer.getExternalData().getWorld();
            Store<EntityStore> entityStore = world.getEntityStore().getStore();
            WorldTimeResource worldTimeResource = entityStore.getResource(WorldTimeResource.getResourceType());
            coopBlockComponent.handleBlockBroken(world, worldTimeResource, entityStore, worldX, worldY, worldZ);
         }
      }

      @Nullable
      @Override
      public Query<ChunkStore> getQuery() {
         return QUERY;
      }
   }

   public static class OnFarmBlockAdded extends RefSystem<ChunkStore> {
      @Nonnull
      private static final Query<ChunkStore> QUERY = Query.and(BlockModule.BlockStateInfo.getComponentType(), FarmingBlock.getComponentType());

      @Override
      public void onEntityAdded(
         @Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer
      ) {
         FarmingBlock farmingBlockComponent = commandBuffer.getComponent(ref, FarmingBlock.getComponentType());
         assert farmingBlockComponent != null;
         BlockModule.BlockStateInfo blockStateInfoComponent = commandBuffer.getComponent(ref, BlockModule.BlockStateInfo.getComponentType());
         assert blockStateInfoComponent != null;
         Ref<ChunkStore> chunkRef = blockStateInfoComponent.getChunkRef();
         if (chunkRef.isValid()) {
            BlockChunk blockChunkComponent = commandBuffer.getComponent(chunkRef, BlockChunk.getComponentType());
            assert blockChunkComponent != null;
            World world = store.getExternalData().getWorld();
            Store<EntityStore> entityStore = world.getEntityStore().getStore();
            WorldTimeResource worldTimeResource = entityStore.getResource(WorldTimeResource.getResourceType());
            if (farmingBlockComponent.getLastTickGameTime() == null) {
               int index = blockStateInfoComponent.getIndex();
               int blockId = blockChunkComponent.getBlock(
                  ChunkUtil.xFromBlockInColumn(index), ChunkUtil.yFromBlockInColumn(index), ChunkUtil.zFromBlockInColumn(index)
               );
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
               if (farmingStages != null) {
                  FarmingStageData[] stages = farmingStages.get(startingStageSet);
                  if (stages != null && stages.length > 0) {
                     boolean found = false;

                     for (int i = 0; i < stages.length; i++) {
                        FarmingStageData stage = stages[i];
                        switch (stage) {
                           case BlockTypeFarmingStageData data:
                              if (data.getBlock().equals(blockType.getId())) {
                                 farmingBlockComponent.setGrowthProgress(i);
                                 found = true;
                              }
                              break;
                           case BlockStateFarmingStageData data:
                              BlockType stateBlockType = blockType.getBlockForState(data.getState());
                              if (stateBlockType != null && stateBlockType.getId().equals(blockType.getId())) {
                                 farmingBlockComponent.setGrowthProgress(i);
                                 found = true;
                              }
                              break;
                           default:
                        }
                     }

                     if (!found) {
                        ChunkColumn chunkColumnComponent = commandBuffer.getComponent(chunkRef, ChunkColumn.getComponentType());
                        assert chunkColumnComponent != null;
                        int chunkCoordinate = ChunkUtil.chunkCoordinate(ChunkUtil.yFromBlockInColumn(index));
                        Ref<ChunkStore> sectionRef = chunkColumnComponent.getSection(chunkCoordinate);
                        if (sectionRef != null && sectionRef.isValid()) {
                           stages[0]
                              .apply(
                                 commandBuffer,
                                 sectionRef,
                                 ref,
                                 ChunkUtil.xFromBlockInColumn(index),
                                 ChunkUtil.yFromBlockInColumn(index),
                                 ChunkUtil.zFromBlockInColumn(index),
                                 null
                              );
                        }
                     }
                  }
               }
            }

            if (farmingBlockComponent.getLastTickGameTime() == null) {
               farmingBlockComponent.setLastTickGameTime(worldTimeResource.getGameTime());
               blockChunkComponent.markNeedsSaving();
            }

            int index = blockStateInfoComponent.getIndex();
            int x = ChunkUtil.xFromBlockInColumn(index);
            int y = ChunkUtil.yFromBlockInColumn(index);
            int z = ChunkUtil.zFromBlockInColumn(index);
            BlockComponentChunk blockComponentChunk = commandBuffer.getComponent(chunkRef, BlockComponentChunk.getComponentType());
            assert blockComponentChunk != null;
            ChunkColumn chunkColumnComponent = commandBuffer.getComponent(chunkRef, ChunkColumn.getComponentType());
            assert chunkColumnComponent != null;
            Ref<ChunkStore> section = chunkColumnComponent.getSection(ChunkUtil.chunkCoordinate(y));
            if (section != null) {
               BlockSection blockSectionComponent = commandBuffer.getComponent(section, BlockSection.getComponentType());
               assert blockSectionComponent != null;
               FarmingUtil.tickFarming(commandBuffer, blockChunkComponent, blockSectionComponent, section, ref, farmingBlockComponent, x, y, z, true);
            }
         }
      }

      @Override
      public void onEntityRemove(
         @Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer
      ) {
      }

      @Nullable
      @Override
      public Query<ChunkStore> getQuery() {
         return QUERY;
      }
   }

   public static class OnSoilAdded extends RefSystem<ChunkStore> {
      @Nonnull
      private static final Query<ChunkStore> QUERY = Query.and(BlockModule.BlockStateInfo.getComponentType(), TilledSoilBlock.getComponentType());

      @Override
      public void onEntityAdded(
         @Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer
      ) {
         TilledSoilBlock soilComponent = commandBuffer.getComponent(ref, TilledSoilBlock.getComponentType());
         assert soilComponent != null;
         BlockModule.BlockStateInfo blockStateInfoComponent = commandBuffer.getComponent(ref, BlockModule.BlockStateInfo.getComponentType());
         assert blockStateInfoComponent != null;
         Ref<ChunkStore> chunkRef = blockStateInfoComponent.getChunkRef();
         if (chunkRef.isValid()) {
            if (!soilComponent.isPlanted()) {
               int index = blockStateInfoComponent.getIndex();
               int x = ChunkUtil.xFromBlockInColumn(index);
               int y = ChunkUtil.yFromBlockInColumn(index);
               int z = ChunkUtil.zFromBlockInColumn(index);
               BlockChunk blockChunkComponent = commandBuffer.getComponent(chunkRef, BlockChunk.getComponentType());
               assert blockChunkComponent != null;
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
      }

      @Override
      public void onEntityRemove(
         @Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer
      ) {
      }

      @Nullable
      @Override
      public Query<ChunkStore> getQuery() {
         return QUERY;
      }
   }

   public static class Ticking extends EntityTickingSystem<ChunkStore> {
      @Nonnull
      private static final Query<ChunkStore> QUERY = Query.and(BlockSection.getComponentType(), ChunkSection.getComponentType());

      @Override
      public void tick(
         float dt,
         int index,
         @Nonnull ArchetypeChunk<ChunkStore> archetypeChunk,
         @Nonnull Store<ChunkStore> store,
         @Nonnull CommandBuffer<ChunkStore> commandBuffer
      ) {
         BlockSection blockSectionComponent = archetypeChunk.getComponent(index, BlockSection.getComponentType());
         assert blockSectionComponent != null;
         if (blockSectionComponent.getTickingBlocksCountCopy() != 0) {
            ChunkSection chunkSectionComponent = archetypeChunk.getComponent(index, ChunkSection.getComponentType());
            assert chunkSectionComponent != null;
            Ref<ChunkStore> chunkColumnRef = chunkSectionComponent.getChunkColumnReference();
            if (chunkColumnRef != null && chunkColumnRef.isValid()) {
               BlockComponentChunk blockComponentChunk = commandBuffer.getComponent(chunkColumnRef, BlockComponentChunk.getComponentType());
               assert blockComponentChunk != null;
               Ref<ChunkStore> ref = archetypeChunk.getReferenceTo(index);
               BlockChunk blockChunk = commandBuffer.getComponent(chunkColumnRef, BlockChunk.getComponentType());
               assert blockChunk != null;
               blockSectionComponent.forEachTicking(
                  blockComponentChunk,
                  commandBuffer,
                  chunkSectionComponent.getY(),
                  (blockComponentChunk1, commandBuffer1, localX, localY, localZ, blockId) -> {
                     Ref<ChunkStore> blockRef = blockComponentChunk1.getEntityReference(ChunkUtil.indexBlockInColumn(localX, localY, localZ));
                     if (blockRef == null) {
                        return BlockTickStrategy.IGNORED;
                     } else {
                        FarmingBlock farmingBlockComponent = commandBuffer1.getComponent(blockRef, FarmingBlock.getComponentType());
                        if (farmingBlockComponent != null) {
                           FarmingUtil.tickFarming(
                              commandBuffer1, blockChunk, blockSectionComponent, ref, blockRef, farmingBlockComponent, localX, localY, localZ, false
                           );
                           return BlockTickStrategy.SLEEP;
                        } else {
                           TilledSoilBlock tilledSoilBlockComponent = commandBuffer1.getComponent(blockRef, TilledSoilBlock.getComponentType());
                           if (tilledSoilBlockComponent != null) {
                              tickSoil(commandBuffer1, blockRef, tilledSoilBlockComponent);
                              return BlockTickStrategy.SLEEP;
                           } else {
                              CoopBlock coopBlockComponent = commandBuffer1.getComponent(blockRef, CoopBlock.getComponentType());
                              if (coopBlockComponent != null) {
                                 tickCoop(commandBuffer1, blockRef, coopBlockComponent);
                                 return BlockTickStrategy.SLEEP;
                              } else {
                                 return BlockTickStrategy.IGNORED;
                              }
                           }
                        }
                     }
                  }
               );
            }
         }
      }

      private static void tickSoil(@Nonnull CommandBuffer<ChunkStore> commandBuffer, @Nonnull Ref<ChunkStore> blockRef, @Nonnull TilledSoilBlock soilBlock) {
         BlockModule.BlockStateInfo blockStateInfoComponent = commandBuffer.getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());
         assert blockStateInfoComponent != null;
         int index = blockStateInfoComponent.getIndex();
         int x = ChunkUtil.xFromBlockInColumn(index);
         int y = ChunkUtil.yFromBlockInColumn(index);
         int z = ChunkUtil.zFromBlockInColumn(index);
         if (y < 320) {
            Ref<ChunkStore> chunkRef = blockStateInfoComponent.getChunkRef();
            if (chunkRef.isValid()) {
               BlockChunk blockChunkComponent = commandBuffer.getComponent(chunkRef, BlockChunk.getComponentType());
               if (blockChunkComponent != null) {
                  BlockSection blockSection = blockChunkComponent.getSectionAtBlockY(y);
                  boolean hasCrop = FarmingSystems.hasCropAbove(blockChunkComponent, x, y, z);
                  int blockId = blockSection.get(x, y, z);
                  BlockType blockTypeAsset = BlockType.getAssetMap().getAsset(blockId);
                  if (blockTypeAsset != null) {
                     Instant currentTime = commandBuffer.getExternalData()
                        .getWorld()
                        .getEntityStore()
                        .getStore()
                        .getResource(WorldTimeResource.getResourceType())
                        .getGameTime();
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
                           if (blockTypeAsset.getFarming() != null && blockTypeAsset.getFarming().getSoilConfig() != null) {
                              FarmingData.SoilConfig soilConfig = blockTypeAsset.getFarming().getSoilConfig();
                              String targetBlock = soilConfig.getTargetBlock();
                              if (targetBlock == null) {
                                 return;
                              }

                              int targetBlockId = BlockType.getAssetMap().getIndex(targetBlock);
                              if (targetBlockId == Integer.MIN_VALUE) {
                                 return;
                              }

                              BlockType targetBlockType = BlockType.getAssetMap().getAsset(targetBlockId);
                              int rotationIndex = blockSection.getRotationIndex(x, y, z);
                              WorldChunk worldChunkComponent = commandBuffer.getComponent(chunkRef, WorldChunk.getComponentType());
                              assert worldChunkComponent != null;
                              commandBuffer.run(_store -> worldChunkComponent.setBlock(x, y, z, targetBlockId, targetBlockType, rotationIndex, 0, 0));
                              return;
                           } else {
                              return;
                           }
                        }
                     } else if (hasCrop) {
                        soilBlock.setDecayTime(null);
                     }

                     String targetBlock = soilBlock.computeBlockType(currentTime, blockTypeAsset);
                     if (targetBlock != null && !targetBlock.equals(blockTypeAsset.getId())) {
                        WorldChunk worldChunkComponent = commandBuffer.getComponent(chunkRef, WorldChunk.getComponentType());
                        assert worldChunkComponent != null;
                        int rotationIndex = blockSection.getRotationIndex(x, y, z);
                        int targetBlockId = BlockType.getAssetMap().getIndex(targetBlock);
                        BlockType targetBlockType = BlockType.getAssetMap().getAsset(targetBlockId);
                        commandBuffer.run(_store -> worldChunkComponent.setBlock(x, y, z, targetBlockId, targetBlockType, rotationIndex, 0, 2));
                     }

                     soilBlock.setPlanted(hasCrop);
                  }
               }
            }
         }
      }

      private static void tickCoop(@Nonnull CommandBuffer<ChunkStore> commandBuffer, @Nonnull Ref<ChunkStore> blockRef, @Nonnull CoopBlock coopBlock) {
         BlockModule.BlockStateInfo blockStateInfoComponent = commandBuffer.getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());
         assert blockStateInfoComponent != null;
         Store<EntityStore> store = commandBuffer.getExternalData().getWorld().getEntityStore().getStore();
         WorldTimeResource worldTimeResource = store.getResource(WorldTimeResource.getResourceType());
         FarmingCoopAsset coopAsset = coopBlock.getCoopAsset();
         if (coopAsset != null) {
            int index = blockStateInfoComponent.getIndex();
            int x = ChunkUtil.xFromBlockInColumn(index);
            int y = ChunkUtil.yFromBlockInColumn(index);
            int z = ChunkUtil.zFromBlockInColumn(index);
            BlockChunk blockChunkComponent = commandBuffer.getComponent(blockStateInfoComponent.getChunkRef(), BlockChunk.getComponentType());
            assert blockChunkComponent != null;
            ChunkColumn chunkColumnComponent = commandBuffer.getComponent(blockStateInfoComponent.getChunkRef(), ChunkColumn.getComponentType());
            assert chunkColumnComponent != null;
            Ref<ChunkStore> sectionRef = chunkColumnComponent.getSection(ChunkUtil.chunkCoordinate(y));
            assert sectionRef != null;
            BlockSection blockSectionComponent = commandBuffer.getComponent(sectionRef, BlockSection.getComponentType());
            assert blockSectionComponent != null;
            ChunkSection chunkSectionComponent = commandBuffer.getComponent(sectionRef, ChunkSection.getComponentType());
            assert chunkSectionComponent != null;
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
            if (tryCapture && captureRange >= 0.0F) {
               world.execute(() -> {
                  for (Ref<EntityStore> entity : TargetUtil.getAllEntitiesInSphere(coopLocation.toVector3d(), captureRange, store)) {
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
                  assert currentBlockType != null;
                  chunk.setBlockInteractionState(blockPos, currentBlockType, coopBlock.hasProduce() ? "Produce_Ready" : "default");
               });
            }

            Instant nextTickInstant = coopBlock.getNextScheduledTick(worldTimeResource);
            if (nextTickInstant != null) {
               blockSectionComponent.scheduleTick(ChunkUtil.indexBlock(x, y, z), nextTickInstant);
            }
         }
      }

      @Nullable
      @Override
      public Query<ChunkStore> getQuery() {
         return QUERY;
      }
   }
}
