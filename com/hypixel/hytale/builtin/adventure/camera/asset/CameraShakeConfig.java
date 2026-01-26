/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.camera.asset;

import com.hypixel.hytale.builtin.adventure.camera.asset.EasingConfig;
import com.hypixel.hytale.builtin.adventure.camera.asset.NoiseConfig;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class CameraShakeConfig
implements NetworkSerializable<com.hypixel.hytale.protocol.CameraShakeConfig> {
    public static final BuilderCodec<CameraShakeConfig> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(CameraShakeConfig.class, CameraShakeConfig::new).appendInherited(new KeyedCodec<Float>("Duration", Codec.FLOAT), (o, v) -> {
        o.duration = v.floatValue();
    }, o -> Float.valueOf(o.duration), (o, p) -> {
        o.duration = p.duration;
    }).documentation("The time period that the camera will shake at full intensity for").addValidator(Validators.min(Float.valueOf(0.0f))).add()).appendInherited(new KeyedCodec<Float>("StartTime", Codec.FLOAT), (o, v) -> {
        o.startTime = v;
    }, o -> o.startTime, (o, p) -> {
        o.startTime = p.startTime;
    }).documentation("The initial time value that the Offset and Rotation noises are sampled from when the camera-shake starts. If absent, the camera-shake uses a continuously incremented time value.").add()).appendInherited(new KeyedCodec<EasingConfig>("EaseIn", EasingConfig.CODEC), (o, v) -> {
        o.easeIn = v;
    }, o -> o.easeIn, (o, p) -> {
        o.easeIn = p.easeIn;
    }).documentation("The fade-in time and intensity curve for the camera shake").addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<EasingConfig>("EaseOut", EasingConfig.CODEC), (o, v) -> {
        o.easeOut = v;
    }, o -> o.easeOut, (o, p) -> {
        o.easeOut = p.easeOut;
    }).documentation("The fade-out time and intensity curve for the camera shake").addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<OffsetNoise>("Offset", OffsetNoise.CODEC), (o, v) -> {
        o.offset = v;
    }, o -> o.offset, (o, p) -> {
        o.offset = p.offset;
    }).documentation("The translational offset motion").add()).appendInherited(new KeyedCodec<RotationNoise>("Rotation", RotationNoise.CODEC), (o, v) -> {
        o.rotation = v;
    }, o -> o.rotation, (o, p) -> {
        o.rotation = p.rotation;
    }).documentation("The rotational motion").add()).build();
    protected float duration;
    protected Float startTime;
    protected EasingConfig easeIn = EasingConfig.NONE;
    protected EasingConfig easeOut = EasingConfig.NONE;
    protected OffsetNoise offset = OffsetNoise.NONE;
    protected RotationNoise rotation = RotationNoise.NONE;

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.CameraShakeConfig toPacket() {
        boolean continuous = this.startTime == null;
        float startTime = continuous ? 0.0f : this.startTime.floatValue();
        return new com.hypixel.hytale.protocol.CameraShakeConfig(this.duration, startTime, continuous, this.easeIn.toPacket(), this.easeOut.toPacket(), this.offset.toPacket(), this.rotation.toPacket());
    }

    @Nonnull
    public String toString() {
        return "CameraShakeConfig{duration=" + this.duration + ", startTime=" + this.startTime + ", easeIn=" + String.valueOf(this.easeIn) + ", easeOut=" + String.valueOf(this.easeOut) + ", offset=" + String.valueOf(this.offset) + ", rotation=" + String.valueOf(this.rotation) + "}";
    }

    public static class OffsetNoise
    implements NetworkSerializable<com.hypixel.hytale.protocol.OffsetNoise> {
        public static final BuilderCodec<OffsetNoise> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(OffsetNoise.class, OffsetNoise::new).documentation("The translational offset noise sources. Each component's list of noise configurations are summed together to calculate the output value for that component")).appendInherited(new KeyedCodec<T[]>("X", NoiseConfig.ARRAY_CODEC), (o, v) -> {
            o.x = v;
        }, o -> o.x, (o, p) -> {
            o.x = p.x;
        }).documentation("The noise used to vary the camera x-offset").add()).appendInherited(new KeyedCodec<T[]>("Y", NoiseConfig.ARRAY_CODEC), (o, v) -> {
            o.y = v;
        }, o -> o.y, (o, p) -> {
            o.y = p.y;
        }).documentation("The noise used to vary the camera y-offset").add()).appendInherited(new KeyedCodec<T[]>("Z", NoiseConfig.ARRAY_CODEC), (o, v) -> {
            o.z = v;
        }, o -> o.z, (o, p) -> {
            o.z = p.z;
        }).documentation("The noise used to vary the camera z-offset").add()).build();
        public static final OffsetNoise NONE = new OffsetNoise();
        protected NoiseConfig[] x;
        protected NoiseConfig[] y;
        protected NoiseConfig[] z;

        @Override
        @Nonnull
        public com.hypixel.hytale.protocol.OffsetNoise toPacket() {
            return new com.hypixel.hytale.protocol.OffsetNoise(NoiseConfig.toPacket(this.x), NoiseConfig.toPacket(this.y), NoiseConfig.toPacket(this.z));
        }

        @Nonnull
        public String toString() {
            return "OffsetNoise{x=" + Arrays.toString(this.x) + ", y=" + Arrays.toString(this.y) + ", z=" + Arrays.toString(this.z) + "}";
        }
    }

    public static class RotationNoise
    implements NetworkSerializable<com.hypixel.hytale.protocol.RotationNoise> {
        public static final BuilderCodec<RotationNoise> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(RotationNoise.class, RotationNoise::new).documentation("The rotational noise sources. Each component's list of noise configurations are summed together to calculate the output value for that component")).appendInherited(new KeyedCodec<T[]>("Pitch", NoiseConfig.ARRAY_CODEC), (o, v) -> {
            o.pitch = v;
        }, o -> o.pitch, (o, p) -> {
            o.pitch = p.pitch;
        }).documentation("The noise used to vary the camera pitch").add()).appendInherited(new KeyedCodec<T[]>("Yaw", NoiseConfig.ARRAY_CODEC), (o, v) -> {
            o.yaw = v;
        }, o -> o.yaw, (o, p) -> {
            o.yaw = p.yaw;
        }).documentation("The noise used to vary the camera yaw").add()).appendInherited(new KeyedCodec<T[]>("Roll", NoiseConfig.ARRAY_CODEC), (o, v) -> {
            o.roll = v;
        }, o -> o.roll, (o, p) -> {
            o.roll = p.roll;
        }).documentation("The noise used to vary the camera roll").add()).build();
        public static final RotationNoise NONE = new RotationNoise();
        protected NoiseConfig[] pitch;
        protected NoiseConfig[] yaw;
        protected NoiseConfig[] roll;

        @Override
        @Nonnull
        public com.hypixel.hytale.protocol.RotationNoise toPacket() {
            return new com.hypixel.hytale.protocol.RotationNoise(NoiseConfig.toPacket(this.pitch), NoiseConfig.toPacket(this.yaw), NoiseConfig.toPacket(this.roll));
        }

        @Nonnull
        public String toString() {
            return "RotationNoise{pitch=" + Arrays.toString(this.pitch) + ", yaw=" + Arrays.toString(this.yaw) + ", roll=" + Arrays.toString(this.roll) + "}";
        }
    }
}

