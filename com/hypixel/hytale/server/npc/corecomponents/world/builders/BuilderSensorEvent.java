/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.world.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.Feature;
import com.hypixel.hytale.server.npc.asset.builder.holder.DoubleHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.EnumHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.StringHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleSingleValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringNullOrNotEmptyValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.corecomponents.world.SensorEvent;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import javax.annotation.Nonnull;

public abstract class BuilderSensorEvent
extends BuilderSensorBase {
    protected final DoubleHolder range = new DoubleHolder();
    protected final EnumHolder<SensorEvent.EventSearchType> searchType = new EnumHolder();
    protected final StringHolder lockOnTargetSlot = new StringHolder();

    @Override
    @Nonnull
    public Builder<Sensor> readConfig(@Nonnull JsonElement data) {
        this.requireDouble(data, "Range", this.range, (DoubleValidator)DoubleSingleValidator.greater0(), BuilderDescriptorState.Stable, "Max range to listen in", null);
        this.getEnum(data, "SearchType", this.searchType, SensorEvent.EventSearchType.class, SensorEvent.EventSearchType.PlayerOnly, BuilderDescriptorState.Stable, "Whether to listen for events triggered by players, npcs, or both in a certain order", null);
        this.getString(data, "TargetSlot", this.lockOnTargetSlot, null, (StringValidator)StringNullOrNotEmptyValidator.get(), BuilderDescriptorState.Stable, "A target slot to place the target in. If omitted, no slot will be used", null);
        this.provideFeature(Feature.LiveEntity);
        return this;
    }

    public double getRange(@Nonnull BuilderSupport support) {
        return this.range.get(support.getExecutionContext());
    }

    public SensorEvent.EventSearchType getEventSearchType(@Nonnull BuilderSupport support) {
        return this.searchType.get(support.getExecutionContext());
    }

    public int getLockOnTargetSlot(@Nonnull BuilderSupport support) {
        String slot = this.lockOnTargetSlot.get(support.getExecutionContext());
        if (slot == null) {
            return Integer.MIN_VALUE;
        }
        return support.getTargetSlot(slot);
    }
}

