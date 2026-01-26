/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.property;

import com.hypixel.hytale.procedurallib.NoiseFunction;
import com.hypixel.hytale.procedurallib.NoiseFunction2d;
import com.hypixel.hytale.procedurallib.NoiseFunction3d;
import com.hypixel.hytale.procedurallib.logic.GeneralNoise;
import com.hypixel.hytale.procedurallib.property.NoiseProperty;
import javax.annotation.Nonnull;

public class FractalNoiseProperty
implements NoiseProperty {
    protected final int seedOffset;
    protected final NoiseFunction function;
    protected final FractalFunction fractalFunction;
    protected final int octaves;
    protected final double lacunarity;
    protected final double persistence;

    public FractalNoiseProperty(int seedOffset, NoiseFunction function, FractalFunction fractalFunction, int octaves, double lacunarity, double persistence) {
        this.seedOffset = seedOffset;
        this.function = function;
        this.fractalFunction = fractalFunction;
        this.octaves = octaves;
        this.lacunarity = lacunarity;
        this.persistence = persistence;
    }

    public int getSeedOffset() {
        return this.seedOffset;
    }

    public NoiseFunction getFunction() {
        return this.function;
    }

    public FractalFunction getFractalFunction() {
        return this.fractalFunction;
    }

    public int getOctaves() {
        return this.octaves;
    }

    public double getLacunarity() {
        return this.lacunarity;
    }

    public double getPersistence() {
        return this.persistence;
    }

    @Override
    public double get(int seed, double x, double y) {
        return this.fractalFunction.get(seed, seed + this.seedOffset, x, y, this.octaves, this.lacunarity, this.persistence, this.function);
    }

    @Override
    public double get(int seed, double x, double y, double z) {
        return this.fractalFunction.get(seed, seed + this.seedOffset, x, y, z, this.octaves, this.lacunarity, this.persistence, this.function);
    }

    @Nonnull
    public String toString() {
        return "FractalNoiseProperty{seedOffset=" + this.seedOffset + ", function=" + String.valueOf(this.function) + ", fractalFunction=" + String.valueOf(this.fractalFunction) + ", octaves=" + this.octaves + ", lacunarity=" + this.lacunarity + ", persistence=" + this.persistence + "}";
    }

    private static interface FractalFunction {
        public double get(int var1, int var2, double var3, double var5, int var7, double var8, double var10, NoiseFunction2d var12);

        public double get(int var1, int var2, double var3, double var5, double var7, int var9, double var10, double var12, NoiseFunction3d var14);
    }

    public static enum FractalMode {
        FBM(new FractalFunction(){

            @Override
            public double get(int seed, int offsetSeed, double x, double y, int octaves, double lacunarity, double persistence, @Nonnull NoiseFunction2d noise) {
                double sum = noise.get(seed, offsetSeed, x, y);
                double amp = 1.0;
                for (int i = 1; i < octaves; ++i) {
                    sum += noise.get(seed, ++offsetSeed, x *= lacunarity, y *= lacunarity) * (amp *= persistence);
                }
                return GeneralNoise.limit(sum * 0.5 + 0.5);
            }

            @Override
            public double get(int seed, int offsetSeed, double x, double y, double z, int octaves, double lacunarity, double persistence, @Nonnull NoiseFunction3d noise) {
                double sum = noise.get(seed, offsetSeed, x, y, z);
                double amp = 1.0;
                for (int i = 1; i < octaves; ++i) {
                    sum += noise.get(seed, ++offsetSeed, x *= lacunarity, y *= lacunarity, z *= lacunarity) * (amp *= persistence);
                }
                return GeneralNoise.limit(sum * 0.5 + 0.5);
            }

            @Nonnull
            public String toString() {
                return "FbmFractalFunction{}";
            }
        }),
        BILLOW(new FractalFunction(){

            @Override
            public double get(int seed, int offsetSeed, double x, double y, int octaves, double lacunarity, double persistence, @Nonnull NoiseFunction2d noise) {
                double sum = Math.abs(noise.get(seed, offsetSeed, x, y)) * 2.0 - 1.0;
                double amp = 1.0;
                for (int i = 1; i < octaves; ++i) {
                    sum += (Math.abs(noise.get(seed, ++offsetSeed, x *= lacunarity, y *= lacunarity)) * 2.0 - 1.0) * (amp *= persistence);
                }
                return GeneralNoise.limit(sum * 0.5 + 0.5);
            }

            @Override
            public double get(int seed, int offsetSeed, double x, double y, double z, int octaves, double lacunarity, double persistence, @Nonnull NoiseFunction3d noise) {
                double sum = Math.abs(noise.get(seed, offsetSeed, x, y, z)) * 2.0 - 1.0;
                double amp = 1.0;
                for (int i = 1; i < octaves; ++i) {
                    sum += (Math.abs(noise.get(seed, ++offsetSeed, x *= lacunarity, y *= lacunarity, z *= lacunarity)) * 2.0 - 1.0) * (amp *= persistence);
                }
                return GeneralNoise.limit(sum * 0.5 + 0.5);
            }

            @Nonnull
            public String toString() {
                return "BillowFractalFunction{}";
            }
        }),
        MULTI_RIGID(new FractalFunction(){

            @Override
            public double get(int seed, int offsetSeed, double x, double y, int octaves, double lacunarity, double persistence, @Nonnull NoiseFunction2d noise) {
                double sum = 1.0 - Math.abs(noise.get(seed, offsetSeed, x, y));
                double amp = 1.0;
                for (int i = 1; i < octaves; ++i) {
                    sum -= (1.0 - Math.abs(noise.get(seed, ++offsetSeed, x *= lacunarity, y *= lacunarity))) * (amp *= persistence);
                }
                return GeneralNoise.limit(sum * 0.5 + 0.5);
            }

            @Override
            public double get(int seed, int offsetSeed, double x, double y, double z, int octaves, double lacunarity, double persistence, @Nonnull NoiseFunction3d noise) {
                double sum = 1.0 - Math.abs(noise.get(seed, offsetSeed, x, y, z));
                float amp = 1.0f;
                for (int i = 1; i < octaves; ++i) {
                    amp = (float)((double)amp * persistence);
                    sum -= (1.0 - Math.abs(noise.get(seed, ++offsetSeed, x *= lacunarity, y *= lacunarity, z *= lacunarity))) * (double)amp;
                }
                return GeneralNoise.limit(sum * 0.5 + 0.5);
            }

            @Nonnull
            public String toString() {
                return "MultiRigidFractalFunction{}";
            }
        }),
        OLDSCHOOL(new FractalFunction(){

            @Override
            public double get(int seed, int offsetSeed, double x, double y, int octaves, double lacunarity, double persistence, @Nonnull NoiseFunction2d noise) {
                double maxAmp = 0.0;
                double amp = 1.0;
                int freq = 1;
                double sum = 0.0;
                --seed;
                for (int i = 0; i < octaves; ++i) {
                    sum += noise.get(seed, offsetSeed++, x * (double)freq, y * (double)freq) * amp;
                    maxAmp += amp;
                    amp *= persistence;
                    freq <<= 1;
                }
                sum /= maxAmp;
                return (sum *= 0.5) + 0.5;
            }

            @Override
            public double get(int seed, int offsetSeed, double x, double y, double z, int octaves, double lacunarity, double persistence, @Nonnull NoiseFunction3d noise) {
                double maxAmp = 0.0;
                double amp = 1.0;
                int freq = 1;
                double sum = 0.0;
                --seed;
                for (int i = 0; i < octaves; ++i) {
                    sum += noise.get(seed, offsetSeed++, x * (double)freq, y * (double)freq, z * (double)freq) * amp;
                    maxAmp += amp;
                    amp *= persistence;
                    freq <<= 1;
                }
                sum /= maxAmp;
                return (sum *= 0.5) + 0.5;
            }

            @Nonnull
            public String toString() {
                return "OldschoolFractalFunction{}";
            }
        }),
        MIN(new FractalFunction(){

            @Override
            public double get(int seed, int offsetSeed, double x, double y, int octaves, double lacunarity, double persistence, @Nonnull NoiseFunction2d noise) {
                double min = noise.get(seed, offsetSeed, x, y);
                for (int i = 0; i < octaves; ++i) {
                    double d;
                    if (!((d = noise.get(seed, ++offsetSeed, x *= lacunarity, y *= lacunarity)) < min)) continue;
                    min = d;
                }
                return GeneralNoise.limit(min * 0.5 + 0.5);
            }

            @Override
            public double get(int seed, int offsetSeed, double x, double y, double z, int octaves, double lacunarity, double persistence, @Nonnull NoiseFunction3d noise) {
                double min = noise.get(seed, offsetSeed, x, y, z);
                for (int i = 0; i < octaves; ++i) {
                    double d;
                    if (!((d = noise.get(seed, ++offsetSeed, x *= lacunarity, y *= lacunarity, z)) < min)) continue;
                    min = d;
                }
                return GeneralNoise.limit(min * 0.5 + 0.5);
            }

            @Nonnull
            public String toString() {
                return "MinFractalFunction{}";
            }
        }),
        MAX(new FractalFunction(){

            @Override
            public double get(int seed, int offsetSeed, double x, double y, int octaves, double lacunarity, double persistence, @Nonnull NoiseFunction2d noise) {
                double max = noise.get(seed, offsetSeed, x, y);
                for (int i = 0; i < octaves; ++i) {
                    double d;
                    if (!((d = noise.get(seed, ++offsetSeed, x *= lacunarity, y *= lacunarity)) > max)) continue;
                    max = d;
                }
                return GeneralNoise.limit(max * 0.5 + 0.5);
            }

            @Override
            public double get(int seed, int offsetSeed, double x, double y, double z, int octaves, double lacunarity, double persistence, @Nonnull NoiseFunction3d noise) {
                double max = noise.get(seed, offsetSeed, x, y, z);
                for (int i = 0; i < octaves; ++i) {
                    double d;
                    if (!((d = noise.get(seed, ++offsetSeed, x *= lacunarity, y *= lacunarity, z)) > max)) continue;
                    max = d;
                }
                return GeneralNoise.limit(max * 0.5 + 0.5);
            }

            @Nonnull
            public String toString() {
                return "MaxFractalFunction{}";
            }
        });

        private final FractalFunction function;

        private FractalMode(FractalFunction function) {
            this.function = function;
        }

        public FractalFunction getFunction() {
            return this.function;
        }
    }
}

