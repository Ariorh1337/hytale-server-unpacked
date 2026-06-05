package com.hypixel.hytale.builtin.hytalegenerator.graph.edgeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import javax.annotation.Nonnull;

public abstract class EdgeSelector {
   public abstract boolean isSelected(@Nonnull GraphSpace var1, @Nonnull GraphSpace.Edge var2);

   public abstract double getReadRange(double var1);
}
