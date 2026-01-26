/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.movement.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.NumberArrayHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleArrayValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleSequenceValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.corecomponents.movement.ActionOverrideAltitude;
import com.hypixel.hytale.server.npc.instructions.Action;
import javax.annotation.Nonnull;

public class BuilderActionOverrideAltitude
extends BuilderActionBase {
    protected final NumberArrayHolder desiredAltitudeRange = new NumberArrayHolder();

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Temporarily override the preferred altitude of a flying NPC";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Temporarily override the preferred altitude of a flying NPC. Must be refreshed each tick";
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Override
    @Nonnull
    public Action build(@Nonnull BuilderSupport builderSupport) {
        return new ActionOverrideAltitude(this, builderSupport);
    }

    @Override
    @Nonnull
    public Builder<Action> readConfig(@Nonnull JsonElement data) {
        this.requireDoubleRange(data, "DesiredAltitudeRange", this.desiredAltitudeRange, (DoubleArrayValidator)DoubleSequenceValidator.betweenWeaklyMonotonic(0.0, Double.MAX_VALUE), BuilderDescriptorState.Stable, "The desired altitude range", null);
        return this;
    }

    public double[] getDesiredAltitudeRange(@Nonnull BuilderSupport support) {
        return this.desiredAltitudeRange.get(support.getExecutionContext());
    }
}

