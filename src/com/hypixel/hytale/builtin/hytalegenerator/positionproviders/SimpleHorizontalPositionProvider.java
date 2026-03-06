package com.hypixel.hytale.builtin.hytalegenerator.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.delimiters.RangeDouble;
import javax.annotation.Nonnull;

public class SimpleHorizontalPositionProvider extends PositionProvider {
   @Nonnull
   private final RangeDouble rangeY;
   @Nonnull
   private final PositionProvider positionProvider;

   public SimpleHorizontalPositionProvider(@Nonnull RangeDouble rangeY, @Nonnull PositionProvider positionProvider) {
      this.rangeY = rangeY;
      this.positionProvider = positionProvider;
   }

   @Override
   public void generate(@Nonnull PositionProvider.Context context) {
      PositionProvider.Context childContext = new PositionProvider.Context(context);
      childContext.pipe = (positions, control) -> {
         if (this.rangeY.contains(positions.y)) {
            context.pipe.accept(positions, control);
         }
      };
      this.positionProvider.generate(childContext);
   }
}
