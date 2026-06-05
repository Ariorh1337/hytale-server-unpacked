package com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class DeleteNodeAction extends NodeAction {
   @Override
   public void run(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Node node, @NonNullDecl Bounds3d bounds) {
      graphSpace.scheduleNodeDeletion(node);
   }

   @Override
   public double getReadRange(double longestConnection) {
      return 0.0;
   }

   @Override
   public double getCausalRangeIncrement() {
      return 0.0;
   }

   @Override
   public void viewAllPossibleContent(@NonNullDecl Consumer<GraphSpace.Content> consumer) {
   }
}
