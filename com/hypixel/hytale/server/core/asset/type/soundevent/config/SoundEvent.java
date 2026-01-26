/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.soundevent.config;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetKeyValidator;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.schema.metadata.ui.UIEditor;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.common.util.AudioUtil;
import com.hypixel.hytale.server.core.asset.type.audiocategory.config.AudioCategory;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEventLayer;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SoundEvent
implements JsonAssetWithMap<String, IndexedLookupTableAssetMap<String, SoundEvent>>,
NetworkSerializable<com.hypixel.hytale.protocol.SoundEvent> {
    public static final int EMPTY_ID = 0;
    public static final String EMPTY = "EMPTY";
    public static final SoundEvent EMPTY_SOUND_EVENT = new SoundEvent("EMPTY");
    public static final AssetBuilderCodec<String, SoundEvent> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(SoundEvent.class, SoundEvent::new, Codec.STRING, (t, k) -> {
        t.id = k;
    }, t -> t.id, (asset, data) -> {
        asset.data = data;
    }, asset -> asset.data).appendInherited(new KeyedCodec<Float>("Volume", Codec.FLOAT), (soundEvent, f) -> {
        soundEvent.volume = AudioUtil.decibelsToLinearGain(f.floatValue());
    }, soundEvent -> Float.valueOf(AudioUtil.linearGainToDecibels(soundEvent.volume)), (soundEvent, parent) -> {
        soundEvent.volume = parent.volume;
    }).metadata(new UIEditor(new UIEditor.FormattedNumber(null, " dB", null))).addValidator(Validators.range(Float.valueOf(-100.0f), Float.valueOf(10.0f))).documentation("Volume adjustment of the sound event in decibels.").add()).appendInherited(new KeyedCodec<Float>("Pitch", Codec.FLOAT), (soundEvent, f) -> {
        soundEvent.pitch = AudioUtil.semitonesToLinearPitch(f.floatValue());
    }, soundEvent -> Float.valueOf(AudioUtil.linearPitchToSemitones(soundEvent.pitch)), (soundEvent, parent) -> {
        soundEvent.pitch = parent.pitch;
    }).addValidator(Validators.range(Float.valueOf(-12.0f), Float.valueOf(12.0f))).documentation("Pitch adjustment of the sound event in semitones.").add()).appendInherited(new KeyedCodec<Float>("MusicDuckingVolume", Codec.FLOAT), (soundEvent, f) -> {
        soundEvent.musicDuckingVolume = AudioUtil.decibelsToLinearGain(f.floatValue());
    }, soundEvent -> Float.valueOf(AudioUtil.linearGainToDecibels(soundEvent.musicDuckingVolume)), (soundEvent, parent) -> {
        soundEvent.musicDuckingVolume = parent.musicDuckingVolume;
    }).addValidator(Validators.range(Float.valueOf(-100.0f), Float.valueOf(0.0f))).documentation("Amount to duck music volume when playing in decibels.").add()).appendInherited(new KeyedCodec<Float>("AmbientDuckingVolume", Codec.FLOAT), (soundEvent, f) -> {
        soundEvent.ambientDuckingVolume = AudioUtil.decibelsToLinearGain(f.floatValue());
    }, soundEvent -> Float.valueOf(AudioUtil.linearGainToDecibels(soundEvent.ambientDuckingVolume)), (soundEvent, parent) -> {
        soundEvent.ambientDuckingVolume = parent.ambientDuckingVolume;
    }).addValidator(Validators.range(Float.valueOf(-100.0f), Float.valueOf(0.0f))).documentation("Amount to duck ambient sounds when playing in decibels.").add()).appendInherited(new KeyedCodec<Float>("StartAttenuationDistance", Codec.FLOAT), (soundEvent, f) -> {
        soundEvent.startAttenuationDistance = f.floatValue();
    }, soundEvent -> Float.valueOf(soundEvent.startAttenuationDistance), (soundEvent, parent) -> {
        soundEvent.startAttenuationDistance = parent.startAttenuationDistance;
    }).documentation("Distance at which to begin attenuation in blocks.").add()).appendInherited(new KeyedCodec<Float>("MaxDistance", Codec.FLOAT), (soundEvent, f) -> {
        soundEvent.maxDistance = f.floatValue();
    }, soundEvent -> Float.valueOf(soundEvent.maxDistance), (soundEvent, parent) -> {
        soundEvent.maxDistance = parent.maxDistance;
    }).documentation("Maximum distance at which this sound event can be heard in blocks (i.e. the distance at which it's attenuated to zero).").add()).appendInherited(new KeyedCodec<Integer>("MaxInstance", Codec.INTEGER), (soundEvent, i) -> {
        soundEvent.maxInstance = i;
    }, soundEvent -> soundEvent.maxInstance, (soundEvent, parent) -> {
        soundEvent.maxInstance = parent.maxInstance;
    }).addValidator(Validators.range(1, 100)).documentation("Max concurrent number of instances of this sound event.").add()).appendInherited(new KeyedCodec<Boolean>("PreventSoundInterruption", Codec.BOOLEAN), (soundEvent, b) -> {
        soundEvent.preventSoundInterruption = b;
    }, soundEvent -> soundEvent.preventSoundInterruption, (soundEvent, parent) -> {
        soundEvent.preventSoundInterruption = parent.preventSoundInterruption;
    }).documentation("Whether to prevent interruption of this sound event.").add()).appendInherited(new KeyedCodec<T[]>("Layers", new ArrayCodec<SoundEventLayer>(SoundEventLayer.CODEC, SoundEventLayer[]::new)), (soundEvent, objects) -> {
        soundEvent.layers = objects;
    }, soundEvent -> soundEvent.layers, (soundEvent, parent) -> {
        soundEvent.layers = parent.layers;
    }).addValidator(Validators.nonEmptyArray()).documentation("The layered sounds that make up this sound event.").add()).appendInherited(new KeyedCodec<String>("AudioCategory", Codec.STRING), (soundEvent, s) -> {
        soundEvent.audioCategoryId = s;
    }, soundEvent -> soundEvent.audioCategoryId, (soundEvent, parent) -> {
        soundEvent.audioCategoryId = parent.audioCategoryId;
    }).addValidator(AudioCategory.VALIDATOR_CACHE.getValidator()).documentation("Audio category to assign this sound event to for additional property routing.").add()).afterDecode(SoundEvent::processConfig)).build();
    public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache(new AssetKeyValidator(SoundEvent::getAssetStore));
    private static AssetStore<String, SoundEvent, IndexedLookupTableAssetMap<String, SoundEvent>> ASSET_STORE;
    protected AssetExtraInfo.Data data;
    protected String id;
    protected transient float volume = 1.0f;
    protected transient float pitch = 1.0f;
    protected transient float musicDuckingVolume = 1.0f;
    protected transient float ambientDuckingVolume = 1.0f;
    protected float startAttenuationDistance = 2.0f;
    protected float maxDistance = 16.0f;
    protected int maxInstance = 50;
    protected boolean preventSoundInterruption = false;
    protected SoundEventLayer[] layers;
    @Nullable
    protected String audioCategoryId = null;
    protected transient int audioCategoryIndex = 0;
    protected transient int highestNumberOfChannels = 0;
    private SoftReference<com.hypixel.hytale.protocol.SoundEvent> cachedPacket;

    public static AssetStore<String, SoundEvent, IndexedLookupTableAssetMap<String, SoundEvent>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(SoundEvent.class);
        }
        return ASSET_STORE;
    }

    public static IndexedLookupTableAssetMap<String, SoundEvent> getAssetMap() {
        return SoundEvent.getAssetStore().getAssetMap();
    }

    protected void processConfig() {
        if (this.audioCategoryId != null) {
            this.audioCategoryIndex = AudioCategory.getAssetMap().getIndex(this.audioCategoryId);
        }
        if (this.layers != null) {
            for (SoundEventLayer layer : this.layers) {
                if (layer.highestNumberOfChannels <= this.highestNumberOfChannels) continue;
                this.highestNumberOfChannels = layer.highestNumberOfChannels;
            }
        }
    }

    public SoundEvent(String id, float volume, float pitch, float musicDuckingVolume, float ambientDuckingVolume, float startAttenuationDistance, float maxDistance, int maxInstance, boolean preventSoundInterruption, SoundEventLayer[] layers) {
        this.id = id;
        this.volume = volume;
        this.pitch = pitch;
        this.musicDuckingVolume = musicDuckingVolume;
        this.ambientDuckingVolume = ambientDuckingVolume;
        this.startAttenuationDistance = startAttenuationDistance;
        this.maxDistance = maxDistance;
        this.maxInstance = maxInstance;
        this.preventSoundInterruption = preventSoundInterruption;
        this.layers = layers;
    }

    public SoundEvent(String id) {
        this.id = id;
    }

    protected SoundEvent() {
    }

    @Override
    public String getId() {
        return this.id;
    }

    public float getVolume() {
        return this.volume;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getMusicDuckingVolume() {
        return this.musicDuckingVolume;
    }

    public float getAmbientDuckingVolume() {
        return this.ambientDuckingVolume;
    }

    public float getStartAttenuationDistance() {
        return this.startAttenuationDistance;
    }

    public float getMaxDistance() {
        return this.maxDistance;
    }

    public int getMaxInstance() {
        return this.maxInstance;
    }

    public boolean getPreventSoundInterruption() {
        return this.preventSoundInterruption;
    }

    public SoundEventLayer[] getLayers() {
        return this.layers;
    }

    @Nullable
    public String getAudioCategoryId() {
        return this.audioCategoryId;
    }

    public int getAudioCategoryIndex() {
        return this.audioCategoryIndex;
    }

    public int getHighestNumberOfChannels() {
        return this.highestNumberOfChannels;
    }

    @Nonnull
    public String toString() {
        return "SoundEvent{id='" + this.id + "', volume=" + this.volume + ", pitch=" + this.pitch + ", musicDuckingVolume=" + this.musicDuckingVolume + ", ambientDuckingVolume=" + this.ambientDuckingVolume + ", startAttenuationDistance=" + this.startAttenuationDistance + ", maxDistance=" + this.maxDistance + ", maxInstance=" + this.maxInstance + ", preventSoundInterruption=" + this.preventSoundInterruption + ", layers=" + Arrays.toString(this.layers) + ", audioCategoryId='" + this.audioCategoryId + "', audioCategoryIndex=" + this.audioCategoryIndex + ", highestNumberOfChannels=" + this.highestNumberOfChannels + "}";
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.SoundEvent toPacket() {
        com.hypixel.hytale.protocol.SoundEvent cached;
        com.hypixel.hytale.protocol.SoundEvent soundEvent = cached = this.cachedPacket == null ? null : this.cachedPacket.get();
        if (cached != null) {
            return cached;
        }
        com.hypixel.hytale.protocol.SoundEvent packet = new com.hypixel.hytale.protocol.SoundEvent();
        packet.id = this.id;
        packet.volume = this.volume;
        packet.pitch = this.pitch;
        packet.musicDuckingVolume = this.musicDuckingVolume;
        packet.ambientDuckingVolume = this.ambientDuckingVolume;
        packet.startAttenuationDistance = this.startAttenuationDistance;
        packet.maxDistance = this.maxDistance;
        packet.maxInstance = this.maxInstance;
        packet.preventSoundInterruption = this.preventSoundInterruption;
        packet.audioCategory = this.audioCategoryIndex;
        if (this.layers != null && this.layers.length > 0) {
            packet.layers = new com.hypixel.hytale.protocol.SoundEventLayer[this.layers.length];
            for (int i = 0; i < this.layers.length; ++i) {
                packet.layers[i] = this.layers[i].toPacket();
            }
        }
        this.cachedPacket = new SoftReference<com.hypixel.hytale.protocol.SoundEvent>(packet);
        return packet;
    }
}

