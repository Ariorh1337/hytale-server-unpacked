/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.patterns;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.SpaceSize;
import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
import javax.annotation.Nonnull;

public class NotPattern
extends Pattern {
    @Nonnull
    private final Pattern pattern;
    private final SpaceSize readSpaceSize;

    public NotPattern(@Nonnull Pattern pattern) {
        this.pattern = pattern;
        this.readSpaceSize = pattern.readSpace();
    }

    @Override
    public boolean matches(@Nonnull Pattern.Context context) {
        return !this.pattern.matches(context);
    }

    @Override
    @Nonnull
    public SpaceSize readSpace() {
        return this.readSpaceSize.clone();
    }
}

