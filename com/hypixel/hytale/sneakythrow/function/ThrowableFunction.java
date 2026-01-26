/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.sneakythrow.function;

import com.hypixel.hytale.sneakythrow.SneakyThrow;
import java.util.function.Function;

@FunctionalInterface
public interface ThrowableFunction<T, R, E extends Throwable>
extends Function<T, R> {
    @Override
    default public R apply(T t) {
        try {
            return this.applyNow(t);
        }
        catch (Throwable e) {
            throw SneakyThrow.sneakyThrow(e);
        }
    }

    public R applyNow(T var1) throws E;
}

