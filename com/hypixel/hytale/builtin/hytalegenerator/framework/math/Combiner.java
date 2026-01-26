/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.framework.math;

import com.hypixel.hytale.builtin.hytalegenerator.framework.math.Calculator;
import javax.annotation.Nonnull;

public class Combiner {
    private final double y;
    private double value;

    public Combiner(double background, double y) {
        this.value = background;
        this.y = y;
    }

    @Nonnull
    public Layer addLayer(double density) {
        return new Layer(this, density);
    }

    public double getValue() {
        return this.value;
    }

    public class Layer {
        @Nonnull
        private final Combiner parent;
        private double value;
        private double floor;
        private double ceiling;
        private double paddingFloor;
        private double paddingCeiling;
        private IntersectionPolicy intersectionPolicy;
        private double intersectionSmoothingRange;
        private boolean withLimitsCheck;
        private boolean withPaddingCheck;
        private boolean withIntersectionPolicyCheck;
        private boolean isFinished = false;

        private Layer(Combiner combiner, double value) {
            if (combiner == null) {
                throw new NullPointerException();
            }
            this.parent = combiner;
            this.value = value;
        }

        @Nonnull
        public Combiner finishLayer() {
            double ceilingPaddingMultiplier;
            double floorPaddingMultiplier;
            if (!(this.withPaddingCheck && this.withIntersectionPolicyCheck && this.withLimitsCheck)) {
                throw new IllegalStateException("incomplete");
            }
            if (this.isFinished) {
                throw new IllegalStateException("method was already called");
            }
            this.isFinished = true;
            if (this.intersectionPolicy == IntersectionPolicy.MAX_POLICY) {
                this.ceiling = Calculator.smoothMax(this.intersectionSmoothingRange, this.floor, this.ceiling);
            } else if (this.intersectionPolicy == IntersectionPolicy.MIN_POLICY) {
                this.floor = Calculator.smoothMin(this.intersectionSmoothingRange, this.floor, this.ceiling);
            } else {
                this.ceiling = this.floor;
            }
            if (Combiner.this.y < this.floor || Combiner.this.y >= this.ceiling) {
                return this.parent;
            }
            if (this.paddingFloor == 0.0) {
                floorPaddingMultiplier = 1.0;
            } else {
                floorPaddingMultiplier = (Combiner.this.y - this.floor) / this.paddingFloor;
                floorPaddingMultiplier = Calculator.clamp(0.0, floorPaddingMultiplier, 1.0);
            }
            if (this.paddingCeiling == 0.0) {
                ceilingPaddingMultiplier = 1.0;
            } else {
                ceilingPaddingMultiplier = (this.ceiling - Combiner.this.y) / this.paddingCeiling;
                ceilingPaddingMultiplier = Calculator.clamp(0.0, ceilingPaddingMultiplier, 1.0);
            }
            double paddingMultiplier = Calculator.smoothMin(0.2, floorPaddingMultiplier, ceilingPaddingMultiplier);
            this.value *= paddingMultiplier;
            this.parent.value += this.value;
            return this.parent;
        }

        @Nonnull
        public Layer withLimits(double floor, double ceiling) {
            this.withLimitsCheck = true;
            this.floor = floor;
            this.ceiling = ceiling;
            return this;
        }

        @Nonnull
        public Layer withPadding(double paddingFloor, double paddingCeiling) {
            if (paddingFloor < 0.0 || paddingCeiling < 0.0) {
                throw new IllegalArgumentException("negative padding values");
            }
            this.withPaddingCheck = true;
            this.paddingFloor = paddingFloor;
            this.paddingCeiling = paddingCeiling;
            return this;
        }

        @Nonnull
        public Layer withIntersectionPolicy(@Nonnull IntersectionPolicy policy, double smoothRange) {
            if (policy == null) {
                throw new NullPointerException();
            }
            this.withIntersectionPolicyCheck = true;
            this.intersectionPolicy = policy;
            this.intersectionSmoothingRange = smoothRange;
            return this;
        }
    }

    public static enum IntersectionPolicy {
        MAX_POLICY,
        MIN_POLICY;

    }
}

