/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.prefabeditor.ui;

import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabEditSession;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabEditingMetadata;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.saving.PrefabSaver;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.saving.PrefabSaverSettings;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.common.util.StringCompareUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class PrefabEditorSaveSettingsPage
extends InteractiveCustomUIPage<PageData> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final Value<String> BUTTON_HIGHLIGHTED = Value.ref("Pages/BasicTextButton.ui", "SelectedLabelStyle");
    private static final Value<String> BUTTON_SELECTED = Value.ref("Pages/BasicTextButton.ui", "SelectedLabelStyle");
    @Nonnull
    private final PrefabEditSession prefabEditSession;
    private volatile boolean isSaving = false;
    private volatile long lastProgressUpdateTime = 0L;
    private static final long PROGRESS_UPDATE_INTERVAL_MS = 100L;
    @Nonnull
    private String browserSearchQuery = "";
    private final Set<UUID> selectedPrefabUuids = new HashSet<UUID>();

    public PrefabEditorSaveSettingsPage(@Nonnull PlayerRef playerRef, @Nonnull PrefabEditSession prefabEditSession) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, PageData.CODEC);
        this.prefabEditSession = prefabEditSession;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> store) {
        commandBuilder.append("Pages/PrefabEditorSaveSettings.ui");
        PrefabEditingMetadata selectedPrefab = this.prefabEditSession.getSelectedPrefab(this.playerRef.getUuid());
        if (selectedPrefab != null) {
            String prefabPath = selectedPrefab.getPrefabPath().toString().replace('\\', '/');
            commandBuilder.set("#MainPage #PrefabsToSave #Input.Value", prefabPath);
            this.selectedPrefabUuids.add(selectedPrefab.getUuid());
        }
        commandBuilder.set("#MainPage #Entities #CheckBox.Value", true);
        commandBuilder.set("#MainPage #Empty #CheckBox.Value", false);
        commandBuilder.set("#MainPage #Overwrite #CheckBox.Value", true);
        commandBuilder.set("#SavingPage.Visible", false);
        commandBuilder.set("#SavingPage #ProgressBar.Value", 0.0f);
        commandBuilder.set("#SavingPage #StatusText.TextSpans", Message.translation("server.commands.editprefab.save.saving"));
        commandBuilder.set("#SavingPage #ErrorText.Visible", false);
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#MainPage #SaveButton", new EventData().append("Action", Action.Save.name()).append("@PrefabsToSave", "#MainPage #PrefabsToSave #Input.Value").append("@Entities", "#MainPage #Entities #CheckBox.Value").append("@Empty", "#MainPage #Empty #CheckBox.Value").append("@Overwrite", "#MainPage #Overwrite #CheckBox.Value"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#MainPage #CancelButton", new EventData().append("Action", Action.Cancel.name()));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#MainPage #PrefabsToSave #BrowseButton", new EventData().append("Action", Action.OpenBrowser.name()));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#MainPage #PrefabsToSave #SelectAllButton", new EventData().append("Action", Action.SelectAll.name()));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#MainPage #PrefabsToSave #SelectEditedButton", new EventData().append("Action", Action.SelectEdited.name()));
        eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#BrowserPage #SearchInput", new EventData().append("Action", Action.BrowserSearch.name()).append("@BrowserSearch", "#BrowserPage #SearchInput.Value"), false);
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#BrowserPage #BrowserButtons #SelectAllBrowserButton", new EventData().append("Action", Action.BrowserSelectAll.name()));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#BrowserPage #BrowserButtons #ConfirmButton", new EventData().append("Action", Action.ConfirmBrowser.name()));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#BrowserPage #BrowserButtons #CancelButton", new EventData().append("Action", Action.CancelBrowser.name()));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#SavingPage #BackButton", new EventData().append("Action", Action.BackFromSaving.name()));
        commandBuilder.set("#BrowserPage.Visible", false);
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull PageData data) {
        Player playerComponent = store.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        switch (data.action.ordinal()) {
            case 0: {
                if (this.isSaving) {
                    return;
                }
                String prefabsToSaveStr = data.prefabsToSave;
                if (prefabsToSaveStr == null || prefabsToSaveStr.isBlank()) {
                    this.playerRef.sendMessage(Message.translation("server.commands.editprefab.save.noPrefabsSelected"));
                    return;
                }
                this.isSaving = true;
                UICommandBuilder showSavingBuilder = new UICommandBuilder();
                showSavingBuilder.set("#MainPage.Visible", false);
                showSavingBuilder.set("#BrowserPage.Visible", false);
                showSavingBuilder.set("#SavingPage.Visible", true);
                showSavingBuilder.set("#SavingPage #ProgressBar.Value", 0.0f);
                showSavingBuilder.set("#SavingPage #StatusText.TextSpans", Message.translation("server.commands.editprefab.save.saving"));
                showSavingBuilder.set("#SavingPage #ErrorText.Visible", false);
                showSavingBuilder.set("#SavingPage #BackButton.Visible", false);
                this.sendUpdate(showSavingBuilder);
                PrefabSaverSettings prefabSaverSettings = new PrefabSaverSettings();
                prefabSaverSettings.setBlocks(true);
                prefabSaverSettings.setEntities(data.entities);
                prefabSaverSettings.setEmpty(data.empty);
                prefabSaverSettings.setOverwriteExisting(data.overwrite);
                String[] prefabPaths = prefabsToSaveStr.split(",");
                ObjectArrayList prefabsToSave = new ObjectArrayList();
                block15: for (String pathStr : prefabPaths) {
                    if ((pathStr = pathStr.trim()).isEmpty()) continue;
                    for (PrefabEditingMetadata metadata : this.prefabEditSession.getLoadedPrefabMetadata().values()) {
                        String prefabPath = metadata.getPrefabPath().toString().replace('\\', '/');
                        if (!prefabPath.equals(pathStr) && !prefabPath.endsWith(pathStr)) continue;
                        prefabsToSave.add(metadata);
                        continue block15;
                    }
                }
                if (prefabsToSave.isEmpty()) {
                    this.onSavingFailed(Message.translation("server.commands.editprefab.save.noPrefabsFound"));
                    return;
                }
                int readOnlyCount = 0;
                for (PrefabEditingMetadata metadata : prefabsToSave) {
                    if (!metadata.isReadOnly()) continue;
                    ++readOnlyCount;
                }
                if (readOnlyCount > 0) {
                    this.playerRef.sendMessage(Message.translation("server.commands.editprefab.save.readOnlyRedirect").param("count", readOnlyCount));
                }
                World world = store.getExternalData().getWorld();
                int totalPrefabs = prefabsToSave.size();
                CompletableFuture[] saveFutures = new CompletableFuture[totalPrefabs];
                AtomicInteger completedCount = new AtomicInteger(0);
                this.lastProgressUpdateTime = 0L;
                for (int i = 0; i < totalPrefabs; ++i) {
                    PrefabEditingMetadata metadata = (PrefabEditingMetadata)prefabsToSave.get(i);
                    Path savePath = this.getWritableSavePath(metadata);
                    saveFutures[i] = PrefabSaver.savePrefab(playerComponent, world, savePath, metadata.getAnchorPoint(), metadata.getMinPoint(), metadata.getMaxPoint(), metadata.getPastePosition(), metadata.getOriginalFileAnchor(), prefabSaverSettings).thenApply(success -> {
                        int completed = completedCount.incrementAndGet();
                        long now = System.currentTimeMillis();
                        if (now - this.lastProgressUpdateTime >= 100L || completed == totalPrefabs) {
                            this.lastProgressUpdateTime = now;
                            float progress = (float)completed / (float)totalPrefabs;
                            UICommandBuilder progressBuilder = new UICommandBuilder();
                            progressBuilder.set("#SavingPage #ProgressBar.Value", progress);
                            progressBuilder.set("#SavingPage #StatusText.TextSpans", Message.translation("server.commands.editprefab.save.progress").param("current", completed).param("total", totalPrefabs));
                            this.sendUpdate(progressBuilder);
                        }
                        return success;
                    });
                }
                ((CompletableFuture)CompletableFuture.allOf(saveFutures).thenAccept(unused -> {
                    int successes = 0;
                    int failures = 0;
                    for (CompletableFuture future : saveFutures) {
                        if (((Boolean)future.join()).booleanValue()) {
                            ++successes;
                            continue;
                        }
                        ++failures;
                    }
                    this.isSaving = false;
                    if (failures == 0) {
                        this.playerRef.sendMessage(Message.translation("server.commands.editprefab.save.saveAll.success").param("successes", successes).param("failures", failures));
                        playerComponent.getPageManager().setPage(ref, store, Page.None);
                    } else {
                        this.onSavingFailed(Message.translation("server.commands.editprefab.save.saveAll.success").param("successes", successes).param("failures", failures));
                    }
                })).exceptionally(throwable -> {
                    this.isSaving = false;
                    ((HytaleLogger.Api)((HytaleLogger.Api)LOGGER.atWarning()).withCause((Throwable)throwable)).log("Error saving prefabs");
                    this.onSavingFailed(Message.raw(throwable.getMessage() != null ? throwable.getMessage() : "Unknown error"));
                    return null;
                });
                break;
            }
            case 1: {
                playerComponent.getPageManager().setPage(ref, store, Page.None);
                break;
            }
            case 2: {
                String allPaths = this.prefabEditSession.getLoadedPrefabMetadata().values().stream().map(m -> m.getPrefabPath().toString().replace('\\', '/')).collect(Collectors.joining(","));
                UICommandBuilder commandBuilder = new UICommandBuilder();
                commandBuilder.set("#MainPage #PrefabsToSave #Input.Value", allPaths);
                this.sendUpdate(commandBuilder);
                break;
            }
            case 3: {
                String editedPaths = this.prefabEditSession.getLoadedPrefabMetadata().values().stream().filter(PrefabEditingMetadata::isDirty).map(m -> m.getPrefabPath().toString().replace('\\', '/')).collect(Collectors.joining(","));
                UICommandBuilder commandBuilder = new UICommandBuilder();
                commandBuilder.set("#MainPage #PrefabsToSave #Input.Value", editedPaths);
                this.sendUpdate(commandBuilder);
                if (!editedPaths.isEmpty()) break;
                this.playerRef.sendMessage(Message.translation("server.commands.editprefab.save.noEditedPrefabs"));
                break;
            }
            case 4: {
                this.browserSearchQuery = "";
                this.selectedPrefabUuids.clear();
                UICommandBuilder commandBuilder = new UICommandBuilder();
                UIEventBuilder eventBuilder = new UIEventBuilder();
                commandBuilder.set("#MainPage.Visible", false);
                commandBuilder.set("#BrowserPage.Visible", true);
                commandBuilder.set("#BrowserPage #SearchInput.Value", "");
                this.buildPrefabList(commandBuilder, eventBuilder);
                this.sendUpdate(commandBuilder, eventBuilder, false);
                break;
            }
            case 5: {
                this.browserSearchQuery = data.browserSearchStr != null ? data.browserSearchStr.trim().toLowerCase() : "";
                UICommandBuilder commandBuilder = new UICommandBuilder();
                UIEventBuilder eventBuilder = new UIEventBuilder();
                this.buildPrefabList(commandBuilder, eventBuilder);
                this.sendUpdate(commandBuilder, eventBuilder, false);
                break;
            }
            case 6: {
                if (data.prefabUuid == null) break;
                try {
                    UUID uuid = UUID.fromString(data.prefabUuid);
                    if (this.selectedPrefabUuids.contains(uuid)) {
                        this.selectedPrefabUuids.remove(uuid);
                    } else {
                        this.selectedPrefabUuids.add(uuid);
                    }
                    UICommandBuilder commandBuilder = new UICommandBuilder();
                    UIEventBuilder eventBuilder = new UIEventBuilder();
                    this.buildPrefabList(commandBuilder, eventBuilder);
                    this.sendUpdate(commandBuilder, eventBuilder, false);
                }
                catch (IllegalArgumentException uuid) {}
                break;
            }
            case 7: {
                this.selectedPrefabUuids.clear();
                for (PrefabEditingMetadata metadata : this.prefabEditSession.getLoadedPrefabMetadata().values()) {
                    this.selectedPrefabUuids.add(metadata.getUuid());
                }
                UICommandBuilder commandBuilder = new UICommandBuilder();
                UIEventBuilder eventBuilder = new UIEventBuilder();
                this.buildPrefabList(commandBuilder, eventBuilder);
                this.sendUpdate(commandBuilder, eventBuilder, false);
                break;
            }
            case 8: {
                ObjectArrayList selectedPaths = new ObjectArrayList();
                for (PrefabEditingMetadata metadata : this.prefabEditSession.getLoadedPrefabMetadata().values()) {
                    if (!this.selectedPrefabUuids.contains(metadata.getUuid())) continue;
                    selectedPaths.add(metadata.getPrefabPath().toString().replace('\\', '/'));
                }
                UICommandBuilder commandBuilder = new UICommandBuilder();
                commandBuilder.set("#MainPage #PrefabsToSave #Input.Value", String.join((CharSequence)",", selectedPaths));
                commandBuilder.set("#BrowserPage.Visible", false);
                commandBuilder.set("#MainPage.Visible", true);
                this.sendUpdate(commandBuilder);
                break;
            }
            case 9: {
                UICommandBuilder commandBuilder = new UICommandBuilder();
                commandBuilder.set("#BrowserPage.Visible", false);
                commandBuilder.set("#MainPage.Visible", true);
                this.sendUpdate(commandBuilder);
                break;
            }
            case 10: {
                this.isSaving = false;
                UICommandBuilder commandBuilder = new UICommandBuilder();
                commandBuilder.set("#SavingPage.Visible", false);
                commandBuilder.set("#MainPage.Visible", true);
                this.sendUpdate(commandBuilder);
            }
        }
    }

    private void buildPrefabList(@Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder) {
        List prefabsToDisplay;
        commandBuilder.clear("#BrowserPage #FileList");
        Map<UUID, PrefabEditingMetadata> loadedPrefabs = this.prefabEditSession.getLoadedPrefabMetadata();
        if (loadedPrefabs.isEmpty()) {
            return;
        }
        if (!this.browserSearchQuery.isEmpty()) {
            Object2IntOpenHashMap<PrefabEditingMetadata> matchScores = new Object2IntOpenHashMap<PrefabEditingMetadata>(loadedPrefabs.size());
            for (PrefabEditingMetadata metadata : loadedPrefabs.values()) {
                String fileName = metadata.getPrefabPath().getFileName().toString();
                String baseName = fileName.endsWith(".prefab.json") ? fileName.substring(0, fileName.length() - ".prefab.json".length()) : fileName;
                int fuzzyDistance = StringCompareUtil.getFuzzyDistance(baseName, this.browserSearchQuery, Locale.ENGLISH);
                if (fuzzyDistance <= 0) continue;
                matchScores.put(metadata, fuzzyDistance);
            }
            prefabsToDisplay = matchScores.keySet().stream().sorted(Comparator.comparingInt(matchScores::getInt).reversed()).collect(Collectors.toList());
        } else {
            prefabsToDisplay = loadedPrefabs.values().stream().sorted(Comparator.comparing(m -> m.getPrefabPath().getFileName().toString().toLowerCase())).collect(Collectors.toList());
        }
        int buttonIndex = 0;
        for (PrefabEditingMetadata metadata : prefabsToDisplay) {
            Path prefabPath = metadata.getPrefabPath();
            String fileName = prefabPath.getFileName().toString();
            String displayName = fileName.endsWith(".prefab.json") ? fileName.substring(0, fileName.length() - ".prefab.json".length()) : fileName;
            boolean isSelected = this.selectedPrefabUuids.contains(metadata.getUuid());
            boolean isReadOnly = metadata.isReadOnly();
            String checkPrefix = isSelected ? "[x] " : "[ ] ";
            String readOnlySuffix = isReadOnly ? " (read-only)" : "";
            String displayText = checkPrefix + displayName + readOnlySuffix;
            String relativePath = prefabPath.toString().replace('\\', '/');
            Object tooltipText = isReadOnly ? relativePath + "\n(Will save to Server/Prefabs)" : relativePath;
            commandBuilder.append("#BrowserPage #FileList", "Pages/BasicTextButton.ui");
            commandBuilder.set("#BrowserPage #FileList[" + buttonIndex + "].Text", displayText);
            commandBuilder.set("#BrowserPage #FileList[" + buttonIndex + "].TooltipText", (String)tooltipText);
            if (isSelected) {
                commandBuilder.set("#BrowserPage #FileList[" + buttonIndex + "].Style", BUTTON_SELECTED);
            }
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#BrowserPage #FileList[" + buttonIndex + "]", new EventData().append("Action", Action.BrowserTogglePrefab.name()).append("PrefabUuid", metadata.getUuid().toString()));
            ++buttonIndex;
        }
    }

    @Nonnull
    private Path getWritableSavePath(@Nonnull PrefabEditingMetadata metadata) {
        if (!metadata.isReadOnly()) {
            return metadata.getPrefabPath();
        }
        Path originalPath = metadata.getPrefabPath();
        String fileName = originalPath.getFileName().toString();
        Path parent = originalPath.getParent();
        if (parent != null && parent.getFileName() != null) {
            String parentName = parent.getFileName().toString();
            return PrefabStore.get().getServerPrefabsPath().resolve(parentName).resolve(fileName);
        }
        return PrefabStore.get().getServerPrefabsPath().resolve(fileName);
    }

    private void onSavingFailed(@Nonnull Message errorMessage) {
        this.isSaving = false;
        UICommandBuilder builder = new UICommandBuilder();
        builder.set("#SavingPage #ProgressBar.Value", 0.0f);
        builder.set("#SavingPage #StatusText.TextSpans", Message.translation("server.commands.editprefab.save.error"));
        builder.set("#SavingPage #ErrorText.Visible", true);
        builder.set("#SavingPage #ErrorText.TextSpans", errorMessage);
        builder.set("#SavingPage #BackButton.Visible", true);
        this.sendUpdate(builder);
    }

    protected static class PageData {
        public static final String PREFABS_TO_SAVE = "@PrefabsToSave";
        public static final String ENTITIES = "@Entities";
        public static final String EMPTY = "@Empty";
        public static final String OVERWRITE = "@Overwrite";
        public static final String BROWSER_SEARCH = "@BrowserSearch";
        public static final String PREFAB_UUID = "PrefabUuid";
        public static final BuilderCodec<PageData> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(PageData.class, PageData::new).append(new KeyedCodec<Action>("Action", new EnumCodec<Action>(Action.class, EnumCodec.EnumStyle.LEGACY)), (o, action) -> {
            o.action = action;
        }, o -> o.action).add()).append(new KeyedCodec<String>("@PrefabsToSave", Codec.STRING), (o, prefabsToSave) -> {
            o.prefabsToSave = prefabsToSave;
        }, o -> o.prefabsToSave).add()).append(new KeyedCodec<Boolean>("@Entities", Codec.BOOLEAN), (o, entities) -> {
            o.entities = entities;
        }, o -> o.entities).add()).append(new KeyedCodec<Boolean>("@Empty", Codec.BOOLEAN), (o, empty) -> {
            o.empty = empty;
        }, o -> o.empty).add()).append(new KeyedCodec<Boolean>("@Overwrite", Codec.BOOLEAN), (o, overwrite) -> {
            o.overwrite = overwrite;
        }, o -> o.overwrite).add()).append(new KeyedCodec<String>("@BrowserSearch", Codec.STRING), (o, browserSearchStr) -> {
            o.browserSearchStr = browserSearchStr;
        }, o -> o.browserSearchStr).add()).append(new KeyedCodec<String>("PrefabUuid", Codec.STRING), (o, prefabUuid) -> {
            o.prefabUuid = prefabUuid;
        }, o -> o.prefabUuid).add()).build();
        public Action action;
        public String prefabsToSave;
        public boolean entities = true;
        public boolean empty = false;
        public boolean overwrite = true;
        public String browserSearchStr;
        public String prefabUuid;
    }

    public static enum Action {
        Save,
        Cancel,
        SelectAll,
        SelectEdited,
        OpenBrowser,
        BrowserSearch,
        BrowserTogglePrefab,
        BrowserSelectAll,
        ConfirmBrowser,
        CancelBrowser,
        BackFromSaving;

    }
}

