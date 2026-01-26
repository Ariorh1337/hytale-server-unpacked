/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.sneakythrow.consumer;

import com.hypixel.hytale.sneakythrow.SneakyThrow;
import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowableConsumer<T, E extends Throwable>
extends Consumer<T> {
    @Override
    default public void accept(T t) {
        try {
            this.acceptNow(t);
        }
        catch (Throwable e) {
            throw SneakyThrow.sneakyThrow(e);
        }
    }

    public void acceptNow(T var1) throws E;
}

