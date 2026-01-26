/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.logic.point;

import com.hypixel.hytale.procedurallib.logic.ResultBuffer;
import com.hypixel.hytale.procedurallib.logic.point.IPointGenerator;
import com.hypixel.hytale.procedurallib.random.ICoordinateRandomizer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DistortedPointGenerator
implements IPointGenerator {
    protected final IPointGenerator pointGenerator;
    protected final ICoordinateRandomizer coordinateRandomizer;

    public DistortedPointGenerator(IPointGenerator pointGenerator, ICoordinateRandomizer coordinateRandomizer) {
        this.pointGenerator = pointGenerator;
        this.coordinateRandomizer = coordinateRandomizer;
    }

    @Override
    public ResultBuffer.ResultBuffer2d nearest2D(int seed, double x, double y) {
        return this.pointGenerator.nearest2D(seed, this.coordinateRandomizer.randomDoubleX(seed, x, y), this.coordinateRandomizer.randomDoubleY(seed, x, y));
    }

    @Override
    public ResultBuffer.ResultBuffer3d nearest3D(int seed, double x, double y, double z) {
        return this.pointGenerator.nearest3D(seed, this.coordinateRandomizer.randomDoubleX(seed, x, y, z), this.coordinateRandomizer.randomDoubleY(seed, x, y, z), this.coordinateRandomizer.randomDoubleZ(seed, x, y, z));
    }

    @Override
    public ResultBuffer.ResultBuffer2d transition2D(int seed, double x, double y) {
        return this.pointGenerator.transition2D(seed, this.coordinateRandomizer.randomDoubleX(seed, x, y), this.coordinateRandomizer.randomDoubleY(seed, x, y));
    }

    @Override
    public ResultBuffer.ResultBuffer3d transition3D(int seed, double x, double y, double z) {
        return this.pointGenerator.transition3D(seed, this.coordinateRandomizer.randomDoubleX(seed, x, y, z), this.coordinateRandomizer.randomDoubleY(seed, x, y, z), this.coordinateRandomizer.randomDoubleZ(seed, x, y, z));
    }

    @Override
    public double getInterval() {
        return this.pointGenerator.getInterval();
    }

    @Override
    public void collect(int seed, double minX, double minY, double maxX, double maxY, IPointGenerator.PointConsumer2d consumer) {
        this.pointGenerator.collect(seed, minX, minY, maxX, maxY, consumer);
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DistortedPointGenerator that = (DistortedPointGenerator)o;
        if (!this.pointGenerator.equals(that.pointGenerator)) {
            return false;
        }
        return this.coordinateRandomizer.equals(that.coordinateRandomizer);
    }

    public int hashCode() {
        int result = this.pointGenerator.hashCode();
        result = 31 * result + this.coordinateRandomizer.hashCode();
        return result;
    }

    @Nonnull
    public String toString() {
        return "DistortedPointGenerator{pointGenerator=" + String.valueOf(this.pointGenerator) + ", coordinateRandomizer=" + String.valueOf(this.coordinateRandomizer) + "}";
    }
}

