/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.random;

import com.hypixel.hytale.procedurallib.property.NoiseProperty;
import com.hypixel.hytale.procedurallib.random.ICoordinateRandomizer;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class CoordinateRandomizer
implements ICoordinateRandomizer {
    public static final ICoordinateRandomizer EMPTY_RANDOMIZER = new EmptyCoordinateRandomizer();
    protected final AmplitudeNoiseProperty[] xNoise;
    protected final AmplitudeNoiseProperty[] yNoise;
    protected final AmplitudeNoiseProperty[] zNoise;

    public CoordinateRandomizer(AmplitudeNoiseProperty[] xNoise, AmplitudeNoiseProperty[] yNoise, AmplitudeNoiseProperty[] zNoise) {
        this.xNoise = xNoise;
        this.yNoise = yNoise;
        this.zNoise = zNoise;
    }

    public AmplitudeNoiseProperty[] getXNoise() {
        return this.xNoise;
    }

    public AmplitudeNoiseProperty[] getYNoise() {
        return this.yNoise;
    }

    public AmplitudeNoiseProperty[] getZNoise() {
        return this.zNoise;
    }

    @Override
    public double randomDoubleX(int seed, double x, double y) {
        double offsetX = 0.0;
        for (AmplitudeNoiseProperty property : this.xNoise) {
            offsetX += (property.property.get(seed, x, y) * 2.0 - 1.0) * property.amplitude;
        }
        return x + offsetX;
    }

    @Override
    public double randomDoubleY(int seed, double x, double y) {
        double offsetY = 0.0;
        for (AmplitudeNoiseProperty property : this.yNoise) {
            offsetY += (property.property.get(seed, x, y) * 2.0 - 1.0) * property.amplitude;
        }
        return y + offsetY;
    }

    @Override
    public double randomDoubleX(int seed, double x, double y, double z) {
        double offsetX = 0.0;
        for (AmplitudeNoiseProperty property : this.xNoise) {
            offsetX += (property.property.get(seed, x, y, z) * 2.0 - 1.0) * property.amplitude;
        }
        return x + offsetX;
    }

    @Override
    public double randomDoubleY(int seed, double x, double y, double z) {
        double offsetY = 0.0;
        for (AmplitudeNoiseProperty property : this.yNoise) {
            offsetY += (property.property.get(seed, x, y, z) * 2.0 - 1.0) * property.amplitude;
        }
        return y + offsetY;
    }

    @Override
    public double randomDoubleZ(int seed, double x, double y, double z) {
        double offsetZ = 0.0;
        for (AmplitudeNoiseProperty property : this.zNoise) {
            offsetZ += (property.property.get(seed, x, y, z) * 2.0 - 1.0) * property.amplitude;
        }
        return z + offsetZ;
    }

    @Nonnull
    public String toString() {
        return "CoordinateRandomizer{xNoise=" + Arrays.toString(this.xNoise) + ", yNoise=" + Arrays.toString(this.yNoise) + ", zNoise=" + Arrays.toString(this.zNoise) + "}";
    }

    public static class AmplitudeNoiseProperty {
        protected NoiseProperty property;
        protected double amplitude;

        public AmplitudeNoiseProperty(NoiseProperty property, double amplitude) {
            this.property = property;
            this.amplitude = amplitude;
        }

        public NoiseProperty getProperty() {
            return this.property;
        }

        public void setProperty(NoiseProperty property) {
            this.property = property;
        }

        public double getAmplitude() {
            return this.amplitude;
        }

        public void setAmplitude(double amplitude) {
            this.amplitude = amplitude;
        }

        @Nonnull
        public String toString() {
            return "AmplitudeNoiseProperty{property=" + String.valueOf(this.property) + ", amplitude=" + this.amplitude + "}";
        }
    }

    private static class EmptyCoordinateRandomizer
    implements ICoordinateRandomizer {
        private EmptyCoordinateRandomizer() {
        }

        @Override
        public double randomDoubleX(int seed, double x, double y) {
            return x;
        }

        @Override
        public double randomDoubleY(int seed, double x, double y) {
            return y;
        }

        @Override
        public double randomDoubleX(int seed, double x, double y, double z) {
            return x;
        }

        @Override
        public double randomDoubleY(int seed, double x, double y, double z) {
            return y;
        }

        @Override
        public double randomDoubleZ(int seed, double x, double y, double z) {
            return z;
        }

        @Nonnull
        public String toString() {
            return "EmptyCoordinateRandomizer{}";
        }
    }
}

