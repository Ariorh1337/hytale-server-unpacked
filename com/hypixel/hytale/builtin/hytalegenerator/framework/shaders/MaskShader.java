/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.framework.shaders;

import com.hypixel.hytale.builtin.hytalegenerator.framework.math.SeedGenerator;
import com.hypixel.hytale.builtin.hytalegenerator.framework.shaders.Shader;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public class MaskShader<T>
implements Shader<T> {
    private final Shader<T> childShader;
    private final Predicate<T> mask;
    private SeedGenerator seedGenerator;

    private MaskShader(Predicate<T> mask, Shader<T> childShader, long seed) {
        this.mask = mask;
        this.childShader = childShader;
        this.seedGenerator = new SeedGenerator(seed);
    }

    @Nonnull
    public static <T> Builder<T> builder(@Nonnull Class<T> dataType) {
        return new Builder();
    }

    @Override
    public T shade(T current, long seed) {
        if (!this.mask.test(current)) {
            return current;
        }
        return this.childShader.shade(current, seed);
    }

    @Override
    public T shade(T current, long seedA, long seedB) {
        return this.shade(current, 0L);
    }

    @Override
    public T shade(T current, long seedA, long seedB, long seedC) {
        return this.shade(current, 0L);
    }

    @Nonnull
    public String toString() {
        return "MaskShader{childShader=" + String.valueOf(this.childShader) + ", mask=" + String.valueOf(this.mask) + ", seedGenerator=" + String.valueOf(this.seedGenerator) + "}";
    }

    public static class Builder<T> {
        private Shader<T> childShader;
        private Predicate<T> mask;
        private long seed = System.nanoTime();

        private Builder() {
        }

        @Nonnull
        public MaskShader<T> build() {
            if (this.childShader == null || this.mask == null) {
                throw new IllegalStateException("incomplete builder");
            }
            return new MaskShader<T>(this.mask, this.childShader, this.seed);
        }

        @Nonnull
        public Builder<T> withSeed(long seed) {
            this.seed = seed;
            return this;
        }

        @Nonnull
        public Builder<T> withMask(@Nonnull Predicate<T> mask) {
            this.mask = mask;
            return this;
        }

        @Nonnull
        public Builder<T> withChildShader(@Nonnull Shader<T> shader) {
            this.childShader = shader;
            return this;
        }
    }
}

