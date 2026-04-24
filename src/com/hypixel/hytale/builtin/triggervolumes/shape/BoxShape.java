package com.hypixel.hytale.builtin.triggervolumes.shape;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import javax.annotation.Nonnull;
import org.joml.Vector3d;

public class BoxShape extends TriggerVolumeShape {
   @Nonnull
   public static final BuilderCodec<BoxShape> CODEC = BuilderCodec.builder(BoxShape.class, BoxShape::new, BASE_CODEC)
      .append(new KeyedCodec<>("Min", Vector3dUtil.AS_ARRAY_CODEC), (s, v) -> s.min = v, s -> s.min)
      .add()
      .append(new KeyedCodec<>("Max", Vector3dUtil.AS_ARRAY_CODEC), (s, v) -> s.max = v, s -> s.max)
      .add()
      .afterDecode(BoxShape::computeDerived)
      .build();
   private Vector3d min;
   private Vector3d max;
   private transient Vector3d offset;
   private transient Vector3d halfExtents;

   public BoxShape() {
   }

   public BoxShape(@Nonnull Vector3d min, @Nonnull Vector3d max) {
      this.min = min;
      this.max = max;
      this.computeDerived();
   }

   private void computeDerived() {
      if (this.min != null && this.max != null) {
         this.offset = new Vector3d(this.min).add(this.max).mul(0.5);
         this.halfExtents = new Vector3d(this.max).sub(this.min).mul(0.5).absolute();
      }
   }

   @Override
   public boolean contains(@Nonnull Vector3d origin, @Nonnull Vector3d testPoint) {
      if (this.offset == null) {
         return false;
      }

      double cx = origin.x() + this.offset.x();
      double cy = origin.y() + this.offset.y();
      double cz = origin.z() + this.offset.z();
      return Math.abs(testPoint.x() - cx) <= this.halfExtents.x()
         && Math.abs(testPoint.y() - cy) <= this.halfExtents.y()
         && Math.abs(testPoint.z() - cz) <= this.halfExtents.z();
   }

   @Override
   public double getBoundingRadius() {
      return this.halfExtents != null ? this.halfExtents.length() : 0.0;
   }

   @Override
   public double getMaxDistanceFromOrigin() {
      return this.offset != null && this.halfExtents != null ? this.offset.length() + this.halfExtents.length() : 0.0;
   }

   @Override
   public void getWorldAABB(@Nonnull Vector3d origin, @Nonnull Vector3d outMin, @Nonnull Vector3d outMax) {
      outMin.set(origin).add(this.min);
      outMax.set(origin).add(this.max);
   }

   @Nonnull
   public Vector3d getMin() {
      return this.min;
   }

   @Nonnull
   public Vector3d getMax() {
      return this.max;
   }
}
