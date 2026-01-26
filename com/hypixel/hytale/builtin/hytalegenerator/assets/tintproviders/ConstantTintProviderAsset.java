/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.tintproviders;

import com.hypixel.hytale.builtin.hytalegenerator.assets.tintproviders.TintProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.tintproviders.ConstantTintProvider;
import com.hypixel.hytale.builtin.hytalegenerator.tintproviders.TintProvider;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.server.core.asset.util.ColorParseUtil;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import javax.annotation.Nonnull;

public class ConstantTintProviderAsset
extends TintProviderAsset {
    public static final Color DEFAULT_COLOR = ColorParseUtil.hexStringToColor("#FF0000");
    public static final BuilderCodec<ConstantTintProviderAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(ConstantTintProviderAsset.class, ConstantTintProviderAsset::new, TintProviderAsset.ABSTRACT_CODEC).append(new KeyedCodec<Color>("Color", ProtocolCodecs.COLOR, true), (t, k) -> {
        t.color = k;
    }, k -> k.color).add()).build();
    private Color color = DEFAULT_COLOR;

    @Override
    @Nonnull
    public TintProvider build(@Nonnull TintProviderAsset.Argument argument) {
        if (super.isSkipped()) {
            return TintProvider.noTintProvider();
        }
        int colorInt = ColorParseUtil.colorToARGBInt(this.color);
        return new ConstantTintProvider(colorInt);
    }
}

