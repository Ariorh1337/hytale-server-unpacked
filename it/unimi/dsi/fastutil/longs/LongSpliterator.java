/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongConsumer;
import java.util.Spliterator;
import java.util.function.Consumer;

public interface LongSpliterator
extends Spliterator.OfLong {
    @Override
    @Deprecated
    default public boolean tryAdvance(Consumer<? super Long> action) {
        return this.tryAdvance(action instanceof java.util.function.LongConsumer ? (java.util.function.LongConsumer)((Object)action) : action::accept);
    }

    @Override
    default public boolean tryAdvance(LongConsumer action) {
        return this.tryAdvance((java.util.function.LongConsumer)action);
    }

    @Override
    @Deprecated
    default public void forEachRemaining(Consumer<? super Long> action) {
        this.forEachRemaining(action instanceof java.util.function.LongConsumer ? (java.util.function.LongConsumer)((Object)action) : action::accept);
    }

    @Override
    default public void forEachRemaining(LongConsumer action) {
        this.forEachRemaining((java.util.function.LongConsumer)action);
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
    public LongSpliterator trySplit();

    default public LongComparator getComparator() {
        throw new IllegalStateException();
    }
}

