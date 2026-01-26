/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.spaceanddepth.conditionassets;

import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.spaceanddepth.conditionassets.AlwaysTrueConditionAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.spaceanddepth.conditionassets.ConditionAsset;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.spaceanddepth.SpaceAndDepthMaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.spaceanddepth.conditions.NotCondition;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class NotConditionAsset
extends ConditionAsset {
    public static final BuilderCodec<NotConditionAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(NotConditionAsset.class, NotConditionAsset::new, ConditionAsset.ABSTRACT_CODEC).append(new KeyedCodec("Condition", ConditionAsset.CODEC, true), (t, k) -> {
        t.conditionAsset = k;
    }, k -> k.conditionAsset).add()).build();
    private ConditionAsset conditionAsset = new AlwaysTrueConditionAsset();

    @Override
    @Nonnull
    public SpaceAndDepthMaterialProvider.Condition build() {
        return new NotCondition(this.conditionAsset.build());
    }
}

