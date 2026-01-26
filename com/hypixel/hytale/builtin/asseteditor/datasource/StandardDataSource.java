/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.asseteditor.datasource;

import com.hypixel.hytale.builtin.asseteditor.AssetTree;
import com.hypixel.hytale.builtin.asseteditor.EditorClient;
import com.hypixel.hytale.builtin.asseteditor.assettypehandler.AssetTypeHandler;
import com.hypixel.hytale.builtin.asseteditor.data.AssetState;
import com.hypixel.hytale.builtin.asseteditor.data.ModifiedAsset;
import com.hypixel.hytale.builtin.asseteditor.datasource.DataSource;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.common.plugin.PluginManifest;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Options;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.hypixel.hytale.server.core.util.BsonUtil;
import com.hypixel.hytale.server.core.util.HashUtil;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonValue;

public class StandardDataSource
implements DataSource {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private final Path rootPath;
    private final ConcurrentHashMap<Path, Deque<EditorFileSaveInfo>> editorSaves;
    private final AssetTree assetTree;
    private final String packKey;
    private final PluginManifest manifest;
    private final boolean isImmutable;
    private final Path recentModificationsFilePath;
    private final AtomicBoolean indexNeedsSaving = new AtomicBoolean();
    private final Map<Path, ModifiedAsset> modifiedAssets = new ConcurrentHashMap<Path, ModifiedAsset>();
    private ScheduledFuture<?> saveSchedule;
    private boolean isAssetPackBeDeleteable;

    public StandardDataSource(String packKey, Path rootPath, boolean isImmutable, PluginManifest manifest) {
        this.rootPath = rootPath;
        this.editorSaves = new ConcurrentHashMap();
        this.packKey = packKey;
        this.isImmutable = isImmutable;
        this.manifest = manifest;
        this.isAssetPackBeDeleteable = !isImmutable && StandardDataSource.isInModsDirectory(rootPath);
        this.assetTree = new AssetTree(rootPath, packKey, isImmutable, this.isAssetPackBeDeleteable);
        this.recentModificationsFilePath = Path.of("assetEditor", "recentAssetEdits_" + packKey.replace(':', '-') + ".json");
    }

    private static boolean isInModsDirectory(Path path) {
        if (path.startsWith(PluginManager.MODS_PATH)) {
            return true;
        }
        for (Path modsPath : Options.getOptionSet().valuesOf(Options.MODS_DIRECTORIES)) {
            if (!path.startsWith(modsPath)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        this.loadRecentModifications();
        this.saveSchedule = HytaleServer.SCHEDULED_EXECUTOR.scheduleWithFixedDelay(() -> {
            try {
                this.saveRecentModifications();
            }
            catch (Exception e) {
                ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(e)).log("Failed to save assets index");
            }
        }, 1L, 1L, TimeUnit.MINUTES);
    }

    @Override
    public void shutdown() {
        this.saveSchedule.cancel(false);
        this.saveRecentModifications();
    }

    private void loadRecentModifications() {
        Path path = this.recentModificationsFilePath;
        if (!Files.exists(path, new LinkOption[0]) && !Files.exists(path = path.resolveSibling(String.valueOf(path.getFileName()) + ".bak"), new LinkOption[0])) {
            return;
        }
        BsonDocument doc = BsonUtil.readDocument(path).join();
        BsonArray assets = doc.getArray("Assets");
        for (BsonValue asset : assets) {
            ModifiedAsset modifiedAsset = ModifiedAsset.CODEC.decode(asset, new ExtraInfo());
            if (modifiedAsset == null) continue;
            this.modifiedAssets.put(modifiedAsset.path, modifiedAsset);
        }
    }

    public void saveRecentModifications() {
        if (!this.indexNeedsSaving.getAndSet(false)) {
            return;
        }
        LOGGER.at(Level.INFO).log("Saving recent asset modification index...");
        BsonDocument doc = new BsonDocument();
        BsonArray assetsArray = new BsonArray();
        for (Map.Entry<Path, ModifiedAsset> modifiedAsset : this.modifiedAssets.entrySet()) {
            assetsArray.add(ModifiedAsset.CODEC.encode((Object)modifiedAsset.getValue(), new ExtraInfo()));
        }
        doc.append("Assets", assetsArray);
        try {
            BsonUtil.writeDocument(this.recentModificationsFilePath, doc);
        }
        catch (Exception ex) {
            ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(ex)).log("Failed to save recent asset modification index...");
            this.indexNeedsSaving.set(true);
        }
    }

    public boolean canAssetPackBeDeleted() {
        return this.isAssetPackBeDeleteable;
    }

    public Path resolveAbsolutePath(Path path) {
        return this.rootPath.resolve(path.toString()).toAbsolutePath();
    }

    @Override
    public Path getFullPathToAssetData(Path assetPath) {
        return this.resolveAbsolutePath(assetPath);
    }

    @Override
    public AssetTree getAssetTree() {
        return this.assetTree;
    }

    @Override
    public boolean isImmutable() {
        return this.isImmutable;
    }

    @Override
    public Path getRootPath() {
        return this.rootPath;
    }

    @Override
    public PluginManifest getManifest() {
        return this.manifest;
    }

    @Override
    public boolean doesDirectoryExist(Path folderPath) {
        return Files.isDirectory(this.resolveAbsolutePath(folderPath), new LinkOption[0]);
    }

    @Override
    public boolean createDirectory(Path dirPath, EditorClient editorClient) {
        try {
            Files.createDirectory(this.resolveAbsolutePath(dirPath), new FileAttribute[0]);
        }
        catch (IOException e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to create directory %s", dirPath);
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteDirectory(Path dirPath) {
        try {
            Files.deleteIfExists(this.resolveAbsolutePath(dirPath));
        }
        catch (IOException e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to delete directory %s", dirPath);
            return false;
        }
        return true;
    }

    @Override
    public boolean moveDirectory(Path oldDirPath, Path newDirPath) {
        try {
            Files.move(this.resolveAbsolutePath(oldDirPath), this.resolveAbsolutePath(newDirPath), new CopyOption[0]);
        }
        catch (IOException e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to move directory %s to %s", (Object)oldDirPath, (Object)newDirPath);
            return false;
        }
        return true;
    }

    @Override
    public boolean doesAssetExist(Path assetPath) {
        return Files.isRegularFile(this.resolveAbsolutePath(assetPath), new LinkOption[0]);
    }

    @Override
    public byte[] getAssetBytes(Path assetPath) {
        try {
            return Files.readAllBytes(this.resolveAbsolutePath(assetPath));
        }
        catch (IOException e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to read asset %s", assetPath);
            return null;
        }
    }

    @Override
    public boolean updateAsset(Path assetPath, byte[] bytes, EditorClient editorClient) {
        Path path = this.resolveAbsolutePath(assetPath);
        try {
            String hash = HashUtil.sha256(bytes);
            this.trackEditorFileSave(assetPath, hash);
            Files.write(path, bytes, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            ModifiedAsset modifiedAsset = new ModifiedAsset();
            modifiedAsset.path = assetPath;
            modifiedAsset.state = AssetState.CHANGED;
            modifiedAsset.markEditedBy(editorClient);
            this.putModifiedAsset(modifiedAsset);
        }
        catch (IOException e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to update asset %s", assetPath);
            return false;
        }
        return true;
    }

    @Override
    public boolean createAsset(Path assetPath, byte[] bytes, EditorClient editorClient) {
        Path path = this.resolveAbsolutePath(assetPath);
        try {
            String hash = HashUtil.sha256(bytes);
            this.trackEditorFileSave(assetPath, hash);
            Files.createDirectories(path.getParent(), new FileAttribute[0]);
            Files.write(path, bytes, StandardOpenOption.CREATE);
            ModifiedAsset modifiedAsset = new ModifiedAsset();
            modifiedAsset.path = assetPath;
            modifiedAsset.state = AssetState.NEW;
            modifiedAsset.markEditedBy(editorClient);
            this.putModifiedAsset(modifiedAsset);
        }
        catch (IOException e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to create asset %s", assetPath);
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteAsset(Path assetPath, EditorClient editorClient) {
        try {
            Files.deleteIfExists(this.resolveAbsolutePath(assetPath));
            ModifiedAsset modifiedAsset = new ModifiedAsset();
            modifiedAsset.path = assetPath;
            modifiedAsset.state = AssetState.DELETED;
            modifiedAsset.markEditedBy(editorClient);
            this.putModifiedAsset(modifiedAsset);
        }
        catch (IOException e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to delete asset %s", assetPath);
            return false;
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean shouldReloadAssetFromDisk(Path assetPath) {
        Deque<EditorFileSaveInfo> fileSaveInfos = this.editorSaves.get(assetPath);
        if (fileSaveInfos == null || fileSaveInfos.isEmpty()) {
            return true;
        }
        byte[] bytes = this.getAssetBytes(assetPath);
        if (bytes == null) {
            return true;
        }
        String hash = HashUtil.sha256(bytes);
        long now = System.currentTimeMillis();
        Deque<EditorFileSaveInfo> deque = fileSaveInfos;
        synchronized (deque) {
            fileSaveInfos.removeIf(m -> m.expiryMs <= now);
            for (EditorFileSaveInfo m2 : fileSaveInfos) {
                if (!m2.hash.equals(hash)) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    public Instant getLastModificationTimestamp(Path assetPath) {
        return null;
    }

    @Override
    public boolean moveAsset(Path oldAssetPath, Path newAssetPath, EditorClient editorClient) {
        try {
            Files.move(this.resolveAbsolutePath(oldAssetPath), this.resolveAbsolutePath(newAssetPath), new CopyOption[0]);
            ModifiedAsset modifiedAsset = new ModifiedAsset();
            modifiedAsset.path = newAssetPath;
            modifiedAsset.oldPath = oldAssetPath;
            modifiedAsset.state = AssetState.CHANGED;
            modifiedAsset.markEditedBy(editorClient);
            this.putModifiedAsset(modifiedAsset);
        }
        catch (IOException e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to move asset %s to %s", (Object)oldAssetPath, (Object)newAssetPath);
            return false;
        }
        return true;
    }

    @Override
    public AssetTree loadAssetTree(Collection<AssetTypeHandler> assetTypes) {
        return new AssetTree(this.rootPath, this.packKey, this.isImmutable, this.isAssetPackBeDeleteable, assetTypes);
    }

    public void putModifiedAsset(ModifiedAsset modifiedAsset) {
        this.modifiedAssets.put(modifiedAsset.path, modifiedAsset);
        if (this.modifiedAssets.size() > 50) {
            ModifiedAsset oldestAsset = null;
            for (ModifiedAsset asset : this.modifiedAssets.values()) {
                if (oldestAsset == null) {
                    oldestAsset = asset;
                    continue;
                }
                if (!asset.lastModificationTimestamp.isBefore(oldestAsset.lastModificationTimestamp)) continue;
                oldestAsset = asset;
            }
            this.modifiedAssets.remove(oldestAsset.path);
        }
        this.indexNeedsSaving.set(true);
    }

    public Map<Path, ModifiedAsset> getRecentlyModifiedAssets() {
        return this.modifiedAssets;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void trackEditorFileSave(Path path, String hash) {
        Deque fileSaves;
        Deque deque = fileSaves = this.editorSaves.computeIfAbsent(path, p -> new ArrayDeque());
        synchronized (deque) {
            fileSaves.addLast(new EditorFileSaveInfo(hash, System.currentTimeMillis() + 30000L));
            while (fileSaves.size() > 20) {
                fileSaves.removeFirst();
            }
        }
    }

    record EditorFileSaveInfo(String hash, long expiryMs) {
    }
}

