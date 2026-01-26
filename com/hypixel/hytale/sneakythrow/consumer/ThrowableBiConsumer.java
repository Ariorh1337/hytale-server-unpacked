/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.sneakythrow.consumer;

import com.hypixel.hytale.sneakythrow.SneakyThrow;
import java.util.function.BiConsumer;

@FunctionalInterface
public interface ThrowableBiConsumer<T, U, E extends Throwable>
extends BiConsumer<T, U> {
    @Override
    default public void accept(T t, U u) {
        try {
            this.acceptNow(t, u);
        }
        catch (Throwable e) {
            throw SneakyThrow.sneakyThrow(e);
        }
    }

    public void acceptNow(T var1, U var2) throws E;
}

