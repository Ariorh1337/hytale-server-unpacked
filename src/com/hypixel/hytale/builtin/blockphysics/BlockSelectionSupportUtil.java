package com.hypixel.hytale.builtin.blockphysics;

import com.hypixel.hytale.math.block.BlockUtil;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongComparators;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3ic;

public final class BlockSelectionSupportUtil {
   private BlockSelectionSupportUtil() {
   }

   public static void applySupportValues(@Nonnull BlockSelection selection) {
      Long2ObjectMap<BlockPhysicsUtil.SupportBlock> blocks = new Long2ObjectOpenHashMap<>();
      Fluid emptyFluid = Fluid.getAssetMap().getAsset(0);
      if (emptyFluid != null) {
         selection.forEachBlock(
            (xx, yx, zx, block) -> blocks.put(
               BlockUtil.pack(xx, yx, zx), new BlockPhysicsUtil.SupportBlock(block.blockId(), block.rotation(), block.filler(), 0, emptyFluid)
            )
         );
         if (!blocks.isEmpty()) {
            Long2IntOpenHashMap supportValues = new Long2IntOpenHashMap(blocks.size());
            supportValues.defaultReturnValue(0);
            BlockSelectionSupportUtil.SelectionSupportReader supportReader = new BlockSelectionSupportUtil.SelectionSupportReader(blocks, supportValues);
            int maxSupportDistance = 0;

            for (Long2ObjectMap.Entry<BlockPhysicsUtil.SupportBlock> entry : blocks.long2ObjectEntrySet()) {
               BlockType blockType = BlockType.getAssetMap().getAsset(entry.getValue().blockId());
               if (blockType != null) {
                  maxSupportDistance = Math.max(maxSupportDistance, blockType.getMaxSupportDistance());
               }
            }

            LongArrayList sortedPositions = new LongArrayList(blocks.size());

            for (Long2ObjectMap.Entry<BlockPhysicsUtil.SupportBlock> entry : blocks.long2ObjectEntrySet()) {
               sortedPositions.add(entry.getLongKey());
            }

            sortedPositions.sort(LongComparators.asLongComparator((a, b) -> Integer.compare(BlockUtil.unpackY(a), BlockUtil.unpackY(b))));
            int passes = Math.max(1, maxSupportDistance + 1);

            for (int pass = 0; pass < passes; pass++) {
               for (int i = 0; i < sortedPositions.size(); i++) {
                  long packed = sortedPositions.getLong(i);
                  BlockPhysicsUtil.SupportBlock snapshot = blocks.get(packed);
                  if (snapshot != null) {
                     BlockType blockType = BlockType.getAssetMap().getAsset(snapshot.blockId());
                     if (blockType != null && blockType.hasSupport()) {
                        int x = BlockUtil.unpackX(packed);
                        int y = BlockUtil.unpackY(packed);
                        int z = BlockUtil.unpackZ(packed);
                        int supportDistance = snapshot.filler() != 0
                           ? -1
                           : BlockPhysicsUtil.testBlockPhysics(supportReader, x, y, z, blockType, snapshot.rotation(), snapshot.filler());
                        if (supportDistance == -1 || supportDistance == -2) {
                           supportValues.put(packed, 0);
                        } else if (supportDistance > 0) {
                           supportValues.put(packed, supportDistance);
                        } else {
                           supportValues.put(packed, 0);
                        }
                     }
                  }
               }
            }

            Long2IntOpenHashMap supportUpdates = new Long2IntOpenHashMap();
            selection.forEachBlock((xx, yx, zx, block) -> {
               BlockType blockTypex = BlockType.getAssetMap().getAsset(block.blockId());
               if (blockTypex != null && blockTypex.hasSupport()) {
                  int supportValue = supportValues.get(BlockUtil.pack(xx, yx, zx));
                  if (supportValue != block.supportValue()) {
                     supportUpdates.put(BlockUtil.pack(xx, yx, zx), supportValue);
                  }
               }
            });

            for (Long2IntMap.Entry entry : supportUpdates.long2IntEntrySet()) {
               selection.setSupportValueAtLocalPos(
                  BlockUtil.unpackX(entry.getLongKey()), BlockUtil.unpackY(entry.getLongKey()), BlockUtil.unpackZ(entry.getLongKey()), entry.getIntValue()
               );
            }
         }
      }
   }

   private record SelectionSupportReader(@Nonnull Long2ObjectMap<BlockPhysicsUtil.SupportBlock> blocks, @Nonnull Long2IntOpenHashMap supportValues)
      implements BlockPhysicsUtil.SupportReader {
      @Override
      public boolean isPositionAvailable(int x, int y, int z) {
         return true;
      }

      @Nullable
      @Override
      public BlockPhysicsUtil.SupportBlock getBlock(int x, int y, int z) {
         return this.blocks.get(BlockUtil.pack(x, y, z));
      }

      @Override
      public int getSupportValue(int x, int y, int z) {
         return this.supportValues.get(BlockUtil.pack(x, y, z));
      }

      @Override
      public boolean doesMissingNeighbourSatisfySupport(@Nonnull Vector3ic neighbourDirection) {
         return neighbourDirection.y() < 0;
      }
   }
}
