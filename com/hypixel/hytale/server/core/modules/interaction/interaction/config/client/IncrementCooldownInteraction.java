/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.client;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.Interaction;
import com.hypixel.hytale.protocol.InteractionCooldown;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import io.netty.util.internal.StringUtil;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class IncrementCooldownInteraction
extends SimpleInstantInteraction {
    @Nonnull
    public static final BuilderCodec<IncrementCooldownInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(IncrementCooldownInteraction.class, IncrementCooldownInteraction::new, SimpleInstantInteraction.CODEC).documentation("Increase the given cooldown.")).appendInherited(new KeyedCodec<String>("Id", Codec.STRING), (o, i) -> {
        o.cooldown = i;
    }, o -> o.cooldown, (o, p) -> {
        o.cooldown = p.cooldown;
    }).documentation("The ID of the cooldown to increment").add()).appendInherited(new KeyedCodec<Float>("Time", Codec.FLOAT), (o, i) -> {
        o.cooldownTime = i.floatValue();
    }, o -> Float.valueOf(o.cooldownTime), (o, p) -> {
        o.cooldownTime = p.cooldownTime;
    }).documentation("The amount of time to increase the current cooldown time by").add()).appendInherited(new KeyedCodec<Float>("ChargeTime", Codec.FLOAT), (o, i) -> {
        o.chargeTime = i.floatValue();
    }, o -> Float.valueOf(o.chargeTime), (o, p) -> {
        o.chargeTime = p.chargeTime;
    }).documentation("The amount of time to increase the current charge time by").add()).appendInherited(new KeyedCodec<Integer>("Charge", Codec.INTEGER), (o, i) -> {
        o.charge = i;
    }, o -> o.charge, (o, p) -> {
        o.charge = p.charge;
    }).documentation("The amount of empty charges to recharge").add()).appendInherited(new KeyedCodec<Boolean>("InterruptRecharge", Codec.BOOLEAN), (o, i) -> {
        o.interruptRecharge = i;
    }, o -> o.interruptRecharge, (o, p) -> {
        o.interruptRecharge = p.interruptRecharge;
    }).documentation("Determines whether the recharge of this cooldown should be interrupted").add()).afterDecode(interaction -> {
        interaction.chargeTime = -interaction.chargeTime;
    })).build();
    @Nullable
    private String cooldown;
    private float cooldownTime;
    private float chargeTime;
    private int charge;
    private boolean interruptRecharge;

    @Override
    protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        InteractionCooldown rootCooldown;
        String cooldownId = this.cooldown;
        if (StringUtil.isNullOrEmpty(cooldownId) && (rootCooldown = context.getChain().getRootInteraction().getCooldown()) != null) {
            cooldownId = rootCooldown.cooldownId;
        }
        this.processCooldown(cooldownHandler, cooldownId);
        context.getState().state = InteractionState.Finished;
    }

    protected void processCooldown(@Nonnull CooldownHandler cooldownHandler, @Nonnull String cooldownId) {
        CooldownHandler.Cooldown cooldown = cooldownHandler.getCooldown(cooldownId);
        if (cooldown == null) {
            return;
        }
        if (this.cooldownTime != 0.0f) {
            cooldown.increaseTime(this.cooldownTime);
        }
        if (this.charge != 0) {
            cooldown.replenishCharge(this.charge, this.interruptRecharge);
        }
        if (this.chargeTime != 0.0f) {
            cooldown.increaseChargeTime(this.chargeTime);
        }
    }

    @Override
    @Nonnull
    protected Interaction generatePacket() {
        return new com.hypixel.hytale.protocol.IncrementCooldownInteraction();
    }

    @Override
    protected void configurePacket(Interaction packet) {
        super.configurePacket(packet);
        com.hypixel.hytale.protocol.IncrementCooldownInteraction p = (com.hypixel.hytale.protocol.IncrementCooldownInteraction)packet;
        p.cooldownId = this.cooldown;
        p.cooldownIncrementTime = this.cooldownTime;
        p.cooldownIncrementCharge = this.charge;
        p.cooldownIncrementChargeTime = this.chargeTime;
        p.cooldownIncrementInterrupt = this.interruptRecharge;
    }

    @Override
    @Nonnull
    public String toString() {
        return "IncrementCooldownInteraction{} " + super.toString();
    }
}

