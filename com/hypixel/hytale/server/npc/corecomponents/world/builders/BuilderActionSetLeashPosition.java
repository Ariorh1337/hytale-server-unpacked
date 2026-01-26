/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.world.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.Feature;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.corecomponents.world.ActionSetLeashPosition;
import javax.annotation.Nonnull;

public class BuilderActionSetLeashPosition
extends BuilderActionBase {
    protected boolean toTarget;
    protected boolean toCurrent;

    @Override
    @Nonnull
    public ActionSetLeashPosition build(@Nonnull BuilderSupport builderSupport) {
        builderSupport.setRequireLeashPosition();
        return new ActionSetLeashPosition(this);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Sets the NPCs current position to the spawn/leash position";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Sets the NPCs current position to the spawn/leash position to be used with the Leash Sensor.";
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Nonnull
    public BuilderActionSetLeashPosition readConfig(@Nonnull JsonElement data) {
        this.getBoolean(data, "ToCurrent", (boolean b) -> {
            this.toCurrent = b;
        }, false, BuilderDescriptorState.Stable, "Set to the NPCs current position.", null);
        this.getBoolean(data, "ToTarget", (boolean b) -> {
            this.toTarget = b;
        }, false, BuilderDescriptorState.Stable, "Set to the target position.", null);
        this.validateAny("ToCurrent", this.toCurrent, "ToTarget", this.toTarget);
        this.requireFeatureIf("ToTarget", true, this.toTarget, Feature.AnyEntity);
        return this;
    }

    public boolean isToTarget() {
        return this.toTarget;
    }

    public boolean isToCurrent() {
        return this.toCurrent;
    }
}

