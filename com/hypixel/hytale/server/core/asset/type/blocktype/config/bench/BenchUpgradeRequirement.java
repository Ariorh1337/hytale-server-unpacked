/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.blocktype.config.bench;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.inventory.MaterialQuantity;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import java.util.Arrays;

public class BenchUpgradeRequirement
implements NetworkSerializable<com.hypixel.hytale.protocol.BenchUpgradeRequirement> {
    public static final BuilderCodec<BenchUpgradeRequirement> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BenchUpgradeRequirement.class, BenchUpgradeRequirement::new).append(new KeyedCodec<T[]>("Material", new ArrayCodec<MaterialQuantity>(MaterialQuantity.CODEC, MaterialQuantity[]::new)), (benchUpgradeRequirement, objects) -> {
        benchUpgradeRequirement.input = objects;
    }, benchUpgradeRequirement -> benchUpgradeRequirement.input).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<Double>("TimeSeconds", Codec.DOUBLE), (benchUpgradeRequirement, d) -> {
        benchUpgradeRequirement.timeSeconds = d.floatValue();
    }, benchUpgradeRequirement -> benchUpgradeRequirement.timeSeconds).addValidator(Validators.min(0.0)).add()).build();
    protected MaterialQuantity[] input;
    protected float timeSeconds;

    public BenchUpgradeRequirement(MaterialQuantity[] input, float timeSeconds) {
        this.input = input;
        this.timeSeconds = timeSeconds;
    }

    protected BenchUpgradeRequirement() {
    }

    public MaterialQuantity[] getInput() {
        return this.input;
    }

    public float getTimeSeconds() {
        return this.timeSeconds;
    }

    public String toString() {
        return "BenchUpgradeRequirement{input=" + Arrays.toString(this.input) + ", timeSeconds=" + this.timeSeconds + "}";
    }

    @Override
    public com.hypixel.hytale.protocol.BenchUpgradeRequirement toPacket() {
        com.hypixel.hytale.protocol.BenchUpgradeRequirement packet = new com.hypixel.hytale.protocol.BenchUpgradeRequirement();
        if (this.input != null && this.input.length > 0) {
            packet.material = new com.hypixel.hytale.protocol.MaterialQuantity[this.input.length];
            for (int i = 0; i < this.input.length; ++i) {
                packet.material[i] = this.input[i].toPacket();
            }
            packet.timeSeconds = this.timeSeconds;
        }
        return packet;
    }
}

