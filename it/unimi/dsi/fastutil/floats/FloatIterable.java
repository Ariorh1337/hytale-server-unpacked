/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleIterators;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterators;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public interface FloatIterable
extends Iterable<Float> {
    public FloatIterator iterator();

    default public DoubleIterator doubleIterator() {
        return DoubleIterators.wrap(this.iterator());
    }

    default public FloatSpliterator spliterator() {
        return FloatSpliterators.asSpliteratorUnknownSize(this.iterator(), 0);
    }

    default public DoubleSpliterator doubleSpliterator() {
        return DoubleSpliterators.wrap(this.spliterator());
    }

    default public void forEach(FloatConsumer action) {
        Objects.requireNonNull(action);
        this.iterator().forEachRemaining(action);
    }

    default public void forEach(DoubleConsumer action) {
        Objects.requireNonNull(action);
        this.forEach(action instanceof FloatConsumer ? (FloatConsumer)action : action::accept);
    }

    @Override
    @Deprecated
    default public void forEach(Consumer<? super Float> action) {
        Objects.requireNonNull(action);
        this.forEach(action instanceof FloatConsumer ? (FloatConsumer)action : action::accept);
    }
}

