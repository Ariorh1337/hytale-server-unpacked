/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.camera.asset;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.schema.metadata.ui.UIDefaultCollapsedState;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.protocol.NoiseType;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NoiseConfig
implements NetworkSerializable<com.hypixel.hytale.protocol.NoiseConfig> {
    @Nonnull
    public static final Codec<NoiseType> NOISE_TYPE_CODEC = new EnumCodec<NoiseType>(NoiseType.class);
    @Nonnull
    public static final BuilderCodec<NoiseConfig> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(NoiseConfig.class, NoiseConfig::new).appendInherited(new KeyedCodec<Integer>("Seed", Codec.INTEGER), (o, v) -> {
        o.seed = v;
    }, o -> o.seed, (o, p) -> {
        o.seed = p.seed;
    }).documentation("The value used to seed the noise source").add()).appendInherited(new KeyedCodec<NoiseType>("Type", NOISE_TYPE_CODEC), (o, v) -> {
        o.type = v;
    }, o -> o.type, (o, p) -> {
        o.type = p.type;
    }).documentation("The type of noise used to move the camera").addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<Float>("Frequency", Codec.FLOAT), (o, v) -> {
        o.frequency = v.floatValue();
    }, o -> Float.valueOf(o.frequency), (o, p) -> {
        o.frequency = p.frequency;
    }).documentation("The frequency at which the noise source is sampled").add()).appendInherited(new KeyedCodec<Float>("Amplitude", Codec.FLOAT), (o, v) -> {
        o.amplitude = v.floatValue();
    }, o -> Float.valueOf(o.amplitude), (o, p) -> {
        o.amplitude = p.amplitude;
    }).documentation("The maximum extent of the noise source output").add()).appendInherited(new KeyedCodec<ClampConfig>("Clamp", ClampConfig.CODEC), (o, v) -> {
        o.clamp = v;
    }, o -> o.clamp, (o, p) -> {
        o.clamp = p.clamp;
    }).documentation("Restricts the range of values that the noise source can output").metadata(UIDefaultCollapsedState.UNCOLLAPSED).add()).build();
    @Nonnull
    public static final ArrayCodec<NoiseConfig> ARRAY_CODEC = ArrayCodec.ofBuilderCodec(CODEC, NoiseConfig[]::new);
    @Nonnull
    public static final com.hypixel.hytale.protocol.NoiseConfig[] NOISE_CONFIGS = new com.hypixel.hytale.protocol.NoiseConfig[0];
    protected int seed;
    @Nonnull
    protected NoiseType type = NoiseType.Sin;
    @Nonnull
    protected ClampConfig clamp = ClampConfig.NONE;
    protected float frequency;
    protected float amplitude;

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.NoiseConfig toPacket() {
        return new com.hypixel.hytale.protocol.NoiseConfig(this.seed, this.type, this.frequency, this.amplitude, this.clamp.toPacket());
    }

    @Nonnull
    public String toString() {
        return "NoiseConfig{seed=" + this.seed + ", type=" + String.valueOf((Object)this.type) + ", clamp=" + String.valueOf(this.clamp) + ", frequency=" + this.frequency + ", amplitude=" + this.amplitude + "}";
    }

    @Nonnull
    public static com.hypixel.hytale.protocol.NoiseConfig[] toPacket(@Nullable NoiseConfig[] configs) {
        if (configs == null || configs.length == 0) {
            return NOISE_CONFIGS;
        }
        com.hypixel.hytale.protocol.NoiseConfig[] result = new com.hypixel.hytale.protocol.NoiseConfig[configs.length];
        for (int i = 0; i < configs.length; ++i) {
            NoiseConfig config = configs[i];
            if (config == null) continue;
            result[i] = config.toPacket();
        }
        return result;
    }

    public static class ClampConfig
    implements NetworkSerializable<com.hypixel.hytale.protocol.ClampConfig> {
        @Nonnull
        public static final BuilderCodec<ClampConfig> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ClampConfig.class, ClampConfig::new).appendInherited(new KeyedCodec<Float>("Min", Codec.FLOAT), (o, v) -> {
            o.min = v.floatValue();
        }, o -> Float.valueOf(o.min), (o, p) -> {
            o.min = p.min;
        }).documentation("The inclusive minimum value of the clamp range").addValidator(Validators.range(Float.valueOf(-1.0f), Float.valueOf(1.0f))).add()).appendInherited(new KeyedCodec<Float>("Max", Codec.FLOAT), (o, v) -> {
            o.max = v.floatValue();
        }, o -> Float.valueOf(o.max), (o, p) -> {
            o.max = p.max;
        }).documentation("The inclusive maximum value of the clamp range").addValidator(Validators.range(Float.valueOf(-1.0f), Float.valueOf(1.0f))).add()).appendInherited(new KeyedCodec<Boolean>("Normalize", Codec.BOOLEAN), (o, v) -> {
            o.normalize = v;
        }, o -> o.normalize, (o, p) -> {
            o.normalize = p.normalize;
        }).documentation("Rescales the clamped output value back to the range -1 to 1").add()).afterDecode(range -> {
            range.min = Math.min(range.min, range.max);
            range.max = Math.max(range.min, range.max);
        })).build();
        @Nonnull
        public static final ClampConfig NONE = new ClampConfig();
        protected float min = -1.0f;
        protected float max = 1.0f;
        protected boolean normalize = true;

        @Override
        @Nonnull
        public com.hypixel.hytale.protocol.ClampConfig toPacket() {
            return new com.hypixel.hytale.protocol.ClampConfig(this.min, this.max, this.normalize);
        }

        @Nonnull
        public String toString() {
            return "ClampConfig{min=" + this.min + ", max=" + this.max + ", normalize=" + this.normalize + "}";
        }
    }
}

