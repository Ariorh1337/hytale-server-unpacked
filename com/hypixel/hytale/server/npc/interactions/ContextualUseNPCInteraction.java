/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.interactions;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class ContextualUseNPCInteraction
extends SimpleInstantInteraction {
    @Nonnull
    public static final BuilderCodec<ContextualUseNPCInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ContextualUseNPCInteraction.class, ContextualUseNPCInteraction::new, SimpleInstantInteraction.CODEC).documentation("Interacts with the target NPC passing in context for it to use.")).appendInherited(new KeyedCodec<String>("Context", Codec.STRING), (interaction, s) -> {
        interaction.context = s;
    }, interaction -> interaction.context, (interaction, parent) -> {
        interaction.context = parent.context;
    }).documentation("The provided context for the use action.").addValidator(Validators.nonNull()).add()).build();
    protected String context;

    public ContextualUseNPCInteraction(String id) {
        super(id);
    }

    protected ContextualUseNPCInteraction() {
    }

    @Override
    protected final void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref;
        Ref<EntityStore> targetRef = context.getTargetEntity();
        if (targetRef == null) {
            context.getState().state = InteractionState.Failed;
            return;
        }
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        Player playerComponent = commandBuffer.getComponent(ref = context.getEntity(), Player.getComponentType());
        if (playerComponent == null) {
            HytaleLogger.getLogger().at(Level.INFO).log("UseNPCInteraction requires a Player but was used for: %s", ref);
            context.getState().state = InteractionState.Failed;
            return;
        }
        NPCEntity npcComponent = commandBuffer.getComponent(targetRef, NPCEntity.getComponentType());
        if (npcComponent == null) {
            HytaleLogger.getLogger().at(Level.INFO).log("UseNPCInteraction requires a target NPC");
            context.getState().state = InteractionState.Failed;
            return;
        }
        if (!npcComponent.getRole().getStateSupport().willInteractWith(ref)) {
            context.getState().state = InteractionState.Failed;
            return;
        }
        npcComponent.getRole().getStateSupport().addContextualInteraction(ref, this.context);
    }

    @Override
    @Nonnull
    public String toString() {
        return "ContextualUseNPCInteraction{} " + super.toString();
    }
}

