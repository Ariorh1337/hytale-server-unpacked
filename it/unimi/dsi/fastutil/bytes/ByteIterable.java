/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteConsumer;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterators;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface ByteIterable
extends Iterable<Byte> {
    public ByteIterator iterator();

    default public IntIterator intIterator() {
        return IntIterators.wrap(this.iterator());
    }

    default public ByteSpliterator spliterator() {
        return ByteSpliterators.asSpliteratorUnknownSize(this.iterator(), 0);
    }

    default public IntSpliterator intSpliterator() {
        return IntSpliterators.wrap(this.spliterator());
    }

    default public void forEach(ByteConsumer action) {
        Objects.requireNonNull(action);
        this.iterator().forEachRemaining(action);
    }

    default public void forEach(IntConsumer action) {
        Objects.requireNonNull(action);
        this.forEach(action instanceof ByteConsumer ? (ByteConsumer)action : action::accept);
    }

    @Override
    @Deprecated
    default public void forEach(Consumer<? super Byte> action) {
        Objects.requireNonNull(action);
        this.forEach(action instanceof ByteConsumer ? (ByteConsumer)action : action::accept);
    }
}

