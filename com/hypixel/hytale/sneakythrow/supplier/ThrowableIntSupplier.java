/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.sneakythrow.supplier;

import com.hypixel.hytale.sneakythrow.SneakyThrow;
import java.util.function.IntSupplier;

@FunctionalInterface
public interface ThrowableIntSupplier<E extends Throwable>
extends IntSupplier {
    @Override
    default public int getAsInt() {
        try {
            return this.getAsIntNow();
        }
        catch (Throwable e) {
            throw SneakyThrow.sneakyThrow(e);
        }
    }

    public int getAsIntNow() throws E;
}

