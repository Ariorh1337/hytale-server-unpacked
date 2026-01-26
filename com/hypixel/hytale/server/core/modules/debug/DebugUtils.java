/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.debug;

import com.hypixel.hytale.math.matrix.Matrix4d;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.DebugShape;
import com.hypixel.hytale.protocol.Packet;
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
    public static boolean DISPLAY_FORCES = false;

    public static void add(@Nonnull World world, @Nonnull DebugShape shape, @Nonnull Matrix4d matrix, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, float time, boolean fade) {
        DisplayDebug packet = new DisplayDebug(shape, matrix.asFloatData(), new Vector3f(color.x, color.y, color.z), time, fade, null);
        for (PlayerRef playerRef : world.getPlayerRefs()) {
            playerRef.getPacketHandler().write((Packet)packet);
        }
    }

    public static void addFrustum(@Nonnull World world, @Nonnull Matrix4d matrix, @Nonnull Matrix4d frustumProjection, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, float time, boolean fade) {
        DisplayDebug packet = new DisplayDebug(DebugShape.Frustum, matrix.asFloatData(), new Vector3f(color.x, color.y, color.z), time, fade, frustumProjection.asFloatData());
        for (PlayerRef playerRef : world.getPlayerRefs()) {
            playerRef.getPacketHandler().write((Packet)packet);
        }
    }

    public static void clear(@Nonnull World world) {
        ClearDebugShapes packet = new ClearDebugShapes();
        for (PlayerRef playerRef : world.getPlayerRefs()) {
            playerRef.getPacketHandler().write((Packet)packet);
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
        Matrix4d matrix = DebugUtils.makeMatrix(pos, scale);
        DebugUtils.add(world, DebugShape.Sphere, matrix, color, time, true);
    }

    public static void addCone(@Nonnull World world, @Nonnull Vector3d pos, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, double scale, float time) {
        Matrix4d matrix = DebugUtils.makeMatrix(pos, scale);
        DebugUtils.add(world, DebugShape.Cone, matrix, color, time, true);
    }

    public static void addCube(@Nonnull World world, @Nonnull Vector3d pos, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, double scale, float time) {
        Matrix4d matrix = DebugUtils.makeMatrix(pos, scale);
        DebugUtils.add(world, DebugShape.Cube, matrix, color, time, true);
    }

    public static void addCylinder(@Nonnull World world, @Nonnull Vector3d pos, @Nonnull com.hypixel.hytale.math.vector.Vector3f color, double scale, float time) {
        Matrix4d matrix = DebugUtils.makeMatrix(pos, scale);
        DebugUtils.add(world, DebugShape.Cylinder, matrix, color, time, true);
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

