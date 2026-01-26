/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.providerevaluators;

import com.hypixel.hytale.server.npc.asset.builder.Feature;
import com.hypixel.hytale.server.npc.asset.builder.providerevaluators.ProviderEvaluator;
import java.util.EnumSet;

public interface FeatureProviderEvaluator
extends ProviderEvaluator {
    public boolean provides(EnumSet<Feature> var1);
}

