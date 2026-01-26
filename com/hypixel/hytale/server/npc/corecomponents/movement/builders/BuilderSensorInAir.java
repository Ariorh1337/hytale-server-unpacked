/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.movement.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.corecomponents.movement.SensorInAir;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import javax.annotation.Nonnull;

public class BuilderSensorInAir
extends BuilderSensorBase {
    @Override
    @Nonnull
    public SensorInAir build(BuilderSupport builderSupport) {
        return new SensorInAir(this);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Test if NPC is not on ground";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Return true if NPC is not on ground. No target is returned.";
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Override
    @Nonnull
    public Builder<Sensor> readConfig(JsonElement data) {
        return this;
    }
}

