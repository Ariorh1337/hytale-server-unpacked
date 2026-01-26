/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ClassLoaderUtils {
    @NotNull
    public static ClassLoader classLoaderOrDefault(@Nullable ClassLoader classLoader) {
        if (classLoader == null) {
            @Nullable ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                return contextClassLoader;
            }
            return ClassLoader.getSystemClassLoader();
        }
        return classLoader;
    }
}

