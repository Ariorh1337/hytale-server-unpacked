package com.hypixel.hytale.builtin.hytalegenerator.vectorproviders;

import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class SubtracterVectorProvider extends VectorProvider {
   @Nonnull
   private final VectorProvider minuend;
   @Nonnull
   private final VectorProvider subtrahend;
   @Nonnull
   private final Vector3d rVectorA;
   @Nonnull
   private final Vector3d rVectorB;

   public SubtracterVectorProvider(@Nonnull VectorProvider minuend, @Nonnull VectorProvider subtrahend) {
      this.minuend = minuend;
      this.subtrahend = subtrahend;
      this.rVectorA = new Vector3d();
      this.rVectorB = new Vector3d();
   }

   @Override
   public void process(@Nonnull VectorProvider.Context context, @Nonnull Vector3d vector_out) {
      this.minuend.process(context, this.rVectorA);
      this.subtrahend.process(context, this.rVectorB);
      vector_out.set(this.rVectorA).sub(this.rVectorB);
   }
}
