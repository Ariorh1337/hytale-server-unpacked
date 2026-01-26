/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleConsumer;
import java.util.Spliterator;
import java.util.function.Consumer;

public interface DoubleSpliterator
extends Spliterator.OfDouble {
    @Override
    @Deprecated
    default public boolean tryAdvance(Consumer<? super Double> action) {
        return this.tryAdvance(action instanceof java.util.function.DoubleConsumer ? (java.util.function.DoubleConsumer)((Object)action) : action::accept);
    }

    @Override
    default public boolean tryAdvance(DoubleConsumer action) {
        return this.tryAdvance((java.util.function.DoubleConsumer)action);
    }

    @Override
    @Deprecated
    default public void forEachRemaining(Consumer<? super Double> action) {
        this.forEachRemaining(action instanceof java.util.function.DoubleConsumer ? (java.util.function.DoubleConsumer)((Object)action) : action::accept);
    }

    @Override
    default public void forEachRemaining(DoubleConsumer action) {
        this.forEachRemaining((java.util.function.DoubleConsumer)action);
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
    public DoubleSpliterator trySplit();

    default public DoubleComparator getComparator() {
        throw new IllegalStateException();
    }
}

