/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.utility.builders;

import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.utility.SensorAnd;
import com.hypixel.hytale.server.npc.corecomponents.utility.builders.BuilderSensorMany;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderSensorAnd
extends BuilderSensorMany {
    @Override
    @Nonnull
    public String getShortDescription() {
        return "Logical AND of list of sensors";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Evaluate all sensors and execute action only when all sensor signal true. Target is provided by first sensor.";
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Override
    @Nullable
    public SensorAnd build(@Nonnull BuilderSupport builderSupport) {
        Object sensors = this.objectListHelper.build(builderSupport);
        if (sensors.isEmpty()) {
            return null;
        }
        return new SensorAnd(this, builderSupport, (List<Sensor>)sensors);
    }
}

