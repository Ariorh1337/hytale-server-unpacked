/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.npcobjectives.npc.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.builtin.adventure.npcobjectives.npc.ActionStartObjective;
import com.hypixel.hytale.builtin.adventure.npcobjectives.npc.validators.ObjectiveExistsValidator;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.InstructionType;
import com.hypixel.hytale.server.npc.asset.builder.holder.AssetHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.AssetValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import java.util.EnumSet;
import javax.annotation.Nonnull;

public class BuilderActionStartObjective
extends BuilderActionBase {
    protected final AssetHolder objectiveId = new AssetHolder();

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Start the given objective for the currently iterated player in the interaction instruction";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    @Nonnull
    public ActionStartObjective build(@Nonnull BuilderSupport builderSupport) {
        return new ActionStartObjective(this, builderSupport);
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Nonnull
    public BuilderActionStartObjective readConfig(@Nonnull JsonElement data) {
        this.requireAsset(data, "Objective", this.objectiveId, (AssetValidator)ObjectiveExistsValidator.required(), BuilderDescriptorState.Stable, "The task to start", null);
        this.requireInstructionType(EnumSet.of(InstructionType.Interaction));
        return this;
    }

    public String getObjectiveId(@Nonnull BuilderSupport support) {
        return this.objectiveId.get(support.getExecutionContext());
    }
}

