package com.hypixel.hytale.builtin.hytalegenerator.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.rng.RngField;
import com.hypixel.hytale.math.util.FastRandom;
import javax.annotation.Nonnull;

public class FieldFunctionOccurrencePositionProvider extends PositionProvider {
   public static final double FP_RESOLUTION = 100.0;
   @Nonnull
   private final Density field;
   @Nonnull
   private final PositionProvider positionProvider;
   @Nonnull
   private final RngField rngField;

   public FieldFunctionOccurrencePositionProvider(@Nonnull Density field, @Nonnull PositionProvider positionProvider, int seed) {
      this.field = field;
      this.positionProvider = positionProvider;
      this.rngField = new RngField(seed);
   }

   @Override
   public void generate(@Nonnull PositionProvider.Context context) {
      PositionProvider.Context childContext = new PositionProvider.Context(context);
      childContext.pipe = (position, control) -> {
         Density.Context densityContext = new Density.Context();
         densityContext.position = position;
         densityContext.positionsAnchor = context.anchor;
         densityContext.densityAnchor = context.anchor;
         double discardChance = 1.0 - this.field.process(densityContext);
         FastRandom random = new FastRandom(this.rngField.get(position.x, position.y, position.z));
         if (!(discardChance > random.nextDouble())) {
            context.pipe.accept(position, control);
         }
      };
      this.positionProvider.generate(childContext);
   }
}
