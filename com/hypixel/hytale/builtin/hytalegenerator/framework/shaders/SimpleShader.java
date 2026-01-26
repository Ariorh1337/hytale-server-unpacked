/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.framework.shaders;

import com.hypixel.hytale.builtin.hytalegenerator.framework.shaders.Shader;
import javax.annotation.Nonnull;

public class SimpleShader<T>
implements Shader<T> {
    @Nonnull
    private final T value;

    private SimpleShader(@Nonnull T value) {
        this.value = value;
    }

    @Nonnull
    public static <T> SimpleShader<T> of(@Nonnull T value) {
        return new SimpleShader<T>(value);
    }

    @Override
    @Nonnull
    public T shade(T current, long seed) {
        return this.value;
    }

    @Override
    @Nonnull
    public T shade(T current, long seedA, long seedB) {
        return this.value;
    }

    @Override
    @Nonnull
    public T shade(T current, long seedA, long seedB, long seedC) {
        return this.value;
    }

    @Nonnull
    public String toString() {
        return "SimpleShader{value=" + String.valueOf(this.value) + "}";
    }
}

