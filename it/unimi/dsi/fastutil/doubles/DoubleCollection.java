/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.doubles.DoubleIterable;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoublePredicate;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface DoubleCollection
extends Collection<Double>,
DoubleIterable {
    @Override
    public DoubleIterator iterator();

    @Override
    default public DoubleIterator doubleIterator() {
        return this.iterator();
    }

    @Override
    default public DoubleSpliterator spliterator() {
        return DoubleSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 320);
    }

    @Override
    default public DoubleSpliterator doubleSpliterator() {
        return this.spliterator();
    }

    @Override
    public boolean add(double var1);

    public boolean contains(double var1);

    public boolean rem(double var1);

    @Override
    @Deprecated
    default public boolean add(Double key) {
        return this.add((double)key);
    }

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        if (key == null) {
            return false;
        }
        return this.contains((Double)key);
    }

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        if (key == null) {
            return false;
        }
        return this.rem((Double)key);
    }

    public double[] toDoubleArray();

    @Deprecated
    default public double[] toDoubleArray(double[] a) {
        return this.toArray(a);
    }

    public double[] toArray(double[] var1);

    public boolean addAll(DoubleCollection var1);

    public boolean containsAll(DoubleCollection var1);

    public boolean removeAll(DoubleCollection var1);

    @Override
    @Deprecated
    default public boolean removeIf(Predicate<? super Double> filter) {
        return this.removeIf(filter instanceof java.util.function.DoublePredicate ? (java.util.function.DoublePredicate)((Object)filter) : key -> filter.test(key));
    }

    default public boolean removeIf(java.util.function.DoublePredicate filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        DoubleIterator each = this.iterator();
        while (each.hasNext()) {
            if (!filter.test(each.nextDouble())) continue;
            each.remove();
            removed = true;
        }
        return removed;
    }

    default public boolean removeIf(DoublePredicate filter) {
        return this.removeIf((java.util.function.DoublePredicate)filter);
    }

    public boolean retainAll(DoubleCollection var1);

    @Override
    @Deprecated
    default public Stream<Double> stream() {
        return Collection.super.stream();
    }

    default public DoubleStream doubleStream() {
        return StreamSupport.doubleStream(this.doubleSpliterator(), false);
    }

    @Override
    @Deprecated
    default public Stream<Double> parallelStream() {
        return Collection.super.parallelStream();
    }

    default public DoubleStream doubleParallelStream() {
        return StreamSupport.doubleStream(this.doubleSpliterator(), true);
    }
}

