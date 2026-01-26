/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.tintproviders;

import com.hypixel.hytale.builtin.hytalegenerator.tintproviders.TintProvider;
import javax.annotation.Nonnull;

public class ConstantTintProvider
extends TintProvider {
    @Nonnull
    private final TintProvider.Result result;

    public ConstantTintProvider(int value) {
        this.result = new TintProvider.Result(value);
    }

    @Override
    @Nonnull
    public TintProvider.Result getValue(@Nonnull TintProvider.Context context) {
        return this.result;
    }
}

