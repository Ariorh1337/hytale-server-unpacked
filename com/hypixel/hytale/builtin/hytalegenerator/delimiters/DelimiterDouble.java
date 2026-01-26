/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.delimiters;

import com.hypixel.hytale.builtin.hytalegenerator.delimiters.RangeDouble;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DelimiterDouble<V> {
    @Nonnull
    private final RangeDouble range;
    @Nullable
    private final V value;

    public DelimiterDouble(@Nonnull RangeDouble range, @Nullable V value) {
        this.range = range;
        this.value = value;
    }

    @Nonnull
    public RangeDouble getRange() {
        return this.range;
    }

    @Nullable
    public V getValue() {
        return this.value;
    }
}

