/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.prefab;

import com.hypixel.hytale.server.core.prefab.PrefabStore;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public enum PrefabStoreRoot {
    ASSETS,
    WORLD_GEN;

    public static final PrefabStoreRoot DEFAULT;

    @Nonnull
    public static Path resolvePrefabStore(@Nonnull PrefabStoreRoot store, @Nonnull Path dataFolder) {
        return switch (store.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> PrefabStore.get().getAssetPrefabsPath();
            case 1 -> dataFolder.resolve("Prefabs");
        };
    }

    static {
        DEFAULT = WORLD_GEN;
    }
}

