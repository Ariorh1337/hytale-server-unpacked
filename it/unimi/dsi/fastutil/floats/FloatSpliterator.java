/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import java.util.Spliterator;
import java.util.function.Consumer;

public interface FloatSpliterator
extends Spliterator.OfPrimitive<Float, FloatConsumer, FloatSpliterator> {
    @Override
    @Deprecated
    default public boolean tryAdvance(Consumer<? super Float> action) {
        return this.tryAdvance(action instanceof FloatConsumer ? (FloatConsumer)action : action::accept);
    }

    @Override
    @Deprecated
    default public void forEachRemaining(Consumer<? super Float> action) {
        this.forEachRemaining(action instanceof FloatConsumer ? (FloatConsumer)action : action::accept);
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
    public FloatSpliterator trySplit();

    default public FloatComparator getComparator() {
        throw new IllegalStateException();
    }
}

