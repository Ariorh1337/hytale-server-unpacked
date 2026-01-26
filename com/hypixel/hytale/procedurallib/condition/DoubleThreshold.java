/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.condition;

import com.hypixel.hytale.procedurallib.condition.IDoubleThreshold;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class DoubleThreshold {

    public static class Multiple
    implements IDoubleThreshold {
        protected final Single[] singles;

        public Multiple(Single[] singles) {
            this.singles = singles;
        }

        @Override
        public boolean eval(double d) {
            for (Single single : this.singles) {
                if (!single.eval(d)) continue;
                return true;
            }
            return false;
        }

        @Override
        public boolean eval(double d, double factor) {
            for (Single single : this.singles) {
                if (!single.eval(d, factor)) continue;
                return true;
            }
            return false;
        }

        @Nonnull
        public String toString() {
            return "DoubleThreshold.Multiple{singles=" + Arrays.toString(this.singles) + "}";
        }
    }

    public static class Single
    implements IDoubleThreshold {
        protected final double min;
        protected final double max;
        protected final double halfRange;

        public Single(double min, double max) {
            this.min = min;
            this.max = max;
            this.halfRange = (max - min) * 0.5;
        }

        @Override
        public boolean eval(double d) {
            return this.min <= d && d <= this.max;
        }

        @Override
        public boolean eval(double d, double factor) {
            double t0 = this.min + this.halfRange * factor;
            double t1 = this.max - this.halfRange * factor;
            return t0 <= d && d <= t1;
        }

        @Nonnull
        public String toString() {
            return "DoubleThreshold.Single{min=" + this.min + ", max=" + this.max + ", halfRange=" + this.halfRange + "}";
        }
    }
}

