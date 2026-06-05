package com.hypixel.hytale.builtin.hytalegenerator.graph.passes;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.graph.GraphSpace;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3d;

public class PositionsGraphPass extends GraphPass {
   @Nonnull
   private final PositionProvider positionProvider;

   public PositionsGraphPass(@Nonnull PositionProvider positionProvider, @Nonnull String label) {
      super(label);
      this.positionProvider = positionProvider;
   }

   @Override
   public void run(@NonNullDecl GraphSpace graphSpace, @NonNullDecl Bounds3d runBounds) {
      PositionProvider.Context context = new PositionProvider.Context(runBounds, (position, control) -> {
         assert runBounds.contains(position);
         graphSpace.scheduleNodeCreation(new Vector3d(position));
      }, null, null);
      this.positionProvider.generate(context);
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
