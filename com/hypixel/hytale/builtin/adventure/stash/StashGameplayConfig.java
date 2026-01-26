/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.stash;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StashGameplayConfig {
    public static final String ID = "Stash";
    public static final BuilderCodec<StashGameplayConfig> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(StashGameplayConfig.class, StashGameplayConfig::new).appendInherited(new KeyedCodec<Boolean>("ClearContainerDropList", Codec.BOOLEAN), (gameplayConfig, clearContainerDropList) -> {
        gameplayConfig.clearContainerDropList = clearContainerDropList;
    }, gameplayConfig -> gameplayConfig.clearContainerDropList, (gameplayConfig, parent) -> {
        gameplayConfig.clearContainerDropList = parent.clearContainerDropList;
    }).add()).build();
    private static final StashGameplayConfig DEFAULT_STASH_GAMEPLAY_CONFIG = new StashGameplayConfig();
    protected boolean clearContainerDropList = true;

    @Nullable
    public static StashGameplayConfig get(@Nonnull GameplayConfig config) {
        return config.getPluginConfig().get(StashGameplayConfig.class);
    }

    public static StashGameplayConfig getOrDefault(@Nonnull GameplayConfig config) {
        StashGameplayConfig stashGameplayConfig = StashGameplayConfig.get(config);
        return stashGameplayConfig != null ? stashGameplayConfig : DEFAULT_STASH_GAMEPLAY_CONFIG;
    }

    public boolean isClearContainerDropList() {
        return this.clearContainerDropList;
    }

    @Nonnull
    public String toString() {
        return "StashGameplayConfig{clearContainerDropList=" + this.clearContainerDropList + "}";
    }
}

