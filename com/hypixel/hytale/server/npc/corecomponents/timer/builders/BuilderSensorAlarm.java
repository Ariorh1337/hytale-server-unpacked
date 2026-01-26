/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.timer.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.BooleanHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.EnumHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.StringHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringNotEmptyValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.corecomponents.timer.SensorAlarm;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import com.hypixel.hytale.server.npc.util.Alarm;
import javax.annotation.Nonnull;

public class BuilderSensorAlarm
extends BuilderSensorBase {
    protected final StringHolder name = new StringHolder();
    protected final EnumHolder<SensorAlarm.State> state = new EnumHolder();
    protected final BooleanHolder clear = new BooleanHolder();

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Check the state of a named alarm";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return "Check the state of a named alarm and optionally clear it if the time has passed";
    }

    @Override
    @Nonnull
    public SensorAlarm build(@Nonnull BuilderSupport builderSupport) {
        return new SensorAlarm(this, builderSupport);
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Override
    @Nonnull
    public Builder<Sensor> readConfig(@Nonnull JsonElement data) {
        this.requireString(data, "Name", this.name, (StringValidator)StringNotEmptyValidator.get(), BuilderDescriptorState.Stable, "The name of the alarm to check", null);
        this.requireEnum(data, "State", this.state, SensorAlarm.State.class, BuilderDescriptorState.Stable, "The state to check for", null);
        this.getBoolean(data, "Clear", this.clear, false, BuilderDescriptorState.Stable, "Whether to clear the alarm (unset it) if it has passed", null);
        return this;
    }

    public Alarm getAlarm(@Nonnull BuilderSupport support) {
        return support.getAlarm(this.name.get(support.getExecutionContext()));
    }

    public SensorAlarm.State getState(@Nonnull BuilderSupport support) {
        return this.state.get(support.getExecutionContext());
    }

    public boolean isClear(@Nonnull BuilderSupport support) {
        return this.clear.get(support.getExecutionContext());
    }
}

