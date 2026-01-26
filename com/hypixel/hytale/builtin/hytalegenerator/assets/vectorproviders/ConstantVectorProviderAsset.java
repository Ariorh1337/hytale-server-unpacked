/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders;

import com.hypixel.hytale.builtin.hytalegenerator.assets.vectorproviders.VectorProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.ConstantVectorProvider;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.VectorProvider;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public class ConstantVectorProviderAsset
extends VectorProviderAsset {
    public static final BuilderCodec<ConstantVectorProviderAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(ConstantVectorProviderAsset.class, ConstantVectorProviderAsset::new, ABSTRACT_CODEC).append(new KeyedCodec<Vector3d>("Value", Vector3d.CODEC, true), (asset, value) -> {
        asset.value = value;
    }, asset -> asset.value).add()).build();
    private Vector3d value = new Vector3d();

    public ConstantVectorProviderAsset() {
    }

    public ConstantVectorProviderAsset(@Nonnull Vector3d vector) {
        this.value.assign(vector);
    }

    @Override
    public VectorProvider build(@Nonnull VectorProviderAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantVectorProvider(new Vector3d());
        }
        return new ConstantVectorProvider(this.value);
    }
}

