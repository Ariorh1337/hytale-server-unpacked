/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.utility.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.BooleanHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.StringHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringNotEmptyValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.corecomponents.utility.SensorFlag;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import javax.annotation.Nonnull;

public class BuilderSensorFlag
extends BuilderSensorBase {
    protected final StringHolder name = new StringHolder();
    protected final BooleanHolder value = new BooleanHolder();

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Test if a named flag is set or not";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    @Nonnull
    public Sensor build(@Nonnull BuilderSupport builderSupport) {
        return new SensorFlag(this, builderSupport);
    }

    @Override
    @Nonnull
    public Builder<Sensor> readConfig(@Nonnull JsonElement data) {
        this.requireString(data, "Name", this.name, (StringValidator)StringNotEmptyValidator.get(), BuilderDescriptorState.Stable, "The name of the flag", null);
        this.getBoolean(data, "Set", this.value, true, BuilderDescriptorState.Stable, "Whether the flag should be set or not", null);
        return this;
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    public int getFlagSlot(@Nonnull BuilderSupport support) {
        String flag = this.name.get(support.getExecutionContext());
        return support.getFlagSlot(flag);
    }

    public boolean getValue(@Nonnull BuilderSupport support) {
        return this.value.get(support.getExecutionContext());
    }
}

