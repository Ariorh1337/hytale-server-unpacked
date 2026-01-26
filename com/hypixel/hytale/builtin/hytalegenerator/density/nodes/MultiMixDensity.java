/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.density.nodes;

import com.hypixel.hytale.builtin.hytalegenerator.ArrayUtil;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.framework.math.Interpolation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import javax.annotation.Nonnull;

public class MultiMixDensity
extends Density {
    @Nonnull
    private final List<Segment> segments;
    private final double min;
    private final double max;
    private final Density firstDensity;
    private final Density lastDensity;
    private Density influenceDensity;

    public MultiMixDensity(@Nonnull List<Key> keys, @Nonnull Density influenceDensity) {
        if (keys.size() < 2) {
            throw new IllegalArgumentException("must have at least two keys");
        }
        keys.sort(Comparator.comparingDouble(element -> element.value));
        if (!MultiMixDensity.isKeysUnique(keys)) {
            throw new IllegalArgumentException("Duplicate keys provided.");
        }
        this.segments = new ArrayList<Segment>(keys.size() - 1);
        for (int i = 1; i < keys.size(); ++i) {
            Key key0 = keys.get(i - 1);
            Key key1 = keys.get(i);
            this.segments.add(new Segment(key0, key1));
        }
        this.min = keys.getFirst().value;
        this.max = keys.getLast().value;
        this.firstDensity = keys.getFirst().density;
        this.lastDensity = keys.getLast().density;
        this.influenceDensity = influenceDensity;
    }

    @Override
    public double process(@Nonnull Density.Context context) {
        double influence = this.influenceDensity.process(context);
        if (influence <= this.min) {
            return this.firstDensity.process(context);
        }
        if (influence >= this.max) {
            return this.lastDensity.process(context);
        }
        int index = ArrayUtil.sortedSearch(this.segments, influence, Segment.GaugeSegmentComparator.INSTANCE);
        if (index == -1) {
            assert (false) : "should never get here";
            return 0.0;
        }
        return this.segments.get(index).getValue(context, influence);
    }

    @Override
    public void setInputs(@Nonnull Density[] inputs) {
        if (inputs.length != 1) {
            throw new IllegalArgumentException("inputs.length != 1");
        }
        this.influenceDensity = inputs[0];
    }

    public static boolean isKeysUnique(@Nonnull List<Key> keys) {
        for (int i = 1; i < keys.size(); ++i) {
            if (keys.get((int)i).value != keys.get((int)(i - 1)).value) continue;
            return false;
        }
        return true;
    }

    public record Key(double value, Density density) {
    }

    private static class Segment {
        @Nonnull
        private final Key key0;
        @Nonnull
        private final Key key1;
        private final double magnitude;

        public Segment(@Nonnull Key key0, @Nonnull Key key1) {
            assert (key0.value < key1.value) : "key0 should be smaller than key1";
            this.key0 = key0;
            this.key1 = key1;
            this.magnitude = key1.value - key0.value;
        }

        public boolean contains(double gauge) {
            return gauge >= this.key0.value && gauge <= this.key1.value;
        }

        public double getValue(@Nonnull Density.Context context, double gauge) {
            assert (gauge >= this.key0.value && gauge <= this.key1.value) : "mix outside range";
            double THRESHOLD_INPUT_0 = 0.0;
            double THRESHOLD_INPUT_1 = 1.0;
            double weight = (gauge - this.key0.value) / this.magnitude;
            if (weight == 0.0) {
                return this.key0.density.process(context);
            }
            if (weight == 1.0) {
                return this.key1.density.process(context);
            }
            if (this.key0.density == this.key1.density) {
                return this.key0.density.process(context);
            }
            double value0 = this.key0.density.process(context);
            double value1 = this.key1.density.process(context);
            return Interpolation.linear(value0, value1, weight);
        }

        public static class GaugeSegmentComparator
        implements BiFunction<Double, Segment, Integer> {
            public static final GaugeSegmentComparator INSTANCE = new GaugeSegmentComparator();

            @Override
            @Nonnull
            public Integer apply(Double gauge, @Nonnull Segment segment) {
                if (gauge < segment.key0.value) {
                    return -1;
                }
                if (gauge >= segment.key1.value) {
                    return 1;
                }
                return 0;
            }
        }
    }
}

