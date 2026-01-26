/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.fields.points.PointProvider;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import javax.annotation.Nonnull;

public class Mesh3DPositionProvider
extends PositionProvider {
    @Nonnull
    private final PointProvider pointGenerator;

    public Mesh3DPositionProvider(@Nonnull PointProvider positionProvider) {
        this.pointGenerator = positionProvider;
    }

    @Override
    public void positionsIn(@Nonnull PositionProvider.Context context) {
        this.pointGenerator.points3d(context.minInclusive, context.maxExclusive, context.consumer);
    }
}

