/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.npccombatactionevaluator.corecomponents.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.builtin.npccombatactionevaluator.corecomponents.ActionAddToTargetMemory;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.Feature;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.instructions.Action;
import javax.annotation.Nonnull;

public class BuilderActionAddToTargetMemory
extends BuilderActionBase {
    @Override
    @Nonnull
    public String getShortDescription() {
        return "Adds the passed target from the sensor to the hostile target memory";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    @Nonnull
    public Action build(BuilderSupport builderSupport) {
        return new ActionAddToTargetMemory(this);
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Override
    @Nonnull
    public Builder<Action> readConfig(JsonElement data) {
        this.requireFeature(Feature.LiveEntity);
        return this;
    }
}

