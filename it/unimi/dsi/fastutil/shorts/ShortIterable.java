/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import it.unimi.dsi.fastutil.shorts.ShortConsumer;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterators;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface ShortIterable
extends Iterable<Short> {
    public ShortIterator iterator();

    default public IntIterator intIterator() {
        return IntIterators.wrap(this.iterator());
    }

    default public ShortSpliterator spliterator() {
        return ShortSpliterators.asSpliteratorUnknownSize(this.iterator(), 0);
    }

    default public IntSpliterator intSpliterator() {
        return IntSpliterators.wrap(this.spliterator());
    }

    default public void forEach(ShortConsumer action) {
        Objects.requireNonNull(action);
        this.iterator().forEachRemaining(action);
    }

    default public void forEach(IntConsumer action) {
        Objects.requireNonNull(action);
        this.forEach(action instanceof ShortConsumer ? (ShortConsumer)action : action::accept);
    }

    @Override
    @Deprecated
    default public void forEach(Consumer<? super Short> action) {
        Objects.requireNonNull(action);
        this.forEach(action instanceof ShortConsumer ? (ShortConsumer)action : action::accept);
    }
}

