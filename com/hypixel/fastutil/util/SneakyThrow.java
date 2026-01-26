/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.fastutil.util;

public class SneakyThrow {
    public static RuntimeException sneakyThrow(Throwable t) {
        if (t == null) {
            throw new NullPointerException("t");
        }
        return (RuntimeException)SneakyThrow.sneakyThrow0(t);
    }

    private static <T extends Throwable> T sneakyThrow0(Throwable t) throws T {
        throw t;
    }
}

