/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.chunk;

import com.hypixel.hytale.math.Axis;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockFlipType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.VariantRotation;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockRotationUtil {
    @Nullable
    public static RotationTuple getFlipped(@Nonnull RotationTuple blockRotation, @Nullable BlockFlipType flipType, @Nonnull Axis axis, @Nonnull VariantRotation variantRotation) {
        Rotation rotationYaw = blockRotation.yaw();
        Rotation rotationPitch = blockRotation.pitch();
        Rotation rotationRoll = blockRotation.roll();
        if (flipType != null) {
            rotationYaw = flipType.flipYaw(rotationYaw, axis);
        }
        boolean preventPitchRotation = axis != Axis.Y;
        return BlockRotationUtil.get(rotationYaw, rotationPitch, rotationRoll, axis, Rotation.OneEighty, variantRotation, preventPitchRotation);
    }

    @Nullable
    public static RotationTuple getRotated(@Nonnull RotationTuple blockRotation, @Nonnull Axis axis, Rotation rotation, @Nonnull VariantRotation variantRotation) {
        return BlockRotationUtil.get(blockRotation.yaw(), blockRotation.pitch(), blockRotation.roll(), axis, rotation, variantRotation, false);
    }

    @Nullable
    private static RotationTuple get(@Nonnull Rotation rotationYaw, @Nonnull Rotation rotationPitch, @Nonnull Rotation rotationRoll, @Nonnull Axis axis, Rotation rotation, @Nonnull VariantRotation variantRotation, boolean preventPitchRotation) {
        RotationTuple rotationPair = null;
        switch (axis) {
            case X: {
                RotationTuple rotateX = variantRotation.rotateX(RotationTuple.of(rotationYaw, rotationPitch), rotation);
                rotationPair = variantRotation.verify(rotateX);
                break;
            }
            case Y: {
                rotationPair = variantRotation.verify(RotationTuple.of(rotationYaw.add(rotation), rotationPitch));
                break;
            }
            case Z: {
                RotationTuple rotateZ = variantRotation.rotateZ(RotationTuple.of(rotationYaw, rotationPitch), rotation);
                rotationPair = variantRotation.verify(rotateZ);
                break;
            }
        }
        if (rotationPair == null) {
            return null;
        }
        if (preventPitchRotation) {
            rotationPair = RotationTuple.of(rotationPair.yaw(), rotationPitch);
        }
        return rotationPair;
    }

    public static int getFlippedFiller(int filler, @Nonnull Axis axis) {
        return BlockRotationUtil.getRotatedFiller(filler, axis, Rotation.OneEighty);
    }

    public static int getRotatedFiller(int filler, @Nonnull Axis axis, Rotation rotation) {
        return switch (axis) {
            default -> throw new MatchException(null, null);
            case Axis.X -> rotation.rotateX(filler);
            case Axis.Y -> rotation.rotateY(filler);
            case Axis.Z -> rotation.rotateZ(filler);
        };
    }
}

