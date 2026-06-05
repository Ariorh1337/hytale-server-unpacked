package com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.VectorProvider;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;

public class MoveNodeAction extends NodeAction {
   @Nonnull
   private final VectorProvider vectorProvider;
   private final double maxDistanceExclusiveSqr;

   public MoveNodeAction(@Nonnull VectorProvider vectorProvider, double maxDistanceExclusive) {
      assert maxDistanceExclusive >= 0.0;
      this.vectorProvider = vectorProvider;
      this.maxDistanceExclusiveSqr = maxDistanceExclusive * maxDistanceExclusive;
   }

   @Override
   public void run(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Node node, @NonNullDecl Bounds3d bounds) {
      Vector3d movementVec = new Vector3d();
      this.vectorProvider.process(new VectorProvider.Context(new Vector3d(node.position()), null, node), movementVec);
      if (!(movementVec.lengthSquared() >= this.maxDistanceExclusiveSqr)) {
         Vector3d newPosition = new Vector3d(node.position());
         newPosition.add(movementVec);
         graphSpace.scheduleMoveNode(node, newPosition);
      }
   }

   @Override
   public double getReadRange(double longestConnection) {
      return 0.0;
   }

   @Override
   public double getCausalRangeIncrement() {
      return this.maxDistanceExclusiveSqr;
   }

   @Override
   public void viewAllPossibleContent(@NonNullDecl Consumer<GraphSpace.Content> consumer) {
   }
}
