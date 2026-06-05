package com.hypixel.hytale.builtin.hytalegenerator.vectorproviders;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class SetXVectorProvider extends VectorProvider {
   @Nonnull
   private final Density value;
   @Nonnull
   private final VectorProvider vectorProvider;
   @Nonnull
   private final Density.Context rDensityContext;
   @Nonnull
   private final Vector3d rPosition;
   @Nonnull
   private final Vector3d rVector;

   public SetXVectorProvider(@Nonnull Density value, @Nonnull VectorProvider vectorProvider) {
      this.value = value;
      this.vectorProvider = vectorProvider;
      this.rDensityContext = new Density.Context();
      this.rPosition = new Vector3d();
      this.rVector = new Vector3d();
   }

   @Override
   public void process(@Nonnull VectorProvider.Context context, @Nonnull Vector3d vector_out) {
      this.vectorProvider.process(context, this.rVector);
      this.rPosition.set(context.position);
      this.rDensityContext.assign(context);
      this.rDensityContext.position = this.rPosition;
      this.rVector.x = this.value.process(this.rDensityContext);
      vector_out.set(this.rVector);
   }
}
