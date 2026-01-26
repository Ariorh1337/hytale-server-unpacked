/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders;

import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.ConstantMaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.MaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.DownwardDepthMaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class DownwardDepthMaterialProviderAsset
extends MaterialProviderAsset {
    public static final BuilderCodec<DownwardDepthMaterialProviderAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(DownwardDepthMaterialProviderAsset.class, DownwardDepthMaterialProviderAsset::new, MaterialProviderAsset.ABSTRACT_CODEC).append(new KeyedCodec<Integer>("Depth", Codec.INTEGER, true), (t, k) -> {
        t.depth = k;
    }, k -> k.depth).add()).append(new KeyedCodec("Material", MaterialProviderAsset.CODEC, true), (t, k) -> {
        t.materialProviderAsset = k;
    }, k -> k.materialProviderAsset).add()).build();
    private int depth = 0;
    private MaterialProviderAsset materialProviderAsset = new ConstantMaterialProviderAsset();

    @Override
    @Nonnull
    public MaterialProvider<Material> build(@Nonnull MaterialProviderAsset.Argument argument) {
        if (super.skip()) {
            return MaterialProvider.noMaterialProvider();
        }
        return new DownwardDepthMaterialProvider<Material>(this.materialProviderAsset.build(argument), this.depth);
    }

    @Override
    public void cleanUp() {
        this.materialProviderAsset.cleanUp();
    }
}

