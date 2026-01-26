/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.world.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.builtin.tagset.config.NPCGroup;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.AssetHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.BooleanHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.EnumHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.AssetValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.TagSetExistsValidator;
import com.hypixel.hytale.server.npc.blackboard.view.event.entity.EntityEventType;
import com.hypixel.hytale.server.npc.corecomponents.world.SensorEntityEvent;
import com.hypixel.hytale.server.npc.corecomponents.world.builders.BuilderSensorEvent;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import javax.annotation.Nonnull;

public class BuilderSensorEntityEvent
extends BuilderSensorEvent {
    protected final AssetHolder npcGroup = new AssetHolder();
    protected final EnumHolder<EntityEventType> entityEventType = new EnumHolder();
    protected final BooleanHolder flockOnly = new BooleanHolder();

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Matches when an entity from a specific NPC group within a certain range is damaged, killed, or interacted with";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    @Nonnull
    public Sensor build(@Nonnull BuilderSupport builderSupport) {
        return new SensorEntityEvent(this, builderSupport);
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Override
    @Nonnull
    public Builder<Sensor> readConfig(@Nonnull JsonElement data) {
        super.readConfig(data);
        this.requireAsset(data, "NPCGroup", this.npcGroup, (AssetValidator)TagSetExistsValidator.required(), BuilderDescriptorState.Stable, "NPC group to listen for", null);
        this.getEnum(data, "EventType", this.entityEventType, EntityEventType.class, EntityEventType.DAMAGE, BuilderDescriptorState.Stable, "The event type to listen for", null);
        this.getBoolean(data, "FlockOnly", this.flockOnly, false, BuilderDescriptorState.Stable, "Whether to only listen for flock events", null);
        return this;
    }

    public int getNPCGroup(@Nonnull BuilderSupport support) {
        String key = this.npcGroup.get(support.getExecutionContext());
        int index = NPCGroup.getAssetMap().getIndex(key);
        if (index == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Unknown key! " + key);
        }
        return index;
    }

    public EntityEventType getEventType(@Nonnull BuilderSupport support) {
        return this.entityEventType.get(support.getExecutionContext());
    }

    public boolean isFlockOnly(@Nonnull BuilderSupport support) {
        return this.flockOnly.get(support.getExecutionContext());
    }
}

