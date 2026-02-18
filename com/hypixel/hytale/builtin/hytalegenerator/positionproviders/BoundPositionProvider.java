/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import javax.annotation.Nonnull;

public class BoundPositionProvider
extends PositionProvider {
    @Nonnull
    private final PositionProvider positionProvider;
    private final Bounds3d bounds;

    public BoundPositionProvider(@Nonnull PositionProvider positionProvider, @Nonnull Bounds3d bounds) {
        this.positionProvider = positionProvider;
        this.bounds = bounds;
    }

    @Override
    public void positionsIn(@Nonnull PositionProvider.Context context) {
        PositionProvider.Context childContext = new PositionProvider.Context(context);
        childContext.minInclusive = this.bounds.min;
        childContext.maxExclusive = this.bounds.max;
        this.positionProvider.positionsIn(childContext);
    }
}

