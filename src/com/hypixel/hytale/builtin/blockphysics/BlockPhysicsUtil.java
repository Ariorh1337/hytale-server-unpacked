package com.hypixel.hytale.builtin.blockphysics;

import com.hypixel.hytale.builtin.fallingblocks.FallingBlock;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.server.core.asset.type.blockhitbox.BlockBoundingBoxes;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockFace;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockFaceSupport;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RequiredBlockFaceSupport;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.blocktype.component.BlockPhysics;
import com.hypixel.hytale.server.core.modules.blockset.BlockSetModule;
import com.hypixel.hytale.server.core.modules.interaction.BlockHarvestUtils;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.FluidSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.FillerBlockUtil;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3i;
import org.joml.Vector3ic;

public class BlockPhysicsUtil {
   public static final int DOESNT_SATISFY = 0;
   public static final int IGNORE = -1;
   public static final int SATISFIES_SUPPORT = -2;
   public static final int WAITING_CHUNK = -3;

   @Nonnull
   public static BlockPhysicsUtil.Result applyBlockPhysics(
      @Nonnull ComponentAccessor<EntityStore> componentAccessor,
      @Nonnull Ref<ChunkStore> chunkReference,
      @Nonnull BlockPhysicsSystems.CachedAccessor chunkAccessor,
      BlockSection blockSection,
      @Nonnull BlockPhysics blockPhysics,
      @Nonnull FluidSection fluidSection,
      int blockX,
      int blockY,
      int blockZ,
      @Nonnull BlockType blockType,
      int rotation,
      int filler
   ) {
      if (filler != 0) {
         return BlockPhysicsUtil.Result.VALID;
      }

      int supportDistance = testBlockPhysics(chunkAccessor, blockSection, blockPhysics, fluidSection, blockX, blockY, blockZ, blockType, rotation, filler);
      if (supportDistance == 0) {
         World world = componentAccessor.getExternalData().getWorld();
         Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
         switch (blockType.getSupportDropType()) {
            case BREAK:
               BlockHarvestUtils.naturallyRemoveBlockByPhysics(
                  new Vector3i(blockX, blockY, blockZ), blockType, filler, 256, chunkReference, componentAccessor, chunkStore
               );
               break;
            case DESTROY:
               BlockHarvestUtils.naturallyRemoveBlockByPhysics(
                  new Vector3i(blockX, blockY, blockZ), blockType, filler, 2304, chunkReference, componentAccessor, chunkStore
               );
               break;
            case FALL:
               FallingBlock.fallBlock(world, componentAccessor.getExternalData().getStore(), blockX, blockY, blockZ);
         }

         return BlockPhysicsUtil.Result.INVALID;
      } else {
         if (supportDistance == -1) {
            return BlockPhysicsUtil.Result.VALID;
         }

         if (supportDistance == -3) {
            return BlockPhysicsUtil.Result.WAITING_CHUNK;
         }

         int currentSupport = blockPhysics.get(blockX, blockY, blockZ);
         if (supportDistance == -2) {
            if (currentSupport != 0) {
               blockPhysics.set(blockX, blockY, blockZ, 0);
               chunkAccessor.performBlockUpdate(blockX, blockY, blockZ);
            }

            return BlockPhysicsUtil.Result.VALID;
         } else {
            if (currentSupport == supportDistance) {
               chunkAccessor.performBlockUpdate(blockX, blockY, blockZ, supportDistance - 1);
            } else {
               blockPhysics.set(blockX, blockY, blockZ, supportDistance);
               chunkAccessor.performBlockUpdate(blockX, blockY, blockZ);
            }

            return BlockPhysicsUtil.Result.VALID;
         }
      }
   }

   public static int testBlockPhysics(
      @Nonnull BlockPhysicsSystems.CachedAccessor chunkAccessor,
      BlockSection blockSection,
      @Nullable BlockPhysics blockPhysics,
      @Nonnull FluidSection fluidSection,
      int blockX,
      int blockY,
      int blockZ,
      @Nonnull BlockType blockType,
      int rotation,
      int filler
   ) {
      return testBlockPhysics(
         new BlockPhysicsUtil.WorldSupportReader(chunkAccessor, blockSection, blockPhysics, fluidSection, blockX, blockY, blockZ),
         blockX,
         blockY,
         blockZ,
         blockType,
         rotation,
         filler
      );
   }

   public static int testBlockPhysics(
      @Nonnull BlockPhysicsUtil.SupportReader supportReader, int blockX, int blockY, int blockZ, @Nonnull BlockType blockType, int rotation, int filler
   ) {
      int supportDistance = -1;
      if (blockType.getHitboxTypeIndex() != 0) {
         BlockBoundingBoxes boundingBoxes = BlockBoundingBoxes.getAssetMap().getAsset(blockType.getHitboxTypeIndex());
         if (boundingBoxes != null && boundingBoxes.protrudesUnitBox()) {
            BlockBoundingBoxes.RotatedVariantBoxes rotatedBox = boundingBoxes.get(rotation);
            Box boundingBox = rotatedBox.getBoundingBox();
            int minX = (int)boundingBox.min.x;
            int minY = (int)boundingBox.min.y;
            int minZ = (int)boundingBox.min.z;
            if (minX - boundingBox.min.x > 0.0) {
               minX--;
            }

            if (minY - boundingBox.min.y > 0.0) {
               minY--;
            }

            if (minZ - boundingBox.min.z > 0.0) {
               minZ--;
            }

            int maxX = (int)boundingBox.max.x;
            int maxY = (int)boundingBox.max.y;
            int maxZ = (int)boundingBox.max.z;
            if (boundingBox.max.x - maxX > 0.0) {
               maxX++;
            }

            if (boundingBox.max.y - maxY > 0.0) {
               maxY++;
            }

            if (boundingBox.max.z - maxZ > 0.0) {
               maxZ++;
            }

            int blockWidth = Math.max(maxX - minX, 1);
            int blockHeight = Math.max(maxY - minY, 1);
            int blockDepth = Math.max(maxZ - minZ, 1);

            for (int x = 0; x < blockWidth; x++) {
               for (int y = 0; y < blockHeight; y++) {
                  for (int z = 0; z < blockDepth; z++) {
                     int fillerX = blockX + minX + x;
                     int fillerY = blockY + minY + y;
                     int fillerZ = blockZ + minZ + z;
                     int neighbourFiller = FillerBlockUtil.pack(minX + x, minY + y, minZ + z);
                     if (!supportReader.isPositionAvailable(fillerX, fillerY, fillerZ)) {
                        return -3;
                     }

                     int fillerRotation = supportReader.getProtrudingFillerRotation(fillerX, fillerY, fillerZ, rotation);
                     int fillerSupportDistance = testBlockPhysicsAtPosition(
                        supportReader, fillerX, fillerY, fillerZ, blockType, fillerRotation, neighbourFiller
                     );
                     if (fillerSupportDistance != -1) {
                        switch (blockType.getBlockSupportsRequiredFor()) {
                           case Any:
                              if (fillerSupportDistance == -2) {
                                 int supportDistancex = -2;
                                 return supportDistancex;
                              }

                              if (fillerSupportDistance == 0) {
                                 supportDistance = 0;
                              } else if (supportDistance < fillerSupportDistance) {
                                 supportDistance = fillerSupportDistance;
                              }
                              break;
                           case All:
                              if (fillerSupportDistance == 0) {
                                 int supportDistancex = 0;
                                 return supportDistancex;
                              }

                              if (fillerSupportDistance == -2) {
                                 supportDistance = -2;
                              } else if (supportDistance == -1 && supportDistance < fillerSupportDistance) {
                                 supportDistance = fillerSupportDistance;
                              }
                        }
                     }
                  }
               }
            }
         } else {
            supportDistance = testBlockPhysicsAtPosition(supportReader, blockX, blockY, blockZ, blockType, rotation, filler);
         }
      } else {
         supportDistance = testBlockPhysicsAtPosition(supportReader, blockX, blockY, blockZ, blockType, rotation, filler);
      }

      return supportDistance;
   }

   private static int testBlockPhysicsAtPosition(
      @Nonnull BlockPhysicsUtil.SupportReader supportReader, int blockX, int blockY, int blockZ, @Nonnull BlockType blockType, int rotation, int filler
   ) {
      if (blockType.isUnknown()) {
         return -1;
      }

      Map<BlockFace, RequiredBlockFaceSupport[]> requiredBlockFaceSupportMap = blockType.getSupport(rotation);
      if (requiredBlockFaceSupportMap != null && !requiredBlockFaceSupportMap.isEmpty()) {
         Vector3i blockFillerOffset = new Vector3i(FillerBlockUtil.unpackX(filler), FillerBlockUtil.unpackY(filler), FillerBlockUtil.unpackZ(filler));
         Vector3i neighbourFillerOffset = new Vector3i();
         BlockBoundingBoxes hitbox = BlockBoundingBoxes.getAssetMap().getAsset(blockType.getHitboxTypeIndex());
         if (hitbox == null) {
            return -1;
         }

         Box boundingBox = hitbox.get(rotation).getBoundingBox();
         Vector3i origin = new Vector3i(
            blockX - FillerBlockUtil.unpackX(filler), blockY - FillerBlockUtil.unpackY(filler), blockZ - FillerBlockUtil.unpackZ(filler)
         );
         boolean hasTestedForSupport = false;
         int requiredSupportDistance = blockType.getMaxSupportDistance();
         int lowestSupportDistance = Integer.MAX_VALUE;

         for (BlockFace blockFace : BlockFace.VALUES) {
            RequiredBlockFaceSupport[] requiredBlockFaceSupports = requiredBlockFaceSupportMap.get(blockFace);
            if (requiredBlockFaceSupports != null && requiredBlockFaceSupports.length != 0) {
               BlockFace[] connectingFaces = blockFace.getConnectingFaces();
               Vector3ic[] connectingFaceOffsets = blockFace.getConnectingFaceOffsets();

               for (int i = 0; i < connectingFaces.length; i++) {
                  BlockFace neighbourBlockFace = connectingFaces[i];
                  Vector3ic neighbourDirection = connectingFaceOffsets[i];
                  int neighbourX = blockX + neighbourDirection.x();
                  int neighbourY = blockY + neighbourDirection.y();
                  int neighbourZ = blockZ + neighbourDirection.z();
                  if (!boundingBox.containsBlock(origin, neighbourX, neighbourY, neighbourZ)) {
                     if (!supportReader.isPositionAvailable(neighbourX, neighbourY, neighbourZ)) {
                        return -3;
                     }

                     BlockPhysicsUtil.SupportBlock neighbour = supportReader.getBlock(neighbourX, neighbourY, neighbourZ);
                     boolean missingNeighbourSatisfiesSupport = neighbour == null && supportReader.doesMissingNeighbourSatisfySupport(neighbourDirection);
                     int neighbourBlockId = 0;
                     int neighbourRotation = rotation;
                     int neighbourFiller = 0;
                     int neighbourFluidId = 0;
                     Fluid neighbourFluid = Fluid.getAssetMap().getAsset(0);
                     BlockType neighbourBlockType = BlockType.getAssetMap().getAsset(0);
                     if (neighbour != null) {
                        neighbourBlockId = neighbour.blockId();
                        neighbourRotation = neighbour.rotation();
                        neighbourFiller = neighbour.filler();
                        neighbourFluidId = neighbour.fluidId();
                        neighbourFluid = neighbour.fluid();
                        neighbourBlockType = BlockType.getAssetMap().getAsset(neighbourBlockId);
                        if (neighbourBlockType == null
                           || neighbourFluid == null
                           || neighbourFiller != 0
                              && neighbourBlockType == blockType
                              && neighbourX - FillerBlockUtil.unpackX(neighbourFiller) == origin.x
                              && neighbourY - FillerBlockUtil.unpackY(neighbourFiller) == origin.y
                              && neighbourZ - FillerBlockUtil.unpackZ(neighbourFiller) == origin.z) {
                           continue;
                        }
                     }

                     neighbourFillerOffset.set(
                        FillerBlockUtil.unpackX(neighbourFiller), FillerBlockUtil.unpackY(neighbourFiller), FillerBlockUtil.unpackZ(neighbourFiller)
                     );
                     boolean doesSatisfySupport = false;
                     boolean failedSatisfySupport = false;

                     for (RequiredBlockFaceSupport requiredBlockFaceSupport : requiredBlockFaceSupports) {
                        if (requiredBlockFaceSupport.isAppliedToFiller(blockFillerOffset)) {
                           boolean doesSatisfyRequirements = missingNeighbourSatisfiesSupport;
                           if (!doesSatisfyRequirements && neighbour != null) {
                              doesSatisfyRequirements = doesSatisfyRequirements(
                                 blockType,
                                 blockFillerOffset,
                                 neighbourFillerOffset,
                                 blockFace,
                                 neighbourBlockFace,
                                 neighbourBlockId,
                                 neighbourBlockType,
                                 neighbourRotation,
                                 neighbourFluidId,
                                 neighbourFluid,
                                 requiredBlockFaceSupport
                              );
                           }

                           if (doesSatisfyRequirements && requiredSupportDistance > 0 && requiredBlockFaceSupport.allowsSupportPropagation()) {
                              int supportDistance = neighbour != null
                                 ? supportReader.getSupportValue(neighbourX, neighbourY, neighbourZ)
                                 : supportReader.getMissingNeighbourSupportValue(neighbourX, neighbourY, neighbourZ);
                              if (supportDistance == 15) {
                                 lowestSupportDistance = 1;
                              } else if (supportDistance < lowestSupportDistance) {
                                 lowestSupportDistance = supportDistance;
                              }
                           }

                           switch (requiredBlockFaceSupport.getSupport()) {
                              case IGNORED:
                              default:
                                 break;
                              case REQUIRED:
                                 if (doesSatisfyRequirements) {
                                    doesSatisfySupport = true;
                                 }

                                 hasTestedForSupport = true;
                                 break;
                              case DISALLOWED:
                                 if (doesSatisfyRequirements) {
                                    failedSatisfySupport = true;
                                 }

                                 hasTestedForSupport = true;
                           }
                        }
                     }

                     if (!failedSatisfySupport && doesSatisfySupport) {
                        return -2;
                     }
                  }
               }
            }
         }

         if (!hasTestedForSupport) {
            return -1;
         }

         if (lowestSupportDistance < Integer.MAX_VALUE && lowestSupportDistance >= 0) {
            int supportDistance = lowestSupportDistance + 1;
            if (requiredSupportDistance >= supportDistance) {
               return supportDistance;
            }
         }

         return 0;
      } else {
         return -1;
      }
   }

   public static boolean doesSatisfyRequirements(
      @Nonnull BlockType blockType,
      Vector3i blockFillerOffset,
      Vector3i neighbourFillerOffset,
      BlockFace blockFace,
      BlockFace neighbourBlockFace,
      int neighbourBlockId,
      @Nonnull BlockType neighbourBlockType,
      int neighbourRotation,
      int neighbourFluidId,
      @Nonnull Fluid neighbourFluid,
      @Nonnull RequiredBlockFaceSupport requiredBlockFaceSupport
   ) {
      String neighbourBlockTypeKey = neighbourBlockType.getId();
      boolean hasSupport = true;
      int blockSetId = requiredBlockFaceSupport.getBlockSetIndex();
      if (blockSetId >= 0 && !BlockSetModule.getInstance().blockInSet(blockSetId, neighbourBlockId)) {
         hasSupport = false;
      }

      String requiredBlockTypeId = requiredBlockFaceSupport.getBlockTypeId();
      if (hasSupport && requiredBlockTypeId != null && !requiredBlockTypeId.equals(neighbourBlockTypeKey)) {
         hasSupport = false;
      }

      String fluidId = requiredBlockFaceSupport.getFluidId();
      if (hasSupport
         && fluidId != null
         && (neighbourBlockType.getMaterial() != BlockMaterial.Empty || neighbourFluidId == 0 || !fluidId.equals(neighbourFluid.getId()))) {
         hasSupport = false;
      }

      int tagIndex = requiredBlockFaceSupport.getTagIndex();
      if (tagIndex >= 0 && !BlockType.getAssetMap().getKeysForTag(tagIndex).contains(neighbourBlockTypeKey)) {
         hasSupport = false;
      }

      if (hasSupport && requiredBlockFaceSupport.getFaceType() != null) {
         hasSupport = doesMatchFaceType(
            neighbourFillerOffset, requiredBlockFaceSupport.getFaceType(), neighbourBlockFace, neighbourBlockType.getSupporting(neighbourRotation)
         );
      }

      if (hasSupport && requiredBlockFaceSupport.getSelfFaceType() != null) {
         hasSupport = doesMatchFaceType(blockFillerOffset, requiredBlockFaceSupport.getSelfFaceType(), blockFace, blockType.getSupporting(neighbourRotation));
      }
      return switch (requiredBlockFaceSupport.getMatchSelf()) {
         case REQUIRED -> {
            if (hasSupport) {
               yield blockType.getId().equals(neighbourBlockTypeKey);
            }
         }
         case DISALLOWED -> {
            if (hasSupport) {
               yield !blockType.getId().equals(neighbourBlockTypeKey);
            }
         }
      };
   }

   public static boolean doesMatchFaceType(
      Vector3i fillerOffset, @Nonnull String faceType, BlockFace blockFace, @Nonnull Map<BlockFace, BlockFaceSupport[]> supporting
   ) {
      boolean faceHasSupport = false;
      BlockFaceSupport[] blockFaceSupports = supporting.get(blockFace);
      if (blockFaceSupports != null) {
         for (BlockFaceSupport blockFaceSupport : blockFaceSupports) {
            if (blockFaceSupport.providesSupportFromFiller(fillerOffset) && faceType.equals(blockFaceSupport.getFaceType())) {
               faceHasSupport = true;
               break;
            }
         }
      }

      return faceHasSupport;
   }

   public enum Result {
      INVALID,
      VALID,
      WAITING_CHUNK;
   }

   public record SupportBlock(int blockId, int rotation, int filler, int fluidId, @Nonnull Fluid fluid) {
   }

   public interface SupportReader {
      boolean isPositionAvailable(int var1, int var2, int var3);

      @Nullable
      BlockPhysicsUtil.SupportBlock getBlock(int var1, int var2, int var3);

      int getSupportValue(int var1, int var2, int var3);

      default int getMissingNeighbourSupportValue(int x, int y, int z) {
         return 0;
      }

      default boolean doesMissingNeighbourSatisfySupport(@Nonnull Vector3ic neighbourDirection) {
         return false;
      }

      default int getProtrudingFillerRotation(int x, int y, int z, int fallbackRotation) {
         return fallbackRotation;
      }
   }

   private static final class WorldSupportReader implements BlockPhysicsUtil.SupportReader {
      @Nonnull
      private final BlockPhysicsSystems.CachedAccessor chunkAccessor;
      private final BlockSection blockSection;
      @Nullable
      private final BlockPhysics blockPhysics;
      @Nonnull
      private final FluidSection fluidSection;
      private final int originX;
      private final int originY;
      private final int originZ;

      private WorldSupportReader(
         @Nonnull BlockPhysicsSystems.CachedAccessor chunkAccessor,
         BlockSection blockSection,
         @Nullable BlockPhysics blockPhysics,
         @Nonnull FluidSection fluidSection,
         int originX,
         int originY,
         int originZ
      ) {
         this.chunkAccessor = chunkAccessor;
         this.blockSection = blockSection;
         this.blockPhysics = blockPhysics;
         this.fluidSection = fluidSection;
         this.originX = originX;
         this.originY = originY;
         this.originZ = originZ;
      }

      @Override
      public boolean isPositionAvailable(int x, int y, int z) {
         return this.getBlockSection(x, y, z) != null && this.getFluidSection(x, y, z) != null;
      }

      @Nullable
      @Override
      public BlockPhysicsUtil.SupportBlock getBlock(int x, int y, int z) {
         BlockSection section = this.getBlockSection(x, y, z);
         FluidSection fluids = this.getFluidSection(x, y, z);
         if (section != null && fluids != null) {
            int fluidId = fluids.getFluidId(x, y, z);
            Fluid fluid = Fluid.getAssetMap().getAsset(fluidId);
            return fluid == null
               ? null
               : new BlockPhysicsUtil.SupportBlock(section.get(x, y, z), section.getRotationIndex(x, y, z), section.getFiller(x, y, z), fluidId, fluid);
         } else {
            return null;
         }
      }

      @Override
      public int getSupportValue(int x, int y, int z) {
         BlockPhysics physics = this.getBlockPhysics(x, y, z);
         return physics != null ? physics.get(x, y, z) : 0;
      }

      @Override
      public int getProtrudingFillerRotation(int x, int y, int z, int fallbackRotation) {
         BlockSection section = this.getBlockSection(x, y, z);
         return section != null ? section.getRotationIndex(x, y, z) : fallbackRotation;
      }

      @Nullable
      private BlockSection getBlockSection(int x, int y, int z) {
         return ChunkUtil.isSameChunkSection(this.originX, this.originY, this.originZ, x, y, z)
            ? this.blockSection
            : this.chunkAccessor.getBlockSection(ChunkUtil.chunkCoordinate(x), ChunkUtil.chunkCoordinate(y), ChunkUtil.chunkCoordinate(z));
      }

      @Nullable
      private FluidSection getFluidSection(int x, int y, int z) {
         return ChunkUtil.isSameChunkSection(this.originX, this.originY, this.originZ, x, y, z)
            ? this.fluidSection
            : this.chunkAccessor.getFluidSection(ChunkUtil.chunkCoordinate(x), ChunkUtil.chunkCoordinate(y), ChunkUtil.chunkCoordinate(z));
      }

      @Nullable
      private BlockPhysics getBlockPhysics(int x, int y, int z) {
         return ChunkUtil.isSameChunkSection(this.originX, this.originY, this.originZ, x, y, z)
            ? this.blockPhysics
            : this.chunkAccessor.getBlockPhysics(ChunkUtil.chunkCoordinate(x), ChunkUtil.chunkCoordinate(y), ChunkUtil.chunkCoordinate(z));
      }
   }
}
