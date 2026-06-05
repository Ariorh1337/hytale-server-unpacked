package com.hypixel.hytale.server.core.asset.type.audiocategory.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.schema.metadata.ui.UIDisplayMode;
import com.hypixel.hytale.codec.validation.ValidationResults;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.protocol.AudioCategoryDuckingRule;
import com.hypixel.hytale.protocol.FadeCurve;
import com.hypixel.hytale.server.core.asset.type.audiostate.config.AudioStateCodecs;
import java.util.HashSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AudioCategoryDuckingRuleConfig {
   public static final float MIN_DUCKING_DB = -100.0F;
   public static final float MAX_DUCKING_DB = 0.0F;
   public static final float MAX_PHASE_MS = 60000.0F;
   @Nonnull
   public static final BuilderCodec<AudioCategoryDuckingRuleConfig> CODEC = BuilderCodec.builder(
         AudioCategoryDuckingRuleConfig.class, AudioCategoryDuckingRuleConfig::new
      )
      .metadata(UIDisplayMode.COMPACT)
      .<String>append(new KeyedCodec<>("TargetCategory", Codec.STRING), (r, s) -> r.targetCategoryId = s, r -> r.targetCategoryId)
      .addValidatorLate(() -> AudioCategory.VALIDATOR_CACHE.getValidator().late())
      .documentation("Id of the AudioCategory this rule ducks.")
      .add()
      .<Float>append(new KeyedCodec<>("DuckingVolumeDb", Codec.FLOAT), (r, f) -> r.duckingVolumeDb = f, r -> r.duckingVolumeDb)
      .addValidator(Validators.range(-100.0F, 0.0F))
      .documentation("Authored ducking target in dB.")
      .add()
      .<Float>append(new KeyedCodec<>("AttackMs", Codec.FLOAT), (r, f) -> r.attackMs = f, r -> r.attackMs)
      .addValidator(Validators.range(0.0F, 60000.0F))
      .documentation("Milliseconds to tween from 0 dB (no duck) toward DuckingVolumeDb when the rule's source first becomes active. Zero snaps immediately.")
      .add()
      .<Float>append(new KeyedCodec<>("HoldMs", Codec.FLOAT), (r, f) -> r.holdMs = f, r -> r.holdMs)
      .addValidator(Validators.range(0.0F, 60000.0F))
      .documentation("Milliseconds to hold the duck at DuckingVolumeDb after the source becomes inactive.")
      .add()
      .<Float>append(new KeyedCodec<>("ReleaseMs", Codec.FLOAT), (r, f) -> r.releaseMs = f, r -> r.releaseMs)
      .addValidator(Validators.range(0.0F, 60000.0F))
      .documentation("Milliseconds to tween from the held duck value back toward the next-priority value. Zero snaps immediately.")
      .add()
      .<FadeCurve>append(new KeyedCodec<>("Curve", AudioStateCodecs.FADE_CURVE), (r, c) -> r.curve = c, r -> r.curve)
      .documentation("Curve applied to the Attack tween.")
      .add()
      .<FadeCurve>append(new KeyedCodec<>("ReleaseCurve", AudioStateCodecs.FADE_CURVE), (r, c) -> r.releaseCurve = c, r -> r.releaseCurve)
      .documentation("Curve applied to the Release tween.")
      .add()
      .<Short>append(new KeyedCodec<>("Priority", Codec.SHORT), (r, s) -> r.priority = s, r -> r.priority)
      .addValidator(Validators.range((short)0, (short)32767))
      .documentation(
         "When multiple rules are active on the same target, only entries at the highest active priority contribute to the composite. Within a priority class, the most-negative dB wins."
      )
      .add()
      .build();
   @Nonnull
   public static final ArrayCodec<AudioCategoryDuckingRuleConfig> CODEC_ARRAY = new ArrayCodec<>(CODEC, AudioCategoryDuckingRuleConfig[]::new);
   @Nullable
   protected String targetCategoryId;
   protected float duckingVolumeDb;
   protected float attackMs;
   protected float holdMs;
   protected float releaseMs;
   protected FadeCurve curve = FadeCurve.Linear;
   protected FadeCurve releaseCurve = FadeCurve.Linear;
   protected short priority;
   transient int targetAudioCategoryIndex = Integer.MIN_VALUE;

   public static void validateUniqueTargets(
      @Nullable AudioCategoryDuckingRuleConfig[] rules, @Nonnull String ownerDescription, @Nonnull ValidationResults results
   ) {
      if (rules != null && rules.length != 0) {
         HashSet<String> seen = new HashSet<>();

         for (int i = 0; i < rules.length; i++) {
            AudioCategoryDuckingRuleConfig rule = rules[i];
            if (rule == null) {
               results.fail(ownerDescription + " DuckingRules[" + i + "] is null.");
            } else if (rule.targetCategoryId == null) {
               results.fail(ownerDescription + " DuckingRules[" + i + "] has no TargetCategory.");
            } else if (!seen.add(rule.targetCategoryId)) {
               results.fail(
                  ownerDescription
                     + " authors multiple DuckingRules targeting '"
                     + rule.targetCategoryId
                     + "'. Each source can have at most one rule per target."
               );
            }
         }
      }
   }

   public static void resolveTargets(@Nullable AudioCategoryDuckingRuleConfig[] rules) {
      if (rules != null) {
         for (AudioCategoryDuckingRuleConfig rule : rules) {
            if (rule != null && rule.targetCategoryId != null) {
               rule.targetAudioCategoryIndex = AudioCategory.getAssetMap().getIndex(rule.targetCategoryId);
            }
         }
      }
   }

   @Nonnull
   public AudioCategoryDuckingRule toPacket() {
      AudioCategoryDuckingRule packet = new AudioCategoryDuckingRule();
      packet.targetAudioCategoryIndex = this.targetAudioCategoryIndex;
      packet.duckingVolumeDb = this.duckingVolumeDb;
      packet.attackMs = this.attackMs;
      packet.holdMs = this.holdMs;
      packet.releaseMs = this.releaseMs;
      packet.curve = this.curve;
      packet.releaseCurve = this.releaseCurve;
      packet.priority = this.priority;
      return packet;
   }

   @Nullable
   public static AudioCategoryDuckingRule[] toPacketArray(@Nullable AudioCategoryDuckingRuleConfig[] rules) {
      if (rules != null && rules.length != 0) {
         AudioCategoryDuckingRule[] packets = new AudioCategoryDuckingRule[rules.length];

         for (int i = 0; i < rules.length; i++) {
            packets[i] = rules[i] != null ? rules[i].toPacket() : null;
         }

         return packets;
      } else {
         return null;
      }
   }
}
