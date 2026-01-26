/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.instructions.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderBase;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderObjectListHelper;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.instructions.Action;
import com.hypixel.hytale.server.npc.instructions.ActionList;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.npc.util.expression.Scope;
import com.hypixel.hytale.server.npc.validators.NPCLoadTimeValidationHelper;
import java.util.List;
import javax.annotation.Nonnull;

public class BuilderActionList
extends BuilderBase<ActionList> {
    protected final BuilderObjectListHelper<Action> actions = new BuilderObjectListHelper(Action.class, this);

    @Override
    @Nonnull
    public String getShortDescription() {
        return "An array of actions to be executed";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    @Nonnull
    public ActionList build(@Nonnull BuilderSupport builderSupport) {
        Object actions = this.actions.build(builderSupport);
        return actions != null && !actions.isEmpty() ? new ActionList((Action[])actions.toArray(Action[]::new)) : ActionList.EMPTY_ACTION_LIST;
    }

    @Override
    @Nonnull
    public Class<ActionList> category() {
        return ActionList.class;
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

    @Nonnull
    public BuilderActionList readConfig(@Nonnull JsonElement data) {
        this.requireArray(data, this.actions, null, BuilderDescriptorState.Stable, "List of actions", null, this.validationHelper);
        return this;
    }

    @Override
    public boolean validate(String configName, @Nonnull NPCLoadTimeValidationHelper validationHelper, @Nonnull ExecutionContext context, Scope globalScope, @Nonnull List<String> errors) {
        return super.validate(configName, validationHelper, context, globalScope, errors) & this.actions.validate(configName, validationHelper, this.builderManager, context, globalScope, errors);
    }
}

