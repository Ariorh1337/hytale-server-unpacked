/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.tintproviders;

import com.hypixel.hytale.builtin.hytalegenerator.tintproviders.TintProvider;
import javax.annotation.Nonnull;

public class NoTintProvider
extends TintProvider {
    @Override
    @Nonnull
    public TintProvider.Result getValue(@Nonnull TintProvider.Context context) {
        return TintProvider.Result.WITHOUT_VALUE;
    }
}

