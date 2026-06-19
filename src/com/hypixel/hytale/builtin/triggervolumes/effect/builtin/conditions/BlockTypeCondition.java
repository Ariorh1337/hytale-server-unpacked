package com.hypixel.hytale.builtin.triggervolumes.effect.builtin.conditions;

import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerCondition;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerContext;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEventType;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import java.util.Arrays;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class BlockTypeCondition extends TriggerCondition {
   @Nonnull
   public static final BuilderCodec<BlockTypeCondition> CODEC = BuilderCodec.builder(BlockTypeCondition.class, BlockTypeCondition::new, BASE_CODEC)
      .append(
         new KeyedCodec<>("BlockType", Codec.STRING_ARRAY, false),
         (condition, blockTypes) -> condition.blockTypes = blockTypes != null ? blockTypes : new String[0],
         condition -> condition.blockTypes.length == 0 ? null : condition.blockTypes
      )
      .add()
      .append(
         new KeyedCodec<>("MatchRotation", Codec.BOOLEAN, false),
         (condition, matchRotation) -> condition.matchRotation = matchRotation,
         condition -> condition.matchRotation
      )
      .add()
      .append(
         new KeyedCodec<>("Rotation", new EnumCodec<>(Rotation.class), false),
         (condition, rotation) -> condition.rotation = rotation,
         condition -> condition.rotation
      )
      .add()
      .build();
   @Nonnull
   private String[] blockTypes = new String[0];
   private boolean matchRotation;
   @Nonnull
   private Rotation rotation = Rotation.None;

   @Nonnull
   public static BlockTypeCondition create(@Nonnull TriggerEventType eventType, @Nonnull String... blockTypes) {
      BlockTypeCondition condition = new BlockTypeCondition();
      condition.setEventType(eventType);
      condition.blockTypes = blockTypes;
      return condition;
   }

   @Override
   public boolean test(@Nonnull TriggerContext context) {
      String blockId = context.getBlockId();
      if (blockId != null && this.blockTypes.length != 0) {
         return !Arrays.asList(this.blockTypes).contains(blockId) ? false : !this.matchRotation || this.matchesRotation(context);
      } else {
         return false;
      }
   }

   private boolean matchesRotation(@Nonnull TriggerContext context) {
      Vector3d blockPosition = context.getBlockPosition();
      if (blockPosition == null) {
         return false;
      }

      World world = context.getStore().getExternalData().getWorld();
      if (world == null) {
         return false;
      }

      int blockX = MathUtil.floor(blockPosition.x());
      int blockY = MathUtil.floor(blockPosition.y());
      int blockZ = MathUtil.floor(blockPosition.z());
      ChunkStore chunkStore = world.getChunkStore();
      Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(ChunkUtil.indexChunkFromBlock(blockX, blockZ));
      if (chunkRef != null && chunkRef.isValid()) {
         BlockChunk blockChunk = chunkStore.getStore().getComponent(chunkRef, BlockChunk.getComponentType());
         if (blockChunk == null) {
            return false;
         }

         int rotationIndex = blockChunk.getSectionAtBlockY(blockY).getRotationIndex(blockX, blockY, blockZ);
         return RotationTuple.get(rotationIndex).yaw() == this.rotation;
      } else {
         return false;
      }
   }
}
