package com.hypixel.hytale.builtin.hytalegenerator.props;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.voxelspace.ArrayVoxelSpace;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class DensityProp extends Prop {
   @Nonnull
   private final Density density;
   @Nonnull
   private final MaterialProvider<Material> materialProvider;
   @Nonnull
   private final Bounds3i writeBounds;
   @Nonnull
   private final Bounds3i rIntersectingWriteBounds;
   @Nonnull
   private final ArrayVoxelSpace<Boolean> rSolidityBuffer;
   @Nonnull
   private final Density.Context rDensityContext;
   @Nonnull
   private final MaterialProvider.Context rMaterialProviderContext;
   @Nonnull
   private final Vector3i rPosition;

   public DensityProp(@Nonnull Density density, @Nonnull MaterialProvider<Material> materialProvider, @Nonnull Bounds3i bounds) {
      this.density = density;
      this.materialProvider = materialProvider;
      this.writeBounds = bounds.clone();
      Bounds3i densityBufferBounds = bounds.clone();
      densityBufferBounds.min.y--;
      densityBufferBounds.max.y++;
      this.rSolidityBuffer = new ArrayVoxelSpace<>(densityBufferBounds);
      this.rIntersectingWriteBounds = new Bounds3i();
      this.rDensityContext = new Density.Context();
      this.rDensityContext.densityAnchor = new Vector3d();
      this.rMaterialProviderContext = new MaterialProvider.Context();
      this.rPosition = new Vector3i();
   }

   @Override
   public boolean generate(@NonNullDecl Prop.Context context) {
      Bounds3i writeSpaceBounds = context.materialWriteSpace.getBounds();
      this.rIntersectingWriteBounds.assign(this.writeBounds);
      this.rIntersectingWriteBounds.offset(context.position);
      this.rIntersectingWriteBounds.min.x = Math.max(this.rIntersectingWriteBounds.min.x, writeSpaceBounds.min.x);
      this.rIntersectingWriteBounds.min.z = Math.max(this.rIntersectingWriteBounds.min.z, writeSpaceBounds.min.z);
      this.rIntersectingWriteBounds.max.x = Math.min(this.rIntersectingWriteBounds.max.x, writeSpaceBounds.max.x);
      this.rIntersectingWriteBounds.max.z = Math.min(this.rIntersectingWriteBounds.max.z, writeSpaceBounds.max.z);
      this.rIntersectingWriteBounds.min.y--;
      this.rIntersectingWriteBounds.max.y++;
      this.rDensityContext.densityAnchor.assign(context.position);
      this.rSolidityBuffer.offset(context.position);

      for (this.rPosition.x = this.rIntersectingWriteBounds.min.x; this.rPosition.x < this.rIntersectingWriteBounds.max.x; this.rPosition.x++) {
         for (this.rPosition.y = this.rIntersectingWriteBounds.min.y; this.rPosition.y < this.rIntersectingWriteBounds.max.y; this.rPosition.y++) {
            for (this.rPosition.z = this.rIntersectingWriteBounds.min.z; this.rPosition.z < this.rIntersectingWriteBounds.max.z; this.rPosition.z++) {
               this.rDensityContext.position.assign(this.rPosition);
               double densityValue = this.density.process(this.rDensityContext);
               this.rSolidityBuffer.set(densityValue > 0.0 ? Boolean.TRUE : Boolean.FALSE, this.rPosition);
            }
         }
      }

      int height = this.rIntersectingWriteBounds.max.y - this.rIntersectingWriteBounds.min.y;

      for (this.rPosition.x = this.rIntersectingWriteBounds.min.x; this.rPosition.x < this.rIntersectingWriteBounds.max.x; this.rPosition.x++) {
         for (this.rPosition.z = this.rIntersectingWriteBounds.min.z; this.rPosition.z < this.rIntersectingWriteBounds.max.z; this.rPosition.z++) {
            int[] depthIntoCeiling = new int[height + 1];
            int[] depthIntoFloor = new int[height + 1];
            int[] spaceBelowCeiling = new int[height + 1];
            int[] spaceAboveFloor = new int[height + 1];

            for (this.rPosition.y = this.rIntersectingWriteBounds.max.y - 2; this.rPosition.y > this.rIntersectingWriteBounds.min.y; this.rPosition.y--) {
               int i = this.rPosition.y - this.rIntersectingWriteBounds.min.y;
               boolean solidity = this.rSolidityBuffer.get(this.rPosition.x, this.rPosition.y, this.rPosition.z);
               if (this.rPosition.y == this.rIntersectingWriteBounds.max.y - 1) {
                  if (solidity) {
                     depthIntoFloor[i] = 1;
                  } else {
                     depthIntoFloor[i] = 0;
                  }

                  spaceAboveFloor[i] = 1073741823;
               } else if (solidity) {
                  depthIntoFloor[i] = depthIntoFloor[i + 1] + 1;
                  spaceAboveFloor[i] = spaceAboveFloor[i + 1];
               } else {
                  depthIntoFloor[i] = 0;
                  if (this.rSolidityBuffer.get(this.rPosition.x, this.rPosition.y + 1, this.rPosition.z)) {
                     spaceAboveFloor[i] = 0;
                  } else {
                     spaceAboveFloor[i] = spaceAboveFloor[i + 1] + 1;
                  }
               }
            }

            for (this.rPosition.y = this.rIntersectingWriteBounds.min.y + 1; this.rPosition.y < this.rIntersectingWriteBounds.max.y - 1; this.rPosition.y++) {
               int i = this.rPosition.y - this.rIntersectingWriteBounds.min.y;
               boolean solidity = this.rSolidityBuffer.get(this.rPosition.x, this.rPosition.y, this.rPosition.z);
               if (this.rPosition.y == this.rIntersectingWriteBounds.min.x) {
                  if (solidity) {
                     depthIntoCeiling[i] = 1;
                  } else {
                     depthIntoCeiling[i] = 0;
                  }

                  spaceBelowCeiling[i] = Integer.MAX_VALUE;
               } else if (solidity) {
                  depthIntoCeiling[i] = depthIntoCeiling[i - 1] + 1;
                  spaceBelowCeiling[i] = spaceBelowCeiling[i - 1];
               } else {
                  depthIntoCeiling[i] = 0;
                  if (this.rSolidityBuffer.get(this.rPosition.x, this.rPosition.y - 1, this.rPosition.z)) {
                     spaceBelowCeiling[i] = 0;
                  } else {
                     spaceBelowCeiling[i] = spaceBelowCeiling[i - 1] + 1;
                  }
               }
            }

            for (this.rPosition.y = this.rIntersectingWriteBounds.max.y - 2; this.rPosition.y > this.rIntersectingWriteBounds.min.x; this.rPosition.y--) {
               if (this.rIntersectingWriteBounds.contains(this.rPosition)) {
                  int i = this.rPosition.y - this.rIntersectingWriteBounds.min.y;
                  this.rMaterialProviderContext.position.assign(this.rPosition);
                  this.rMaterialProviderContext.depthIntoFloor = depthIntoFloor[i];
                  this.rMaterialProviderContext.depthIntoCeiling = depthIntoCeiling[i];
                  this.rMaterialProviderContext.spaceAboveFloor = spaceAboveFloor[i];
                  this.rMaterialProviderContext.spaceBelowCeiling = spaceBelowCeiling[i];
                  this.rMaterialProviderContext.distanceToBiomeEdge = context.distanceToBiomeEdge;
                  Material material = this.materialProvider.getVoxelTypeAt(this.rMaterialProviderContext);
                  if (material != null) {
                     context.materialWriteSpace.set(material, this.rPosition);
                  }
               }
            }
         }
      }

      this.rSolidityBuffer.offsetOpposite(context.position);
      return true;
   }

   @NonNullDecl
   @Override
   public Bounds3i getReadBounds_voxelGrid() {
      return Bounds3i.ZERO;
   }

   @NonNullDecl
   @Override
   public Bounds3i getWriteBounds_voxelGrid() {
      return this.writeBounds;
   }
}
