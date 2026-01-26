/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class UnionPositionProvider
extends PositionProvider {
    @Nonnull
    private final List<PositionProvider> positionProviders = new ArrayList<PositionProvider>();

    public UnionPositionProvider(@Nonnull List<PositionProvider> positionProviders) {
        this.positionProviders.addAll(positionProviders);
    }

    @Override
    public void positionsIn(@Nonnull PositionProvider.Context context) {
        for (PositionProvider position : this.positionProviders) {
            position.positionsIn(context);
        }
    }
}

