/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.weather.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import javax.annotation.Nonnull;

public class TimeColor {
    public static final BuilderCodec<TimeColor> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(TimeColor.class, TimeColor::new).append(new KeyedCodec<Double>("Hour", Codec.DOUBLE), (timeColor, i) -> {
        timeColor.hour = i.floatValue();
    }, timeColor -> timeColor.getHour()).addValidator(Validators.range(0.0, 24.0)).add()).addField(new KeyedCodec<Color>("Color", ProtocolCodecs.COLOR), (timeColor, o) -> {
        timeColor.color = o;
    }, TimeColor::getColor)).build();
    public static final ArrayCodec<TimeColor> ARRAY_CODEC = new ArrayCodec<TimeColor>(CODEC, TimeColor[]::new);
    protected float hour;
    protected Color color;

    public TimeColor(float hour, Color color) {
        this.hour = hour;
        this.color = color;
    }

    protected TimeColor() {
    }

    public float getHour() {
        return this.hour;
    }

    public Color getColor() {
        return this.color;
    }

    @Nonnull
    public String toString() {
        return "TimeColor{hour=" + this.hour + ", color='" + String.valueOf(this.color) + "'}";
    }
}

