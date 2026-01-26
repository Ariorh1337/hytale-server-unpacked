/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.utility.builders;

import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.utility.SensorOr;
import com.hypixel.hytale.server.npc.corecomponents.utility.builders.BuilderSensorMany;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderSensorOr
extends BuilderSensorMany {
    @Override
    @Nonnull
    public String getShortDescription() {
        return "Logical OR of list of sensors";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Evaluate sensors and execute action when at least one sensor signals true. Target is provided by first sensor signalling true.";
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Override
    @Nullable
    public SensorOr build(@Nonnull BuilderSupport builderSupport) {
        Object sensors = this.objectListHelper.build(builderSupport);
        if (sensors.isEmpty()) {
            return null;
        }
        return new SensorOr(this, builderSupport, (List<Sensor>)sensors);
    }
}

