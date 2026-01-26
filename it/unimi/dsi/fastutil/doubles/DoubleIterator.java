/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.DoubleConsumer;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;

public interface DoubleIterator
extends PrimitiveIterator.OfDouble {
    @Override
    public double nextDouble();

    @Override
    @Deprecated
    default public Double next() {
        return this.nextDouble();
    }

    @Override
    default public void forEachRemaining(DoubleConsumer action) {
        this.forEachRemaining((java.util.function.DoubleConsumer)action);
    }

    @Override
    @Deprecated
    default public void forEachRemaining(Consumer<? super Double> action) {
        this.forEachRemaining(action instanceof java.util.function.DoubleConsumer ? (java.util.function.DoubleConsumer)((Object)action) : action::accept);
    }

    default public int skip(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
        }
        int i = n;
        while (i-- != 0 && this.hasNext()) {
            this.nextDouble();
        }
        return n - i - 1;
    }
}

