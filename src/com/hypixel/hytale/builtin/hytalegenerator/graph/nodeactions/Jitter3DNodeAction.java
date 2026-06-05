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

public class Jitter3DNodeAction extends NodeAction {
   private static final float PI = (float) Math.PI;
   private static final double SEED_GENERATOR_RESOLUTION = 10.0;
   private final double magnitude;
   @Nonnull
   private final RngField rngField;
   @Nonnull
   private final FastRandom random;
   @Nonnull
   private final Vector3d rNewPosition;

   public Jitter3DNodeAction(double magnitude, int seed) {
      assert magnitude >= 0.0;
      this.magnitude = magnitude;
      this.rngField = new RngField(seed);
      this.random = new FastRandom();
      this.rNewPosition = new Vector3d();
   }

   @Override
   public void run(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Node node, @NonNullDecl Bounds3d bounds) {
      Vector3dc nodePosition = node.position();
      this.random.setSeed(this.rngField.get(nodePosition));
      double radius = this.magnitude * Math.sqrt(this.random.nextDouble());
      float rotationX = this.random.nextFloat() * 2.0F * (float) Math.PI;
      float rotationY = this.random.nextFloat() * 2.0F * (float) Math.PI;
      float rotationZ = this.random.nextFloat() * 2.0F * (float) Math.PI;
      this.rNewPosition.set(radius, 0.0, 0.0);
      this.rNewPosition.rotateX(rotationX);
      this.rNewPosition.rotateY(rotationY);
      this.rNewPosition.rotateZ(rotationZ);
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
