/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.holder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderBase;
import com.hypixel.hytale.server.npc.asset.builder.BuilderParameters;
import com.hypixel.hytale.server.npc.asset.builder.holder.StringArrayHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.AssetValidator;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AssetArrayHolder
extends StringArrayHolder {
    protected AssetValidator assetValidator;

    public void readJSON(@Nonnull JsonElement requiredJsonElement, int minLength, int maxLength, AssetValidator validator, String name, @Nonnull BuilderParameters builderParameters) {
        this.readJSON(requiredJsonElement, minLength, maxLength, name, builderParameters);
        this.assetValidator = validator;
    }

    public void readJSON(JsonElement optionalJsonElement, int minLength, int maxLength, String[] defaultValue, AssetValidator validator, String name, @Nonnull BuilderParameters builderParameters) {
        this.readJSON(optionalJsonElement, minLength, maxLength, defaultValue, name, builderParameters);
        this.assetValidator = validator;
    }

    @Override
    @Nullable
    public String[] get(ExecutionContext executionContext) {
        String[] value = this.rawGet(executionContext);
        this.validateRelations(executionContext, value);
        return value;
    }

    @Override
    @Nullable
    public String[] rawGet(ExecutionContext executionContext) {
        String[] value = this.expression.getStringArray(executionContext);
        if (this.assetValidator != null) {
            BuilderBase.validateAssetList(value, this.assetValidator, this.name, true);
        }
        return value;
    }

    public void staticValidate() {
        if (this.assetValidator == null || !this.expression.isStatic()) {
            return;
        }
        BuilderBase.validateAssetList(this.expression.getStringArray(null), this.assetValidator, this.name, true);
    }
}

