package com.hypixel.hytale.server.core.util;

import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.common.util.CompletableFutureUtil;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.function.consumer.TriIntConsumer;
import com.hypixel.hytale.function.predicate.TriIntPredicate;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.asset.type.blockhitbox.BlockBoundingBoxes;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.blocktype.component.BlockPhysics;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockOperations;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public class FillerBlockUtil {
   public static final float THRESHOLD = 0.0F;
   public static final int NO_FILLER = 0;
   private static final int BITS_PER_AXIS = 5;
   private static final int MASK = 31;
   private static final int INVERT = -32;
   private static final int MAX_FILLER_OFFSET = 15;

   public static void forEachFillerBlock(@Nonnull BlockBoundingBoxes.RotatedVariantBoxes blockBoundingBoxes, @Nonnull TriIntConsumer consumer) {
      forEachFillerBlock(0.0F, blockBoundingBoxes, consumer);
   }

   public static void forEachFillerBlock(float threshold, @Nonnull BlockBoundingBoxes.RotatedVariantBoxes blockBoundingBoxes, @Nonnull TriIntConsumer consumer) {
      forEachFillerBlock(threshold, 0, blockBoundingBoxes, consumer);
   }

   public static void forEachFillerBlock(
      float threshold, int expand, @Nonnull BlockBoundingBoxes.RotatedVariantBoxes blockBoundingBoxes, @Nonnull TriIntConsumer consumer
   ) {
      forEachFillerBlock(threshold, expand, expand, expand, blockBoundingBoxes, consumer);
   }

   public static void forEachFillerBlock(
      float threshold,
      int expandX,
      int expandY,
      int expandZ,
      @Nonnull BlockBoundingBoxes.RotatedVariantBoxes blockBoundingBoxes,
      @Nonnull TriIntConsumer consumer
   ) {
      if (!(threshold < 0.0F) && !(threshold >= 1.0F)) {
         Box boundingBox = blockBoundingBoxes.getBoundingBox();
         int minX = (int)boundingBox.min.x;
         int minY = (int)boundingBox.min.y;
         int minZ = (int)boundingBox.min.z;
         if (minX - boundingBox.min.x > threshold) {
            minX--;
         }

         if (minY - boundingBox.min.y > threshold) {
            minY--;
         }

         if (minZ - boundingBox.min.z > threshold) {
            minZ--;
         }

         int maxX = (int)boundingBox.max.x;
         int maxY = (int)boundingBox.max.y;
         int maxZ = (int)boundingBox.max.z;
         minX -= expandX;
         minY -= expandY;
         minZ -= expandZ;
         maxX += expandX;
         maxY += expandY;
         maxZ += expandZ;
         if (boundingBox.max.x - maxX > threshold) {
            maxX++;
         }

         if (boundingBox.max.y - maxY > threshold) {
            maxY++;
         }

         if (boundingBox.max.z - maxZ > threshold) {
            maxZ++;
         }

         int blockWidth = Math.max(maxX - minX, 1);
         int blockHeight = Math.max(maxY - minY, 1);
         int blockDepth = Math.max(maxZ - minZ, 1);

         for (int x = 0; x < blockWidth; x++) {
            for (int y = 0; y < blockHeight; y++) {
               for (int z = 0; z < blockDepth; z++) {
                  consumer.accept(minX + x, minY + y, minZ + z);
               }
            }
         }
      } else {
         throw new IllegalArgumentException("Threshold must be between 0 and 1");
      }
   }

   public static boolean testFillerBlocks(@Nonnull BlockBoundingBoxes.RotatedVariantBoxes blockBoundingBoxes, @Nonnull TriIntPredicate predicate) {
      return testFillerBlocks(0.0F, blockBoundingBoxes, predicate);
   }

   public static boolean testFillerBlocks(
      float threshold, @Nonnull BlockBoundingBoxes.RotatedVariantBoxes blockBoundingBoxes, @Nonnull TriIntPredicate predicate
   ) {
      if (!(threshold < 0.0F) && !(threshold >= 1.0F)) {
         Box boundingBox = blockBoundingBoxes.getBoundingBox();
         int minX = (int)boundingBox.min.x;
         int minY = (int)boundingBox.min.y;
         int minZ = (int)boundingBox.min.z;
         if (minX - boundingBox.min.x > threshold) {
            minX--;
         }

         if (minY - boundingBox.min.y > threshold) {
            minY--;
         }

         if (minZ - boundingBox.min.z > threshold) {
            minZ--;
         }

         int maxX = (int)boundingBox.max.x;
         int maxY = (int)boundingBox.max.y;
         int maxZ = (int)boundingBox.max.z;
         if (boundingBox.max.x - maxX > threshold) {
            maxX++;
         }

         if (boundingBox.max.y - maxY > threshold) {
            maxY++;
         }

         if (boundingBox.max.z - maxZ > threshold) {
            maxZ++;
         }

         int blockWidth = Math.max(maxX - minX, 1);
         int blockHeight = Math.max(maxY - minY, 1);
         int blockDepth = Math.max(maxZ - minZ, 1);

         for (int x = 0; x < blockWidth; x++) {
            for (int y = 0; y < blockHeight; y++) {
               for (int z = 0; z < blockDepth; z++) {
                  if (!predicate.test(minX + x, minY + y, minZ + z)) {
                     return false;
                  }
               }
            }
         }

         return true;
      } else {
         throw new IllegalArgumentException("Threshold must be between 0 and 1");
      }
   }

   public static <A, B> FillerBlockUtil.ValidationResult validateBlock(
      int x, int y, int z, int blockId, int rotation, int filler, A a, B b, @Nonnull FillerBlockUtil.FillerFetcher<A, B> fetcher
   ) {
      if (blockId == 0) {
         return FillerBlockUtil.ValidationResult.OK;
      }

      BlockTypeAssetMap<String, BlockType> blockTypeAssetMap = BlockType.getAssetMap();
      BlockType blockType = blockTypeAssetMap.getAsset(blockId);
      if (blockType == null) {
         return FillerBlockUtil.ValidationResult.OK;
      }

      String id = blockType.getId();
      IndexedLookupTableAssetMap<String, BlockBoundingBoxes> hitboxAssetMap = BlockBoundingBoxes.getAssetMap();
      if (filler != 0) {
         int fillerX = unpackX(filler);
         int fillerY = unpackY(filler);
         int fillerZ = unpackZ(filler);
         int baseBlockId = fetcher.getBlock(a, b, x - fillerX, y - fillerY, z - fillerZ);
         BlockType baseBlock = blockTypeAssetMap.getAsset(baseBlockId);
         if (baseBlock == null) {
            return FillerBlockUtil.ValidationResult.INVALID_BLOCK;
         }

         String baseId = baseBlock.getId();
         BlockBoundingBoxes hitbox = hitboxAssetMap.getAsset(baseBlock.getHitboxTypeIndex());
         if (hitbox == null) {
            return FillerBlockUtil.ValidationResult.OK;
         }

         int baseFiller = fetcher.getFiller(a, b, x - fillerX, y - fillerY, z - fillerZ);
         int baseRotation = fetcher.getRotationIndex(a, b, x - fillerX, y - fillerY, z - fillerZ);
         return baseFiller == 0
               && baseRotation == rotation
               && id.equals(baseId)
               && hitbox.get(baseRotation).getBoundingBox().containsBlock(fillerX, fillerY, fillerZ)
            ? FillerBlockUtil.ValidationResult.OK
            : FillerBlockUtil.ValidationResult.INVALID_BLOCK;
      } else {
         BlockBoundingBoxes hitbox = hitboxAssetMap.getAsset(blockType.getHitboxTypeIndex());
         if (hitbox != null && hitbox.protrudesUnitBox()) {
            boolean result = testFillerBlocks(hitbox.get(rotation), (x1, y1, z1) -> {
               if (x1 == 0 && y1 == 0 && z1 == 0) {
                  return true;
               }

               int worldX = x + x1;
               int worldY = y + y1;
               int worldZ = z + z1;
               int fillerBlockId = fetcher.getBlock(a, b, worldX, worldY, worldZ);
               BlockType fillerBlock = blockTypeAssetMap.getAsset(fillerBlockId);
               int expectedFiller = pack(x1, y1, z1);
               if (fetcher.getFiller(a, b, worldX, worldY, worldZ) != expectedFiller) {
                  return false;
               }

               if (fetcher.getRotationIndex(a, b, worldX, worldY, worldZ) != rotation) {
                  return false;
               }

               if (fillerBlock == null) {
                  return false;
               }

               String blockTypeKey = fillerBlock.getId();
               return blockTypeKey.equals(id);
            });
            return result ? FillerBlockUtil.ValidationResult.OK : FillerBlockUtil.ValidationResult.INVALID_FILLER;
         } else {
            return FillerBlockUtil.ValidationResult.OK;
         }
      }
   }

   public static int pack(int x, int y, int z) {
      return x & 31 | (z & 31) << 5 | (y & 31) << 10;
   }

   public static int unpackX(int val) {
      int result = val & 31;
      if ((result & 16) != 0) {
         result |= -32;
      }

      return result;
   }

   public static int unpackY(int val) {
      int result = val >> 10 & 31;
      if ((result & 16) != 0) {
         result |= -32;
      }

      return result;
   }

   public static int unpackZ(int val) {
      int result = val >> 5 & 31;
      if ((result & 16) != 0) {
         result |= -32;
      }

      return result;
   }

   private static void removeBlockEntity(ComponentAccessor<ChunkStore> accessor, BlockComponentChunk blockComponentChunk, int x, int y, int z) {
      int indexInColumn = ChunkUtil.indexBlockInColumn(x, y, z);
      if (!(accessor instanceof Store<ChunkStore> store && (!accessor.getExternalData().getWorld().isInThread() || store.isProcessing()))) {
         Ref<ChunkStore> reference = blockComponentChunk.getEntityReference(indexInColumn);
         if (reference != null) {
            accessor.removeEntity(reference, ChunkStore.REGISTRY.newHolder(), RemoveReason.REMOVE);
         } else {
            blockComponentChunk.removeEntityHolder(indexInColumn);
         }
      } else {
         CompletableFutureUtil._catch(CompletableFuture.runAsync(() -> {
            Ref<ChunkStore> referencex = blockComponentChunk.getEntityReference(indexInColumn);
            if (referencex != null) {
               accessor.removeEntity(referencex, ChunkStore.REGISTRY.newHolder(), RemoveReason.REMOVE);
            } else {
               blockComponentChunk.removeEntityHolder(indexInColumn);
            }
         }, accessor.getExternalData().getWorld()));
      }
   }

   private static void withBlockSection(
      @Nonnull ComponentAccessor<ChunkStore> accessor,
      @Nonnull BlockSection localSection,
      int localX,
      int localY,
      int localZ,
      int targetX,
      int targetY,
      int targetZ,
      @Nonnull Consumer<BlockSection> action
   ) {
      if (ChunkUtil.isSameChunkSection(localX, localY, localZ, targetX, targetY, targetZ)) {
         action.accept(localSection);
      } else {
         ChunkStore chunkStore = accessor.getExternalData();
         Ref<ChunkStore> section = chunkStore.getWorld().isInThread() ? chunkStore.getChunkSectionReferenceAtBlock(targetX, targetY, targetZ) : null;
         if (section != null) {
            BlockSection otherSection = accessor.getComponent(section, BlockSection.getComponentType());
            if (otherSection != null) {
               action.accept(otherSection);
            }
         } else {
            CompletableFutureUtil._catch(chunkStore.getChunkSectionReferenceAtBlockAsync(targetX, targetY, targetZ).thenAcceptAsync(asyncRef -> {
               BlockSection otherSectionx = asyncRef.getStore().getComponent((Ref<ChunkStore>)asyncRef, BlockSection.getComponentType());
               if (otherSectionx != null) {
                  action.accept(otherSectionx);
               }
            }, chunkStore.getWorld()));
         }
      }
   }

   private static void removeFiller(
      ComponentAccessor<ChunkStore> accessor, BlockSection blockSection, int x, int y, int z, FillerBlockUtil.ChangeReason changeReason
   ) {
      Ref<ChunkStore> column = accessor.getExternalData().getChunkReference(ChunkUtil.indexChunkFromBlock(x, z));
      if (column != null) {
         BlockChunk blockChunk = accessor.getComponent(column, BlockChunk.getComponentType());
         if (blockChunk != null) {
            BlockComponentChunk blockComponentChunk = accessor.getComponent(column, BlockComponentChunk.getComponentType());
            if (blockComponentChunk != null) {
               short oldHeight = blockChunk.getHeight(x, z);
               int oldBlock = blockSection.get(x, y, z);
               boolean changed = blockSection.set(x, y, z, 0, 0, 0);
               if (changed) {
                  short newHeight = BlockOperations.updateBlockHeight(blockChunk, 0, BlockType.EMPTY, x, y, z, oldHeight);
                  if (changeReason != FillerBlockUtil.ChangeReason.NONE) {
                     BlockOperations.spawnBlockParticles(
                        accessor.getExternalData(), oldBlock, 0, x, y, z, changeReason == FillerBlockUtil.ChangeReason.BY_PHYSICS
                     );
                  }

                  removeBlockEntity(accessor, blockComponentChunk, x, y, z);
                  accessor.getExternalData()
                     .getWorld()
                     .getChunkLighting()
                     .invalidateLightAtBlock(accessor.getExternalData(), x, y, z, BlockType.EMPTY, oldHeight, newHeight);
               }
            }
         }
      }
   }

   public static void removeFillerBlocksAt(
      @Nonnull ComponentAccessor<ChunkStore> accessor,
      BlockSection blockSection,
      int x,
      int y,
      int z,
      int blockId,
      int filler,
      int rotation,
      FillerBlockUtil.ChangeReason changeReason
   ) {
      removeFillerBlocksAt(accessor, blockSection, x, y, z, blockId, filler, rotation, changeReason, true);
   }

   private static void removeFillerBlocksAt(
      @Nonnull ComponentAccessor<ChunkStore> accessor,
      BlockSection blockSection,
      int x,
      int y,
      int z,
      int blockId,
      int filler,
      int rotation,
      FillerBlockUtil.ChangeReason changeReason,
      boolean scanForOrphans
   ) {
      BlockType oldBlockType = BlockType.getAssetMap().getAsset(blockId);
      if (oldBlockType != null) {
         int fx = unpackX(filler);
         int fy = unpackY(filler);
         int fz = unpackZ(filler);
         int baseX = x - fx;
         int baseY = y - fy;
         int baseZ = z - fz;
         BlockBoundingBoxes hitbox = BlockBoundingBoxes.getAssetMap().getAsset(oldBlockType.getHitboxTypeIndex());
         if (hitbox == null) {
            hitbox = BlockBoundingBoxes.UNIT_BOX;
         }

         forEachFillerBlock(hitbox.get(rotation), (x1, y1, z1) -> {
            if (x1 != fx || y1 != fy || z1 != fz) {
               int blockX = baseX + x1;
               int blockY = baseY + y1;
               int blockZ = baseZ + z1;
               withBlockSection(accessor, blockSection, x, y, z, blockX, blockY, blockZ, section -> {
                  if (section.get(blockX, blockY, blockZ) == blockId) {
                     removeFiller(accessor, section, blockX, blockY, blockZ, changeReason);
                  }
               });
            }
         });
         if (scanForOrphans && oldBlockType.getHitboxTypeIndex() != 0) {
            removeOrphanedFillers(accessor, blockSection, x, y, z, baseX, baseY, baseZ, blockId, changeReason);
         }
      }
   }

   private static void removeOrphanedFillers(
      @Nonnull ComponentAccessor<ChunkStore> accessor,
      BlockSection blockSection,
      int x,
      int y,
      int z,
      int baseX,
      int baseY,
      int baseZ,
      int blockId,
      FillerBlockUtil.ChangeReason changeReason
   ) {
      int scanMinX = baseX - 15;
      int scanMaxX = baseX + 15;
      int scanMinY = baseY - 15;
      int scanMaxY = baseY + 15;
      int scanMinZ = baseZ - 15;
      int scanMaxZ = baseZ + 15;
      int localSX = ChunkUtil.chunkCoordinate(x);
      int localSY = ChunkUtil.chunkCoordinate(y);
      int localSZ = ChunkUtil.chunkCoordinate(z);

      for (int sx = ChunkUtil.chunkCoordinate(scanMinX); sx <= ChunkUtil.chunkCoordinate(scanMaxX); sx++) {
         int bxMin = Math.max(scanMinX, ChunkUtil.minBlock(sx));
         int bxMax = Math.min(scanMaxX, ChunkUtil.maxBlock(sx));

         for (int sy = ChunkUtil.chunkCoordinate(scanMinY); sy <= ChunkUtil.chunkCoordinate(scanMaxY); sy++) {
            int byMin = Math.max(scanMinY, ChunkUtil.minBlock(sy));
            int byMax = Math.min(scanMaxY, ChunkUtil.maxBlock(sy));

            for (int sz = ChunkUtil.chunkCoordinate(scanMinZ); sz <= ChunkUtil.chunkCoordinate(scanMaxZ); sz++) {
               int bzMin = Math.max(scanMinZ, ChunkUtil.minBlock(sz));
               int bzMax = Math.min(scanMaxZ, ChunkUtil.maxBlock(sz));
               if (sx != localSX || sy != localSY || sz != localSZ) {
                  ChunkStore chunkStore = accessor.getExternalData();
                  Ref<ChunkStore> sectionRef = chunkStore.getWorld().isInThread() ? chunkStore.getChunkSectionReferenceAtBlock(bxMin, byMin, bzMin) : null;
                  if (sectionRef != null) {
                     BlockSection otherSection = accessor.getComponent(sectionRef, BlockSection.getComponentType());
                     if (otherSection != null && otherSection.contains(blockId)) {
                        scanSectionOrphans(accessor, otherSection, bxMin, bxMax, byMin, byMax, bzMin, bzMax, baseX, baseY, baseZ, blockId, changeReason);
                     }
                  } else {
                     CompletableFutureUtil._catch(chunkStore.getChunkSectionReferenceAtBlockAsync(bxMin, byMin, bzMin).thenAcceptAsync(asyncRef -> {
                        BlockSection otherSectionx = asyncRef.getStore().getComponent((Ref<ChunkStore>)asyncRef, BlockSection.getComponentType());
                        if (otherSectionx != null && otherSectionx.contains(blockId)) {
                           scanSectionOrphans(accessor, otherSectionx, bxMin, bxMax, byMin, byMax, bzMin, bzMax, baseX, baseY, baseZ, blockId, changeReason);
                        }
                     }, chunkStore.getWorld()));
                  }
               } else if (blockSection.contains(blockId)) {
                  scanSectionOrphans(accessor, blockSection, bxMin, bxMax, byMin, byMax, bzMin, bzMax, baseX, baseY, baseZ, blockId, changeReason);
               }
            }
         }
      }
   }

   private static void scanSectionOrphans(
      ComponentAccessor<ChunkStore> accessor,
      BlockSection section,
      int minX,
      int maxX,
      int minY,
      int maxY,
      int minZ,
      int maxZ,
      int baseX,
      int baseY,
      int baseZ,
      int blockId,
      FillerBlockUtil.ChangeReason changeReason
   ) {
      for (int bx = minX; bx <= maxX; bx++) {
         for (int by = minY; by <= maxY; by++) {
            for (int bz = minZ; bz <= maxZ; bz++) {
               if ((bx != baseX || by != baseY || bz != baseZ) && isOrphanedFiller(section, bx, by, bz, baseX, baseY, baseZ, blockId)) {
                  removeFiller(accessor, section, bx, by, bz, changeReason);
               }
            }
         }
      }
   }

   private static boolean isOrphanedFiller(BlockSection section, int x, int y, int z, int baseX, int baseY, int baseZ, int blockId) {
      if (section.get(x, y, z) != blockId) {
         return false;
      }

      int filler = section.getFiller(x, y, z);
      return filler == 0 ? false : x - unpackX(filler) == baseX && y - unpackY(filler) == baseY && z - unpackZ(filler) == baseZ;
   }

   private static void setFiller(
      @Nonnull ComponentAccessor<ChunkStore> accessor,
      @Nonnull Ref<ChunkStore> ref,
      @Nonnull BlockSection blockSection,
      int x,
      int y,
      int z,
      int blockId,
      BlockType blockType,
      int filler,
      int rotation,
      FillerBlockUtil.ChangeReason changeReason
   ) {
      int oldBlock = blockSection.get(x, y, z);
      int oldFiller = blockSection.getFiller(x, y, z);
      int oldRotation = blockSection.getRotationIndex(x, y, z);
      if (blockSection.set(x, y, z, blockId, rotation, filler)) {
         if (oldBlock != 0) {
            removeFillerBlocksAt(accessor, blockSection, x, y, z, oldBlock, oldFiller, oldRotation, changeReason, false);
         }

         Ref<ChunkStore> column = accessor.getExternalData().getChunkReference(ChunkUtil.indexChunkFromBlock(x, z));
         if (column != null) {
            BlockChunk blockChunk = accessor.getComponent(column, BlockChunk.getComponentType());
            if (blockChunk != null) {
               BlockComponentChunk blockComponentChunk = accessor.getComponent(column, BlockComponentChunk.getComponentType());
               if (blockComponentChunk != null) {
                  short oldHeight = blockChunk.getHeight(x, z);
                  short newHeight = BlockOperations.updateBlockHeight(blockChunk, blockId, blockType, x, y, z, oldHeight);
                  if (changeReason != FillerBlockUtil.ChangeReason.NONE) {
                     BlockOperations.spawnBlockParticles(
                        accessor.getExternalData(), oldBlock, blockId, x, y, z, changeReason == FillerBlockUtil.ChangeReason.BY_PHYSICS
                     );
                  }

                  accessor.getExternalData()
                     .getWorld()
                     .getChunkLighting()
                     .invalidateLightAtBlock(accessor.getExternalData(), x, y, z, blockType, oldHeight, newHeight);
                  removeBlockEntity(accessor, blockComponentChunk, x, y, z);
                  World world = accessor.getExternalData().getWorld();
                  if (world.isInThread()) {
                     if (!blockType.hasSupport()) {
                        BlockPhysics.clear(accessor, ref, x, y, z);
                     } else {
                        BlockPhysics.reset(accessor, ref, x, y, z);
                     }
                  } else {
                     world.execute(() -> {
                        if (!blockType.hasSupport()) {
                           BlockPhysics.clear(accessor, ref, x, y, z);
                        } else {
                           BlockPhysics.reset(accessor, ref, x, y, z);
                        }
                     });
                  }
               }
            }
         }
      }
   }

   public static void setFillerBlocksAt(
      @Nonnull ComponentAccessor<ChunkStore> accessor,
      @Nonnull Ref<ChunkStore> ref,
      BlockSection blockSection,
      int x,
      int y,
      int z,
      int blockId,
      int filler,
      int rotation,
      FillerBlockUtil.ChangeReason changeReason
   ) {
      BlockType blockType = BlockType.getAssetMap().getAsset(blockId);
      if (blockType != null) {
         int fx = unpackX(filler);
         int fy = unpackY(filler);
         int fz = unpackZ(filler);
         int baseX = x - fx;
         int baseY = y - fy;
         int baseZ = z - fz;
         BlockBoundingBoxes hitbox = BlockBoundingBoxes.getAssetMap().getAsset(blockType.getHitboxTypeIndex());
         if (hitbox == null) {
            hitbox = BlockBoundingBoxes.UNIT_BOX;
         }

         forEachFillerBlock(
            hitbox.get(rotation),
            (x1, y1, z1) -> {
               if (x1 != fx || y1 != fy || z1 != fz) {
                  int blockX = baseX + x1;
                  int blockY = baseY + y1;
                  int blockZ = baseZ + z1;
                  if (ChunkUtil.isSameChunkSection(x, y, z, blockX, blockY, blockZ)) {
                     setFiller(accessor, ref, blockSection, blockX, blockY, blockZ, blockId, blockType, pack(x1, y1, z1), rotation, changeReason);
                  } else {
                     ChunkStore chunkStore = accessor.getExternalData();
                     Ref<ChunkStore> section = chunkStore.getWorld().isInThread() ? chunkStore.getChunkSectionReferenceAtBlock(blockX, blockY, blockZ) : null;
                     if (section != null) {
                        BlockSection otherBlockSection = accessor.getComponent(section, BlockSection.getComponentType());
                        if (otherBlockSection == null) {
                           return;
                        }

                        setFiller(accessor, section, otherBlockSection, blockX, blockY, blockZ, blockId, blockType, pack(x1, y1, z1), rotation, changeReason);
                     } else {
                        chunkStore.getChunkSectionReferenceAtBlockAsync(blockX, blockY, blockZ)
                           .thenAcceptAsync(
                              section1 -> {
                                 BlockSection otherBlockSectionx = section1.getStore().getComponent((Ref<ChunkStore>)section1, BlockSection.getComponentType());
                                 if (otherBlockSectionx != null) {
                                    setFiller(
                                       accessor,
                                       (Ref<ChunkStore>)section1,
                                       otherBlockSectionx,
                                       blockX,
                                       blockY,
                                       blockZ,
                                       blockId,
                                       blockType,
                                       pack(x1, y1, z1),
                                       rotation,
                                       changeReason
                                    );
                                 }
                              },
                              accessor.getExternalData().getWorld()
                           );
                     }
                  }
               }
            }
         );
      }
   }

   public enum ChangeReason {
      NONE,
      NORMAL,
      BY_PHYSICS;
   }

   public interface FillerFetcher<A, B> {
      int getBlock(A var1, B var2, int var3, int var4, int var5);

      int getFiller(A var1, B var2, int var3, int var4, int var5);

      int getRotationIndex(A var1, B var2, int var3, int var4, int var5);
   }

   public enum ValidationResult {
      OK,
      INVALID_BLOCK,
      INVALID_FILLER;
   }
}
