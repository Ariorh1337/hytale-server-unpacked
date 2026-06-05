package com.hypixel.hytale.builtin.hytalegenerator.vectorproviders;

import com.hypixel.hytale.builtin.hytalegenerator.VectorUtil;
import java.util.List;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class MultiplierVectorProvider extends VectorProvider {
   @Nonnull
   private final List<VectorProvider> vectorProviders;
   @Nonnull
   private final Vector3d rVector;

   public MultiplierVectorProvider(@Nonnull List<VectorProvider> vectorProviders) {
      this.vectorProviders = vectorProviders;
      this.rVector = new Vector3d();
   }

   @Override
   public void process(@Nonnull VectorProvider.Context context, @Nonnull Vector3d vector_out) {
      if (this.vectorProviders.isEmpty()) {
         vector_out.set(0.0, 0.0, 0.0);
      } else {
         this.vectorProviders.getFirst().process(context, vector_out);
         if (!VectorUtil.isZero(vector_out)) {
            for (int i = 1; i < this.vectorProviders.size(); i++) {
               this.vectorProviders.get(i).process(context, this.rVector);
               vector_out.mul(this.rVector);
               if (VectorUtil.isZero(vector_out)) {
                  return;
               }
            }
         }
      }
   }
}
