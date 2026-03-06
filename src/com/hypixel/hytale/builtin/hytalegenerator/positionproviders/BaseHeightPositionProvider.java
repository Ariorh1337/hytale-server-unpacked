package com.hypixel.hytale.builtin.hytalegenerator.positionproviders;

import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public class BaseHeightPositionProvider extends PositionProvider {
   @Nonnull
   private final double baseHeight;
   private final double maxYInput;
   private final double minYInput;
   @Nonnull
   private final PositionProvider positionProvider;

   public BaseHeightPositionProvider(double baseHeight, @Nonnull PositionProvider positionProvider, double minYInput, double maxYInput) {
      maxYInput = Math.max(minYInput, maxYInput);
      this.baseHeight = baseHeight;
      this.positionProvider = positionProvider;
      this.maxYInput = maxYInput;
      this.minYInput = minYInput;
   }

   @Override
   public void generate(@Nonnull PositionProvider.Context context) {
      PositionProvider.Context childContext = new PositionProvider.Context(context);
      childContext.pipe = (position, control) -> {
         Vector3d offsetP = position.clone();
         offsetP.y = offsetP.y + this.baseHeight;
         if (context.bounds.contains(offsetP)) {
            context.pipe.accept(offsetP, control);
         }
      };
      this.positionProvider.generate(childContext);
   }
}
