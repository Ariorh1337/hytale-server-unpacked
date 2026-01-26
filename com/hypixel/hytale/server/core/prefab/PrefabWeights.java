/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.prefab;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.Object2DoubleMapCodec;
import com.hypixel.hytale.codec.validation.LegacyValidator;
import com.hypixel.hytale.codec.validation.ValidationResults;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.common.util.ArrayUtil;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMaps;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrefabWeights {
    public static final Supplier<Object2DoubleMap<String>> MAP_SUPPLIER = Object2DoubleOpenHashMap::new;
    public static final Codec<Object2DoubleMap<String>> MAP_CODEC = new Object2DoubleMapCodec<String>(Codec.STRING, MAP_SUPPLIER, false);
    public static final Codec<PrefabWeights> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(PrefabWeights.class, PrefabWeights::new).append(new KeyedCodec<Double>("Default", Codec.DOUBLE), (weights, def) -> {
        weights.defaultWeight = def;
    }, weights -> weights.defaultWeight).documentation("The default weight to use for entries that are not specifically mapped to a weight value.").addValidator(Validators.greaterThanOrEqual(0.0)).add()).append(new KeyedCodec<Object2DoubleMap<String>>("Weights", MAP_CODEC), (weights, map) -> {
        weights.weightsLookup = map;
    }, weights -> weights.weightsLookup).documentation("The mapping of prefab names to weight values.").addValidator(new WeightMapValidator()).add()).build();
    public static final PrefabWeights NONE = new PrefabWeights(Object2DoubleMaps.emptyMap()){
        {
            this.sum = 0.0;
            this.weights = ArrayUtil.EMPTY_DOUBLE_ARRAY;
            this.initialized = true;
        }
    };
    public static final double DEFAULT_WEIGHT = 1.0;
    public static final char DELIMITER_CHAR = ',';
    public static final char ASSIGNMENT_CHAR = '=';
    private double defaultWeight;
    private Object2DoubleMap<String> weightsLookup;
    protected double sum;
    protected double[] weights;
    protected volatile boolean initialized;

    public PrefabWeights() {
        this(MAP_SUPPLIER.get());
    }

    private PrefabWeights(Object2DoubleMap<String> weights) {
        this.weightsLookup = weights;
        this.defaultWeight = 1.0;
    }

    public int size() {
        return this.weightsLookup.size();
    }

    @Nullable
    public <T> T get(@Nonnull T[] elements, @Nonnull Function<T, String> nameFunc, @Nonnull Random random) {
        return this.get(elements, nameFunc, random.nextDouble());
    }

    @Nullable
    public <T> T get(@Nonnull T[] elements, @Nonnull Function<T, String> nameFunc, double value) {
        if (value < 0.0) {
            return null;
        }
        this.initialize(elements, nameFunc);
        if (this.weights.length != elements.length) {
            return null;
        }
        double weightedValue = Math.min(value, 0.99999) * this.sum;
        for (int i = 0; i < this.weights.length; ++i) {
            if (!(weightedValue <= this.weights[i])) continue;
            return elements[i];
        }
        return null;
    }

    public double getWeight(String prefab) {
        return this.weightsLookup.getOrDefault((Object)prefab, this.defaultWeight);
    }

    public void setWeight(String prefab, double weight) {
        if (this == NONE) {
            return;
        }
        PrefabWeights.checkWeight(prefab, weight);
        this.weightsLookup.put(prefab, weight);
    }

    public void removeWeight(String prefab) {
        if (this == NONE) {
            return;
        }
        this.weightsLookup.removeDouble(prefab);
    }

    public double getDefaultWeight() {
        return this.defaultWeight;
    }

    public void setDefaultWeight(double defaultWeight) {
        if (this == NONE) {
            return;
        }
        this.defaultWeight = Math.max(0.0, defaultWeight);
    }

    @Nonnull
    public String getMappingString() {
        if (this.weightsLookup.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object2DoubleMap.Entry entry : Object2DoubleMaps.fastIterable(this.weightsLookup)) {
            if (!sb.isEmpty()) {
                sb.append(',').append(' ');
            }
            sb.append((String)entry.getKey()).append('=').append(entry.getDoubleValue());
        }
        return sb.toString();
    }

    @Nonnull
    public String toString() {
        return "PrefabWeights{default=" + this.defaultWeight + ", weights=" + this.getMappingString() + "}";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private <T> void initialize(@Nonnull T[] elements, @Nonnull Function<T, String> nameFunc) {
        if (this.initialized) {
            return;
        }
        PrefabWeights prefabWeights = this;
        synchronized (prefabWeights) {
            if (this.initialized) {
                return;
            }
            double sum = 0.0;
            double[] weights = new double[elements.length];
            for (int i = 0; i < elements.length; ++i) {
                String name = nameFunc.apply(elements[i]);
                weights[i] = sum += this.getWeight(name);
            }
            this.sum = sum;
            this.weights = weights;
            this.initialized = true;
        }
    }

    @Nonnull
    public static PrefabWeights parse(@Nonnull String mappingString) {
        Object2DoubleMap<String> map = null;
        for (int startPoint = 0; startPoint < mappingString.length(); ++startPoint) {
            int equalsPoint;
            int endPoint = mappingString.indexOf(44, startPoint);
            if (endPoint == -1) {
                endPoint = mappingString.length();
            }
            if ((equalsPoint = mappingString.indexOf(61, startPoint)) <= startPoint) break;
            String name = mappingString.substring(startPoint, equalsPoint).trim();
            String value = mappingString.substring(equalsPoint + 1, endPoint).trim();
            double weight = Double.parseDouble(value);
            if (map == null) {
                map = MAP_SUPPLIER.get();
            }
            map.put(name, weight);
            startPoint = endPoint;
        }
        if (map == null) {
            return NONE;
        }
        return new PrefabWeights(map);
    }

    public Set<Object2DoubleMap.Entry<String>> entrySet() {
        return this.weightsLookup.object2DoubleEntrySet();
    }

    private static void checkWeight(String prefab, double weight) {
        if (weight < 0.0) {
            throw new IllegalArgumentException(String.format("Negative weight %.5f assigned to prefab %s", weight, prefab));
        }
    }

    private static class WeightMapValidator
    implements LegacyValidator<Object2DoubleMap<String>> {
        private WeightMapValidator() {
        }

        @Override
        public void accept(@Nonnull Object2DoubleMap<String> stringObject2DoubleMap, ValidationResults results) {
            for (Object2DoubleMap.Entry entry : Object2DoubleMaps.fastIterable(stringObject2DoubleMap)) {
                PrefabWeights.checkWeight((String)entry.getKey(), entry.getDoubleValue());
            }
        }
    }
}

