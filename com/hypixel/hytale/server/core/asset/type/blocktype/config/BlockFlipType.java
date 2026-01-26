/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.blocktype.config;

import com.hypixel.hytale.math.Axis;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import javax.annotation.Nonnull;

public enum BlockFlipType {
    ORTHOGONAL,
    SYMMETRIC;


    public Rotation flipYaw(@Nonnull Rotation rotation, Axis axis) {
        if (axis == Axis.Y) {
            return rotation;
        }
        switch (this.ordinal()) {
            case 0: {
                int multiplier = axis == rotation.getAxisOfAlignment() ? -1 : 1;
                int index = rotation.ordinal() + multiplier + Rotation.VALUES.length;
                return Rotation.VALUES[index %= Rotation.VALUES.length];
            }
            case 1: {
                if (rotation.getAxisOfAlignment() == axis) {
                    return rotation.add(Rotation.OneEighty);
                }
                return rotation;
            }
        }
        throw new UnsupportedOperationException();
    }
}

