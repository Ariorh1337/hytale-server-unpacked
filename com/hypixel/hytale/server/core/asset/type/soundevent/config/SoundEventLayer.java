/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.soundevent.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.schema.metadata.ui.UIEditor;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.codec.validation.validator.ArrayValidator;
import com.hypixel.hytale.common.util.AudioUtil;
import com.hypixel.hytale.protocol.SoundEventLayerRandomSettings;
import com.hypixel.hytale.server.core.asset.common.CommonAssetValidator;
import com.hypixel.hytale.server.core.asset.common.OggVorbisInfoCache;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class SoundEventLayer
implements NetworkSerializable<com.hypixel.hytale.protocol.SoundEventLayer> {
    public static final Codec<SoundEventLayer> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(SoundEventLayer.class, SoundEventLayer::new).append(new KeyedCodec<Float>("Volume", Codec.FLOAT), (soundEventLayer, f) -> {
        soundEventLayer.volume = AudioUtil.decibelsToLinearGain(f.floatValue());
    }, soundEventLayer -> Float.valueOf(AudioUtil.linearGainToDecibels(soundEventLayer.volume))).metadata(new UIEditor(new UIEditor.FormattedNumber(null, " dB", null))).addValidator(Validators.range(Float.valueOf(-100.0f), Float.valueOf(10.0f))).documentation("Volume offset for this layer in decibels.").add()).append(new KeyedCodec<Float>("StartDelay", Codec.FLOAT), (soundEventLayer, f) -> {
        soundEventLayer.startDelay = f.floatValue();
    }, soundEventLayer -> Float.valueOf(soundEventLayer.startDelay)).documentation("A delay in seconds from when the sound event starts after which this layer should begin.").add()).append(new KeyedCodec<Boolean>("Looping", Codec.BOOLEAN), (soundEventLayer, b) -> {
        soundEventLayer.looping = b;
    }, soundEventLayer -> soundEventLayer.looping).documentation("Whether this layer loops.").add()).append(new KeyedCodec<Integer>("Probability", Codec.INTEGER), (soundEventLayer, i) -> {
        soundEventLayer.probability = i;
    }, soundEventLayer -> soundEventLayer.probability).documentation("The probability of this layer being played when the sound event is triggered in percentage.").add()).append(new KeyedCodec<Float>("ProbabilityRerollDelay", Codec.FLOAT), (soundEventLayer, f) -> {
        soundEventLayer.probabilityRerollDelay = f.floatValue();
    }, soundEventLayer -> Float.valueOf(soundEventLayer.probabilityRerollDelay)).documentation("A delay in seconds before the probability of this layer playing can be rerolled to see if it will now play (or not play) again.").add()).append(new KeyedCodec<RandomSettings>("RandomSettings", RandomSettings.CODEC), (soundEventLayer, o) -> {
        soundEventLayer.randomSettings = o;
    }, soundEventLayer -> soundEventLayer.randomSettings).documentation("Randomization settings for parameters of this layer.").add()).append(new KeyedCodec<T[]>("Files", Codec.STRING_ARRAY), (soundEventLayer, s) -> {
        soundEventLayer.files = s;
    }, soundEventLayer -> soundEventLayer.files).addValidator(Validators.nonEmptyArray()).addValidator(new ArrayValidator<String>(CommonAssetValidator.SOUNDS)).documentation("The list of possible sound files for this layer. One will be chosen at random.").add()).append(new KeyedCodec<Integer>("RoundRobinHistorySize", Codec.INTEGER), (soundEventLayer, i) -> {
        soundEventLayer.roundRobinHistorySize = i;
    }, soundEventLayer -> soundEventLayer.roundRobinHistorySize).addValidator(Validators.range(0, 32)).documentation("The same sound file will not repeat within this many plays. 0 disables round-robin behavior.").add()).afterDecode(layer -> {
        if (layer.files == null) {
            return;
        }
        for (String file : layer.files) {
            OggVorbisInfoCache.OggVorbisInfo info = OggVorbisInfoCache.getNow(file);
            if (info == null || info.channels <= layer.highestNumberOfChannels) continue;
            layer.highestNumberOfChannels = info.channels;
        }
    })).build();
    protected transient float volume = 1.0f;
    protected float startDelay = 0.0f;
    protected boolean looping = false;
    protected int probability = 100;
    protected float probabilityRerollDelay = 1.0f;
    protected RandomSettings randomSettings = RandomSettings.DEFAULT;
    protected String[] files;
    protected int roundRobinHistorySize = 0;
    protected transient int highestNumberOfChannels = 0;

    public SoundEventLayer(float volume, float startDelay, boolean looping, int probability, float probabilityRerollDelay, RandomSettings randomSettings, String[] files, int roundRobinHistorySize) {
        this.volume = volume;
        this.startDelay = startDelay;
        this.looping = looping;
        this.probability = probability;
        this.probabilityRerollDelay = probabilityRerollDelay;
        this.randomSettings = randomSettings;
        this.files = files;
        this.roundRobinHistorySize = roundRobinHistorySize;
    }

    protected SoundEventLayer() {
    }

    public float getVolume() {
        return this.volume;
    }

    public float getStartDelay() {
        return this.startDelay;
    }

    public boolean isLooping() {
        return this.looping;
    }

    public int getProbability() {
        return this.probability;
    }

    public float getProbabilityRerollDelay() {
        return this.probabilityRerollDelay;
    }

    public RandomSettings getRandomSettings() {
        return this.randomSettings;
    }

    public String[] getFiles() {
        return this.files;
    }

    public int getRoundRobinHistorySize() {
        return this.roundRobinHistorySize;
    }

    public int getHighestNumberOfChannels() {
        return this.highestNumberOfChannels;
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.SoundEventLayer toPacket() {
        com.hypixel.hytale.protocol.SoundEventLayer packet = new com.hypixel.hytale.protocol.SoundEventLayer();
        packet.volume = this.volume;
        packet.startDelay = this.startDelay;
        packet.looping = this.looping;
        packet.probability = this.probability;
        packet.probabilityRerollDelay = this.probabilityRerollDelay;
        packet.randomSettings = new SoundEventLayerRandomSettings();
        packet.randomSettings.minVolume = this.randomSettings.minVolume;
        packet.randomSettings.maxVolume = this.randomSettings.maxVolume;
        packet.randomSettings.minPitch = this.randomSettings.minPitch;
        packet.randomSettings.maxPitch = this.randomSettings.maxPitch;
        packet.randomSettings.maxStartOffset = this.randomSettings.maxStartOffset;
        packet.files = this.files;
        packet.roundRobinHistorySize = this.roundRobinHistorySize;
        return packet;
    }

    @Nonnull
    public String toString() {
        return "SoundEventLayer{, volume=" + this.volume + ", startDelay=" + this.startDelay + ", looping=" + this.looping + ", probability=" + this.probability + ", probabilityRerollDelay=" + this.probabilityRerollDelay + ", randomSettings=" + String.valueOf(this.randomSettings) + ", files=" + Arrays.toString(this.files) + ", roundRobinHistorySize=" + this.roundRobinHistorySize + ", highestNumberOfChannels=" + this.highestNumberOfChannels + "}";
    }

    public static class RandomSettings {
        public static final Codec<RandomSettings> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(RandomSettings.class, RandomSettings::new).append(new KeyedCodec<Float>("MinVolume", Codec.FLOAT), (soundEventLayer, f) -> {
            soundEventLayer.minVolume = AudioUtil.decibelsToLinearGain(f.floatValue());
        }, soundEventLayer -> Float.valueOf(AudioUtil.linearGainToDecibels(soundEventLayer.minVolume))).addValidator(Validators.range(Float.valueOf(-100.0f), Float.valueOf(0.0f))).documentation("Minimum additional random volume offset in decibels.").add()).append(new KeyedCodec<Float>("MaxVolume", Codec.FLOAT), (soundEventLayer, f) -> {
            soundEventLayer.maxVolume = AudioUtil.decibelsToLinearGain(f.floatValue());
        }, soundEventLayer -> Float.valueOf(AudioUtil.linearGainToDecibels(soundEventLayer.maxVolume))).addValidator(Validators.range(Float.valueOf(0.0f), Float.valueOf(10.0f))).documentation("Maximum additional random volume offset in decibels.").add()).append(new KeyedCodec<Float>("MinPitch", Codec.FLOAT), (soundEventLayer, f) -> {
            soundEventLayer.minPitch = AudioUtil.semitonesToLinearPitch(f.floatValue());
        }, soundEventLayer -> Float.valueOf(AudioUtil.linearPitchToSemitones(soundEventLayer.minPitch))).addValidator(Validators.range(Float.valueOf(-12.0f), Float.valueOf(0.0f))).documentation("Minimum additional random pitch offset in semitones.").add()).append(new KeyedCodec<Float>("MaxPitch", Codec.FLOAT), (soundEventLayer, f) -> {
            soundEventLayer.maxPitch = AudioUtil.semitonesToLinearPitch(f.floatValue());
        }, soundEventLayer -> Float.valueOf(AudioUtil.linearPitchToSemitones(soundEventLayer.maxPitch))).addValidator(Validators.range(Float.valueOf(0.0f), Float.valueOf(12.0f))).documentation("Maximum additional random pitch offset in semitones.").add()).append(new KeyedCodec<Float>("MaxStartOffset", Codec.FLOAT), (soundEventLayer, f) -> {
            soundEventLayer.maxStartOffset = f.floatValue();
        }, soundEventLayer -> Float.valueOf(soundEventLayer.maxStartOffset)).addValidator(Validators.range(Float.valueOf(0.0f), Float.valueOf(Float.MAX_VALUE))).documentation("Maximum amount by which to offset the start of this sound event (e.g. start up to x seconds into the sound). This should only really be used for looping sounds to prevent phasing issues.").add()).build();
        public static final RandomSettings DEFAULT = new RandomSettings();
        protected transient float minVolume = 1.0f;
        protected transient float maxVolume = 1.0f;
        protected transient float minPitch = 1.0f;
        protected transient float maxPitch = 1.0f;
        protected float maxStartOffset;

        public float getMinVolume() {
            return this.minVolume;
        }

        public float getMaxVolume() {
            return this.maxVolume;
        }

        public float getMinPitch() {
            return this.minPitch;
        }

        public float getMaxPitch() {
            return this.maxPitch;
        }

        public float getMaxStartOffset() {
            return this.maxStartOffset;
        }

        @Nonnull
        public String toString() {
            return "RandomSettings{, minVolume=" + this.minVolume + ", maxVolume=" + this.maxVolume + ", minPitch=" + this.minPitch + ", maxPitch=" + this.maxPitch + ", maxStartOffset=" + this.maxStartOffset + "}";
        }
    }
}

