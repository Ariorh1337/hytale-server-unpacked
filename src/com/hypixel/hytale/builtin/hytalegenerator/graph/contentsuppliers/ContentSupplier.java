package com.hypixel.hytale.builtin.hytalegenerator.graph.contentsuppliers;

import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public abstract class ContentSupplier {
   @Nonnull
   public abstract GraphSpace.Content get(@Nonnull GraphSpace.Node var1);

   public abstract void viewAllPossibleContent(@Nonnull Consumer<GraphSpace.Content> var1);
}
