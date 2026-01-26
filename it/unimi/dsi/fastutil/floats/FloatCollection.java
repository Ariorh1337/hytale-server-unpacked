/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.floats.FloatIterable;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatPredicate;
import it.unimi.dsi.fastutil.floats.FloatSpliterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterators;
import java.util.Collection;
import java.util.Objects;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface FloatCollection
extends Collection<Float>,
FloatIterable {
    @Override
    public FloatIterator iterator();

    @Override
    default public DoubleIterator doubleIterator() {
        return FloatIterable.super.doubleIterator();
    }

    @Override
    default public FloatSpliterator spliterator() {
        return FloatSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 320);
    }

    @Override
    default public DoubleSpliterator doubleSpliterator() {
        return FloatIterable.super.doubleSpliterator();
    }

    @Override
    public boolean add(float var1);

    public boolean contains(float var1);

    public boolean rem(float var1);

    @Override
    @Deprecated
    default public boolean add(Float key) {
        return this.add(key.floatValue());
    }

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        if (key == null) {
            return false;
        }
        return this.contains(((Float)key).floatValue());
    }

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        if (key == null) {
            return false;
        }
        return this.rem(((Float)key).floatValue());
    }

    public float[] toFloatArray();

    @Deprecated
    default public float[] toFloatArray(float[] a) {
        return this.toArray(a);
    }

    public float[] toArray(float[] var1);

    public boolean addAll(FloatCollection var1);

    public boolean containsAll(FloatCollection var1);

    public boolean removeAll(FloatCollection var1);

    @Override
    @Deprecated
    default public boolean removeIf(Predicate<? super Float> filter) {
        return this.removeIf(filter instanceof FloatPredicate ? (FloatPredicate)filter : key -> filter.test(Float.valueOf(SafeMath.safeDoubleToFloat(key))));
    }

    default public boolean removeIf(FloatPredicate filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        FloatIterator each = this.iterator();
        while (each.hasNext()) {
            if (!filter.test(each.nextFloat())) continue;
            each.remove();
            removed = true;
        }
        return removed;
    }

    default public boolean removeIf(DoublePredicate filter) {
        return this.removeIf(filter instanceof FloatPredicate ? (FloatPredicate)filter : filter::test);
    }

    public boolean retainAll(FloatCollection var1);

    @Override
    @Deprecated
    default public Stream<Float> stream() {
        return Collection.super.stream();
    }

    default public DoubleStream doubleStream() {
        return StreamSupport.doubleStream(this.doubleSpliterator(), false);
    }

    @Override
    @Deprecated
    default public Stream<Float> parallelStream() {
        return Collection.super.parallelStream();
    }

    default public DoubleStream doubleParallelStream() {
        return StreamSupport.doubleStream(this.doubleSpliterator(), true);
    }
}

