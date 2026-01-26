/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.utility.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.corecomponents.utility.SensorAny;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import java.util.Set;
import javax.annotation.Nonnull;

public class BuilderSensorAny
extends BuilderSensorBase {
    @Override
    @Nonnull
    public Sensor build(BuilderSupport builderSupport) {
        if (!this.once) {
            return Sensor.NULL;
        }
        return new SensorAny(this);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Return always true";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Sensor always signals true but doesn't return a target.";
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
    public Builder<Sensor> readConfig(JsonElement data) {
        return this;
    }
}

