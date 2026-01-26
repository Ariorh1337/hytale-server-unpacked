/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderBase;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringNotEmptyValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringValidator;
import com.hypixel.hytale.server.spawning.ISpawnable;
import javax.annotation.Nonnull;

public abstract class BuilderBaseWithType<T>
extends BuilderBase<T>
implements ISpawnable {
    private String type;

    @Override
    public Builder<T> readCommonConfig(JsonElement data) {
        return super.readCommonConfig(data);
    }

    protected void readTypeKey(@Nonnull JsonElement data, String typeKey) {
        this.requireString(data, typeKey, (String s) -> {
            this.type = s;
        }, (StringValidator)StringNotEmptyValidator.get(), BuilderDescriptorState.Stable, "Type field", null);
    }

    protected void readTypeKey(@Nonnull JsonElement data) {
        this.readTypeKey(data, "Type");
    }

    public String getType() {
        return this.type;
    }
}

