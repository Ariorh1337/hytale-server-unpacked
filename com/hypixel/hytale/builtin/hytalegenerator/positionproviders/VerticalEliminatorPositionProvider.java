/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public class VerticalEliminatorPositionProvider
extends PositionProvider {
    private final int maxY;
    private final int minY;
    @Nonnull
    private final PositionProvider positionProvider;

    public VerticalEliminatorPositionProvider(int minY, int maxY, @Nonnull PositionProvider positionProvider) {
        this.minY = minY;
        this.maxY = maxY;
        this.positionProvider = positionProvider;
    }

    @Override
    public void positionsIn(@Nonnull PositionProvider.Context context) {
        PositionProvider.Context childContext = new PositionProvider.Context(context);
        childContext.consumer = positions -> {
            if (positions.y < (double)this.minY || positions.y >= (double)this.maxY) {
                return;
            }
            context.consumer.accept((Vector3d)positions);
        };
        this.positionProvider.positionsIn(childContext);
    }
}

