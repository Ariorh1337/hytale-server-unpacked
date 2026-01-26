/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.util;

import com.hypixel.hytale.protocol.Direction;
import com.hypixel.hytale.protocol.Position;
import javax.annotation.Nonnull;

public class ValidateUtil {
    public static boolean isSafeDouble(double x) {
        return !Double.isNaN(x) && Double.isFinite(x);
    }

    public static boolean isSafeFloat(float x) {
        return !Float.isNaN(x) && Float.isFinite(x);
    }

    public static boolean isSafePosition(@Nonnull Position position) {
        return ValidateUtil.isSafeDouble(position.x) && ValidateUtil.isSafeDouble(position.y) && ValidateUtil.isSafeDouble(position.z);
    }

    public static boolean isSafeDirection(@Nonnull Direction direction) {
        return ValidateUtil.isSafeFloat(direction.yaw) && ValidateUtil.isSafeFloat(direction.pitch) && ValidateUtil.isSafeFloat(direction.roll);
    }
}

