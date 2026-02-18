/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.assets.framework.PositionsFrameworkAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.PositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class FrameworkPositionProviderAsset
extends PositionProviderAsset {
    @Nonnull
    public static final BuilderCodec<FrameworkPositionProviderAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(FrameworkPositionProviderAsset.class, FrameworkPositionProviderAsset::new, PositionProviderAsset.ABSTRACT_CODEC).append(new KeyedCodec<String>("Name", Codec.STRING, true), (asset, v) -> {
        asset.name = v;
    }, asset -> asset.name).add()).build();
    private String name = "";

    @Override
    public PositionProvider build(@Nonnull PositionProviderAsset.Argument argument) {
        if (super.skip()) {
            return PositionProvider.noPositionProvider();
        }
        PositionProviderAsset baseAsset = PositionsFrameworkAsset.Entries.get(this.name, argument.referenceBundle);
        if (baseAsset == null) {
            LoggerUtil.getLogger().log(Level.WARNING, "Couldn't find WorldFramework Positions with name " + this.name);
            return PositionProvider.noPositionProvider();
        }
        return baseAsset.build(argument);
    }
}

