/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.memories;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelParticle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MemoriesGameplayConfig {
    public static final String ID = "Memories";
    public static final BuilderCodec<MemoriesGameplayConfig> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(MemoriesGameplayConfig.class, MemoriesGameplayConfig::new).appendInherited(new KeyedCodec<int[]>("MemoriesAmountPerLevel", Codec.INT_ARRAY), (config, value) -> {
        config.memoriesAmountPerLevel = value;
    }, config -> config.memoriesAmountPerLevel, (config, parent) -> {
        config.memoriesAmountPerLevel = parent.memoriesAmountPerLevel;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<String>("MemoriesRecordParticles", Codec.STRING), (config, value) -> {
        config.memoriesRecordParticles = value;
    }, config -> config.memoriesRecordParticles, (config, parent) -> {
        config.memoriesRecordParticles = parent.memoriesRecordParticles;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<String>("MemoriesCatchItemId", Codec.STRING), (memoriesGameplayConfig, s) -> {
        memoriesGameplayConfig.memoriesCatchItemId = s;
    }, memoriesGameplayConfig -> memoriesGameplayConfig.memoriesCatchItemId, (memoriesGameplayConfig, parent) -> {
        memoriesGameplayConfig.memoriesCatchItemId = parent.memoriesCatchItemId;
    }).addValidator(Validators.nonNull()).addValidator(Item.VALIDATOR_CACHE.getValidator()).add()).appendInherited(new KeyedCodec<ModelParticle>("MemoriesCatchEntityParticle", ModelParticle.CODEC), (activationEffects, s) -> {
        activationEffects.memoriesCatchEntityParticle = s;
    }, activationEffects -> activationEffects.memoriesCatchEntityParticle, (activationEffects, parent) -> {
        activationEffects.memoriesCatchEntityParticle = parent.memoriesCatchEntityParticle;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<Integer>("MemoriesCatchParticleViewDistance", Codec.INTEGER), (memoriesGameplayConfig, integer) -> {
        memoriesGameplayConfig.memoriesCatchParticleViewDistance = integer;
    }, memoriesGameplayConfig -> memoriesGameplayConfig.memoriesCatchParticleViewDistance, (memoriesGameplayConfig, parent) -> {
        memoriesGameplayConfig.memoriesCatchParticleViewDistance = parent.memoriesCatchParticleViewDistance;
    }).addValidator(Validators.greaterThan(16)).add()).build();
    private int[] memoriesAmountPerLevel;
    private String memoriesRecordParticles;
    private String memoriesCatchItemId;
    private ModelParticle memoriesCatchEntityParticle;
    private int memoriesCatchParticleViewDistance = 64;

    @Nullable
    public static MemoriesGameplayConfig get(@Nonnull GameplayConfig config) {
        return config.getPluginConfig().get(MemoriesGameplayConfig.class);
    }

    public int[] getMemoriesAmountPerLevel() {
        return this.memoriesAmountPerLevel;
    }

    public String getMemoriesRecordParticles() {
        return this.memoriesRecordParticles;
    }

    public String getMemoriesCatchItemId() {
        return this.memoriesCatchItemId;
    }

    public ModelParticle getMemoriesCatchEntityParticle() {
        return this.memoriesCatchEntityParticle;
    }

    public int getMemoriesCatchParticleViewDistance() {
        return this.memoriesCatchParticleViewDistance;
    }
}

