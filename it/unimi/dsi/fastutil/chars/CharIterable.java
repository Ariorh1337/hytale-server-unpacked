/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharConsumer;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSpliterator;
import it.unimi.dsi.fastutil.chars.CharSpliterators;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface CharIterable
extends Iterable<Character> {
    public CharIterator iterator();

    default public IntIterator intIterator() {
        return IntIterators.wrap(this.iterator());
    }

    default public CharSpliterator spliterator() {
        return CharSpliterators.asSpliteratorUnknownSize(this.iterator(), 0);
    }

    default public IntSpliterator intSpliterator() {
        return IntSpliterators.wrap(this.spliterator());
    }

    default public void forEach(CharConsumer action) {
        Objects.requireNonNull(action);
        this.iterator().forEachRemaining(action);
    }

    default public void forEach(IntConsumer action) {
        Objects.requireNonNull(action);
        this.forEach(action instanceof CharConsumer ? (CharConsumer)action : action::accept);
    }

    @Override
    @Deprecated
    default public void forEach(Consumer<? super Character> action) {
        Objects.requireNonNull(action);
        this.forEach(action instanceof CharConsumer ? (CharConsumer)action : action::accept);
    }
}

