/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.asseteditor;

import com.hypixel.hytale.builtin.asseteditor.EditorClient;
import com.hypixel.hytale.builtin.asseteditor.assettypehandler.AssetTypeHandler;
import com.hypixel.hytale.builtin.asseteditor.data.AssetState;
import com.hypixel.hytale.builtin.asseteditor.data.ModifiedAsset;
import com.hypixel.hytale.builtin.asseteditor.util.AssetPathUtil;
import com.hypixel.hytale.common.util.FormatUtil;
import com.hypixel.hytale.common.util.ListUtil;
import com.hypixel.hytale.common.util.PathUtil;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorAssetListSetup;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorFileEntry;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorFileTree;
import com.hypixel.hytale.server.core.asset.common.CommonAssetModule;
import com.hypixel.hytale.server.core.util.io.FileUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AssetTree {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private final StampedLock lock = new StampedLock();
    private final Path rootPath;
    private final String packKey;
    private final boolean isReadOnly;
    private final boolean canBeDeleted;
    List<AssetEditorFileEntry> serverAssets = new ObjectArrayList<AssetEditorFileEntry>();
    List<AssetEditorFileEntry> commonAssets = new ObjectArrayList<AssetEditorFileEntry>();

    public AssetTree(Path rootPath, String packKey, boolean isReadOnly, boolean canBeDeleted) {
        this.rootPath = rootPath;
        this.packKey = packKey;
        this.isReadOnly = isReadOnly;
        this.canBeDeleted = canBeDeleted;
    }

    public AssetTree(Path rootPath, String packKey, boolean isReadOnly, boolean canBeDeleted, @Nonnull Collection<AssetTypeHandler> assetTypes) {
        this.rootPath = rootPath;
        this.packKey = packKey;
        this.isReadOnly = isReadOnly;
        this.canBeDeleted = canBeDeleted;
        this.load(assetTypes);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void replaceAssetTree(@Nonnull AssetTree assetTree) {
        long stamp = this.lock.writeLock();
        try {
            this.serverAssets = assetTree.serverAssets;
            this.commonAssets = assetTree.commonAssets;
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sendPackets(@Nonnull EditorClient editorClient) {
        long stamp = this.lock.readLock();
        try {
            editorClient.getPacketHandler().write((Packet)new AssetEditorAssetListSetup(this.packKey, this.isReadOnly, this.canBeDeleted, AssetEditorFileTree.Server, (AssetEditorFileEntry[])this.serverAssets.toArray(AssetEditorFileEntry[]::new)));
            editorClient.getPacketHandler().write((Packet)new AssetEditorAssetListSetup(this.packKey, this.isReadOnly, this.canBeDeleted, AssetEditorFileTree.Common, (AssetEditorFileEntry[])this.commonAssets.toArray(AssetEditorFileEntry[]::new)));
        }
        finally {
            this.lock.unlockRead(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isDirectoryEmpty(@Nonnull Path path) {
        String pathString = PathUtil.toUnixPathString(path);
        long stamp = this.lock.readLock();
        try {
            List<AssetEditorFileEntry> assets = this.getAssetListForPath(path);
            int index = ListUtil.binarySearch(assets, o -> o.path, pathString, String::compareTo);
            if (index < 0) {
                boolean bl = true;
                return bl;
            }
            if (!assets.get((int)index).isDirectory) {
                boolean bl = false;
                return bl;
            }
            int fileIndex = index + 1;
            if (fileIndex >= assets.size()) {
                boolean bl = false;
                return bl;
            }
            boolean hasFile = assets.get((int)fileIndex).path.startsWith(pathString + "/");
            boolean bl = !hasFile;
            return bl;
        }
        finally {
            this.lock.unlockRead(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public AssetEditorFileEntry ensureAsset(@Nonnull Path path, boolean isDirectory) {
        String pathString = PathUtil.toUnixPathString(path);
        long stamp = this.lock.writeLock();
        try {
            List<AssetEditorFileEntry> assets = this.getAssetListForPath(path);
            int index = ListUtil.binarySearch(assets, o -> o.path, pathString, String::compareTo);
            if (index >= 0) {
                AssetEditorFileEntry assetEditorFileEntry = null;
                return assetEditorFileEntry;
            }
            int insertionPoint = -(index + 1);
            if (path.getNameCount() > 1) {
                Path parentPath = path.getName(0);
                for (int i = 1; i < path.getNameCount() - 1; ++i) {
                    parentPath = parentPath.resolve(path.getName(i));
                    String name = PathUtil.toUnixPathString(parentPath);
                    if (insertionPoint > 0 && assets.get((int)(insertionPoint - 1)).path.startsWith(name)) continue;
                    assets.add(insertionPoint++, new AssetEditorFileEntry(name, true));
                }
            }
            AssetEditorFileEntry entry = new AssetEditorFileEntry(pathString, isDirectory);
            assets.add(insertionPoint, entry);
            AssetEditorFileEntry assetEditorFileEntry = entry;
            return assetEditorFileEntry;
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public AssetEditorFileEntry getAssetFile(@Nonnull Path path) {
        String pathString = PathUtil.toUnixPathString(path);
        long stamp = this.lock.readLock();
        try {
            List<AssetEditorFileEntry> assets = this.getAssetListForPath(path);
            int index = ListUtil.binarySearch(assets, o -> o.path, pathString, String::compareTo);
            AssetEditorFileEntry assetEditorFileEntry = index >= 0 ? assets.get(index) : null;
            return assetEditorFileEntry;
        }
        finally {
            this.lock.unlockRead(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public AssetEditorFileEntry removeAsset(@Nonnull Path path) {
        String pathString = PathUtil.toUnixPathString(path);
        long stamp = this.lock.writeLock();
        try {
            List<AssetEditorFileEntry> assets = this.getAssetListForPath(path);
            int index = ListUtil.binarySearch(assets, o -> o.path, pathString, String::compareTo);
            if (index < 0) {
                AssetEditorFileEntry assetEditorFileEntry = null;
                return assetEditorFileEntry;
            }
            AssetEditorFileEntry entry = assets.remove(index);
            if (entry.isDirectory) {
                int i;
                String pathPrefix = pathString + "/";
                int removeCount = 0;
                for (i = index; i < assets.size(); ++i) {
                    AssetEditorFileEntry asset = assets.get(i);
                    if (!asset.path.startsWith(pathPrefix)) break;
                    ++removeCount;
                }
                for (i = 0; i < removeCount; ++i) {
                    assets.remove(index);
                }
            }
            AssetEditorFileEntry assetEditorFileEntry = entry;
            return assetEditorFileEntry;
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
    }

    public void applyAssetChanges(@Nonnull Map<Path, ModifiedAsset> createdDirectories, @Nonnull Map<Path, ModifiedAsset> modifiedAssets) {
        for (ModifiedAsset dir : createdDirectories.values()) {
            this.ensureAsset(dir.path, true);
        }
        for (ModifiedAsset file : modifiedAssets.values()) {
            if (file.state == AssetState.NEW) {
                this.ensureAsset(file.path, false);
                continue;
            }
            if (file.state == AssetState.DELETED) {
                this.removeAsset(file.oldPath != null ? file.oldPath : file.path);
                continue;
            }
            if (file.oldPath == null) continue;
            this.removeAsset(file.oldPath);
            this.ensureAsset(file.path, false);
        }
    }

    private List<AssetEditorFileEntry> getAssetListForPath(@Nonnull Path path) {
        if (path.getNameCount() > 0) {
            String firstName = path.getName(0).toString();
            if (firstName.equals("..")) {
                try {
                    firstName = path.getName(2).toString();
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    // empty catch block
                }
            }
            if ("Server".equals(firstName)) {
                return this.serverAssets;
            }
            if ("Common".equals(firstName)) {
                return this.commonAssets;
            }
        }
        throw new IllegalArgumentException("Invalid path " + String.valueOf(path));
    }

    private void load(@Nonnull Collection<AssetTypeHandler> assetTypes) {
        long start;
        try {
            start = System.nanoTime();
            AssetTree.loadServerAssets(this.rootPath, assetTypes, this.serverAssets);
            LOGGER.at(Level.INFO).log("Loaded Server/ asset tree! Took: %s", FormatUtil.nanosToString(System.nanoTime() - start));
        }
        catch (IOException e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to load server asset tree!");
        }
        try {
            start = System.nanoTime();
            AssetTree.walkFileTree(this.rootPath, this.rootPath.resolve("Common"), this.commonAssets);
            LOGGER.at(Level.INFO).log("Loaded Common/ asset tree! Took: %s", FormatUtil.nanosToString(System.nanoTime() - start));
        }
        catch (IOException e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to load common asset tree!");
        }
        long start2 = System.nanoTime();
        this.serverAssets.sort(Comparator.comparing(o -> o.path));
        this.commonAssets.sort(Comparator.comparing(o -> o.path));
        LOGGER.at(Level.INFO).log("Sorted asset tree! Took: %s", FormatUtil.nanosToString(System.nanoTime() - start2));
    }

    private static void loadServerAssets(@Nonnull Path root, @Nonnull Collection<AssetTypeHandler> assetTypes, @Nonnull List<AssetEditorFileEntry> files) throws IOException {
        HashSet<String> assetTypePaths = new HashSet<String>();
        HashSet<String> subPaths = new HashSet<String>();
        for (AssetTypeHandler assetTypeHandler : assetTypes) {
            String assetTypePath;
            if (!assetTypeHandler.getRootPath().startsWith(AssetPathUtil.PATH_DIR_SERVER) || !assetTypePaths.add(assetTypePath = assetTypeHandler.getConfig().path)) continue;
            Path path = Path.of(assetTypePath, new String[0]);
            Path subpath = AssetPathUtil.PATH_DIR_SERVER;
            for (int i = 1; i < path.getNameCount() - 1; ++i) {
                String name = PathUtil.toUnixPathString(subpath = subpath.resolve(path.getName(i)));
                if (!subPaths.add(name)) continue;
                files.add(new AssetEditorFileEntry(name, true));
            }
        }
        for (String path : assetTypePaths) {
            Path dirPath = root.resolve(path);
            AssetTree.walkFileTree(root, dirPath, files);
            String name = PathUtil.toUnixPathString(root.relativize(dirPath));
            files.add(new AssetEditorFileEntry(name, true));
        }
    }

    private static void walkFileTree(final @Nonnull Path root, final @Nonnull Path dirPath, final @Nonnull List<AssetEditorFileEntry> files) throws IOException {
        if (!Files.isDirectory(dirPath, new LinkOption[0])) {
            return;
        }
        Files.walkFileTree(dirPath, FileUtil.DEFAULT_WALK_TREE_OPTIONS_SET, Integer.MAX_VALUE, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

            @Override
            public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) throws IOException {
                if (path.equals(dirPath)) {
                    return FileVisitResult.CONTINUE;
                }
                files.add(new AssetEditorFileEntry(PathUtil.toUnixPathString(root.relativize(path)), true));
                return super.preVisitDirectory(path, attrs);
            }

            @Override
            @Nonnull
            public FileVisitResult visitFile(@Nonnull Path path, @Nonnull BasicFileAttributes attrs) {
                if (CommonAssetModule.IGNORED_FILES.contains(path.getFileName())) {
                    return FileVisitResult.CONTINUE;
                }
                files.add(new AssetEditorFileEntry(PathUtil.toUnixPathString(root.relativize(path)), false));
                return FileVisitResult.CONTINUE;
            }
        });
    }
}

