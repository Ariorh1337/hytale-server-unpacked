/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.movement.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringNotEmptyValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.corecomponents.movement.SensorMotionController;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import javax.annotation.Nonnull;

public class BuilderSensorMotionController
extends BuilderSensorBase {
    protected String motionControllerName;

    @Override
    @Nonnull
    public Sensor build(BuilderSupport builderSupport) {
        return new SensorMotionController(this);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Test if specific motion controller is active.";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Experimental;
    }

    @Override
    @Nonnull
    public Builder<Sensor> readConfig(@Nonnull JsonElement data) {
        this.requireString(data, "MotionController", (String s) -> {
            this.motionControllerName = s;
        }, (StringValidator)StringNotEmptyValidator.get(), BuilderDescriptorState.Experimental, "Motion controller name to test for", null);
        return this;
    }

    public String getMotionControllerName() {
        return this.motionControllerName;
    }
}

