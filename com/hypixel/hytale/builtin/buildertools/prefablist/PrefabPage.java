/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.prefablist;

import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
import com.hypixel.hytale.builtin.buildertools.prefablist.AssetPrefabFileProvider;
import com.hypixel.hytale.builtin.buildertools.utils.PasteToolUtil;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.browser.FileBrowserConfig;
import com.hypixel.hytale.server.core.ui.browser.FileBrowserEventData;
import com.hypixel.hytale.server.core.ui.browser.FileListProvider;
import com.hypixel.hytale.server.core.ui.browser.ServerFileBrowser;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.annotation.Nonnull;

public class PrefabPage
extends InteractiveCustomUIPage<FileBrowserEventData> {
    private static final String ASSETS_ROOT_KEY = "Assets";
    @Nonnull
    private final ServerFileBrowser browser;
    @Nonnull
    private final BuilderToolsPlugin.BuilderState builderState;
    @Nonnull
    private final AssetPrefabFileProvider assetProvider;
    private boolean inAssetsRoot = true;
    @Nonnull
    private Path assetsCurrentDir = Paths.get("", new String[0]);

    public PrefabPage(@Nonnull PlayerRef playerRef, Path defaultRoot, @Nonnull BuilderToolsPlugin.BuilderState builderState) {
        super(playerRef, CustomPageLifetime.CanDismiss, FileBrowserEventData.CODEC);
        this.builderState = builderState;
        this.assetProvider = new AssetPrefabFileProvider();
        PrefabStore prefabStore = PrefabStore.get();
        List<FileBrowserConfig.RootEntry> roots = PrefabPage.buildRootEntries(prefabStore);
        FileBrowserConfig config = FileBrowserConfig.builder().listElementId("#FileList").rootSelectorId("#RootSelector").searchInputId("#SearchInput").roots(roots).allowedExtensions(".prefab.json").enableRootSelector(true).enableSearch(true).enableDirectoryNav(true).maxResults(50).build();
        String savedSearchQuery = builderState.getPrefabListSearchQuery();
        Path initialRoot = roots.get(0).path();
        this.browser = new ServerFileBrowser(config, initialRoot, null);
        if (savedSearchQuery != null && !savedSearchQuery.isEmpty()) {
            this.browser.setSearchQuery(savedSearchQuery);
        }
    }

    @Nonnull
    private static List<FileBrowserConfig.RootEntry> buildRootEntries(@Nonnull PrefabStore prefabStore) {
        ObjectArrayList<FileBrowserConfig.RootEntry> roots = new ObjectArrayList<FileBrowserConfig.RootEntry>();
        roots.add(new FileBrowserConfig.RootEntry(ASSETS_ROOT_KEY, Paths.get(ASSETS_ROOT_KEY, new String[0])));
        roots.add(new FileBrowserConfig.RootEntry("Server", prefabStore.getServerPrefabsPath()));
        return roots;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> store) {
        commandBuilder.append("Pages/PrefabListPage.ui");
        this.browser.buildRootSelector(commandBuilder, eventBuilder);
        this.browser.buildSearchInput(commandBuilder, eventBuilder);
        this.buildCurrentPath(commandBuilder);
        this.buildFileList(commandBuilder, eventBuilder);
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull FileBrowserEventData data) {
        String selectedPath;
        if (data.getRoot() != null) {
            this.inAssetsRoot = ASSETS_ROOT_KEY.equals(data.getRoot());
            this.assetsCurrentDir = Paths.get("", new String[0]);
            this.browser.handleEvent(data);
            UICommandBuilder commandBuilder = new UICommandBuilder();
            UIEventBuilder eventBuilder = new UIEventBuilder();
            this.buildCurrentPath(commandBuilder);
            this.buildFileList(commandBuilder, eventBuilder);
            this.sendUpdate(commandBuilder, eventBuilder, false);
            return;
        }
        if (data.getSearchQuery() != null) {
            this.browser.handleEvent(data);
            UICommandBuilder commandBuilder = new UICommandBuilder();
            UIEventBuilder eventBuilder = new UIEventBuilder();
            this.buildCurrentPath(commandBuilder);
            this.buildFileList(commandBuilder, eventBuilder);
            this.sendUpdate(commandBuilder, eventBuilder, false);
            return;
        }
        String string = selectedPath = data.getSearchResult() != null ? data.getSearchResult() : data.getFile();
        if (selectedPath != null) {
            if (this.inAssetsRoot) {
                this.handleAssetsNavigation(ref, store, selectedPath, data.getSearchResult() != null);
            } else {
                this.handleRegularNavigation(ref, store, selectedPath, data.getSearchResult() != null);
            }
        }
    }

    private void handleAssetsNavigation(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull String selectedPath, boolean isSearchResult) {
        if ("..".equals(selectedPath)) {
            if (!this.assetsCurrentDir.toString().isEmpty()) {
                Path parent = this.assetsCurrentDir.getParent();
                this.assetsCurrentDir = parent != null ? parent : Paths.get("", new String[0]);
                UICommandBuilder commandBuilder = new UICommandBuilder();
                UIEventBuilder eventBuilder = new UIEventBuilder();
                this.buildCurrentPath(commandBuilder);
                this.buildFileList(commandBuilder, eventBuilder);
                this.sendUpdate(commandBuilder, eventBuilder, false);
            }
            return;
        }
        Object targetVirtualPath = isSearchResult ? selectedPath : (this.assetsCurrentDir.toString().isEmpty() ? selectedPath : this.assetsCurrentDir.toString().replace('\\', '/') + "/" + selectedPath);
        Path resolvedPath = this.assetProvider.resolveVirtualPath((String)targetVirtualPath);
        if (resolvedPath == null) {
            this.sendUpdate();
            return;
        }
        if (Files.isDirectory(resolvedPath, new LinkOption[0])) {
            this.assetsCurrentDir = Paths.get((String)targetVirtualPath, new String[0]);
            UICommandBuilder commandBuilder = new UICommandBuilder();
            UIEventBuilder eventBuilder = new UIEventBuilder();
            this.buildCurrentPath(commandBuilder);
            this.buildFileList(commandBuilder, eventBuilder);
            this.sendUpdate(commandBuilder, eventBuilder, false);
        } else {
            this.handlePrefabSelection(ref, store, resolvedPath, (String)targetVirtualPath);
        }
    }

    private void handleRegularNavigation(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull String selectedPath, boolean isSearchResult) {
        if (this.browser.handleEvent(FileBrowserEventData.file(selectedPath))) {
            UICommandBuilder commandBuilder = new UICommandBuilder();
            UIEventBuilder eventBuilder = new UIEventBuilder();
            this.buildCurrentPath(commandBuilder);
            this.buildFileList(commandBuilder, eventBuilder);
            this.sendUpdate(commandBuilder, eventBuilder, false);
            return;
        }
        Path file = isSearchResult ? this.browser.resolveSecure(selectedPath) : this.browser.resolveFromCurrent(selectedPath);
        if (file == null || Files.isDirectory(file, new LinkOption[0])) {
            this.sendUpdate();
            return;
        }
        this.handlePrefabSelection(ref, store, file, selectedPath);
    }

    private void handlePrefabSelection(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull Path file, @Nonnull String displayPath) {
        Player playerComponent = store.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        if (playerComponent.getGameMode() != GameMode.Creative) {
            playerComponent.getPageManager().setPage(ref, store, Page.None);
            return;
        }
        PlayerRef playerRefComponent = store.getComponent(ref, PlayerRef.getComponentType());
        assert (playerRefComponent != null);
        playerComponent.getPageManager().setPage(ref, store, Page.None);
        BlockSelection prefab = PrefabStore.get().getPrefab(file);
        BuilderToolsPlugin.addToQueue(playerComponent, playerRefComponent, (r, s, componentAccessor) -> s.load(displayPath, prefab, (ComponentAccessor<EntityStore>)componentAccessor));
        PasteToolUtil.switchToPasteTool(playerComponent, playerRefComponent);
    }

    private void buildCurrentPath(@Nonnull UICommandBuilder commandBuilder) {
        Object displayPath;
        if (this.inAssetsRoot) {
            String currentDirStr = this.assetsCurrentDir.toString().replace('\\', '/');
            if (currentDirStr.isEmpty()) {
                displayPath = ASSETS_ROOT_KEY;
            } else {
                String subPath;
                String[] parts = currentDirStr.split("/", 2);
                String packName = parts[0];
                String string = subPath = parts.length > 1 ? "/" + parts[1] : "";
                displayPath = "HytaleAssets".equals(packName) ? packName + subPath : "Mods/" + packName + subPath;
            }
        } else {
            Path root = this.browser.getRoot();
            String rootDisplay = this.getRootDisplayName(root);
            String currentPath = this.browser.getCurrentDir().toString().replace('\\', '/');
            displayPath = currentPath.isEmpty() ? rootDisplay : rootDisplay + "/" + currentPath;
        }
        commandBuilder.set("#CurrentPath.Text", (String)displayPath);
    }

    @Nonnull
    private String getRootDisplayName(@Nonnull Path root) {
        PrefabStore prefabStore = PrefabStore.get();
        String rootStr = root.toString();
        if (ASSETS_ROOT_KEY.equals(rootStr)) {
            return ASSETS_ROOT_KEY;
        }
        if (rootStr.equals(prefabStore.getServerPrefabsPath().toString())) {
            return "Server";
        }
        if (rootStr.equals(prefabStore.getWorldGenPrefabsPath().toString())) {
            return "WorldGen";
        }
        return root.getFileName() != null ? root.getFileName().toString() : rootStr;
    }

    private void buildFileList(@Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder) {
        if (this.inAssetsRoot) {
            this.buildAssetsFileList(commandBuilder, eventBuilder);
        } else {
            this.browser.buildFileList(commandBuilder, eventBuilder);
        }
    }

    private void buildAssetsFileList(@Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder) {
        commandBuilder.clear("#FileList");
        List<FileListProvider.FileEntry> entries = this.assetProvider.getFiles(this.assetsCurrentDir, this.browser.getSearchQuery());
        int buttonIndex = 0;
        if (!this.assetsCurrentDir.toString().isEmpty() && this.browser.getSearchQuery().isEmpty()) {
            commandBuilder.append("#FileList", "Pages/BasicTextButton.ui");
            commandBuilder.set("#FileList[0].Text", "../");
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#FileList[0]", EventData.of("File", ".."));
            ++buttonIndex;
        }
        for (FileListProvider.FileEntry entry : entries) {
            Object displayText = entry.isDirectory() ? entry.displayName() + "/" : entry.displayName();
            commandBuilder.append("#FileList", "Pages/BasicTextButton.ui");
            commandBuilder.set("#FileList[" + buttonIndex + "].Text", (String)displayText);
            if (!entry.isDirectory()) {
                commandBuilder.set("#FileList[" + buttonIndex + "].Style", Value.ref("Pages/BasicTextButton.ui", "SelectedLabelStyle"));
            }
            String eventKey = !this.browser.getSearchQuery().isEmpty() && !entry.isDirectory() ? "SearchResult" : "File";
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#FileList[" + buttonIndex + "]", EventData.of(eventKey, entry.name()));
            ++buttonIndex;
        }
    }
}

