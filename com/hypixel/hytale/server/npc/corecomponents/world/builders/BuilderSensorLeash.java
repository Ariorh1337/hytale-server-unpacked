/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.world.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.Feature;
import com.hypixel.hytale.server.npc.asset.builder.holder.DoubleHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleSingleValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.corecomponents.world.SensorLeash;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import javax.annotation.Nonnull;

public class BuilderSensorLeash
extends BuilderSensorBase {
    protected final DoubleHolder range = new DoubleHolder();

    @Override
    @Nonnull
    public SensorLeash build(@Nonnull BuilderSupport builderSupport) {
        builderSupport.setRequireLeashPosition();
        return new SensorLeash(this, builderSupport);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Triggers when the NPC is outside a specified range from the leash point";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Triggers when the NPC is outside a specified range from the leash point";
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Override
    @Nonnull
    public Builder<Sensor> readConfig(@Nonnull JsonElement data) {
        this.requireDouble(data, "Range", this.range, (DoubleValidator)DoubleSingleValidator.greater0(), BuilderDescriptorState.Stable, "The farthest distance allowed from the leash point", null);
        this.provideFeature(Feature.Position);
        return this;
    }

    public double getRange(@Nonnull BuilderSupport builderSupport) {
        return this.range.get(builderSupport.getExecutionContext());
    }
}

