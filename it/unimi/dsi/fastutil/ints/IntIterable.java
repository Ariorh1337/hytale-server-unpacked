/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.IntConsumer;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import java.util.Objects;
import java.util.function.Consumer;

public interface IntIterable
extends Iterable<Integer> {
    public IntIterator iterator();

    default public IntIterator intIterator() {
        return this.iterator();
    }

    default public IntSpliterator spliterator() {
        return IntSpliterators.asSpliteratorUnknownSize(this.iterator(), 0);
    }

    default public IntSpliterator intSpliterator() {
        return this.spliterator();
    }

    default public void forEach(java.util.function.IntConsumer action) {
        Objects.requireNonNull(action);
        this.iterator().forEachRemaining(action);
    }

    default public void forEach(IntConsumer action) {
        this.forEach((java.util.function.IntConsumer)action);
    }

    @Override
    @Deprecated
    default public void forEach(Consumer<? super Integer> action) {
        Objects.requireNonNull(action);
        this.forEach(action instanceof java.util.function.IntConsumer ? (java.util.function.IntConsumer)((Object)action) : action::accept);
    }
}

