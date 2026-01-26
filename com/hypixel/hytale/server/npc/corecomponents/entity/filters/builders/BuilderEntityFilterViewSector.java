/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.entity.filters.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.ComponentContext;
import com.hypixel.hytale.server.npc.asset.builder.InstructionType;
import com.hypixel.hytale.server.npc.asset.builder.holder.FloatHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleRangeValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleValidator;
import com.hypixel.hytale.server.npc.corecomponents.IEntityFilter;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderEntityFilterBase;
import com.hypixel.hytale.server.npc.corecomponents.entity.filters.EntityFilterViewSector;
import javax.annotation.Nonnull;

public class BuilderEntityFilterViewSector
extends BuilderEntityFilterBase {
    protected final FloatHolder viewSector = new FloatHolder();

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Matches entities within the given view sector";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    @Nonnull
    public IEntityFilter build(@Nonnull BuilderSupport builderSupport) {
        return new EntityFilterViewSector(this, builderSupport);
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Override
    @Nonnull
    public Builder<IEntityFilter> readConfig(@Nonnull JsonElement data) {
        this.getFloat(data, "ViewSector", this.viewSector, 0.0, (DoubleValidator)DoubleRangeValidator.between(0.0, 360.0), BuilderDescriptorState.Stable, "View sector to test entities in", null);
        this.requireContext(InstructionType.Any, ComponentContext.NotSelfEntitySensor);
        return this;
    }

    public float getViewSectorRadians(@Nonnull BuilderSupport builderSupport) {
        return (float)Math.PI / 180 * this.viewSector.get(builderSupport.getExecutionContext());
    }
}

