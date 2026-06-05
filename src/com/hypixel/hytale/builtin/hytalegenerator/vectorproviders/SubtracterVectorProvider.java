package com.hypixel.hytale.builtin.hytalegenerator.vectorproviders;

import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class SubtracterVectorProvider extends VectorProvider {
   @Nonnull
   private final VectorProvider vectorProviderA;
   @Nonnull
   private final VectorProvider vectorProviderB;
   @Nonnull
   private final Vector3d rVectorA;
   @Nonnull
   private final Vector3d rVectorB;

   public SubtracterVectorProvider(@Nonnull VectorProvider vectorProviderA, @Nonnull VectorProvider vectorProviderB) {
      this.vectorProviderA = vectorProviderA;
      this.vectorProviderB = vectorProviderB;
      this.rVectorA = new Vector3d();
      this.rVectorB = new Vector3d();
   }

   @Override
   public void process(@Nonnull VectorProvider.Context context, @Nonnull Vector3d vector_out) {
      this.vectorProviderA.process(context, this.rVectorA);
      this.vectorProviderB.process(context, this.rVectorB);
      vector_out.set(this.rVectorA).sub(this.rVectorB);
   }
}
