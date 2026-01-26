/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.statemachine.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.InstructionType;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.corecomponents.statemachine.ActionState;
import javax.annotation.Nonnull;

public class BuilderActionState
extends BuilderActionBase {
    protected String state;
    protected String subState;
    protected int stateIndex;
    protected int subStateIndex;
    protected boolean clearState;
    protected boolean componentLocal;

    @Override
    @Nonnull
    public ActionState build(@Nonnull BuilderSupport builderSupport) {
        return new ActionState(this, builderSupport);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Set state of NPC";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Set state of NPC. The state can be queried with a sensor later on.";
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Nonnull
    public BuilderActionState readConfig(@Nonnull JsonElement data) {
        this.requireStateString(data, "State", true, (state, subState, isDefault) -> {
            this.state = state;
            this.subState = subState;
        }, BuilderDescriptorState.Stable, "State name to set", null);
        this.getBoolean(data, "ClearState", (boolean v) -> {
            this.clearState = v;
        }, true, BuilderDescriptorState.Stable, "Clear the state of things like set once flags on transition", null);
        this.componentLocal = this.isComponent();
        this.registerStateSetter(this.state, this.subState, (main, sub) -> {
            this.stateIndex = main;
            this.subStateIndex = sub;
        });
        this.requireInstructionType(InstructionType.StateChangeAllowedInstructions);
        return this;
    }

    public int getStateIndex() {
        return this.stateIndex;
    }

    public int getSubStateIndex() {
        return this.subStateIndex;
    }

    public boolean isClearState() {
        return this.clearState;
    }

    public boolean isComponentLocal() {
        return this.componentLocal;
    }
}

