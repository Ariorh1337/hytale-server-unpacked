/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.shorts.ShortConsumer;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface ShortIterator
extends PrimitiveIterator<Short, ShortConsumer> {
    public short nextShort();

    @Override
    @Deprecated
    default public Short next() {
        return this.nextShort();
    }

    @Override
    default public void forEachRemaining(ShortConsumer action) {
        Objects.requireNonNull(action);
        while (this.hasNext()) {
            action.accept(this.nextShort());
        }
    }

    @Override
    default public void forEachRemaining(IntConsumer action) {
        Objects.requireNonNull(action);
        this.forEachRemaining(action instanceof ShortConsumer ? (ShortConsumer)action : action::accept);
    }

    @Override
    @Deprecated
    default public void forEachRemaining(Consumer<? super Short> action) {
        this.forEachRemaining(action instanceof ShortConsumer ? (ShortConsumer)action : action::accept);
    }

    default public int skip(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
        }
        int i = n;
        while (i-- != 0 && this.hasNext()) {
            this.nextShort();
        }
        return n - i - 1;
    }
}

