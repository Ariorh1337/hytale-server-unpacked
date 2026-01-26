/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
public interface LongConsumer
extends Consumer<Long>,
java.util.function.LongConsumer {
    @Override
    @Deprecated
    default public void accept(Long t) {
        this.accept(t.longValue());
    }

    @Override
    default public LongConsumer andThen(java.util.function.LongConsumer after) {
        Objects.requireNonNull(after);
        return t -> {
            this.accept(t);
            after.accept(t);
        };
    }

    default public LongConsumer andThen(LongConsumer after) {
        return this.andThen((java.util.function.LongConsumer)after);
    }

    @Override
    @Deprecated
    default public Consumer<Long> andThen(Consumer<? super Long> after) {
        return Consumer.super.andThen(after);
    }
}

