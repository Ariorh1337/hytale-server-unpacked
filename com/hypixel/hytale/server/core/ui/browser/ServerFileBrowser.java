/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.ui.browser;

import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.common.plugin.PluginManifest;
import com.hypixel.hytale.common.util.PathUtil;
import com.hypixel.hytale.common.util.StringCompareUtil;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.core.ui.DropdownEntryInfo;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.browser.FileBrowserConfig;
import com.hypixel.hytale.server.core.ui.browser.FileBrowserEventData;
import com.hypixel.hytale.server.core.ui.browser.FileListProvider;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ServerFileBrowser {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final Value<String> BUTTON_HIGHLIGHTED = Value.ref("Pages/BasicTextButton.ui", "SelectedLabelStyle");
    private static final String BASE_ASSET_PACK_DISPLAY_NAME = "HytaleAssets";
    @Nonnull
    private final FileBrowserConfig config;
    @Nonnull
    private Path root;
    @Nonnull
    private Path currentDir;
    @Nonnull
    private String searchQuery;
    @Nonnull
    private final Set<String> selectedItems;

    public ServerFileBrowser(@Nonnull FileBrowserConfig config) {
        this.config = config;
        this.selectedItems = new LinkedHashSet<String>();
        this.searchQuery = "";
        this.root = !config.roots().isEmpty() ? config.roots().get(0).path() : Paths.get("", new String[0]);
        this.currentDir = this.root.getFileSystem().getPath("", new String[0]);
    }

    public ServerFileBrowser(@Nonnull FileBrowserConfig config, @Nullable Path initialRoot, @Nullable Path initialDir) {
        this(config);
        if (initialRoot != null && Files.isDirectory(initialRoot, new LinkOption[0])) {
            this.root = initialRoot;
            this.currentDir = this.root.getFileSystem().getPath("", new String[0]);
        }
        if (initialDir != null) {
            this.currentDir = initialDir;
        }
    }

    public void buildRootSelector(@Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder) {
        if (!this.config.enableRootSelector() || this.config.rootSelectorId() == null) {
            if (this.config.rootSelectorId() != null) {
                commandBuilder.set(this.config.rootSelectorId() + ".Visible", false);
            }
            return;
        }
        ObjectArrayList<DropdownEntryInfo> entries = new ObjectArrayList<DropdownEntryInfo>();
        for (FileBrowserConfig.RootEntry rootEntry : this.config.roots()) {
            entries.add(new DropdownEntryInfo(rootEntry.displayName(), rootEntry.path().toString()));
        }
        commandBuilder.set(this.config.rootSelectorId() + ".Entries", entries);
        commandBuilder.set(this.config.rootSelectorId() + ".Value", this.root.toString());
        eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, this.config.rootSelectorId(), new EventData().append("@Root", this.config.rootSelectorId() + ".Value"), false);
    }

    public void buildSearchInput(@Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder) {
        if (!this.config.enableSearch() || this.config.searchInputId() == null) {
            return;
        }
        if (!this.searchQuery.isEmpty()) {
            commandBuilder.set(this.config.searchInputId() + ".Value", this.searchQuery);
        }
        eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, this.config.searchInputId(), EventData.of("@SearchQuery", this.config.searchInputId() + ".Value"), false);
    }

    public void buildCurrentPath(@Nonnull UICommandBuilder commandBuilder) {
        Object displayPath;
        if (this.config.currentPathId() == null) {
            return;
        }
        if (this.config.assetPackMode()) {
            String currentDirStr = this.currentDir.toString().replace('\\', '/');
            if (currentDirStr.isEmpty()) {
                displayPath = "Assets";
            } else {
                String subPath;
                String[] parts = currentDirStr.split("/", 2);
                String packName = parts[0];
                String string = subPath = parts.length > 1 ? "/" + parts[1] : "";
                displayPath = BASE_ASSET_PACK_DISPLAY_NAME.equals(packName) ? packName + subPath : "Mods/" + packName + subPath;
            }
        } else {
            String rootDisplay = this.root.toString().replace("\\", "/");
            String relativeDisplay = this.currentDir.toString().isEmpty() ? "" : "/" + this.currentDir.toString().replace("\\", "/");
            displayPath = rootDisplay + relativeDisplay;
        }
        commandBuilder.set(this.config.currentPathId() + ".Text", (String)displayPath);
    }

    public void buildFileList(@Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder) {
        commandBuilder.clear(this.config.listElementId());
        List<FileListProvider.FileEntry> entries = this.config.customProvider() != null ? this.config.customProvider().getFiles(this.currentDir, this.searchQuery) : (this.config.assetPackMode() ? (!this.searchQuery.isEmpty() && this.config.enableSearch() ? this.buildAssetPackSearchResults() : this.buildAssetPackListing()) : (!this.searchQuery.isEmpty() && this.config.enableSearch() ? this.buildSearchResults() : this.buildDirectoryListing()));
        int buttonIndex = 0;
        if (this.config.enableDirectoryNav() && !this.currentDir.toString().isEmpty() && this.searchQuery.isEmpty()) {
            commandBuilder.append(this.config.listElementId(), "Pages/BasicTextButton.ui");
            commandBuilder.set(this.config.listElementId() + "[0].Text", "../");
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, this.config.listElementId() + "[0]", EventData.of("File", ".."));
            ++buttonIndex;
        }
        for (FileListProvider.FileEntry entry : entries) {
            boolean isNavigableDir = entry.isDirectory() && !entry.isTerminal();
            Object displayText = isNavigableDir ? entry.displayName() + "/" : entry.displayName();
            commandBuilder.append(this.config.listElementId(), "Pages/BasicTextButton.ui");
            commandBuilder.set(this.config.listElementId() + "[" + buttonIndex + "].Text", (String)displayText);
            if (!entry.isDirectory() || entry.isTerminal()) {
                commandBuilder.set(this.config.listElementId() + "[" + buttonIndex + "].Style", BUTTON_HIGHLIGHTED);
            }
            String eventKey = !this.searchQuery.isEmpty() && !isNavigableDir ? "SearchResult" : "File";
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, this.config.listElementId() + "[" + buttonIndex + "]", EventData.of(eventKey, entry.name()));
            ++buttonIndex;
        }
    }

    public void buildUI(@Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder) {
        this.buildRootSelector(commandBuilder, eventBuilder);
        this.buildSearchInput(commandBuilder, eventBuilder);
        this.buildCurrentPath(commandBuilder);
        this.buildFileList(commandBuilder, eventBuilder);
    }

    public boolean handleEvent(@Nonnull FileBrowserEventData data) {
        if (data.getSearchQuery() != null) {
            this.searchQuery = data.getSearchQuery().trim().toLowerCase();
            return true;
        }
        if (data.getRoot() != null) {
            Path newRoot = this.findConfigRoot(data.getRoot());
            if (newRoot == null) {
                newRoot = Path.of(data.getRoot(), new String[0]);
            }
            this.setRoot(newRoot);
            this.currentDir = this.root.getFileSystem().getPath("", new String[0]);
            this.searchQuery = "";
            return true;
        }
        if (data.getFile() != null) {
            Path targetPath;
            String fileName = data.getFile();
            if ("..".equals(fileName)) {
                this.navigateUp();
                return true;
            }
            if (this.config.assetPackMode()) {
                return this.handleAssetPackNavigation(fileName);
            }
            if (this.config.enableDirectoryNav() && Files.isDirectory(targetPath = this.root.resolve(this.currentDir.toString()).resolve(fileName), new LinkOption[0])) {
                this.currentDir = PathUtil.relativize(this.root, targetPath);
                return true;
            }
            return false;
        }
        if (data.getSearchResult() != null) {
            return false;
        }
        return false;
    }

    private List<FileListProvider.FileEntry> buildDirectoryListing() {
        ObjectArrayList<FileListProvider.FileEntry> entries = new ObjectArrayList<FileListProvider.FileEntry>();
        Path path = this.root.resolve(this.currentDir.toString());
        if (!Files.isDirectory(path, new LinkOption[0])) {
            return entries;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path);){
            for (Path file : stream) {
                boolean isDirectory;
                String fileName = file.getFileName().toString();
                if (fileName.startsWith(".") || !(isDirectory = Files.isDirectory(file, new LinkOption[0])) && !this.matchesExtension(fileName)) continue;
                entries.add(new FileListProvider.FileEntry(fileName, isDirectory));
            }
        }
        catch (IOException e) {
            ((HytaleLogger.Api)((HytaleLogger.Api)LOGGER.atSevere()).withCause(e)).log("Error listing directory: %s", path);
        }
        entries.sort((a, b) -> {
            if (a.isDirectory() == b.isDirectory()) {
                return a.name().compareToIgnoreCase(b.name());
            }
            return a.isDirectory() ? -1 : 1;
        });
        return entries;
    }

    private List<FileListProvider.FileEntry> buildSearchResults() {
        final ObjectArrayList allFiles = new ObjectArrayList();
        if (!Files.isDirectory(this.root, new LinkOption[0])) {
            return List.of();
        }
        try {
            Files.walkFileTree(this.root, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(this){
                final /* synthetic */ ServerFileBrowser this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                @Nonnull
                public FileVisitResult visitFile(@Nonnull Path file, @Nonnull BasicFileAttributes attrs) {
                    String fileName = file.getFileName().toString();
                    if (this.this$0.matchesExtension(fileName)) {
                        allFiles.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException e) {
            ((HytaleLogger.Api)((HytaleLogger.Api)LOGGER.atSevere()).withCause(e)).log("Error walking directory: %s", this.root);
        }
        Object2IntOpenHashMap<Path> matchScores = new Object2IntOpenHashMap<Path>(allFiles.size());
        for (Path file2 : allFiles) {
            String fileName = file2.getFileName().toString();
            String baseName = this.removeExtensions(fileName);
            int score = StringCompareUtil.getFuzzyDistance(baseName, this.searchQuery, Locale.ENGLISH);
            if (score <= 0) continue;
            matchScores.put(file2, score);
        }
        return matchScores.keySet().stream().sorted(Comparator.comparingInt(matchScores::getInt).reversed()).limit(this.config.maxResults()).map(file -> {
            Path relativePath = PathUtil.relativize(this.root, file);
            String fileName = file.getFileName().toString();
            String displayName = this.removeExtensions(fileName);
            return new FileListProvider.FileEntry(relativePath.toString(), displayName, false, false, matchScores.getInt(file));
        }).collect(Collectors.toList());
    }

    private boolean matchesExtension(@Nonnull String fileName) {
        if (this.config.allowedExtensions().isEmpty()) {
            return true;
        }
        for (String ext : this.config.allowedExtensions()) {
            if (!fileName.endsWith(ext)) continue;
            return true;
        }
        return false;
    }

    private List<FileListProvider.FileEntry> buildAssetPackListing() {
        ObjectArrayList<FileListProvider.FileEntry> entries = new ObjectArrayList<FileListProvider.FileEntry>();
        String currentDirStr = this.currentDir.toString().replace('\\', '/');
        if (currentDirStr.isEmpty()) {
            for (AssetPack pack : AssetModule.get().getAssetPacks()) {
                Path subPath = this.getAssetPackSubPath(pack);
                if (subPath == null || !Files.isDirectory(subPath, new LinkOption[0])) continue;
                String displayName = this.getAssetPackDisplayName(pack);
                entries.add(new FileListProvider.FileEntry(displayName, displayName, true));
            }
        } else {
            Path packSubPath;
            String[] parts = currentDirStr.split("/", 2);
            String packName = parts[0];
            String subDir = parts.length > 1 ? parts[1] : "";
            AssetPack pack = this.findAssetPackByDisplayName(packName);
            if (pack != null && (packSubPath = this.getAssetPackSubPath(pack)) != null) {
                Path targetDir;
                Path path = targetDir = subDir.isEmpty() ? packSubPath : packSubPath.resolve(subDir);
                if (Files.isDirectory(targetDir, new LinkOption[0])) {
                    try (DirectoryStream<Path> stream = Files.newDirectoryStream(targetDir);){
                        for (Path file : stream) {
                            boolean isDirectory;
                            String fileName = file.getFileName().toString();
                            if (fileName.startsWith(".") || !(isDirectory = Files.isDirectory(file, new LinkOption[0])) && !this.matchesExtension(fileName)) continue;
                            String displayName = isDirectory ? fileName : this.removeExtensions(fileName);
                            boolean isTerminal = isDirectory && this.isTerminalDirectory(file);
                            entries.add(new FileListProvider.FileEntry(fileName, displayName, isDirectory, isTerminal));
                        }
                    }
                    catch (IOException e) {
                        ((HytaleLogger.Api)((HytaleLogger.Api)LOGGER.atSevere()).withCause(e)).log("Error listing asset pack directory: %s", targetDir);
                    }
                }
            }
        }
        entries.sort((a, b) -> {
            boolean bIsBase;
            boolean aIsBase = BASE_ASSET_PACK_DISPLAY_NAME.equals(a.name());
            if (aIsBase != (bIsBase = BASE_ASSET_PACK_DISPLAY_NAME.equals(b.name()))) {
                return aIsBase ? -1 : 1;
            }
            if (a.isDirectory() == b.isDirectory()) {
                return a.displayName().compareToIgnoreCase(b.displayName());
            }
            return a.isDirectory() ? -1 : 1;
        });
        return entries;
    }

    private List<FileListProvider.FileEntry> buildAssetPackSearchResults() {
        ObjectArrayList<AssetPackSearchResult> allResults = new ObjectArrayList<AssetPackSearchResult>();
        String currentDirStr = this.currentDir.toString().replace('\\', '/');
        if (currentDirStr.isEmpty()) {
            for (AssetPack pack : AssetModule.get().getAssetPacks()) {
                Path subPath = this.getAssetPackSubPath(pack);
                if (subPath == null || !Files.isDirectory(subPath, new LinkOption[0])) continue;
                String packDisplayName = this.getAssetPackDisplayName(pack);
                this.searchInAssetPackDirectory(subPath, packDisplayName, "", allResults);
            }
        } else {
            Path packSubPath;
            String[] parts = currentDirStr.split("/", 2);
            String packName = parts[0];
            String subDir = parts.length > 1 ? parts[1] : "";
            AssetPack pack = this.findAssetPackByDisplayName(packName);
            if (pack != null && (packSubPath = this.getAssetPackSubPath(pack)) != null) {
                Path searchRoot;
                Path path = searchRoot = subDir.isEmpty() ? packSubPath : packSubPath.resolve(subDir);
                if (Files.isDirectory(searchRoot, new LinkOption[0])) {
                    this.searchInAssetPackDirectory(searchRoot, packName, subDir, allResults);
                }
            }
        }
        allResults.sort(Comparator.comparingInt(AssetPackSearchResult::score).reversed());
        return allResults.stream().limit(this.config.maxResults()).map(r -> new FileListProvider.FileEntry(r.virtualPath(), r.displayName(), r.isTerminal(), r.isTerminal(), r.score())).collect(Collectors.toList());
    }

    private void searchInAssetPackDirectory(final @Nonnull Path searchRoot, final @Nonnull String packName, final @Nonnull String basePath, final @Nonnull List<AssetPackSearchResult> results) {
        try {
            Files.walkFileTree(searchRoot, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(this){
                final /* synthetic */ ServerFileBrowser this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                @Nonnull
                public FileVisitResult preVisitDirectory(@Nonnull Path dir, @Nonnull BasicFileAttributes attrs) {
                    if (dir.equals(searchRoot)) {
                        return FileVisitResult.CONTINUE;
                    }
                    if (this.this$0.isTerminalDirectory(dir)) {
                        String dirName = dir.getFileName().toString();
                        int score = StringCompareUtil.getFuzzyDistance(dirName.toLowerCase(), this.this$0.searchQuery, Locale.ENGLISH);
                        if (score > 0) {
                            Path relativePath = searchRoot.relativize(dir);
                            String relativeStr = relativePath.toString().replace('\\', '/');
                            String virtualPath = basePath.isEmpty() ? packName + "/" + relativeStr : packName + "/" + basePath + "/" + relativeStr;
                            results.add(new AssetPackSearchResult(virtualPath, dirName, score, true));
                        }
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                @Nonnull
                public FileVisitResult visitFile(@Nonnull Path file, @Nonnull BasicFileAttributes attrs) {
                    String baseName;
                    int score;
                    String fileName = file.getFileName().toString();
                    if (this.this$0.matchesExtension(fileName) && (score = StringCompareUtil.getFuzzyDistance((baseName = this.this$0.removeExtensions(fileName)).toLowerCase(), this.this$0.searchQuery, Locale.ENGLISH)) > 0) {
                        Path relativePath = searchRoot.relativize(file);
                        String relativeStr = relativePath.toString().replace('\\', '/');
                        String virtualPath = basePath.isEmpty() ? packName + "/" + relativeStr : packName + "/" + basePath + "/" + relativeStr;
                        results.add(new AssetPackSearchResult(virtualPath, baseName, score, false));
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException e) {
            ((HytaleLogger.Api)((HytaleLogger.Api)LOGGER.atSevere()).withCause(e)).log("Error searching asset pack directory: %s", searchRoot);
        }
    }

    private boolean handleAssetPackNavigation(@Nonnull String fileName) {
        String currentDirStr = this.currentDir.toString().replace('\\', '/');
        if (currentDirStr.isEmpty()) {
            Path subPath;
            AssetPack pack = this.findAssetPackByDisplayName(fileName);
            if (pack != null && (subPath = this.getAssetPackSubPath(pack)) != null && Files.isDirectory(subPath, new LinkOption[0])) {
                this.currentDir = Paths.get(fileName, new String[0]);
                return true;
            }
            return false;
        }
        String[] parts = currentDirStr.split("/", 2);
        String packName = parts[0];
        String subDir = parts.length > 1 ? parts[1] : "";
        AssetPack pack = this.findAssetPackByDisplayName(packName);
        if (pack == null) {
            return false;
        }
        Path packSubPath = this.getAssetPackSubPath(pack);
        if (packSubPath == null) {
            return false;
        }
        Path targetDir = subDir.isEmpty() ? packSubPath : packSubPath.resolve(subDir);
        Path targetPath = targetDir.resolve(fileName);
        if (Files.isDirectory(targetPath, new LinkOption[0])) {
            if (this.isTerminalDirectory(targetPath)) {
                return false;
            }
            String newPath = subDir.isEmpty() ? packName + "/" + fileName : packName + "/" + subDir + "/" + fileName;
            this.currentDir = Paths.get(newPath, new String[0]);
            return true;
        }
        return false;
    }

    @Nullable
    private Path getAssetPackSubPath(@Nonnull AssetPack pack) {
        if (this.config.assetPackSubPath() == null) {
            return null;
        }
        return pack.getRoot().resolve(this.config.assetPackSubPath());
    }

    @Nonnull
    private String getAssetPackDisplayName(@Nonnull AssetPack pack) {
        if (pack.equals(AssetModule.get().getBaseAssetPack())) {
            return BASE_ASSET_PACK_DISPLAY_NAME;
        }
        PluginManifest manifest = pack.getManifest();
        return manifest != null ? manifest.getName() : pack.getName();
    }

    @Nullable
    private AssetPack findAssetPackByDisplayName(@Nonnull String displayName) {
        for (AssetPack pack : AssetModule.get().getAssetPacks()) {
            if (!this.getAssetPackDisplayName(pack).equals(displayName)) continue;
            return pack;
        }
        return null;
    }

    private boolean isTerminalDirectory(@Nonnull Path path) {
        Predicate<Path> predicate = this.config.terminalDirectoryPredicate();
        return predicate != null && predicate.test(path);
    }

    @Nullable
    public Path resolveAssetPackPath(@Nonnull String virtualPath) {
        if (!this.config.assetPackMode() || virtualPath.isEmpty()) {
            return null;
        }
        String[] parts = virtualPath.replace('\\', '/').split("/", 2);
        String packName = parts[0];
        String subPath = parts.length > 1 ? parts[1] : "";
        AssetPack pack = this.findAssetPackByDisplayName(packName);
        if (pack == null) {
            return null;
        }
        Path packSubPath = this.getAssetPackSubPath(pack);
        if (packSubPath == null) {
            return null;
        }
        return subPath.isEmpty() ? packSubPath : packSubPath.resolve(subPath);
    }

    @Nonnull
    public String getAssetPackCurrentPath() {
        return this.currentDir.toString().replace('\\', '/');
    }

    private String removeExtensions(@Nonnull String fileName) {
        for (String ext : this.config.allowedExtensions()) {
            if (!fileName.endsWith(ext)) continue;
            return fileName.substring(0, fileName.length() - ext.length());
        }
        return fileName;
    }

    @Nonnull
    public Path getRoot() {
        return this.root;
    }

    public void setRoot(@Nonnull Path root) {
        this.root = root;
    }

    @Nonnull
    public Path getCurrentDir() {
        return this.currentDir;
    }

    public void setCurrentDir(@Nonnull Path currentDir) {
        this.currentDir = currentDir;
    }

    @Nonnull
    public String getSearchQuery() {
        return this.searchQuery;
    }

    public void setSearchQuery(@Nonnull String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public void navigateUp() {
        if (!this.currentDir.toString().isEmpty()) {
            Path parent = this.currentDir.getParent();
            this.currentDir = parent != null ? parent : Paths.get("", new String[0]);
        }
    }

    public void navigateTo(@Nonnull Path relativePath) {
        Path targetPath = this.root.resolve(this.currentDir.toString()).resolve(relativePath.toString());
        if (!targetPath.normalize().startsWith(this.root.normalize())) {
            return;
        }
        if (Files.isDirectory(targetPath, new LinkOption[0])) {
            this.currentDir = PathUtil.relativize(this.root, targetPath);
        }
    }

    @Nonnull
    public Set<String> getSelectedItems() {
        return Collections.unmodifiableSet(this.selectedItems);
    }

    public void addSelection(@Nonnull String item) {
        if (this.config.enableMultiSelect()) {
            this.selectedItems.add(item);
        } else {
            this.selectedItems.clear();
            this.selectedItems.add(item);
        }
    }

    public void clearSelection() {
        this.selectedItems.clear();
    }

    @Nonnull
    public FileBrowserConfig getConfig() {
        return this.config;
    }

    @Nullable
    public Path resolveSecure(@Nonnull String relativePath) {
        Path resolved = this.root.resolve(relativePath);
        if (!resolved.normalize().startsWith(this.root.normalize())) {
            return null;
        }
        return resolved;
    }

    @Nullable
    public Path resolveFromCurrent(@Nonnull String fileName) {
        Path resolved = this.root.resolve(this.currentDir.toString()).resolve(fileName);
        if (!resolved.normalize().startsWith(this.root.normalize())) {
            return null;
        }
        return resolved;
    }

    @Nullable
    private Path findConfigRoot(@Nonnull String pathStr) {
        for (FileBrowserConfig.RootEntry rootEntry : this.config.roots()) {
            if (!rootEntry.path().toString().equals(pathStr)) continue;
            return rootEntry.path();
        }
        return null;
    }

    private record AssetPackSearchResult(@Nonnull String virtualPath, @Nonnull String displayName, int score, boolean isTerminal) {
    }
}

