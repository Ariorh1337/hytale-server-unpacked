/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.datastructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;

public class WeightedMap<T> {
    @Nonnull
    private final Set<T> elementSet;
    @Nonnull
    private final List<T> elements;
    @Nonnull
    private final List<Double> weights;
    @Nonnull
    private final Map<T, Integer> indices;
    private double totalWeight = 0.0;
    private boolean immutable = false;

    public WeightedMap(@Nonnull WeightedMap<T> other) {
        this.totalWeight = other.totalWeight;
        this.elementSet = new HashSet<T>(other.elementSet);
        this.elements = new ArrayList<T>(other.elements);
        this.weights = new ArrayList<Double>(other.weights);
        this.indices = new HashMap<T, Integer>(other.indices);
        this.immutable = other.immutable;
    }

    public WeightedMap() {
        this(2);
    }

    public WeightedMap(int initialCapacity) {
        this.elementSet = new HashSet<T>(initialCapacity);
        this.elements = new ArrayList<T>(initialCapacity);
        this.weights = new ArrayList<Double>(initialCapacity);
        this.indices = new HashMap<T, Integer>(initialCapacity);
    }

    @Nonnull
    public WeightedMap<T> add(@Nonnull T element, double weight) {
        if (element == null) {
            throw new NullPointerException();
        }
        if (this.immutable) {
            throw new IllegalStateException("method can't be called when object is immutable");
        }
        if (weight < 0.0) {
            throw new IllegalArgumentException("weight must be positive");
        }
        this.elements.add(element);
        this.weights.add(weight);
        this.elementSet.add(element);
        this.totalWeight += weight;
        this.indices.put(element, this.indices.size());
        return this;
    }

    public double get(@Nonnull T element) {
        if (element == null) {
            throw new NullPointerException();
        }
        if (this.immutable) {
            throw new IllegalStateException("method can't be called when object is immutable");
        }
        if (!this.elementSet.contains(element)) {
            return 0.0;
        }
        return this.weights.get(this.indices.get(element));
    }

    public T pick(@Nonnull Random rand) {
        if (rand == null) {
            throw new NullPointerException();
        }
        if (this.elements.isEmpty()) {
            throw new IllegalStateException("can't be empty when calling this method");
        }
        double pointer = rand.nextDouble() * this.totalWeight;
        for (int i = 0; i < this.elements.size(); ++i) {
            if (!((pointer -= this.weights.get(i).doubleValue()) <= 0.0)) continue;
            return this.elements.get(i);
        }
        return this.elements.getLast();
    }

    public int size() {
        return this.elements.size();
    }

    @Nonnull
    public List<T> allElements() {
        return new ArrayList<T>(this.elements);
    }

    public void makeImmutable() {
        this.immutable = true;
    }

    public boolean isImmutable() {
        return this.immutable;
    }

    public void forEach(@Nonnull BiConsumer<T, Double> consumer) {
        for (int i = 0; i < this.elements.size(); ++i) {
            consumer.accept(this.elements.get(i), this.weights.get(i));
        }
    }

    @Nonnull
    public String toString() {
        return "WeighedMap{elementSet=" + String.valueOf(this.elementSet) + ", elements=" + String.valueOf(this.elements) + ", weights=" + String.valueOf(this.weights) + ", indices=" + String.valueOf(this.indices) + ", totalWeight=" + this.totalWeight + ", immutable=" + this.immutable + "}";
    }
}

