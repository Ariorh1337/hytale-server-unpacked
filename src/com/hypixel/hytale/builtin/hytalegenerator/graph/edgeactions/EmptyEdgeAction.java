package com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class EmptyEdgeAction extends EdgeAction {
   public static final EdgeAction INSTANCE = new EmptyEdgeAction();

   @Override
   public void run(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Edge edge, @NonNullDecl Bounds3d bounds) {
   }

   @Override
   public double getReadRange(double longestConnection) {
      return 0.0;
   }

   @Override
   public double getConnectionRangeIncrement() {
      return 0.0;
   }

   @Override
   public void viewAllPossibleContent(@NonNullDecl Consumer<GraphSpace.Content> consumer) {
   }
}
