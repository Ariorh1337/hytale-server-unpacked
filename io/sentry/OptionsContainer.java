/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import java.lang.reflect.InvocationTargetException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class OptionsContainer<T> {
    @NotNull
    private final Class<T> clazz;

    @NotNull
    public static <T> OptionsContainer<T> create(@NotNull Class<T> clazz) {
        return new OptionsContainer<T>(clazz);
    }

    private OptionsContainer(@NotNull Class<T> clazz) {
        this.clazz = clazz;
    }

    @NotNull
    public T createInstance() throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return this.clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
    }
}

