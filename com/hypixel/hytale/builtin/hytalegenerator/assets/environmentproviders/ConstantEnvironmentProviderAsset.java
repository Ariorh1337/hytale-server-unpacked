/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.environmentproviders;

import com.hypixel.hytale.builtin.hytalegenerator.assets.environmentproviders.EnvironmentProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.environmentproviders.ConstantEnvironmentProvider;
import com.hypixel.hytale.builtin.hytalegenerator.environmentproviders.EnvironmentProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
import javax.annotation.Nonnull;

public class ConstantEnvironmentProviderAsset
extends EnvironmentProviderAsset {
    public static final BuilderCodec<ConstantEnvironmentProviderAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(ConstantEnvironmentProviderAsset.class, ConstantEnvironmentProviderAsset::new, EnvironmentProviderAsset.ABSTRACT_CODEC).append(new KeyedCodec<String>("Environment", Codec.STRING, true), (t, k) -> {
        t.environment = k;
    }, k -> k.environment).add()).build();
    private String environment = "Unknown";

    @Override
    @Nonnull
    public EnvironmentProvider build(@Nonnull EnvironmentProviderAsset.Argument argument) {
        if (super.isSkipped()) {
            return EnvironmentProvider.noEnvironmentProvider();
        }
        int index = Environment.getAssetMap().getIndex(this.environment);
        if (index == Integer.MIN_VALUE) {
            index = 0;
        }
        return new ConstantEnvironmentProvider(index);
    }
}

