/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.scriptedbrushes.ui;

import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.builtin.buildertools.PrototypePlayerBuilderToolSettings;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.BrushConfigCommandExecutor;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.ScriptedBrushAsset;
import com.hypixel.hytale.builtin.buildertools.tooloperations.ToolOperation;
import com.hypixel.hytale.common.util.StringCompareUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.browser.FileBrowserConfig;
import com.hypixel.hytale.server.core.ui.browser.FileBrowserEventData;
import com.hypixel.hytale.server.core.ui.browser.FileListProvider;
import com.hypixel.hytale.server.core.ui.browser.ServerFileBrowser;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class ScriptedBrushPage
extends InteractiveCustomUIPage<FileBrowserEventData> {
    @Nonnull
    private final ServerFileBrowser browser;

    public ScriptedBrushPage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, FileBrowserEventData.CODEC);
        FileBrowserConfig config = FileBrowserConfig.builder().listElementId("#FileList").searchInputId("#SearchInput").enableRootSelector(false).enableSearch(true).enableDirectoryNav(false).maxResults(50).customProvider(new ScriptedBrushListProvider()).build();
        this.browser = new ServerFileBrowser(config);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> store) {
        commandBuilder.append("Pages/ScriptedBrushListPage.ui");
        commandBuilder.set("#RootSelector.Visible", false);
        this.browser.buildSearchInput(commandBuilder, eventBuilder);
        this.browser.buildFileList(commandBuilder, eventBuilder);
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull FileBrowserEventData data) {
        if (this.browser.handleEvent(data)) {
            UICommandBuilder commandBuilder = new UICommandBuilder();
            UIEventBuilder eventBuilder = new UIEventBuilder();
            this.browser.buildFileList(commandBuilder, eventBuilder);
            this.sendUpdate(commandBuilder, eventBuilder, false);
            return;
        }
        String brushName = data.getFile();
        if (brushName != null) {
            this.handleBrushSelection(ref, store, brushName);
        }
    }

    private void handleBrushSelection(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull String brushName) {
        Player playerComponent = store.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        PlayerRef playerRefComponent = store.getComponent(ref, PlayerRef.getComponentType());
        assert (playerRefComponent != null);
        ScriptedBrushAsset scriptedBrushAsset = ScriptedBrushAsset.get(brushName);
        if (scriptedBrushAsset == null) {
            playerRefComponent.sendMessage(Message.translation("server.commands.brushConfig.load.error.notFound").param("name", brushName));
            this.sendUpdate();
            return;
        }
        UUID playerUUID = playerRefComponent.getUuid();
        PrototypePlayerBuilderToolSettings prototypeSettings = ToolOperation.getOrCreatePrototypeSettings(playerUUID);
        BrushConfigCommandExecutor brushConfigCommandExecutor = prototypeSettings.getBrushConfigCommandExecutor();
        try {
            scriptedBrushAsset.loadIntoExecutor(brushConfigCommandExecutor);
            prototypeSettings.setCurrentlyLoadedBrushConfigName(scriptedBrushAsset.getId());
            prototypeSettings.setUsePrototypeBrushConfigurations(true);
            playerComponent.getPageManager().setPage(ref, store, Page.None);
            playerRefComponent.sendMessage(Message.translation("server.commands.brushConfig.loaded").param("name", scriptedBrushAsset.getId()));
        }
        catch (Exception e) {
            playerRefComponent.sendMessage(Message.translation("server.commands.brushConfig.load.error.loadFailed").param("name", brushName).param("error", e.getMessage() != null ? e.getMessage() : "Unknown error"));
            this.sendUpdate();
        }
    }

    private static class ScriptedBrushListProvider
    implements FileListProvider {
        private ScriptedBrushListProvider() {
        }

        @Override
        @Nonnull
        public List<FileListProvider.FileEntry> getFiles(@Nonnull Path currentDir, @Nonnull String searchQuery) {
            DefaultAssetMap<String, ScriptedBrushAsset> assetMap = ScriptedBrushAsset.getAssetMap();
            if (searchQuery.isEmpty()) {
                return assetMap.getAssetMap().keySet().stream().sorted().map(name -> new FileListProvider.FileEntry((String)name, false)).collect(Collectors.toList());
            }
            ObjectArrayList<FileListProvider.FileEntry> results = new ObjectArrayList<FileListProvider.FileEntry>();
            for (String name2 : assetMap.getAssetMap().keySet()) {
                int score = StringCompareUtil.getFuzzyDistance(name2, searchQuery, Locale.ENGLISH);
                if (score <= 0) continue;
                results.add(new FileListProvider.FileEntry(name2, name2, false, false, score));
            }
            results.sort(Comparator.comparingInt(FileListProvider.FileEntry::matchScore).reversed());
            if (results.size() > 50) {
                return results.subList(0, 50);
            }
            return results;
        }
    }
}

