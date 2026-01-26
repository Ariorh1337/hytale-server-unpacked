/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.ListPositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.PositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.SpherePositionProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class SpherePositionProviderAsset
extends PositionProviderAsset {
    public static final BuilderCodec<SpherePositionProviderAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(SpherePositionProviderAsset.class, SpherePositionProviderAsset::new, PositionProviderAsset.ABSTRACT_CODEC).append(new KeyedCodec<Double>("Range", Codec.DOUBLE, false), (t, k) -> {
        t.range = k;
    }, k -> k.range).add()).append(new KeyedCodec("Positions", PositionProviderAsset.CODEC, true), (t, k) -> {
        t.positionProviderAsset = k;
    }, k -> k.positionProviderAsset).add()).build();
    private double range = 0.0;
    private PositionProviderAsset positionProviderAsset = new ListPositionProviderAsset();

    @Override
    @Nonnull
    public PositionProvider build(@Nonnull PositionProviderAsset.Argument argument) {
        if (super.skip()) {
            return PositionProvider.noPositionProvider();
        }
        PositionProvider positionProvider = this.positionProviderAsset == null ? PositionProvider.noPositionProvider() : this.positionProviderAsset.build(argument);
        return new SpherePositionProvider(positionProvider, this.range);
    }

    @Override
    public void cleanUp() {
        this.positionProviderAsset.cleanUp();
    }
}

