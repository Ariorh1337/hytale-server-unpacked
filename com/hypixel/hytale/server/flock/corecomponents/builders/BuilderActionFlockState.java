/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.flock.corecomponents.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.flock.corecomponents.ActionFlockState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.InstructionType;
import com.hypixel.hytale.server.npc.asset.builder.holder.StringHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.StateStringValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import javax.annotation.Nonnull;

public class BuilderActionFlockState
extends BuilderActionBase {
    protected final StringHolder state = new StringHolder();

    @Override
    @Nonnull
    public ActionFlockState build(@Nonnull BuilderSupport builderSupport) {
        return new ActionFlockState(this, builderSupport);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Set state name for flock.";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Sets the state name for the flock the NPC is member of.The flock leader is explicitly excluded from this operation.";
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Nonnull
    public BuilderActionFlockState readConfig(@Nonnull JsonElement data) {
        this.requireString(data, "State", this.state, (StringValidator)StateStringValidator.requireMainState(), BuilderDescriptorState.Stable, "State name to set", null);
        this.requireInstructionType(InstructionType.StateChangeAllowedInstructions);
        return this;
    }

    public String getState(@Nonnull BuilderSupport support) {
        return this.state.get(support.getExecutionContext());
    }
}

