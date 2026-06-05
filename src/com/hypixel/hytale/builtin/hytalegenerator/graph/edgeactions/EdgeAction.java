package com.hypixel.hytale.builtin.hytalegenerator.graph.edgeactions;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public abstract class EdgeAction {
   public abstract void run(@Nonnull GraphSpace var1, @Nonnull GraphSpace.Edge var2, @Nonnull Bounds3d var3);

   public abstract double getReadRange(double var1);

   public abstract double getConnectionRangeIncrement();

   public abstract void viewAllPossibleContent(@Nonnull Consumer<GraphSpace.Content> var1);
}
