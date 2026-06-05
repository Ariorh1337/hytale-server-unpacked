package com.hypixel.hytale.builtin.hytalegenerator.graph.passes;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class EmptyGraphPass extends GraphPass {
   public static final EmptyGraphPass INSTANCE = new EmptyGraphPass();

   private EmptyGraphPass() {
      super("Empty");
   }

   @Override
   public void run(@NonNullDecl GraphSpace graphSpace, @NonNullDecl Bounds3d runBounds) {
   }

   @NonNullDecl
   @Override
   public Bounds3d getReadBounds(double longestConnection) {
      return Bounds3d.ZERO;
   }

   @Override
   public double getConnectionRangeIncrement() {
      return 0.0;
   }

   @Override
   public void viewAllPossibleContent(@NonNullDecl Consumer<GraphSpace.Content> consumer) {
   }
}
