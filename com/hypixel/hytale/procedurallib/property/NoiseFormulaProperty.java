/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.property;

import com.hypixel.hytale.procedurallib.property.NoiseProperty;
import javax.annotation.Nonnull;

public class NoiseFormulaProperty
implements NoiseProperty {
    protected final NoiseProperty property;
    protected final NoiseFormula.Formula formula;

    public NoiseFormulaProperty(NoiseProperty property, NoiseFormula.Formula formula) {
        this.property = property;
        this.formula = formula;
    }

    public NoiseProperty getProperty() {
        return this.property;
    }

    public NoiseFormula.Formula getFormula() {
        return this.formula;
    }

    @Override
    public double get(int seed, double x, double y) {
        return this.formula.eval(this.property.get(seed, x, y));
    }

    @Override
    public double get(int seed, double x, double y, double z) {
        return this.formula.eval(this.property.get(seed, x, y, z));
    }

    @Nonnull
    public String toString() {
        return "NoiseFormulaProperty{property=" + String.valueOf(this.property) + ", formula=" + String.valueOf(this.formula) + "}";
    }

    public static enum NoiseFormula {
        NORMAL(new Formula(){

            @Override
            public double eval(double noise) {
                return noise;
            }

            @Nonnull
            public String toString() {
                return "NormalFormula{}";
            }
        }),
        INVERTED(new Formula(){

            @Override
            public double eval(double noise) {
                return 1.0 - noise;
            }

            @Nonnull
            public String toString() {
                return "InvertedFormula{}";
            }
        }),
        SQUARED(new Formula(){

            @Override
            public double eval(double noise) {
                return noise * noise;
            }

            @Nonnull
            public String toString() {
                return "SquaredFormula{}";
            }
        }),
        INVERTED_SQUARED(new Formula(){

            @Override
            public double eval(double noise) {
                return 1.0 - noise * noise;
            }

            @Nonnull
            public String toString() {
                return "InvertedSquaredFormula{}";
            }
        }),
        SQRT(new Formula(){

            @Override
            public double eval(double noise) {
                return Math.sqrt(noise);
            }

            @Nonnull
            public String toString() {
                return "SqrtFormula{}";
            }
        }),
        INVERTED_SQRT(new Formula(){

            @Override
            public double eval(double noise) {
                return 1.0 - Math.sqrt(noise);
            }

            @Nonnull
            public String toString() {
                return "InvertedSqrtFormula{}";
            }
        }),
        RIDGED(new Formula(){

            @Override
            public double eval(double noise) {
                return Math.abs(noise - 0.5);
            }

            @Nonnull
            public String toString() {
                return "RidgedFormula{}";
            }
        }),
        INVERTED_RIDGED(new Formula(){

            @Override
            public double eval(double noise) {
                return 1.0 - Math.abs(noise - 0.5);
            }

            @Nonnull
            public String toString() {
                return "InvertedRidgedFormula{}";
            }
        }),
        RIDGED_SQRT(new Formula(){

            @Override
            public double eval(double noise) {
                return Math.sqrt(Math.abs(noise - 0.5));
            }

            @Nonnull
            public String toString() {
                return "RidgedSqrtFormula{}";
            }
        }),
        INVERTED_RIDGED_SQRT(new Formula(){

            @Override
            public double eval(double noise) {
                return 1.0 - Math.sqrt(Math.abs(noise - 0.5));
            }

            @Nonnull
            public String toString() {
                return "InvertedRidgedSqrtFormula{}";
            }
        }),
        RIDGED_FIX(new Formula(){

            @Override
            public double eval(double noise) {
                return Math.abs(noise * 2.0 - 1.0);
            }

            @Nonnull
            public String toString() {
                return "RidgedFixFormula{}";
            }
        }),
        INVERTED_RIDGED_FIX(new Formula(){

            @Override
            public double eval(double noise) {
                return 1.0 - Math.abs(noise * 2.0 - 1.0);
            }

            @Nonnull
            public String toString() {
                return "InvertedRidgedFixFormula{}";
            }
        }),
        RIDGED_SQRT_FIX(new Formula(){

            @Override
            public double eval(double noise) {
                return Math.sqrt(Math.abs(noise * 2.0 - 1.0));
            }

            @Nonnull
            public String toString() {
                return "RidgedSqrtFixFormula{}";
            }
        }),
        INVERTED_RIDGED_SQRT_FIX(new Formula(){

            @Override
            public double eval(double noise) {
                return 1.0 - Math.sqrt(Math.abs(noise * 2.0 - 1.0));
            }

            @Nonnull
            public String toString() {
                return "InvertedRidgedSqrtFixFormula{}";
            }
        }),
        RIDGED_SQUARED_FIX(new Formula(){

            @Override
            public double eval(double noise) {
                noise = Math.abs(noise * 2.0 - 1.0);
                return noise * noise;
            }

            @Nonnull
            public String toString() {
                return "RidgedSquaredFixFormula{}";
            }
        }),
        INVERTED_RIDGED_SQUARED_FIX(new Formula(){

            @Override
            public double eval(double noise) {
                noise = Math.abs(noise * 2.0 - 1.0);
                return 1.0 - noise * noise;
            }

            @Nonnull
            public String toString() {
                return "InvertedRidgedSquaredFixFormula{}";
            }
        });

        public final Formula formula;

        private NoiseFormula(Formula formula) {
            this.formula = formula;
        }

        public Formula getFormula() {
            return this.formula;
        }

        @FunctionalInterface
        public static interface Formula {
            public double eval(double var1);
        }
    }
}

