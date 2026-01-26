/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.entity.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.Feature;
import com.hypixel.hytale.server.npc.asset.builder.holder.StringHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringNotEmptyValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.corecomponents.entity.ActionSetMarkedTarget;
import com.hypixel.hytale.server.npc.instructions.Action;
import javax.annotation.Nonnull;

public class BuilderActionSetMarkedTarget
extends BuilderActionBase {
    protected final StringHolder targetSlot = new StringHolder();

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Explicitly sets a marked target in a given slot.";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    @Nonnull
    public Action build(@Nonnull BuilderSupport builderSupport) {
        return new ActionSetMarkedTarget(this, builderSupport);
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Nonnull
    public BuilderActionSetMarkedTarget readConfig(@Nonnull JsonElement data) {
        this.getString(data, "TargetSlot", this.targetSlot, "LockedTarget", (StringValidator)StringNotEmptyValidator.get(), BuilderDescriptorState.Stable, "The target slot to set a target to.", null);
        this.requireFeature(Feature.LiveEntity);
        return this;
    }

    public int getTargetSlot(@Nonnull BuilderSupport support) {
        return support.getTargetSlot(this.targetSlot.get(support.getExecutionContext()));
    }
}

