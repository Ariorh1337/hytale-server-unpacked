/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.debug;

import com.hypixel.hytale.math.matrix.Matrix4d;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.DebugShape;
import com.hypixel.hytale.protocol.ToClientPacket;
import com.hypixel.hytale.protocol.Vector3f;
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

public class DebugUtils {
    public static final com.hypixel.hytale.math.vector.Vector3f COLOR_BLACK = new com.hypixel.hytale.math.vector.Vector3f(0.0f, 0.0f, 0.0f);
    public static final com.hypixel.hytale.math.vector.Vector3f COLOR_WHITE = new com.hypixel.hytale.math.vector.Vector3f(1.0f, 1.0f, 1.0f);
    public static final com.hypixel.hytale.math.vector.Vector3f COLOR_RED = new com.hypixel.hytale.math.vector.Vector3f(1.0f, 0.0f, 0.0f);
    public static final com.hypixel.hytale.math.vector.Vector3f COLOR_LIME = new com.hypixel.hytale.math.vector.Vector3f(0.0f, 1.0f, 0.0f);
    public static final com.hypixel.hytale.math.vector.Vector3f COLOR_BLUE = new com.hypixel.hytale.math.vector.Vector3f(0.0f, 0.0f, 1.0f);
    public static final com.hypixel.hytale.math.vector.Vector3f COLOR_YELLOW = new com.hypixel.hytale.math.vector.Vector3f(1.0f, 1.0f, 0.0f);
    public static final com.hypixel.hytale.math.vector.Vector3f COLOR_CYAN = new com.hypixel.hytale.math.vector.Vector3f(0.0f, 1.0f, 1.0f);
    public static final com.hypixel.hytale.math.vector.Vector3f COLOR_MAGENTA = new com.hypixel.hytale.math.vector.Vector3f(1.0f, 0.0f, 1.0f);
    public static final com.hypixel.hytale.math.vector.Vector3f COLOR_SILVER = new com.hypixel.hytale.math.vector.Vector3f(0.75f, 0.75f, 0.75f);
    public static final com.hypixel.hytale.math.vector.Vector3f COLOR_GRAY = new com.hypixel.hytale.math.vector.Vector3f(0.5f, 0.5f, 0.5f);
    public static final com.hypixel.hytale.math.vector.Vector3f COLOR_MAROON = new com.hypixel.hytale.math.vector.Vector3f(0.5f, 0.0f, 0.0f);
    public static final com.hypixel.hytale.math.vector.Vector3f COLOR_OLIVE = new com.hypixel.hytale.math.vector.Vector3f(0.5f, 0.5f, 0.0f);
    public static final com.hypixel.hytale.math.vector.Vector3f COLOR_GREEN = new com.hypixel.hytale.math.vector.Vector3f(0.0f, 0.5f, 0.0f);
    public static final com.hypixel.hytale.math.vector.Vector3f COLOR_PURPLE = new com.hypixel.hytale.math.vector.Vector3f(0.5f, 0.0f, 0.5f);
    public static final com.hypixel.hytale.math.vector.Vector3f COLOR_TEAL = new com.hypixel.hytale.math.vector.Vector3f(0.0f, 0.5f, 0.5f);
    public static final com.hypixel.hytale.math.vector.Vector3f COLOR_NAVY = new com.hypixel.hytale.math.vector.Vector3f(0.0f, 0.0f, 0.5f);
    public static final com.hypixel.hytale.math.vector.Vector3f[] INDEXED_COLORS = new com.hypixel.hytale.math.vector.Vector3f[]{COLOR_RED, COLOR_BLUE, COLOR_LIME, COLOR_YELLOW, COLOR_CYAN, COLOR_MAGENTA, COLOR_PURPLE, COLOR_GREEN};
    public static final String[] INDEXED_COLOR_NAMES = new String[]{"Red", "Blue", "Lime", "Yellow", "Cyan", "Magenta", "Purple", "Green"};
    public static boolean DISPLAY_FORCES = false;
    public static final float DEFAULT_OPACITY = 0.8f;

    public static void add(@Nonnull World world, @Nonnull DebugShape shape, @Nonnull Matrix4d matrix, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, float time, boolean fade) {
        DebugUtils.add(world, shape, matrix, color, 0.8f, time, fade, null);
    }

    public static void add(@Nonnull World world, @Nonnull DebugShape shape, @Nonnull Matrix4d matrix, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, float opacity, float time, boolean fade) {
        DebugUtils.add(world, shape, matrix, color, opacity, time, fade, null);
    }

    private static void add(@Nonnull World world, @Nonnull DebugShape shape, @Nonnull Matrix4d matrix, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, float opacity, float time, boolean fade, @Nullable float[] shapeParams) {
        DisplayDebug packet = new DisplayDebug(shape, matrix.asFloatData(), new Vector3f(color.x, color.y, color.z), time, fade, shapeParams, opacity);
        for (PlayerRef playerRef : world.getPlayerRefs()) {
            playerRef.getPacketHandler().write((ToClientPacket)packet);
        }
    }

    public static void addFrustum(@Nonnull World world, @Nonnull Matrix4d matrix, @Nonnull Matrix4d frustumProjection, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, float time, boolean fade) {
        DebugUtils.add(world, DebugShape.Frustum, matrix, color, 0.8f, time, fade, frustumProjection.asFloatData());
    }

    public static void clear(@Nonnull World world) {
        ClearDebugShapes packet = new ClearDebugShapes();
        for (PlayerRef playerRef : world.getPlayerRefs()) {
            playerRef.getPacketHandler().write((ToClientPacket)packet);
        }
    }

    public static void addArrow(@Nonnull World world, @Nonnull Matrix4d baseMatrix, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, double length, float time, boolean fade) {
        Matrix4d matrix;
        double adjustedLength = length - 0.3;
        if (adjustedLength > 0.0) {
            matrix = new Matrix4d(baseMatrix);
            matrix.translate(0.0, adjustedLength * 0.5, 0.0);
            matrix.scale(0.1f, adjustedLength, 0.1f);
            DebugUtils.add(world, DebugShape.Cylinder, matrix, color, time, fade);
        }
        matrix = new Matrix4d(baseMatrix);
        matrix.translate(0.0, adjustedLength + 0.15, 0.0);
        matrix.scale(0.3f, 0.3f, 0.3f);
        DebugUtils.add(world, DebugShape.Cone, matrix, color, time, fade);
    }

    public static void addSphere(@Nonnull World world, @Nonnull Vector3d pos, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, double scale, float time) {
        DebugUtils.addSphere(world, pos.x, pos.y, pos.z, color, scale, time);
    }

    public static void addSphere(@Nonnull World world, double x, double y, double z, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, double scale, float time) {
        Matrix4d matrix = new Matrix4d();
        matrix.identity();
        matrix.translate(x, y, z);
        matrix.scale(scale, scale, scale);
        DebugUtils.add(world, DebugShape.Sphere, matrix, color, time, true);
    }

    public static void addCone(@Nonnull World world, @Nonnull Vector3d pos, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, double scale, float time) {
        Matrix4d matrix = DebugUtils.makeMatrix(pos, scale);
        DebugUtils.add(world, DebugShape.Cone, matrix, color, time, true);
    }

    public static void addCube(@Nonnull World world, @Nonnull Vector3d pos, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, double scale, float time) {
        DebugUtils.addCube(world, pos.x, pos.y, pos.z, color, scale, time);
    }

    public static void addCube(@Nonnull World world, double x, double y, double z, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, double scale, float time) {
        Matrix4d matrix = new Matrix4d();
        matrix.identity();
        matrix.translate(x, y, z);
        matrix.scale(scale, scale, scale);
        DebugUtils.add(world, DebugShape.Cube, matrix, color, time, true);
    }

    public static void addCylinder(@Nonnull World world, @Nonnull Vector3d pos, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, double scale, float time) {
        Matrix4d matrix = DebugUtils.makeMatrix(pos, scale);
        DebugUtils.add(world, DebugShape.Cylinder, matrix, color, time, true);
    }

    public static void addLine(@Nonnull World world, @Nonnull Vector3d start, @Nonnull Vector3d end, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, double thickness, float time, boolean fade) {
        DebugUtils.addLine(world, start.x, start.y, start.z, end.x, end.y, end.z, color, thickness, time, fade);
    }

    public static void addLine(@Nonnull World world, double startX, double startY, double startZ, double endX, double endY, double endZ, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, double thickness, float time, boolean fade) {
        double dirX = endX - startX;
        double dirY = endY - startY;
        double dirZ = endZ - startZ;
        double length = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        if (length < 0.001) {
            return;
        }
        Matrix4d tmp = new Matrix4d();
        Matrix4d matrix = new Matrix4d();
        matrix.identity();
        matrix.translate(startX, startY, startZ);
        double angleY = Math.atan2(dirZ, dirX);
        matrix.rotateAxis(angleY + 1.5707963267948966, 0.0, 1.0, 0.0, tmp);
        double angleX = Math.atan2(Math.sqrt(dirX * dirX + dirZ * dirZ), dirY);
        matrix.rotateAxis(angleX, 1.0, 0.0, 0.0, tmp);
        matrix.translate(0.0, length / 2.0, 0.0);
        matrix.scale(thickness, length, thickness);
        DebugUtils.add(world, DebugShape.Cylinder, matrix, color, time, fade);
    }

    public static void addDisc(@Nonnull World world, @Nonnull Matrix4d matrix, double outerRadius, double innerRadius, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, float opacity, int segmentCount, float time, boolean fade) {
        float[] shapeParams = new float[]{(float)outerRadius, segmentCount, (float)innerRadius, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        DebugUtils.add(world, DebugShape.Disc, matrix, color, opacity, time, fade, shapeParams);
    }

    public static void addDisc(@Nonnull World world, @Nonnull Matrix4d matrix, double outerRadius, double innerRadius, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, float opacity, float time, boolean fade) {
        DebugUtils.addDisc(world, matrix, outerRadius, innerRadius, color, opacity, 32, time, fade);
    }

    public static void addDisc(@Nonnull World world, @Nonnull Vector3d center, double radius, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, float time, boolean fade) {
        DebugUtils.addDisc(world, center.x, center.y, center.z, radius, 0.0, color, 0.8f, time, fade);
    }

    public static void addDisc(@Nonnull World world, double x, double y, double z, double radius, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, float time, boolean fade) {
        DebugUtils.addDisc(world, x, y, z, radius, 0.0, color, 0.8f, time, fade);
    }

    public static void addDisc(@Nonnull World world, double x, double y, double z, double radius, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, float opacity, float time, boolean fade) {
        DebugUtils.addDisc(world, x, y, z, radius, 0.0, color, opacity, 32, time, fade);
    }

    public static void addDisc(@Nonnull World world, double x, double y, double z, double outerRadius, double innerRadius, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, float opacity, float time, boolean fade) {
        DebugUtils.addDisc(world, x, y, z, outerRadius, innerRadius, color, opacity, 32, time, fade);
    }

    public static void addDisc(@Nonnull World world, double x, double y, double z, double outerRadius, double innerRadius, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, float opacity, int segmentCount, float time, boolean fade) {
        Matrix4d matrix = new Matrix4d();
        matrix.identity();
        matrix.translate(x, y, z);
        DebugUtils.addDisc(world, matrix, outerRadius, innerRadius, color, opacity, segmentCount, time, fade);
    }

    public static void addSector(@Nonnull World world, double x, double y, double z, double heading, double radius, double angle, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, float time, boolean fade) {
        DebugUtils.addSector(world, x, y, z, heading, radius, angle, 0.0, color, 0.8f, 16, time, fade);
    }

    public static void addSector(@Nonnull World world, double x, double y, double z, double heading, double radius, double angle, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, float opacity, float time, boolean fade) {
        DebugUtils.addSector(world, x, y, z, heading, radius, angle, 0.0, color, opacity, 16, time, fade);
    }

    public static void addSector(@Nonnull World world, double x, double y, double z, double heading, double outerRadius, double angle, double innerRadius, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, float opacity, float time, boolean fade) {
        DebugUtils.addSector(world, x, y, z, heading, outerRadius, angle, innerRadius, color, opacity, 16, time, fade);
    }

    public static void addSector(@Nonnull World world, double x, double y, double z, double heading, double outerRadius, double angle, double innerRadius, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, float opacity, int segmentCount, float time, boolean fade) {
        Matrix4d tmp = new Matrix4d();
        Matrix4d matrix = new Matrix4d();
        matrix.identity();
        matrix.translate(x, y, z);
        matrix.rotateAxis(heading, 0.0, 1.0, 0.0, tmp);
        float[] shapeParams = new float[]{(float)outerRadius, (float)angle, (float)innerRadius, segmentCount, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        DebugUtils.add(world, DebugShape.Sector, matrix, color, opacity, time, fade, shapeParams);
    }

    public static void addArrow(@Nonnull World world, @Nonnull Vector3d position, @Nonnull Vector3d direction, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, float time, boolean fade) {
        Vector3d directionClone = direction.clone();
        Matrix4d tmp = new Matrix4d();
        Matrix4d matrix = new Matrix4d();
        matrix.identity();
        matrix.translate(position);
        double angleY = Math.atan2(directionClone.z, directionClone.x);
        matrix.rotateAxis(angleY + 1.5707963267948966, 0.0, 1.0, 0.0, tmp);
        double angleX = Math.atan2(Math.sqrt(directionClone.x * directionClone.x + directionClone.z * directionClone.z), directionClone.y);
        matrix.rotateAxis(angleX, 1.0, 0.0, 0.0, tmp);
        DebugUtils.addArrow(world, matrix, color, directionClone.length(), time, fade);
    }

    public static void addForce(@Nonnull World world, @Nonnull Vector3d position, @Nonnull Vector3d force, @Nullable VelocityConfig velocityConfig) {
        if (!DISPLAY_FORCES) {
            return;
        }
        Vector3d forceClone = force.clone();
        if (velocityConfig == null || SplitVelocity.SHOULD_MODIFY_VELOCITY) {
            forceClone.x /= (double)DamageSystems.HackKnockbackValues.PLAYER_KNOCKBACK_SCALE;
            forceClone.z /= (double)DamageSystems.HackKnockbackValues.PLAYER_KNOCKBACK_SCALE;
        }
        Matrix4d tmp = new Matrix4d();
        Matrix4d matrix = new Matrix4d();
        matrix.identity();
        matrix.translate(position);
        double angleY = Math.atan2(forceClone.z, forceClone.x);
        matrix.rotateAxis(angleY + 1.5707963267948966, 0.0, 1.0, 0.0, tmp);
        double angleX = Math.atan2(Math.sqrt(forceClone.x * forceClone.x + forceClone.z * forceClone.z), forceClone.y);
        matrix.rotateAxis(angleX, 1.0, 0.0, 0.0, tmp);
        Random random = new Random();
        com.hypixel.hytale.math.vector.Vector3f color = new com.hypixel.hytale.math.vector.Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
        DebugUtils.addArrow(world, matrix, color, forceClone.length(), 10.0f, true);
    }

    @Nonnull
    private static Matrix4d makeMatrix(@Nonnull Vector3d pos, double scale) {
        Matrix4d matrix = new Matrix4d();
        matrix.identity();
        matrix.translate(pos);
        matrix.scale(scale, scale, scale);
        return matrix;
    }
}

