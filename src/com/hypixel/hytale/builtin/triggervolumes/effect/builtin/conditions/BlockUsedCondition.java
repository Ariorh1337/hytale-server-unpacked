package com.hypixel.hytale.builtin.triggervolumes.effect.builtin.conditions;

import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerCondition;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerContext;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEventType;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.world.World;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class BlockUsedCondition extends TriggerCondition {
   @Nonnull
   public static final BuilderCodec<BlockUsedCondition> CODEC = BuilderCodec.builder(BlockUsedCondition.class, BlockUsedCondition::new, BASE_CODEC)
      .append(
         new KeyedCodec<>("BlockType", Codec.STRING_ARRAY, false),
         (condition, blockTypes) -> condition.blockTypes = blockTypes != null ? blockTypes : new String[0],
         condition -> condition.blockTypes.length == 0 ? null : condition.blockTypes
      )
      .add()
      .append(
         new KeyedCodec<>("State", Codec.STRING, false),
         (condition, value) -> condition.state = value != null ? value : "",
         condition -> condition.state.isEmpty() ? null : condition.state
      )
      .add()
      .append(
         new KeyedCodec<>("MatchBlockAsUsed", Codec.BOOLEAN, false),
         (condition, value) -> condition.matchBlockAsUsed = Boolean.TRUE.equals(value),
         condition -> condition.matchBlockAsUsed ? Boolean.TRUE : null
      )
      .add()
      .build();
   @Nonnull
   private String[] blockTypes = new String[0];
   @Nonnull
   private String state = "";
   private boolean matchBlockAsUsed = false;

   @Nonnull
   public static BlockUsedCondition create(@Nonnull TriggerEventType eventType) {
      BlockUsedCondition condition = new BlockUsedCondition();
      condition.setEventType(eventType);
      return condition;
   }

   @Override
   public boolean test(@Nonnull TriggerContext context) {
      BlockType placed;
      if (this.matchBlockAsUsed) {
         String blockId = context.getBlockId();
         placed = blockId != null ? BlockType.getAssetMap().getAsset(blockId) : null;
      } else {
         World world = context.getStore().getExternalData().getWorld();
         Vector3d blockPos = context.getBlockPosition();
         if (world == null || blockPos == null) {
            return false;
         }

         placed = world.getBlockType((int)Math.floor(blockPos.x()), (int)Math.floor(blockPos.y()), (int)Math.floor(blockPos.z()));
      }

      if ((this.blockTypes.length > 0 || !this.state.isEmpty()) && placed == null) {
         return false;
      }

      String blockId = placed != null ? placed.getId() : null;
      if (this.blockTypes.length > 0) {
         String baseKey = placed != null ? placed.getBlockKeyForState("default") : null;
         if (baseKey == null) {
            baseKey = blockId;
         }

         List<String> accepted = Arrays.asList(this.blockTypes);
         if (!accepted.contains(baseKey) && !accepted.contains(blockId)) {
            return false;
         }
      }

      if (!this.state.isEmpty()) {
         String current = placed != null ? placed.getStateForBlock(placed) : null;
         if (current == null) {
            current = "default";
         }

         boolean matched = false;

         for (String token : this.state.split(",")) {
            String trimmed = token.trim();
            if (!trimmed.isEmpty() && trimmed.equalsIgnoreCase(current)) {
               matched = true;
               break;
            }
         }

         if (!matched) {
            return false;
         }
      }

      return context.getInteractionType() == InteractionType.Use;
   }
}
