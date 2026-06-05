package com.hypixel.hytale.builtin.hytalegenerator.graph.nodeactions;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.graph.contentsuppliers.ContentSupplier;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ContentNodeAction extends NodeAction {
   @Nonnull
   private final ContentSupplier contentSupplier;

   public ContentNodeAction(@Nonnull ContentSupplier contentSupplier) {
      this.contentSupplier = contentSupplier;
   }

   @Override
   public void run(@NonNullDecl GraphSpace graphSpace, @NonNullDecl GraphSpace.Node node, @NonNullDecl Bounds3d bounds) {
      node.setContent(this.contentSupplier.get(node));
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
      this.contentSupplier.viewAllPossibleContent(consumer);
   }
}
