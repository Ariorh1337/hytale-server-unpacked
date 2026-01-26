/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.flock.corecomponents.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.flock.corecomponents.SensorInflictedDamage;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.Feature;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import javax.annotation.Nonnull;

public class BuilderSensorInflictedDamage
extends BuilderSensorBase {
    protected SensorInflictedDamage.Target target;
    protected boolean friendlyFire;

    @Override
    @Nonnull
    public SensorInflictedDamage build(BuilderSupport builderSupport) {
        return new SensorInflictedDamage(this);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Test if an individual or the flock it belongs to inflicted combat damage";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Return true if an individual or the flock it belongs to inflicted combat damage. Target position is entity which received most damage.";
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Override
    @Nonnull
    public Builder<Sensor> readConfig(@Nonnull JsonElement data) {
        this.getEnum(data, "Target", (E v) -> {
            this.target = v;
        }, SensorInflictedDamage.Target.class, SensorInflictedDamage.Target.Self, BuilderDescriptorState.Stable, "Who to check has inflicted damage", null);
        this.getBoolean(data, "FriendlyFire", (boolean v) -> {
            this.friendlyFire = v;
        }, false, BuilderDescriptorState.Stable, "Consider friendly fire too", null);
        this.provideFeature(Feature.LiveEntity);
        return this;
    }

    public boolean isFriendlyFire() {
        return this.friendlyFire;
    }

    public SensorInflictedDamage.Target getTarget() {
        return this.target;
    }
}

