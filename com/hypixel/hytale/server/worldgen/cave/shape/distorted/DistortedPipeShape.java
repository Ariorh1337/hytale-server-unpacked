/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.cave.shape.distorted;

import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.procedurallib.logic.GeneralNoise;
import com.hypixel.hytale.server.worldgen.cave.shape.distorted.DistortedCylinderShape;
import com.hypixel.hytale.server.worldgen.cave.shape.distorted.DistortedShape;
import javax.annotation.Nonnull;

public class DistortedPipeShape
extends DistortedCylinderShape {
    private final double compensation;

    public DistortedPipeShape(@Nonnull Vector3d o, @Nonnull Vector3d v, double startWidth, double startHeight, double midWidth, double midHeight, double endWidth, double endHeight, double maxWidth, double maxHeight, double compensation, GeneralNoise.InterpolationFunction interpolation) {
        super(o, v, startWidth, startHeight, midWidth, midHeight, endWidth, endHeight, maxWidth, maxHeight, interpolation);
        this.compensation = compensation;
    }

    @Override
    public double getWidthAt(double t) {
        return DistortedPipeShape.getCompensatedDim(t, this.startWidth, this.midWidth, this.endWidth, this.compensation, this.interpolation);
    }

    @Override
    public double getHeightAt(double t) {
        return DistortedPipeShape.getCompensatedDim(t, this.startHeight, this.midHeight, this.endHeight, this.compensation, this.interpolation);
    }

    @Override
    public boolean isValidProjection(double t) {
        return true;
    }

    @Override
    @Nonnull
    public String toString() {
        return "DistortedPipeShape{origin=" + String.valueOf(this.o) + ", direction=" + String.valueOf(this.v) + ", startWidth=" + this.startWidth + ", startHeight=" + this.startHeight + ", midWidth=" + this.midWidth + ", midHeight=" + this.midHeight + ", endWidth=" + this.endWidth + ", endHeight=" + this.endHeight + "}";
    }

    protected static double getCompensatedDim(double t, double startDim, double midDim, double endDim, double compensation, @Nonnull GeneralNoise.InterpolationFunction interpolation) {
        if (t <= 0.0) {
            double fade = 1.0 - MathUtil.clamp(t, -0.5, 0.0) * -2.0;
            fade = interpolation.interpolate(fade);
            return MathUtil.lerp(startDim, startDim * fade, compensation);
        }
        if (t >= 1.0) {
            double fade = 1.0 - MathUtil.clamp(t - 1.0, 0.0, 0.5) * 2.0;
            fade = interpolation.interpolate(fade);
            return MathUtil.lerp(endDim, endDim * fade, compensation);
        }
        if (t <= 0.5) {
            t = interpolation.interpolate(t * 2.0);
            return MathUtil.lerpUnclamped(startDim, midDim, t);
        }
        t = interpolation.interpolate((t - 0.5) * 2.0);
        return MathUtil.lerpUnclamped(midDim, endDim, t);
    }

    public static class Factory
    implements DistortedShape.Factory {
        @Override
        @Nonnull
        public DistortedShape create(@Nonnull Vector3d origin, @Nonnull Vector3d direction, double length, double startWidth, double startHeight, double midWidth, double midHeight, double endWidth, double endHeight, GeneralNoise.InterpolationFunction interpolation) {
            double compensation = DistortedCylinderShape.getCompensationFactor(direction);
            double scale = DistortedCylinderShape.getHeightCompensation(compensation);
            double maxWidth = MathUtil.maxValue(startWidth, midWidth, endWidth);
            double maxHeight = MathUtil.maxValue(startHeight *= scale, midHeight *= scale, endHeight *= scale);
            return new DistortedPipeShape(origin, direction, startWidth, startHeight, midWidth, midHeight, endWidth, endHeight, maxWidth, maxHeight, compensation, interpolation);
        }
    }
}

