/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.sneakythrow;

import com.hypixel.hytale.sneakythrow.SneakyThrow;

@FunctionalInterface
public interface ThrowableRunnable<E extends Throwable>
extends Runnable {
    @Override
    default public void run() {
        try {
            this.runNow();
        }
        catch (Throwable e) {
            throw SneakyThrow.sneakyThrow(e);
        }
    }

    public void runNow() throws E;
}

