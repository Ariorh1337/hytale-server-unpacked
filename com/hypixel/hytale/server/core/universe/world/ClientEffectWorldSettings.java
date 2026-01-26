/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.packets.world.UpdatePostFxSettings;
import com.hypixel.hytale.protocol.packets.world.UpdateSunSettings;

public class ClientEffectWorldSettings {
    public static BuilderCodec<ClientEffectWorldSettings> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ClientEffectWorldSettings.class, ClientEffectWorldSettings::new).append(new KeyedCodec<Float>("SunHeightPercent", Codec.FLOAT), (settings, o) -> {
        settings.sunHeightPercent = o.floatValue();
    }, settings -> Float.valueOf(settings.sunHeightPercent)).add()).append(new KeyedCodec<Float>("SunAngleDegrees", Codec.FLOAT), (settings, o) -> {
        settings.sunAngleRadians = (float)Math.toRadians(o.floatValue());
    }, settings -> Float.valueOf((float)Math.toDegrees(settings.sunAngleRadians))).add()).append(new KeyedCodec<Float>("BloomIntensity", Codec.FLOAT), (settings, o) -> {
        settings.bloomIntensity = o.floatValue();
    }, settings -> Float.valueOf(settings.bloomIntensity)).add()).append(new KeyedCodec<Float>("BloomPower", Codec.FLOAT), (settings, o) -> {
        settings.bloomPower = o.floatValue();
    }, settings -> Float.valueOf(settings.bloomPower)).add()).append(new KeyedCodec<Float>("SunIntensity", Codec.FLOAT), (settings, o) -> {
        settings.sunIntensity = o.floatValue();
    }, settings -> Float.valueOf(settings.sunIntensity)).add()).append(new KeyedCodec<Float>("SunshaftIntensity", Codec.FLOAT), (settings, o) -> {
        settings.sunshaftIntensity = o.floatValue();
    }, settings -> Float.valueOf(settings.sunshaftIntensity)).add()).append(new KeyedCodec<Float>("SunshaftScaleFactor", Codec.FLOAT), (settings, o) -> {
        settings.sunshaftScaleFactor = o.floatValue();
    }, settings -> Float.valueOf(settings.sunshaftScaleFactor)).add()).build();
    private float sunHeightPercent = 100.0f;
    private float sunAngleRadians = 0.0f;
    private float bloomIntensity = 0.3f;
    private float bloomPower = 8.0f;
    private float sunIntensity = 0.25f;
    private float sunshaftIntensity = 0.3f;
    private float sunshaftScaleFactor = 4.0f;

    public float getSunHeightPercent() {
        return this.sunHeightPercent;
    }

    public void setSunHeightPercent(float sunHeightPercent) {
        this.sunHeightPercent = sunHeightPercent;
    }

    public float getSunAngleRadians() {
        return this.sunAngleRadians;
    }

    public void setSunAngleRadians(float sunAngleRadians) {
        this.sunAngleRadians = sunAngleRadians;
    }

    public float getBloomIntensity() {
        return this.bloomIntensity;
    }

    public void setBloomIntensity(float bloomIntensity) {
        this.bloomIntensity = bloomIntensity;
    }

    public float getBloomPower() {
        return this.bloomPower;
    }

    public void setBloomPower(float bloomPower) {
        this.bloomPower = bloomPower;
    }

    public float getSunIntensity() {
        return this.sunIntensity;
    }

    public void setSunIntensity(float sunIntensity) {
        this.sunIntensity = sunIntensity;
    }

    public float getSunshaftIntensity() {
        return this.sunshaftIntensity;
    }

    public void setSunshaftIntensity(float sunshaftIntensity) {
        this.sunshaftIntensity = sunshaftIntensity;
    }

    public float getSunshaftScaleFactor() {
        return this.sunshaftScaleFactor;
    }

    public void setSunshaftScaleFactor(float sunshaftScaleFactor) {
        this.sunshaftScaleFactor = sunshaftScaleFactor;
    }

    public UpdateSunSettings createSunSettingsPacket() {
        return new UpdateSunSettings(this.sunHeightPercent, this.sunAngleRadians);
    }

    public UpdatePostFxSettings createPostFxSettingsPacket() {
        return new UpdatePostFxSettings(this.bloomIntensity, this.bloomPower, this.sunshaftScaleFactor, this.sunIntensity, this.sunshaftIntensity);
    }
}

