package com.hypixel.hytale.server.core.modules.debug;

import com.hypixel.hytale.math.matrix.Matrix4dUtil;
import com.hypixel.hytale.protocol.DebugFlags;
import com.hypixel.hytale.protocol.DebugShape;
import com.hypixel.hytale.protocol.packets.player.ClearDebugShapes;
import com.hypixel.hytale.protocol.packets.player.DisplayDebug;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.modules.splitvelocity.SplitVelocity;
import com.hypixel.hytale.server.core.modules.splitvelocity.VelocityConfig;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class DebugUtils {
   public static final Vector3f COLOR_BLACK = new Vector3f(0.0F, 0.0F, 0.0F);
   public static final Vector3f COLOR_WHITE = new Vector3f(1.0F, 1.0F, 1.0F);
   public static final Vector3f COLOR_RED = new Vector3f(1.0F, 0.0F, 0.0F);
   public static final Vector3f COLOR_LIME = new Vector3f(0.0F, 1.0F, 0.0F);
   public static final Vector3f COLOR_BLUE = new Vector3f(0.0F, 0.0F, 1.0F);
   public static final Vector3f COLOR_YELLOW = new Vector3f(1.0F, 1.0F, 0.0F);
   public static final Vector3f COLOR_CYAN = new Vector3f(0.0F, 1.0F, 1.0F);
   public static final Vector3f COLOR_MAGENTA = new Vector3f(1.0F, 0.0F, 1.0F);
   public static final Vector3f COLOR_SILVER = new Vector3f(0.75F, 0.75F, 0.75F);
   public static final Vector3f COLOR_GRAY = new Vector3f(0.5F, 0.5F, 0.5F);
   public static final Vector3f COLOR_MAROON = new Vector3f(0.5F, 0.0F, 0.0F);
   public static final Vector3f COLOR_OLIVE = new Vector3f(0.5F, 0.5F, 0.0F);
   public static final Vector3f COLOR_GREEN = new Vector3f(0.0F, 0.5F, 0.0F);
   public static final Vector3f COLOR_PURPLE = new Vector3f(0.5F, 0.0F, 0.5F);
   public static final Vector3f COLOR_TEAL = new Vector3f(0.0F, 0.5F, 0.5F);
   public static final Vector3f COLOR_NAVY = new Vector3f(0.0F, 0.0F, 0.5F);
   public static final Vector3f[] INDEXED_COLORS = new Vector3f[]{
      COLOR_RED, COLOR_BLUE, COLOR_LIME, COLOR_YELLOW, COLOR_CYAN, COLOR_MAGENTA, COLOR_PURPLE, COLOR_GREEN
   };
   public static final String[] INDEXED_COLOR_NAMES = new String[]{"Red", "Blue", "Lime", "Yellow", "Cyan", "Magenta", "Purple", "Green"};
   public static boolean DISPLAY_FORCES = false;
   public static final float DEFAULT_OPACITY = 0.8F;
   public static final int FLAG_NONE = 0;
   public static final int FLAG_FADE = 1 << DebugFlags.Fade.getValue();
   public static final int FLAG_NO_WIREFRAME = 1 << DebugFlags.NoWireframe.getValue();
   public static final int FLAG_NO_SOLID = 1 << DebugFlags.NoSolid.getValue();

   public static void add(@Nonnull World world, @Nonnull DebugShape shape, @Nonnull Matrix4dc matrix, @Nonnull Vector3fc color, float time, int flags) {
      add(world, shape, matrix, color, 0.8F, time, flags, null);
   }

   public static void add(
      @Nonnull World world, @Nonnull DebugShape shape, @Nonnull Matrix4dc matrix, @Nonnull Vector3fc color, float opacity, float time, int flags
   ) {
      add(world, shape, matrix, color, opacity, time, flags, null);
   }

   private static void add(
      @Nonnull World world,
      @Nonnull DebugShape shape,
      @Nonnull Matrix4dc matrix,
      @Nonnull Vector3fc color,
      float opacity,
      float time,
      int flags,
      @Nullable float[] shapeParams
   ) {
      DisplayDebug packet = new DisplayDebug(shape, Matrix4dUtil.asFloatData(matrix), color, time, (byte)flags, shapeParams, opacity);

      for (PlayerRef playerRef : world.getPlayerRefs()) {
         playerRef.getPacketHandler().write(packet);
      }
   }

   public static void addFrustum(
      @Nonnull World world, @Nonnull Matrix4dc matrix, @Nonnull Matrix4dc frustumProjection, @Nonnull Vector3fc color, float time, int flags
   ) {
      add(world, DebugShape.Frustum, matrix, color, 0.8F, time, flags, Matrix4dUtil.asFloatData(frustumProjection));
   }

   public static void clear(@Nonnull World world) {
      ClearDebugShapes packet = new ClearDebugShapes();

      for (PlayerRef playerRef : world.getPlayerRefs()) {
         playerRef.getPacketHandler().write(packet);
      }
   }

   public static void addArrow(
      @Nonnull World world, @Nonnull Matrix4dc baseMatrix, @Nonnull Vector3fc color, float opacity, double length, float time, int flags
   ) {
      double adjustedLength = length - 0.3;
      if (adjustedLength > 0.0) {
         Matrix4d matrix = new Matrix4d(baseMatrix);
         matrix.translate(0.0, adjustedLength * 0.5, 0.0);
         matrix.scale(0.1F, adjustedLength, 0.1F);
         add(world, DebugShape.Cylinder, matrix, color, time, flags);
      }

      Matrix4d matrix = new Matrix4d(baseMatrix);
      matrix.translate(0.0, adjustedLength + 0.15, 0.0);
      matrix.scale(0.3F, 0.3F, 0.3F);
      add(world, DebugShape.Cone, matrix, color, opacity, time, flags);
   }

   public static void addArrow(@Nonnull World world, @Nonnull Matrix4dc baseMatrix, @Nonnull Vector3fc color, double length, float time, int flags) {
      addArrow(world, baseMatrix, color, 0.8F, length, time, flags);
   }

   public static void addSphere(@Nonnull World world, @Nonnull Vector3dc pos, @Nonnull Vector3fc color, double scale, float time) {
      addSphere(world, pos.x(), pos.y(), pos.z(), color, scale, time);
   }

   public static void addSphere(@Nonnull World world, double x, double y, double z, @Nonnull Vector3fc color, double scale, float time) {
      Matrix4d matrix = new Matrix4d();
      matrix.translate(x, y, z);
      matrix.scale(scale, scale, scale);
      add(world, DebugShape.Sphere, matrix, color, time, FLAG_FADE);
   }

   public static void addSphere(@Nonnull World world, @Nonnull Vector3dc pos, @Nonnull Vector3fc color, float opacity, double scale, float time) {
      addSphere(world, pos.x(), pos.y(), pos.z(), color, opacity, scale, time);
   }

   public static void addSphere(@Nonnull World world, double x, double y, double z, @Nonnull Vector3fc color, float opacity, double scale, float time) {
      Matrix4d matrix = new Matrix4d();
      matrix.translate(x, y, z);
      matrix.scale(scale, scale, scale);
      add(world, DebugShape.Sphere, matrix, color, opacity, time, FLAG_FADE);
   }

   public static void addCone(@Nonnull World world, @Nonnull Vector3dc pos, @Nonnull Vector3fc color, double scale, float time) {
      Matrix4d matrix = makeMatrix(pos, scale);
      add(world, DebugShape.Cone, matrix, color, time, FLAG_FADE);
   }

   public static void addCube(@Nonnull World world, @Nonnull Vector3dc pos, @Nonnull Vector3fc color, double scale, float time) {
      addCube(world, pos.x(), pos.y(), pos.z(), color, scale, time);
   }

   public static void addCube(@Nonnull World world, double x, double y, double z, @Nonnull Vector3fc color, double scale, float time) {
      Matrix4d matrix = new Matrix4d();
      matrix.translate(x, y, z);
      matrix.scale(scale, scale, scale);
      add(world, DebugShape.Cube, matrix, color, time, FLAG_FADE);
   }

   public static void addCylinder(@Nonnull World world, @Nonnull Vector3dc pos, @Nonnull Vector3fc color, double scale, float time) {
      Matrix4d matrix = makeMatrix(pos, scale);
      add(world, DebugShape.Cylinder, matrix, color, time, FLAG_FADE);
   }

   public static void addLine(
      @Nonnull World world, @Nonnull Vector3dc start, @Nonnull Vector3dc end, @Nonnull Vector3fc color, double thickness, float time, int flags
   ) {
      addLine(world, start.x(), start.y(), start.z(), end.x(), end.y(), end.z(), color, thickness, time, flags);
   }

   public static void addLine(
      @Nonnull World world,
      double startX,
      double startY,
      double startZ,
      double endX,
      double endY,
      double endZ,
      @Nonnull Vector3fc color,
      double thickness,
      float time,
      int flags
   ) {
      double dirX = endX - startX;
      double dirY = endY - startY;
      double dirZ = endZ - startZ;
      double length = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
      if (!(length < 0.001)) {
         Matrix4d matrix = new Matrix4d();
         matrix.translate(startX, startY, startZ);
         double angleY = Math.atan2(dirZ, dirX);
         matrix.rotate(-(angleY + (Math.PI / 2)), 0.0, 1.0, 0.0);
         double angleX = Math.atan2(Math.sqrt(dirX * dirX + dirZ * dirZ), dirY);
         matrix.rotate(-angleX, 1.0, 0.0, 0.0);
         matrix.translate(0.0, length / 2.0, 0.0);
         matrix.scale(thickness, length, thickness);
         add(world, DebugShape.Cylinder, matrix, color, time, flags);
      }
   }

   public static void addDisc(
      @Nonnull World world,
      @Nonnull Matrix4dc matrix,
      double outerRadius,
      double innerRadius,
      @Nonnull Vector3fc color,
      float opacity,
      int segmentCount,
      float time,
      int flags
   ) {
      float[] shapeParams = new float[]{
         (float)outerRadius, segmentCount, (float)innerRadius, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F
      };
      add(world, DebugShape.Disc, matrix, color, opacity, time, flags, shapeParams);
   }

   public static void addDisc(
      @Nonnull World world, @Nonnull Matrix4dc matrix, double outerRadius, double innerRadius, @Nonnull Vector3fc color, float opacity, float time, int flags
   ) {
      addDisc(world, matrix, outerRadius, innerRadius, color, opacity, 32, time, flags);
   }

   public static void addDisc(@Nonnull World world, @Nonnull Vector3dc center, double radius, @Nonnull Vector3fc color, float time, int flags) {
      addDisc(world, center.x(), center.y(), center.z(), radius, 0.0, color, 0.8F, time, flags);
   }

   public static void addDisc(@Nonnull World world, double x, double y, double z, double radius, @Nonnull Vector3fc color, float time, int flags) {
      addDisc(world, x, y, z, radius, 0.0, color, 0.8F, time, flags);
   }

   public static void addDisc(@Nonnull World world, double x, double y, double z, double radius, @Nonnull Vector3fc color, float opacity, float time, int flags) {
      addDisc(world, x, y, z, radius, 0.0, color, opacity, 32, time, flags);
   }

   public static void addDisc(
      @Nonnull World world,
      double x,
      double y,
      double z,
      double outerRadius,
      double innerRadius,
      @Nonnull Vector3fc color,
      float opacity,
      float time,
      int flags
   ) {
      addDisc(world, x, y, z, outerRadius, innerRadius, color, opacity, 32, time, flags);
   }

   public static void addDisc(
      @Nonnull World world,
      double x,
      double y,
      double z,
      double outerRadius,
      double innerRadius,
      @Nonnull Vector3fc color,
      float opacity,
      int segmentCount,
      float time,
      int flags
   ) {
      Matrix4d matrix = new Matrix4d();
      matrix.translate(x, y, z);
      addDisc(world, matrix, outerRadius, innerRadius, color, opacity, segmentCount, time, flags);
   }

   public static void addSector(
      @Nonnull World world, double x, double y, double z, double heading, double radius, double angle, @Nonnull Vector3fc color, float time, int flags
   ) {
      addSector(world, x, y, z, heading, radius, angle, 0.0, color, 0.8F, 16, time, flags);
   }

   public static void addSector(
      @Nonnull World world,
      double x,
      double y,
      double z,
      double heading,
      double radius,
      double angle,
      @Nonnull Vector3fc color,
      float opacity,
      float time,
      int flags
   ) {
      addSector(world, x, y, z, heading, radius, angle, 0.0, color, opacity, 16, time, flags);
   }

   public static void addSector(
      @Nonnull World world,
      double x,
      double y,
      double z,
      double heading,
      double outerRadius,
      double angle,
      double innerRadius,
      @Nonnull Vector3fc color,
      float opacity,
      float time,
      int flags
   ) {
      addSector(world, x, y, z, heading, outerRadius, angle, innerRadius, color, opacity, 16, time, flags);
   }

   public static void addSector(
      @Nonnull World world,
      double x,
      double y,
      double z,
      double heading,
      double outerRadius,
      double angle,
      double innerRadius,
      @Nonnull Vector3fc color,
      float opacity,
      int segmentCount,
      float time,
      int flags
   ) {
      Matrix4d matrix = new Matrix4d();
      matrix.translate(x, y, z);
      matrix.rotate(-heading, 0.0, 1.0, 0.0);
      float[] shapeParams = new float[]{
         (float)outerRadius, (float)angle, (float)innerRadius, segmentCount, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F
      };
      add(world, DebugShape.Sector, matrix, color, opacity, time, flags, shapeParams);
   }

   public static void addArrow(
      @Nonnull World world, @Nonnull Vector3dc position, @Nonnull Vector3dc direction, @Nonnull Vector3fc color, float opacity, float time, int flags
   ) {
      Matrix4d matrix = new Matrix4d();
      matrix.translate(position);
      double angleY = Math.atan2(direction.z(), direction.x());
      matrix.rotate(-(angleY + (Math.PI / 2)), 0.0, 1.0, 0.0);
      double angleX = Math.atan2(Math.sqrt(direction.x() * direction.x() + direction.z() * direction.z()), direction.y());
      matrix.rotate(-angleX, 1.0, 0.0, 0.0);
      addArrow(world, matrix, color, opacity, direction.length(), time, flags);
   }

   public static void addArrow(@Nonnull World world, @Nonnull Vector3dc position, @Nonnull Vector3dc direction, @Nonnull Vector3fc color, float time, int flags) {
      addArrow(world, position, direction, color, 0.8F, time, flags);
   }

   public static void addVelocity(@Nonnull World world, @Nonnull Vector3dc position, @Nonnull Vector3dc velocity, @Nullable VelocityConfig velocityConfig) {
      if (DISPLAY_FORCES) {
         Vector3d velocityClone = new Vector3d(velocity);
         if (velocityConfig == null || SplitVelocity.SHOULD_MODIFY_VELOCITY) {
            velocityClone.x = velocityClone.x / DamageSystems.HackKnockbackValues.PLAYER_KNOCKBACK_SCALE;
            velocityClone.z = velocityClone.z / DamageSystems.HackKnockbackValues.PLAYER_KNOCKBACK_SCALE;
         }

         Matrix4d matrix = new Matrix4d();
         matrix.translate(position);
         double angleY = Math.atan2(velocityClone.z, velocityClone.x);
         matrix.rotate(-(angleY + (Math.PI / 2)), 0.0, 1.0, 0.0);
         double angleX = Math.atan2(Math.sqrt(velocityClone.x * velocityClone.x + velocityClone.z * velocityClone.z), velocityClone.y);
         matrix.rotate(-angleX, 1.0, 0.0, 0.0);
         Random random = new Random();
         Vector3f color = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
         addArrow(world, matrix, color, velocityClone.length(), 10.0F, FLAG_FADE);
      }
   }

   @Nonnull
   public static Matrix4d makeMatrix(@Nonnull Vector3dc pos, double scale) {
      Matrix4d matrix = new Matrix4d();
      matrix.translate(pos);
      matrix.scale(scale, scale, scale);
      return matrix;
   }
}
