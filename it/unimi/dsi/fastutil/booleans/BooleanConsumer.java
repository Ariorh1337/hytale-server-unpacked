/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
public interface BooleanConsumer
extends Consumer<Boolean> {
    @Override
    public void accept(boolean var1);

    @Override
    @Deprecated
    default public void accept(Boolean t) {
        this.accept((boolean)t);
    }

    default public BooleanConsumer andThen(BooleanConsumer after) {
        Objects.requireNonNull(after);
        return t -> {
            this.accept(t);
            after.accept(t);
        };
    }

    @Override
    @Deprecated
    default public Consumer<Boolean> andThen(Consumer<? super Boolean> after) {
        return Consumer.super.andThen(after);
    }
}

