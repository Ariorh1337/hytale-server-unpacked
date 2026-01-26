/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.entity.filters.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.IEntityFilter;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderEntityFilterBase;
import com.hypixel.hytale.server.npc.corecomponents.entity.filters.EntityFilterMovementState;
import com.hypixel.hytale.server.npc.movement.MovementState;
import javax.annotation.Nonnull;

public class BuilderEntityFilterMovementState
extends BuilderEntityFilterBase {
    protected MovementState movementState;

    @Override
    @Nonnull
    public EntityFilterMovementState build(BuilderSupport builderSupport) {
        return new EntityFilterMovementState(this);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Check if the entity is in the given movement state";
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
    public Builder<IEntityFilter> readConfig(@Nonnull JsonElement data) {
        this.requireEnum(data, "State", (E e) -> {
            this.movementState = e;
        }, MovementState.class, BuilderDescriptorState.Stable, "The movement state to check", null);
        return this;
    }

    public MovementState getMovementState() {
        return this.movementState;
    }
}

