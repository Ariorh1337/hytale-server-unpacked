/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders;

import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.ConstantMaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.MaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.SolidityMaterialProvider;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class SolidityMaterialProviderAsset
extends MaterialProviderAsset {
    public static final BuilderCodec<SolidityMaterialProviderAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(SolidityMaterialProviderAsset.class, SolidityMaterialProviderAsset::new, MaterialProviderAsset.ABSTRACT_CODEC).append(new KeyedCodec("Solid", MaterialProviderAsset.CODEC, true), (t, k) -> {
        t.solidMaterialProvider = k;
    }, k -> k.solidMaterialProvider).add()).append(new KeyedCodec("Empty", MaterialProviderAsset.CODEC, true), (t, k) -> {
        t.emptyMaterialProvider = k;
    }, k -> k.emptyMaterialProvider).add()).build();
    private MaterialProviderAsset solidMaterialProvider = new ConstantMaterialProviderAsset();
    private MaterialProviderAsset emptyMaterialProvider = new ConstantMaterialProviderAsset();

    @Override
    @Nonnull
    public MaterialProvider<Material> build(@Nonnull MaterialProviderAsset.Argument argument) {
        if (super.skip()) {
            return MaterialProvider.noMaterialProvider();
        }
        return new SolidityMaterialProvider<Material>(this.solidMaterialProvider.build(argument), this.emptyMaterialProvider.build(argument));
    }

    @Override
    public void cleanUp() {
        this.solidMaterialProvider.cleanUp();
        this.emptyMaterialProvider.cleanUp();
    }
}

