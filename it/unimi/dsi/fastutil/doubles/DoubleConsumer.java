/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
public interface DoubleConsumer
extends Consumer<Double>,
java.util.function.DoubleConsumer {
    @Override
    @Deprecated
    default public void accept(Double t) {
        this.accept(t.doubleValue());
    }

    @Override
    default public DoubleConsumer andThen(java.util.function.DoubleConsumer after) {
        Objects.requireNonNull(after);
        return t -> {
            this.accept(t);
            after.accept(t);
        };
    }

    default public DoubleConsumer andThen(DoubleConsumer after) {
        return this.andThen((java.util.function.DoubleConsumer)after);
    }

    @Override
    @Deprecated
    default public Consumer<Double> andThen(Consumer<? super Double> after) {
        return Consumer.super.andThen(after);
    }
}

