package com.hypixel.hytale.builtin.hytalegenerator.graph.nodeselectors;

import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import javax.annotation.Nonnull;

public abstract class NodeSelector {
   public abstract boolean isSelected(@Nonnull GraphSpace var1, @Nonnull GraphSpace.Node var2);

   public abstract double getReadRange(double var1);
}
