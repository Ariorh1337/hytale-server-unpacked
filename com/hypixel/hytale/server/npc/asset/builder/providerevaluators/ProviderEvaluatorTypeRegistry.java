/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.providerevaluators;

import com.google.gson.GsonBuilder;
import com.hypixel.hytale.server.npc.asset.builder.providerevaluators.ProviderEvaluator;
import com.hypixel.hytale.server.npc.asset.builder.providerevaluators.UnconditionalFeatureProviderEvaluator;
import com.hypixel.hytale.server.npc.asset.builder.providerevaluators.UnconditionalParameterProviderEvaluator;
import com.hypixel.hytale.server.npc.asset.builder.validators.SubTypeTypeAdapterFactory;
import javax.annotation.Nonnull;

public class ProviderEvaluatorTypeRegistry {
    @Nonnull
    public static GsonBuilder registerTypes(@Nonnull GsonBuilder gsonBuilder) {
        SubTypeTypeAdapterFactory factory = SubTypeTypeAdapterFactory.of(ProviderEvaluator.class, "Type");
        factory.registerSubType(UnconditionalFeatureProviderEvaluator.class, "ProvidesFeatureUnconditionally");
        factory.registerSubType(UnconditionalParameterProviderEvaluator.class, "ProvidesParameterUnconditionally");
        gsonBuilder.registerTypeAdapterFactory(factory);
        return gsonBuilder;
    }
}

