/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.none;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.Interaction;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.ValueType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.none.StatsConditionBaseInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import javax.annotation.Nonnull;

public class StatsConditionInteraction
extends StatsConditionBaseInteraction {
    @Nonnull
    public static final BuilderCodec<StatsConditionInteraction> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(StatsConditionInteraction.class, StatsConditionInteraction::new, StatsConditionBaseInteraction.CODEC).documentation("Interaction that is successful if the given stat conditions match.")).build();

    @Override
    protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        assert (commandBuffer != null);
        Ref<EntityStore> ref = context.getEntity();
        if (!this.canAfford(ref, commandBuffer)) {
            context.getState().state = InteractionState.Failed;
        }
    }

    @Override
    protected boolean canAfford(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        EntityStatMap entityStatMapComponent = componentAccessor.getComponent(ref, EntityStatMap.getComponentType());
        if (entityStatMapComponent == null) {
            return false;
        }
        if (this.costs == null) {
            return false;
        }
        for (Int2FloatMap.Entry cost : this.costs.int2FloatEntrySet()) {
            float statValue;
            EntityStatValue stat = entityStatMapComponent.get(cost.getIntKey());
            if (stat == null) {
                return false;
            }
            float f = statValue = this.valueType == ValueType.Absolute ? stat.get() : stat.asPercentage() * 100.0f;
            if (!(this.lessThan ? statValue >= cost.getFloatValue() : statValue < cost.getFloatValue() && !this.canOverdraw(statValue, stat.getMin()))) continue;
            return false;
        }
        return true;
    }

    @Override
    @Nonnull
    protected Interaction generatePacket() {
        return new com.hypixel.hytale.protocol.StatsConditionInteraction();
    }

    @Override
    protected void configurePacket(Interaction packet) {
        super.configurePacket(packet);
        com.hypixel.hytale.protocol.StatsConditionInteraction p = (com.hypixel.hytale.protocol.StatsConditionInteraction)packet;
        p.costs = this.costs;
        p.lessThan = this.lessThan;
        p.lenient = this.lenient;
        p.valueType = this.valueType;
    }

    @Override
    @Nonnull
    public String toString() {
        return "StatsConditionInteraction{}" + super.toString();
    }
}

