/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.npccombatactionevaluator.corecomponents.builders;

import com.hypixel.hytale.builtin.npccombatactionevaluator.corecomponents.SensorHasHostileTargetMemory;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import javax.annotation.Nonnull;

public class BuilderSensorHasHostileTargetMemory
extends BuilderSensorBase {
    @Override
    @Nonnull
    public Sensor build(BuilderSupport builderSupport) {
        return new SensorHasHostileTargetMemory(this);
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Checks if there is currently a hostile target in the target memory.";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }
}

