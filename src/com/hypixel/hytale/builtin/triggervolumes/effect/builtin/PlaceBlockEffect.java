package com.hypixel.hytale.builtin.triggervolumes.effect.builtin;

import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerContext;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.prefab.selection.mask.BlockFilter;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.ChunkColumn;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.FluidSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;

public class PlaceBlockEffect extends TriggerEffect {
   @Nonnull
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   @Nonnull
   public static final BuilderCodec<PlaceBlockEffect> CODEC = BuilderCodec.builder(PlaceBlockEffect.class, PlaceBlockEffect::new, BASE_CODEC)
      .append(new KeyedCodec<>("BlockType", Codec.STRING), (effect, blockType) -> effect.blockType = blockType, effect -> effect.blockType)
      .add()
      .append(new KeyedCodec<>("Position", Vector3dUtil.CODEC, false), (effect, position) -> effect.position = position, effect -> effect.position)
      .add()
      .append(
         new KeyedCodec<>("Origin", new EnumCodec<>(PlaceBlockEffect.Origin.class), false), (effect, origin) -> effect.origin = origin, effect -> effect.origin
      )
      .add()
      .append(
         new KeyedCodec<>("ReplaceMode", new EnumCodec<>(PlaceBlockEffect.ReplaceMode.class), false),
         (effect, replaceMode) -> effect.replaceMode = replaceMode,
         effect -> effect.replaceMode
      )
      .add()
      .append(new KeyedCodec<>("Rotation", new EnumCodec<>(Rotation.class), false), (effect, rotation) -> effect.rotation = rotation, effect -> effect.rotation)
      .add()
      .build();
   @Nullable
   private String blockType;
   @Nonnull
   private Vector3d position = new Vector3d();
   @Nonnull
   private PlaceBlockEffect.Origin origin = PlaceBlockEffect.Origin.VOLUME_ORIGIN;
   @Nonnull
   private PlaceBlockEffect.ReplaceMode replaceMode = PlaceBlockEffect.ReplaceMode.ALWAYS;
   @Nonnull
   private Rotation rotation = Rotation.None;

   @Override
   public void execute(@Nonnull TriggerContext context) {
      if (this.blockType != null && !this.blockType.isBlank()) {
         World world = context.getStore().getExternalData().getWorld();
         PlaceBlockEffect.TargetType targetType = resolveTargetType(this.blockType);
         if (targetType != null) {
            Vector3d target = this.resolveTargetPosition(context);
            int blockX = MathUtil.floor(target.x());
            int blockY = MathUtil.floor(target.y());
            int blockZ = MathUtil.floor(target.z());
            ChunkStore chunkStore = world.getChunkStore();
            Store<ChunkStore> chunkComponentStore = chunkStore.getStore();
            long chunkIndex = ChunkUtil.indexChunkFromBlock(blockX, blockZ);
            Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(chunkIndex);
            if (chunkRef != null && chunkRef.isValid()) {
               BlockChunk blockChunkComponent = chunkComponentStore.getComponent(chunkRef, BlockChunk.getComponentType());
               if (blockChunkComponent != null) {
                  if (this.replaceMode != PlaceBlockEffect.ReplaceMode.ONLY_AIR || blockChunkComponent.getBlock(blockX, blockY, blockZ) == 0) {
                     WorldChunk worldChunkComponent = chunkComponentStore.getComponent(chunkRef, WorldChunk.getComponentType());
                     if (worldChunkComponent != null) {
                        if (targetType.fluidId() != 0) {
                           worldChunkComponent.setBlock(blockX, blockY, blockZ, 0, BlockType.EMPTY, 0, 0, 256);
                           setFluid(chunkStore, chunkRef, blockX, blockY, blockZ, targetType.fluidId());
                        } else {
                           clearFluid(chunkStore, chunkRef, blockX, blockY, blockZ);
                           BlockType blockTypeAsset = BlockType.getAssetMap().getAsset(targetType.blockId());
                           if (blockTypeAsset != null) {
                              int rotationIndex = RotationTuple.index(this.rotation, Rotation.None, Rotation.None);
                              worldChunkComponent.setBlock(blockX, blockY, blockZ, targetType.blockId(), blockTypeAsset, rotationIndex, 0, 256);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Nullable
   private static PlaceBlockEffect.TargetType resolveTargetType(@Nonnull String blockType) {
      BlockFilter.BlocksAndFluids resolvedTypes = BlockFilter.parseBlocksAndFluids(new String[]{blockType});
      if (resolvedTypes.hasInvalidBlocks()) {
         return null;
      } else {
         int blockCount = resolvedTypes.blocks().size();
         int fluidCount = resolvedTypes.fluids() != null ? resolvedTypes.fluids().size() : 0;
         if (blockCount + fluidCount != 1) {
            LOGGER.at(Level.FINE).log("PlaceBlockEffect: target '%s' resolves to %d blocks and %d fluids", blockType, blockCount, fluidCount);
            return null;
         } else if (blockCount == 1) {
            IntIterator blockIterator = resolvedTypes.blocks().iterator();
            return new PlaceBlockEffect.TargetType(blockIterator.nextInt(), 0);
         } else {
            IntIterator fluidIterator = resolvedTypes.fluids().iterator();
            return new PlaceBlockEffect.TargetType(0, fluidIterator.nextInt());
         }
      }
   }

   private static void setFluid(@Nonnull ChunkStore chunkStore, @Nonnull Ref<ChunkStore> chunkRef, int blockX, int blockY, int blockZ, int fluidId) {
      Fluid fluid = Fluid.getAssetMap().getAsset(fluidId);
      if (fluid != null) {
         Store<ChunkStore> chunkComponentStore = chunkStore.getStore();
         ChunkColumn chunkColumnComponent = chunkComponentStore.getComponent(chunkRef, ChunkColumn.getComponentType());
         if (chunkColumnComponent != null) {
            Ref<ChunkStore> sectionRef = chunkColumnComponent.getSection(ChunkUtil.chunkCoordinate(blockY));
            if (sectionRef != null && sectionRef.isValid()) {
               FluidSection fluidSection = chunkComponentStore.ensureAndGetComponent(sectionRef, FluidSection.getComponentType());
               fluidSection.setFluid(blockX, blockY, blockZ, fluidId, (byte)fluid.getMaxFluidLevel());
            }
         }
      }
   }

   private static void clearFluid(@Nonnull ChunkStore chunkStore, @Nonnull Ref<ChunkStore> chunkRef, int blockX, int blockY, int blockZ) {
      Store<ChunkStore> chunkComponentStore = chunkStore.getStore();
      ChunkColumn chunkColumnComponent = chunkComponentStore.getComponent(chunkRef, ChunkColumn.getComponentType());
      if (chunkColumnComponent != null) {
         Ref<ChunkStore> sectionRef = chunkColumnComponent.getSection(ChunkUtil.chunkCoordinate(blockY));
         if (sectionRef != null && sectionRef.isValid()) {
            FluidSection fluidSectionComponent = chunkComponentStore.getComponent(sectionRef, FluidSection.getComponentType());
            if (fluidSectionComponent != null) {
               if (fluidSectionComponent.getFluidId(blockX, blockY, blockZ) != 0) {
                  fluidSectionComponent.setFluid(blockX, blockY, blockZ, 0, (byte)0);
               }
            }
         }
      }
   }

   @Nonnull
   private Vector3d resolveTargetPosition(@Nonnull TriggerContext context) {
      Vector3d offset = this.position != null ? new Vector3d(this.position) : new Vector3d();

      return switch (this.origin != null ? this.origin : PlaceBlockEffect.Origin.VOLUME_ORIGIN) {
         case VOLUME_ORIGIN -> new Vector3d(context.getVolume().getPosition()).add(offset);
         case ENTITY -> {
            Vector3d actorPosition = context.getActorPosition();
            Vector3d base = actorPosition != null ? actorPosition : new Vector3d(context.getVolume().getPosition());
            yield base.add(offset);
         }
         case WORLD_ABSOLUTE -> offset;
      };
   }

   public enum Origin {
      VOLUME_ORIGIN,
      ENTITY,
      WORLD_ABSOLUTE;
   }

   public enum ReplaceMode {
      ALWAYS,
      ONLY_AIR;
   }

   private record TargetType(int blockId, int fluidId) {
   }
}
