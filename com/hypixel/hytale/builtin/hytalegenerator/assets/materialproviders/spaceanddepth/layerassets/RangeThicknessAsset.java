/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.spaceanddepth.layerassets;

import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.ConstantMaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.MaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.spaceanddepth.layerassets.LayerAsset;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.spaceanddepth.SpaceAndDepthMaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.spaceanddepth.layers.RangedThicknessLayer;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class RangeThicknessAsset
extends LayerAsset {
    public static final BuilderCodec<RangeThicknessAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(RangeThicknessAsset.class, RangeThicknessAsset::new, LayerAsset.ABSTRACT_CODEC).append(new KeyedCodec<Integer>("RangeMin", Codec.INTEGER, true), (t, k) -> {
        t.rangeMin = k;
    }, k -> k.rangeMin).add()).append(new KeyedCodec<Integer>("RangeMax", Codec.INTEGER, true), (t, k) -> {
        t.rangeMax = k;
    }, k -> k.rangeMax).add()).append(new KeyedCodec("Material", MaterialProviderAsset.CODEC, true), (t, k) -> {
        t.materialProviderAsset = k;
    }, k -> k.materialProviderAsset).add()).append(new KeyedCodec<String>("Seed", Codec.STRING, true), (t, k) -> {
        t.seed = k;
    }, k -> k.seed).add()).afterDecode(asset -> {
        asset.rangeMax = Math.max(asset.rangeMin, asset.rangeMax);
    })).build();
    private MaterialProviderAsset materialProviderAsset = new ConstantMaterialProviderAsset();
    private String seed = "";
    private int rangeMin = 0;
    private int rangeMax = 0;

    @Override
    @Nonnull
    public SpaceAndDepthMaterialProvider.Layer<Material> build(@Nonnull MaterialProviderAsset.Argument argument) {
        MaterialProvider<Material> materialProvider = this.materialProviderAsset.build(argument);
        return new RangedThicknessLayer<Material>(this.rangeMin, this.rangeMax, argument.parentSeed.child(this.seed), materialProvider);
    }

    @Override
    public void cleanUp() {
        this.materialProviderAsset.cleanUp();
    }
}

