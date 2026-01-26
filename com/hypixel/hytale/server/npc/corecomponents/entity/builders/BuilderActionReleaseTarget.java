/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.entity.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.StringHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringNotEmptyValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.corecomponents.entity.ActionReleaseTarget;
import javax.annotation.Nonnull;

public class BuilderActionReleaseTarget
extends BuilderActionBase {
    protected final StringHolder targetSlot = new StringHolder();

    @Override
    @Nonnull
    public ActionReleaseTarget build(@Nonnull BuilderSupport builderSupport) {
        return new ActionReleaseTarget(this, builderSupport);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Clear locked target";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Clear locked target for NPC.";
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Nonnull
    public BuilderActionReleaseTarget readConfig(@Nonnull JsonElement data) {
        this.getString(data, "TargetSlot", this.targetSlot, "LockedTarget", (StringValidator)StringNotEmptyValidator.get(), BuilderDescriptorState.Stable, "The target slot to release", null);
        return this;
    }

    public int getTargetSlot(@Nonnull BuilderSupport support) {
        return support.getTargetSlot(this.targetSlot.get(support.getExecutionContext()));
    }
}

