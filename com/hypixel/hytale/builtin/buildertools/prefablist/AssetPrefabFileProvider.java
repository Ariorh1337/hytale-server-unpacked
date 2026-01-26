/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.prefablist;

import com.hypixel.hytale.common.util.StringCompareUtil;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.ui.browser.FileListProvider;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AssetPrefabFileProvider
implements FileListProvider {
    private static final String PREFAB_EXTENSION = ".prefab.json";
    private static final int MAX_SEARCH_RESULTS = 50;
    private static final String BASE_ASSET_PACK_DISPLAY_NAME = "HytaleAssets";

    @Override
    @Nonnull
    public List<FileListProvider.FileEntry> getFiles(@Nonnull Path currentDir, @Nonnull String searchQuery) {
        String currentDirStr = currentDir.toString().replace('\\', '/');
        if (!searchQuery.isEmpty()) {
            return this.buildSearchResults(currentDirStr, searchQuery);
        }
        if (currentDirStr.isEmpty()) {
            return this.buildPackListings();
        }
        return this.buildPackDirectoryListing(currentDirStr);
    }

    @Nonnull
    private List<FileListProvider.FileEntry> buildPackListings() {
        ObjectArrayList<FileListProvider.FileEntry> entries = new ObjectArrayList<FileListProvider.FileEntry>();
        for (PrefabStore.AssetPackPrefabPath packPath : PrefabStore.get().getAllAssetPrefabPaths()) {
            String displayName = packPath.getDisplayName();
            String packKey = this.getPackKey(packPath);
            entries.add(new FileListProvider.FileEntry(packKey, displayName, true));
        }
        entries.sort((a, b) -> {
            boolean bIsBase;
            boolean aIsBase = BASE_ASSET_PACK_DISPLAY_NAME.equals(a.displayName());
            if (aIsBase != (bIsBase = BASE_ASSET_PACK_DISPLAY_NAME.equals(b.displayName()))) {
                return aIsBase ? -1 : 1;
            }
            return a.displayName().compareToIgnoreCase(b.displayName());
        });
        return entries;
    }

    @Nonnull
    private List<FileListProvider.FileEntry> buildPackDirectoryListing(@Nonnull String currentDirStr) {
        ObjectArrayList<FileListProvider.FileEntry> entries = new ObjectArrayList<FileListProvider.FileEntry>();
        String[] parts = currentDirStr.split("/", 2);
        String packKey = parts[0];
        String subPath = parts.length > 1 ? parts[1] : "";
        PrefabStore.AssetPackPrefabPath packPath = this.findPackByKey(packKey);
        if (packPath == null) {
            return entries;
        }
        Path targetPath = packPath.prefabsPath();
        if (!subPath.isEmpty()) {
            targetPath = targetPath.resolve(subPath);
        }
        if (!Files.isDirectory(targetPath, new LinkOption[0])) {
            return entries;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(targetPath);){
            for (Path file : stream) {
                boolean isDirectory;
                String fileName = file.getFileName().toString();
                if (fileName.startsWith(".") || !(isDirectory = Files.isDirectory(file, new LinkOption[0])) && !fileName.endsWith(PREFAB_EXTENSION)) continue;
                String displayName = isDirectory ? fileName : this.removeExtension(fileName);
                entries.add(new FileListProvider.FileEntry(fileName, displayName, isDirectory));
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        entries.sort((a, b) -> {
            if (a.isDirectory() == b.isDirectory()) {
                return a.displayName().compareToIgnoreCase(b.displayName());
            }
            return a.isDirectory() ? -1 : 1;
        });
        return entries;
    }

    @Nonnull
    private List<FileListProvider.FileEntry> buildSearchResults(@Nonnull String currentDirStr, @Nonnull String searchQuery) {
        ObjectArrayList<SearchResult> allResults = new ObjectArrayList<SearchResult>();
        String lowerQuery = searchQuery.toLowerCase();
        if (currentDirStr.isEmpty()) {
            for (PrefabStore.AssetPackPrefabPath packPath : PrefabStore.get().getAllAssetPrefabPaths()) {
                String packKey = this.getPackKey(packPath);
                this.searchInDirectory(packPath.prefabsPath(), packKey, "", lowerQuery, allResults);
            }
        } else {
            String[] parts = currentDirStr.split("/", 2);
            String packKey = parts[0];
            String subPath = parts.length > 1 ? parts[1] : "";
            PrefabStore.AssetPackPrefabPath packPath = this.findPackByKey(packKey);
            if (packPath != null) {
                Path searchRoot = packPath.prefabsPath();
                if (!subPath.isEmpty()) {
                    searchRoot = searchRoot.resolve(subPath);
                }
                this.searchInDirectory(searchRoot, packKey, subPath, lowerQuery, allResults);
            }
        }
        allResults.sort(Comparator.comparingInt(SearchResult::score).reversed());
        ObjectArrayList<FileListProvider.FileEntry> entries = new ObjectArrayList<FileListProvider.FileEntry>();
        for (int i = 0; i < Math.min(allResults.size(), 50); ++i) {
            SearchResult result = (SearchResult)allResults.get(i);
            entries.add(new FileListProvider.FileEntry(result.relativePath(), result.displayName(), false, false, result.score()));
        }
        return entries;
    }

    private void searchInDirectory(final @Nonnull Path root, final @Nonnull String packKey, final @Nonnull String basePath, final @Nonnull String searchQuery, final @Nonnull List<SearchResult> results) {
        if (!Files.isDirectory(root, new LinkOption[0])) {
            return;
        }
        try {
            Files.walkFileTree(root, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(this){
                final /* synthetic */ AssetPrefabFileProvider this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                @Nonnull
                public FileVisitResult visitFile(@Nonnull Path file, @Nonnull BasicFileAttributes attrs) {
                    String baseName;
                    int score;
                    String fileName = file.getFileName().toString();
                    if (fileName.endsWith(AssetPrefabFileProvider.PREFAB_EXTENSION) && (score = StringCompareUtil.getFuzzyDistance((baseName = this.this$0.removeExtension(fileName)).toLowerCase(), searchQuery, Locale.ENGLISH)) > 0) {
                        Path relativePath = root.relativize(file);
                        String fullRelativePath = basePath.isEmpty() ? packKey + "/" + relativePath.toString().replace('\\', '/') : packKey + "/" + basePath + "/" + relativePath.toString().replace('\\', '/');
                        results.add(new SearchResult(fullRelativePath, baseName, score));
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    @Nonnull
    private String getPackKey(@Nonnull PrefabStore.AssetPackPrefabPath packPath) {
        return packPath.getDisplayName();
    }

    @Nullable
    private PrefabStore.AssetPackPrefabPath findPackByKey(@Nonnull String packKey) {
        for (PrefabStore.AssetPackPrefabPath packPath : PrefabStore.get().getAllAssetPrefabPaths()) {
            if (!this.getPackKey(packPath).equals(packKey)) continue;
            return packPath;
        }
        return null;
    }

    @Nonnull
    private String removeExtension(@Nonnull String fileName) {
        if (fileName.endsWith(PREFAB_EXTENSION)) {
            return fileName.substring(0, fileName.length() - PREFAB_EXTENSION.length());
        }
        return fileName;
    }

    @Nullable
    public Path resolveVirtualPath(@Nonnull String virtualPath) {
        if (virtualPath.isEmpty()) {
            return null;
        }
        String[] parts = virtualPath.split("/", 2);
        String packKey = parts[0];
        String subPath = parts.length > 1 ? parts[1] : "";
        PrefabStore.AssetPackPrefabPath packPath = this.findPackByKey(packKey);
        if (packPath == null) {
            return null;
        }
        if (subPath.isEmpty()) {
            return packPath.prefabsPath();
        }
        return packPath.prefabsPath().resolve(subPath);
    }

    @Nonnull
    public String getPackDisplayName(@Nonnull String packKey) {
        PrefabStore.AssetPackPrefabPath packPath = this.findPackByKey(packKey);
        return packPath != null ? packPath.getDisplayName() : packKey;
    }

    private record SearchResult(@Nonnull String relativePath, @Nonnull String displayName, int score) {
    }
}

