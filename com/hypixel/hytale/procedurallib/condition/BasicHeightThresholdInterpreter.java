/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.condition;

import com.hypixel.hytale.procedurallib.condition.IHeightThresholdInterpreter;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class BasicHeightThresholdInterpreter
implements IHeightThresholdInterpreter {
    @Nonnull
    protected final float[] interpolatedThresholds;
    protected final int lowestNonOne;
    protected final int highestNonZero;

    public BasicHeightThresholdInterpreter(@Nonnull int[] positions, @Nonnull float[] thresholds, int length) {
        int highestNonZero;
        int lowestNonOne;
        if (positions.length != thresholds.length) {
            throw new IllegalArgumentException(String.format("Mismatching array lengths! positions: %s, thresholds: %s", positions.length, thresholds.length));
        }
        this.interpolatedThresholds = new float[length];
        for (int y = 0; y < this.interpolatedThresholds.length; ++y) {
            float threshold = thresholds[thresholds.length - 1];
            for (int i = 0; i < positions.length; ++i) {
                if (y >= positions[i]) continue;
                if (i == 0) {
                    threshold = thresholds[i];
                    break;
                }
                float distance = (float)(y - positions[i - 1]) / (float)(positions[i] - positions[i - 1]);
                threshold = IHeightThresholdInterpreter.lerp(thresholds[i - 1], thresholds[i], distance);
                break;
            }
            this.interpolatedThresholds[y] = threshold;
        }
        for (lowestNonOne = 0; lowestNonOne < length && !(this.interpolatedThresholds[lowestNonOne] < 1.0f); ++lowestNonOne) {
        }
        this.lowestNonOne = lowestNonOne;
        for (highestNonZero = length - 1; highestNonZero >= 0 && !(this.interpolatedThresholds[highestNonZero] > 0.0f); --highestNonZero) {
        }
        this.highestNonZero = highestNonZero;
    }

    @Override
    public int getLowestNonOne() {
        return this.lowestNonOne;
    }

    @Override
    public int getHighestNonZero() {
        return this.highestNonZero;
    }

    @Override
    public double getContext(int seed, double x, double y) {
        return 0.0;
    }

    @Override
    public int getLength() {
        return this.interpolatedThresholds.length;
    }

    @Override
    public float getThreshold(int seed, double x, double y, int height) {
        return this.getThreshold(seed, x, y, height, this.getContext(seed, x, y));
    }

    @Override
    public float getThreshold(int seed, double x, double y, int height, double context) {
        if (height > this.highestNonZero) {
            return 0.0f;
        }
        if (height < 0) {
            height = 0;
        }
        if (height >= this.interpolatedThresholds.length) {
            height = this.interpolatedThresholds.length - 1;
        }
        return this.interpolatedThresholds[height];
    }

    @Nonnull
    public String toString() {
        return "BasicHeightThresholdInterpreter{interpolatedThresholds=" + Arrays.toString(this.interpolatedThresholds) + ", lowestNonOne=" + this.lowestNonOne + ", highestNonZero=" + this.highestNonZero + "}";
    }

    public static interface Constants {
        public static final String ERROR_ARRAY_LENGTH = "Mismatching array lengths! positions: %s, thresholds: %s";
    }
}

