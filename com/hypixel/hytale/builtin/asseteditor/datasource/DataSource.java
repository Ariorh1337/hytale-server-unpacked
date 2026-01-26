/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.asseteditor.datasource;

import com.hypixel.hytale.builtin.asseteditor.AssetTree;
import com.hypixel.hytale.builtin.asseteditor.EditorClient;
import com.hypixel.hytale.builtin.asseteditor.assettypehandler.AssetTypeHandler;
import com.hypixel.hytale.common.plugin.PluginManifest;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;

public interface DataSource {
    public void start();

    public void shutdown();

    public AssetTree getAssetTree();

    public AssetTree loadAssetTree(Collection<AssetTypeHandler> var1);

    public boolean doesDirectoryExist(Path var1);

    public boolean createDirectory(Path var1, EditorClient var2);

    public boolean deleteDirectory(Path var1);

    public boolean moveDirectory(Path var1, Path var2);

    public boolean doesAssetExist(Path var1);

    public byte[] getAssetBytes(Path var1);

    public boolean updateAsset(Path var1, byte[] var2, EditorClient var3);

    public boolean createAsset(Path var1, byte[] var2, EditorClient var3);

    public boolean deleteAsset(Path var1, EditorClient var2);

    public boolean moveAsset(Path var1, Path var2, EditorClient var3);

    public boolean shouldReloadAssetFromDisk(Path var1);

    public Instant getLastModificationTimestamp(Path var1);

    default public void updateRuntimeAssets() {
    }

    public Path getFullPathToAssetData(Path var1);

    public boolean isImmutable();

    public Path getRootPath();

    public PluginManifest getManifest();
}

