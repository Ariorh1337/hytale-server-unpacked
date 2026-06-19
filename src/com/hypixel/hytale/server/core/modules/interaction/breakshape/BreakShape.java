package com.hypixel.hytale.server.core.modules.interaction.breakshape;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.function.consumer.TriIntConsumer;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.protocol.BreakShapeOrientation;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;

public abstract class BreakShape implements NetworkSerializable<com.hypixel.hytale.protocol.BreakShape> {
   public static final int MAX_DIMENSION = 32;
   public static final int MAX_TOTAL_CELLS = 1024;
   public static final int MAX_OFFSET = 32;
   @Nonnull
   public static final CodecMapCodec<BreakShape> CODEC = new CodecMapCodec<>();
   @Nonnull
   public static final BuilderCodec<BreakShape> BASE_CODEC = BuilderCodec.abstractBuilder(BreakShape.class)
      .append(new KeyedCodec<>("Width", Codec.INTEGER, true), (shape, value) -> shape.width = value, shape -> shape.width)
      .addValidator(Validators.range(1, 32))
      .documentation("The size of the shape along the user's horizontal view axis (right).")
      .add()
      .<Integer>append(new KeyedCodec<>("Height", Codec.INTEGER, true), (shape, value) -> shape.height = value, shape -> shape.height)
      .addValidator(Validators.range(1, 32))
      .documentation("The size of the shape along the user's vertical view axis (up).")
      .add()
      .<Integer>append(new KeyedCodec<>("Depth", Codec.INTEGER), (shape, value) -> shape.depth = value, shape -> shape.depth)
      .addValidator(Validators.range(1, 32))
      .documentation("How many blocks deep the shape extends into the targeted surface. Defaults to 1.")
      .add()
      .<Boolean>append(new KeyedCodec<>("Centered", Codec.BOOLEAN), (shape, value) -> shape.centered = value, shape -> shape.centered)
      .documentation("Whether the shape's width/height are centered on the targeted block. Defaults to true.")
      .add()
      .<Vector3d>append(new KeyedCodec<>("Offset", Vector3dUtil.CODEC), (shape, value) -> shape.offset.set(value), shape -> shape.offset)
      .addValidator(Validators.nonNull())
      .documentation("Shifts the shape's center off the targeted block, in the view basis (x = right, y = up, z = into the surface). Rounded to whole blocks.")
      .add()
      .<BreakShapeOrientation>append(
         new KeyedCodec<>("Orientation", new EnumCodec<>(BreakShapeOrientation.class)), (shape, value) -> shape.orientation = value, shape -> shape.orientation
      )
      .addValidator(Validators.nonNull())
      .documentation(
         "How the shape is oriented: 'View' (the dominant axis of the look direction, the default) or 'Surface' (into the targeted block face that the look ray hits)."
      )
      .add()
      .build();
   protected int width = 1;
   protected int height = 1;
   protected int depth = 1;
   protected boolean centered = true;
   @Nonnull
   protected final Vector3d offset = new Vector3d();
   @Nonnull
   protected BreakShapeOrientation orientation = BreakShapeOrientation.View;

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getDepth() {
      return this.depth;
   }

   public boolean isCentered() {
      return this.centered;
   }

   @Nonnull
   public Vector3f getOffset() {
      return new Vector3f((float)this.offset.x, (float)this.offset.y, (float)this.offset.z);
   }

   @Nonnull
   public BreakShapeOrientation getOrientation() {
      return this.orientation;
   }

   public abstract void forEachLocalOffset(@Nonnull TriIntConsumer var1);

   public void selectBlocks(
      @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Ref<EntityStore> ref, @Nullable Vector3i anchorBlock, @Nonnull TriIntConsumer consumer
   ) {
      if (anchorBlock != null) {
         Vector3i forward = new Vector3i();
         Vector3i up = new Vector3i();
         Vector3i right = new Vector3i();
         if (this.orientation == BreakShapeOrientation.Surface) {
            computeSurfaceBasis(commandBuffer, ref, anchorBlock, forward, up, right);
         } else {
            computeViewBasis(commandBuffer.getComponent(ref, HeadRotation.getComponentType()), forward, up, right);
         }

         int ox = (int)Math.round(this.offset.x);
         int oy = (int)Math.round(this.offset.y);
         int oz = (int)Math.round(this.offset.z);
         int baseX = anchorBlock.x() + right.x() * ox + up.x() * oy + forward.x() * oz;
         int baseY = anchorBlock.y() + right.y() * ox + up.y() * oy + forward.y() * oz;
         int baseZ = anchorBlock.z() + right.z() * ox + up.z() * oy + forward.z() * oz;
         this.forEachLocalOffset((u, v, w) -> {
            int x = baseX + right.x() * u + up.x() * v + forward.x() * w;
            int y = baseY + right.y() * u + up.y() * v + forward.y() * w;
            int z = baseZ + right.z() * u + up.z() * v + forward.z() * w;
            consumer.accept(x, y, z);
         });
      }
   }

   protected void validateShape() {
      long total = (long)this.width * this.height * this.depth;
      if (total > 1024L) {
         throw new IllegalArgumentException(
            "Break shape volume " + total + " (" + this.width + "x" + this.height + "x" + this.depth + ") exceeds the maximum of 1024"
         );
      } else if (Math.abs(this.offset.x) > 32.0 || Math.abs(this.offset.y) > 32.0 || Math.abs(this.offset.z) > 32.0) {
         throw new IllegalArgumentException(
            "Break shape offset (" + this.offset.x + ", " + this.offset.y + ", " + this.offset.z + ") exceeds the maximum magnitude of 32 per axis"
         );
      }
   }

   protected static int lowBound(int size, boolean centered) {
      return centered ? -((size - 1) / 2) : 0;
   }

   private static void computeViewBasis(@Nullable HeadRotation headRotation, @Nonnull Vector3i forward, @Nonnull Vector3i up, @Nonnull Vector3i right) {
      double dx;
      double dy;
      double dz;
      if (headRotation != null) {
         Vector3d dir = headRotation.getDirection();
         dx = dir.x();
         dy = dir.y();
         dz = dir.z();
      } else {
         dx = 0.0;
         dy = -1.0;
         dz = 0.0;
      }

      double ax = Math.abs(dx);
      double ay = Math.abs(dy);
      double az = Math.abs(dz);
      if (ay >= ax && ay >= az) {
         forward.set(0, dy >= 0.0 ? 1 : -1, 0);
         if (ax >= az) {
            up.set(dx >= 0.0 ? 1 : -1, 0, 0);
         } else {
            up.set(0, 0, dz >= 0.0 ? 1 : -1);
         }
      } else if (ax >= az) {
         forward.set(dx >= 0.0 ? 1 : -1, 0, 0);
         up.set(0, 1, 0);
      } else {
         forward.set(0, 0, dz >= 0.0 ? 1 : -1);
         up.set(0, 1, 0);
      }

      right.set(up.y() * forward.z() - up.z() * forward.y(), up.z() * forward.x() - up.x() * forward.z(), up.x() * forward.y() - up.y() * forward.x());
   }

   private static void computeSurfaceBasis(
      @Nonnull CommandBuffer<EntityStore> commandBuffer,
      @Nonnull Ref<EntityStore> ref,
      @Nonnull Vector3i anchorBlock,
      @Nonnull Vector3i forward,
      @Nonnull Vector3i up,
      @Nonnull Vector3i right
   ) {
      HeadRotation headRotation = commandBuffer.getComponent(ref, HeadRotation.getComponentType());
      TransformComponent transform = commandBuffer.getComponent(ref, TransformComponent.getComponentType());
      if (headRotation != null && transform != null) {
         float eyeHeight = 0.0F;
         ModelComponent modelComponent = commandBuffer.getComponent(ref, ModelComponent.getComponentType());
         if (modelComponent != null) {
            eyeHeight = modelComponent.getModel().getEyeHeight(ref, commandBuffer);
         }

         Vector3d eyePosition = transform.getPosition();
         double ex = eyePosition.x();
         double ey = eyePosition.y() + eyeHeight;
         double ez = eyePosition.z();
         Vector3d dir = headRotation.getDirection();
         double tx = entryDistance(ex, dir.x(), anchorBlock.x());
         double ty = entryDistance(ey, dir.y(), anchorBlock.y());
         double tz = entryDistance(ez, dir.z(), anchorBlock.z());
         if (ty >= tx && ty >= tz) {
            forward.set(0, dir.y() >= 0.0 ? 1 : -1, 0);
         } else if (tx >= tz) {
            forward.set(dir.x() >= 0.0 ? 1 : -1, 0, 0);
         } else {
            forward.set(0, 0, dir.z() >= 0.0 ? 1 : -1);
         }

         if (forward.y() != 0) {
            if (Math.abs(dir.x()) >= Math.abs(dir.z())) {
               up.set(dir.x() >= 0.0 ? 1 : -1, 0, 0);
            } else {
               up.set(0, 0, dir.z() >= 0.0 ? 1 : -1);
            }
         } else {
            up.set(0, 1, 0);
         }

         right.set(up.y() * forward.z() - up.z() * forward.y(), up.z() * forward.x() - up.x() * forward.z(), up.x() * forward.y() - up.y() * forward.x());
      } else {
         computeViewBasis(headRotation, forward, up, right);
      }
   }

   private static double entryDistance(double origin, double direction, int lo) {
      if (direction > 0.0) {
         return (lo - origin) / direction;
      } else {
         return direction < 0.0 ? (lo + 1 - origin) / direction : Double.NEGATIVE_INFINITY;
      }
   }
}
