/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.splitvelocity;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.protocol.VelocityThresholdStyle;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import javax.annotation.Nonnull;

public class VelocityConfig
implements NetworkSerializable<com.hypixel.hytale.protocol.VelocityConfig> {
    @Nonnull
    public static BuilderCodec<VelocityConfig> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(VelocityConfig.class, VelocityConfig::new).appendInherited(new KeyedCodec<Float>("GroundResistance", Codec.FLOAT), (o, i) -> {
        o.groundResistance = i.floatValue();
    }, o -> Float.valueOf(o.groundResistance), (o, p) -> {
        o.groundResistance = p.groundResistance;
    }).addValidator(Validators.range(Float.valueOf(0.0f), Float.valueOf(1.0f))).add()).appendInherited(new KeyedCodec<Float>("GroundResistanceMax", Codec.FLOAT), (o, i) -> {
        o.groundResistanceMax = i.floatValue();
    }, o -> Float.valueOf(o.groundResistanceMax), (o, p) -> {
        o.groundResistanceMax = p.groundResistanceMax;
    }).addValidator(Validators.range(Float.valueOf(0.0f), Float.valueOf(1.0f))).add()).appendInherited(new KeyedCodec<Float>("AirResistance", Codec.FLOAT), (o, i) -> {
        o.airResistance = i.floatValue();
    }, o -> Float.valueOf(o.airResistance), (o, p) -> {
        o.airResistance = p.airResistance;
    }).addValidator(Validators.range(Float.valueOf(0.0f), Float.valueOf(1.0f))).add()).appendInherited(new KeyedCodec<Float>("AirResistanceMax", Codec.FLOAT), (o, i) -> {
        o.airResistanceMax = i.floatValue();
    }, o -> Float.valueOf(o.airResistanceMax), (o, p) -> {
        o.airResistance = p.airResistanceMax;
    }).addValidator(Validators.range(Float.valueOf(0.0f), Float.valueOf(1.0f))).add()).appendInherited(new KeyedCodec<Float>("Threshold", Codec.FLOAT), (o, i) -> {
        o.threshold = i.floatValue();
    }, o -> Float.valueOf(o.threshold), (o, p) -> {
        o.threshold = p.threshold;
    }).documentation("The threshold of the velocity's length before resistance starts to transition to the Max values (if set)").add()).appendInherited(new KeyedCodec<VelocityThresholdStyle>("Style", new EnumCodec<VelocityThresholdStyle>(VelocityThresholdStyle.class)), (o, i) -> {
        o.style = i;
    }, o -> o.style, (o, p) -> {
        o.style = p.style;
    }).documentation("Whether the transition from min to max resistance values should be linear or not").add()).build();
    private float groundResistance = 0.82f;
    private float groundResistanceMax = 0.0f;
    private float airResistance = 0.96f;
    private float airResistanceMax = 0.0f;
    private float threshold = 1.0f;
    private VelocityThresholdStyle style = VelocityThresholdStyle.Linear;

    public float getGroundResistance() {
        return this.groundResistance;
    }

    public float getAirResistance() {
        return this.airResistance;
    }

    public float getGroundResistanceMax() {
        return this.groundResistanceMax;
    }

    public float getAirResistanceMax() {
        return this.airResistanceMax;
    }

    public float getThreshold() {
        return this.threshold;
    }

    public VelocityThresholdStyle getStyle() {
        return this.style;
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.VelocityConfig toPacket() {
        return new com.hypixel.hytale.protocol.VelocityConfig(this.groundResistance, this.groundResistanceMax, this.airResistance, this.airResistanceMax, this.threshold, this.style);
    }
}

