/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.materialproviders;

import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StripedMaterialProvider<V>
extends MaterialProvider<V> {
    @Nonnull
    private final MaterialProvider<V> materialProvider;
    @Nonnull
    private final Stripe[] stripes;

    public StripedMaterialProvider(@Nonnull MaterialProvider<V> materialProvider, @Nonnull List<Stripe> stripes) {
        this.materialProvider = materialProvider;
        this.stripes = new Stripe[stripes.size()];
        for (int i = 0; i < stripes.size(); ++i) {
            Stripe s;
            this.stripes[i] = s = stripes.get(i);
        }
    }

    @Override
    @Nullable
    public V getVoxelTypeAt(@Nonnull MaterialProvider.Context context) {
        for (Stripe s : this.stripes) {
            if (!s.contains(context.position.y)) continue;
            return this.materialProvider.getVoxelTypeAt(context);
        }
        return null;
    }

    public static class Stripe {
        private final int topY;
        private final int bottomY;

        public Stripe(int topY, int bottomY) {
            this.topY = topY;
            this.bottomY = bottomY;
        }

        public boolean contains(int y) {
            return y >= this.bottomY && y <= this.topY;
        }
    }
}

