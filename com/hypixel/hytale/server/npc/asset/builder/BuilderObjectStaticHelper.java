/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hypixel.hytale.server.npc.asset.builder.BuilderContext;
import com.hypixel.hytale.server.npc.asset.builder.BuilderManager;
import com.hypixel.hytale.server.npc.asset.builder.BuilderObjectReferenceHelper;
import com.hypixel.hytale.server.npc.asset.builder.BuilderParameters;
import com.hypixel.hytale.server.npc.asset.builder.BuilderValidationHelper;
import com.hypixel.hytale.server.npc.asset.builder.InternalReferenceResolver;
import com.hypixel.hytale.server.npc.asset.builder.holder.StringHolder;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderObjectStaticHelper<T>
extends BuilderObjectReferenceHelper<T> {
    public BuilderObjectStaticHelper(Class<?> classType, BuilderContext owner) {
        super(classType, owner);
    }

    @Override
    public void readConfig(@Nonnull JsonElement data, @Nonnull BuilderManager builderManager, @Nonnull BuilderParameters builderParameters, @Nonnull BuilderValidationHelper builderValidationHelper) {
        super.readConfig(data, builderManager, builderParameters, builderValidationHelper);
        if (!this.isFinal()) {
            throw new IllegalStateException("Static object must be static, at: " + this.getBreadCrumbs());
        }
    }

    @Override
    protected void setInternalReference(StringHolder holder, InternalReferenceResolver referenceResolver) {
        throw new IllegalStateException("Static object cannot contain a reference, at: " + this.getBreadCrumbs());
    }

    @Override
    protected void setFileReference(StringHolder holder, JsonObject jsonObject, BuilderManager builderManager) {
        throw new IllegalStateException("Static object cannot contain a reference, at: " + this.getBreadCrumbs());
    }

    @Nullable
    public T staticBuild(@Nonnull BuilderManager manager) {
        return this.getBuilder(manager, null, null).build(null);
    }
}

