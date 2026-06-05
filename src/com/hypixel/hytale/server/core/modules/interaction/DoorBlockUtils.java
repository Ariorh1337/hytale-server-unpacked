package com.hypixel.hytale.server.core.modules.interaction;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.TrigMathUtil;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.DoorInteraction;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.util.FillerBlockUtil;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3i;

public final class DoorBlockUtils {
   public static final String OPEN_DOOR_IN = "OpenDoorIn";
   public static final String OPEN_DOOR_OUT = "OpenDoorOut";
   public static final String CLOSE_DOOR_IN = "CloseDoorIn";
   public static final String CLOSE_DOOR_OUT = "CloseDoorOut";

   private DoorBlockUtils() {
   }

   public static boolean isHorizontalDoor(@Nonnull BlockType blockType) {
      String rootInteractionId = blockType.getInteractions().get(InteractionType.Use);
      if (rootInteractionId == null) {
         return false;
      }

      RootInteraction rootInteraction = RootInteraction.getAssetMap().getAsset(rootInteractionId);
      if (rootInteraction == null) {
         return false;
      }

      for (String interactionId : rootInteraction.getInteractionIds()) {
         Interaction interaction = Interaction.getAssetMap().getAsset(interactionId);
         if (interaction instanceof DoorInteraction doorInteraction) {
            return doorInteraction.getIsHorizontal();
         }
      }

      return false;
   }

   @Nonnull
   public static String getInteractionState(@Nonnull DoorBlockUtils.DoorState fromState, @Nonnull DoorBlockUtils.DoorState doorState) {
      if (doorState == DoorBlockUtils.DoorState.CLOSED && fromState == DoorBlockUtils.DoorState.OPENED_IN) {
         return "CloseDoorOut";
      } else if (doorState == DoorBlockUtils.DoorState.CLOSED && fromState == DoorBlockUtils.DoorState.OPENED_OUT) {
         return "CloseDoorIn";
      } else {
         return doorState == DoorBlockUtils.DoorState.OPENED_IN ? "OpenDoorOut" : "OpenDoorIn";
      }
   }

   @Nonnull
   public static DoorBlockUtils.DoorState getOppositeDoorState(@Nonnull DoorBlockUtils.DoorState doorState) {
      return switch (doorState) {
         case CLOSED -> DoorBlockUtils.DoorState.CLOSED;
         case OPENED_IN -> DoorBlockUtils.DoorState.OPENED_OUT;
         case OPENED_OUT -> DoorBlockUtils.DoorState.OPENED_IN;
      };
   }

   public static boolean isInFrontOfDoor(@Nonnull Vector3i blockPosition, @Nullable Rotation doorRotationYaw, @Nonnull Vector3d viewerPosition) {
      double doorRotationRad = Math.toRadians(doorRotationYaw != null ? doorRotationYaw.getDegrees() : 0.0);
      Vector3d doorRotationVector = new Vector3d(TrigMathUtil.sin(doorRotationRad), 0.0, TrigMathUtil.cos(doorRotationRad));
      Vector3d direction = Vector3dUtil.directionTo(new Vector3d(blockPosition).add(0.5, 0.5, 0.5), viewerPosition);
      return direction.dot(doorRotationVector) < 0.0;
   }

   public static boolean canOpenDoor(@Nonnull ChunkStore chunkStore, @Nonnull Vector3i blockPosition, @Nonnull String state) {
      long chunkIndex = ChunkUtil.indexChunkFromBlock(blockPosition.x, blockPosition.z);
      Ref<ChunkStore> chunkReference = chunkStore.getChunkReference(chunkIndex);
      if (chunkReference != null && chunkReference.isValid()) {
         Store<ChunkStore> chunkComponentStore = chunkStore.getStore();
         WorldChunk worldChunkComponent = chunkComponentStore.getComponent(chunkReference, WorldChunk.getComponentType());
         if (worldChunkComponent == null) {
            return false;
         }

         int blockId = worldChunkComponent.getBlock(blockPosition.x, blockPosition.y, blockPosition.z);
         BlockType originalBlockType = BlockType.getAssetMap().getAsset(blockId);
         if (originalBlockType == null) {
            return false;
         }

         BlockType variantBlockType = originalBlockType.getBlockForState(state);
         if (variantBlockType == null) {
            return false;
         }

         BlockChunk blockChunkComponent = chunkComponentStore.getComponent(chunkReference, BlockChunk.getComponentType());
         assert blockChunkComponent != null;
         int rotation = blockChunkComponent.getSectionAtBlockY(blockPosition.y).getRotationIndex(blockPosition.x, blockPosition.y, blockPosition.z);
         return worldChunkComponent.testPlaceBlock(
            blockPosition.x, blockPosition.y, blockPosition.z, variantBlockType, rotation, (blockX, blockY, blockZ, var4, var5x, filler) -> {
               if (filler != 0) {
                  blockX -= FillerBlockUtil.unpackX(filler);
                  blockY -= FillerBlockUtil.unpackY(filler);
                  blockZ -= FillerBlockUtil.unpackZ(filler);
               }

               return blockX == blockPosition.x && blockY == blockPosition.y && blockZ == blockPosition.z;
            }
         );
      } else {
         return false;
      }
   }

   public enum DoorState {
      CLOSED,
      OPENED_IN,
      OPENED_OUT;

      @Nonnull
      public static DoorBlockUtils.DoorState fromBlockState(@Nullable String state) {
         if (state == null) {
            return CLOSED;
         }

         return switch (state) {
            case "OpenDoorOut" -> OPENED_IN;
            case "OpenDoorIn" -> OPENED_OUT;
            default -> CLOSED;
         };
      }
   }
}
