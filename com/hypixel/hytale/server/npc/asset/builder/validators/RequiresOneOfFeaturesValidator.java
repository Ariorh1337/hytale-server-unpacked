/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.validators;

import com.hypixel.hytale.server.npc.asset.builder.BuilderBase;
import com.hypixel.hytale.server.npc.asset.builder.Feature;
import com.hypixel.hytale.server.npc.asset.builder.FeatureEvaluatorHelper;
import com.hypixel.hytale.server.npc.asset.builder.providerevaluators.FeatureProviderEvaluator;
import com.hypixel.hytale.server.npc.asset.builder.providerevaluators.ProviderEvaluator;
import com.hypixel.hytale.server.npc.asset.builder.validators.RequiredFeatureValidator;
import java.util.EnumSet;
import javax.annotation.Nonnull;

public class RequiresOneOfFeaturesValidator
extends RequiredFeatureValidator {
    @Nonnull
    private final EnumSet<Feature> requiredFeature;
    @Nonnull
    private final String[] description;

    private RequiresOneOfFeaturesValidator(@Nonnull EnumSet<Feature> requiredFeature) {
        this.requiredFeature = requiredFeature;
        this.description = BuilderBase.getDescriptionArray(requiredFeature);
    }

    @Override
    public boolean validate(@Nonnull FeatureEvaluatorHelper evaluatorHelper) {
        for (ProviderEvaluator provider : evaluatorHelper.getProviders()) {
            if (!(provider instanceof FeatureProviderEvaluator) || !((FeatureProviderEvaluator)provider).provides(this.requiredFeature)) continue;
            return true;
        }
        return false;
    }

    @Override
    @Nonnull
    public String getErrorMessage(String context) {
        return String.format("At least one of required features %s must be provided at %s", String.join((CharSequence)", ", this.description), context);
    }

    @Nonnull
    public static RequiresOneOfFeaturesValidator withFeatures(@Nonnull EnumSet<Feature> requiredFeature) {
        return new RequiresOneOfFeaturesValidator(requiredFeature);
    }
}

