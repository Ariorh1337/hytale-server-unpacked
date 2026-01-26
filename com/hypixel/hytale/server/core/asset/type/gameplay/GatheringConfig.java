/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.gameplay;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.asset.type.gameplay.GatheringEffectsConfig;
import javax.annotation.Nonnull;

public class GatheringConfig {
    @Nonnull
    public static final BuilderCodec<GatheringConfig> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(GatheringConfig.class, GatheringConfig::new).appendInherited(new KeyedCodec<GatheringEffectsConfig>("UnbreakableBlock", GatheringEffectsConfig.CODEC), (gameplayConfig, o) -> {
        gameplayConfig.unbreakableBlockConfig = o;
    }, gameplayConfig -> gameplayConfig.unbreakableBlockConfig, (gameplayConfig, parent) -> {
        gameplayConfig.unbreakableBlockConfig = parent.unbreakableBlockConfig;
    }).add()).appendInherited(new KeyedCodec<GatheringEffectsConfig>("IncorrectTool", GatheringEffectsConfig.CODEC), (gameplayConfig, o) -> {
        gameplayConfig.incorrectToolConfig = o;
    }, gameplayConfig -> gameplayConfig.incorrectToolConfig, (gameplayConfig, parent) -> {
        gameplayConfig.incorrectToolConfig = parent.incorrectToolConfig;
    }).add()).build();
    @Nonnull
    protected GatheringEffectsConfig unbreakableBlockConfig = new GatheringEffectsConfig();
    @Nonnull
    protected GatheringEffectsConfig incorrectToolConfig = new GatheringEffectsConfig();

    @Nonnull
    public GatheringEffectsConfig getUnbreakableBlockConfig() {
        return this.unbreakableBlockConfig;
    }

    @Nonnull
    public GatheringEffectsConfig getIncorrectToolConfig() {
        return this.incorrectToolConfig;
    }
}

