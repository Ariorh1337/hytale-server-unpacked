package com.hypixel.hytale.builtin.hytalegenerator.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public class AnchorPositionProvider extends PositionProvider {
   @Nonnull
   private final PositionProvider positionProvider;
   private final boolean isReversed;

   public AnchorPositionProvider(@Nonnull PositionProvider positionProvider, boolean isReversed) {
      this.positionProvider = positionProvider;
      this.isReversed = isReversed;
   }

   @Override
   public void generate(@Nonnull PositionProvider.Context context) {
      if (context != null) {
         Vector3d anchor = context.anchor;
         if (anchor != null) {
            Bounds3d offsetBounds;
            if (this.isReversed) {
               offsetBounds = context.bounds.clone().offset(anchor);
            } else {
               offsetBounds = context.bounds.clone().offset(anchor.clone().scale(-1.0));
            }

            PositionProvider.Context childContext = new PositionProvider.Context(offsetBounds, (position, control) -> {
               Vector3d newPoint = position.clone();
               if (this.isReversed) {
                  newPoint.addScaled(anchor, -1.0);
               } else {
                  newPoint.add(anchor);
               }

               if (context.bounds.contains(newPoint)) {
                  context.pipe.accept(newPoint, control);
               }
            }, context.anchor);
            this.positionProvider.generate(childContext);
         }
      }
   }
}
