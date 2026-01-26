/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.spaceanddepth.conditionassets;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.spaceanddepth.conditionassets.ConditionAsset;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.spaceanddepth.SpaceAndDepthMaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.spaceanddepth.conditions.OrCondition;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import java.util.ArrayList;
import javax.annotation.Nonnull;

public class OrConditionAsset
extends ConditionAsset {
    public static final BuilderCodec<OrConditionAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(OrConditionAsset.class, OrConditionAsset::new, ConditionAsset.ABSTRACT_CODEC).append(new KeyedCodec<T[]>("Conditions", new ArrayCodec(ConditionAsset.CODEC, ConditionAsset[]::new), true), (t, k) -> {
        t.conditionAssets = k;
    }, k -> k.conditionAssets).addValidator(Validators.nonNullArrayElements()).add()).build();
    private ConditionAsset[] conditionAssets = new ConditionAsset[0];

    @Override
    @Nonnull
    public SpaceAndDepthMaterialProvider.Condition build() {
        ArrayList<SpaceAndDepthMaterialProvider.Condition> conditions = new ArrayList<SpaceAndDepthMaterialProvider.Condition>(this.conditionAssets.length);
        for (ConditionAsset asset : this.conditionAssets) {
            if (asset == null) {
                LoggerUtil.getLogger().warning("Null condition asset found, skipped.");
                continue;
            }
            conditions.add(asset.build());
        }
        return new OrCondition(conditions);
    }
}

