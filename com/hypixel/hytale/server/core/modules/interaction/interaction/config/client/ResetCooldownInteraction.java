/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.client;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.Interaction;
import com.hypixel.hytale.protocol.InteractionCooldown;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionChain;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ResetCooldownInteraction
extends SimpleInstantInteraction {
    @Nonnull
    public static final BuilderCodec<ResetCooldownInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ResetCooldownInteraction.class, ResetCooldownInteraction::new, SimpleInstantInteraction.CODEC).documentation("Resets the cooldown.")).appendInherited(new KeyedCodec<InteractionCooldown>("Cooldown", RootInteraction.COOLDOWN_CODEC), (interaction, s) -> {
        interaction.cooldown = s;
    }, interaction -> interaction.cooldown, (interaction, parent) -> {
        interaction.next = parent.next;
    }).documentation("The cooldown concerning this interaction, defaulting to the root cooldown if none presented").add()).build();
    @Nullable
    private InteractionCooldown cooldown;

    @Override
    protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        String cooldownId = null;
        float cooldownTime = 0.0f;
        float[] charges = null;
        boolean interruptRecharge = false;
        if (this.cooldown != null) {
            cooldownId = this.cooldown.cooldownId;
            cooldownTime = this.cooldown.cooldown;
            charges = this.cooldown.chargeTimes;
            interruptRecharge = this.cooldown.interruptRecharge;
        }
        ResetCooldownInteraction.resetCooldown(context, cooldownHandler, cooldownId, cooldownTime, charges, interruptRecharge);
    }

    protected static void resetCooldown(@Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler, @Nullable String cooldownId, float cooldownTime, @Nullable float[] chargeTimes, boolean interruptRecharge0) {
        CooldownHandler.Cooldown cooldown;
        CooldownHandler.Cooldown possibleCooldown;
        float time = 0.35f;
        float[] charges = InteractionManager.DEFAULT_CHARGE_TIMES;
        boolean interruptRecharge = false;
        if (cooldownId == null) {
            InteractionChain chain = context.getChain();
            assert (chain != null);
            RootInteraction rootInteraction = context.getChain().getInitialRootInteraction();
            InteractionCooldown rootCooldown = rootInteraction.getCooldown();
            if (rootCooldown != null) {
                cooldownId = rootCooldown.cooldownId;
                if (rootCooldown.cooldown > 0.0f) {
                    time = rootCooldown.cooldown;
                }
                if (rootCooldown.interruptRecharge) {
                    interruptRecharge = true;
                }
                if (rootCooldown.chargeTimes != null && rootCooldown.chargeTimes.length > 0) {
                    charges = rootCooldown.chargeTimes;
                }
            }
            if (cooldownId == null) {
                cooldownId = rootInteraction.getId();
            }
        }
        if ((possibleCooldown = cooldownHandler.getCooldown(cooldownId)) != null) {
            time = possibleCooldown.getCooldown();
            charges = possibleCooldown.getCharges();
            interruptRecharge = possibleCooldown.interruptRecharge();
        }
        if (cooldownTime > 0.0f) {
            time = cooldownTime;
        }
        if (chargeTimes != null && chargeTimes.length > 0) {
            charges = chargeTimes;
        }
        if (interruptRecharge0) {
            interruptRecharge = true;
        }
        if ((cooldown = cooldownHandler.getCooldown(cooldownId, time, charges, true, interruptRecharge)) != null) {
            cooldown.setCooldownMax(time);
            cooldown.setCharges(charges);
            cooldown.resetCooldown();
            cooldown.resetCharges();
        }
    }

    @Override
    @Nonnull
    protected Interaction generatePacket() {
        return new com.hypixel.hytale.protocol.ResetCooldownInteraction();
    }

    @Override
    protected void configurePacket(Interaction packet) {
        super.configurePacket(packet);
        com.hypixel.hytale.protocol.ResetCooldownInteraction p = (com.hypixel.hytale.protocol.ResetCooldownInteraction)packet;
        p.cooldown = this.cooldown;
    }

    @Override
    @Nonnull
    public String toString() {
        return "ResetCooldownInteraction{} " + super.toString();
    }
}

