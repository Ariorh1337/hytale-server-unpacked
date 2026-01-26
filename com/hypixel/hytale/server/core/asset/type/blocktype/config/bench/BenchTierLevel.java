/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.blocktype.config.bench;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.BenchUpgradeRequirement;
import com.hypixel.hytale.server.core.io.NetworkSerializable;

public class BenchTierLevel
implements NetworkSerializable<com.hypixel.hytale.protocol.BenchTierLevel> {
    public static final BuilderCodec<BenchTierLevel> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BenchTierLevel.class, BenchTierLevel::new).append(new KeyedCodec<BenchUpgradeRequirement>("UpgradeRequirement", BenchUpgradeRequirement.CODEC), (tier, d) -> {
        tier.upgradeRequirement = d;
    }, tier -> tier.upgradeRequirement).add()).append(new KeyedCodec<Double>("CraftingTimeReductionModifier", Codec.DOUBLE), (tier, d) -> {
        tier.craftingTimeReductionModifier = d.floatValue();
    }, tier -> tier.craftingTimeReductionModifier).addValidator(Validators.range(0.0, 1.0)).add()).append(new KeyedCodec<Integer>("ExtraInputSlot", Codec.INTEGER), (tier, d) -> {
        tier.extraInputSlot = d;
    }, tier -> tier.extraInputSlot).addValidator(Validators.min(0)).add()).append(new KeyedCodec<Integer>("ExtraOutputSlot", Codec.INTEGER), (tier, d) -> {
        tier.extraOutputSlot = d;
    }, tier -> tier.extraOutputSlot).addValidator(Validators.min(0)).add()).build();
    protected BenchUpgradeRequirement upgradeRequirement;
    protected float craftingTimeReductionModifier;
    protected int extraInputSlot;
    protected int extraOutputSlot;

    public BenchTierLevel(BenchUpgradeRequirement upgradeRequirement, float craftingTimeReductionModifier, int extraInputSlot, int extraOutputSlot) {
        this.upgradeRequirement = upgradeRequirement;
        this.craftingTimeReductionModifier = craftingTimeReductionModifier;
        this.extraInputSlot = extraInputSlot;
        this.extraOutputSlot = extraOutputSlot;
    }

    protected BenchTierLevel() {
    }

    public float getCraftingTimeReductionModifier() {
        return this.craftingTimeReductionModifier;
    }

    public BenchUpgradeRequirement getUpgradeRequirement() {
        return this.upgradeRequirement;
    }

    public int getExtraInputSlot() {
        return this.extraInputSlot;
    }

    public int getExtraOutputSlot() {
        return this.extraOutputSlot;
    }

    @Override
    public com.hypixel.hytale.protocol.BenchTierLevel toPacket() {
        com.hypixel.hytale.protocol.BenchTierLevel packet = new com.hypixel.hytale.protocol.BenchTierLevel();
        if (this.upgradeRequirement != null) {
            packet.benchUpgradeRequirement = this.upgradeRequirement.toPacket();
        }
        packet.craftingTimeReductionModifier = this.craftingTimeReductionModifier;
        packet.extraInputSlot = this.extraInputSlot;
        packet.extraOutputSlot = this.extraOutputSlot;
        return packet;
    }
}

