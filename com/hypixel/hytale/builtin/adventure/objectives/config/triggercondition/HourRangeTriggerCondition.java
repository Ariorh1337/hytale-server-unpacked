/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.objectives.config.triggercondition;

import com.hypixel.hytale.builtin.adventure.objectives.config.triggercondition.ObjectiveLocationTriggerCondition;
import com.hypixel.hytale.builtin.adventure.objectives.markers.objectivelocation.ObjectiveLocationMarker;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class HourRangeTriggerCondition
extends ObjectiveLocationTriggerCondition {
    public static final BuilderCodec<HourRangeTriggerCondition> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(HourRangeTriggerCondition.class, HourRangeTriggerCondition::new).append(new KeyedCodec<Integer>("MinHour", Codec.INTEGER), (hourRangeTriggerCondition, integer) -> {
        hourRangeTriggerCondition.minHour = integer;
    }, hourRangeTriggerCondition -> hourRangeTriggerCondition.minHour).add()).append(new KeyedCodec<Integer>("MaxHour", Codec.INTEGER), (hourRangeTriggerCondition, integer) -> {
        hourRangeTriggerCondition.maxHour = integer;
    }, hourRangeTriggerCondition -> hourRangeTriggerCondition.maxHour).add()).build();
    protected static final ResourceType<EntityStore, WorldTimeResource> WORLD_TIME_RESOURCE_RESOURCE_TYPE = WorldTimeResource.getResourceType();
    protected int minHour;
    protected int maxHour;

    @Override
    public boolean isConditionMet(@Nonnull ComponentAccessor<EntityStore> componentAccessor, Ref<EntityStore> ref, ObjectiveLocationMarker objectiveLocationMarker) {
        int currentHour = componentAccessor.getResource(WORLD_TIME_RESOURCE_RESOURCE_TYPE).getCurrentHour();
        if (this.minHour > this.maxHour) {
            return currentHour >= this.minHour || currentHour < this.maxHour;
        }
        return currentHour >= this.minHour && currentHour < this.maxHour;
    }

    @Override
    @Nonnull
    public String toString() {
        return "HourRangeTriggerCondition{minHour=" + this.minHour + ", maxHour=" + this.maxHour + "} " + super.toString();
    }
}

