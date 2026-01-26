/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.PositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.UnionPositionProvider;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import java.util.ArrayList;
import javax.annotation.Nonnull;

public class UnionPositionProviderAsset
extends PositionProviderAsset {
    public static final BuilderCodec<UnionPositionProviderAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(UnionPositionProviderAsset.class, UnionPositionProviderAsset::new, PositionProviderAsset.ABSTRACT_CODEC).append(new KeyedCodec<T[]>("Positions", new ArrayCodec(PositionProviderAsset.CODEC, PositionProviderAsset[]::new), true), (asset, v) -> {
        asset.positionProviderAssets = v;
    }, asset -> asset.positionProviderAssets).add()).build();
    private PositionProviderAsset[] positionProviderAssets = new PositionProviderAsset[0];

    @Override
    @Nonnull
    public PositionProvider build(@Nonnull PositionProviderAsset.Argument argument) {
        if (super.skip()) {
            return PositionProvider.noPositionProvider();
        }
        ArrayList<PositionProvider> list = new ArrayList<PositionProvider>();
        for (PositionProviderAsset asset : this.positionProviderAssets) {
            PositionProvider positionProvider = asset.build(argument);
            list.add(positionProvider);
        }
        return new UnionPositionProvider(list);
    }

    @Override
    public void cleanUp() {
        for (PositionProviderAsset positionProviderAsset : this.positionProviderAssets) {
            positionProviderAsset.cleanUp();
        }
    }
}

