/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.providerevaluators;

import com.hypixel.hytale.server.npc.asset.builder.providerevaluators.ParameterType;
import com.hypixel.hytale.server.npc.asset.builder.providerevaluators.ProviderEvaluator;

public interface ParameterProviderEvaluator
extends ProviderEvaluator {
    public boolean hasParameter(String var1, ParameterType var2);
}

