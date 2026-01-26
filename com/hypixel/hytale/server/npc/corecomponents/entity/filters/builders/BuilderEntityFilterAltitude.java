/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.entity.filters.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.NumberArrayHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleArrayValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleSequenceValidator;
import com.hypixel.hytale.server.npc.corecomponents.IEntityFilter;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderEntityFilterBase;
import com.hypixel.hytale.server.npc.corecomponents.entity.filters.EntityFilterAltitude;
import javax.annotation.Nonnull;

public class BuilderEntityFilterAltitude
extends BuilderEntityFilterBase {
    protected final NumberArrayHolder altitudeRange = new NumberArrayHolder();

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Matches targets if they're within the defined range above the ground";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    @Nonnull
    public IEntityFilter build(@Nonnull BuilderSupport builderSupport) {
        return new EntityFilterAltitude(this, builderSupport);
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Override
    @Nonnull
    public Builder<IEntityFilter> readConfig(@Nonnull JsonElement data) {
        this.requireDoubleArray(data, "AltitudeRange", this.altitudeRange, 0, Integer.MAX_VALUE, (DoubleArrayValidator)DoubleSequenceValidator.betweenWeaklyMonotonic(0.0, Double.MAX_VALUE), BuilderDescriptorState.Stable, "The range above the ground to match", null);
        return this;
    }

    public double[] getAltitudeRange(@Nonnull BuilderSupport support) {
        return this.altitudeRange.get(support.getExecutionContext());
    }
}

