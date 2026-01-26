/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders;

import com.hypixel.hytale.builtin.hytalegenerator.assets.material.MaterialAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.MaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.ConstantMaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class ConstantMaterialProviderAsset
extends MaterialProviderAsset {
    public static final BuilderCodec<ConstantMaterialProviderAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(ConstantMaterialProviderAsset.class, ConstantMaterialProviderAsset::new, MaterialProviderAsset.ABSTRACT_CODEC).append(new KeyedCodec("Material", MaterialAsset.CODEC, true), (asset, value) -> {
        asset.materialAsset = value;
    }, asset -> asset.materialAsset).add()).build();
    private MaterialAsset materialAsset = new MaterialAsset();

    @Override
    @Nonnull
    public MaterialProvider<Material> build(@Nonnull MaterialProviderAsset.Argument argument) {
        if (super.skip()) {
            return MaterialProvider.noMaterialProvider();
        }
        if (this.materialAsset == null) {
            return new ConstantMaterialProvider<Object>(null);
        }
        Material material = this.materialAsset.build(argument.materialCache);
        return new ConstantMaterialProvider<Material>(material);
    }
}

