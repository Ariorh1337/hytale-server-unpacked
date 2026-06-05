package com.hypixel.hytale.builtin.hytalegenerator.graph.passes;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public abstract class GraphPass {
   @Nonnull
   protected final String label;

   protected GraphPass(@Nonnull String label) {
      this.label = label;
   }

   public abstract void run(@Nonnull GraphSpace var1, @Nonnull Bounds3d var2);

   @Nonnull
   public abstract Bounds3d getReadBounds(double var1);

   public abstract double getConnectionRangeIncrement();

   public abstract void viewAllPossibleContent(@Nonnull Consumer<GraphSpace.Content> var1);

   @Nonnull
   public String getLabel() {
      return this.label;
   }
}
