package com.hypixel.hytale.server.core.asset.type.blocktype.config;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import javax.annotation.Nonnull;

public record RotationTuple(int index, Rotation yaw, Rotation pitch, Rotation roll) {
   public static final RotationTuple[] EMPTY_ARRAY = new RotationTuple[0];
   public static final RotationTuple NONE = new RotationTuple(0, Rotation.None, Rotation.None, Rotation.None);
   public static final int NONE_INDEX = 0;
   @Nonnull
   public static final RotationTuple[] VALUES;

   public static RotationTuple of(@Nonnull Rotation yaw, @Nonnull Rotation pitch, @Nonnull Rotation roll) {
      return VALUES[index(yaw, pitch, roll)];
   }

   public static RotationTuple of(@Nonnull Rotation yaw, @Nonnull Rotation pitch) {
      return VALUES[index(yaw, pitch, Rotation.None)];
   }

   public static int index(@Nonnull Rotation yaw, @Nonnull Rotation pitch, @Nonnull Rotation roll) {
      return roll.ordinal() * Rotation.VALUES.length * Rotation.VALUES.length + pitch.ordinal() * Rotation.VALUES.length + yaw.ordinal();
   }

   public static RotationTuple get(int index) {
      return VALUES[index];
   }

   public static RotationTuple getRotation(@Nonnull RotationTuple[] rotations, @Nonnull RotationTuple pair, @Nonnull Rotation rotation) {
      int index = 0;

      for (int i = 0; i < rotations.length; i++) {
         RotationTuple rotationPair = rotations[i];
         if (pair.equals(rotationPair)) {
            index = i;
            break;
         }
      }

      return rotations[(index + rotation.ordinal()) % Rotation.VALUES.length];
   }

   @Nonnull
   public RotationTuple add(@Nonnull RotationTuple rotation) {
      return of(rotation.yaw.add(this.yaw), rotation.pitch.add(this.pitch), rotation.roll.add(this.roll));
   }

   @Nonnull
   public Vector3d rotatedVector(@Nonnull Vector3d vector) {
      return Rotation.rotate(vector, this.yaw, this.pitch, this.roll);
   }

   public void applyRotationTo(@Nonnull Vector3i vector) {
      Rotation.applyRotationTo(vector, this.yaw, this.pitch, this.roll);
   }

   public void applyRotationTo(@Nonnull Vector3f vector) {
      Rotation.applyRotationTo(vector, this.yaw, this.pitch, this.roll);
   }

   public void applyRotationTo(@Nonnull Vector3d vector) {
      Rotation.applyRotationTo(vector, this.yaw, this.pitch, this.roll);
   }

   public void undoRotationTo(@Nonnull Vector3i vector) {
      Rotation.undoRotationTo(vector, this.yaw, this.pitch, this.roll);
   }

   public void undoRotationTo(@Nonnull Vector3f vector) {
      Rotation.undoRotationTo(vector, this.yaw, this.pitch, this.roll);
   }

   public void undoRotationTo(@Nonnull Vector3d vector) {
      Rotation.undoRotationTo(vector, this.yaw, this.pitch, this.roll);
   }

   static {
      RotationTuple[] arr = new RotationTuple[Rotation.VALUES.length * Rotation.VALUES.length * Rotation.VALUES.length];
      arr[0] = NONE;

      for (Rotation roll : Rotation.VALUES) {
         for (Rotation pitch : Rotation.VALUES) {
            for (Rotation yaw : Rotation.VALUES) {
               if (yaw != Rotation.None || pitch != Rotation.None || roll != Rotation.None) {
                  int index = index(yaw, pitch, roll);
                  arr[index] = new RotationTuple(index, yaw, pitch, roll);
               }
            }
         }
      }

      VALUES = arr;
   }
}
