package com.hypixel.hytale.builtin.hytalegenerator.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public class OffsetPositionProvider extends PositionProvider {
   @Nonnull
   private final Vector3d vector;
   @Nonnull
   private final PositionProvider positionProvider;
   @Nonnull
   private final Bounds3d rBounds;
   @Nonnull
   private final PositionProvider.Context rChildContext;

   public OffsetPositionProvider(@Nonnull Vector3d vector, @Nonnull PositionProvider positionProvider) {
      this.vector = vector.clone();
      this.positionProvider = positionProvider;
      this.rBounds = new Bounds3d();
      this.rChildContext = new PositionProvider.Context();
   }

   @Override
   public void generate(@Nonnull PositionProvider.Context context) {
      this.rBounds.assign(context.bounds);
      this.rBounds.offsetOpposite(this.vector);
      this.rChildContext.assign(context);
      this.rChildContext.bounds = this.rBounds;
      this.rChildContext.pipe = (position, control) -> {
         position.add(this.vector);
         context.pipe.accept(position, control);
      };
      this.positionProvider.generate(this.rChildContext);
   }
}
