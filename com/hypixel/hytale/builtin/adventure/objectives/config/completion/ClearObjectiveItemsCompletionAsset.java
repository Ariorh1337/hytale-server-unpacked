/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.objectives.config.completion;

import com.hypixel.hytale.builtin.adventure.objectives.config.completion.ObjectiveCompletionAsset;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class ClearObjectiveItemsCompletionAsset
extends ObjectiveCompletionAsset {
    public static final BuilderCodec<ClearObjectiveItemsCompletionAsset> CODEC = BuilderCodec.builder(ClearObjectiveItemsCompletionAsset.class, ClearObjectiveItemsCompletionAsset::new, BASE_CODEC).build();

    protected ClearObjectiveItemsCompletionAsset() {
    }

    @Override
    @Nonnull
    public String toString() {
        return "ClearObjectiveItemsCompletionAsset{} " + super.toString();
    }
}

