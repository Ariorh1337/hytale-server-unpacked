package com.hypixel.hytale.builtin.hytalegenerator.graph.passes;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions.NodeAction;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;

public class NodeActionGraphPass extends GraphPass {
   @Nonnull
   private final NodeAction nodeAction;
   @Nonnull
   private final List<GraphSpace.Node> rResultList;

   public NodeActionGraphPass(@Nonnull NodeAction nodeAction, @Nonnull String label) {
      super(label);
      this.nodeAction = nodeAction;
      this.rResultList = new ArrayList<>();
   }

   @Override
   public void run(@NonNullDecl GraphSpace graphSpace, @NonNullDecl Bounds3d runBounds) {
      this.rResultList.clear();
      graphSpace.viewNodes(runBounds, this.rResultList);

      for (GraphSpace.Node node : this.rResultList) {
         this.nodeAction.run(graphSpace, node, runBounds);
      }

      this.rResultList.clear();
   }

   @NonNullDecl
   @Override
   public Bounds3d getReadBounds(double longestConnection) {
      double readRange = this.nodeAction.getReadRange(longestConnection);
      return getBounds(readRange);
   }

   @Override
   public double getConnectionRangeIncrement() {
      return this.nodeAction.getCausalRangeIncrement();
   }

   @Override
   public void viewAllPossibleContent(@NonNullDecl Consumer<GraphSpace.Content> consumer) {
      this.nodeAction.viewAllPossibleContent(consumer);
   }

   private static double getRange(@Nonnull Bounds3d bounds) {
      return Math.min(
         Math.min(Math.abs(bounds.min.x), Math.min(Math.abs(bounds.min.y), Math.abs(bounds.min.z))),
         Math.min(Math.abs(bounds.max.x), Math.min(Math.abs(bounds.max.y), Math.abs(bounds.max.z)))
      );
   }

   @Nonnull
   private static Bounds3d getBounds(double range) {
      return new Bounds3d(new Vector3d(-range), new Vector3d(range));
   }
}
