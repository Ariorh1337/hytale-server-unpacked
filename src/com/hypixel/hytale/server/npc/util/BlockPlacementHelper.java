package com.hypixel.hytale.server.npc.util;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.server.core.asset.type.blockhitbox.BlockBoundingBoxes;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import javax.annotation.Nonnull;

public class BlockPlacementHelper {
   public static boolean canPlaceUnitBlock(@Nonnull World world, BlockType placedBlockType, boolean allowEmptyMaterials, int x, int y, int z) {
      ChunkStore chunkStore = world.getChunkStore();
      long chunkIndex = ChunkUtil.indexChunkFromBlock(x, z);
      Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(chunkIndex);
      if (chunkRef != null && chunkRef.isValid()) {
         Store<ChunkStore> chunkComponentStore = chunkStore.getStore();
         BlockChunk blockChunkComponent = chunkComponentStore.getComponent(chunkRef, BlockChunk.getComponentType());
         if (blockChunkComponent == null) {
            return false;
         }

         int target = blockChunkComponent.getBlock(x, y, z);
         BlockType targetBlockType = BlockType.getAssetMap().getAsset(target);
         if (!testBlock(placedBlockType, targetBlockType, allowEmptyMaterials)) {
            return false;
         }

         target = blockChunkComponent.getBlock(x, y - 1, z);
         targetBlockType = BlockType.getAssetMap().getAsset(target);
         BlockSection section = blockChunkComponent.getSectionAtBlockY(y - 1);
         int filler = section.getFiller(x, y - 1, z);
         int rotation = section.getRotationIndex(x, y - 1, z);
         return testSupportingBlock(targetBlockType, rotation, filler);
      } else {
         return false;
      }
   }

   public static boolean canPlaceBlock(
      @Nonnull World world, @Nonnull BlockType placedBlockType, int rotationIndex, boolean allowEmptyMaterials, int x, int y, int z
   ) {
      return world.testBlockTypes(
         x,
         y,
         z,
         placedBlockType,
         rotationIndex,
         (blockX, blockY, blockZ, blockType, rotation, filler) -> testBlock(placedBlockType, blockType, allowEmptyMaterials)
      );
   }

   public static boolean testBlock(BlockType placedBlockType, @Nonnull BlockType blockType, boolean allowEmptyMaterials) {
      if (blockType == BlockType.EMPTY) {
         return true;
      } else {
         return allowEmptyMaterials && blockType.getMaterial() == BlockMaterial.Empty ? true : true;
      }
   }

   public static boolean testSupportingBlock(@Nonnull BlockType blockType, int rotation, int filler) {
      Box targetHitbox = BlockBoundingBoxes.getAssetMap().getAsset(blockType.getHitboxTypeIndex()).get(rotation).getBoundingBox();
      return blockType != BlockType.EMPTY
         && blockType != BlockType.UNKNOWN
         && blockType.getMaterial() == BlockMaterial.Solid
         && filler == 0
         && targetHitbox.isUnitBox();
   }
}
