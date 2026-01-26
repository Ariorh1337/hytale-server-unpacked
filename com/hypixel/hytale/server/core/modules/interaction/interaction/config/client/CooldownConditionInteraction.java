/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.client;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.Interaction;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionSyncData;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class CooldownConditionInteraction
extends SimpleInstantInteraction {
    @Nonnull
    public static final BuilderCodec<CooldownConditionInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(CooldownConditionInteraction.class, CooldownConditionInteraction::new, SimpleInstantInteraction.CODEC).documentation("Checks if a given cooldown is complete.")).appendInherited(new KeyedCodec<String>("Id", Codec.STRING), (o, i) -> {
        o.cooldown = i;
    }, o -> o.cooldown, (o, p) -> {
        o.cooldown = p.cooldown;
    }).documentation("The ID of the cooldown to check for in this condition.").addValidator(Validators.nonNull()).add()).build();
    private String cooldown;

    @Override
    protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        assert (this.cooldown != null);
        InteractionSyncData state = context.getState();
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        assert (commandBuffer != null);
        Ref<EntityStore> ref = context.getEntity();
        Player playerComponent = commandBuffer.getComponent(ref, Player.getComponentType());
        if (playerComponent == null && context.getClientState() != null) {
            state.state = context.getClientState().state;
            return;
        }
        state.state = this.checkCooldown(cooldownHandler, this.cooldown) ? InteractionState.Failed : InteractionState.Finished;
    }

    protected boolean checkCooldown(@Nonnull CooldownHandler cooldownHandler, @Nonnull String cooldownId) {
        CooldownHandler.Cooldown cooldown = cooldownHandler.getCooldown(cooldownId);
        return cooldown != null && cooldown.hasCooldown(false);
    }

    @Override
    @Nonnull
    protected Interaction generatePacket() {
        return new com.hypixel.hytale.protocol.CooldownConditionInteraction();
    }

    @Override
    protected void configurePacket(Interaction packet) {
        super.configurePacket(packet);
        com.hypixel.hytale.protocol.CooldownConditionInteraction p = (com.hypixel.hytale.protocol.CooldownConditionInteraction)packet;
        p.cooldownId = this.cooldown;
    }

    @Override
    @Nonnull
    public String toString() {
        return "CooldownConditionInteraction{} " + super.toString();
    }
}

