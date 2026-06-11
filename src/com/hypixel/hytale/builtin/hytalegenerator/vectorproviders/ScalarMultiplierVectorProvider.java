package com.hypixel.hytale.builtin.hytalegenerator.vectorproviders;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class ScalarMultiplierVectorProvider extends VectorProvider {
   @Nonnull
   private final Density scalar;
   @Nonnull
   private final VectorProvider vectorProvider;
   @Nonnull
   private final Density.Context rChildContext;
   @Nonnull
   private final Vector3d rPosition;
   @Nonnull
   private final Vector3d rVector;

   public ScalarMultiplierVectorProvider(@Nonnull Density scalar, @Nonnull VectorProvider vectorProvider) {
      this.scalar = scalar;
      this.vectorProvider = vectorProvider;
      this.rChildContext = new Density.Context();
      this.rPosition = new Vector3d();
      this.rVector = new Vector3d();
   }

   @Override
   public void process(@Nonnull VectorProvider.Context context, @Nonnull Vector3d vector_out) {
      this.rPosition.set(context.position);
      this.rChildContext.assign(context);
      this.rChildContext.position = this.rPosition;
      this.vectorProvider.process(context, this.rVector);
      double densityValue = this.scalar.process(this.rChildContext);
      this.rVector.mul(densityValue);
      vector_out.set(this.rVector);
   }
}
