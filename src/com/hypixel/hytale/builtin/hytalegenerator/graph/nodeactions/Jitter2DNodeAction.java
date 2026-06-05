package com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.rng.RngField;
import com.hypixel.hytale.math.util.FastRandom;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class Jitter2DNodeAction extends NodeAction {
   private final double magnitude;
   @Nonnull
   private final RngField rngField;
   @Nonnull
   private final FastRandom random;
   @Nonnull
   private final Vector3d rNewPosition;

   public Jitter2DNodeAction(double magnitude, int seed) {
      assert magnitude >= 0.0;
      this.magnitude = magnitude;
      this.rngField = new RngField(seed);
      this.random = new FastRandom();
      this.rNewPosition = new Vector3d();
   }

   @Override
   public void run(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Node node, @NonNullDecl Bounds3d bounds) {
      Vector3dc nodePosition = node.position();
      int localSeed = this.rngField.get(nodePosition);
      this.random.setSeed(localSeed);
      double radius = this.magnitude * Math.sqrt(this.random.nextDouble());
      double theta = this.random.nextDouble() * 2.0 * Math.PI;
      this.rNewPosition.set(radius * Math.cos(theta), 0.0, radius * Math.sin(theta));
      this.rNewPosition.add(nodePosition);
      graphSpace.moveNode(node, this.rNewPosition);
   }

   @Override
   public double getReadRange(double longestConnection) {
      return 0.0;
   }

   @Override
   public double getCausalRangeIncrement() {
      return this.magnitude;
   }

   @Override
   public void viewAllPossibleContent(@NonNullDecl Consumer<GraphSpace.Content> consumer) {
   }
}
