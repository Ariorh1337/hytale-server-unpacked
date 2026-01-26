/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.vectorproviders;

import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.VectorProvider;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public class ConstantVectorProvider
extends VectorProvider {
    @Nonnull
    private final Vector3d value;

    public ConstantVectorProvider(@Nonnull Vector3d value) {
        this.value = value.clone();
    }

    @Override
    @Nonnull
    public Vector3d process(@Nonnull VectorProvider.Context context) {
        return this.value.clone();
    }
}

