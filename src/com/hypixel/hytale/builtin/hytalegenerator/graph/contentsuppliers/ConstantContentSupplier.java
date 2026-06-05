package com.hypixel.hytale.builtin.hytalegenerator.graph.contentsuppliers;

import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ConstantContentSupplier extends ContentSupplier {
   public static final ConstantContentSupplier INSTANCE = new ConstantContentSupplier(GraphSpace.Content.DEFAULT);
   @Nonnull
   private final GraphSpace.Content content;

   public ConstantContentSupplier(@Nonnull GraphSpace.Content content) {
      this.content = content;
   }

   @NonNullDecl
   @Override
   public GraphSpace.Content get(@NonNullDecl GraphSpace.Node node) {
      return this.content;
   }

   @Override
   public void viewAllPossibleContent(@NonNullDecl Consumer<GraphSpace.Content> consumer) {
      consumer.accept(this.content);
   }
}
