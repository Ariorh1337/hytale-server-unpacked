package com.hypixel.hytale.builtin.hytalegenerator.vectorproviders;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class SetZVectorProvider extends VectorProvider {
   @Nonnull
   private final Density value;
   @Nonnull
   private final VectorProvider vectorProvider;
   @Nonnull
   private final Density.Context DensityContext;
   @Nonnull
   private final Vector3d rPosition;
   @Nonnull
   private final Vector3d rVector;

   public SetZVectorProvider(@Nonnull Density value, @Nonnull VectorProvider vectorProvider) {
      this.value = value;
      this.vectorProvider = vectorProvider;
      this.DensityContext = new Density.Context();
      this.rPosition = new Vector3d();
      this.rVector = new Vector3d();
   }

   @Override
   public void process(@Nonnull VectorProvider.Context context, @Nonnull Vector3d vector_out) {
      this.vectorProvider.process(context, this.rVector);
      this.rPosition.set(context.position);
      this.DensityContext.assign(context);
      this.DensityContext.position = this.rPosition;
      this.rVector.z = this.value.process(this.DensityContext);
      vector_out.set(this.rVector);
   }
}
