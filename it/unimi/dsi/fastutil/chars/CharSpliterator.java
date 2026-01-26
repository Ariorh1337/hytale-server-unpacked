/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.chars.CharConsumer;
import java.util.Spliterator;
import java.util.function.Consumer;

public interface CharSpliterator
extends Spliterator.OfPrimitive<Character, CharConsumer, CharSpliterator> {
    @Override
    @Deprecated
    default public boolean tryAdvance(Consumer<? super Character> action) {
        return this.tryAdvance(action instanceof CharConsumer ? (CharConsumer)action : action::accept);
    }

    @Override
    @Deprecated
    default public void forEachRemaining(Consumer<? super Character> action) {
        this.forEachRemaining(action instanceof CharConsumer ? (CharConsumer)action : action::accept);
    }

    default public long skip(long n) {
        if (n < 0L) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
        }
        long i = n;
        while (i-- != 0L && this.tryAdvance(unused -> {})) {
        }
        return n - i - 1L;
    }

    @Override
    public CharSpliterator trySplit();

    default public CharComparator getComparator() {
        throw new IllegalStateException();
    }
}

