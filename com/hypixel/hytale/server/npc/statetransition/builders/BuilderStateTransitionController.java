/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.statetransition.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderBase;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderObjectListHelper;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.statetransition.StateTransitionController;
import com.hypixel.hytale.server.npc.statetransition.builders.BuilderStateTransition;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.npc.util.expression.Scope;
import com.hypixel.hytale.server.npc.validators.NPCLoadTimeValidationHelper;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderStateTransitionController
extends BuilderBase<StateTransitionController> {
    protected final BuilderObjectListHelper<BuilderStateTransition.StateTransition> stateTransitionEntries = new BuilderObjectListHelper(BuilderStateTransition.StateTransition.class, this);

    @Override
    @Nonnull
    public String getShortDescription() {
        return "A list of state transitions";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    @Nonnull
    public StateTransitionController build(@Nonnull BuilderSupport builderSupport) {
        return new StateTransitionController(this, builderSupport);
    }

    @Override
    @Nonnull
    public Class<StateTransitionController> category() {
        return StateTransitionController.class;
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Override
    public boolean isEnabled(ExecutionContext context) {
        return true;
    }

    @Override
    @Nonnull
    public Builder<StateTransitionController> readConfig(@Nonnull JsonElement data) {
        this.requireArray(data, this.stateTransitionEntries, null, BuilderDescriptorState.Stable, "A list of state transition entries with lists of actions", null, this.validationHelper);
        return this;
    }

    @Override
    public boolean validate(String configName, @Nonnull NPCLoadTimeValidationHelper validationHelper, @Nonnull ExecutionContext context, Scope globalScope, @Nonnull List<String> errors) {
        return super.validate(configName, validationHelper, context, globalScope, errors) & this.stateTransitionEntries.validate(configName, validationHelper, this.builderManager, context, globalScope, errors);
    }

    @Nullable
    public List<BuilderStateTransition.StateTransition> getStateTransitionEntries(@Nonnull BuilderSupport support) {
        return this.stateTransitionEntries.build(support);
    }
}

