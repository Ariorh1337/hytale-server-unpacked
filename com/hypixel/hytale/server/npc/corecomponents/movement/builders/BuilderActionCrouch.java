/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.movement.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.BooleanHolder;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.corecomponents.movement.ActionCrouch;
import com.hypixel.hytale.server.npc.instructions.Action;
import javax.annotation.Nonnull;

public class BuilderActionCrouch
extends BuilderActionBase {
    protected final BooleanHolder crouching = new BooleanHolder();

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Set NPC crouching state";
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
    public Action build(@Nonnull BuilderSupport builderSupport) {
        return new ActionCrouch(this, this.crouching.get(builderSupport.getExecutionContext()));
    }

    @Override
    @Nonnull
    public Builder<Action> readConfig(@Nonnull JsonElement data) {
        this.getBoolean(data, "Crouch", this.crouching, true, BuilderDescriptorState.Stable, "True for crouching, false for non-crouching", null);
        return this;
    }
}

