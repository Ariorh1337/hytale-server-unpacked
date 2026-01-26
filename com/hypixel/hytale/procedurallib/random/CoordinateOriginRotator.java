/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.random;

import com.hypixel.hytale.procedurallib.random.CoordinateRotator;
import javax.annotation.Nonnull;

public class CoordinateOriginRotator
extends CoordinateRotator {
    private final double originX;
    private final double originY;
    private final double originZ;

    public CoordinateOriginRotator(double pitch, double yaw, double originX, double originY, double originZ) {
        super(pitch, yaw);
        this.originX = originX;
        this.originY = originY;
        this.originZ = originZ;
    }

    @Override
    public double randomDoubleX(int seed, double x, double y) {
        return this.originX + this.rotateX(x -= this.originX, y -= this.originY);
    }

    @Override
    public double randomDoubleY(int seed, double x, double y) {
        return this.originY + this.rotateY(x -= this.originX, y -= this.originY);
    }

    @Override
    public double randomDoubleX(int seed, double x, double y, double z) {
        return this.originX + this.rotateX(x -= this.originX, y -= this.originY, z -= this.originZ);
    }

    @Override
    public double randomDoubleY(int seed, double x, double y, double z) {
        return this.originY + this.rotateY(x -= this.originX, y -= this.originY, z -= this.originZ);
    }

    @Override
    public double randomDoubleZ(int seed, double x, double y, double z) {
        return this.originZ + this.rotateZ(x -= this.originX, y -= this.originY, z -= this.originZ);
    }

    @Override
    @Nonnull
    public String toString() {
        return "CoordinateOriginRotator{pitch=" + this.pitch + ", yaw=" + this.yaw + ", originX=" + this.originX + ", originY=" + this.originY + ", originZ=" + this.originZ + "}";
    }
}

