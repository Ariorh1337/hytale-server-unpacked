/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongConsumer;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSpliterator;
import it.unimi.dsi.fastutil.longs.LongSpliterators;
import java.util.Objects;
import java.util.function.Consumer;

public interface LongIterable
extends Iterable<Long> {
    public LongIterator iterator();

    default public LongIterator longIterator() {
        return this.iterator();
    }

    default public LongSpliterator spliterator() {
        return LongSpliterators.asSpliteratorUnknownSize(this.iterator(), 0);
    }

    default public LongSpliterator longSpliterator() {
        return this.spliterator();
    }

    default public void forEach(java.util.function.LongConsumer action) {
        Objects.requireNonNull(action);
        this.iterator().forEachRemaining(action);
    }

    default public void forEach(LongConsumer action) {
        this.forEach((java.util.function.LongConsumer)action);
    }

    @Override
    @Deprecated
    default public void forEach(Consumer<? super Long> action) {
        Objects.requireNonNull(action);
        this.forEach(action instanceof java.util.function.LongConsumer ? (java.util.function.LongConsumer)((Object)action) : action::accept);
    }
}

