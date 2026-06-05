package com.hypixel.hytale.builtin.hytalegenerator.propdistributions;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.pipe.Pipe;
import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;

public abstract class PropDistribution {
   public abstract void distribute(@Nonnull PropDistribution.Context var1);

   public abstract void forEachPossibleProp(@Nonnull Consumer<Prop> var1);

   public static class Context {
      @Nonnull
      public Bounds3d bounds;
      @Nonnull
      public Pipe.Two<Vector3d, Prop> pipe;
      public double distanceFromBiomeEdge;
      @Nullable
      public GraphSpace.Node graphNode;
      @Nullable
      public Vector3d anchor;

      public Context() {
         this.bounds = new Bounds3d();
         this.pipe = (position, prop, control) -> {};
         this.distanceFromBiomeEdge = Double.MAX_VALUE;
         this.graphNode = null;
         this.anchor = null;
      }

      public Context(@Nonnull PropDistribution.Context other) {
         this.bounds = other.bounds;
         this.pipe = other.pipe;
         this.distanceFromBiomeEdge = other.distanceFromBiomeEdge;
         this.graphNode = other.graphNode;
         this.anchor = other.anchor;
      }

      public Context(
         @Nonnull Bounds3d bounds,
         @Nonnull Pipe.Two<Vector3d, Prop> pipe,
         double distanceFromBiomeEdge,
         @Nonnull GraphSpace.Node graphNode,
         @Nullable Vector3d anchor
      ) {
         this.bounds = bounds;
         this.pipe = pipe;
         this.distanceFromBiomeEdge = distanceFromBiomeEdge;
         this.graphNode = graphNode;
         this.anchor = anchor;
      }

      public void assign(@Nonnull PropDistribution.Context other) {
         this.bounds = other.bounds;
         this.pipe = other.pipe;
         this.distanceFromBiomeEdge = other.distanceFromBiomeEdge;
         this.graphNode = other.graphNode;
         this.anchor = other.anchor;
      }
   }
}
