package com.hypixel.hytale.builtin.fallingblocks;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.BlockParticleEvent;
import com.hypixel.hytale.protocol.BlockSoundEvent;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.asset.type.blockhitbox.BlockBoundingBoxes;
import com.hypixel.hytale.server.core.asset.type.blocksound.config.BlockSoundSet;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.fallingblocks.FallingBlockImpact;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.FillerBlockUtil;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class BreakFallingBlockImpact extends FallingBlockImpact {
   public static final String ID = "Break";
   public static final BuilderCodec<BreakFallingBlockImpact> CODEC = BuilderCodec.builder(BreakFallingBlockImpact.class, BreakFallingBlockImpact::new).build();

   @Override
   public void apply(
      @Nonnull WorldChunk worldChunk,
      @Nonnull World world,
      @Nonnull BlockType blockType,
      @Nonnull Vector3d position,
      @Nonnull RotationTuple rotation,
      @Nonnull Store<EntityStore> store
   ) {
      breakFallingBlock(world, blockType, position, rotation, store);
   }

   public static void breakFallingBlock(
      @Nonnull World world, @Nonnull BlockType blockType, @Nonnull Vector3d position, @Nonnull RotationTuple rotation, @Nonnull Store<EntityStore> store
   ) {
      int blockId = BlockType.getAssetMap().getIndex(blockType.getId());
      BlockBoundingBoxes blockBoundingBoxes = BlockBoundingBoxes.getAssetMap().getAsset(blockType.getHitboxTypeIndex());
      if (blockBoundingBoxes != null) {
         BlockBoundingBoxes.RotatedVariantBoxes rotatedBoxes = blockBoundingBoxes.get(rotation.index());
         if (rotatedBoxes != null) {
            FillerBlockUtil.forEachFillerBlock(
               rotatedBoxes,
               (x, y, z) -> world.getNotificationHandler().sendBlockParticle(position.x + x, position.y + y, position.z + z, blockId, BlockParticleEvent.Break)
            );
            BlockSoundSet soundSet = BlockSoundSet.getAssetMap().getAsset(blockType.getBlockSoundSetIndex());
            if (soundSet != null) {
               int soundEventIndex = soundSet.getSoundEventIndices().getOrDefault(BlockSoundEvent.Break, 0);
               if (soundEventIndex != 0) {
                  SoundUtil.playSoundEvent3d(soundEventIndex, SoundCategory.SFX, position, store);
               }
            }
         }
      }
   }
}
