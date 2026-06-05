package com.hypixel.hytale.builtin.hytalegenerator.vectorproviders;

import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class PlaneProjectorVectorProvider extends VectorProvider {
   @Nonnull
   private final VectorProvider vectorProvider;
   @Nonnull
   private final VectorProvider planeA;
   @Nonnull
   private final VectorProvider planeB;
   @Nonnull
   private final Vector3d rVector;
   @Nonnull
   private final Vector3d rPlaneA;
   @Nonnull
   private final Vector3d rPlaneB;
   @Nonnull
   private final Vector3d rNormal;

   public PlaneProjectorVectorProvider(@Nonnull VectorProvider vectorProvider, @Nonnull VectorProvider planeA, @Nonnull VectorProvider planeB) {
      this.vectorProvider = vectorProvider;
      this.planeA = planeA;
      this.planeB = planeB;
      this.rVector = new Vector3d();
      this.rPlaneA = new Vector3d();
      this.rPlaneB = new Vector3d();
      this.rNormal = new Vector3d();
   }

   @Override
   public void process(@Nonnull VectorProvider.Context context, @Nonnull Vector3d vector_out) {
      this.vectorProvider.process(context, this.rVector);
      this.planeA.process(context, this.rPlaneA);
      this.planeB.process(context, this.rPlaneB);
      this.rPlaneA.cross(this.rPlaneB, this.rNormal);
      double sqrMag = this.rNormal.lengthSquared();
      if (sqrMag == 0.0) {
         vector_out.set(this.rVector);
      } else {
         double dot = this.rVector.dot(this.rNormal);
         vector_out.set(this.rVector).fma(-dot / sqrMag, this.rNormal);
      }
   }
}
