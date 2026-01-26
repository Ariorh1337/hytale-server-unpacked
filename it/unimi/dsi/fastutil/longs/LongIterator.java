/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongConsumer;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;

public interface LongIterator
extends PrimitiveIterator.OfLong {
    @Override
    public long nextLong();

    @Override
    @Deprecated
    default public Long next() {
        return this.nextLong();
    }

    @Override
    default public void forEachRemaining(LongConsumer action) {
        this.forEachRemaining((java.util.function.LongConsumer)action);
    }

    @Override
    @Deprecated
    default public void forEachRemaining(Consumer<? super Long> action) {
        this.forEachRemaining(action instanceof java.util.function.LongConsumer ? (java.util.function.LongConsumer)((Object)action) : action::accept);
    }

    default public int skip(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
        }
        int i = n;
        while (i-- != 0 && this.hasNext()) {
            this.nextLong();
        }
        return n - i - 1;
    }
}

