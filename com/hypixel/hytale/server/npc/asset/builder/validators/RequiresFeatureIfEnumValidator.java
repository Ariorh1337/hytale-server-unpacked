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
import javax.annotation.Nullable;

public class RequiresFeatureIfEnumValidator<E extends Enum<E>>
extends RequiredFeatureValidator {
    @Nonnull
    private final String[] description;
    private final String attribute;
    private final E value;

    private RequiresFeatureIfEnumValidator(String attribute, E value, @Nonnull EnumSet<Feature> feature) {
        this.attribute = attribute;
        this.description = BuilderBase.getDescriptionArray(feature);
        this.value = value;
    }

    @Override
    public boolean validate(FeatureEvaluatorHelper evaluatorHelper) {
        return false;
    }

    @Override
    @Nullable
    public String getErrorMessage(String context) {
        return null;
    }

    public static <E extends Enum<E>> boolean staticValidate(@Nonnull FeatureEvaluatorHelper evaluatorHelper, EnumSet<Feature> requiredFeature, E requiredValue, E value) {
        if (requiredValue != value) {
            return true;
        }
        for (ProviderEvaluator providedFeature : evaluatorHelper.getProviders()) {
            if (!(providedFeature instanceof FeatureProviderEvaluator) || !((FeatureProviderEvaluator)providedFeature).provides(requiredFeature)) continue;
            return true;
        }
        return false;
    }

    @Nonnull
    public static <E extends Enum<E>> RequiresFeatureIfEnumValidator withAttributes(String attribute, E value, @Nonnull EnumSet<Feature> feature) {
        return new RequiresFeatureIfEnumValidator<E>(attribute, value, feature);
    }
}

