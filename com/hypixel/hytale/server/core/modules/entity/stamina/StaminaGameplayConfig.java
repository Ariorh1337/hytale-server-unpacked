/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.entity.stamina;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import javax.annotation.Nonnull;

public class StaminaGameplayConfig {
    public static final String ID = "Stamina";
    public static final BuilderCodec<StaminaGameplayConfig> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(StaminaGameplayConfig.class, StaminaGameplayConfig::new).appendInherited(new KeyedCodec<SprintRegenDelayConfig>("SprintRegenDelay", SprintRegenDelayConfig.CODEC), (staminaGameplayConfig, s) -> {
        staminaGameplayConfig.sprintRegenDelay = s;
    }, staminaGameplayConfig -> staminaGameplayConfig.sprintRegenDelay, (staminaGameplayConfig, parent) -> {
        staminaGameplayConfig.sprintRegenDelay = parent.sprintRegenDelay;
    }).addValidator(Validators.nonNull()).documentation("The stamina regeneration delay applied after sprinting").add()).build();
    protected SprintRegenDelayConfig sprintRegenDelay;

    @Nonnull
    public SprintRegenDelayConfig getSprintRegenDelay() {
        return this.sprintRegenDelay;
    }

    @Nonnull
    public String toString() {
        return "StaminaGameplayConfig{sprintRegenDelay=" + String.valueOf(this.sprintRegenDelay) + "}";
    }

    public static class SprintRegenDelayConfig {
        public static final BuilderCodec<SprintRegenDelayConfig> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(SprintRegenDelayConfig.class, SprintRegenDelayConfig::new).appendInherited(new KeyedCodec<String>("EntityStatId", Codec.STRING), (entityStatConfig, s) -> {
            entityStatConfig.statId = s;
        }, entityStatConfig -> entityStatConfig.statId, (entityStatConfig, parent) -> {
            entityStatConfig.statId = parent.statId;
        }).addValidator(Validators.nonNull()).addValidator(EntityStatType.VALIDATOR_CACHE.getValidator()).documentation("The ID of the stamina regen delay EntityStat").add()).appendInherited(new KeyedCodec<Float>("Value", Codec.FLOAT), (entityStatConfig, s) -> {
            entityStatConfig.statValue = s.floatValue();
        }, entityStatConfig -> Float.valueOf(entityStatConfig.statValue), (entityStatConfig, parent) -> {
            entityStatConfig.statValue = parent.statValue;
        }).addValidator(Validators.max(Float.valueOf(0.0f))).documentation("The amount of stamina regen delay to apply").add()).afterDecode(entityStatConfig -> {
            entityStatConfig.statIndex = EntityStatType.getAssetMap().getIndex(entityStatConfig.statId);
        })).build();
        protected String statId;
        protected int statIndex;
        protected float statValue;

        public int getIndex() {
            return this.statIndex;
        }

        public float getValue() {
            return this.statValue;
        }

        @Nonnull
        public String toString() {
            return "SprintRegenDelayConfig{statId='" + this.statId + "', statIndex=" + this.statIndex + ", statValue=" + this.statValue + "}";
        }
    }
}

