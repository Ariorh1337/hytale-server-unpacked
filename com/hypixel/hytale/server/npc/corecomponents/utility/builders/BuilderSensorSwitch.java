/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.utility.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.BooleanHolder;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.corecomponents.utility.SensorSwitch;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import java.util.Set;
import javax.annotation.Nonnull;

public class BuilderSensorSwitch
extends BuilderSensorBase {
    protected final BooleanHolder switchHolder = new BooleanHolder();

    @Override
    @Nonnull
    public SensorSwitch build(@Nonnull BuilderSupport builderSupport) {
        return new SensorSwitch(this, builderSupport);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Check if a computed boolean is true";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Check if a computed boolean is true";
    }

    @Override
    public void registerTags(@Nonnull Set<String> tags) {
        super.registerTags(tags);
        tags.add("logic");
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Override
    @Nonnull
    public Builder<Sensor> readConfig(@Nonnull JsonElement data) {
        this.requireBoolean(data, "Switch", this.switchHolder, BuilderDescriptorState.Stable, "The switch to check", "The switch to check");
        return this;
    }

    public boolean getSwitch(@Nonnull BuilderSupport builderSupport) {
        return this.switchHolder.get(builderSupport.getExecutionContext());
    }
}

