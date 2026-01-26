/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.sneakythrow.supplier;

import com.hypixel.hytale.sneakythrow.SneakyThrow;
import java.util.function.Supplier;

@FunctionalInterface
public interface ThrowableSupplier<T, E extends Throwable>
extends Supplier<T> {
    @Override
    default public T get() {
        try {
            return this.getNow();
        }
        catch (Throwable e) {
            throw SneakyThrow.sneakyThrow(e);
        }
    }

    public T getNow() throws E;
}

