/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.prefabeditor.ui;

import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabEditSessionManager;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabEditorCreationSettings;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabLoadingState;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.commands.PrefabEditLoadCommand;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.enums.PrefabAlignment;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.enums.PrefabRootDirectory;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.enums.PrefabRowSplitMode;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.enums.PrefabStackingAxis;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.enums.WorldGenType;
import com.hypixel.hytale.builtin.buildertools.prefablist.AssetPrefabFileProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.common.util.PathUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.modules.singleplayer.SingleplayerModule;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.ui.DropdownEntryInfo;
import com.hypixel.hytale.server.core.ui.LocalizableString;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.browser.FileListProvider;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrefabEditorLoadSettingsPage
extends InteractiveCustomUIPage<PageData> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final Value<String> BUTTON_HIGHLIGHTED = Value.ref("Pages/BasicTextButton.ui", "SelectedLabelStyle");
    private static final String ASSETS_ROOT_KEY = "Assets";
    private final List<DropdownEntryInfo> savedConfigsDropdown = new ObjectArrayList<DropdownEntryInfo>();
    private volatile boolean isLoading;
    private volatile boolean loadingCancelled;
    private volatile boolean isShuttingDown;
    private PrefabLoadingState currentLoadingState;
    private String loadingWorldName;
    private Path browserRoot;
    private Path browserCurrent;
    private String selectedPath;
    @Nonnull
    private String browserSearchQuery = "";
    private final List<String> selectedItems = new ObjectArrayList<String>();
    @Nonnull
    private final AssetPrefabFileProvider assetProvider = new AssetPrefabFileProvider();
    private boolean inAssetsRoot = false;
    @Nonnull
    private Path assetsCurrentDir = Paths.get("", new String[0]);

    public PrefabEditorLoadSettingsPage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, PageData.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> store) {
        commandBuilder.append("Pages/PrefabEditorSettings.ui");
        this.savedConfigsDropdown.add(new DropdownEntryInfo(LocalizableString.fromMessageId("server.commands.editprefab.ui.savedConfigs.noneSelected"), ""));
        for (String string : PrefabEditorCreationSettings.getAssetMap().getAssetMap().keySet()) {
            this.savedConfigsDropdown.add(new DropdownEntryInfo(LocalizableString.fromString(string), string));
        }
        commandBuilder.set("#SavedConfigs #Input.Entries", this.savedConfigsDropdown);
        commandBuilder.set("#SavedConfigs #Input.Value", "");
        ObjectArrayList<DropdownEntryInfo> rootDirectoryDropdown = new ObjectArrayList<DropdownEntryInfo>();
        for (PrefabRootDirectory value : PrefabRootDirectory.values()) {
            if (value == PrefabRootDirectory.WORLDGEN) continue;
            rootDirectoryDropdown.add(new DropdownEntryInfo(LocalizableString.fromMessageId(value.getLocalizationString()), value.name()));
        }
        commandBuilder.set("#MainPage #RootDir #Input.Entries", rootDirectoryDropdown);
        commandBuilder.set("#MainPage #RootDir #Input.Value", PrefabEditLoadCommand.DEFAULT_PREFAB_ROOT_DIRECTORY.name());
        ObjectArrayList<DropdownEntryInfo> objectArrayList = new ObjectArrayList<DropdownEntryInfo>();
        for (WorldGenType value : WorldGenType.values()) {
            objectArrayList.add(new DropdownEntryInfo(LocalizableString.fromMessageId(value.getLocalizationString()), value.name()));
        }
        commandBuilder.set("#MainPage #WorldGenType #Input.Entries", objectArrayList);
        commandBuilder.set("#MainPage #WorldGenType #Input.Value", PrefabEditLoadCommand.DEFAULT_WORLD_GEN_TYPE.name());
        ObjectArrayList environmentDropdown = new ObjectArrayList();
        Environment.getAssetMap().getAssetMap().keySet().stream().sorted().forEach(envId -> environmentDropdown.add(new DropdownEntryInfo(LocalizableString.fromString(envId), (String)envId)));
        commandBuilder.set("#MainPage #Environment #Input.Entries", environmentDropdown);
        commandBuilder.set("#MainPage #Environment #Input.Value", "Env_Zone1_Plains");
        commandBuilder.set("#MainPage #GrassTint #Input.Color", "#5B9E28");
        ObjectArrayList<DropdownEntryInfo> axisToPasteOnDropdown = new ObjectArrayList<DropdownEntryInfo>();
        for (PrefabStackingAxis value : PrefabStackingAxis.values()) {
            axisToPasteOnDropdown.add(new DropdownEntryInfo(LocalizableString.fromString(value.name()), value.name()));
        }
        commandBuilder.set("#MainPage #PasteAxis #Input.Entries", axisToPasteOnDropdown);
        commandBuilder.set("#MainPage #PasteAxis #Input.Value", PrefabEditLoadCommand.DEFAULT_PREFAB_STACKING_AXIS.name());
        ObjectArrayList<DropdownEntryInfo> alignmentMethodDropdown = new ObjectArrayList<DropdownEntryInfo>();
        for (PrefabAlignment value : PrefabAlignment.values()) {
            alignmentMethodDropdown.add(new DropdownEntryInfo(LocalizableString.fromMessageId(value.getLocalizationString()), value.name()));
        }
        commandBuilder.set("#MainPage #AlignmentMethod #Input.Entries", alignmentMethodDropdown);
        commandBuilder.set("#MainPage #AlignmentMethod #Input.Value", PrefabEditLoadCommand.DEFAULT_PREFAB_ALIGNMENT.name());
        ObjectArrayList<DropdownEntryInfo> rowSplitModeDropdown = new ObjectArrayList<DropdownEntryInfo>();
        for (PrefabRowSplitMode value : PrefabRowSplitMode.values()) {
            rowSplitModeDropdown.add(new DropdownEntryInfo(LocalizableString.fromMessageId(value.getLocalizationString()), value.name()));
        }
        commandBuilder.set("#MainPage #RowSplitMode #Input.Entries", rowSplitModeDropdown);
        commandBuilder.set("#MainPage #RowSplitMode #Input.Value", PrefabEditLoadCommand.DEFAULT_ROW_SPLIT_MODE.name());
        commandBuilder.set("#MainPage #DesiredYLevel #Input.Value", 55);
        commandBuilder.set("#MainPage #BlocksBetweenPrefabs #Input.Value", 15);
        commandBuilder.set("#MainPage #NumAirBeforeGround #Input.Value", 0);
        commandBuilder.set("#MainPage #EnableWorldTicking #CheckBox.Value", false);
        commandBuilder.set("#MainPage #Children.Visible", false);
        commandBuilder.set("#LoadingPage.Visible", false);
        commandBuilder.set("#LoadingPage #ProgressBar.Value", 0.0f);
        commandBuilder.set("#LoadingPage #StatusText.TextSpans", Message.translation("server.commands.editprefab.loading.phase.initializing"));
        commandBuilder.set("#LoadingPage #ErrorText.Visible", false);
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#MainPage #LoadButton", new EventData().append("Action", Action.Load.name()).append("@RootDir", "#MainPage #RootDir #Input.Value").append("@PrefabPaths", "#MainPage #PrefabPaths #Input.Value").append("@Recursive", "#MainPage #Recursive #CheckBox.Value").append("@Children", "#MainPage #Children #CheckBox.Value").append("@Entities", "#MainPage #Entities #CheckBox.Value").append("@EnableWorldTicking", "#MainPage #EnableWorldTicking #CheckBox.Value").append("@DesiredYLevel", "#MainPage #DesiredYLevel #Input.Value").append("@BlocksBetweenPrefabs", "#MainPage #BlocksBetweenPrefabs #Input.Value").append("@WorldGenType", "#MainPage #WorldGenType #Input.Value").append("@Environment", "#MainPage #Environment #Input.Value").append("@GrassTint", "#MainPage #GrassTint #Input.Color").append("@PasteAxis", "#MainPage #PasteAxis #Input.Value").append("@NumAirBeforeGround", "#MainPage #NumAirBeforeGround #Input.Value").append("@AlignmentMethod", "#MainPage #AlignmentMethod #Input.Value").append("@RowSplitMode", "#MainPage #RowSplitMode #Input.Value"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#MainPage #SavedConfigs #Input", new EventData().append("Action", Action.ApplySavedProperties.name()).append("@ConfigName", "#MainPage #SavedConfigs #Input.Value"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#MainPage #CancelButton", new EventData().append("Action", Action.Cancel.name()));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#MainPage #SavePropertiesButton", new EventData().append("Action", Action.OpenSavePropertiesDialog.name()));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#SaveConfigPage #CancelButton", new EventData().append("Action", Action.CancelSavePropertiesDialog.name()));
        eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#SaveConfigPage #SaveName #Input", new EventData().append("Action", Action.SavePropertiesNameChanged.name()).append("@ConfigName", "#SaveConfigPage #SaveName #Input.Value"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#SaveConfigPage #SavePropertiesButton", new EventData().append("Action", Action.SavePropertiesConfig.name()).append("@ConfigName", "#SaveConfigPage #SaveName #Input.Value").append("@RootDir", "#MainPage #RootDir #Input.Value").append("@PrefabPaths", "#MainPage #PrefabPaths #Input.Value").append("@Recursive", "#MainPage #Recursive #CheckBox.Value").append("@Children", "#MainPage #Children #CheckBox.Value").append("@Entities", "#MainPage #Entities #CheckBox.Value").append("@EnableWorldTicking", "#MainPage #EnableWorldTicking #CheckBox.Value").append("@DesiredYLevel", "#MainPage #DesiredYLevel #Input.Value").append("@BlocksBetweenPrefabs", "#MainPage #BlocksBetweenPrefabs #Input.Value").append("@WorldGenType", "#MainPage #WorldGenType #Input.Value").append("@Environment", "#MainPage #Environment #Input.Value").append("@GrassTint", "#MainPage #GrassTint #Input.Color").append("@PasteAxis", "#MainPage #PasteAxis #Input.Value").append("@NumAirBeforeGround", "#MainPage #NumAirBeforeGround #Input.Value").append("@AlignmentMethod", "#MainPage #AlignmentMethod #Input.Value").append("@RowSplitMode", "#MainPage #RowSplitMode #Input.Value"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#LoadingPage #CancelButton", new EventData().append("Action", Action.CancelLoading.name()));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#MainPage #PrefabPaths #BrowseButton", new EventData().append("Action", Action.OpenBrowser.name()));
        eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#BrowserPage #BrowserContent #RootSelector", new EventData().append("Action", Action.BrowserRootChanged.name()).append("@BrowserRoot", "#BrowserPage #BrowserContent #RootSelector.Value"), false);
        eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#BrowserPage #BrowserContent #SearchInput", new EventData().append("Action", Action.BrowserSearch.name()).append("@BrowserSearch", "#BrowserPage #BrowserContent #SearchInput.Value"), false);
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#BrowserPage #AddToListButton", new EventData().append("Action", Action.AddFolderToList.name()));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#BrowserPage #ConfirmButton", new EventData().append("Action", Action.ConfirmBrowser.name()));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#BrowserPage #CancelButton", new EventData().append("Action", Action.CancelBrowser.name()));
        commandBuilder.set("#BrowserPage.Visible", false);
        commandBuilder.set("#BrowserPage #SelectedSection #SelectedItems.Value", "");
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull PageData data) {
        Player playerComponent = store.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        PlayerRef playerRefComponent = store.getComponent(ref, PlayerRef.getComponentType());
        assert (playerRefComponent != null);
        switch (data.uiAction.ordinal()) {
            case 0: {
                if (this.isLoading || this.isShuttingDown) {
                    return;
                }
                this.isLoading = true;
                this.loadingCancelled = false;
                this.currentLoadingState = new PrefabLoadingState();
                this.loadingWorldName = "prefabEditor-" + playerRefComponent.getUsername();
                UICommandBuilder showLoadingBuilder = new UICommandBuilder();
                showLoadingBuilder.set("#MainPage.Visible", false);
                showLoadingBuilder.set("#SaveConfigPage.Visible", false);
                showLoadingBuilder.set("#LoadingPage.Visible", true);
                showLoadingBuilder.set("#LoadingPage #ProgressBar.Value", 0.0f);
                showLoadingBuilder.set("#LoadingPage #StatusText.TextSpans", Message.translation("server.commands.editprefab.loading.phase.initializing"));
                showLoadingBuilder.set("#LoadingPage #ErrorText.Visible", false);
                showLoadingBuilder.set("#LoadingPage #CancelButton.Visible", true);
                this.sendUpdate(showLoadingBuilder);
                this.playerRef.sendMessage(Message.translation("server.commands.editprefab.loading"));
                CompletableFuture<Void> result = BuilderToolsPlugin.get().getPrefabEditSessionManager().loadPrefabAndCreateEditSession(ref, playerComponent, data.toCreationSettings(), store, this::onLoadingProgress);
                if (result == null) {
                    this.onLoadingFailed(Message.translation("server.commands.editprefab.error.failedToStart"));
                    return;
                }
                result.whenComplete((unused, throwable) -> {
                    if (this.loadingCancelled) {
                        return;
                    }
                    if (throwable != null) {
                        this.onLoadingFailed(Message.raw(throwable.getMessage() != null ? throwable.getMessage() : "Unknown error"));
                    } else if (this.currentLoadingState != null && this.currentLoadingState.hasErrors()) {
                        this.onLoadingFailed(this.currentLoadingState.getStatusMessage());
                    } else {
                        this.isLoading = false;
                        this.loadingWorldName = null;
                        this.currentLoadingState = null;
                        playerComponent.getPageManager().setPage(ref, store, Page.ContentCreation);
                    }
                });
                break;
            }
            case 1: {
                UICommandBuilder commandBuilder = new UICommandBuilder();
                commandBuilder.set("#MainPage.Visible", false);
                commandBuilder.set("#SaveConfigPage.Visible", true);
                this.sendUpdate(commandBuilder);
                break;
            }
            case 2: {
                UICommandBuilder commandBuilder = new UICommandBuilder();
                commandBuilder.set("#MainPage.Visible", true);
                commandBuilder.set("#SaveConfigPage.Visible", false);
                this.sendUpdate(commandBuilder);
                break;
            }
            case 7: {
                UICommandBuilder builder = new UICommandBuilder();
                builder.set("#SaveConfigPage #Buttons #SavePropertiesButton.Disabled", data.configName.isBlank());
                this.sendUpdate(builder);
                break;
            }
            case 3: {
                PrefabEditorCreationSettings.save(data.configName, data.toCreationSettings()).thenRun(() -> {
                    UICommandBuilder builder = new UICommandBuilder();
                    builder.set("#MainPage.Visible", true);
                    builder.set("#SaveConfigPage.Visible", false);
                    builder.set("#SaveConfigPage #Buttons.Visible", true);
                    builder.set("#SaveConfigPage #SaveName #Input.Value", "");
                    this.savedConfigsDropdown.add(new DropdownEntryInfo(LocalizableString.fromString(data.configName), data.configName));
                    builder.set("#SavedConfigs #Input.Entries", this.savedConfigsDropdown);
                    builder.set("#SavedConfigs #Input.Value", data.configName);
                    this.sendUpdate(builder);
                });
                break;
            }
            case 4: {
                if (data.configName == null || data.configName.isBlank()) {
                    UICommandBuilder builder = new UICommandBuilder();
                    builder.set("#MainPage #RootDir #Input.Value", PrefabEditLoadCommand.DEFAULT_PREFAB_ROOT_DIRECTORY.name());
                    builder.set("#MainPage #PrefabPaths #Input.Value", "");
                    builder.set("#MainPage #Recursive #CheckBox.Value", false);
                    builder.set("#MainPage #Children #CheckBox.Value", false);
                    builder.set("#MainPage #Entities #CheckBox.Value", false);
                    builder.set("#MainPage #EnableWorldTicking #CheckBox.Value", false);
                    builder.set("#MainPage #DesiredYLevel #Input.Value", 55);
                    builder.set("#MainPage #BlocksBetweenPrefabs #Input.Value", 15);
                    builder.set("#MainPage #WorldGenType #Input.Value", PrefabEditLoadCommand.DEFAULT_WORLD_GEN_TYPE.name());
                    builder.set("#MainPage #Environment #Input.Value", "Env_Zone1_Plains");
                    builder.set("#MainPage #GrassTint #Input.Color", "#5B9E28");
                    builder.set("#MainPage #NumAirBeforeGround #Input.Value", 0);
                    builder.set("#MainPage #PasteAxis #Input.Value", PrefabEditLoadCommand.DEFAULT_PREFAB_STACKING_AXIS.name());
                    builder.set("#MainPage #AlignmentMethod #Input.Value", PrefabEditLoadCommand.DEFAULT_PREFAB_ALIGNMENT.name());
                    builder.set("#MainPage #RowSplitMode #Input.Value", PrefabEditLoadCommand.DEFAULT_ROW_SPLIT_MODE.name());
                    this.sendUpdate(builder);
                    return;
                }
                PrefabEditorCreationSettings.load(data.configName).thenAccept(settings -> {
                    if (settings == null) {
                        return;
                    }
                    UICommandBuilder builder = new UICommandBuilder();
                    builder.set("#MainPage #RootDir #Input.Value", settings.getPrefabRootDirectory().name());
                    builder.set("#MainPage #PrefabPaths #Input.Value", String.join((CharSequence)",", settings.getUnprocessedPrefabPaths()));
                    builder.set("#MainPage #Recursive #CheckBox.Value", settings.isRecursive());
                    builder.set("#MainPage #Children #CheckBox.Value", settings.isLoadChildren());
                    builder.set("#MainPage #Entities #CheckBox.Value", settings.shouldLoadEntities());
                    builder.set("#MainPage #EnableWorldTicking #CheckBox.Value", settings.isWorldTickingEnabled());
                    builder.set("#MainPage #DesiredYLevel #Input.Value", settings.getPasteYLevelGoal());
                    builder.set("#MainPage #BlocksBetweenPrefabs #Input.Value", settings.getBlocksBetweenEachPrefab());
                    builder.set("#MainPage #WorldGenType #Input.Value", settings.getWorldGenType().name());
                    builder.set("#MainPage #Environment #Input.Value", settings.getEnvironment());
                    builder.set("#MainPage #GrassTint #Input.Color", settings.getGrassTint());
                    builder.set("#MainPage #NumAirBeforeGround #Input.Value", settings.getBlocksAboveSurface());
                    builder.set("#MainPage #PasteAxis #Input.Value", settings.getStackingAxis().name());
                    builder.set("#MainPage #AlignmentMethod #Input.Value", settings.getAlignment().name());
                    builder.set("#MainPage #RowSplitMode #Input.Value", settings.getRowSplitMode().name());
                    this.sendUpdate(builder);
                });
                break;
            }
            case 5: {
                playerComponent.getPageManager().setPage(ref, store, Page.None);
                break;
            }
            case 6: {
                if (this.isShuttingDown) {
                    return;
                }
                this.loadingCancelled = true;
                this.isLoading = false;
                this.isShuttingDown = true;
                UICommandBuilder cancellingBuilder = new UICommandBuilder();
                cancellingBuilder.set("#LoadingPage #CancelButton.Disabled", true);
                cancellingBuilder.set("#LoadingPage #StatusText.TextSpans", Message.translation("server.commands.editprefab.loading.phase.cancelling"));
                cancellingBuilder.set("#LoadingPage #ProgressBar.Value", 0.1f);
                cancellingBuilder.set("#LoadingPage #ErrorText.Visible", false);
                this.sendUpdate(cancellingBuilder);
                PrefabEditSessionManager sessionManager = BuilderToolsPlugin.get().getPrefabEditSessionManager();
                if (this.loadingWorldName != null) {
                    String worldNameToClean = this.loadingWorldName;
                    this.loadingWorldName = null;
                    sessionManager.cleanupCancelledSession(this.playerRef.getUuid(), worldNameToClean, this::onShutdownProgress).whenComplete((unused, throwable) -> {
                        this.isShuttingDown = false;
                        this.currentLoadingState = null;
                        UICommandBuilder builder = new UICommandBuilder();
                        builder.set("#LoadingPage.Visible", false);
                        builder.set("#LoadingPage #CancelButton.Disabled", false);
                        builder.set("#MainPage.Visible", true);
                        this.sendUpdate(builder);
                        if (throwable != null) {
                            this.playerRef.sendMessage(Message.translation("server.commands.editprefab.error.shutdownFailed"));
                        }
                    });
                    break;
                }
                this.isShuttingDown = false;
                this.currentLoadingState = null;
                UICommandBuilder builder = new UICommandBuilder();
                builder.set("#LoadingPage.Visible", false);
                builder.set("#MainPage.Visible", true);
                this.sendUpdate(builder);
                break;
            }
            case 8: {
                this.inAssetsRoot = true;
                this.assetsCurrentDir = Paths.get("", new String[0]);
                this.browserRoot = Paths.get(ASSETS_ROOT_KEY, new String[0]);
                this.browserCurrent = Paths.get("", new String[0]);
                this.selectedPath = null;
                this.browserSearchQuery = "";
                this.selectedItems.clear();
                UICommandBuilder commandBuilder = new UICommandBuilder();
                UIEventBuilder eventBuilder = new UIEventBuilder();
                commandBuilder.set("#MainPage.Visible", false);
                commandBuilder.set("#BrowserPage.Visible", true);
                List<DropdownEntryInfo> roots = this.buildBrowserRootEntries();
                commandBuilder.set("#BrowserPage #BrowserContent #RootSelector.Entries", roots);
                commandBuilder.set("#BrowserPage #BrowserContent #RootSelector.Value", ASSETS_ROOT_KEY);
                commandBuilder.set("#BrowserPage #BrowserContent #SearchInput.Value", "");
                commandBuilder.set("#BrowserPage #SelectedSection #SelectedItems.Value", "");
                this.buildBrowserList(commandBuilder, eventBuilder);
                this.sendUpdate(commandBuilder, eventBuilder, false);
                break;
            }
            case 9: {
                if (data.browserFile == null) {
                    return;
                }
                String fileName = data.browserFile;
                if (this.inAssetsRoot) {
                    this.handleAssetsNavigation(fileName);
                    break;
                }
                this.handleRegularNavigation(fileName);
                break;
            }
            case 10: {
                if (data.browserRootStr == null) {
                    return;
                }
                if (!this.isAllowedBrowserRoot(data.browserRootStr)) {
                    return;
                }
                this.inAssetsRoot = ASSETS_ROOT_KEY.equals(data.browserRootStr);
                this.assetsCurrentDir = Paths.get("", new String[0]);
                if (this.inAssetsRoot) {
                    this.browserRoot = Paths.get(ASSETS_ROOT_KEY, new String[0]);
                    this.browserCurrent = Paths.get("", new String[0]);
                } else {
                    this.browserRoot = this.findActualRootPath(data.browserRootStr);
                    if (this.browserRoot == null) {
                        this.browserRoot = Path.of(data.browserRootStr, new String[0]);
                    }
                    this.browserCurrent = this.browserRoot.getFileSystem().getPath("", new String[0]);
                }
                this.selectedPath = null;
                this.browserSearchQuery = "";
                this.selectedItems.clear();
                UICommandBuilder commandBuilder = new UICommandBuilder();
                UIEventBuilder eventBuilder = new UIEventBuilder();
                commandBuilder.set("#BrowserPage #BrowserContent #SearchInput.Value", "");
                commandBuilder.set("#BrowserPage #SelectedSection #SelectedItems.Value", "");
                PrefabRootDirectory rootDirValue = this.getRootDirectoryForPath(data.browserRootStr);
                if (rootDirValue != null) {
                    commandBuilder.set("#MainPage #RootDir #Input.Value", rootDirValue.name());
                }
                this.buildBrowserList(commandBuilder, eventBuilder);
                this.sendUpdate(commandBuilder, eventBuilder, false);
                break;
            }
            case 11: {
                this.browserSearchQuery = data.browserSearchStr != null ? data.browserSearchStr.trim().toLowerCase() : "";
                UICommandBuilder commandBuilder = new UICommandBuilder();
                UIEventBuilder eventBuilder = new UIEventBuilder();
                this.buildBrowserList(commandBuilder, eventBuilder);
                this.sendUpdate(commandBuilder, eventBuilder, false);
                break;
            }
            case 12: {
                String pathToAdd = this.getCurrentBrowserPath();
                if (!pathToAdd.isEmpty() && !this.selectedItems.contains(pathToAdd)) {
                    this.selectedItems.add(pathToAdd);
                }
                UICommandBuilder commandBuilder = new UICommandBuilder();
                commandBuilder.set("#BrowserPage #SelectedSection #SelectedItems.Value", String.join((CharSequence)"\n", this.selectedItems));
                this.sendUpdate(commandBuilder);
                break;
            }
            case 13: {
                PrefabRootDirectory rootDirValue;
                String pathsToSet = !this.selectedItems.isEmpty() ? String.join((CharSequence)",", this.selectedItems) : this.getCurrentBrowserPath();
                UICommandBuilder commandBuilder = new UICommandBuilder();
                commandBuilder.set("#MainPage #PrefabPaths #Input.Value", pathsToSet);
                PrefabRootDirectory prefabRootDirectory = rootDirValue = this.inAssetsRoot ? PrefabRootDirectory.ASSET : this.getRootDirectoryForPath(this.browserRoot.toString());
                if (rootDirValue != null) {
                    commandBuilder.set("#MainPage #RootDir #Input.Value", rootDirValue.name());
                }
                commandBuilder.set("#BrowserPage.Visible", false);
                commandBuilder.set("#MainPage.Visible", true);
                this.sendUpdate(commandBuilder);
                break;
            }
            case 14: {
                UICommandBuilder commandBuilder = new UICommandBuilder();
                commandBuilder.set("#BrowserPage.Visible", false);
                commandBuilder.set("#MainPage.Visible", true);
                this.sendUpdate(commandBuilder);
            }
        }
    }

    private void onLoadingProgress(@Nonnull PrefabLoadingState state) {
        if (this.loadingCancelled) {
            return;
        }
        this.currentLoadingState = state;
        UICommandBuilder builder = new UICommandBuilder();
        builder.set("#LoadingPage #ProgressBar.Value", state.getProgressPercentage());
        builder.set("#LoadingPage #StatusText.TextSpans", state.getStatusMessage());
        if (state.hasErrors()) {
            builder.set("#LoadingPage #ErrorText.Visible", true);
            builder.set("#LoadingPage #ErrorText.TextSpans", state.getErrors().getLast().toMessage());
            builder.set("#LoadingPage #CancelButton.Visible", true);
        }
        this.sendUpdate(builder);
    }

    private void onLoadingFailed(@Nonnull Message errorMessage) {
        this.isLoading = false;
        UICommandBuilder builder = new UICommandBuilder();
        builder.set("#LoadingPage #ProgressBar.Value", 0.0f);
        builder.set("#LoadingPage #StatusText.TextSpans", Message.translation("server.commands.editprefab.loading.phase.error"));
        builder.set("#LoadingPage #ErrorText.Visible", true);
        builder.set("#LoadingPage #ErrorText.TextSpans", errorMessage);
        builder.set("#LoadingPage #CancelButton.Visible", true);
        this.sendUpdate(builder);
    }

    private void onShutdownProgress(@Nonnull PrefabLoadingState state) {
        UICommandBuilder builder = new UICommandBuilder();
        builder.set("#LoadingPage #ProgressBar.Value", state.getProgressPercentage());
        builder.set("#LoadingPage #StatusText.TextSpans", state.getStatusMessage());
        this.sendUpdate(builder);
    }

    private void handleAssetsNavigation(@Nonnull String fileName) {
        if ("..".equals(fileName)) {
            if (!this.assetsCurrentDir.toString().isEmpty()) {
                Path parent = this.assetsCurrentDir.getParent();
                this.assetsCurrentDir = parent != null ? parent : Paths.get("", new String[0]);
                UICommandBuilder commandBuilder = new UICommandBuilder();
                UIEventBuilder eventBuilder = new UIEventBuilder();
                this.buildBrowserList(commandBuilder, eventBuilder);
                this.sendUpdate(commandBuilder, eventBuilder, false);
            }
            return;
        }
        String currentDirStr = this.assetsCurrentDir.toString().replace('\\', '/');
        Object targetVirtualPath = currentDirStr.isEmpty() ? fileName : currentDirStr + "/" + fileName;
        Path resolvedPath = this.assetProvider.resolveVirtualPath((String)targetVirtualPath);
        if (resolvedPath == null) {
            this.sendUpdate();
            return;
        }
        if (Files.isDirectory(resolvedPath, new LinkOption[0])) {
            this.assetsCurrentDir = Paths.get((String)targetVirtualPath, new String[0]);
            this.selectedPath = (String)targetVirtualPath + "/";
            UICommandBuilder commandBuilder = new UICommandBuilder();
            UIEventBuilder eventBuilder = new UIEventBuilder();
            this.buildBrowserList(commandBuilder, eventBuilder);
            this.sendUpdate(commandBuilder, eventBuilder, false);
        } else {
            this.selectedPath = targetVirtualPath;
            UICommandBuilder commandBuilder = new UICommandBuilder();
            commandBuilder.set("#BrowserPage #CurrentPath.Text", "Assets/" + (String)targetVirtualPath);
            this.sendUpdate(commandBuilder);
        }
    }

    private void handleRegularNavigation(@Nonnull String fileName) {
        Path file = this.browserRoot.resolve(this.browserCurrent).resolve(fileName);
        if (!file.normalize().startsWith(this.browserRoot.normalize())) {
            this.sendUpdate();
            return;
        }
        if (Files.isDirectory(file, new LinkOption[0])) {
            this.browserCurrent = PathUtil.relativize(this.browserRoot, file);
            String pathStr = this.browserCurrent.toString().replace('\\', '/');
            this.selectedPath = pathStr.isEmpty() ? "/" : (pathStr.endsWith("/") ? pathStr : pathStr + "/");
            UICommandBuilder commandBuilder = new UICommandBuilder();
            UIEventBuilder eventBuilder = new UIEventBuilder();
            this.buildBrowserList(commandBuilder, eventBuilder);
            this.sendUpdate(commandBuilder, eventBuilder, false);
        } else {
            this.selectedPath = PathUtil.relativize(this.browserRoot, file).toString().replace('\\', '/');
            UICommandBuilder commandBuilder = new UICommandBuilder();
            commandBuilder.set("#BrowserPage #CurrentPath.Text", this.selectedPath);
            this.sendUpdate(commandBuilder);
        }
    }

    @Nonnull
    private String getCurrentBrowserPath() {
        if (this.selectedPath != null) {
            return this.selectedPath;
        }
        if (this.inAssetsRoot) {
            String currentDirStr = this.assetsCurrentDir.toString().replace('\\', '/');
            return currentDirStr.isEmpty() ? "/" : (currentDirStr.endsWith("/") ? currentDirStr : currentDirStr + "/");
        }
        String pathStr = this.browserCurrent.toString().replace('\\', '/');
        return pathStr.isEmpty() ? "/" : (pathStr.endsWith("/") ? pathStr : pathStr + "/");
    }

    private void buildBrowserList(@Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder) {
        commandBuilder.clear("#BrowserPage #BrowserContent #FileList");
        if (this.inAssetsRoot) {
            this.buildAssetsBrowserList(commandBuilder, eventBuilder);
        } else {
            this.buildRegularBrowserList(commandBuilder, eventBuilder);
        }
    }

    private void buildAssetsBrowserList(@Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder) {
        Object displayPath;
        String currentDirStr = this.assetsCurrentDir.toString().replace('\\', '/');
        if (currentDirStr.isEmpty()) {
            displayPath = ASSETS_ROOT_KEY;
        } else {
            String[] parts = currentDirStr.split("/", 2);
            String packName = parts[0];
            String subPath = parts.length > 1 ? "/" + parts[1] : "";
            displayPath = "HytaleAssets".equals(packName) ? packName + subPath : "Mods/" + packName + subPath;
        }
        commandBuilder.set("#BrowserPage #CurrentPath.Text", (String)displayPath);
        List<FileListProvider.FileEntry> entries = this.assetProvider.getFiles(this.assetsCurrentDir, this.browserSearchQuery);
        int buttonIndex = 0;
        if (!currentDirStr.isEmpty() && this.browserSearchQuery.isEmpty()) {
            commandBuilder.append("#BrowserPage #BrowserContent #FileList", "Pages/BasicTextButton.ui");
            commandBuilder.set("#BrowserPage #BrowserContent #FileList[0].Text", "../");
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#BrowserPage #BrowserContent #FileList[0]", new EventData().append("Action", Action.BrowserNavigate.name()).append("File", ".."));
            ++buttonIndex;
        }
        for (FileListProvider.FileEntry entry : entries) {
            Object displayText = entry.isDirectory() ? entry.displayName() + "/" : entry.displayName();
            commandBuilder.append("#BrowserPage #BrowserContent #FileList", "Pages/BasicTextButton.ui");
            commandBuilder.set("#BrowserPage #BrowserContent #FileList[" + buttonIndex + "].Text", (String)displayText);
            if (!entry.isDirectory()) {
                commandBuilder.set("#BrowserPage #BrowserContent #FileList[" + buttonIndex + "].Style", BUTTON_HIGHLIGHTED);
            }
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#BrowserPage #BrowserContent #FileList[" + buttonIndex + "]", new EventData().append("Action", Action.BrowserNavigate.name()).append("File", entry.name()));
            ++buttonIndex;
        }
    }

    private void buildRegularBrowserList(@Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder) {
        String rootDisplayPath = this.getRootDisplayPath(this.browserRoot);
        String currentPath = this.browserCurrent.toString().replace('\\', '/');
        String currentPathDisplay = currentPath.isEmpty() ? rootDisplayPath : rootDisplayPath + "/" + currentPath;
        commandBuilder.set("#BrowserPage #CurrentPath.Text", currentPathDisplay);
        ObjectArrayList files = new ObjectArrayList();
        Path path = this.browserRoot.resolve(this.browserCurrent);
        if (Files.isDirectory(path, new LinkOption[0])) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path);){
                for (Path path2 : stream) {
                    String fileName = path2.getFileName().toString();
                    if (fileName.charAt(0) == '/') {
                        fileName = fileName.substring(1);
                    }
                    if (!fileName.endsWith(".prefab.json") && !Files.isDirectory(path2, new LinkOption[0]) || !this.browserSearchQuery.isEmpty() && !fileName.toLowerCase().contains(this.browserSearchQuery)) continue;
                    files.add(path2.toFile());
                }
            }
            catch (IOException e) {
                ((HytaleLogger.Api)LOGGER.atSevere()).log("Error reading directory for browser", e);
            }
        }
        files.sort((a, b) -> {
            if (a.isDirectory() == b.isDirectory()) {
                return a.compareTo((File)b);
            }
            return a.isDirectory() ? -1 : 1;
        });
        int buttonIndex = 0;
        if (!this.browserCurrent.toString().isEmpty() && this.browserSearchQuery.isEmpty()) {
            commandBuilder.append("#BrowserPage #BrowserContent #FileList", "Pages/BasicTextButton.ui");
            commandBuilder.set("#BrowserPage #BrowserContent #FileList[0].Text", "../");
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#BrowserPage #BrowserContent #FileList[0]", new EventData().append("Action", Action.BrowserNavigate.name()).append("File", ".."));
            ++buttonIndex;
        }
        for (File file : files) {
            boolean isDirectory = file.isDirectory();
            String fileName = file.getName();
            commandBuilder.append("#BrowserPage #BrowserContent #FileList", "Pages/BasicTextButton.ui");
            commandBuilder.set("#BrowserPage #BrowserContent #FileList[" + buttonIndex + "].Text", (String)(!isDirectory ? fileName : fileName + "/"));
            if (!isDirectory) {
                commandBuilder.set("#BrowserPage #BrowserContent #FileList[" + buttonIndex + "].Style", BUTTON_HIGHLIGHTED);
            }
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#BrowserPage #BrowserContent #FileList[" + buttonIndex + "]", new EventData().append("Action", Action.BrowserNavigate.name()).append("File", fileName));
            ++buttonIndex;
        }
    }

    @Nonnull
    private List<DropdownEntryInfo> buildBrowserRootEntries() {
        ObjectArrayList<DropdownEntryInfo> roots = new ObjectArrayList<DropdownEntryInfo>();
        roots.add(new DropdownEntryInfo(LocalizableString.fromString(ASSETS_ROOT_KEY), ASSETS_ROOT_KEY));
        roots.add(new DropdownEntryInfo(LocalizableString.fromString("Server"), PrefabStore.get().getServerPrefabsPath().toString()));
        return roots;
    }

    @Nullable
    private Path findActualRootPath(@Nonnull String pathStr) {
        for (PrefabStore.AssetPackPrefabPath packPath : PrefabStore.get().getAllAssetPrefabPaths()) {
            if (!packPath.prefabsPath().toString().equals(pathStr)) continue;
            return packPath.prefabsPath();
        }
        if (PrefabStore.get().getServerPrefabsPath().toString().equals(pathStr)) {
            return PrefabStore.get().getServerPrefabsPath();
        }
        if (PrefabStore.get().getWorldGenPrefabsPath().toString().equals(pathStr)) {
            return PrefabStore.get().getWorldGenPrefabsPath();
        }
        return null;
    }

    @Nullable
    private AssetPack findAssetPackForPath(@Nonnull String pathStr) {
        Path path = Path.of(pathStr, new String[0]).toAbsolutePath().normalize();
        for (AssetPack pack : AssetModule.get().getAssetPacks()) {
            Path packPrefabsPath = PrefabStore.get().getAssetPrefabsPathForPack(pack).toAbsolutePath().normalize();
            if (!path.equals(packPrefabsPath) && !path.startsWith(packPrefabsPath)) continue;
            return pack;
        }
        return null;
    }

    @Nullable
    private PrefabRootDirectory getRootDirectoryForPath(@Nonnull String pathStr) {
        if (ASSETS_ROOT_KEY.equals(pathStr)) {
            return PrefabRootDirectory.ASSET;
        }
        if (pathStr.equals(PrefabStore.get().getServerPrefabsPath().toString())) {
            return PrefabRootDirectory.SERVER;
        }
        if (pathStr.equals(PrefabStore.get().getWorldGenPrefabsPath().toString())) {
            return PrefabRootDirectory.WORLDGEN;
        }
        if (this.findAssetPackForPath(pathStr) != null) {
            return PrefabRootDirectory.ASSET;
        }
        return null;
    }

    private boolean isAllowedBrowserRoot(@Nonnull String pathStr) {
        if (SingleplayerModule.isOwner(this.playerRef)) {
            return true;
        }
        return this.getRootDirectoryForPath(pathStr) != null;
    }

    @Nonnull
    private String getRootDisplayPath(@Nonnull Path root) {
        String rootStr = root.toString();
        if (rootStr.equals(PrefabStore.get().getServerPrefabsPath().toString())) {
            return "ServerRoot/" + String.valueOf(root.getFileName());
        }
        if (rootStr.equals(PrefabStore.get().getWorldGenPrefabsPath().toString())) {
            Path parent = root.getParent();
            if (parent != null && parent.getFileName() != null) {
                return "WorldgenRoot/" + String.valueOf(parent.getFileName()) + "/" + String.valueOf(root.getFileName());
            }
            return "WorldgenRoot/" + String.valueOf(root.getFileName());
        }
        AssetPack pack = this.findAssetPackForPath(rootStr);
        if (pack != null) {
            String packPrefix = pack.equals(AssetModule.get().getBaseAssetPack()) ? "HytaleAssets" : "[" + pack.getName() + "]";
            Path parent = root.getParent();
            if (parent != null && parent.getFileName() != null) {
                return packPrefix + "/" + String.valueOf(parent.getFileName()) + "/" + String.valueOf(root.getFileName());
            }
            return packPrefix + "/" + String.valueOf(root.getFileName());
        }
        return root.toString();
    }

    protected static class PageData {
        public static final String CONFIG_NAME = "@ConfigName";
        public static final String ROOT_DIR = "@RootDir";
        public static final String PREFAB_PATHS = "@PrefabPaths";
        public static final String RECURSIVE = "@Recursive";
        public static final String CHILDREN = "@Children";
        public static final String ENTITIES = "@Entities";
        public static final String ENABLE_WORLD_TICKING = "@EnableWorldTicking";
        public static final String DESIRED_Y_LEVEL = "@DesiredYLevel";
        public static final String BLOCKS_BETWEEN_PREFABS = "@BlocksBetweenPrefabs";
        public static final String WORLD_GEN_TYPE = "@WorldGenType";
        public static final String ENVIRONMENT = "@Environment";
        public static final String GRASS_TINT = "@GrassTint";
        public static final String NUM_AIR_BEFORE_GROUND = "@NumAirBeforeGround";
        public static final String PASTE_AXIS = "@PasteAxis";
        public static final String ALIGNMENT_METHOD = "@AlignmentMethod";
        public static final String ROW_SPLIT_MODE = "@RowSplitMode";
        public static final String BROWSER_FILE = "File";
        public static final String BROWSER_ROOT = "@BrowserRoot";
        public static final String BROWSER_SEARCH = "@BrowserSearch";
        public static final BuilderCodec<PageData> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(PageData.class, PageData::new).append(new KeyedCodec<Action>("Action", new EnumCodec<Action>(Action.class, EnumCodec.EnumStyle.LEGACY)), (o, uiAction) -> {
            o.uiAction = uiAction;
        }, o -> o.uiAction).add()).append(new KeyedCodec<String>("@ConfigName", Codec.STRING), (o, configName) -> {
            o.configName = configName;
        }, o -> o.configName).add()).append(new KeyedCodec<PrefabRootDirectory>("@RootDir", new EnumCodec<PrefabRootDirectory>(PrefabRootDirectory.class, EnumCodec.EnumStyle.LEGACY)), (o, rootDirectory) -> {
            o.prefabRootDirectory = rootDirectory;
        }, o -> o.prefabRootDirectory).add()).append(new KeyedCodec<String>("@PrefabPaths", Codec.STRING), (o, unprocessedPrefabPaths) -> {
            o.unprocessedPrefabPaths = unprocessedPrefabPaths;
        }, o -> o.unprocessedPrefabPaths).add()).append(new KeyedCodec<Integer>("@DesiredYLevel", Codec.INTEGER), (o, pasteYLevelGoal) -> {
            o.pasteYLevelGoal = pasteYLevelGoal;
        }, o -> o.pasteYLevelGoal).add()).append(new KeyedCodec<Integer>("@BlocksBetweenPrefabs", Codec.INTEGER), (o, blocksBetweenEachPrefab) -> {
            o.blocksBetweenEachPrefab = blocksBetweenEachPrefab;
        }, o -> o.blocksBetweenEachPrefab).add()).append(new KeyedCodec<WorldGenType>("@WorldGenType", new EnumCodec<WorldGenType>(WorldGenType.class, EnumCodec.EnumStyle.LEGACY)), (o, worldGenType) -> {
            o.worldGenType = worldGenType;
        }, o -> o.worldGenType).add()).append(new KeyedCodec<String>("@Environment", Codec.STRING), (o, environment) -> {
            o.environment = environment;
        }, o -> o.environment).add()).append(new KeyedCodec<String>("@GrassTint", Codec.STRING), (o, grassTint) -> {
            o.grassTint = grassTint;
        }, o -> o.grassTint).add()).append(new KeyedCodec<Integer>("@NumAirBeforeGround", Codec.INTEGER), (o, blocksAboveSurface) -> {
            o.blocksAboveSurface = blocksAboveSurface;
        }, o -> o.blocksAboveSurface).add()).append(new KeyedCodec<PrefabStackingAxis>("@PasteAxis", new EnumCodec<PrefabStackingAxis>(PrefabStackingAxis.class, EnumCodec.EnumStyle.LEGACY)), (o, stackingAxis) -> {
            o.stackingAxis = stackingAxis;
        }, o -> o.stackingAxis).add()).append(new KeyedCodec<PrefabAlignment>("@AlignmentMethod", new EnumCodec<PrefabAlignment>(PrefabAlignment.class, EnumCodec.EnumStyle.LEGACY)), (o, alignment) -> {
            o.alignment = alignment;
        }, o -> o.alignment).add()).append(new KeyedCodec<PrefabRowSplitMode>("@RowSplitMode", new EnumCodec<PrefabRowSplitMode>(PrefabRowSplitMode.class, EnumCodec.EnumStyle.LEGACY)), (o, rowSplitMode) -> {
            o.rowSplitMode = rowSplitMode;
        }, o -> o.rowSplitMode).add()).append(new KeyedCodec<Boolean>("@Recursive", Codec.BOOLEAN), (o, recursive) -> {
            o.recursive = recursive;
        }, o -> o.recursive).add()).append(new KeyedCodec<Boolean>("@Children", Codec.BOOLEAN), (o, loadChildren) -> {
            o.loadChildren = loadChildren;
        }, o -> o.loadChildren).add()).append(new KeyedCodec<Boolean>("@Entities", Codec.BOOLEAN), (o, loadEntities) -> {
            o.loadEntities = loadEntities;
        }, o -> o.loadEntities).add()).append(new KeyedCodec<Boolean>("@EnableWorldTicking", Codec.BOOLEAN), (o, enableWorldTicking) -> {
            o.enableWorldTicking = enableWorldTicking;
        }, o -> o.enableWorldTicking).add()).append(new KeyedCodec<String>("File", Codec.STRING), (o, browserFile) -> {
            o.browserFile = browserFile;
        }, o -> o.browserFile).add()).append(new KeyedCodec<String>("@BrowserRoot", Codec.STRING), (o, browserRootStr) -> {
            o.browserRootStr = browserRootStr;
        }, o -> o.browserRootStr).add()).append(new KeyedCodec<String>("@BrowserSearch", Codec.STRING), (o, browserSearchStr) -> {
            o.browserSearchStr = browserSearchStr;
        }, o -> o.browserSearchStr).add()).build();
        public String configName;
        public Action uiAction;
        public PrefabRootDirectory prefabRootDirectory = PrefabRootDirectory.ASSET;
        public String unprocessedPrefabPaths = "";
        public int pasteYLevelGoal = 55;
        public int blocksBetweenEachPrefab = 15;
        public WorldGenType worldGenType = PrefabEditLoadCommand.DEFAULT_WORLD_GEN_TYPE;
        public String environment = "Env_Zone1_Plains";
        public String grassTint = "#5B9E28";
        public int blocksAboveSurface = 0;
        public PrefabStackingAxis stackingAxis = PrefabEditLoadCommand.DEFAULT_PREFAB_STACKING_AXIS;
        public PrefabAlignment alignment = PrefabEditLoadCommand.DEFAULT_PREFAB_ALIGNMENT;
        public PrefabRowSplitMode rowSplitMode = PrefabEditLoadCommand.DEFAULT_ROW_SPLIT_MODE;
        public boolean recursive;
        public boolean loadChildren;
        public boolean loadEntities;
        public boolean enableWorldTicking = false;
        public String browserFile;
        public String browserRootStr;
        public String browserSearchStr;

        @Nonnull
        public PrefabEditorCreationSettings toCreationSettings() {
            String normalizedGrassTint = this.grassTint;
            if (normalizedGrassTint != null && normalizedGrassTint.length() > 7) {
                normalizedGrassTint = normalizedGrassTint.substring(0, 7);
            }
            return new PrefabEditorCreationSettings(this.prefabRootDirectory, List.of(this.unprocessedPrefabPaths.split(",")), this.pasteYLevelGoal, this.blocksBetweenEachPrefab, this.worldGenType, this.blocksAboveSurface, this.stackingAxis, this.alignment, this.recursive, this.loadChildren, this.loadEntities, this.enableWorldTicking, this.rowSplitMode, this.environment, normalizedGrassTint);
        }
    }

    public static enum Action {
        Load,
        OpenSavePropertiesDialog,
        CancelSavePropertiesDialog,
        SavePropertiesConfig,
        ApplySavedProperties,
        Cancel,
        CancelLoading,
        SavePropertiesNameChanged,
        OpenBrowser,
        BrowserNavigate,
        BrowserRootChanged,
        BrowserSearch,
        AddFolderToList,
        ConfirmBrowser,
        CancelBrowser;

    }
}

