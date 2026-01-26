/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.logic.point;

import com.hypixel.hytale.procedurallib.logic.ResultBuffer;
import com.hypixel.hytale.procedurallib.logic.point.IPointGenerator;
import com.hypixel.hytale.procedurallib.logic.point.PointGenerator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ScaledPointGenerator
implements IPointGenerator {
    protected final PointGenerator pointGenerator;
    protected final double scale;

    public ScaledPointGenerator(PointGenerator pointGenerator, double scale) {
        this.pointGenerator = pointGenerator;
        this.scale = scale;
    }

    @Override
    @Nonnull
    public ResultBuffer.ResultBuffer2d nearest2D(int seed, double x, double y) {
        ResultBuffer.ResultBuffer2d buf = this.pointGenerator.nearest2D(seed, x * this.scale, y * this.scale);
        buf.x /= this.scale;
        buf.y /= this.scale;
        buf.distance = Math.sqrt(buf.distance) / this.scale;
        return buf;
    }

    @Override
    @Nonnull
    public ResultBuffer.ResultBuffer3d nearest3D(int seed, double x, double y, double z) {
        ResultBuffer.ResultBuffer3d buf = this.pointGenerator.nearest3D(seed, x * this.scale, y * this.scale, z * this.scale);
        buf.x /= this.scale;
        buf.y /= this.scale;
        buf.z /= this.scale;
        buf.distance = Math.sqrt(buf.distance) / this.scale;
        return buf;
    }

    @Override
    @Nonnull
    public ResultBuffer.ResultBuffer2d transition2D(int seed, double x, double y) {
        ResultBuffer.ResultBuffer2d buf = this.pointGenerator.transition2D(seed, x * this.scale, y * this.scale);
        buf.x /= this.scale;
        buf.x2 /= this.scale;
        buf.y /= this.scale;
        buf.y2 /= this.scale;
        buf.distance = Math.sqrt(buf.distance) / this.scale;
        buf.distance2 = Math.sqrt(buf.distance2) / this.scale;
        return buf;
    }

    @Override
    @Nonnull
    public ResultBuffer.ResultBuffer3d transition3D(int seed, double x, double y, double z) {
        ResultBuffer.ResultBuffer3d buf = this.pointGenerator.transition3D(seed, x * this.scale, y * this.scale, z * this.scale);
        buf.x /= this.scale;
        buf.x2 /= this.scale;
        buf.y /= this.scale;
        buf.y2 /= this.scale;
        buf.z /= this.scale;
        buf.z2 /= this.scale;
        buf.distance = Math.sqrt(buf.distance) / this.scale;
        buf.distance2 = Math.sqrt(buf.distance2) / this.scale;
        return buf;
    }

    @Override
    public double getInterval() {
        return this.pointGenerator.getInterval() / this.scale;
    }

    @Override
    public void collect(int seed, double minX, double minY, double maxX, double maxY, IPointGenerator.PointConsumer2d consumer) {
        this.pointGenerator.collect0(seed, minX *= this.scale, minY *= this.scale, maxX *= this.scale, maxY *= this.scale, (x, y, t) -> t.accept(x / this.scale, y / this.scale), consumer);
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ScaledPointGenerator that = (ScaledPointGenerator)o;
        if (Double.compare(that.scale, this.scale) != 0) {
            return false;
        }
        return this.pointGenerator.equals(that.pointGenerator);
    }

    public int hashCode() {
        int result = this.pointGenerator.hashCode();
        long temp = Double.doubleToLongBits(this.scale);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        return result;
    }

    @Nonnull
    public String toString() {
        return "ScaledPointGenerator{pointGenerator=" + String.valueOf(this.pointGenerator) + ", scale=" + this.scale + "}";
    }
}

