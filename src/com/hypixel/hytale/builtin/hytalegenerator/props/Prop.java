package com.hypixel.hytale.builtin.hytalegenerator.props;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.engine.entityfunnel.EntityFunnel;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.voxelspace.NullSpace;
import com.hypixel.hytale.builtin.hytalegenerator.voxelspace.VoxelSpace;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3i;

public abstract class Prop {
   public abstract boolean generate(@Nonnull Prop.Context var1);

   @Nonnull
   public abstract Bounds3i getReadBounds_voxelGrid();

   @Nonnull
   public abstract Bounds3i getWriteBounds_voxelGrid();

   public static class Context {
      @Nonnull
      public Vector3i position;
      @Nonnull
      public VoxelSpace<Material> materialReadSpace;
      @Nonnull
      public VoxelSpace<Material> materialWriteSpace;
      @Nonnull
      public EntityFunnel entityWriteBuffer;
      public double distanceToBiomeEdge;
      @Nullable
      public Vector3d anchor;
      @Nullable
      public GraphSpace.Node graphNode;

      public Context() {
         this.position = new Vector3i();
         this.materialReadSpace = NullSpace.instance();
         this.materialWriteSpace = NullSpace.instance();
         this.entityWriteBuffer = EntityFunnel.NULL;
         this.distanceToBiomeEdge = 0.0;
         this.anchor = null;
         this.graphNode = null;
      }

      public Context(
         @Nonnull Vector3i position,
         @Nonnull VoxelSpace<Material> materialReadSpace,
         @Nonnull VoxelSpace<Material> materialWriteSpace,
         @Nonnull EntityFunnel entityWriteBuffer,
         double distanceToBiomeEdge,
         @Nullable Vector3d anchor,
         @Nullable GraphSpace.Node graphNode
      ) {
         this.position = position;
         this.materialReadSpace = materialReadSpace;
         this.materialWriteSpace = materialWriteSpace;
         this.entityWriteBuffer = entityWriteBuffer;
         this.distanceToBiomeEdge = distanceToBiomeEdge;
         this.anchor = anchor;
         this.graphNode = graphNode;
      }

      public Context(@Nonnull Prop.Context other) {
         this.position = other.position;
         this.materialReadSpace = other.materialReadSpace;
         this.materialWriteSpace = other.materialWriteSpace;
         this.entityWriteBuffer = other.entityWriteBuffer;
         this.distanceToBiomeEdge = other.distanceToBiomeEdge;
         this.anchor = other.anchor;
         this.graphNode = other.graphNode;
      }

      public void assign(@Nonnull Prop.Context other) {
         this.position = other.position;
         this.materialReadSpace = other.materialReadSpace;
         this.materialWriteSpace = other.materialWriteSpace;
         this.entityWriteBuffer = other.entityWriteBuffer;
         this.distanceToBiomeEdge = other.distanceToBiomeEdge;
         this.anchor = other.anchor;
         this.graphNode = other.graphNode;
      }
   }
}
