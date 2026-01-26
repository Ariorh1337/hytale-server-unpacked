/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.DoubleConsumer;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
import java.util.Objects;
import java.util.function.Consumer;

public interface DoubleIterable
extends Iterable<Double> {
    public DoubleIterator iterator();

    default public DoubleIterator doubleIterator() {
        return this.iterator();
    }

    default public DoubleSpliterator spliterator() {
        return DoubleSpliterators.asSpliteratorUnknownSize(this.iterator(), 0);
    }

    default public DoubleSpliterator doubleSpliterator() {
        return this.spliterator();
    }

    default public void forEach(java.util.function.DoubleConsumer action) {
        Objects.requireNonNull(action);
        this.iterator().forEachRemaining(action);
    }

    default public void forEach(DoubleConsumer action) {
        this.forEach((java.util.function.DoubleConsumer)action);
    }

    @Override
    @Deprecated
    default public void forEach(Consumer<? super Double> action) {
        Objects.requireNonNull(action);
        this.forEach(action instanceof java.util.function.DoubleConsumer ? (java.util.function.DoubleConsumer)((Object)action) : action::accept);
    }
}

