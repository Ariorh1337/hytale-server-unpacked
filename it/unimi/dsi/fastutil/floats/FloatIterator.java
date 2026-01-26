/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.FloatConsumer;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public interface FloatIterator
extends PrimitiveIterator<Float, FloatConsumer> {
    public float nextFloat();

    @Override
    @Deprecated
    default public Float next() {
        return Float.valueOf(this.nextFloat());
    }

    @Override
    default public void forEachRemaining(FloatConsumer action) {
        Objects.requireNonNull(action);
        while (this.hasNext()) {
            action.accept(this.nextFloat());
        }
    }

    @Override
    default public void forEachRemaining(DoubleConsumer action) {
        Objects.requireNonNull(action);
        this.forEachRemaining(action instanceof FloatConsumer ? (FloatConsumer)action : action::accept);
    }

    @Override
    @Deprecated
    default public void forEachRemaining(Consumer<? super Float> action) {
        this.forEachRemaining(action instanceof FloatConsumer ? (FloatConsumer)action : action::accept);
    }

    default public int skip(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
        }
        int i = n;
        while (i-- != 0 && this.hasNext()) {
            this.nextFloat();
        }
        return n - i - 1;
    }
}

