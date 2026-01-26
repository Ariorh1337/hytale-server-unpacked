/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.asseteditor.assettypehandler;

import com.hypixel.hytale.assetstore.AssetUpdateQuery;
import com.hypixel.hytale.builtin.asseteditor.AssetPath;
import com.hypixel.hytale.builtin.asseteditor.EditorClient;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorAssetType;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public abstract class AssetTypeHandler {
    @Nonnull
    protected final AssetEditorAssetType config;
    @Nonnull
    protected final Path rootPath;
    protected AssetUpdateQuery cachedDefaultUpdateQuery;

    protected AssetTypeHandler(@Nonnull AssetEditorAssetType config) {
        this.config = config;
        this.rootPath = Path.of(config.path, new String[0]);
    }

    public abstract AssetLoadResult loadAsset(AssetPath var1, Path var2, byte[] var3, AssetUpdateQuery var4, EditorClient var5);

    public abstract AssetLoadResult unloadAsset(AssetPath var1, AssetUpdateQuery var2);

    public abstract AssetLoadResult restoreOriginalAsset(AssetPath var1, AssetUpdateQuery var2);

    public abstract AssetUpdateQuery getDefaultUpdateQuery();

    public AssetLoadResult loadAsset(AssetPath path, Path dataPath, byte[] data, EditorClient editorClient) {
        return this.loadAsset(path, dataPath, data, this.getDefaultUpdateQuery(), editorClient);
    }

    public AssetLoadResult unloadAsset(AssetPath path) {
        return this.unloadAsset(path, this.getDefaultUpdateQuery());
    }

    public AssetLoadResult restoreOriginalAsset(AssetPath originalAssetPath) {
        return this.restoreOriginalAsset(originalAssetPath, this.getDefaultUpdateQuery());
    }

    public boolean isValidData(byte[] data) {
        return true;
    }

    @Nonnull
    public AssetEditorAssetType getConfig() {
        return this.config;
    }

    @Nonnull
    public Path getRootPath() {
        return this.rootPath;
    }

    public static enum AssetLoadResult {
        ASSETS_UNCHANGED,
        ASSETS_CHANGED,
        COMMON_ASSETS_CHANGED;

    }
}

