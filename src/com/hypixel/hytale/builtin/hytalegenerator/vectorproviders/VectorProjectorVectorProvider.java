package com.hypixel.hytale.builtin.hytalegenerator.vectorproviders;

import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class VectorProjectorVectorProvider extends VectorProvider {
   @Nonnull
   private final VectorProvider source;
   @Nonnull
   private final VectorProvider target;
   @Nonnull
   private final Vector3d rSource;
   @Nonnull
   private final Vector3d rTarget;

   public VectorProjectorVectorProvider(@Nonnull VectorProvider source, @Nonnull VectorProvider target) {
      this.source = source;
      this.target = target;
      this.rSource = new Vector3d();
      this.rTarget = new Vector3d();
   }

   @Override
   public void process(@Nonnull VectorProvider.Context context, @Nonnull Vector3d vector_out) {
      this.source.process(context, this.rSource);
      this.target.process(context, this.rTarget);
      double sqrMag = this.rTarget.lengthSquared();
      if (sqrMag == 0.0) {
         vector_out.set(0.0, 0.0, 0.0);
      } else {
         double scale = this.rSource.dot(this.rTarget) / sqrMag;
         vector_out.set(this.rTarget).mul(scale);
      }
   }
}
