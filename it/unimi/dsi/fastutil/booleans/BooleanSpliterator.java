/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.BooleanComparator;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.Spliterator;
import java.util.function.Consumer;

public interface BooleanSpliterator
extends Spliterator.OfPrimitive<Boolean, BooleanConsumer, BooleanSpliterator> {
    @Override
    @Deprecated
    default public boolean tryAdvance(Consumer<? super Boolean> action) {
        return this.tryAdvance(action instanceof BooleanConsumer ? (BooleanConsumer)action : action::accept);
    }

    @Override
    @Deprecated
    default public void forEachRemaining(Consumer<? super Boolean> action) {
        this.forEachRemaining(action instanceof BooleanConsumer ? (BooleanConsumer)action : action::accept);
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
    public BooleanSpliterator trySplit();

    default public BooleanComparator getComparator() {
        throw new IllegalStateException();
    }
}

