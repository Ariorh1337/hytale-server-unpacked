/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.equalizereffect.config;

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

public class EqualizerEffect
implements JsonAssetWithMap<String, IndexedLookupTableAssetMap<String, EqualizerEffect>>,
NetworkSerializable<com.hypixel.hytale.protocol.EqualizerEffect> {
    public static final int EMPTY_ID = 0;
    public static final String EMPTY = "EMPTY";
    public static final EqualizerEffect EMPTY_EQUALIZER_EFFECT = new EqualizerEffect("EMPTY");
    public static final float MIN_GAIN_DB = -18.0f;
    public static final float MAX_GAIN_DB = 18.0f;
    public static final float MIN_WIDTH = 0.01f;
    public static final float MAX_WIDTH = 1.0f;
    public static final float LOW_FREQ_MIN = 50.0f;
    public static final float LOW_FREQ_MAX = 800.0f;
    public static final float LOW_MID_FREQ_MIN = 200.0f;
    public static final float LOW_MID_FREQ_MAX = 3000.0f;
    public static final float HIGH_MID_FREQ_MIN = 1000.0f;
    public static final float HIGH_MID_FREQ_MAX = 8000.0f;
    public static final float HIGH_FREQ_MIN = 4000.0f;
    public static final float HIGH_FREQ_MAX = 16000.0f;
    public static final AssetBuilderCodec<String, EqualizerEffect> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(EqualizerEffect.class, EqualizerEffect::new, Codec.STRING, (t, k) -> {
        t.id = k;
    }, t -> t.id, (asset, data) -> {
        asset.data = data;
    }, asset -> asset.data).documentation("An asset used to define a 4-band equalizer audio effect.")).metadata(new UIEditorPreview(UIEditorPreview.PreviewType.EQUALIZER_EFFECT))).appendInherited(new KeyedCodec<Float>("LowGain", Codec.FLOAT), (eq, f) -> {
        eq.lowGain = AudioUtil.decibelsToLinearGain(f.floatValue());
    }, eq -> Float.valueOf(AudioUtil.linearGainToDecibels(eq.lowGain)), (eq, parent) -> {
        eq.lowGain = parent.lowGain;
    }).metadata(new UIEditor(new UIEditor.FormattedNumber(null, " dB", null))).addValidator(Validators.range(Float.valueOf(-18.0f), Float.valueOf(18.0f))).documentation("Low band gain in decibels.").add()).appendInherited(new KeyedCodec<Float>("LowCutOff", Codec.FLOAT), (eq, f) -> {
        eq.lowCutOff = f.floatValue();
    }, eq -> Float.valueOf(eq.lowCutOff), (eq, parent) -> {
        eq.lowCutOff = parent.lowCutOff;
    }).addValidator(Validators.range(Float.valueOf(50.0f), Float.valueOf(800.0f))).documentation("Low band cutoff frequency in Hz.").add()).appendInherited(new KeyedCodec<Float>("LowMidGain", Codec.FLOAT), (eq, f) -> {
        eq.lowMidGain = AudioUtil.decibelsToLinearGain(f.floatValue());
    }, eq -> Float.valueOf(AudioUtil.linearGainToDecibels(eq.lowMidGain)), (eq, parent) -> {
        eq.lowMidGain = parent.lowMidGain;
    }).metadata(new UIEditor(new UIEditor.FormattedNumber(null, " dB", null))).addValidator(Validators.range(Float.valueOf(-18.0f), Float.valueOf(18.0f))).documentation("Low-mid band gain in decibels.").add()).appendInherited(new KeyedCodec<Float>("LowMidCenter", Codec.FLOAT), (eq, f) -> {
        eq.lowMidCenter = f.floatValue();
    }, eq -> Float.valueOf(eq.lowMidCenter), (eq, parent) -> {
        eq.lowMidCenter = parent.lowMidCenter;
    }).addValidator(Validators.range(Float.valueOf(200.0f), Float.valueOf(3000.0f))).documentation("Low-mid band center frequency in Hz.").add()).appendInherited(new KeyedCodec<Float>("LowMidWidth", Codec.FLOAT), (eq, f) -> {
        eq.lowMidWidth = f.floatValue();
    }, eq -> Float.valueOf(eq.lowMidWidth), (eq, parent) -> {
        eq.lowMidWidth = parent.lowMidWidth;
    }).addValidator(Validators.range(Float.valueOf(0.01f), Float.valueOf(1.0f))).documentation("Low-mid band width.").add()).appendInherited(new KeyedCodec<Float>("HighMidGain", Codec.FLOAT), (eq, f) -> {
        eq.highMidGain = AudioUtil.decibelsToLinearGain(f.floatValue());
    }, eq -> Float.valueOf(AudioUtil.linearGainToDecibels(eq.highMidGain)), (eq, parent) -> {
        eq.highMidGain = parent.highMidGain;
    }).metadata(new UIEditor(new UIEditor.FormattedNumber(null, " dB", null))).addValidator(Validators.range(Float.valueOf(-18.0f), Float.valueOf(18.0f))).documentation("High-mid band gain in decibels.").add()).appendInherited(new KeyedCodec<Float>("HighMidCenter", Codec.FLOAT), (eq, f) -> {
        eq.highMidCenter = f.floatValue();
    }, eq -> Float.valueOf(eq.highMidCenter), (eq, parent) -> {
        eq.highMidCenter = parent.highMidCenter;
    }).addValidator(Validators.range(Float.valueOf(1000.0f), Float.valueOf(8000.0f))).documentation("High-mid band center frequency in Hz.").add()).appendInherited(new KeyedCodec<Float>("HighMidWidth", Codec.FLOAT), (eq, f) -> {
        eq.highMidWidth = f.floatValue();
    }, eq -> Float.valueOf(eq.highMidWidth), (eq, parent) -> {
        eq.highMidWidth = parent.highMidWidth;
    }).addValidator(Validators.range(Float.valueOf(0.01f), Float.valueOf(1.0f))).documentation("High-mid band width.").add()).appendInherited(new KeyedCodec<Float>("HighGain", Codec.FLOAT), (eq, f) -> {
        eq.highGain = AudioUtil.decibelsToLinearGain(f.floatValue());
    }, eq -> Float.valueOf(AudioUtil.linearGainToDecibels(eq.highGain)), (eq, parent) -> {
        eq.highGain = parent.highGain;
    }).metadata(new UIEditor(new UIEditor.FormattedNumber(null, " dB", null))).addValidator(Validators.range(Float.valueOf(-18.0f), Float.valueOf(18.0f))).documentation("High band gain in decibels.").add()).appendInherited(new KeyedCodec<Float>("HighCutOff", Codec.FLOAT), (eq, f) -> {
        eq.highCutOff = f.floatValue();
    }, eq -> Float.valueOf(eq.highCutOff), (eq, parent) -> {
        eq.highCutOff = parent.highCutOff;
    }).addValidator(Validators.range(Float.valueOf(4000.0f), Float.valueOf(16000.0f))).documentation("High band cutoff frequency in Hz.").add()).build();
    public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache(new AssetKeyValidator(EqualizerEffect::getAssetStore));
    private static AssetStore<String, EqualizerEffect, IndexedLookupTableAssetMap<String, EqualizerEffect>> ASSET_STORE;
    protected AssetExtraInfo.Data data;
    protected String id;
    protected float lowGain = 1.0f;
    protected float lowCutOff = 200.0f;
    protected float lowMidGain = 1.0f;
    protected float lowMidCenter = 500.0f;
    protected float lowMidWidth = 1.0f;
    protected float highMidGain = 1.0f;
    protected float highMidCenter = 3000.0f;
    protected float highMidWidth = 1.0f;
    protected float highGain = 1.0f;
    protected float highCutOff = 6000.0f;
    private SoftReference<com.hypixel.hytale.protocol.EqualizerEffect> cachedPacket;

    public static AssetStore<String, EqualizerEffect, IndexedLookupTableAssetMap<String, EqualizerEffect>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(EqualizerEffect.class);
        }
        return ASSET_STORE;
    }

    public static IndexedLookupTableAssetMap<String, EqualizerEffect> getAssetMap() {
        return EqualizerEffect.getAssetStore().getAssetMap();
    }

    public EqualizerEffect(String id) {
        this.id = id;
    }

    protected EqualizerEffect() {
    }

    @Override
    public String getId() {
        return this.id;
    }

    public float getLowGain() {
        return this.lowGain;
    }

    public float getLowCutOff() {
        return this.lowCutOff;
    }

    public float getLowMidGain() {
        return this.lowMidGain;
    }

    public float getLowMidCenter() {
        return this.lowMidCenter;
    }

    public float getLowMidWidth() {
        return this.lowMidWidth;
    }

    public float getHighMidGain() {
        return this.highMidGain;
    }

    public float getHighMidCenter() {
        return this.highMidCenter;
    }

    public float getHighMidWidth() {
        return this.highMidWidth;
    }

    public float getHighGain() {
        return this.highGain;
    }

    public float getHighCutOff() {
        return this.highCutOff;
    }

    @Nonnull
    public String toString() {
        return "EqualizerEffect{id='" + this.id + "', lowGain=" + this.lowGain + ", lowCutOff=" + this.lowCutOff + ", lowMidGain=" + this.lowMidGain + ", lowMidCenter=" + this.lowMidCenter + ", lowMidWidth=" + this.lowMidWidth + ", highMidGain=" + this.highMidGain + ", highMidCenter=" + this.highMidCenter + ", highMidWidth=" + this.highMidWidth + ", highGain=" + this.highGain + ", highCutOff=" + this.highCutOff + "}";
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.EqualizerEffect toPacket() {
        com.hypixel.hytale.protocol.EqualizerEffect cached;
        com.hypixel.hytale.protocol.EqualizerEffect equalizerEffect = cached = this.cachedPacket == null ? null : this.cachedPacket.get();
        if (cached != null) {
            return cached;
        }
        com.hypixel.hytale.protocol.EqualizerEffect packet = new com.hypixel.hytale.protocol.EqualizerEffect();
        packet.id = this.id;
        packet.lowGain = this.lowGain;
        packet.lowCutOff = this.lowCutOff;
        packet.lowMidGain = this.lowMidGain;
        packet.lowMidCenter = this.lowMidCenter;
        packet.lowMidWidth = this.lowMidWidth;
        packet.highMidGain = this.highMidGain;
        packet.highMidCenter = this.highMidCenter;
        packet.highMidWidth = this.highMidWidth;
        packet.highGain = this.highGain;
        packet.highCutOff = this.highCutOff;
        this.cachedPacket = new SoftReference<com.hypixel.hytale.protocol.EqualizerEffect>(packet);
        return packet;
    }
}

