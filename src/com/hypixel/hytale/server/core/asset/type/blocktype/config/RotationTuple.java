package com.hypixel.hytale.server.core.asset.type.blocktype.config;

import com.hypixel.hytale.math.Axis;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.HashMap;
import javax.annotation.Nonnull;

public record RotationTuple(int index, Rotation yaw, Rotation pitch, Rotation roll) {
   public static final RotationTuple[] EMPTY_ARRAY = new RotationTuple[0];
   public static final RotationTuple NONE = new RotationTuple(0, Rotation.None, Rotation.None, Rotation.None);
   public static final int NONE_INDEX = 0;
   @Nonnull
   public static final RotationTuple[] VALUES;
   private static final HashMap<Long, RotationTuple> MATRIX_TO_TUPLE;

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
   public RotationTuple composeOnAxis(@Nonnull Axis axis, @Nonnull Rotation rotation) {
      int[][] current = eulerToMatrix(this.yaw, this.pitch, this.roll);
      int[][] axisRot = axisRotationMatrix(axis, rotation);
      int[][] result = multiply3x3(axisRot, current);
      return matrixToRotationTuple(result);
   }

   private static int[][] eulerToMatrix(@Nonnull Rotation yaw, @Nonnull Rotation pitch, @Nonnull Rotation roll) {
      int cy = cos90(yaw);
      int sy = sin90(yaw);
      int cp = cos90(pitch);
      int sp = sin90(pitch);
      int cr = cos90(roll);
      int sr = sin90(roll);
      return new int[][]{
         {cy * cr + sy * sp * sr, -cy * sr + sy * sp * cr, sy * cp}, {cp * sr, cp * cr, -sp}, {-sy * cr + cy * sp * sr, sy * sr + cy * sp * cr, cy * cp}
      };
   }

   private static int[][] axisRotationMatrix(@Nonnull Axis axis, @Nonnull Rotation rotation) {
      int c = cos90(rotation);
      int s = sin90(rotation);

      return switch (axis) {
         case X -> new int[][]{{1, 0, 0}, {0, c, -s}, {0, s, c}};
         case Y -> new int[][]{{c, 0, s}, {0, 1, 0}, {-s, 0, c}};
         case Z -> new int[][]{{c, -s, 0}, {s, c, 0}, {0, 0, 1}};
      };
   }

   private static int[][] multiply3x3(int[][] a, int[][] b) {
      int[][] r = new int[3][3];

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++) {
            r[i][j] = a[i][0] * b[0][j] + a[i][1] * b[1][j] + a[i][2] * b[2][j];
         }
      }

      return r;
   }

   private static RotationTuple matrixToRotationTuple(int[][] m) {
      int sp = -m[1][2];
      Rotation newPitch = sinToRotation(sp);
      Rotation newYaw;
      Rotation newRoll;
      if (sp != 1 && sp != -1) {
         newYaw = atan2_90(m[0][2], m[2][2]);
         newRoll = atan2_90(m[1][0], m[1][1]);
      } else {
         newYaw = atan2_90(-m[2][0], m[0][0]);
         newRoll = Rotation.None;
      }

      return of(newYaw, newPitch, newRoll);
   }

   private static int cos90(@Nonnull Rotation r) {
      return switch (r) {
         case None -> 1;
         case Ninety -> 0;
         case OneEighty -> -1;
         case TwoSeventy -> 0;
      };
   }

   private static int sin90(@Nonnull Rotation r) {
      return switch (r) {
         case None -> 0;
         case Ninety -> 1;
         case OneEighty -> 0;
         case TwoSeventy -> -1;
      };
   }

   private static Rotation sinToRotation(int s) {
      return switch (s) {
         case -1 -> Rotation.TwoSeventy;
         case 0 -> Rotation.None;
         case 1 -> Rotation.Ninety;
         default -> throw new IllegalArgumentException("Invalid sin value for 90-degree rotation: " + s);
      };
   }

   private static Rotation atan2_90(int sinVal, int cosVal) {
      if (sinVal == 0 && cosVal == 1) {
         return Rotation.None;
      } else if (sinVal == 1 && cosVal == 0) {
         return Rotation.Ninety;
      } else if (sinVal == 0 && cosVal == -1) {
         return Rotation.OneEighty;
      } else if (sinVal == -1 && cosVal == 0) {
         return Rotation.TwoSeventy;
      } else {
         throw new IllegalArgumentException("Invalid atan2 values for 90-degree rotation: sin=" + sinVal + " cos=" + cosVal);
      }
   }

   @Nonnull
   public Axis getAxisOfSymmetry() {
      int[] v = this.traceLocalY();
      if (v[1] != 0) {
         return Axis.Y;
      } else {
         return v[0] != 0 ? Axis.X : Axis.Z;
      }
   }

   public boolean isSymmetryNegative() {
      int[] v = this.traceLocalY();
      if (v[1] != 0) {
         return v[1] < 0;
      } else {
         return v[0] != 0 ? v[0] < 0 : v[2] < 0;
      }
   }

   private int[] traceLocalY() {
      int x = 0;
      int y = 1;
      int z = 0;
      switch (this.roll) {
         case Ninety: {
            int t = x;
            x = -y;
            y = t;
            break;
         }
         case OneEighty:
            x = -x;
            y = -y;
            break;
         case TwoSeventy: {
            int t = x;
            x = y;
            y = -t;
         }
      }

      switch (this.pitch) {
         case Ninety: {
            int t = y;
            y = -z;
            z = t;
            break;
         }
         case OneEighty:
            y = -y;
            z = -z;
            break;
         case TwoSeventy: {
            int t = y;
            y = z;
            z = -t;
         }
      }

      switch (this.yaw) {
         case Ninety: {
            int t = x;
            x = z;
            z = -t;
            break;
         }
         case OneEighty:
            x = -x;
            z = -z;
            break;
         case TwoSeventy: {
            int t = x;
            x = -z;
            z = t;
         }
      }

      return new int[]{x, y, z};
   }

   public int[][] toMatrix() {
      int[][] m = new int[][]{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
      rightMultiplyRotation(m, this.roll, 2);
      rightMultiplyRotation(m, this.yaw, 1);
      rightMultiplyRotation(m, this.pitch, 0);
      return m;
   }

   private static void rightMultiplyRotation(int[][] m, Rotation rot, int axisIdx) {
      if (rot != Rotation.None) {
         int c;
         int s;
         switch (rot) {
            case Ninety:
               c = 0;
               s = 1;
               break;
            case OneEighty:
               c = -1;
               s = 0;
               break;
            case TwoSeventy:
               c = 0;
               s = -1;
               break;
            default:
               return;
         }

         int a = (axisIdx + 1) % 3;
         int b = (axisIdx + 2) % 3;

         for (int row = 0; row < 3; row++) {
            int va = m[row][a];
            int vb = m[row][b];
            m[row][a] = c * va + s * vb;
            m[row][b] = -s * va + c * vb;
         }
      }
   }

   private static long matrixKey(int[][] m) {
      long key = 0L;

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++) {
            key = key << 2 | m[i][j] + 1;
         }
      }

      return key;
   }

   @Nonnull
   public static RotationTuple fromMatrix(int[][] m) {
      RotationTuple result = MATRIX_TO_TUPLE.get(matrixKey(m));
      if (result == null) {
         throw new IllegalArgumentException("Matrix does not correspond to any valid RotationTuple");
      } else {
         return result;
      }
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
      MATRIX_TO_TUPLE = new HashMap<>();

      for (RotationTuple t : VALUES) {
         long key = matrixKey(t.toMatrix());
         MATRIX_TO_TUPLE.putIfAbsent(key, t);
      }
   }
}
