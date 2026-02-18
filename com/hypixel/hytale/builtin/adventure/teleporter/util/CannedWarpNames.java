/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.teleporter.util;

import com.hypixel.hytale.builtin.adventure.teleporter.component.Teleporter;
import com.hypixel.hytale.builtin.teleport.TeleportPlugin;
import com.hypixel.hytale.builtin.teleport.Warp;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.asset.type.wordlist.WordList;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.i18n.I18nModule;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class CannedWarpNames {
    private CannedWarpNames() {
    }

    @Nullable
    public static String generateCannedWarpName(@Nonnull Ref<ChunkStore> blockRef, @Nonnull String language) {
        String translationKey = CannedWarpNames.generateCannedWarpNameKey(blockRef, language);
        if (translationKey == null) {
            return null;
        }
        return I18nModule.get().getMessage(language, translationKey);
    }

    @Nullable
    public static String generateCannedWarpNameKey(@Nonnull Ref<ChunkStore> blockRef, @Nonnull String language) {
        String ownName;
        Store<ChunkStore> store = blockRef.getStore();
        World world = store.getExternalData().getWorld();
        BlockModule.BlockStateInfo blockStateInfo = store.getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());
        Random random = blockStateInfo == null ? new Random() : new Random(blockStateInfo.getIndex());
        Teleporter teleporterComponent = store.getComponent(blockRef, Teleporter.getComponentType());
        String wordListKey = teleporterComponent == null ? null : teleporterComponent.getWarpNameWordListKey();
        Set<String> existingNames = CannedWarpNames.getWarpNamesInWorld(world);
        if (teleporterComponent != null && (ownName = teleporterComponent.getOwnedWarp()) != null && !teleporterComponent.isCustomName()) {
            existingNames.remove(ownName);
        }
        return WordList.getWordList(wordListKey).pickTranslationKey(random, existingNames, language);
    }

    @Nonnull
    private static Set<String> getWarpNamesInWorld(@Nonnull World world) {
        HashSet<String> existingNames = new HashSet<String>();
        for (Warp warp : TeleportPlugin.get().getWarps().values()) {
            if (!warp.getWorld().equals(world.getName())) continue;
            existingNames.add(warp.getId());
        }
        return existingNames;
    }
}

