/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.buildertool.config.args;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolArg;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolArgType;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolFloatArg;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.args.ToolArg;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.args.ToolArgException;
import javax.annotation.Nonnull;

public class FloatArg
extends ToolArg<Float> {
    public static final BuilderCodec<FloatArg> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(FloatArg.class, FloatArg::new, ToolArg.DEFAULT_CODEC).addField(new KeyedCodec<Double>("Default", Codec.DOUBLE), (floatArg, o) -> {
        floatArg.value = Float.valueOf(o.floatValue());
    }, floatArg -> ((Float)floatArg.value).floatValue())).addField(new KeyedCodec<Double>("Min", Codec.DOUBLE), (floatArg, o) -> {
        floatArg.min = o.floatValue();
    }, floatArg -> floatArg.min)).addField(new KeyedCodec<Double>("Max", Codec.DOUBLE), (floatArg, o) -> {
        floatArg.max = o.floatValue();
    }, floatArg -> floatArg.max)).build();
    protected float min;
    protected float max;

    public FloatArg() {
        this.value = Float.valueOf(0.0f);
    }

    public FloatArg(float value, float min, float max) {
        this.value = Float.valueOf(value);
        this.min = min;
        this.max = max;
    }

    public float getMin() {
        return this.min;
    }

    public float getMax() {
        return this.max;
    }

    @Override
    @Nonnull
    public Codec<Float> getCodec() {
        return Codec.FLOAT;
    }

    @Override
    @Nonnull
    public Float fromString(@Nonnull String str) throws ToolArgException {
        float value = Float.parseFloat(str);
        if (value < this.min || value > this.max) {
            throw new ToolArgException(Message.translation("server.builderTools.toolArgRangeError").param("value", value).param("min", this.min).param("max", this.max));
        }
        return Float.valueOf(value);
    }

    @Nonnull
    public BuilderToolFloatArg toFloatArgPacket() {
        return new BuilderToolFloatArg(((Float)this.value).floatValue(), this.min, this.max);
    }

    @Override
    protected void setupPacket(@Nonnull BuilderToolArg packet) {
        packet.argType = BuilderToolArgType.Float;
        packet.floatArg = this.toFloatArgPacket();
    }

    @Override
    @Nonnull
    public String toString() {
        return "FloatArg{min=" + this.min + ", max=" + this.max + "} " + super.toString();
    }
}

