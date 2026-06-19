package com.hypixel.hytale.builtin.triggervolumes.effect.builtin;

import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerContext;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.builtin.triggervolumes.manager.VolumeEntry;
import com.hypixel.hytale.builtin.triggervolumes.shape.TriggerVolumeShape;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.block.BlockUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.modules.interaction.DoorBlockUtils;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.FillerBlockUtil;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import javax.annotation.Nonnull;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class ControlDoorsEffect extends TriggerEffect {
   @Nonnull
   public static final BuilderCodec<ControlDoorsEffect> CODEC = BuilderCodec.builder(ControlDoorsEffect.class, ControlDoorsEffect::new, BASE_CODEC)
      .append(new KeyedCodec<>("Action", new EnumCodec<>(ControlDoorsEffect.DoorAction.class)), (e, v) -> e.action = v, e -> e.action)
      .add()
      .build();
   @Nonnull
   private ControlDoorsEffect.DoorAction action = ControlDoorsEffect.DoorAction.CLOSE;

   @Override
   public void execute(@Nonnull TriggerContext context) {
      Store<EntityStore> store = context.getStore();
      World world = store.getExternalData().getWorld();
      Vector3d actorPosition = context.getActorPosition();
      Vector3d triggerPos = actorPosition != null ? actorPosition : new Vector3d(context.getVolume().getPosition());
      ChunkStore chunkStore = world.getChunkStore();
      Store<ChunkStore> chunkComponentStore = chunkStore.getStore();
      Vector3d min = new Vector3d();
      Vector3d max = new Vector3d();
      LongOpenHashSet processedBlocks = new LongOpenHashSet();

      for (VolumeEntry volume : context.getSpatialVolumes()) {
         TriggerVolumeShape shape = volume.getShape();
         Vector3d origin = volume.getPosition();
         shape.getWorldAABB(origin, min, max);
         int minX = MathUtil.floor(min.x());
         int minY = MathUtil.floor(min.y());
         int minZ = MathUtil.floor(min.z());
         int maxX = MathUtil.floor(max.x());
         int maxY = MathUtil.floor(max.y());
         int maxZ = MathUtil.floor(max.z());

         for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
               for (int z = minZ; z <= maxZ; z++) {
                  Ref<ChunkStore> sectionRef = chunkStore.getChunkSectionReferenceAtBlock(x, y, z);
                  if (sectionRef != null && sectionRef.isValid()) {
                     BlockSection section = chunkComponentStore.getComponent(sectionRef, BlockSection.getComponentType());
                     if (section != null) {
                        BlockType blockType = BlockType.getAssetMap().getAsset(section.get(x, y, z));
                        if (blockType != null && blockType.isDoor()) {
                           Vector3i anchor = doorAnchorForCell(chunkStore, x, y, z);
                           if (processedBlocks.add(BlockUtil.pack(anchor.x, anchor.y, anchor.z))) {
                              Ref<ChunkStore> anchorSectionRef = chunkStore.getChunkSectionReferenceAtBlock(anchor.x, anchor.y, anchor.z);
                              if (anchorSectionRef != null && anchorSectionRef.isValid()) {
                                 BlockSection anchorSection = chunkComponentStore.getComponent(anchorSectionRef, BlockSection.getComponentType());
                                 if (anchorSection != null) {
                                    BlockType typeAtAnchor = BlockType.getAssetMap().getAsset(anchorSection.get(anchor.x, anchor.y, anchor.z));
                                    if (typeAtAnchor != null && typeAtAnchor.isDoor()) {
                                       this.applyDoorState(world, chunkStore, anchorSection, typeAtAnchor, anchor.x, anchor.y, anchor.z, triggerPos);
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

   @Nonnull
   private static Vector3i doorAnchorForCell(@Nonnull ChunkStore chunkStore, int x, int y, int z) {
      Ref<ChunkStore> sectionRef = chunkStore.getChunkSectionReferenceAtBlock(x, y, z);
      if (sectionRef != null && sectionRef.isValid()) {
         BlockSection section = chunkStore.getStore().getComponent(sectionRef, BlockSection.getComponentType());
         if (section == null) {
            return new Vector3i(x, y, z);
         }

         int filler = section.getFiller(x, y, z);
         return filler == 0
            ? new Vector3i(x, y, z)
            : new Vector3i(x - FillerBlockUtil.unpackX(filler), y - FillerBlockUtil.unpackY(filler), z - FillerBlockUtil.unpackZ(filler));
      } else {
         return new Vector3i(x, y, z);
      }
   }

   private void applyDoorState(
      @Nonnull World world,
      @Nonnull ChunkStore chunkStore,
      @Nonnull BlockSection section,
      @Nonnull BlockType blockType,
      int x,
      int y,
      int z,
      @Nonnull Vector3d triggerPos
   ) {
      Vector3i pos = new Vector3i(x, y, z);
      String blockState = blockType.getStateForBlock(blockType);
      DoorBlockUtils.DoorState doorState = DoorBlockUtils.DoorState.fromBlockState(blockState);
      if (this.action == ControlDoorsEffect.DoorAction.OPEN) {
         if (doorState != DoorBlockUtils.DoorState.CLOSED) {
            return;
         }

         DoorBlockUtils.DoorState preferred;
         if (DoorBlockUtils.isHorizontalDoor(blockType)) {
            preferred = DoorBlockUtils.DoorState.OPENED_IN;
         } else {
            int rotation = section.getRotationIndex(x, y, z);
            Rotation yaw = RotationTuple.get(rotation).yaw();
            preferred = DoorBlockUtils.isInFrontOfDoor(pos, yaw, triggerPos) ? DoorBlockUtils.DoorState.OPENED_OUT : DoorBlockUtils.DoorState.OPENED_IN;
         }

         DoorBlockUtils.DoorState alternate = DoorBlockUtils.getOppositeDoorState(preferred);
         tryOpen(world, chunkStore, pos, blockType, preferred);
         Ref<ChunkStore> sectionAfterRef = chunkStore.getChunkSectionReferenceAtBlock(x, y, z);
         if (sectionAfterRef == null || !sectionAfterRef.isValid()) {
            return;
         }

         BlockSection sectionAfter = chunkStore.getStore().getComponent(sectionAfterRef, BlockSection.getComponentType());
         if (sectionAfter == null) {
            return;
         }

         BlockType updatedType = BlockType.getAssetMap().getAsset(sectionAfter.get(x, y, z));
         if (updatedType == null) {
            return;
         }

         if (DoorBlockUtils.DoorState.fromBlockState(updatedType.getStateForBlock(updatedType)) == DoorBlockUtils.DoorState.CLOSED) {
            tryOpen(world, chunkStore, pos, updatedType, alternate);
         }
      } else {
         if (doorState == DoorBlockUtils.DoorState.CLOSED) {
            return;
         }

         String closeInteraction = DoorBlockUtils.getInteractionState(doorState, DoorBlockUtils.DoorState.CLOSED);
         world.setBlockInteractionState(pos, blockType, closeInteraction);
      }
   }

   private static void tryOpen(
      @Nonnull World world, @Nonnull ChunkStore chunkStore, @Nonnull Vector3i pos, @Nonnull BlockType blockType, @Nonnull DoorBlockUtils.DoorState targetOpen
   ) {
      String interaction = DoorBlockUtils.getInteractionState(DoorBlockUtils.DoorState.CLOSED, targetOpen);
      if (DoorBlockUtils.canOpenDoor(chunkStore, pos, interaction)) {
         world.setBlockInteractionState(pos, blockType, interaction);
      }
   }

   private enum DoorAction {
      OPEN,
      CLOSE;
   }
}
