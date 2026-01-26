/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.reverbeffect.config;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetKeyValidator;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.schema.metadata.ui.UIEditor;
import com.hypixel.hytale.codec.schema.metadata.ui.UIEditorPreview;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.common.util.AudioUtil;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import java.lang.ref.SoftReference;
import javax.annotation.Nonnull;

public class ReverbEffect
implements JsonAssetWithMap<String, IndexedLookupTableAssetMap<String, ReverbEffect>>,
NetworkSerializable<com.hypixel.hytale.protocol.ReverbEffect> {
    public static final int EMPTY_ID = 0;
    public static final String EMPTY = "EMPTY";
    public static final ReverbEffect EMPTY_REVERB_EFFECT = new ReverbEffect("EMPTY");
    public static final AssetBuilderCodec<String, ReverbEffect> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(ReverbEffect.class, ReverbEffect::new, Codec.STRING, (t, k) -> {
        t.id = k;
    }, t -> t.id, (asset, data) -> {
        asset.data = data;
    }, asset -> asset.data).documentation("An asset used to define a reverb audio effect.")).metadata(new UIEditorPreview(UIEditorPreview.PreviewType.REVERB_EFFECT))).appendInherited(new KeyedCodec<Float>("DryGain", Codec.FLOAT), (reverb, f) -> {
        reverb.dryGain = AudioUtil.decibelsToLinearGain(f.floatValue());
    }, reverb -> Float.valueOf(AudioUtil.linearGainToDecibels(reverb.dryGain)), (reverb, parent) -> {
        reverb.dryGain = parent.dryGain;
    }).metadata(new UIEditor(new UIEditor.FormattedNumber(null, " dB", null))).addValidator(Validators.range(Float.valueOf(-100.0f), Float.valueOf(10.0f))).documentation("Dry signal gain adjustment in decibels.").add()).appendInherited(new KeyedCodec<Float>("ModalDensity", Codec.FLOAT), (reverb, f) -> {
        reverb.modalDensity = f.floatValue();
    }, reverb -> Float.valueOf(reverb.modalDensity), (reverb, parent) -> {
        reverb.modalDensity = parent.modalDensity;
    }).addValidator(Validators.range(Float.valueOf(0.0f), Float.valueOf(1.0f))).documentation("Modal density of the reverb.").add()).appendInherited(new KeyedCodec<Float>("Diffusion", Codec.FLOAT), (reverb, f) -> {
        reverb.diffusion = f.floatValue();
    }, reverb -> Float.valueOf(reverb.diffusion), (reverb, parent) -> {
        reverb.diffusion = parent.diffusion;
    }).addValidator(Validators.range(Float.valueOf(0.0f), Float.valueOf(1.0f))).documentation("Diffusion of the reverb reflections.").add()).appendInherited(new KeyedCodec<Float>("Gain", Codec.FLOAT), (reverb, f) -> {
        reverb.gain = AudioUtil.decibelsToLinearGain(f.floatValue());
    }, reverb -> Float.valueOf(AudioUtil.linearGainToDecibels(reverb.gain)), (reverb, parent) -> {
        reverb.gain = parent.gain;
    }).metadata(new UIEditor(new UIEditor.FormattedNumber(null, " dB", null))).addValidator(Validators.range(Float.valueOf(-100.0f), Float.valueOf(0.0f))).documentation("Overall reverb gain in decibels.").add()).appendInherited(new KeyedCodec<Float>("HighFrequencyGain", Codec.FLOAT), (reverb, f) -> {
        reverb.highFrequencyGain = AudioUtil.decibelsToLinearGain(f.floatValue());
    }, reverb -> Float.valueOf(AudioUtil.linearGainToDecibels(reverb.highFrequencyGain)), (reverb, parent) -> {
        reverb.highFrequencyGain = parent.highFrequencyGain;
    }).metadata(new UIEditor(new UIEditor.FormattedNumber(null, " dB", null))).addValidator(Validators.range(Float.valueOf(-100.0f), Float.valueOf(0.0f))).documentation("High frequency gain in decibels.").add()).appendInherited(new KeyedCodec<Float>("DecayTime", Codec.FLOAT), (reverb, f) -> {
        reverb.decayTime = f.floatValue();
    }, reverb -> Float.valueOf(reverb.decayTime), (reverb, parent) -> {
        reverb.decayTime = parent.decayTime;
    }).addValidator(Validators.range(Float.valueOf(0.1f), Float.valueOf(20.0f))).documentation("Decay time in seconds.").add()).appendInherited(new KeyedCodec<Float>("HighFrequencyDecayRatio", Codec.FLOAT), (reverb, f) -> {
        reverb.highFrequencyDecayRatio = f.floatValue();
    }, reverb -> Float.valueOf(reverb.highFrequencyDecayRatio), (reverb, parent) -> {
        reverb.highFrequencyDecayRatio = parent.highFrequencyDecayRatio;
    }).addValidator(Validators.range(Float.valueOf(0.1f), Float.valueOf(2.0f))).documentation("High frequency decay ratio.").add()).appendInherited(new KeyedCodec<Float>("ReflectionGain", Codec.FLOAT), (reverb, f) -> {
        reverb.reflectionGain = AudioUtil.decibelsToLinearGain(f.floatValue());
    }, reverb -> Float.valueOf(AudioUtil.linearGainToDecibels(reverb.reflectionGain)), (reverb, parent) -> {
        reverb.reflectionGain = parent.reflectionGain;
    }).metadata(new UIEditor(new UIEditor.FormattedNumber(null, " dB", null))).addValidator(Validators.range(Float.valueOf(-100.0f), Float.valueOf(10.0f))).documentation("Early reflections gain in decibels.").add()).appendInherited(new KeyedCodec<Float>("ReflectionDelay", Codec.FLOAT), (reverb, f) -> {
        reverb.reflectionDelay = f.floatValue();
    }, reverb -> Float.valueOf(reverb.reflectionDelay), (reverb, parent) -> {
        reverb.reflectionDelay = parent.reflectionDelay;
    }).addValidator(Validators.range(Float.valueOf(0.0f), Float.valueOf(0.3f))).documentation("Early reflections delay in seconds.").add()).appendInherited(new KeyedCodec<Float>("LateReverbGain", Codec.FLOAT), (reverb, f) -> {
        reverb.lateReverbGain = AudioUtil.decibelsToLinearGain(f.floatValue());
    }, reverb -> Float.valueOf(AudioUtil.linearGainToDecibels(reverb.lateReverbGain)), (reverb, parent) -> {
        reverb.lateReverbGain = parent.lateReverbGain;
    }).metadata(new UIEditor(new UIEditor.FormattedNumber(null, " dB", null))).addValidator(Validators.range(Float.valueOf(-100.0f), Float.valueOf(20.0f))).documentation("Late reverb gain in decibels.").add()).appendInherited(new KeyedCodec<Float>("LateReverbDelay", Codec.FLOAT), (reverb, f) -> {
        reverb.lateReverbDelay = f.floatValue();
    }, reverb -> Float.valueOf(reverb.lateReverbDelay), (reverb, parent) -> {
        reverb.lateReverbDelay = parent.lateReverbDelay;
    }).addValidator(Validators.range(Float.valueOf(0.0f), Float.valueOf(0.1f))).documentation("Late reverb delay in seconds.").add()).appendInherited(new KeyedCodec<Float>("RoomRolloffFactor", Codec.FLOAT), (reverb, f) -> {
        reverb.roomRolloffFactor = f.floatValue();
    }, reverb -> Float.valueOf(reverb.roomRolloffFactor), (reverb, parent) -> {
        reverb.roomRolloffFactor = parent.roomRolloffFactor;
    }).addValidator(Validators.range(Float.valueOf(0.0f), Float.valueOf(10.0f))).documentation("Room rolloff factor.").add()).appendInherited(new KeyedCodec<Float>("AirAbsorbptionHighFrequencyGain", Codec.FLOAT), (reverb, f) -> {
        reverb.airAbsorptionHighFrequencyGain = AudioUtil.decibelsToLinearGain(f.floatValue());
    }, reverb -> Float.valueOf(AudioUtil.linearGainToDecibels(reverb.airAbsorptionHighFrequencyGain)), (reverb, parent) -> {
        reverb.airAbsorptionHighFrequencyGain = parent.airAbsorptionHighFrequencyGain;
    }).metadata(new UIEditor(new UIEditor.FormattedNumber(null, " dB", null))).addValidator(Validators.range(Float.valueOf(-1.0f), Float.valueOf(0.0f))).documentation("Air absorption high frequency gain in decibels.").add()).appendInherited(new KeyedCodec<Boolean>("LimitDecayHighFrequency", Codec.BOOLEAN), (reverb, b) -> {
        reverb.limitDecayHighFrequency = b;
    }, reverb -> reverb.limitDecayHighFrequency, (reverb, parent) -> {
        reverb.limitDecayHighFrequency = parent.limitDecayHighFrequency;
    }).documentation("Whether to limit high frequency decay time.").add()).build();
    public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache(new AssetKeyValidator(ReverbEffect::getAssetStore));
    private static AssetStore<String, ReverbEffect, IndexedLookupTableAssetMap<String, ReverbEffect>> ASSET_STORE;
    protected AssetExtraInfo.Data data;
    protected String id;
    protected float dryGain = AudioUtil.decibelsToLinearGain(0.0f);
    protected float modalDensity = 1.0f;
    protected float diffusion = 1.0f;
    protected float gain = AudioUtil.decibelsToLinearGain(-10.0f);
    protected float highFrequencyGain = AudioUtil.decibelsToLinearGain(-1.0f);
    protected float decayTime = 1.49f;
    protected float highFrequencyDecayRatio = 0.83f;
    protected float reflectionGain = AudioUtil.decibelsToLinearGain(-26.0f);
    protected float reflectionDelay = 0.007f;
    protected float lateReverbGain = AudioUtil.decibelsToLinearGain(2.0f);
    protected float lateReverbDelay = 0.011f;
    protected float roomRolloffFactor = 0.0f;
    protected float airAbsorptionHighFrequencyGain = AudioUtil.decibelsToLinearGain(-0.05f);
    protected boolean limitDecayHighFrequency = true;
    private SoftReference<com.hypixel.hytale.protocol.ReverbEffect> cachedPacket;

    public static AssetStore<String, ReverbEffect, IndexedLookupTableAssetMap<String, ReverbEffect>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(ReverbEffect.class);
        }
        return ASSET_STORE;
    }

    public static IndexedLookupTableAssetMap<String, ReverbEffect> getAssetMap() {
        return ReverbEffect.getAssetStore().getAssetMap();
    }

    public ReverbEffect(String id) {
        this.id = id;
    }

    protected ReverbEffect() {
    }

    @Override
    public String getId() {
        return this.id;
    }

    public float getDryGain() {
        return this.dryGain;
    }

    public float getModalDensity() {
        return this.modalDensity;
    }

    public float getDiffusion() {
        return this.diffusion;
    }

    public float getGain() {
        return this.gain;
    }

    public float getHighFrequencyGain() {
        return this.highFrequencyGain;
    }

    public float getDecayTime() {
        return this.decayTime;
    }

    public float getHighFrequencyDecayRatio() {
        return this.highFrequencyDecayRatio;
    }

    public float getReflectionGain() {
        return this.reflectionGain;
    }

    public float getReflectionDelay() {
        return this.reflectionDelay;
    }

    public float getLateReverbGain() {
        return this.lateReverbGain;
    }

    public float getLateReverbDelay() {
        return this.lateReverbDelay;
    }

    public float getRoomRolloffFactor() {
        return this.roomRolloffFactor;
    }

    public float getAirAbsorptionHighFrequencyGain() {
        return this.airAbsorptionHighFrequencyGain;
    }

    public boolean isLimitDecayHighFrequency() {
        return this.limitDecayHighFrequency;
    }

    @Nonnull
    public String toString() {
        return "ReverbEffect{id='" + this.id + "', dryGain=" + this.dryGain + ", modalDensity=" + this.modalDensity + ", diffusion=" + this.diffusion + ", gain=" + this.gain + ", highFrequencyGain=" + this.highFrequencyGain + ", decayTime=" + this.decayTime + ", highFrequencyDecayRatio=" + this.highFrequencyDecayRatio + ", reflectionGain=" + this.reflectionGain + ", reflectionDelay=" + this.reflectionDelay + ", lateReverbGain=" + this.lateReverbGain + ", lateReverbDelay=" + this.lateReverbDelay + ", roomRolloffFactor=" + this.roomRolloffFactor + ", airAbsorptionHighFrequencyGain=" + this.airAbsorptionHighFrequencyGain + ", limitDecayHighFrequency=" + this.limitDecayHighFrequency + "}";
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.ReverbEffect toPacket() {
        com.hypixel.hytale.protocol.ReverbEffect cached;
        com.hypixel.hytale.protocol.ReverbEffect reverbEffect = cached = this.cachedPacket == null ? null : this.cachedPacket.get();
        if (cached != null) {
            return cached;
        }
        com.hypixel.hytale.protocol.ReverbEffect packet = new com.hypixel.hytale.protocol.ReverbEffect();
        packet.id = this.id;
        packet.dryGain = this.dryGain;
        packet.modalDensity = this.modalDensity;
        packet.diffusion = this.diffusion;
        packet.gain = this.gain;
        packet.highFrequencyGain = this.highFrequencyGain;
        packet.decayTime = this.decayTime;
        packet.highFrequencyDecayRatio = this.highFrequencyDecayRatio;
        packet.reflectionGain = this.reflectionGain;
        packet.reflectionDelay = this.reflectionDelay;
        packet.lateReverbGain = this.lateReverbGain;
        packet.lateReverbDelay = this.lateReverbDelay;
        packet.roomRolloffFactor = this.roomRolloffFactor;
        packet.airAbsorptionHighFrequencyGain = this.airAbsorptionHighFrequencyGain;
        packet.limitDecayHighFrequency = this.limitDecayHighFrequency;
        this.cachedPacket = new SoftReference<com.hypixel.hytale.protocol.ReverbEffect>(packet);
        return packet;
    }
}

