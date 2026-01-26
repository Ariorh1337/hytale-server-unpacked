/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.ConstantMaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.MaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.StripedMaterialProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.ArrayList;
import javax.annotation.Nonnull;

public class StripedMaterialProviderAsset
extends MaterialProviderAsset {
    public static final BuilderCodec<StripedMaterialProviderAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(StripedMaterialProviderAsset.class, StripedMaterialProviderAsset::new, MaterialProviderAsset.ABSTRACT_CODEC).append(new KeyedCodec<T[]>("Stripes", new ArrayCodec(StripeAsset.CODEC, StripeAsset[]::new), true), (t, k) -> {
        t.stripeAssets = k;
    }, k -> k.stripeAssets).add()).append(new KeyedCodec("Material", MaterialProviderAsset.CODEC, true), (t, k) -> {
        t.materialProviderAsset = k;
    }, k -> k.materialProviderAsset).add()).build();
    private StripeAsset[] stripeAssets = new StripeAsset[0];
    private MaterialProviderAsset materialProviderAsset = new ConstantMaterialProviderAsset();

    @Override
    @Nonnull
    public MaterialProvider<Material> build(@Nonnull MaterialProviderAsset.Argument argument) {
        if (super.skip()) {
            return MaterialProvider.noMaterialProvider();
        }
        ArrayList<StripedMaterialProvider.Stripe> stripes = new ArrayList<StripedMaterialProvider.Stripe>();
        for (StripeAsset asset : this.stripeAssets) {
            if (asset == null) {
                LoggerUtil.getLogger().warning("Couldn't load a strip asset, will skip it.");
                continue;
            }
            StripedMaterialProvider.Stripe stripe = new StripedMaterialProvider.Stripe(asset.topY, asset.bottomY);
            stripes.add(stripe);
        }
        MaterialProvider<Material> materialProvider = this.materialProviderAsset.build(argument);
        return new StripedMaterialProvider<Material>(materialProvider, stripes);
    }

    @Override
    public void cleanUp() {
        this.materialProviderAsset.cleanUp();
    }

    public static class StripeAsset
    implements JsonAssetWithMap<String, DefaultAssetMap<String, StripeAsset>> {
        public static final AssetBuilderCodec<String, StripeAsset> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(StripeAsset.class, StripeAsset::new, Codec.STRING, (asset, id) -> {
            asset.id = id;
        }, config -> config.id, (config, data) -> {
            config.data = data;
        }, config -> config.data).append(new KeyedCodec<Integer>("TopY", Codec.INTEGER, true), (t, y) -> {
            t.topY = y;
        }, t -> t.bottomY).add()).append(new KeyedCodec<Integer>("BottomY", Codec.INTEGER, true), (t, y) -> {
            t.bottomY = y;
        }, t -> t.bottomY).add()).build();
        private String id;
        private AssetExtraInfo.Data data;
        private int topY;
        private int bottomY;

        @Override
        public String getId() {
            return this.id;
        }
    }
}

