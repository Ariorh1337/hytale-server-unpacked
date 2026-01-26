/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.soundevent.validator;

import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.validation.ValidationResults;
import com.hypixel.hytale.codec.validation.Validator;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import com.hypixel.hytale.server.core.asset.common.SoundFileValidators;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEventLayer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SoundEventValidators {
    public static final LoopValidator LOOPING = new LoopValidator(true);
    public static final LoopValidator ONESHOT = new LoopValidator(false);
    public static final ChannelValidator MONO = new ChannelValidator(1);
    public static final ChannelValidator STEREO = new ChannelValidator(2);
    public static final ValidatorCache<String> MONO_VALIDATOR_CACHE = new ValidatorCache<String>(MONO);
    public static final ValidatorCache<String> STEREO_VALIDATOR_CACHE = new ValidatorCache<String>(STEREO);
    public static final ValidatorCache<String> ONESHOT_VALIDATOR_CACHE = new ValidatorCache<String>(ONESHOT);

    public static class LoopValidator
    implements Validator<String> {
        private final boolean looping;

        private LoopValidator(boolean looping) {
            this.looping = looping;
        }

        @Override
        public void accept(@Nullable String s, @Nonnull ValidationResults results) {
            if (s == null) {
                return;
            }
            SoundEvent soundEvent = (SoundEvent)SoundEvent.getAssetMap().getAsset(s);
            if (soundEvent == null) {
                results.fail("Sound event with name '" + s + "' does not exist");
                return;
            }
            if (soundEvent.getLayers() == null) {
                return;
            }
            if (this.looping) {
                for (SoundEventLayer layer : soundEvent.getLayers()) {
                    if (!layer.isLooping()) continue;
                    return;
                }
                results.fail("Sound event with name '" + s + "' does not have a looping layer");
            } else {
                for (SoundEventLayer layer : soundEvent.getLayers()) {
                    if (!layer.isLooping()) continue;
                    results.fail("Sound event with name '" + s + "' has a looping layer and is not a oneshot sound");
                    return;
                }
            }
        }

        @Override
        public void updateSchema(SchemaContext context, Schema target) {
        }
    }

    public static class ChannelValidator
    implements Validator<String> {
        private final int channelCount;

        public ChannelValidator(int channelCount) {
            assert (channelCount == 1 || channelCount == 2);
            this.channelCount = channelCount;
        }

        @Override
        public void accept(@Nullable String s, @Nonnull ValidationResults results) {
            if (s == null) {
                return;
            }
            SoundEvent soundEvent = (SoundEvent)SoundEvent.getAssetMap().getAsset(s);
            if (soundEvent == null) {
                results.fail("Sound event with name '" + s + "' does not exist");
                return;
            }
            if (soundEvent.getHighestNumberOfChannels() != this.channelCount) {
                results.fail("Sound event with name '" + s + "' is " + SoundFileValidators.getEncoding(soundEvent.getHighestNumberOfChannels()) + " instead of " + SoundFileValidators.getEncoding(this.channelCount));
            }
        }

        @Override
        public void updateSchema(SchemaContext context, Schema target) {
        }
    }
}

