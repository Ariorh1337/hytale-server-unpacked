/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.weather.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.protocol.ColorAlpha;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import javax.annotation.Nonnull;

public class TimeColorAlpha {
    public static final BuilderCodec<TimeColorAlpha> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(TimeColorAlpha.class, TimeColorAlpha::new).append(new KeyedCodec<Double>("Hour", Codec.DOUBLE), (timeColorAlpha, i) -> {
        timeColorAlpha.hour = i.floatValue();
    }, timeColorAlpha -> timeColorAlpha.getHour()).addValidator(Validators.range(0.0, 24.0)).add()).addField(new KeyedCodec<ColorAlpha>("Color", ProtocolCodecs.COLOR_AlPHA), (timeColorAlpha, o) -> {
        timeColorAlpha.color = o;
    }, TimeColorAlpha::getColor)).build();
    public static final ArrayCodec<TimeColorAlpha> ARRAY_CODEC = new ArrayCodec<TimeColorAlpha>(CODEC, TimeColorAlpha[]::new);
    protected float hour;
    protected ColorAlpha color;

    public TimeColorAlpha(float hour, ColorAlpha color) {
        this.hour = hour;
        this.color = color;
    }

    protected TimeColorAlpha() {
    }

    public float getHour() {
        return this.hour;
    }

    public ColorAlpha getColor() {
        return this.color;
    }

    @Nonnull
    public String toString() {
        return "TimeColorAlpha{hour=" + this.hour + ", color='" + String.valueOf(this.color) + "'}";
    }
}

