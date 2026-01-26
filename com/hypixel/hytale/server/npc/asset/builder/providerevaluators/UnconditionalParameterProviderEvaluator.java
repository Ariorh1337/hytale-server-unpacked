/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.providerevaluators;

import com.hypixel.hytale.server.npc.asset.builder.BuilderManager;
import com.hypixel.hytale.server.npc.asset.builder.providerevaluators.ParameterProviderEvaluator;
import com.hypixel.hytale.server.npc.asset.builder.providerevaluators.ParameterType;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public class UnconditionalParameterProviderEvaluator
implements ParameterProviderEvaluator {
    private final Map<String, ParameterType> parameters = new HashMap<String, ParameterType>();

    public UnconditionalParameterProviderEvaluator(@Nonnull String[] parameters, @Nonnull ParameterType[] types) {
        if (parameters.length != types.length) {
            throw new IllegalArgumentException("Different number of parameters to types");
        }
        for (int i = 0; i < parameters.length; ++i) {
            this.parameters.put(parameters[i], types[i]);
        }
    }

    @Override
    public boolean hasParameter(String parameter, ParameterType type) {
        return this.parameters.get(parameter) == type;
    }

    @Override
    public void resolveReferences(BuilderManager builderManager) {
    }
}

