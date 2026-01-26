/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.json;

import com.hypixel.hytale.procedurallib.json.SeedResource;
import io.sentry.util.Objects;
import javax.annotation.Nonnull;

public class SeedString<T extends SeedResource> {
    public static final SeedResource DEFAULT_RESOURCE = new SeedResource(){};
    @Nonnull
    protected final T t;
    protected final String original;
    protected final String seed;
    protected final int hash;

    public SeedString(String original, @Nonnull T t) {
        this(original, original, t);
    }

    public SeedString(String original, String seed, @Nonnull T t) {
        this.original = original;
        this.seed = seed;
        this.t = (SeedResource)Objects.requireNonNull(t, "SeedResource must not be null. Use SeedString#DEFAULT");
        this.hash = this.seed.hashCode() * 114512143;
    }

    @Nonnull
    public SeedString<T> append(String suffix) {
        return new SeedString<T>(this.original, this.seed + suffix, this.t);
    }

    @Nonnull
    public SeedString<T> appendToOriginal(String suffix) {
        return new SeedString<T>(this.original, this.original + suffix, this.t);
    }

    @Nonnull
    public SeedString<T> alternateOriginal(String suffix) {
        String altOriginal = this.original + suffix;
        String altSeed = altOriginal + this.seed.substring(this.original.length());
        return new SeedString<T>(altOriginal, altSeed, this.t);
    }

    @Nonnull
    public T get() {
        return this.t;
    }

    public int hashCode() {
        return this.hash;
    }

    public String toString() {
        return this.seed;
    }
}

