package com.hypixel.hytale.builtin.hytalegenerator.vectorproviders;

import java.util.List;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class AdderVectorProvider extends VectorProvider {
   @Nonnull
   private final List<VectorProvider> vectorProviders;
   @Nonnull
   private final Vector3d rVector;

   public AdderVectorProvider(@Nonnull List<VectorProvider> vectorProviders) {
      this.vectorProviders = vectorProviders;
      this.rVector = new Vector3d();
   }

   @Override
   public void process(@Nonnull VectorProvider.Context context, @Nonnull Vector3d vector_out) {
      vector_out.set(0.0, 0.0, 0.0);

      for (VectorProvider vectorProvider : this.vectorProviders) {
         vectorProvider.process(context, this.rVector);
         vector_out.add(this.rVector);
      }
   }
}
