/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderBase;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderObjectReferenceHelper;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.DoubleHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleSingleValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleValidator;
import com.hypixel.hytale.server.npc.corecomponents.WeightedAction;
import com.hypixel.hytale.server.npc.instructions.Action;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.npc.util.expression.Scope;
import com.hypixel.hytale.server.npc.validators.NPCLoadTimeValidationHelper;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderWeightedAction
extends BuilderBase<WeightedAction> {
    private final BuilderObjectReferenceHelper<Action> action = new BuilderObjectReferenceHelper(Action.class, this);
    private final DoubleHolder weight = new DoubleHolder();

    @Override
    @Nonnull
    public WeightedAction build(@Nonnull BuilderSupport builderSupport) {
        return new WeightedAction(this, builderSupport);
    }

    @Override
    @Nonnull
    public Class<WeightedAction> category() {
        return WeightedAction.class;
    }

    @Override
    public boolean isEnabled(ExecutionContext context) {
        return true;
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "A wrapped and weighted action intended to be used for Random action lists.";
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

    @Override
    @Nonnull
    public Builder<WeightedAction> readConfig(@Nonnull JsonElement data) {
        this.requireObject(data, "Action", this.action, BuilderDescriptorState.Stable, "The action to run", null, this.validationHelper);
        this.requireDouble(data, "Weight", this.weight, (DoubleValidator)DoubleSingleValidator.greater0(), BuilderDescriptorState.Stable, "The weight representing how likely this action is to run", null);
        return this;
    }

    @Override
    public boolean validate(String configName, NPCLoadTimeValidationHelper validationHelper, @Nonnull ExecutionContext context, Scope globalScope, @Nonnull List<String> errors) {
        return this.action.validate(configName, validationHelper, this.builderManager, context, globalScope, errors);
    }

    @Nullable
    public Action getAction(@Nonnull BuilderSupport support) {
        return this.action.build(support);
    }

    public double getWeight(@Nonnull BuilderSupport support) {
        return this.weight.get(support.getExecutionContext());
    }
}

