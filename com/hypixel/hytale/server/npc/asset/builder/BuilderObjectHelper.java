/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderContext;
import com.hypixel.hytale.server.npc.asset.builder.BuilderManager;
import com.hypixel.hytale.server.npc.asset.builder.BuilderParameters;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.BuilderValidationHelper;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.npc.util.expression.Scope;
import com.hypixel.hytale.server.npc.validators.NPCLoadTimeValidationHelper;
import java.util.List;
import javax.annotation.Nullable;

public abstract class BuilderObjectHelper<T>
implements BuilderContext {
    protected final Class<?> classType;
    protected BuilderParameters builderParameters;
    protected final BuilderContext owner;

    protected BuilderObjectHelper(Class<?> classType, BuilderContext owner) {
        this.owner = owner;
        this.classType = classType;
    }

    @Nullable
    public abstract T build(BuilderSupport var1);

    public abstract boolean validate(String var1, NPCLoadTimeValidationHelper var2, BuilderManager var3, ExecutionContext var4, Scope var5, List<String> var6);

    @Override
    public BuilderContext getOwner() {
        return this.owner;
    }

    public final Class<?> getClassType() {
        return this.classType;
    }

    public void readConfig(JsonElement data, BuilderManager builderManager, BuilderParameters builderParameters, BuilderValidationHelper builderValidationHelper) {
        this.builderParameters = builderParameters;
    }

    public abstract boolean isPresent();
}

