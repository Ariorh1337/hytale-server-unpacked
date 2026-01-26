/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.entity.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.entity.SensorPlayer;
import com.hypixel.hytale.server.npc.corecomponents.entity.builders.BuilderSensorEntityBase;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import javax.annotation.Nonnull;

public class BuilderSensorPlayer
extends BuilderSensorEntityBase {
    @Override
    @Nonnull
    public SensorPlayer build(@Nonnull BuilderSupport builderSupport) {
        return new SensorPlayer(this, builderSupport);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Test if player matching specific attributes and filters is in range";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Return true if player matching specific attributes and filters is in range. Target is player.";
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Override
    @Nonnull
    public Builder<Sensor> readConfig(@Nonnull JsonElement data) {
        super.readConfig(data);
        return this;
    }
}

