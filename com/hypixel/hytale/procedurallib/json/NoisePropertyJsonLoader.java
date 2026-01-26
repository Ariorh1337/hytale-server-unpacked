/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.hypixel.hytale.procedurallib.NoiseFunction;
import com.hypixel.hytale.procedurallib.json.BlendNoisePropertyJsonLoader;
import com.hypixel.hytale.procedurallib.json.CoordinateRandomizerJsonLoader;
import com.hypixel.hytale.procedurallib.json.CoordinateRotatorJsonLoader;
import com.hypixel.hytale.procedurallib.json.CurveNoisePropertyJsonLoader;
import com.hypixel.hytale.procedurallib.json.DoubleRangeJsonLoader;
import com.hypixel.hytale.procedurallib.json.GradientNoisePropertyJsonLoader;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.NoiseFunctionJsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.property.DistortedNoiseProperty;
import com.hypixel.hytale.procedurallib.property.FractalNoiseProperty;
import com.hypixel.hytale.procedurallib.property.InvertNoiseProperty;
import com.hypixel.hytale.procedurallib.property.MaxNoiseProperty;
import com.hypixel.hytale.procedurallib.property.MinNoiseProperty;
import com.hypixel.hytale.procedurallib.property.MultiplyNoiseProperty;
import com.hypixel.hytale.procedurallib.property.NoiseFormulaProperty;
import com.hypixel.hytale.procedurallib.property.NoiseProperty;
import com.hypixel.hytale.procedurallib.property.NoisePropertyType;
import com.hypixel.hytale.procedurallib.property.NormalizeNoiseProperty;
import com.hypixel.hytale.procedurallib.property.OffsetNoiseProperty;
import com.hypixel.hytale.procedurallib.property.RotateNoiseProperty;
import com.hypixel.hytale.procedurallib.property.ScaleNoiseProperty;
import com.hypixel.hytale.procedurallib.property.SingleNoiseProperty;
import com.hypixel.hytale.procedurallib.property.SumNoiseProperty;
import com.hypixel.hytale.procedurallib.random.CoordinateRotator;
import com.hypixel.hytale.procedurallib.supplier.IDoubleRange;
import java.nio.file.Path;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NoisePropertyJsonLoader<K extends SeedResource>
extends JsonLoader<K, NoiseProperty> {
    public NoisePropertyJsonLoader(@Nonnull SeedString<K> seed, Path dataFolder, JsonElement json) {
        super(seed.append(".NoiseProperty"), dataFolder, json);
    }

    @Override
    @Nonnull
    public NoiseProperty load() {
        NoiseProperty noiseProperty;
        NoisePropertyType type;
        block45: {
            block44: {
                type = null;
                if (!this.has("Type")) break block44;
                type = NoisePropertyType.valueOf(this.get("Type").getAsString());
                switch (type) {
                    case MAX: {
                        if (!this.has("Noise")) {
                            throw new IllegalStateException("Could not find noise map data. Keyword: Noise");
                        }
                        NoiseProperty[] noiseProperties = this.loadNoiseProperties(this.get("Noise"));
                        noiseProperty = new MaxNoiseProperty(noiseProperties);
                        break block45;
                    }
                    case MIN: {
                        if (!this.has("Noise")) {
                            throw new IllegalStateException("Could not find noise map data. Keyword: Noise");
                        }
                        NoiseProperty[] noiseProperties = this.loadNoiseProperties(this.get("Noise"));
                        noiseProperty = new MinNoiseProperty(noiseProperties);
                        break block45;
                    }
                    case SUM: {
                        if (!this.has("Noise")) {
                            throw new IllegalStateException("Could not find noise map data. Keyword: Noise");
                        }
                        if (!this.has("Factors")) {
                            throw new IllegalStateException("Could not find factors for sum composed noise map. Keyword: Factors");
                        }
                        NoiseProperty[] noiseProperties = this.loadNoiseProperties(this.get("Noise"));
                        double[] factors = this.loadDoubleArray(this.get("Factors"), noiseProperties.length);
                        SumNoiseProperty.Entry[] entries = new SumNoiseProperty.Entry[noiseProperties.length];
                        for (int i = 0; i < entries.length; ++i) {
                            entries[i] = new SumNoiseProperty.Entry(noiseProperties[i], factors[i]);
                        }
                        noiseProperty = new SumNoiseProperty(entries);
                        break block45;
                    }
                    case SCALE: {
                        if (!this.has("Noise")) {
                            throw new IllegalStateException("Could not find noise map data. Keyword: Noise");
                        }
                        if (!this.has("Scale")) {
                            throw new IllegalStateException("Could not find scale data for scaled noise map. Keyword: Scale");
                        }
                        NoiseProperty noise = new NoisePropertyJsonLoader<K>(this.seed, this.dataFolder, this.get("Noise")).load();
                        double scale = this.get("Scale").getAsDouble();
                        noiseProperty = new ScaleNoiseProperty(noise, scale);
                        break block45;
                    }
                    case FORMULA: {
                        if (!this.has("Noise")) {
                            throw new IllegalStateException("Could not find noise map data. Keyword: Noise");
                        }
                        if (!this.has("Formula")) {
                            throw new IllegalStateException("Could not find formula type for noise map. Keyword: Formula");
                        }
                        NoiseProperty noise = new NoisePropertyJsonLoader<K>(this.seed, this.dataFolder, this.get("Noise")).load();
                        NoiseFormulaProperty.NoiseFormula noiseFormula = NoiseFormulaProperty.NoiseFormula.valueOf(this.get("Formula").getAsString());
                        noiseProperty = new NoiseFormulaProperty(noise, noiseFormula.getFormula());
                        break block45;
                    }
                    case MULTIPLY: {
                        if (!this.has("Noise")) {
                            throw new IllegalStateException("Could not find noise map data. Keyword: Noise");
                        }
                        NoiseProperty[] noiseProperties = this.loadNoiseProperties(this.get("Noise"));
                        noiseProperty = new MultiplyNoiseProperty(noiseProperties);
                        break block45;
                    }
                    case DISTORTED: {
                        if (!this.has("Noise")) {
                            throw new IllegalStateException("Could not find noise map data. Keyword: Noise");
                        }
                        if (!this.has("Randomizer")) {
                            throw new IllegalStateException("Could not find randomizer for distorted noise map. Keyword: Randomizer");
                        }
                        NoiseProperty noise = new NoisePropertyJsonLoader<K>(this.seed, this.dataFolder, this.get("Noise")).load();
                        noiseProperty = new DistortedNoiseProperty(noise, new CoordinateRandomizerJsonLoader(this.seed, this.dataFolder, this.get("Randomizer")).load());
                        break block45;
                    }
                    case NORMALIZE: {
                        if (!this.has("Noise")) {
                            throw new IllegalStateException("Could not find noise map data. Keyword: Noise");
                        }
                        if (!this.has("Range")) {
                            throw new IllegalStateException("Could not find range data for normalized noise map. Keyword: Range");
                        }
                        NoiseProperty noise = new NoisePropertyJsonLoader<K>(this.seed, this.dataFolder, this.get("Noise")).load();
                        IDoubleRange range = new DoubleRangeJsonLoader(this.seed, this.dataFolder, this.get("Range")).load();
                        noiseProperty = new NormalizeNoiseProperty(noise, range.getValue(0.0), range.getValue(1.0) - range.getValue(0.0));
                        break block45;
                    }
                    case INVERT: {
                        if (!this.has("Noise")) {
                            throw new IllegalStateException("Could not find noise map data. Keyword: Noise");
                        }
                        NoiseProperty noise = new NoisePropertyJsonLoader<K>(this.seed, this.dataFolder, this.get("Noise")).load();
                        noiseProperty = new InvertNoiseProperty(noise);
                        break block45;
                    }
                    case OFFSET: {
                        if (!this.has("Noise")) {
                            throw new IllegalStateException("Could not find noise map data. Keyword: Noise");
                        }
                        NoiseProperty noise = new NoisePropertyJsonLoader<K>(this.seed, this.dataFolder, this.get("Noise")).load();
                        double offset = this.has("Offset") ? this.get("Offset").getAsDouble() : 0.0;
                        double offsetX = this.has("OffsetX") ? this.get("OffsetX").getAsDouble() : offset;
                        double offsetY = this.has("OffsetY") ? this.get("OffsetY").getAsDouble() : offset;
                        double offsetZ = this.has("OffsetZ") ? this.get("OffsetZ").getAsDouble() : offset;
                        noiseProperty = new OffsetNoiseProperty(noise, offsetX, offsetY, offsetZ);
                        break block45;
                    }
                    case ROTATE: {
                        if (!this.has("Noise")) {
                            throw new IllegalStateException("Could not find noise map data. Keyword: Noise");
                        }
                        if (!this.has("Rotate")) {
                            throw new IllegalStateException("Could not find noise map data. Keyword: Noise");
                        }
                        NoiseProperty noise = new NoisePropertyJsonLoader<K>(this.seed, this.dataFolder, this.get("Noise")).load();
                        CoordinateRotator rotation = new CoordinateRotatorJsonLoader(this.seed, this.dataFolder, this.get("Rotate")).load();
                        noiseProperty = new RotateNoiseProperty(noise, rotation);
                        break block45;
                    }
                    case GRADIENT: {
                        if (!this.has("Noise")) {
                            throw new IllegalStateException("Could not find noise map data. Keyword: Noise");
                        }
                        NoiseProperty noise = new NoisePropertyJsonLoader<K>(this.seed, this.dataFolder, this.get("Noise")).load();
                        noiseProperty = new GradientNoisePropertyJsonLoader(this.seed, this.dataFolder, this.json, noise).load();
                        break block45;
                    }
                    case CURVE: {
                        noiseProperty = new CurveNoisePropertyJsonLoader(this.seed, this.dataFolder, this.json, null).load();
                        break block45;
                    }
                    case BLEND: {
                        noiseProperty = new BlendNoisePropertyJsonLoader(this.seed, this.dataFolder, this.json).load();
                        break block45;
                    }
                    default: {
                        throw new Error(String.format("Could not find instructions for noise property type: %s", new Object[]{type}));
                    }
                }
            }
            NoiseFunction noiseFunction = this.newNoiseFunctionJsonLoader(this.seed, this.dataFolder, this.json).load();
            if (this.has("Octaves")) {
                FractalNoiseProperty.FractalMode fractalMode = this.has("FractalMode") ? FractalNoiseProperty.FractalMode.valueOf(this.get("FractalMode").getAsString()) : Constants.DEFAULT_FRACTAL_MODE;
                int octaves = this.get("Octaves").getAsInt();
                double lacunarity = this.has("Lacunarity") ? this.get("Lacunarity").getAsDouble() : 2.0;
                double persistence = this.has("Persistence") ? this.get("Persistence").getAsDouble() : 0.5;
                noiseProperty = new FractalNoiseProperty(this.loadSeed(), noiseFunction, fractalMode.getFunction(), octaves, lacunarity, persistence);
            } else {
                noiseProperty = new SingleNoiseProperty(this.loadSeed(), noiseFunction);
            }
        }
        if (type != NoisePropertyType.FORMULA && this.has("Formula")) {
            NoiseFormulaProperty.NoiseFormula noiseFormula = NoiseFormulaProperty.NoiseFormula.valueOf(this.get("Formula").getAsString());
            noiseProperty = new NoiseFormulaProperty(noiseProperty, noiseFormula.getFormula());
        }
        if (type != NoisePropertyType.CURVE && this.has("Curve")) {
            noiseProperty = new CurveNoisePropertyJsonLoader(this.seed, this.dataFolder, this.get("Curve"), noiseProperty).load();
        }
        if (type != NoisePropertyType.SCALE && this.has("Scale")) {
            double scale = this.get("Scale").getAsDouble();
            noiseProperty = new ScaleNoiseProperty(noiseProperty, scale);
        }
        if (type != NoisePropertyType.NORMALIZE && this.has("Normalize") && type != NoisePropertyType.GRADIENT) {
            IDoubleRange range = new DoubleRangeJsonLoader(this.seed, this.dataFolder, this.get("Normalize")).load();
            noiseProperty = new NormalizeNoiseProperty(noiseProperty, range.getValue(0.0), range.getValue(1.0) - range.getValue(0.0));
        }
        if (type != NoisePropertyType.OFFSET && (this.has("Offset") || this.has("OffsetX") || this.has("OffsetY") || this.has("OffsetZ"))) {
            double offset = this.has("Offset") ? this.get("Offset").getAsDouble() : 0.0;
            double offsetX = this.has("OffsetX") ? this.get("OffsetX").getAsDouble() : offset;
            double offsetY = this.has("OffsetY") ? this.get("OffsetY").getAsDouble() : offset;
            double offsetZ = this.has("OffsetZ") ? this.get("OffsetZ").getAsDouble() : offset;
            noiseProperty = new OffsetNoiseProperty(noiseProperty, offsetX, offsetY, offsetZ);
        }
        if (type != NoisePropertyType.ROTATE && (this.has("Pitch") || this.has("Yaw"))) {
            CoordinateRotator rotation = new CoordinateRotatorJsonLoader(this.seed, this.dataFolder, this.json).load();
            noiseProperty = new RotateNoiseProperty(noiseProperty, rotation);
        }
        if (type != NoisePropertyType.GRADIENT && this.has("Gradient")) {
            noiseProperty = new GradientNoisePropertyJsonLoader(this.seed, this.dataFolder, this.get("Gradient"), noiseProperty).load();
        }
        return noiseProperty;
    }

    protected int loadSeed() {
        int seedVal = this.seed.hashCode();
        if (this.has("Seed")) {
            SeedString overwritten = this.seed.appendToOriginal(this.get("Seed").getAsString());
            seedVal = overwritten.hashCode();
            this.seed.get().reportSeeds(seedVal, this.seed.original, this.seed.seed, overwritten.seed);
        } else {
            this.seed.get().reportSeeds(seedVal, this.seed.original, this.seed.seed, null);
        }
        return seedVal;
    }

    @Nonnull
    protected NoiseProperty[] loadNoiseProperties(@Nonnull JsonElement element) {
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            NoiseProperty[] noiseProperties = new NoiseProperty[array.size()];
            for (int i = 0; i < noiseProperties.length; ++i) {
                noiseProperties[i] = new NoisePropertyJsonLoader(this.seed.append(String.format("-#%s", i)), this.dataFolder, array.get(i)).load();
            }
            return noiseProperties;
        }
        return new NoiseProperty[0];
    }

    protected double[] loadDoubleArray(@Nullable JsonElement element, int size) {
        double[] values = new double[size];
        if (element == null || element.isJsonNull()) {
            Arrays.fill(values, 1.0 / (double)size);
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (int i = 0; i < size; ++i) {
                values[i] = array.get(i).getAsDouble();
            }
        }
        return values;
    }

    @Nonnull
    protected NoiseFunctionJsonLoader newNoiseFunctionJsonLoader(@Nonnull SeedString<K> seed, Path dataFolder, JsonElement json) {
        return new NoiseFunctionJsonLoader<K>(seed, dataFolder, json);
    }

    public static interface Constants {
        public static final String KEY_SEED = "Seed";
        public static final String KEY_SUM_FACTORS = "Factors";
        public static final String KEY_NORMALIZE_RANGE = "Range";
        public static final String KEY_DISTORTED_RANDOMIZER = "Randomizer";
        public static final String KEY_TYPE = "Type";
        public static final String KEY_NOISE = "Noise";
        public static final String KEY_FRACTAL_MODE = "FractalMode";
        public static final String KEY_OCTAVES = "Octaves";
        public static final String KEY_LACUNARITY = "Lacunarity";
        public static final String KEY_PERSISTENCE = "Persistence";
        public static final String KEY_FORMULA = "Formula";
        public static final String KEY_CURVE = "Curve";
        public static final String KEY_SCALE = "Scale";
        public static final String KEY_NORMALIZE = "Normalize";
        public static final String KEY_OFFSET = "Offset";
        public static final String KEY_OFFSET_X = "OffsetX";
        public static final String KEY_OFFSET_Y = "OffsetY";
        public static final String KEY_OFFSET_Z = "OffsetZ";
        public static final String KEY_GRADIENT = "Gradient";
        public static final String ERROR_NO_NOISE = "Could not find noise map data. Keyword: Noise";
        public static final String ERROR_SUM_NO_FACTORS = "Could not find factors for sum composed noise map. Keyword: Factors";
        public static final String ERROR_NO_FORMULA = "Could not find formula type for noise map. Keyword: Formula";
        public static final String ERROR_NO_SCALE = "Could not find scale data for scaled noise map. Keyword: Scale";
        public static final String ERROR_DISTORTED_RANDOMIZER = "Could not find randomizer for distorted noise map. Keyword: Randomizer";
        public static final String ERROR_NORMALIZE_NO_RANGE = "Could not find range data for normalized noise map. Keyword: Range";
        public static final String ERROR_UNKOWN_TYPE = "Could not find instructions for noise property type: %s";
        public static final FractalNoiseProperty.FractalMode DEFAULT_FRACTAL_MODE = FractalNoiseProperty.FractalMode.FBM;
        public static final double DEFAULT_LACUNARITY = 2.0;
        public static final double DEFAULT_PERSISTENCE = 0.5;
    }
}

