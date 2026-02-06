package com.hypixel.hytale.builtin.hytalegenerator.patterns;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.SpaceSize;
import com.hypixel.hytale.math.vector.Vector3i;
import javax.annotation.Nonnull;

public class CeilingPattern extends Pattern {
   @Nonnull
   private final Pattern ceilingPattern;
   @Nonnull
   private final Pattern airPattern;
   @Nonnull
   private final SpaceSize readSpaceSize;
   @Nonnull
   private final Vector3i rCeilingPosition;
   @Nonnull
   private final Pattern.Context rCeilingContext;

   public CeilingPattern(@Nonnull Pattern ceilingPattern, @Nonnull Pattern airPattern) {
      this.ceilingPattern = ceilingPattern;
      this.airPattern = airPattern;
      SpaceSize ceilingSpace = ceilingPattern.readSpace();
      ceilingSpace.moveBy(new Vector3i(0, 1, 0));
      this.readSpaceSize = SpaceSize.merge(ceilingSpace, airPattern.readSpace());
      this.rCeilingPosition = new Vector3i();
      this.rCeilingContext = new Pattern.Context();
   }

   @Override
   public boolean matches(@Nonnull Pattern.Context context) {
      this.rCeilingPosition.assign(context.position);
      if (context.materialSpace.isInsideSpace(context.position) && context.materialSpace.isInsideSpace(this.rCeilingPosition)) {
         this.rCeilingContext.assign(context);
         this.rCeilingContext.position = this.rCeilingPosition;
         return this.airPattern.matches(context) && this.ceilingPattern.matches(this.rCeilingContext);
      } else {
         return false;
      }
   }

   @Nonnull
   @Override
   public SpaceSize readSpace() {
      return this.readSpaceSize.clone();
   }
}
