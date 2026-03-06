package com.hypixel.hytale.builtin.hytalegenerator.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import javax.annotation.Nonnull;

public class BoundPositionProvider extends PositionProvider {
   @Nonnull
   private final PositionProvider positionProvider;
   private final Bounds3d bounds;

   public BoundPositionProvider(@Nonnull PositionProvider positionProvider, @Nonnull Bounds3d bounds) {
      this.positionProvider = positionProvider;
      this.bounds = bounds;
   }

   @Override
   public void generate(@Nonnull PositionProvider.Context context) {
      PositionProvider.Context childContext = new PositionProvider.Context(context);
      childContext.bounds.assign(this.bounds);
      this.positionProvider.generate(childContext);
   }
}
