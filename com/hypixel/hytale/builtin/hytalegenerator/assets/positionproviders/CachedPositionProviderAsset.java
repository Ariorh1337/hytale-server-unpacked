/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.ListPositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.PositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.cached.CachedPositionProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import javax.annotation.Nonnull;

public class CachedPositionProviderAsset
extends PositionProviderAsset {
    public static final BuilderCodec<CachedPositionProviderAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(CachedPositionProviderAsset.class, CachedPositionProviderAsset::new, PositionProviderAsset.ABSTRACT_CODEC).append(new KeyedCodec("Positions", PositionProviderAsset.CODEC, true), (asset, v) -> {
        asset.childAsset = v;
    }, asset -> asset.childAsset).add()).append(new KeyedCodec<Integer>("SectionSize", Codec.INTEGER, true), (asset, v) -> {
        asset.sectionSize = v;
    }, asset -> asset.sectionSize).addValidator(Validators.greaterThan(0)).add()).append(new KeyedCodec<Integer>("CacheSize", Codec.INTEGER, true), (asset, v) -> {
        asset.cacheSize = v;
    }, asset -> asset.cacheSize).addValidator(Validators.greaterThan(-1)).add()).build();
    private PositionProviderAsset childAsset = new ListPositionProviderAsset();
    private int sectionSize = 32;
    private int cacheSize = 100;

    @Override
    @Nonnull
    public PositionProvider build(@Nonnull PositionProviderAsset.Argument argument) {
        if (super.skip()) {
            return PositionProvider.noPositionProvider();
        }
        PositionProvider childPositions = this.childAsset.build(argument);
        CachedPositionProvider instance = new CachedPositionProvider(childPositions, this.sectionSize, this.cacheSize, false, argument.workerIndexer.getWorkerCount());
        return instance;
    }

    @Override
    public void cleanUp() {
        this.childAsset.cleanUp();
    }
}

