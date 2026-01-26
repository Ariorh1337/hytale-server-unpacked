/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.statemachine.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.corecomponents.statemachine.ActionToggleStateEvaluator;
import javax.annotation.Nonnull;

public class BuilderActionToggleStateEvaluator
extends BuilderActionBase {
    protected boolean enable;

    @Override
    @Nonnull
    public ActionToggleStateEvaluator build(BuilderSupport builderSupport) {
        return new ActionToggleStateEvaluator(this);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Enable or disable the NPC's state evaluator";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Nonnull
    public BuilderActionToggleStateEvaluator readConfig(@Nonnull JsonElement data) {
        this.requireBoolean(data, "Enabled", (boolean b) -> {
            this.enable = b;
        }, BuilderDescriptorState.Stable, "Whether or not to enable the state evaluator", null);
        if (!this.isCreatingDescriptor()) {
            this.stateHelper.setRequiresStateEvaluator();
        }
        return this;
    }

    public boolean isEnable() {
        return this.enable;
    }
}

