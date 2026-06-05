package com.hypixel.hytale.builtin.hytalegenerator.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.pipe.Pipe;
import com.hypixel.hytale.builtin.hytalegenerator.propdistributions.PropDistribution;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;

public abstract class PositionProvider {
   public abstract void generate(@Nonnull PositionProvider.Context var1);

   public static class Context {
      public Bounds3d bounds;
      public Pipe.One<Vector3d> pipe;
      @Nullable
      public Vector3d anchor;
      @Nullable
      public GraphSpace.Node graphNode;

      public Context() {
         this.bounds = new Bounds3d();
         this.pipe = Pipe.getEmptyOne();
         this.anchor = null;
         this.graphNode = null;
      }

      public Context(@Nonnull Bounds3d bounds, @Nonnull Pipe.One<Vector3d> pipe, @Nullable Vector3d anchor, @Nullable GraphSpace.Node graphNode) {
         this.bounds = bounds;
         this.pipe = pipe;
         this.anchor = anchor;
         this.graphNode = graphNode;
      }

      public Context(@Nonnull PositionProvider.Context other) {
         this.bounds = other.bounds;
         this.pipe = other.pipe;
         this.anchor = other.anchor;
         this.graphNode = other.graphNode;
      }

      public void assign(@Nonnull PositionProvider.Context other) {
         this.bounds = other.bounds;
         this.pipe = other.pipe;
         this.anchor = other.anchor;
         this.graphNode = other.graphNode;
      }

      public void assign(@Nonnull PropDistribution.Context other) {
         this.bounds.assign(other.bounds);
         this.pipe = Pipe.getEmptyOne();
         this.anchor = other.anchor;
         this.graphNode = other.graphNode;
      }
   }
}
