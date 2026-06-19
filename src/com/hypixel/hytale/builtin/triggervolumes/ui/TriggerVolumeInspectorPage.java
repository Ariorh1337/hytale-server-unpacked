package com.hypixel.hytale.builtin.triggervolumes.ui;

import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.builtin.triggervolumes.EntityTargetType;
import com.hypixel.hytale.builtin.triggervolumes.TriggerVolumesPlugin;
import com.hypixel.hytale.builtin.triggervolumes.asset.TriggerEffectAsset;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerCondition;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEventType;
import com.hypixel.hytale.builtin.triggervolumes.effect.builtin.PastePrefabEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.builtin.PlayAnimationEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.builtin.TaggedVolumeEffectUtil;
import com.hypixel.hytale.builtin.triggervolumes.manager.ConditionTiming;
import com.hypixel.hytale.builtin.triggervolumes.manager.CooldownMode;
import com.hypixel.hytale.builtin.triggervolumes.manager.GroupEntry;
import com.hypixel.hytale.builtin.triggervolumes.manager.ProjectileSource;
import com.hypixel.hytale.builtin.triggervolumes.manager.RejectionDelayMode;
import com.hypixel.hytale.builtin.triggervolumes.manager.TriggerVolumeManager;
import com.hypixel.hytale.builtin.triggervolumes.manager.VolumeEntry;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.EmptyExtraInfo;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.builder.BuilderField;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.common.util.StringCompareUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.EditorBlocksChange;
import com.hypixel.hytale.protocol.packets.player.HideTriggerVolumePastePrefabPreview;
import com.hypixel.hytale.protocol.packets.player.ShowTriggerVolumePastePrefabPreview;
import com.hypixel.hytale.protocol.packets.player.TriggerVolumeShapeType;
import com.hypixel.hytale.protocol.packets.player.TriggerVolumeToolSelection;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.PrefabListAsset;
import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.asset.util.ColorParseUtil;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.modules.i18n.I18nModule;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.ui.Anchor;
import com.hypixel.hytale.server.core.ui.DropdownEntryInfo;
import com.hypixel.hytale.server.core.ui.LocalizableString;
import com.hypixel.hytale.server.core.ui.PatchStyle;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.browser.AssetPackSaveBrowser;
import com.hypixel.hytale.server.core.ui.browser.AssetPackSaveBrowserConfig;
import com.hypixel.hytale.server.core.ui.browser.AssetPackSaveBrowserEventData;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.BsonUtil;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class TriggerVolumeInspectorPage extends InteractiveCustomUIPage<TriggerVolumeInspectorPage.PageData> {
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   private static final Pattern VALID_ID = Pattern.compile("^[a-zA-Z0-9_]{1,64}$");
   private static final int ASSET_PICKER_MAX_RESULTS = 50;
   private static final Value<String> NORMAL_ROW_STYLE = Value.ref("Pages/TriggerVolume/TriggerVolumeBrowseGroupRow.ui", "NormalRowStyle");
   private static final Value<String> SELECTED_ROW_STYLE = Value.ref("Pages/TriggerVolume/TriggerVolumeBrowseGroupRow.ui", "SelectedRowStyle");
   private static final Value<String> NORMAL_EFFECT_ROW_STYLE = Value.ref("Pages/TriggerVolume/TriggerVolumeInspectorEffectRow.ui", "NormalRowStyle");
   private static final Value<String> SELECTED_EFFECT_ROW_STYLE = Value.ref("Pages/TriggerVolume/TriggerVolumeInspectorEffectRow.ui", "SelectedRowStyle");
   private static final Value<String> INHERITED_EFFECT_ROW_STYLE = Value.ref("Pages/TriggerVolume/TriggerVolumeInspectorEffectRow.ui", "InheritedRowStyle");
   private static final Value<String> NORMAL_EFFECT_LABEL_STYLE = Value.ref("Pages/TriggerVolume/TriggerVolumeInspectorEffectRow.ui", "NormalLabelStyle");
   private static final Value<String> SELECTED_EFFECT_LABEL_STYLE = Value.ref("Pages/TriggerVolume/TriggerVolumeInspectorEffectRow.ui", "SelectedLabelStyle");
   private static final Value<String> INHERITED_EFFECT_LABEL_STYLE = Value.ref("Pages/TriggerVolume/TriggerVolumeInspectorEffectRow.ui", "InheritedLabelStyle");
   private static final String PAGE = "Pages/TriggerVolume/TriggerVolumeInspectorPage.ui";
   private static final String GROUP_ROW = "Pages/TriggerVolume/TriggerVolumeBrowseGroupRow.ui";
   private static final String VOLUME_ROW = "Pages/TriggerVolume/TriggerVolumeBrowseVolumeRow.ui";
   private static final String TAG_ROW = "Pages/TriggerVolume/TriggerVolumeBrowseTagRow.ui";
   private static final String TAG_CHIP = "Pages/TriggerVolume/TriggerVolumeTagChip.ui";
   private static final int[] TAG_CHIP_COLORS = new int[]{
      5223543,
      4176047,
      5214176,
      7237344,
      10181046,
      13393320,
      14707612,
      14715452,
      14070332,
      14247259,
      6013118,
      8365648,
      11566278,
      15241563,
      13194606,
      4878288,
      4894348,
      12546880,
      7317724,
      13332382
   };
   private static final int OVERFLOW_CHIP_COLOR = 5596014;
   private static final int INSPECTOR_PANE_WIDTH = 895;
   private static final int TAB_BUTTONS_WIDTH = 300;
   private static final int TITLE_CHIP_ROW_BUDGET = 579;
   private static final int[] EMPTY_ENTRIES = new int[0];
   private static final String ADD_ENTRY_NEW = "NEW";
   private static final String PROPERTY_ROW = "Pages/TriggerVolume/TriggerVolumeBrowsePropertyRow.ui";
   private static final String TAB_BUTTON = "Pages/TriggerVolume/TriggerVolumeInspectorTabButton.ui";
   private static final String EFFECT_ROW = "Pages/TriggerVolume/TriggerVolumeInspectorEffectRow.ui";
   private static final String EVENT_CATEGORY_HEADER = "Pages/TriggerVolume/TriggerVolumeInspectorEventCategoryHeader.ui";
   private static final String EVENT_SECTION_LABEL = "Pages/TriggerVolume/TriggerVolumeInspectorEventSectionLabel.ui";
   private static final String EVENT_CATEGORY_SPACER = "Pages/TriggerVolume/TriggerVolumeInspectorEventCategorySpacer.ui";
   private static final String SECTION_LABEL = "Pages/TriggerVolume/TriggerVolumeInspectorSectionLabel.ui";
   private static final String VOLUME_SECTION_LABEL = "Pages/TriggerVolume/TriggerVolumeInspectorVolumeSectionLabel.ui";
   private static final String EFFECT_OWNER_SECTION_LABEL = "Pages/TriggerVolume/TriggerVolumeInspectorEffectOwnerSectionLabel.ui";
   private static final String COMMON_TEXT_BUTTON_DOCUMENT = "Common/TextButton.ui";
   private static final String BASE_HEADER_GRID = "Pages/TriggerVolume/TriggerVolumeInspectorBaseHeaderGrid.ui";
   private static final String FIELD_TEXT = "Pages/TriggerVolume/TriggerVolumeInspectorTextRow.ui";
   private static final String FIELD_COLOR = "Pages/TriggerVolume/TriggerVolumeInspectorColorRow.ui";
   private static final String FIELD_NUMBER = "Pages/TriggerVolume/TriggerVolumeInspectorNumberRow.ui";
   private static final String FIELD_INT = "Pages/TriggerVolume/TriggerVolumeInspectorIntRow.ui";
   private static final String FIELD_CHECKBOX = "Pages/TriggerVolume/TriggerVolumeInspectorCheckboxRow.ui";
   private static final String FIELD_DROPDOWN = "Pages/TriggerVolume/TriggerVolumeInspectorDropdownRow.ui";
   private static final String FIELD_VEC3 = "Pages/TriggerVolume/TriggerVolumeInspectorVec3Row.ui";
   private static final String FIELD_DIMENSIONS_BOX = "Pages/TriggerVolume/TriggerVolumeInspectorDimensionsBoxRow.ui";
   private static final String FIELD_DIMENSIONS_SPHERE = "Pages/TriggerVolume/TriggerVolumeInspectorDimensionsSphereRow.ui";
   private static final String FIELD_DIMENSIONS_CYLINDER = "Pages/TriggerVolume/TriggerVolumeInspectorDimensionsCylinderRow.ui";
   private static final String FIELD_ASSET_PICKER = "Pages/TriggerVolume/TriggerVolumeInspectorAssetPickerRow.ui";
   private static final String SOUND_ASSET_PICKER_ROW = "Pages/TriggerVolume/TriggerVolumeInspectorSoundAssetRow.ui";
   private static final Set<String> NON_NEGATIVE_NUMERIC_FIELDS = Set.of(
      "CooldownCondition.Cooldown",
      "ItemCondition.Quantity",
      "PlayerCountCondition.Count",
      "GiveItem.Quantity",
      "TriggerNpcMarkers.Range",
      "TriggerNpcMarkers.Radius",
      "ModifyTags.Radius",
      "EnableVolume.Radius",
      "DisableVolume.Radius",
      "DeleteVolume.Radius",
      "TagCondition.Radius",
      "TagCondition.MinimumCount"
   );
   private static final Map<String, BsonValue> DEFAULT_FIELD_VALUES = Map.of(
      "ModifyTags.TagValue",
      new BsonString("Empty"),
      "ModifyTags.Radius",
      new BsonDouble(50.0),
      "EnableVolume.Radius",
      new BsonDouble(50.0),
      "DisableVolume.Radius",
      new BsonDouble(50.0),
      "DeleteVolume.Radius",
      new BsonDouble(50.0),
      "TagCondition.Radius",
      new BsonDouble(50.0)
   );
   private static final Color DEFAULT_PREFAB_BIOME_TINT = new Color((byte)91, (byte)-98, (byte)40);
   private static final int DEFAULT_BIOME_TINT = ColorParseUtil.colorToARGBInt(DEFAULT_PREFAB_BIOME_TINT) & 16777215;
   private static final int DEFAULT_WATER_TINT = ColorParseUtil.colorToARGBInt(Environment.getUnknownFor("").getWaterTint()) & 16777215;
   @Nonnull
   private String selectedWorld;
   @Nullable
   private String selectedId;
   private boolean selectedIsGroup;
   @Nonnull
   private TriggerVolumeInspectorPage.InspectorTab selectedTab;
   @Nonnull
   private String filterText = "";
   private final Map<String, TriggerVolumeInspectorDrafts.VolumeDraft> volumeDrafts = new LinkedHashMap<>();
   private final Map<String, TriggerVolumeInspectorDrafts.GroupDraft> groupDrafts = new LinkedHashMap<>();
   private final Set<String> deletedVolumes = new LinkedHashSet<>();
   private final Set<String> deletedGroups = new LinkedHashSet<>();
   private final List<TriggerVolumeInspectorPage.RowEntry> currentRows = new ArrayList<>();
   private final TriggerVolumeManager.SelectionObserver selectionObserver = this::onExternalSelectionChanged;
   private final TriggerVolumeManager.VolumeUpdateObserver volumeUpdateObserver = new TriggerVolumeManager.VolumeUpdateObserver() {
      @Override
      public void onVolumeUpdated(@Nonnull VolumeEntry volume) {
         TriggerVolumeInspectorPage.this.onExternalVolumeUpdated(volume);
      }

      @Override
      public void onVolumeRemoved(@Nonnull String volumeId) {
         TriggerVolumeInspectorPage.this.onExternalVolumeRemoved(volumeId);
      }
   };
   @Nullable
   private final String preSelectedVolumeId;
   private final boolean preSelectedIsGroup;
   @Nonnull
   private TriggerVolumeInspectorPage.EffectListKind selectedKind = TriggerVolumeInspectorPage.EffectListKind.EFFECT;
   @Nonnull
   private TriggerVolumeInspectorPage.EffectListKind addTargetKind = TriggerVolumeInspectorPage.EffectListKind.EFFECT;
   @Nonnull
   private TriggerEventType addEventType = TriggerEventType.ENTER;
   @Nonnull
   private String addEffectType = "";
   private int addEntry = 0;
   private boolean renamingSelected = false;
   @Nullable
   private String renameOriginalId = null;
   private int selectedEffectIndex = -1;
   @Nonnull
   private final Set<TriggerVolumeInspectorPage.EventCategoryKey> collapsedVolumeEventCategories = new HashSet<>();
   @Nonnull
   private final Set<TriggerVolumeInspectorPage.EventCategoryKey> expandedGroupEventCategories = new HashSet<>();
   private boolean suppressSelectionObserver;
   private boolean skipSaveOnDismiss;
   private boolean pendingScrollToSelection = true;
   @Nullable
   private String pendingPickerFieldKey;
   @Nullable
   private String pendingPickerSource;
   private boolean pendingPickerMultiSelect;
   @Nonnull
   private final Set<String> pendingPickerSelections = new LinkedHashSet<>();
   @Nonnull
   private String assetPickerSearchQuery = "";
   @Nonnull
   private final Set<String> missingOptionLangKeys = new HashSet<>();
   @Nonnull
   private final AssetPackSaveBrowser presetPackBrowser = new AssetPackSaveBrowser(AssetPackSaveBrowserConfig.defaults());

   public TriggerVolumeInspectorPage(
      @Nonnull PlayerRef playerRef,
      @Nonnull String selectedWorld,
      @Nullable String preSelectedVolumeId,
      @Nonnull TriggerVolumeInspectorPage.InspectorTab initialTab
   ) {
      this(playerRef, selectedWorld, preSelectedVolumeId, false, initialTab);
   }

   public TriggerVolumeInspectorPage(
      @Nonnull PlayerRef playerRef,
      @Nonnull String selectedWorld,
      @Nullable String preSelectedVolumeId,
      boolean preSelectedIsGroup,
      @Nonnull TriggerVolumeInspectorPage.InspectorTab initialTab
   ) {
      super(playerRef, CustomPageLifetime.CanDismiss, TriggerVolumeInspectorPage.PageData.CODEC);
      this.selectedWorld = selectedWorld;
      this.preSelectedVolumeId = preSelectedVolumeId;
      this.preSelectedIsGroup = preSelectedIsGroup;
      this.selectedTab = initialTab;
   }

   @Override
   public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, @Nonnull Store<EntityStore> store) {
      cmd.append("Pages/TriggerVolume/TriggerVolumeInspectorPage.ui");
      this.registerSelectionObserver();
      this.registerVolumeUpdateObserver();
      this.clearPastePrefabPreviewIfFromDifferentWorld();
      this.buildWorldDropdown(cmd);
      this.buildTabs(cmd, evt);
      this.buildList(cmd, evt);
      if (this.preSelectedVolumeId != null) {
         this.applyPreSelection(cmd);
      }

      this.buildSelectedPane(cmd, evt);
      this.bindStaticEvents(evt);
      this.presetPackBrowser.buildUI(cmd, evt);
   }

   @Override
   public void onDismiss(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
      TriggerVolumeManager manager = getManagerForWorld(this.selectedWorld);
      if (manager != null) {
         if (!this.skipSaveOnDismiss) {
            this.saveDrafts(manager, false);
         }

         manager.clearSelectionObserver(this.playerRef.getUuid(), this.selectionObserver);
         manager.clearVolumeUpdateObserver(this.playerRef.getUuid(), this.volumeUpdateObserver);
      }

      this.skipSaveOnDismiss = false;
   }

   public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull TriggerVolumeInspectorPage.PageData data) {
      AssetPackSaveBrowser.ActionResult packResult = this.presetPackBrowser
         .handleAction(data.action != null ? data.action.name() : null, data.packBrowserData, "#PresetSavePage #SelectedPackLabel");
      if (packResult != null) {
         if (packResult.errorKey() != null) {
            this.playerRef.sendMessage(Message.translation(packResult.errorKey()));
         }

         this.sendUpdate(packResult.commandBuilder(), packResult.eventBuilder(), false);
      } else if (data.action != null) {
         switch (data.action) {
            case Select:
               this.onSelect(data);
               break;
            case ChangeWorld:
               this.onChangeWorld(data);
               break;
            case FilterChanged:
               this.onFilterChanged(data);
               break;
            case ChangeTab:
               this.onChangeTab(data);
               break;
            case UpdateVolumeField:
               this.onUpdateVolumeField(data);
               break;
            case UpdateTag:
               this.onUpdateTag(data);
               break;
            case RemoveTag:
               this.onRemoveTag(data);
               break;
            case DeleteSelection:
               this.onDeleteSelection();
               break;
            case Save:
               this.onSave();
               break;
            case Discard:
               this.onDiscard();
               break;
            case Teleport:
               this.onTeleport(ref, store);
               break;
            case ToggleRenameSelected:
               this.onToggleRenameSelected();
               break;
            case ConfirmRenameSelected:
               this.onConfirmRenameSelected();
               break;
            case CancelRenameSelected:
               this.onCancelRenameSelected();
               break;
            case SelectEffect:
               this.onSelectEffect(data);
               break;
            case AddEffect:
               this.onAddEffect(data);
               break;
            case RemoveEffect:
               this.onRemoveEffect();
               break;
            case DuplicateEffect:
               this.onDuplicateEffect();
               break;
            case MoveEffectUp:
               this.onMoveEffect(-1);
               break;
            case MoveEffectDown:
               this.onMoveEffect(1);
               break;
            case UpdateAddTarget:
               this.onUpdateAddTarget(data);
               break;
            case UpdateAddEffectType:
               this.onUpdateAddEffectType(data);
               break;
            case UpdateAddEventType:
               this.onUpdateAddEventType(data);
               break;
            case UpdateAddEntry:
               this.onUpdateAddEntry(data);
               break;
            case ToggleEventCategory:
               this.onToggleEventCategory(data);
               break;
            case UpdateParameter:
               this.onUpdateParameter(data);
               break;
            case CommitEntry:
               this.onCommitEntry();
               break;
            case TogglePrefabPreview:
               this.onTogglePrefabPreview();
               break;
            case OpenPresetSave:
               this.onOpenPresetSave();
               break;
            case PresetNameChanged:
               this.onPresetNameChanged(data);
               break;
            case ConfirmSavePreset:
               this.onConfirmSavePreset(data);
               break;
            case CancelPresetSave:
               this.onCancelPresetSave();
               break;
            case OpenPresetLoad:
               this.onOpenPresetLoad();
               break;
            case LoadPreset:
               this.onLoadPreset(data);
               break;
            case CancelPresetLoad:
               this.onCancelPresetLoad();
               break;
            case OpenAssetPicker:
               this.onOpenAssetPicker(data);
               break;
            case AssetPickerSearch:
               this.onAssetPickerSearch(data);
               break;
            case AssetPickerSelect:
               this.onAssetPickerSelect(data);
               break;
            case ConfirmAssetPicker:
               this.onConfirmAssetPicker();
               break;
            case PreviewSound:
               this.onPreviewSound(data, store);
               break;
            case CancelAssetPicker:
               this.onCancelAssetPicker();
         }
      }
   }

   private void buildWorldDropdown(@Nonnull UICommandBuilder cmd) {
      ObjectArrayList<DropdownEntryInfo> entries = new ObjectArrayList<>();

      for (World world : Universe.get().getWorlds().values()) {
         String name = world.getName().toLowerCase(Locale.ROOT);
         entries.add(new DropdownEntryInfo(LocalizableString.fromString(name), name));
      }

      cmd.set("#WorldDropdown.Entries", entries);
      cmd.set("#WorldDropdown.Value", this.selectedWorld);
      cmd.set("#FilterField.Value", this.filterText);
   }

   private void buildTabs(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt) {
      cmd.clear("#TabButtons");
      int idx = 0;

      for (TriggerVolumeInspectorPage.InspectorTab tab : TriggerVolumeInspectorPage.InspectorTab.values()) {
         String sel = "#TabButtons[" + idx + "]";
         cmd.append("#TabButtons", "Pages/TriggerVolume/TriggerVolumeInspectorTabButton.ui");
         cmd.set(sel + ".Text", tab.label());
         cmd.set(sel + ".TooltipText", tab.tooltip());
         cmd.set(sel + ".Disabled", this.selectedTab == tab);
         evt.addEventBinding(
            CustomUIEventBindingType.Activating,
            sel,
            new EventData().append("Action", TriggerVolumeInspectorPage.Action.ChangeTab.name()).append("Tab", tab.name())
         );
         idx++;
      }
   }

   private void buildList(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt) {
      cmd.clear("#ListContainer");
      this.currentRows.clear();
      TriggerVolumeManager manager = getManagerForWorld(this.selectedWorld);
      if (manager != null && (!manager.getVolumesMap().isEmpty() || !manager.getGroupsMap().isEmpty())) {
         LinkedHashMap<String, List<VolumeEntry>> groupedVolumes = new LinkedHashMap<>();

         for (GroupEntry group : manager.getGroupsMap().values()) {
            if (!this.deletedGroups.contains(group.getId())) {
               groupedVolumes.put(group.getId(), new ArrayList<>());
            }
         }

         ArrayList<VolumeEntry> ungrouped = new ArrayList<>();

         for (VolumeEntry volume : manager.getVolumesMap().values()) {
            if (!this.deletedVolumes.contains(volume.getId())) {
               TriggerVolumeInspectorDrafts.VolumeDraft draft = this.volumeDrafts.get(volume.getId());
               String groupId = draft != null ? draft.groupId : volume.getGroupId();
               if (groupId != null && groupedVolumes.containsKey(groupId)) {
                  groupedVolumes.get(groupId).add(volume);
               } else {
                  ungrouped.add(volume);
               }
            }
         }

         int idx = 0;

         for (GroupEntry group : manager.getGroupsMap().values()) {
            if (!this.deletedGroups.contains(group.getId())) {
               TriggerVolumeInspectorDrafts.GroupDraft draft = this.draftForGroup(group);
               List<VolumeEntry> children = groupedVolumes.getOrDefault(group.getId(), List.of());
               if (this.matchesGroupFilter(draft.id, group, children)) {
                  idx = this.appendGroupRow(cmd, evt, idx, group.getId(), draft.id, draft.color);

                  for (VolumeEntry volume : children) {
                     if (this.matchesVolumeFilter(volume)) {
                        TriggerVolumeInspectorDrafts.VolumeDraft volumeDraft = this.draftForVolume(volume);
                        idx = this.appendVolumeRow(cmd, evt, idx, volume.getId(), volumeDraft.id, true, draft.color, volumeDraft.tags);
                     }
                  }
               }
            }
         }

         List<VolumeEntry> visibleUngrouped = ungrouped.stream().filter(this::matchesVolumeFilter).toList();
         if (!visibleUngrouped.isEmpty()) {
            cmd.append("#ListContainer", "Pages/TriggerVolume/TriggerVolumeInspectorSectionLabel.ui");
            cmd.set("#ListContainer[" + idx + "].Text", Message.translation("server.customUI.triggerVolumeBrowse.ungrouped"));
            idx++;

            for (VolumeEntry volume : visibleUngrouped) {
               TriggerVolumeInspectorDrafts.VolumeDraft draft = this.draftForVolume(volume);
               idx = this.appendVolumeRow(cmd, evt, idx, volume.getId(), draft.id, false, 0, draft.tags);
            }
         }

         if (idx == 0) {
            this.appendListMessage(cmd, Message.translation("server.customUI.triggerVolumeBrowse.emptyState"));
         } else {
            this.scrollSelectedRowToView(cmd);
         }
      } else {
         this.appendListMessage(cmd, Message.translation("server.customUI.triggerVolumeBrowse.emptyState"));
      }
   }

   private int appendGroupRow(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, int idx, @Nonnull String originalId, @Nonnull String label, int color) {
      String sel = "#ListContainer[" + idx + "]";
      cmd.append("#ListContainer", "Pages/TriggerVolume/TriggerVolumeBrowseGroupRow.ui");
      cmd.set(sel + " #Label.Text", label);
      cmd.setObject(sel + " #ColorSwatch.Background", colorPatch(color));
      cmd.set(sel + ".Style", this.selectedIsGroup && originalId.equals(this.selectedId) ? SELECTED_ROW_STYLE : NORMAL_ROW_STYLE);
      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         sel,
         new EventData().append("Action", TriggerVolumeInspectorPage.Action.Select.name()).append("Id", originalId).append("IsGroup", "true"),
         false
      );
      this.currentRows.add(new TriggerVolumeInspectorPage.RowEntry(originalId, true, idx));
      return idx + 1;
   }

   private int appendVolumeRow(
      @Nonnull UICommandBuilder cmd,
      @Nonnull UIEventBuilder evt,
      int idx,
      @Nonnull String originalId,
      @Nonnull String label,
      boolean indented,
      int groupColor,
      @Nonnull Map<String, String> tags
   ) {
      String sel = "#ListContainer[" + idx + "]";
      cmd.append("#ListContainer", "Pages/TriggerVolume/TriggerVolumeBrowseVolumeRow.ui");
      cmd.set(sel + " #Label.Text", label);
      int chipRows = this.appendRowTagChips(cmd, sel + " #TagChips", tags, indented);
      cmd.set(sel + " #TagChips.Visible", chipRows > 0);
      if (chipRows > 0) {
         Anchor anchor = new Anchor();
         anchor.setHeight(Value.of(chipRows == 1 ? 44 : 64));
         cmd.setObject(sel + ".Anchor", anchor);
      }

      cmd.set(sel + " #Indent.Visible", indented);
      cmd.set(sel + " #ColorBar.Visible", indented);
      if (indented) {
         cmd.setObject(sel + " #ColorBar.Background", colorPatch(groupColor));
      }

      cmd.set(sel + ".Style", !this.selectedIsGroup && originalId.equals(this.selectedId) ? SELECTED_ROW_STYLE : NORMAL_ROW_STYLE);
      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         sel,
         new EventData().append("Action", TriggerVolumeInspectorPage.Action.Select.name()).append("Id", originalId).append("IsGroup", "false"),
         false
      );
      this.currentRows.add(new TriggerVolumeInspectorPage.RowEntry(originalId, false, idx));
      return idx + 1;
   }

   private void appendListMessage(@Nonnull UICommandBuilder cmd, @Nonnull Message text) {
      cmd.append("#ListContainer", "Pages/TriggerVolume/TriggerVolumeInspectorSectionLabel.ui");
      cmd.set("#ListContainer[0].Text", text);
   }

   private void applyPreSelection(@Nonnull UICommandBuilder cmd) {
      for (TriggerVolumeInspectorPage.RowEntry row : this.currentRows) {
         if (row.isGroup == this.preSelectedIsGroup && row.id.equals(this.preSelectedVolumeId)) {
            this.selectedId = row.id;
            this.selectedIsGroup = row.isGroup;
            cmd.set("#ListContainer[" + row.listIndex + "].Style", SELECTED_ROW_STYLE);
            cmd.set("#ListContainer.ScrollChildIndexIntoView", row.listIndex);
            return;
         }
      }
   }

   private void scrollSelectedRowToView(@Nonnull UICommandBuilder cmd) {
      if (this.pendingScrollToSelection && this.selectedId != null) {
         for (TriggerVolumeInspectorPage.RowEntry row : this.currentRows) {
            if (row.isGroup == this.selectedIsGroup && row.id.equals(this.selectedId)) {
               cmd.set("#ListContainer.ScrollChildIndexIntoView", row.listIndex);
               this.pendingScrollToSelection = false;
               return;
            }
         }
      }
   }

   private void buildSelectedPane(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt) {
      cmd.clear("#VolumeTab");
      cmd.clear("#TagsList");
      cmd.clear("#EffectListContainer");
      cmd.clear("#EffectDetailPanel");
      cmd.set("#NoSelectionLabel.Visible", this.selectedId == null);
      this.buildSelectedNameHeader(cmd);
      this.buildTitleTagChips(cmd);
      cmd.set("#VolumeTab.Visible", this.selectedId != null && this.selectedTab == TriggerVolumeInspectorPage.InspectorTab.VOLUME);
      cmd.set("#TagsTab.Visible", this.selectedId != null && this.selectedTab == TriggerVolumeInspectorPage.InspectorTab.TAGS);
      cmd.set("#EffectsTab.Visible", this.selectedId != null && this.selectedTab == TriggerVolumeInspectorPage.InspectorTab.EFFECTS);
      cmd.set("#DeleteButton.Disabled", this.selectedId == null);
      cmd.set("#SavePresetButton.Disabled", this.selectedId == null);
      cmd.set("#LoadPresetButton.Disabled", this.selectedId == null);
      if (this.selectedId != null) {
         switch (this.selectedTab) {
            case VOLUME:
               this.buildVolumeTab(cmd, evt);
               break;
            case EFFECTS:
               this.buildEffectsTab(cmd, evt);
               break;
            case TAGS:
               this.buildTagsTab(cmd, evt);
         }
      }
   }

   private void buildSelectedNameHeader(@Nonnull UICommandBuilder cmd) {
      String name = this.selectedDraftId();
      cmd.set("#SelectedNameHeader.Visible", name != null);
      if (name == null) {
         this.renamingSelected = false;
         cmd.set("#SelectedNameValidation.Visible", false);
      } else {
         cmd.set("#SelectedNameTitle.Text", name);
         cmd.set("#SelectedNameTitle.Visible", !this.renamingSelected);
         cmd.set("#SelectedNameSpacer.Visible", !this.renamingSelected);
         cmd.set("#SelectedNameEditButton.Visible", !this.renamingSelected);
         cmd.set("#SelectedNameFieldContainer.Visible", this.renamingSelected);
         cmd.set("#SelectedNameField.Value", name);
         cmd.set("#SelectedNameEditButton.TooltipText", Message.translation("server.customUI.triggerVolumeInspector.renameToggle.tooltip"));
         cmd.set("#SelectedNameConfirmButton.TooltipText", Message.translation("server.customUI.triggerVolumeInspector.renameConfirm.tooltip"));
         cmd.set("#SelectedNameCancelButton.TooltipText", Message.translation("server.customUI.triggerVolumeInspector.renameCancel.tooltip"));
         Message validationMessage = this.idValidationMessage(name);
         cmd.set("#SelectedNameValidation.Visible", this.renamingSelected && validationMessage != null);
         if (validationMessage != null) {
            cmd.set("#SelectedNameValidation.Text", validationMessage);
         }
      }
   }

   private void buildTitleTagChips(@Nonnull UICommandBuilder cmd) {
      cmd.clear("#TitleTagChips");
      cmd.clear("#TitleTagChipsOverflow");
      Map<String, String> tags = null;
      if (this.selectedIsGroup) {
         TriggerVolumeInspectorDrafts.GroupDraft draft = this.selectedGroupDraft();
         if (draft != null) {
            tags = draft.tags;
         }
      } else {
         TriggerVolumeInspectorDrafts.VolumeDraft draft = this.selectedVolumeDraft();
         if (draft != null) {
            tags = draft.tags;
         }
      }

      if (this.selectedId != null && tags != null) {
         ArrayList<Entry<String, String>> entries = new ArrayList<>(tags.entrySet());
         int inlineEnd = this.fillTitleChipRows(cmd, "#TitleTagChips", entries, 0, 579, 1, false);
         if (inlineEnd < entries.size()) {
            this.fillTitleChipRows(cmd, "#TitleTagChipsOverflow", entries, inlineEnd, 579, 2, true);
         }
      }
   }

   private int fillTitleChipRows(
      @Nonnull UICommandBuilder cmd,
      @Nonnull String container,
      @Nonnull List<Entry<String, String>> entries,
      int startIndex,
      int budget,
      int maxRows,
      boolean lastRow
   ) {
      int chipIdx = 0;
      int rowsUsed = 1;
      int x = 0;

      int index;
      for (index = startIndex; index < entries.size(); index++) {
         Entry<String, String> entry = entries.get(index);
         String value = entry.getValue();
         String text = value != null && !value.isEmpty() ? entry.getKey() + ": " + value : entry.getKey();
         int width = chipWidth(text);
         if (x > 0 && x + width > budget) {
            if (rowsUsed == maxRows) {
               if (lastRow) {
                  this.appendChip(cmd, container, chipIdx, "+" + (entries.size() - index), 5596014);
               }
               break;
            }

            rowsUsed++;
            x = 0;
         }

         this.appendChip(cmd, container, chipIdx++, text, tagChipColor(entry.getKey()));
         x += width;
      }

      return index;
   }

   @Nullable
   private String selectedDraftId() {
      if (this.selectedId == null) {
         return null;
      } else if (this.selectedIsGroup) {
         TriggerVolumeInspectorDrafts.GroupDraft draft = this.selectedGroupDraft();
         return draft != null ? draft.id : null;
      } else {
         TriggerVolumeInspectorDrafts.VolumeDraft draft = this.selectedVolumeDraft();
         return draft != null ? draft.id : null;
      }
   }

   private void buildVolumeTab(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt) {
      int row = 0;
      if (this.selectedIsGroup) {
         TriggerVolumeInspectorDrafts.GroupDraft draft = this.selectedGroupDraft();
         if (draft == null) {
            return;
         }

         row = this.addVolumeSectionLabel(cmd, row, "identity");
         row = this.addVolumeTextRow(cmd, evt, row, "id", draft.id);
         row = this.addVolumeColorRow(cmd, evt, row, "color", colorToHex(draft.color));
         row = this.addVolumeSectionLabel(cmd, row, "transform");
         row = this.addVolumeVec3Row(cmd, evt, row, "position", draft.origin);
         row = this.addVolumeSectionLabel(cmd, row, "behavior");
         row = this.addVolumeDropdownRow(cmd, evt, row, "targetTypes", targetTypeEntries(), targetTypesValue(draft.targetTypes));
         row = this.addVolumeCheckboxRow(cmd, evt, row, "enabled", draft.enabled);
         row = this.addVolumeSectionLabel(cmd, row, "timing");
         row = this.addVolumeDropdownRow(cmd, evt, row, "conditionTiming", conditionTimingEntries(), draft.conditionTiming.name());
         row = this.addVolumeDropdownRow(cmd, evt, row, "rejectionDelayMode", rejectionDelayModeEntries(), draft.rejectionDelayMode.name());
         row = this.addReadonlyTextRow(cmd, row, "members", String.join(", ", draft.memberVolumeIds));
      } else {
         TriggerVolumeInspectorDrafts.VolumeDraft draft = this.selectedVolumeDraft();
         if (draft == null) {
            return;
         }

         row = this.addVolumeSectionLabel(cmd, row, "identity");
         row = this.addVolumeTextRow(cmd, evt, row, "id", draft.id);
         row = this.addVolumeColorRow(cmd, evt, row, "color", draft.color != null ? colorToHex(draft.color) : "#00CCCC");
         row = this.addVolumeSectionLabel(cmd, row, "transform");
         row = this.addVolumeDropdownRow(cmd, evt, row, "shape", shapeEntries(), draft.shapeType.name());
         row = this.addVolumeVec3Row(cmd, evt, row, "position", draft.position, true);
         row = this.addVolumeDimensionsRow(cmd, evt, row, draft.shapeType, draft.dimensions);
         row = this.addVolumeSectionLabel(cmd, row, "behavior");
         row = this.addVolumeDropdownRow(cmd, evt, row, "targetTypes", targetTypeEntries(), targetTypesValue(draft.targetTypes));
         row = this.addVolumeDropdownRow(cmd, evt, row, "projectileSource", projectileSourceEntries(), draft.projectileSource.name());
         row = this.addVolumeCheckboxRow(cmd, evt, row, "enabled", draft.enabled);
         row = this.addVolumeCheckboxRow(cmd, evt, row, "keepLoaded", draft.keepLoaded);
         row = this.addVolumeCheckboxRow(cmd, evt, row, "cancelDelayedOnExit", draft.cancelDelayedOnExit);
         row = this.addVolumeSectionLabel(cmd, row, "timing");
         row = this.addVolumeNumberRow(cmd, evt, row, "activationDelay", String.valueOf(draft.activationDelay), 2);
         row = this.addVolumeNumberRow(cmd, evt, row, "cooldown", String.valueOf(draft.cooldown), 2);
         row = this.addVolumeDropdownRow(cmd, evt, row, "cooldownMode", cooldownModeEntries(), draft.cooldownMode.name());
         row = this.addVolumeDropdownRow(cmd, evt, row, "conditionTiming", conditionTimingEntries(), draft.conditionTiming.name());
         row = this.addVolumeDropdownRow(cmd, evt, row, "rejectionDelayMode", rejectionDelayModeEntries(), draft.rejectionDelayMode.name());
      }
   }

   private int addVolumeSectionLabel(@Nonnull UICommandBuilder cmd, int row, @Nonnull String sectionKey) {
      String selector = "#VolumeTab[" + row + "]";
      cmd.append("#VolumeTab", "Pages/TriggerVolume/TriggerVolumeInspectorVolumeSectionLabel.ui");
      cmd.set(selector + " #Title.Text", Message.translation("server.customUI.triggerVolumeInspector.section." + sectionKey));
      return row + 1;
   }

   private int addVolumeTextRow(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, int row, @Nonnull String key, @Nonnull String value) {
      String sel = "#VolumeTab[" + row + "]";
      cmd.append("#VolumeTab", "Pages/TriggerVolume/TriggerVolumeInspectorTextRow.ui");
      cmd.set(sel + " #Label.Text", fieldLabel(key));
      cmd.set(sel + " #Label.TooltipText", volumeFieldTooltip(key));
      cmd.set(sel + " #Input.Value", value);
      this.setIdValidation(cmd, sel, key, value);
      evt.addEventBinding(CustomUIEventBindingType.ValueChanged, sel + " #Input", volumeFieldEvent(key).append("@ParamValue", sel + " #Input.Value"), false);
      return row + 1;
   }

   private void setIdValidation(@Nonnull UICommandBuilder cmd, @Nonnull String selector, @Nonnull String key, @Nonnull String value) {
      Message validationMessage = "id".equals(key) ? this.idValidationMessage(value) : null;
      if (validationMessage == null) {
         cmd.set(selector + " #ValidationLabel.Visible", false);
      } else {
         cmd.set(selector + " #ValidationLabel.Visible", true);
         cmd.set(selector + " #ValidationLabel.Text", validationMessage);
      }
   }

   private int addVolumeColorRow(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, int row, @Nonnull String key, @Nonnull String value) {
      String sel = "#VolumeTab[" + row + "]";
      cmd.append("#VolumeTab", "Pages/TriggerVolume/TriggerVolumeInspectorColorRow.ui");
      cmd.set(sel + " #Label.Text", fieldLabel(key));
      cmd.set(sel + " #Label.TooltipText", volumeFieldTooltip(key));
      cmd.set(sel + " #Input.Color", value);
      evt.addEventBinding(CustomUIEventBindingType.ValueChanged, sel + " #Input", volumeFieldEvent(key).append("@ParamValue", sel + " #Input.Color"), false);
      return row + 1;
   }

   private int addReadonlyTextRow(@Nonnull UICommandBuilder cmd, int row, @Nonnull String key, @Nonnull String value) {
      String sel = "#VolumeTab[" + row + "]";
      cmd.append("#VolumeTab", "Pages/TriggerVolume/TriggerVolumeBrowsePropertyRow.ui");
      cmd.set(sel + " #Key.Text", fieldLabel(key));
      cmd.set(sel + " #Key.TooltipText", volumeFieldTooltip(key));
      cmd.set(sel + " #Value.Text", value);
      return row + 1;
   }

   private int addVolumeNumberRow(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, int row, @Nonnull String key, @Nonnull String value, int decimals) {
      String sel = "#VolumeTab[" + row + "]";
      cmd.append("#VolumeTab", decimals > 0 ? "Pages/TriggerVolume/TriggerVolumeInspectorNumberRow.ui" : "Pages/TriggerVolume/TriggerVolumeInspectorIntRow.ui");
      cmd.set(sel + " #Label.Text", fieldLabel(key));
      cmd.set(sel + " #Label.TooltipText", volumeFieldTooltip(key));

      try {
         cmd.set(sel + " #Input.Value", Double.parseDouble(value));
      } catch (NumberFormatException exception) {
         cmd.set(sel + " #Input.Value", 0.0);
      }

      evt.addEventBinding(
         CustomUIEventBindingType.ValueChanged, sel + " #Input", volumeFieldEvent(key).append("@ParamNumericValue", sel + " #Input.Value"), false
      );
      return row + 1;
   }

   private int addVolumeCheckboxRow(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, int row, @Nonnull String key, boolean value) {
      String sel = "#VolumeTab[" + row + "]";
      cmd.append("#VolumeTab", "Pages/TriggerVolume/TriggerVolumeInspectorCheckboxRow.ui");
      cmd.set(sel + " #Label.Text", fieldLabel(key));
      cmd.set(sel + " #Label.TooltipText", volumeFieldTooltip(key));
      cmd.set(sel + " #Input.Value", value);
      evt.addEventBinding(CustomUIEventBindingType.ValueChanged, sel + " #Input", volumeFieldEvent(key).append("@ParamBool", sel + " #Input.Value"), false);
      return row + 1;
   }

   private int addVolumeDropdownRow(
      @Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, int row, @Nonnull String key, @Nonnull List<DropdownEntryInfo> entries, @Nonnull String value
   ) {
      String sel = "#VolumeTab[" + row + "]";
      cmd.append("#VolumeTab", "Pages/TriggerVolume/TriggerVolumeInspectorDropdownRow.ui");
      cmd.set(sel + " #Label.Text", fieldLabel(key));
      cmd.set(sel + " #Label.TooltipText", volumeFieldTooltip(key));
      cmd.set(sel + " #Input.Entries", new ObjectArrayList<>(entries));
      cmd.set(sel + " #Input.Value", value);
      evt.addEventBinding(CustomUIEventBindingType.ValueChanged, sel + " #Input", volumeFieldEvent(key).append("@ParamValue", sel + " #Input.Value"), false);
      return row + 1;
   }

   private int addVolumeVec3Row(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, int row, @Nonnull String key, @Nonnull Vector3d value) {
      return this.addVolumeVec3Row(cmd, evt, row, key, value, false);
   }

   private int addVolumeVec3Row(
      @Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, int row, @Nonnull String key, @Nonnull Vector3d value, boolean withTeleport
   ) {
      String sel = "#VolumeTab[" + row + "]";
      cmd.append("#VolumeTab", "Pages/TriggerVolume/TriggerVolumeInspectorVec3Row.ui");
      cmd.set(sel + " #Label.Text", fieldLabel(key));
      cmd.set(sel + " #Label.TooltipText", volumeFieldTooltip(key));
      cmd.set(sel + " #X.Value", value.x());
      cmd.set(sel + " #Y.Value", value.y());
      cmd.set(sel + " #Z.Value", value.z());

      for (String comp : List.of("X", "Y", "Z")) {
         evt.addEventBinding(
            CustomUIEventBindingType.ValueChanged,
            sel + " #" + comp,
            volumeFieldEvent(key).append("@VecX", sel + " #X.Value").append("@VecY", sel + " #Y.Value").append("@VecZ", sel + " #Z.Value"),
            false
         );
      }

      if (withTeleport) {
         cmd.set(sel + " #TeleportButton.Visible", true);
         evt.addEventBinding(
            CustomUIEventBindingType.Activating, sel + " #TeleportButton", new EventData().append("Action", TriggerVolumeInspectorPage.Action.Teleport.name())
         );
      }

      return row + 1;
   }

   private int addVolumeDimensionsRow(
      @Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, int row, @Nonnull TriggerVolumeShapeType shapeType, @Nonnull Vector3d value
   ) {
      return switch (shapeType) {
         case Box -> this.addBoxDimensionsRow(cmd, evt, row, value);
         case Sphere -> this.addSphereDimensionsRow(cmd, evt, row, value);
         case Cylinder -> this.addCylinderDimensionsRow(cmd, evt, row, value);
      };
   }

   private int addBoxDimensionsRow(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, int row, @Nonnull Vector3d value) {
      String sel = "#VolumeTab[" + row + "]";
      cmd.append("#VolumeTab", "Pages/TriggerVolume/TriggerVolumeInspectorDimensionsBoxRow.ui");
      setDimensionsHeader(cmd, sel);
      cmd.set(sel + " #XLabel.Text", Message.translation("server.customUI.triggerVolumeInspector.field.dimensions.x"));
      cmd.set(sel + " #YLabel.Text", Message.translation("server.customUI.triggerVolumeInspector.field.dimensions.y"));
      cmd.set(sel + " #ZLabel.Text", Message.translation("server.customUI.triggerVolumeInspector.field.dimensions.z"));
      cmd.set(sel + " #X.Value", value.x());
      cmd.set(sel + " #Y.Value", value.y());
      cmd.set(sel + " #Z.Value", value.z());

      for (String component : List.of("X", "Y", "Z")) {
         evt.addEventBinding(
            CustomUIEventBindingType.ValueChanged,
            sel + " #" + component,
            volumeFieldEvent("dimensions").append("@VecX", sel + " #X.Value").append("@VecY", sel + " #Y.Value").append("@VecZ", sel + " #Z.Value"),
            false
         );
      }

      return row + 1;
   }

   private int addSphereDimensionsRow(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, int row, @Nonnull Vector3d value) {
      String sel = "#VolumeTab[" + row + "]";
      cmd.append("#VolumeTab", "Pages/TriggerVolume/TriggerVolumeInspectorDimensionsSphereRow.ui");
      setDimensionsHeader(cmd, sel);
      cmd.set(sel + " #RadiusLabel.Text", Message.translation("server.customUI.triggerVolumeInspector.field.dimensions.radius"));
      cmd.set(sel + " #Radius.Value", value.x());
      evt.addEventBinding(
         CustomUIEventBindingType.ValueChanged,
         sel + " #Radius",
         volumeFieldEvent("dimensionsRadius").append("@ParamNumericValue", sel + " #Radius.Value"),
         false
      );
      return row + 1;
   }

   private int addCylinderDimensionsRow(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, int row, @Nonnull Vector3d value) {
      String sel = "#VolumeTab[" + row + "]";
      cmd.append("#VolumeTab", "Pages/TriggerVolume/TriggerVolumeInspectorDimensionsCylinderRow.ui");
      setDimensionsHeader(cmd, sel);
      cmd.set(sel + " #RadiusLabel.Text", Message.translation("server.customUI.triggerVolumeInspector.field.dimensions.radius"));
      cmd.set(sel + " #HeightLabel.Text", Message.translation("server.customUI.triggerVolumeInspector.field.dimensions.height"));
      cmd.set(sel + " #Radius.Value", value.x());
      cmd.set(sel + " #Height.Value", value.y());

      for (String component : List.of("Radius", "Height")) {
         evt.addEventBinding(
            CustomUIEventBindingType.ValueChanged,
            sel + " #" + component,
            volumeFieldEvent("dimensionsCylinder").append("@VecX", sel + " #Radius.Value").append("@VecY", sel + " #Height.Value"),
            false
         );
      }

      return row + 1;
   }

   private static void setDimensionsHeader(@Nonnull UICommandBuilder cmd, @Nonnull String selector) {
      cmd.set(selector + " #Label.Text", fieldLabel("dimensions"));
      cmd.set(selector + " #Label.TooltipText", volumeFieldTooltip("dimensions"));
   }

   @Nonnull
   private static EventData volumeFieldEvent(@Nonnull String key) {
      return new EventData().append("Action", TriggerVolumeInspectorPage.Action.UpdateVolumeField.name()).append("ParamKey", key);
   }

   private void buildTagsTab(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt) {
      Map<String, String> tags = this.selectedIsGroup ? this.selectedGroupDraft().tags : this.selectedVolumeDraft().tags;
      int idx = 0;

      for (Entry<String, String> entry : tags.entrySet()) {
         String sel = "#TagsList[" + idx + "]";
         cmd.append("#TagsList", "Pages/TriggerVolume/TriggerVolumeBrowseTagRow.ui");
         int color = tagChipColor(entry.getKey());
         cmd.set(sel + " #TagLabel.Text", entry.getKey() + ": " + entry.getValue());
         cmd.setObject(sel + " #TagLabel.Background", tagColorPatch(color));
         evt.addEventBinding(
            CustomUIEventBindingType.Activating,
            sel + " #RemoveButton",
            new EventData().append("Action", TriggerVolumeInspectorPage.Action.RemoveTag.name()).append("RemoveTagKey", entry.getKey())
         );
         idx++;
      }
   }

   private void buildEffectsTab(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt) {
      this.buildAddEventTypeDropdown(cmd);
      this.buildAddEntryDropdown(cmd);
      this.buildAddTargetDropdown(cmd);
      this.buildAddEffectDropdown(cmd);
      this.buildEffectList(cmd, evt);
      this.buildEffectDetailPanel(cmd, evt);
   }

   private void bindStaticEvents(@Nonnull UIEventBuilder evt) {
      this.presetPackBrowser.buildEventBindings(evt, "#BrowsePackButton");
      evt.addEventBinding(
         CustomUIEventBindingType.ValueChanged,
         "#WorldDropdown",
         new EventData().append("Action", TriggerVolumeInspectorPage.Action.ChangeWorld.name()).append("@WorldName", "#WorldDropdown.Value"),
         false
      );
      evt.addEventBinding(
         CustomUIEventBindingType.ValueChanged,
         "#FilterField",
         new EventData().append("Action", TriggerVolumeInspectorPage.Action.FilterChanged.name()).append("@FilterText", "#FilterField.Value"),
         false
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         "#AddTagButton",
         new EventData()
            .append("Action", TriggerVolumeInspectorPage.Action.UpdateTag.name())
            .append("@TagKey", "#TagKeyField.Value")
            .append("@TagValues", "#TagValuesField.Value")
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating, "#DeleteButton", new EventData().append("Action", TriggerVolumeInspectorPage.Action.DeleteSelection.name())
      );
      evt.addEventBinding(CustomUIEventBindingType.Activating, "#SaveButton", new EventData().append("Action", TriggerVolumeInspectorPage.Action.Save.name()));
      evt.addEventBinding(
         CustomUIEventBindingType.Activating, "#DiscardButton", new EventData().append("Action", TriggerVolumeInspectorPage.Action.Discard.name())
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         "#AddEffectButton",
         new EventData()
            .append("Action", TriggerVolumeInspectorPage.Action.AddEffect.name())
            .append("@EffectType", "#AddEffectDropdown.Value")
            .append("@AddTargetKind", "#AddTargetDropdown.Value")
            .append("@AddEventType", "#AddEventTypeDropdown.Value")
            .append("@AddEntry", "#AddEntryDropdown.Value")
      );
      evt.addEventBinding(
         CustomUIEventBindingType.ValueChanged, "#SelectedNameField", volumeFieldEvent("id").append("@ParamValue", "#SelectedNameField.Value"), false
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         "#SelectedNameEditButton",
         new EventData().append("Action", TriggerVolumeInspectorPage.Action.ToggleRenameSelected.name())
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         "#SelectedNameConfirmButton",
         new EventData().append("Action", TriggerVolumeInspectorPage.Action.ConfirmRenameSelected.name())
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         "#SelectedNameCancelButton",
         new EventData().append("Action", TriggerVolumeInspectorPage.Action.CancelRenameSelected.name())
      );
      evt.addEventBinding(
         CustomUIEventBindingType.ValueChanged,
         "#AddEffectDropdown",
         new EventData().append("Action", TriggerVolumeInspectorPage.Action.UpdateAddEffectType.name()).append("@EffectType", "#AddEffectDropdown.Value"),
         false
      );
      evt.addEventBinding(
         CustomUIEventBindingType.ValueChanged,
         "#AddTargetDropdown",
         new EventData().append("Action", TriggerVolumeInspectorPage.Action.UpdateAddTarget.name()).append("@AddTargetKind", "#AddTargetDropdown.Value"),
         false
      );
      evt.addEventBinding(
         CustomUIEventBindingType.ValueChanged,
         "#AddEventTypeDropdown",
         new EventData().append("Action", TriggerVolumeInspectorPage.Action.UpdateAddEventType.name()).append("@AddEventType", "#AddEventTypeDropdown.Value"),
         false
      );
      evt.addEventBinding(
         CustomUIEventBindingType.ValueChanged,
         "#AddEntryDropdown",
         new EventData().append("Action", TriggerVolumeInspectorPage.Action.UpdateAddEntry.name()).append("@AddEntry", "#AddEntryDropdown.Value"),
         false
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating, "#RemoveEffectButton", new EventData().append("Action", TriggerVolumeInspectorPage.Action.RemoveEffect.name())
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         "#DuplicateEffectButton",
         new EventData().append("Action", TriggerVolumeInspectorPage.Action.DuplicateEffect.name())
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating, "#MoveEffectUpButton", new EventData().append("Action", TriggerVolumeInspectorPage.Action.MoveEffectUp.name())
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         "#MoveEffectDownButton",
         new EventData().append("Action", TriggerVolumeInspectorPage.Action.MoveEffectDown.name())
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating, "#SavePresetButton", new EventData().append("Action", TriggerVolumeInspectorPage.Action.OpenPresetSave.name())
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating, "#LoadPresetButton", new EventData().append("Action", TriggerVolumeInspectorPage.Action.OpenPresetLoad.name())
      );
      evt.addEventBinding(
         CustomUIEventBindingType.ValueChanged,
         "#PresetName #Input",
         new EventData().append("Action", TriggerVolumeInspectorPage.Action.PresetNameChanged.name()).append("@PresetName", "#PresetName #Input.Value"),
         false
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         "#ConfirmSavePresetButton",
         new EventData().append("Action", TriggerVolumeInspectorPage.Action.ConfirmSavePreset.name()).append("@PresetName", "#PresetName #Input.Value")
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         "#CancelSavePresetButton",
         new EventData().append("Action", TriggerVolumeInspectorPage.Action.CancelPresetSave.name())
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         "#CancelLoadPresetButton",
         new EventData().append("Action", TriggerVolumeInspectorPage.Action.CancelPresetLoad.name())
      );
      evt.addEventBinding(
         CustomUIEventBindingType.ValueChanged,
         "#AssetPickerPage #SearchInput",
         new EventData()
            .append("Action", TriggerVolumeInspectorPage.Action.AssetPickerSearch.name())
            .append("@AssetPickerQuery", "#AssetPickerPage #SearchInput.Value"),
         false
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         "#ConfirmAssetPickerButton",
         new EventData().append("Action", TriggerVolumeInspectorPage.Action.ConfirmAssetPicker.name())
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         "#CancelAssetPickerButton",
         new EventData().append("Action", TriggerVolumeInspectorPage.Action.CancelAssetPicker.name())
      );
   }

   private void onSelect(@Nonnull TriggerVolumeInspectorPage.PageData data) {
      if (data.id != null) {
         this.selectedId = data.id;
         this.selectedIsGroup = "true".equals(data.isGroup);
         this.selectedEffectIndex = -1;
         this.renamingSelected = false;
         this.pendingScrollToSelection = false;
         this.syncSelectionToTool();
         this.rebuildAll();
      }
   }

   private void onToggleRenameSelected() {
      if (this.selectedId != null) {
         this.renamingSelected = !this.renamingSelected;
         this.renameOriginalId = this.renamingSelected ? this.selectedDraftId() : null;
         this.rebuildAll();
      }
   }

   private void onConfirmRenameSelected() {
      if (this.renamingSelected) {
         this.renamingSelected = false;
         this.renameOriginalId = null;
         this.rebuildAll();
      }
   }

   private void onCancelRenameSelected() {
      if (this.renamingSelected) {
         if (this.renameOriginalId != null) {
            if (this.selectedIsGroup) {
               TriggerVolumeInspectorDrafts.GroupDraft draft = this.selectedGroupDraft();
               if (draft != null) {
                  draft.id = this.renameOriginalId;
               }
            } else {
               TriggerVolumeInspectorDrafts.VolumeDraft draft = this.selectedVolumeDraft();
               if (draft != null) {
                  draft.id = this.renameOriginalId;
               }
            }
         }

         this.renamingSelected = false;
         this.renameOriginalId = null;
         this.rebuildAll();
      }
   }

   private void onChangeWorld(@Nonnull TriggerVolumeInspectorPage.PageData data) {
      if (data.worldName != null && !data.worldName.equals(this.selectedWorld)) {
         TriggerVolumeManager oldManager = getManagerForWorld(this.selectedWorld);
         if (oldManager != null && !this.saveDrafts(oldManager, false)) {
            this.revertRejectedDraftIds();
            this.rebuildAll();
         } else {
            this.hidePastePrefabPreview();
            if (oldManager != null) {
               oldManager.clearSelectionObserver(this.playerRef.getUuid(), this.selectionObserver);
               oldManager.clearVolumeUpdateObserver(this.playerRef.getUuid(), this.volumeUpdateObserver);
            }

            this.clearDraftState();
            this.selectedWorld = data.worldName;
            this.selectedId = null;
            this.selectedIsGroup = false;
            this.selectedEffectIndex = -1;
            this.registerSelectionObserver();
            this.registerVolumeUpdateObserver();
            this.rebuildAll();
         }
      }
   }

   private void onFilterChanged(@Nonnull TriggerVolumeInspectorPage.PageData data) {
      this.filterText = data.filterText != null ? data.filterText.trim().toLowerCase(Locale.ROOT) : "";
      this.rebuildAll();
   }

   private void onChangeTab(@Nonnull TriggerVolumeInspectorPage.PageData data) {
      if (data.tab != null) {
         try {
            this.selectedTab = TriggerVolumeInspectorPage.InspectorTab.valueOf(data.tab);
            this.rebuildAll();
         } catch (IllegalArgumentException var3) {
         }
      }
   }

   private void onUpdateVolumeField(@Nonnull TriggerVolumeInspectorPage.PageData data) {
      if (data.paramKey != null && this.selectedId != null) {
         boolean shouldRebuild = !this.selectedIsGroup && "shape".equals(data.paramKey);
         if (this.selectedIsGroup) {
            TriggerVolumeInspectorDrafts.GroupDraft draft = this.selectedGroupDraft();
            if (draft == null) {
               return;
            }

            this.updateGroupField(draft, data);
            if ("id".equals(data.paramKey)) {
               this.updateIdValidation(draft.id);
               return;
            }
         } else {
            TriggerVolumeInspectorDrafts.VolumeDraft draft = this.selectedVolumeDraft();
            if (draft == null) {
               return;
            }

            this.updateVolumeField(draft, data);
            if ("id".equals(data.paramKey)) {
               this.updateIdValidation(draft.id);
               return;
            }
         }

         if (shouldRebuild) {
            this.rebuildAll();
         }

         if ("position".equals(data.paramKey)) {
            this.refreshActivePastePrefabPreviewPosition();
         }
      }
   }

   private void updateVolumeField(@Nonnull TriggerVolumeInspectorDrafts.VolumeDraft draft, @Nonnull TriggerVolumeInspectorPage.PageData data) {
      boolean updated = true;
      switch (data.paramKey) {
         case "id":
            this.updateDraftId(draft, data.paramValue);
            break;
         case "shape":
            draft.shapeType = parseEnum(TriggerVolumeShapeType.class, data.paramValue, draft.shapeType);
            break;
         case "position":
            setVec(draft.position, data);
            break;
         case "dimensions": {
            Vector3d oldDimensions = new Vector3d(draft.dimensions);
            setBoxDimensions(draft.dimensions, data);
            draft.rescaleAnchorOffset(oldDimensions);
            break;
         }
         case "dimensionsRadius": {
            Vector3d oldDimensions = new Vector3d(draft.dimensions);
            setSphereDimensions(draft.dimensions, data);
            draft.rescaleAnchorOffset(oldDimensions);
            break;
         }
         case "dimensionsCylinder": {
            Vector3d oldDimensions = new Vector3d(draft.dimensions);
            setCylinderDimensions(draft.dimensions, data);
            draft.rescaleAnchorOffset(oldDimensions);
            break;
         }
         case "color":
            draft.color = parseColor(data.paramValue);
            break;
         case "targetTypes":
            draft.targetTypes = parseTargetTypes(data.paramValue);
            break;
         case "projectileSource":
            draft.projectileSource = parseEnum(ProjectileSource.class, data.paramValue, draft.projectileSource);
            break;
         case "enabled":
            draft.enabled = Boolean.TRUE.equals(data.paramBool);
            break;
         case "keepLoaded":
            draft.keepLoaded = Boolean.TRUE.equals(data.paramBool);
            break;
         case "cancelDelayedOnExit":
            draft.cancelDelayedOnExit = Boolean.TRUE.equals(data.paramBool);
            break;
         case "activationDelay":
            draft.activationDelay = data.paramNumericValue != null ? Math.max(0.0F, data.paramNumericValue.floatValue()) : draft.activationDelay;
            break;
         case "cooldown":
            draft.cooldown = data.paramNumericValue != null ? Math.max(0.0F, data.paramNumericValue.floatValue()) : draft.cooldown;
            break;
         case "cooldownMode":
            draft.cooldownMode = parseEnum(CooldownMode.class, data.paramValue, draft.cooldownMode);
            break;
         case "conditionTiming":
            draft.conditionTiming = parseEnum(ConditionTiming.class, data.paramValue, draft.conditionTiming);
            break;
         case "rejectionDelayMode":
            draft.rejectionDelayMode = parseEnum(RejectionDelayMode.class, data.paramValue, draft.rejectionDelayMode);
            break;
         default:
            updated = false;
      }

      if (updated) {
         draft.markDirty();
      }
   }

   private void updateDraftId(@Nonnull TriggerVolumeInspectorDrafts.VolumeDraft draft, @Nullable String value) {
      draft.id = value != null ? value.trim() : draft.id;
   }

   private void updateIdValidation(@Nonnull String value) {
      UICommandBuilder cmd = new UICommandBuilder();
      if (this.selectedTab == TriggerVolumeInspectorPage.InspectorTab.VOLUME) {
         this.setIdValidation(cmd, "#VolumeTab[1]", "id", value);
      }

      Message validationMessage = this.idValidationMessage(value);
      cmd.set("#SelectedNameValidation.Visible", validationMessage != null);
      if (validationMessage != null) {
         cmd.set("#SelectedNameValidation.Text", validationMessage);
      }

      this.sendUpdate(cmd, false);
   }

   private void updateGroupField(@Nonnull TriggerVolumeInspectorDrafts.GroupDraft draft, @Nonnull TriggerVolumeInspectorPage.PageData data) {
      boolean updated = true;
      switch (data.paramKey) {
         case "id":
            this.updateDraftId(draft, data.paramValue);
            break;
         case "position":
            setVec(draft.origin, data);
            break;
         case "color":
            draft.color = parsePackedColor(data.paramValue, draft.color);
            break;
         case "targetTypes":
            draft.targetTypes = parseTargetTypes(data.paramValue);
            break;
         case "enabled":
            draft.enabled = Boolean.TRUE.equals(data.paramBool);
            break;
         case "conditionTiming":
            draft.conditionTiming = parseEnum(ConditionTiming.class, data.paramValue, draft.conditionTiming);
            break;
         case "rejectionDelayMode":
            draft.rejectionDelayMode = parseEnum(RejectionDelayMode.class, data.paramValue, draft.rejectionDelayMode);
            break;
         default:
            updated = false;
      }

      if (updated) {
         draft.markDirty();
      }
   }

   private void updateDraftId(@Nonnull TriggerVolumeInspectorDrafts.GroupDraft draft, @Nullable String value) {
      draft.id = value != null ? value.trim() : draft.id;
   }

   private void onUpdateTag(@Nonnull TriggerVolumeInspectorPage.PageData data) {
      if (this.selectedId != null && data.tagKey != null) {
         String key = data.tagKey.trim();
         if (!key.isEmpty()) {
            this.currentTags().put(key, TaggedVolumeEffectUtil.normalizeTagValue(data.tagValues));
            this.markSelectedDraftDirty();
            UICommandBuilder cmd = new UICommandBuilder();
            UIEventBuilder evt = new UIEventBuilder();
            cmd.clear("#TagsList");
            this.buildTagsTab(cmd, evt);
            cmd.set("#TagKeyField.Value", "");
            cmd.set("#TagValuesField.Value", "");
            this.sendUpdate(cmd, evt, false);
         }
      }
   }

   private void onRemoveTag(@Nonnull TriggerVolumeInspectorPage.PageData data) {
      if (data.removeTagKey != null) {
         if (this.currentTags().remove(data.removeTagKey) != null) {
            this.markSelectedDraftDirty();
            this.rebuildAll();
         }
      }
   }

   private void onDeleteSelection() {
      if (this.selectedId != null) {
         this.hidePastePrefabPreview();
         if (this.selectedIsGroup) {
            this.deletedGroups.add(this.selectedId);
         } else {
            this.deletedVolumes.add(this.selectedId);
         }

         this.selectedId = null;
         this.rebuildAll();
      }
   }

   private void onTeleport(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
      if (this.selectedId != null && !this.selectedIsGroup) {
         TriggerVolumeManager manager = getManagerForWorld(this.selectedWorld);
         if (manager != null) {
            VolumeEntry entry = manager.getVolume(this.selectedId);
            if (entry != null && ref.isValid()) {
               TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
               if (transform != null) {
                  Vector3d destination = new Vector3d(entry.getPosition());
                  store.addComponent(ref, Teleport.getComponentType(), Teleport.createForPlayer(destination, transform.getRotation()));
                  this.playerRef.sendMessage(Message.translation("server.customUI.triggerVolumeInspector.teleported").param("name", this.selectedId));
                  this.close();
               }
            }
         }
      }
   }

   private void onSave() {
      TriggerVolumeManager manager = getManagerForWorld(this.selectedWorld);
      if (manager != null && this.saveDrafts(manager, true)) {
         this.skipSaveOnDismiss = true;
         this.close();
      } else {
         this.revertRejectedDraftIds();
         this.rebuildAll();
      }
   }

   private void clearDraftState() {
      this.volumeDrafts.clear();
      this.groupDrafts.clear();
      this.deletedVolumes.clear();
      this.deletedGroups.clear();
   }

   private void onDiscard() {
      this.skipSaveOnDismiss = true;
      this.close();
   }

   private boolean saveDrafts(@Nonnull TriggerVolumeManager manager, boolean notifyPlayer) {
      if (!this.validateDraftIds(manager)) {
         return false;
      }

      this.applyDeletes(manager);
      this.applyGroupDrafts(manager);
      this.applyVolumeDrafts(manager);
      manager.markSpatialDirty();
      manager.notifyViewers();
      if (notifyPlayer) {
         this.playerRef.sendMessage(Message.translation("server.customUI.triggerVolumeInspector.saved"));
      }

      return true;
   }

   private void revertRejectedDraftIds() {
      for (TriggerVolumeInspectorDrafts.GroupDraft draft : this.groupDrafts.values()) {
         if (!this.deletedGroups.contains(draft.originalId) && !this.isDraftIdGood(draft.id, draft.originalId, true)) {
            draft.id = draft.originalId;
            draft.markDirty();
         }
      }

      for (TriggerVolumeInspectorDrafts.VolumeDraft draft : this.volumeDrafts.values()) {
         if (!this.deletedVolumes.contains(draft.originalId) && !this.isDraftIdGood(draft.id, draft.originalId, false)) {
            draft.id = draft.originalId;
            draft.markDirty();
         }
      }
   }

   private boolean validateDraftIds(@Nonnull TriggerVolumeManager manager) {
      HashSet<String> used = new HashSet<>();

      for (TriggerVolumeInspectorDrafts.GroupDraft draft : this.groupDrafts.values()) {
         if (!this.deletedGroups.contains(draft.originalId)) {
            if (!this.isValidDraftId(draft.id)) {
               return false;
            }

            if (!used.add(draft.id) || manager.hasVolume(draft.id) || manager.hasGroup(draft.id) && !draft.originalId.equals(draft.id)) {
               this.playerRef.sendMessage(Message.translation("server.customUI.triggerVolumeInspector.idCollision").param("id", draft.id));
               return false;
            }
         }
      }

      for (TriggerVolumeInspectorDrafts.VolumeDraft draft : this.volumeDrafts.values()) {
         if (!this.deletedVolumes.contains(draft.originalId)) {
            if (!this.isValidDraftId(draft.id)) {
               return false;
            }

            if (!used.add(draft.id) || manager.hasGroup(draft.id) || manager.hasVolume(draft.id) && !draft.originalId.equals(draft.id)) {
               this.playerRef.sendMessage(Message.translation("server.customUI.triggerVolumeInspector.idCollision").param("id", draft.id));
               return false;
            }
         }
      }

      return true;
   }

   private boolean isValidDraftId(@Nonnull String id) {
      if (isDraftIdFormatValid(id)) {
         return true;
      }

      this.playerRef.sendMessage(Message.translation("server.customUI.triggerVolumeInspector.invalidId"));
      return false;
   }

   private static boolean isDraftIdFormatValid(@Nonnull String id) {
      return VALID_ID.matcher(id).matches();
   }

   @Nullable
   private Message idValidationMessage(@Nonnull String id) {
      if (!isDraftIdFormatValid(id)) {
         return Message.translation("server.customUI.triggerVolumeInspector.invalidId");
      } else {
         return this.selectedId != null && this.hasDraftIdCollision(id, this.selectedId, this.selectedIsGroup)
            ? Message.translation("server.customUI.triggerVolumeInspector.idCollision").param("id", id)
            : null;
      }
   }

   private boolean isDraftIdGood(@Nonnull String id, @Nonnull String originalId, boolean group) {
      return isDraftIdFormatValid(id) && !this.hasDraftIdCollision(id, originalId, group);
   }

   private boolean hasDraftIdCollision(@Nonnull String id, @Nonnull String originalId, boolean group) {
      TriggerVolumeManager manager = this.getSelectedManager();
      if (manager == null) {
         return false;
      }

      for (TriggerVolumeInspectorDrafts.GroupDraft draft : this.groupDrafts.values()) {
         if (!this.deletedGroups.contains(draft.originalId) && (!group || !draft.originalId.equals(originalId)) && id.equals(draft.id)) {
            return true;
         }
      }

      for (TriggerVolumeInspectorDrafts.VolumeDraft draft : this.volumeDrafts.values()) {
         if (!this.deletedVolumes.contains(draft.originalId) && (group || !draft.originalId.equals(originalId)) && id.equals(draft.id)) {
            return true;
         }
      }

      return group
         ? manager.hasVolume(id) || manager.hasGroup(id) && !originalId.equals(id)
         : manager.hasGroup(id) || manager.hasVolume(id) && !originalId.equals(id);
   }

   private void applyDeletes(@Nonnull TriggerVolumeManager manager) {
      for (String volumeId : this.deletedVolumes) {
         VolumeEntry volume = manager.getVolume(volumeId);
         if (volume != null) {
            if (volume.getGroupId() != null) {
               GroupEntry group = manager.getGroup(volume.getGroupId());
               if (group != null) {
                  group.removeMember(volumeId);
               }
            }

            manager.unregister(volumeId);
            manager.notifyViewersRemove(volumeId);
         }
      }

      for (String groupId : this.deletedGroups) {
         GroupEntry group = manager.getGroup(groupId);
         if (group != null) {
            for (String memberId : new ArrayList<>(group.getMemberVolumeIds())) {
               VolumeEntry volume = manager.getVolume(memberId);
               if (volume != null) {
                  volume.setGroupId(null);
                  manager.notifyViewersAdd(volume);
               }
            }

            manager.unregisterGroup(groupId);
         }
      }
   }

   private void applyGroupDrafts(@Nonnull TriggerVolumeManager manager) {
      for (TriggerVolumeInspectorDrafts.GroupDraft draft : this.groupDrafts.values()) {
         if (!this.deletedGroups.contains(draft.originalId) && draft.dirty) {
            GroupEntry group = manager.getGroup(draft.originalId);
            if (group != null) {
               if (!draft.originalId.equals(draft.id)) {
                  manager.unregisterGroup(draft.originalId);
                  manager.registerGroup(draft.id, group);

                  for (VolumeEntry volume : manager.getVolumesMap().values()) {
                     if (draft.originalId.equals(volume.getGroupId())) {
                        volume.setGroupId(draft.id);
                     }
                  }

                  TriggerVolumeInspectorDrafts.remapVolumeGroupId(this.volumeDrafts.values(), draft.originalId, draft.id);
               }

               draft.applyTo(group);
            }
         }
      }
   }

   private void applyVolumeDrafts(@Nonnull TriggerVolumeManager manager) {
      for (TriggerVolumeInspectorDrafts.VolumeDraft draft : this.volumeDrafts.values()) {
         if (!this.deletedVolumes.contains(draft.originalId) && draft.dirty) {
            VolumeEntry volume = manager.getVolume(draft.originalId);
            if (volume != null) {
               String oldId = draft.originalId;
               String oldGroupId = volume.getGroupId();
               if (!oldId.equals(draft.id)) {
                  volume = manager.renameVolume(oldId, draft.id);
                  if (volume == null) {
                     continue;
                  }

                  manager.notifyViewersRemove(oldId);
               }

               draft.applyTo(volume);
               if (oldGroupId != null && !oldGroupId.equals(draft.groupId)) {
                  GroupEntry oldGroup = manager.getGroup(oldGroupId);
                  if (oldGroup != null) {
                     oldGroup.removeMember(oldId);
                  }
               }

               if (draft.groupId != null) {
                  GroupEntry group = manager.getGroup(draft.groupId);
                  if (group != null) {
                     group.addMember(draft.id);
                  }
               }

               manager.notifyViewersAdd(volume);
            }
         }
      }
   }

   private void buildAddEffectDropdown(@Nonnull UICommandBuilder cmd) {
      boolean isCondition = this.addTargetKind == TriggerVolumeInspectorPage.EffectListKind.CONDITION;
      List<String> typeIds = isCondition ? getSortedConditionTypeIds() : getSortedTypeIds();
      ObjectArrayList<DropdownEntryInfo> entries = new ObjectArrayList<>();

      for (String typeId : typeIds) {
         entries.add(new DropdownEntryInfo(this.typeDropdownLabel(typeId, isCondition), typeId));
      }

      cmd.set("#AddEffectDropdown.Entries", entries);
      String selected = !this.addEffectType.isEmpty() && typeIds.contains(this.addEffectType) ? this.addEffectType : (typeIds.isEmpty() ? "" : typeIds.get(0));
      if (!selected.isEmpty()) {
         cmd.set("#AddEffectDropdown.Value", selected);
      }
   }

   private void buildAddEventTypeDropdown(@Nonnull UICommandBuilder cmd) {
      ObjectArrayList<DropdownEntryInfo> entries = new ObjectArrayList<>();

      for (TriggerEventType eventType : TriggerEventType.values()) {
         entries.add(
            new DropdownEntryInfo(
               LocalizableString.fromMessageId("server.customUI.triggerVolumeEffectEditor.addEventType." + eventType.name()), eventType.name()
            )
         );
      }

      cmd.set("#AddEventTypeDropdown.Entries", entries);
      cmd.set("#AddEventTypeDropdown.Value", this.addEventType.name());
   }

   private void buildAddEntryDropdown(@Nonnull UICommandBuilder cmd) {
      IntOpenHashSet entryIds = new IntOpenHashSet();
      entryIds.add(0);
      entryIds.add(this.addEntry);

      for (int existing : this.getEventEntries(TriggerVolumeInspectorPage.EventCategoryScope.VOLUME, null, this.addEventType)) {
         entryIds.add(existing);
      }

      int[] sorted = entryIds.toIntArray();
      Arrays.sort(sorted);
      ObjectArrayList<DropdownEntryInfo> entries = new ObjectArrayList<>();

      for (int entryId : sorted) {
         entries.add(
            new DropdownEntryInfo(
               LocalizableString.fromMessageId("server.customUI.triggerVolumeEffectEditor.addEntry.option", Map.of("entry", String.valueOf(entryId + 1))),
               String.valueOf(entryId)
            )
         );
      }

      entries.add(new DropdownEntryInfo(LocalizableString.fromMessageId("server.customUI.triggerVolumeEffectEditor.addEntry.new"), "NEW"));
      cmd.set("#AddEntryDropdown.Entries", entries);
      cmd.set("#AddEntryDropdown.Value", String.valueOf(this.addEntry));
   }

   private void buildAddTargetDropdown(@Nonnull UICommandBuilder cmd) {
      ObjectArrayList<DropdownEntryInfo> entries = new ObjectArrayList<>();
      entries.add(
         new DropdownEntryInfo(
            LocalizableString.fromMessageId("server.customUI.triggerVolumeEffectEditor.addTarget.conditions"),
            TriggerVolumeInspectorPage.EffectListKind.CONDITION.name()
         )
      );
      entries.add(
         new DropdownEntryInfo(
            LocalizableString.fromMessageId("server.customUI.triggerVolumeEffectEditor.addTarget.successEffects"),
            TriggerVolumeInspectorPage.EffectListKind.EFFECT.name()
         )
      );
      entries.add(
         new DropdownEntryInfo(
            LocalizableString.fromMessageId("server.customUI.triggerVolumeEffectEditor.addTarget.rejectionEffects"),
            TriggerVolumeInspectorPage.EffectListKind.REJECTION_EFFECT.name()
         )
      );
      cmd.set("#AddTargetDropdown.Entries", entries);
      cmd.set("#AddTargetDropdown.Value", this.addTargetKind.name());
   }

   private void buildEffectList(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt) {
      this.appendEffectList(cmd, evt, "#EffectListContainer");
      Object selectedItem = this.getSelectedItem();
      cmd.set("#DuplicateEffectButton.Disabled", selectedItem == null);
      cmd.set("#RemoveEffectButton.Disabled", selectedItem == null);
      cmd.set("#MoveEffectUpButton.Disabled", selectedItem == null || this.selectedEffectIndex <= 0);
      cmd.set("#MoveEffectDownButton.Disabled", selectedItem == null || this.selectedEffectIndex >= this.getItemCount(this.selectedKind) - 1);
   }

   private void appendEffectList(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, @Nonnull String container) {
      cmd.clear(container);
      int childIndex = 0;
      childIndex = this.appendInheritedGroupSections(cmd, evt, container, childIndex);
      childIndex = this.appendVolumeEffectsSection(cmd, container, childIndex, childIndex > 0 || this.selectedIsGroup);
      this.appendEventCategoryGroups(cmd, evt, container, childIndex, TriggerVolumeInspectorPage.EventCategoryScope.VOLUME, null);
   }

   @Nonnull
   private Message effectSectionLabel(@Nonnull TriggerVolumeInspectorPage.EffectListKind kind, boolean inherited) {
      String baseKey = switch (kind) {
         case CONDITION -> "conditions";
         case EFFECT -> "successEffects";
         case REJECTION_EFFECT -> "rejectionEffects";
      };
      String ownerKey = inherited ? "group" : "volume";
      return Message.translation("server.customUI.triggerVolumeEffectEditor." + ownerKey + "." + baseKey);
   }

   @Nonnull
   private Message effectRowLabel(int index, @Nonnull String typeId, boolean isCondition) {
      return Message.raw(index + ". ").insert(this.typeMessage(typeId, isCondition));
   }

   @Nonnull
   private Message typeMessage(@Nonnull String typeId, boolean isCondition) {
      String langKey = typeLangKey(typeId, isCondition);
      return messageExists(langKey) ? Message.translation(langKey) : Message.raw(humanizeTypeId(typeId));
   }

   @Nonnull
   private LocalizableString typeDropdownLabel(@Nonnull String typeId, boolean isCondition) {
      String langKey = typeLangKey(typeId, isCondition);
      return messageExists(langKey) ? LocalizableString.fromMessageId(langKey) : LocalizableString.fromString(humanizeTypeId(typeId));
   }

   @Nonnull
   private static String typeLangKey(@Nonnull String typeId, boolean isCondition) {
      return "server.customUI.triggerVolumeEffectEditor." + (isCondition ? "conditionType." : "effectType.") + typeId;
   }

   private int appendInheritedGroupSections(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, @Nonnull String container, int childIndex) {
      TriggerVolumeInspectorDrafts.GroupDraft groupDraft = this.selectedInheritedGroupDraft();
      if (groupDraft != null && (!groupDraft.conditions.isEmpty() || !groupDraft.rejectionEffects.isEmpty() || !groupDraft.effects.isEmpty())) {
         cmd.append(container, "Pages/TriggerVolume/TriggerVolumeInspectorEffectOwnerSectionLabel.ui");
         cmd.set(
            container + "[" + childIndex + "].Text",
            Message.translation("server.customUI.triggerVolumeEffectEditor.inheritedFromGroup").param("group", groupDraft.id)
         );
         return this.appendEventCategoryGroups(cmd, evt, container, ++childIndex, TriggerVolumeInspectorPage.EventCategoryScope.GROUP, groupDraft);
      } else {
         return childIndex;
      }
   }

   private int appendVolumeEffectsSection(@Nonnull UICommandBuilder cmd, @Nonnull String container, int childIndex, boolean showLabel) {
      if (this.currentConditions().isEmpty()
         && this.currentEffects(TriggerVolumeInspectorPage.EffectListKind.REJECTION_EFFECT).isEmpty()
         && this.currentEffects(TriggerVolumeInspectorPage.EffectListKind.EFFECT).isEmpty()) {
         return childIndex;
      }

      if (!showLabel) {
         return childIndex;
      }

      if (childIndex > 0 && !this.selectedIsGroup) {
         childIndex = this.appendEventCategorySpacer(cmd, container, childIndex);
      }

      cmd.append(container, "Pages/TriggerVolume/TriggerVolumeInspectorEffectOwnerSectionLabel.ui");
      Message label = this.selectedIsGroup
         ? Message.translation("server.customUI.triggerVolumeEffectEditor.groupEffects")
         : Message.translation("server.customUI.triggerVolumeEffectEditor.volumeEffects");
      cmd.set(container + "[" + childIndex + "].Text", label);
      return childIndex + 1;
   }

   private int appendEventCategoryGroups(
      @Nonnull UICommandBuilder cmd,
      @Nonnull UIEventBuilder evt,
      @Nonnull String container,
      int childIndex,
      @Nonnull TriggerVolumeInspectorPage.EventCategoryScope scope,
      @Nullable TriggerVolumeInspectorDrafts.GroupDraft groupDraft
   ) {
      for (TriggerEventType eventType : TriggerEventType.values()) {
         for (int entry : this.getEventEntries(scope, groupDraft, eventType)) {
            int totalCount = this.getEventCategoryItemCount(scope, groupDraft, eventType, entry);
            if (totalCount != 0) {
               childIndex = this.appendEventCategoryHeader(cmd, evt, container, childIndex, scope, eventType, entry, totalCount);
               if (!this.isEventCategoryCollapsed(scope, eventType, entry)) {
                  childIndex = this.appendEventSection(
                     cmd, evt, container, childIndex, scope, groupDraft, eventType, entry, TriggerVolumeInspectorPage.EffectListKind.CONDITION
                  );
                  childIndex = this.appendEventSection(
                     cmd, evt, container, childIndex, scope, groupDraft, eventType, entry, TriggerVolumeInspectorPage.EffectListKind.REJECTION_EFFECT
                  );
                  childIndex = this.appendEventSection(
                     cmd, evt, container, childIndex, scope, groupDraft, eventType, entry, TriggerVolumeInspectorPage.EffectListKind.EFFECT
                  );
                  childIndex = this.appendEventCategorySpacer(cmd, container, childIndex);
               }
            }
         }
      }

      return childIndex;
   }

   private int appendEventCategoryHeader(
      @Nonnull UICommandBuilder cmd,
      @Nonnull UIEventBuilder evt,
      @Nonnull String container,
      int childIndex,
      @Nonnull TriggerVolumeInspectorPage.EventCategoryScope scope,
      @Nonnull TriggerEventType eventType,
      int entry,
      int totalCount
   ) {
      String selector = container + "[" + childIndex + "]";
      String togglePrefix = this.isEventCategoryCollapsed(scope, eventType, entry) ? ">" : "v";
      cmd.append(container, "Pages/TriggerVolume/TriggerVolumeInspectorEventCategoryHeader.ui");
      cmd.set(selector + ".TextSpans", eventCategoryLabel(scope, eventType, entry).param("state", togglePrefix).param("count", totalCount));
      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         selector,
         new EventData()
            .append("Action", TriggerVolumeInspectorPage.Action.ToggleEventCategory.name())
            .append("EventType", eventType.name())
            .append("EventEntry", String.valueOf(entry))
            .append("EventCategoryScope", scope.name())
      );
      return childIndex + 1;
   }

   private int appendEventSection(
      @Nonnull UICommandBuilder cmd,
      @Nonnull UIEventBuilder evt,
      @Nonnull String container,
      int childIndex,
      @Nonnull TriggerVolumeInspectorPage.EventCategoryScope scope,
      @Nullable TriggerVolumeInspectorDrafts.GroupDraft groupDraft,
      @Nonnull TriggerEventType eventType,
      int entry,
      @Nonnull TriggerVolumeInspectorPage.EffectListKind kind
   ) {
      IntList indices = this.getEventItemIndices(scope, groupDraft, eventType, entry, kind);
      if (indices.isEmpty()) {
         return childIndex;
      }

      cmd.append(container, "Pages/TriggerVolume/TriggerVolumeInspectorEventSectionLabel.ui");
      cmd.set(container + "[" + childIndex + "] #Label.Text", this.effectSectionLabel(kind, scope == TriggerVolumeInspectorPage.EventCategoryScope.GROUP));
      childIndex++;

      for (int i = 0; i < indices.size(); i++) {
         int itemIndex = indices.getInt(i);
         String selector = container + "[" + childIndex + "]";
         String typeId = scope == TriggerVolumeInspectorPage.EventCategoryScope.GROUP
            ? getGroupItemTypeId(kind, itemIndex, groupDraft)
            : this.getItemTypeId(kind, itemIndex);
         Message label = this.effectRowLabel(i + 1, typeId, kind == TriggerVolumeInspectorPage.EffectListKind.CONDITION);
         cmd.append(container, "Pages/TriggerVolume/TriggerVolumeInspectorEffectRow.ui");
         cmd.set(selector + " #Label.TextSpans", label);
         if (scope == TriggerVolumeInspectorPage.EventCategoryScope.GROUP) {
            cmd.set(selector + ".Style", INHERITED_EFFECT_ROW_STYLE);
            cmd.set(selector + " #Label.Style", INHERITED_EFFECT_LABEL_STYLE);
         } else {
            boolean selectedEffect = kind == this.selectedKind && itemIndex == this.selectedEffectIndex;
            cmd.set(selector + ".Style", selectedEffect ? SELECTED_EFFECT_ROW_STYLE : NORMAL_EFFECT_ROW_STYLE);
            cmd.set(selector + " #Label.Style", selectedEffect ? SELECTED_EFFECT_LABEL_STYLE : NORMAL_EFFECT_LABEL_STYLE);
            evt.addEventBinding(
               CustomUIEventBindingType.Activating,
               selector,
               new EventData()
                  .append("Action", TriggerVolumeInspectorPage.Action.SelectEffect.name())
                  .append("EffectListKind", kind.name())
                  .append("EffectIndex", String.valueOf(itemIndex))
            );
         }

         childIndex++;
      }

      return childIndex;
   }

   private int appendEventCategorySpacer(@Nonnull UICommandBuilder cmd, @Nonnull String container, int childIndex) {
      cmd.append(container, "Pages/TriggerVolume/TriggerVolumeInspectorEventCategorySpacer.ui");
      return childIndex + 1;
   }

   @Nonnull
   private static Message eventCategoryLabel(@Nonnull TriggerVolumeInspectorPage.EventCategoryScope scope, @Nonnull TriggerEventType eventType, int entry) {
      Message label = Message.translation("server.customUI.triggerVolumeEffectEditor.eventCategory." + eventType.name());
      if (entry > 0) {
         label = label.insert(
            Message.translation("server.customUI.triggerVolumeEffectEditor.eventCategoryEntrySuffix").param("entry", String.valueOf(entry + 1))
         );
      }

      return scope == TriggerVolumeInspectorPage.EventCategoryScope.GROUP
         ? label.insert(Message.translation("server.customUI.triggerVolumeEffectEditor.eventCategoryInheritedSuffix"))
         : label;
   }

   @Nonnull
   private int[] getEventEntries(
      @Nonnull TriggerVolumeInspectorPage.EventCategoryScope scope,
      @Nullable TriggerVolumeInspectorDrafts.GroupDraft groupDraft,
      @Nonnull TriggerEventType eventType
   ) {
      if (scope == TriggerVolumeInspectorPage.EventCategoryScope.GROUP && groupDraft == null) {
         return EMPTY_ENTRIES;
      }

      IntOpenHashSet entries = new IntOpenHashSet();

      for (TriggerVolumeInspectorPage.EffectListKind kind : TriggerVolumeInspectorPage.EffectListKind.values()) {
         int count = scope == TriggerVolumeInspectorPage.EventCategoryScope.GROUP ? getGroupItemCount(kind, groupDraft) : this.getItemCount(kind);

         for (int i = 0; i < count; i++) {
            TriggerEventType itemEventType = scope == TriggerVolumeInspectorPage.EventCategoryScope.GROUP
               ? getGroupItemEventType(kind, i, groupDraft)
               : this.getItemEventType(kind, i);
            if (normalizeEventType(itemEventType) == eventType) {
               entries.add(scope == TriggerVolumeInspectorPage.EventCategoryScope.GROUP ? getGroupItemEntry(kind, i, groupDraft) : this.getItemEntry(kind, i));
            }
         }
      }

      int[] array = entries.toIntArray();
      Arrays.sort(array);
      return array;
   }

   private int getEventCategoryItemCount(
      @Nonnull TriggerVolumeInspectorPage.EventCategoryScope scope,
      @Nullable TriggerVolumeInspectorDrafts.GroupDraft groupDraft,
      @Nonnull TriggerEventType eventType,
      int entry
   ) {
      int totalCount = 0;

      for (TriggerVolumeInspectorPage.EffectListKind kind : TriggerVolumeInspectorPage.EffectListKind.values()) {
         totalCount += this.getEventItemIndices(scope, groupDraft, eventType, entry, kind).size();
      }

      return totalCount;
   }

   @Nonnull
   private IntList getEventItemIndices(
      @Nonnull TriggerVolumeInspectorPage.EventCategoryScope scope,
      @Nullable TriggerVolumeInspectorDrafts.GroupDraft groupDraft,
      @Nonnull TriggerEventType eventType,
      int entry,
      @Nonnull TriggerVolumeInspectorPage.EffectListKind kind
   ) {
      IntArrayList indices = new IntArrayList();
      if (scope == TriggerVolumeInspectorPage.EventCategoryScope.GROUP && groupDraft == null) {
         return indices;
      }

      int count = scope == TriggerVolumeInspectorPage.EventCategoryScope.GROUP ? getGroupItemCount(kind, groupDraft) : this.getItemCount(kind);

      for (int i = 0; i < count; i++) {
         TriggerEventType itemEventType = scope == TriggerVolumeInspectorPage.EventCategoryScope.GROUP
            ? getGroupItemEventType(kind, i, groupDraft)
            : this.getItemEventType(kind, i);
         if (normalizeEventType(itemEventType) == eventType) {
            int itemEntry = scope == TriggerVolumeInspectorPage.EventCategoryScope.GROUP ? getGroupItemEntry(kind, i, groupDraft) : this.getItemEntry(kind, i);
            if (itemEntry == entry) {
               indices.add(i);
            }
         }
      }

      return indices;
   }

   private boolean isEventCategoryCollapsed(@Nonnull TriggerVolumeInspectorPage.EventCategoryScope scope, @Nonnull TriggerEventType eventType, int entry) {
      TriggerVolumeInspectorPage.EventCategoryKey key = new TriggerVolumeInspectorPage.EventCategoryKey(eventType, entry);
      return scope == TriggerVolumeInspectorPage.EventCategoryScope.GROUP
         ? !this.expandedGroupEventCategories.contains(key)
         : this.collapsedVolumeEventCategories.contains(key);
   }

   private void setEventCategoryExpanded(@Nonnull TriggerVolumeInspectorPage.EventCategoryScope scope, @Nonnull TriggerEventType eventType, int entry) {
      TriggerVolumeInspectorPage.EventCategoryKey key = new TriggerVolumeInspectorPage.EventCategoryKey(eventType, entry);
      if (scope == TriggerVolumeInspectorPage.EventCategoryScope.GROUP) {
         this.expandedGroupEventCategories.add(key);
      } else {
         this.collapsedVolumeEventCategories.remove(key);
      }
   }

   private void toggleEventCategory(@Nonnull TriggerVolumeInspectorPage.EventCategoryScope scope, @Nonnull TriggerEventType eventType, int entry) {
      TriggerVolumeInspectorPage.EventCategoryKey key = new TriggerVolumeInspectorPage.EventCategoryKey(eventType, entry);
      if (scope == TriggerVolumeInspectorPage.EventCategoryScope.GROUP) {
         if (!this.expandedGroupEventCategories.remove(key)) {
            this.expandedGroupEventCategories.add(key);
         }
      } else if (!this.collapsedVolumeEventCategories.remove(key)) {
         this.collapsedVolumeEventCategories.add(key);
      }
   }

   @Nonnull
   private static TriggerEventType normalizeEventType(@Nullable TriggerEventType eventType) {
      return eventType != null ? eventType : TriggerEventType.ENTER;
   }

   private void buildEffectDetailPanel(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt) {
      cmd.clear("#EffectDetailPanel");
      Object selected = this.getSelectedItem();
      if (selected == null) {
         cmd.set("#NoEffectSelectionLabel.Visible", true);
         cmd.set("#EffectDetailPanel.Visible", false);
      } else {
         cmd.set("#NoEffectSelectionLabel.Visible", false);
         cmd.set("#EffectDetailPanel.Visible", true);
         int row = 0;
         if (selected instanceof TriggerCondition condition) {
            String typeId = getConditionTypeId(condition);
            String eventValue = condition.getEventType() != null ? condition.getEventType().name() : TriggerEventType.ENTER.name();
            row = this.appendBaseFieldHeader(cmd, evt, row, true, eventValue, String.valueOf(condition.getEntry()), "0", "0");
            BuilderCodec<TriggerCondition> codec = getConditionBuilderCodecFor(typeId);
            if (codec != null) {
               BsonDocument encoded = encodeCondition(codec, condition);

               for (Entry<String, List<BuilderField<TriggerCondition, ?>>> entry : codec.getEntries().entrySet()) {
                  String key = entry.getKey();
                  if (!"Event".equals(key) && !"Entry".equals(key) && !entry.getValue().isEmpty()) {
                     BuilderField<TriggerCondition, ?> field = entry.getValue().getLast();
                     row = this.addEffectFieldRow(cmd, evt, row, typeId, key, field.getCodec().getChildCodec(), encoded.get(key));
                  }
               }
            }
         } else {
            TriggerEffect effect = (TriggerEffect)selected;
            String typeId = getTypeId(effect);
            String eventValue = effect.getEventType() != null ? effect.getEventType().name() : TriggerEventType.ENTER.name();
            row = this.appendBaseFieldHeader(
               cmd, evt, row, false, eventValue, String.valueOf(effect.getEntry()), String.valueOf(effect.getDelay()), String.valueOf(effect.getInterval())
            );
            BuilderCodec<TriggerEffect> codec = getBuilderCodecFor(typeId);
            if (codec != null) {
               BsonDocument encoded = encodeEffect(codec, effect);

               for (Entry<String, List<BuilderField<TriggerEffect, ?>>> entry : codec.getEntries().entrySet()) {
                  String key = entry.getKey();
                  if (!"Event".equals(key) && !"Interval".equals(key) && !"Delay".equals(key) && !"Entry".equals(key) && !entry.getValue().isEmpty()) {
                     BuilderField<TriggerEffect, ?> field = entry.getValue().getLast();
                     row = this.addEffectFieldRow(cmd, evt, row, typeId, key, field.getCodec().getChildCodec(), encoded.get(key));
                  }
               }
            }

            if (effect instanceof PastePrefabEffect) {
               this.addPastePrefabPreviewButtonRow(cmd, evt, row);
            }
         }
      }
   }

   private int appendBaseFieldHeader(
      @Nonnull UICommandBuilder cmd,
      @Nonnull UIEventBuilder evt,
      int row,
      boolean isCondition,
      @Nonnull String eventValue,
      @Nonnull String entryValue,
      @Nonnull String delayValue,
      @Nonnull String intervalValue
   ) {
      String sel = "#EffectDetailPanel[" + row + "]";
      cmd.append("#EffectDetailPanel", "Pages/TriggerVolume/TriggerVolumeInspectorBaseHeaderGrid.ui");
      String eventCell = sel + " #EventCell";
      cmd.set(eventCell + " #Label.Text", Message.translation("server.customUI.triggerVolumeEffectEditor.baseField.event"));
      cmd.set(eventCell + " #Label.TooltipText", Message.translation("server.customUI.triggerVolumeEffectEditor.baseField.event.tooltip"));
      ObjectArrayList<DropdownEntryInfo> entries = new ObjectArrayList<>();

      for (TriggerEventType eventType : TriggerEventType.values()) {
         String name = eventType.name();
         entries.add(new DropdownEntryInfo(this.optionLabel("baseField", "Event", name), name, this.optionTooltip("baseField", "Event", name)));
      }

      cmd.set(eventCell + " #Input.Entries", entries);
      cmd.set(eventCell + " #Input.Value", eventValue);
      evt.addEventBinding(CustomUIEventBindingType.ValueChanged, eventCell + " #Input", paramEvent("Event", eventCell + " #Input.Value"), false);
      this.setBaseNumberCell(cmd, evt, sel + " #EntryCell", "Entry", "baseField.entry", entryValue);
      evt.addEventBinding(
         CustomUIEventBindingType.FocusLost,
         sel + " #EntryCell #Input",
         new EventData().append("Action", TriggerVolumeInspectorPage.Action.CommitEntry.name()),
         false
      );
      if (isCondition) {
         cmd.set(sel + " #SecondRow.Visible", false);
      } else {
         this.setBaseNumberCell(cmd, evt, sel + " #DelayCell", "Delay", "effectDelay", delayValue);
         this.setBaseNumberCell(cmd, evt, sel + " #IntervalCell", "Interval", "baseField.interval", intervalValue);
      }

      return row + 1;
   }

   private void setBaseNumberCell(
      @Nonnull UICommandBuilder cmd,
      @Nonnull UIEventBuilder evt,
      @Nonnull String cell,
      @Nonnull String paramKey,
      @Nonnull String labelKey,
      @Nonnull String value
   ) {
      cmd.set(cell + " #Label.Text", Message.translation("server.customUI.triggerVolumeEffectEditor." + labelKey));
      cmd.set(cell + " #Label.TooltipText", Message.translation("server.customUI.triggerVolumeEffectEditor." + labelKey + ".tooltip"));

      double numeric;
      try {
         numeric = Double.parseDouble(value);
      } catch (NumberFormatException exception) {
         numeric = 0.0;
      }

      cmd.set(cell + " #Input.Value", numeric);
      evt.addEventBinding(CustomUIEventBindingType.ValueChanged, cell + " #Input", numericParamEvent(paramKey, cell + " #Input.Value"), false);
   }

   private int addEffectFieldRow(
      @Nonnull UICommandBuilder cmd,
      @Nonnull UIEventBuilder evt,
      int row,
      @Nonnull String typeId,
      @Nonnull String key,
      @Nonnull Codec<?> childCodec,
      @Nullable BsonValue bsonValue
   ) {
      if (childCodec == Codec.BOOLEAN) {
         boolean value = bsonValue instanceof BsonBoolean bsonBoolean && bsonBoolean.getValue();
         return this.addEffectCheckboxRow(cmd, evt, row, typeId, key, value);
      }

      if (childCodec == Codec.FLOAT) {
         String value = bsonValue instanceof BsonDouble bsonDouble ? String.valueOf((float)bsonDouble.getValue()) : "0.0";
         return this.addEffectNumberRow(cmd, evt, row, typeId, key, value, 2);
      }

      if (childCodec == Codec.DOUBLE) {
         String value = bsonValue instanceof BsonDouble bsonDouble ? String.valueOf(bsonDouble.getValue()) : "0.0";
         return this.addEffectNumberRow(cmd, evt, row, typeId, key, value, 2);
      }

      if (childCodec == Codec.INTEGER) {
         String value = bsonValue instanceof BsonInt32 bsonInt ? String.valueOf(bsonInt.getValue()) : "0";
         return this.addEffectNumberRow(cmd, evt, row, typeId, key, value, 0);
      }

      if (childCodec == Codec.LONG) {
         String value = bsonValue instanceof BsonInt64 bsonLong ? String.valueOf(bsonLong.getValue()) : "0";
         return this.addEffectNumberRow(cmd, evt, row, typeId, key, value, 0);
      }

      if (childCodec == Vector3dUtil.CODEC) {
         double x = 0.0;
         double y = 0.0;
         double z = 0.0;
         if (bsonValue instanceof BsonDocument doc) {
            x = doc.get("X", new BsonDouble(0.0)).asDouble().getValue();
            y = doc.get("Y", new BsonDouble(0.0)).asDouble().getValue();
            z = doc.get("Z", new BsonDouble(0.0)).asDouble().getValue();
         }

         return this.addEffectVec3Row(cmd, evt, row, typeId, key, x, y, z);
      } else if (!(childCodec instanceof EnumCodec<?> enumCodec)) {
         if (childCodec == Codec.STRING) {
            String value = bsonValue instanceof BsonString bsonString ? bsonString.getValue() : "";
            return getAssetSourceForField(typeId, key) != null
               ? this.addAssetPickerRow(cmd, evt, row, typeId, key, value)
               : this.addEffectTextRow(cmd, evt, row, typeId, key, value);
         } else if (childCodec == Codec.STRING_ARRAY) {
            String value = bsonValue instanceof BsonArray bsonArray
               ? String.join(", ", bsonArray.stream().filter(BsonString.class::isInstance).map(BsonString.class::cast).map(BsonString::getValue).toList())
               : "";
            return getAssetSourceForField(typeId, key) != null
               ? this.addAssetPickerRow(cmd, evt, row, typeId, key, value)
               : this.addEffectTextRow(cmd, evt, row, typeId, key, value);
         } else {
            String value = bsonValue != null ? bsonValueToString(bsonValue) : "";
            return this.addEffectTextRow(cmd, evt, row, typeId, key, value);
         }
      } else {
         String value = "";
         if (bsonValue != null) {
            try {
               Enum<?> decoded = enumCodec.decode(bsonValue, ExtraInfo.THREAD_LOCAL.get());
               value = enumCodec.getEnumKeys()[decoded.ordinal()];
            } catch (Exception exception) {
               value = bsonValue instanceof BsonString bsonString ? bsonString.getValue() : "";
            }
         }

         return this.addEffectDropdownRow(cmd, evt, row, typeId, key, List.of(enumCodec.getEnumKeys()), value);
      }
   }

   private int addEffectTextRow(
      @Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, int row, @Nonnull String typeId, @Nonnull String key, @Nonnull String value
   ) {
      String sel = "#EffectDetailPanel[" + row + "]";
      cmd.append("#EffectDetailPanel", "Pages/TriggerVolume/TriggerVolumeInspectorTextRow.ui");
      cmd.set(sel + " #Label.Text", fieldLabel(typeId, key));
      setEffectFieldTooltip(cmd, sel, typeId, key);
      setEffectFieldPlaceholder(cmd, sel, typeId, key);
      cmd.set(sel + " #Input.Value", value);
      evt.addEventBinding(CustomUIEventBindingType.ValueChanged, sel + " #Input", paramEvent(key, sel + " #Input.Value"), false);
      return row + 1;
   }

   private int addEffectNumberRow(
      @Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, int row, @Nonnull String typeId, @Nonnull String key, @Nonnull String value, int decimals
   ) {
      return this.addEffectNumberRow(cmd, evt, row, typeId, key, fieldLabel(typeId, key), null, value, decimals);
   }

   private int addEffectNumberRow(
      @Nonnull UICommandBuilder cmd,
      @Nonnull UIEventBuilder evt,
      int row,
      @Nonnull String typeId,
      @Nonnull String key,
      @Nonnull Object label,
      @Nullable Message tooltip,
      @Nonnull String value,
      int decimals
   ) {
      String sel = "#EffectDetailPanel[" + row + "]";
      cmd.append(
         "#EffectDetailPanel", decimals > 0 ? "Pages/TriggerVolume/TriggerVolumeInspectorNumberRow.ui" : "Pages/TriggerVolume/TriggerVolumeInspectorIntRow.ui"
      );
      if (label instanceof Message message) {
         cmd.set(sel + " #Label.Text", message);
      } else {
         cmd.set(sel + " #Label.Text", label.toString());
      }

      if (tooltip != null) {
         cmd.set(sel + " #Label.TooltipText", tooltip);
      } else {
         setEffectFieldTooltip(cmd, sel, typeId, key);
      }

      if (isNonNegativeNumericField(typeId, key)) {
         cmd.set(sel + " #Input.Format.MinValue", 0.0);
      }

      try {
         cmd.set(sel + " #Input.Value", Double.parseDouble(value));
      } catch (NumberFormatException exception) {
         cmd.set(sel + " #Input.Value", 0.0);
      }

      evt.addEventBinding(CustomUIEventBindingType.ValueChanged, sel + " #Input", numericParamEvent(key, sel + " #Input.Value"), false);
      return row + 1;
   }

   private int addEffectCheckboxRow(
      @Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, int row, @Nonnull String typeId, @Nonnull String key, boolean value
   ) {
      String sel = "#EffectDetailPanel[" + row + "]";
      cmd.append("#EffectDetailPanel", "Pages/TriggerVolume/TriggerVolumeInspectorCheckboxRow.ui");
      cmd.set(sel + " #Label.Text", fieldLabel(typeId, key));
      setEffectFieldTooltip(cmd, sel, typeId, key);
      cmd.set(sel + " #Input.Value", value);
      evt.addEventBinding(CustomUIEventBindingType.ValueChanged, sel + " #Input", boolParamEvent(key, sel + " #Input.Value"), false);
      return row + 1;
   }

   private int addEffectDropdownRow(
      @Nonnull UICommandBuilder cmd,
      @Nonnull UIEventBuilder evt,
      int row,
      @Nonnull String typeId,
      @Nonnull String key,
      @Nonnull List<String> options,
      @Nonnull String value
   ) {
      String sel = "#EffectDetailPanel[" + row + "]";
      cmd.append("#EffectDetailPanel", "Pages/TriggerVolume/TriggerVolumeInspectorDropdownRow.ui");
      cmd.set(sel + " #Label.Text", fieldLabel(typeId, key));
      setEffectFieldTooltip(cmd, sel, typeId, key);
      ObjectArrayList<DropdownEntryInfo> entries = new ObjectArrayList<>();

      for (String opt : options) {
         entries.add(new DropdownEntryInfo(this.optionLabel(typeId, key, opt), opt, this.optionTooltip(typeId, key, opt)));
      }

      cmd.set(sel + " #Input.Entries", entries);
      cmd.set(sel + " #Input.Value", value);
      evt.addEventBinding(CustomUIEventBindingType.ValueChanged, sel + " #Input", paramEvent(key, sel + " #Input.Value"), false);
      return row + 1;
   }

   @Nonnull
   private LocalizableString optionLabel(@Nonnull String typeId, @Nonnull String key, @Nonnull String value) {
      for (String optionKey : optionLangKeys(typeId, key, value, "")) {
         if (messageExists(optionKey)) {
            return LocalizableString.fromMessageId(optionKey);
         }
      }

      this.logMissingOptionKey(optionLangKeys(typeId, key, value, "").getFirst());
      return LocalizableString.fromString(humanizeEnumValue(value));
   }

   @Nullable
   private LocalizableString optionTooltip(@Nonnull String typeId, @Nonnull String key, @Nonnull String value) {
      for (String optionKey : optionLangKeys(typeId, key, value, ".tooltip")) {
         if (messageExists(optionKey)) {
            return LocalizableString.fromMessageId(optionKey);
         }
      }

      return null;
   }

   @Nonnull
   private static List<String> optionLangKeys(@Nonnull String typeId, @Nonnull String key, @Nonnull String value, @Nonnull String suffix) {
      String upperSnakeValue = toUpperSnake(value);
      String upperValue = value.toUpperCase(Locale.ROOT);
      ArrayList<String> keys = new ArrayList<>();
      appendOptionLangKeys(
         keys, "server.customUI.triggerVolumeEffectEditor.field." + typeId + "." + key + ".option.", value, upperSnakeValue, upperValue, suffix
      );
      appendOptionLangKeys(keys, "server.customUI.triggerVolumeEffectEditor.field.common." + key + ".option.", value, upperSnakeValue, upperValue, suffix);
      return keys;
   }

   private static void appendOptionLangKeys(
      @Nonnull List<String> keys,
      @Nonnull String prefix,
      @Nonnull String value,
      @Nonnull String upperSnakeValue,
      @Nonnull String upperValue,
      @Nonnull String suffix
   ) {
      keys.add(prefix + value + suffix);
      if (!upperSnakeValue.equals(value)) {
         keys.add(prefix + upperSnakeValue + suffix);
      }

      if (!upperValue.equals(value) && !upperValue.equals(upperSnakeValue)) {
         keys.add(prefix + upperValue + suffix);
      }
   }

   private static boolean messageExists(@Nonnull String langKey) {
      I18nModule i18n = I18nModule.get();
      return i18n != null && i18n.getMessage("en-US", langKey) != null;
   }

   private void logMissingOptionKey(@Nonnull String langKey) {
      if (this.missingOptionLangKeys.add(langKey)) {
         LOGGER.at(Level.FINE).log("Missing trigger volume dropdown option label '%s'", langKey);
      }
   }

   @Nonnull
   private static String humanizeEnumValue(@Nonnull String value) {
      String normalized = toUpperSnake(value);
      if (normalized.startsWith("PERCENT") && normalized.length() > "PERCENT".length()) {
         normalized = "PERCENT_" + normalized.substring("PERCENT".length());
      }

      String[] words = normalized.toLowerCase(Locale.ROOT).split("_+");
      StringBuilder result = new StringBuilder();

      for (String word : words) {
         if (!word.isEmpty()) {
            if (!result.isEmpty()) {
               result.append(' ');
            }

            result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
         }
      }

      return result.isEmpty() ? value : result.toString();
   }

   @Nonnull
   private static String toUpperSnake(@Nonnull String value) {
      StringBuilder result = new StringBuilder();
      char previous = 0;

      for (int charIndex = 0; charIndex < value.length(); charIndex++) {
         char character = value.charAt(charIndex);
         if (character == '_') {
            if (!result.isEmpty() && result.charAt(result.length() - 1) != '_') {
               result.append('_');
            }
         } else {
            if (charIndex > 0 && Character.isUpperCase(character) && (Character.isLowerCase(previous) || Character.isDigit(previous))) {
               result.append('_');
            }

            result.append(Character.toUpperCase(character));
         }

         previous = character;
      }

      return result.toString();
   }

   private int addEffectVec3Row(
      @Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, int row, @Nonnull String typeId, @Nonnull String key, double x, double y, double z
   ) {
      String sel = "#EffectDetailPanel[" + row + "]";
      cmd.append("#EffectDetailPanel", "Pages/TriggerVolume/TriggerVolumeInspectorVec3Row.ui");
      cmd.set(sel + " #Label.Text", fieldLabel(typeId, key));
      setEffectFieldTooltip(cmd, sel, typeId, key);
      cmd.set(sel + " #X.Value", x);
      cmd.set(sel + " #Y.Value", y);
      cmd.set(sel + " #Z.Value", z);

      for (String comp : List.of("X", "Y", "Z")) {
         evt.addEventBinding(
            CustomUIEventBindingType.ValueChanged,
            sel + " #" + comp,
            new EventData()
               .append("Action", TriggerVolumeInspectorPage.Action.UpdateParameter.name())
               .append("ParamKey", key)
               .append("@VecX", sel + " #X.Value")
               .append("@VecY", sel + " #Y.Value")
               .append("@VecZ", sel + " #Z.Value"),
            false
         );
      }

      return row + 1;
   }

   private int addAssetPickerRow(
      @Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, int row, @Nonnull String typeId, @Nonnull String key, @Nonnull String value
   ) {
      String sel = "#EffectDetailPanel[" + row + "]";
      cmd.append("#EffectDetailPanel", "Pages/TriggerVolume/TriggerVolumeInspectorAssetPickerRow.ui");
      cmd.set(sel + " #Label.Text", fieldLabel(typeId, key));
      setEffectFieldTooltip(cmd, sel, typeId, key);
      if (value.isEmpty()) {
         cmd.set(sel + " #PickerLabel.Text", Message.translation("server.customUI.triggerVolumeEffectEditor.assetPicker.none"));
      } else if (this.isUnsupportedAnimation(typeId, key, value)) {
         cmd.set(
            sel + " #PickerLabel.Text",
            Message.translation("server.customUI.triggerVolumeEffectEditor.assetPicker.animationUnsupported").param("animation", value)
         );
      } else if (this.isLoopingAnimation(typeId, key, value)) {
         cmd.set(
            sel + " #PickerLabel.Text", Message.translation("server.customUI.triggerVolumeEffectEditor.assetPicker.animationLooping").param("animation", value)
         );
      } else if (isApplyOnField(typeId, key)) {
         this.setApplyOnLabel(cmd, sel + " #PickerLabel.Text", value);
      } else {
         cmd.set(sel + " #PickerLabel.Text", value);
      }

      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         sel + " #PickerButton",
         new EventData().append("Action", TriggerVolumeInspectorPage.Action.OpenAssetPicker.name()).append("ParamKey", key)
      );
      return row + 1;
   }

   private boolean isUnsupportedAnimation(@Nonnull String typeId, @Nonnull String key, @Nonnull String value) {
      if ("PlayAnimation".equals(typeId) && "Animation".equals(key)) {
         if (this.getSelectedItem() instanceof PlayAnimationEffect animation) {
            String applyOn = animation.getNpcType();
            return applyOn.isBlank() ? false : !TriggerVolumesPlugin.get().collectAnimationIdsForApplyOn(applyOn).contains(value);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean isUnsupportedApplyOn(@Nonnull String typeId, @Nonnull String key, @Nonnull String value) {
      if ("PlayAnimation".equals(typeId) && "NpcType".equals(key) && !value.isBlank()) {
         if (this.getSelectedItem() instanceof PlayAnimationEffect animation) {
            String animationKey = animation.getAnimation();
            return animationKey != null && !animationKey.isBlank()
               ? !TriggerVolumesPlugin.get().collectApplyOnIdsForAnimation(animationKey).contains(value)
               : false;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean isLoopingAnimation(@Nonnull String typeId, @Nonnull String key, @Nonnull String value) {
      if ("PlayAnimation".equals(typeId) && "Animation".equals(key)) {
         return this.getSelectedItem() instanceof PlayAnimationEffect animation
            ? TriggerVolumesPlugin.get().isLoopingAnimation(animation.getNpcType(), value)
            : false;
      } else {
         return false;
      }
   }

   private static boolean isApplyOnField(@Nonnull String typeId, @Nonnull String key) {
      return "PlayAnimation".equals(typeId) && "NpcType".equals(key);
   }

   private static boolean isApplyOnPseudo(@Nonnull String value) {
      return "Player".equals(value) || "Everyone".equals(value);
   }

   private void setApplyOnLabel(@Nonnull UICommandBuilder cmd, @Nonnull String selector, @Nonnull String value) {
      boolean pseudo = isApplyOnPseudo(value);
      String langKey = "server.customUI.triggerVolumeEffectEditor.applyOn." + value;
      if (this.isUnsupportedApplyOn("PlayAnimation", "NpcType", value)) {
         if (pseudo) {
            cmd.set(selector, Message.translation(langKey + "Unsupported"));
         } else {
            cmd.set(selector, Message.translation("server.customUI.triggerVolumeEffectEditor.assetPicker.targetUnsupported").param("target", value));
         }
      } else if (pseudo) {
         cmd.set(selector, Message.translation(langKey));
      } else {
         cmd.set(selector, value);
      }
   }

   private int addPastePrefabPreviewButtonRow(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, int row) {
      String sel = "#EffectDetailPanel[" + row + "]";
      cmd.append("#EffectDetailPanel", "Common/TextButton.ui");
      Message label = this.isPreviewingSelectedEffect()
         ? Message.translation("server.customUI.triggerVolumeEffectEditor.pastePrefab.preview.hide")
         : Message.translation("server.customUI.triggerVolumeEffectEditor.pastePrefab.preview.show");
      cmd.set(sel + " #Button.Text", label);
      cmd.set(sel + " #Button.TooltipText", Message.translation("server.customUI.triggerVolumeEffectEditor.pastePrefab.preview.tooltip"));
      evt.addEventBinding(
         CustomUIEventBindingType.Activating, sel + " #Button", new EventData().append("Action", TriggerVolumeInspectorPage.Action.TogglePrefabPreview.name())
      );
      return row + 1;
   }

   private void onSelectEffect(@Nonnull TriggerVolumeInspectorPage.PageData data) {
      try {
         TriggerVolumeInspectorPage.EffectListKind newKind = data.effectListKind != null
            ? TriggerVolumeInspectorPage.EffectListKind.valueOf(data.effectListKind)
            : TriggerVolumeInspectorPage.EffectListKind.EFFECT;
         int newIndex = Integer.parseInt(data.effectIndex);
         this.selectedKind = newKind;
         this.selectedEffectIndex = newIndex;
         this.syncAddControlsToSelectedEffect();
         this.rebuildAll();
      } catch (IllegalArgumentException exception) {
      }
   }

   private void syncAddControlsToSelectedEffect() {
      if (this.selectedEffectIndex >= 0 && this.selectedEffectIndex < this.getItemCount(this.selectedKind)) {
         TriggerEventType eventType;
         int entry;
         if (this.selectedKind == TriggerVolumeInspectorPage.EffectListKind.CONDITION) {
            TriggerCondition condition = this.currentConditions().get(this.selectedEffectIndex);
            eventType = condition.getEventType();
            entry = condition.getEntry();
         } else {
            TriggerEffect effect = this.currentEffects(this.selectedKind).get(this.selectedEffectIndex);
            eventType = effect.getEventType();
            entry = effect.getEntry();
         }

         this.addTargetKind = this.selectedKind;
         this.addEventType = eventType;
         this.addEntry = entry;
      }
   }

   private void onAddEffect(@Nonnull TriggerVolumeInspectorPage.PageData data) {
      if (data.effectType != null && !data.effectType.isBlank()) {
         TriggerVolumeInspectorPage.EffectListKind target = data.addTargetKind != null
            ? parseEffectListKind(data.addTargetKind, TriggerVolumeInspectorPage.EffectListKind.EFFECT)
            : this.addTargetKind;
         this.addTargetKind = target;
         TriggerEventType eventType = data.addEventType != null ? parseTriggerEventType(data.addEventType, this.addEventType) : this.addEventType;
         this.addEventType = eventType;
         int entry = this.resolveAddEntry(data.addEntry, eventType);
         this.addEntry = entry;
         if (target == TriggerVolumeInspectorPage.EffectListKind.CONDITION) {
            BuilderCodec<TriggerCondition> codec = getConditionBuilderCodecFor(data.effectType);
            if (codec == null) {
               this.playerRef.sendMessage(Message.translation("server.customUI.triggerVolumeEffectEditor.unknownType"));
            } else {
               TriggerCondition condition = codec.getSupplier().get();
               condition.setEventType(eventType);
               condition.setEntry(entry);
               materializeConditionDefaults(codec, condition);
               this.currentConditions().add(condition);
               this.markSelectedDraftDirty();
               this.selectedKind = TriggerVolumeInspectorPage.EffectListKind.CONDITION;
               this.selectedEffectIndex = this.currentConditions().size() - 1;
               this.setEventCategoryExpanded(TriggerVolumeInspectorPage.EventCategoryScope.VOLUME, eventType, entry);
               this.rebuildAll();
            }
         } else {
            BuilderCodec<TriggerEffect> codec = getBuilderCodecFor(data.effectType);
            if (codec == null) {
               this.playerRef.sendMessage(Message.translation("server.customUI.triggerVolumeEffectEditor.unknownType"));
            } else {
               TriggerEffect effect = codec.getSupplier().get();
               effect.setEventType(eventType);
               effect.setEntry(entry);
               materializeDefaults(codec, effect);
               this.currentEffects(target).add(effect);
               this.markSelectedDraftDirty();
               this.selectedKind = target;
               this.selectedEffectIndex = this.currentEffects(target).size() - 1;
               this.setEventCategoryExpanded(TriggerVolumeInspectorPage.EventCategoryScope.VOLUME, eventType, entry);
               this.rebuildAll();
            }
         }
      }
   }

   private int resolveAddEntry(@Nullable String raw, @Nonnull TriggerEventType eventType) {
      if ("NEW".equals(raw)) {
         return this.maxEntryForEvent(eventType) + 1;
      } else {
         return raw != null && !raw.isBlank() ? parseEntry(raw) : this.addEntry;
      }
   }

   private int maxEntryForEvent(@Nonnull TriggerEventType eventType) {
      int max = 0;

      for (int existing : this.getEventEntries(TriggerVolumeInspectorPage.EventCategoryScope.VOLUME, null, eventType)) {
         max = Math.max(max, existing);
      }

      return max;
   }

   private void onRemoveEffect() {
      if (this.selectedEffectIndex >= 0 && this.selectedEffectIndex < this.getItemCount(this.selectedKind)) {
         if (this.isPreviewingSelectedEffect()) {
            this.hidePastePrefabPreview();
         }

         if (this.selectedKind == TriggerVolumeInspectorPage.EffectListKind.CONDITION) {
            this.currentConditions().remove(this.selectedEffectIndex);
         } else {
            this.currentEffects(this.selectedKind).remove(this.selectedEffectIndex);
         }

         this.markSelectedDraftDirty();
         if (this.selectedEffectIndex >= this.getItemCount(this.selectedKind)) {
            this.selectedEffectIndex = this.getItemCount(this.selectedKind) - 1;
         }

         this.rebuildAll();
      }
   }

   private void onDuplicateEffect() {
      if (this.selectedEffectIndex >= 0 && this.selectedEffectIndex < this.getItemCount(this.selectedKind)) {
         this.hidePastePrefabPreview();
         if (this.selectedKind == TriggerVolumeInspectorPage.EffectListKind.CONDITION) {
            List<TriggerCondition> conditions = this.currentConditions();
            conditions.add(this.selectedEffectIndex + 1, TriggerCondition.deepCopy(conditions.get(this.selectedEffectIndex)));
         } else {
            List<TriggerEffect> effects = this.currentEffects(this.selectedKind);
            effects.add(this.selectedEffectIndex + 1, TriggerEffect.deepCopy(effects.get(this.selectedEffectIndex)));
         }

         this.markSelectedDraftDirty();
         this.selectedEffectIndex++;
         this.rebuildAll();
      }
   }

   private void onMoveEffect(int direction) {
      if (this.selectedEffectIndex >= 0 && this.selectedEffectIndex < this.getItemCount(this.selectedKind)) {
         this.hidePastePrefabPreview();
         int targetIndex = this.selectedEffectIndex + direction;
         if (targetIndex >= 0 && targetIndex < this.getItemCount(this.selectedKind)) {
            if (this.selectedKind == TriggerVolumeInspectorPage.EffectListKind.CONDITION) {
               Collections.swap(this.currentConditions(), this.selectedEffectIndex, targetIndex);
            } else {
               Collections.swap(this.currentEffects(this.selectedKind), this.selectedEffectIndex, targetIndex);
            }

            this.markSelectedDraftDirty();
            this.selectedEffectIndex = targetIndex;
            this.rebuildAll();
         }
      }
   }

   private void onUpdateAddTarget(@Nonnull TriggerVolumeInspectorPage.PageData data) {
      if (data.addTargetKind != null) {
         this.addTargetKind = parseEffectListKind(data.addTargetKind, TriggerVolumeInspectorPage.EffectListKind.EFFECT);
         this.rebuildAll();
      }
   }

   private void onUpdateAddEffectType(@Nonnull TriggerVolumeInspectorPage.PageData data) {
      if (data.effectType != null && !data.effectType.isBlank()) {
         this.addEffectType = data.effectType;
      }
   }

   private void onUpdateAddEventType(@Nonnull TriggerVolumeInspectorPage.PageData data) {
      if (data.addEventType != null) {
         this.addEventType = parseTriggerEventType(data.addEventType, this.addEventType);
         this.addEntry = 0;
         this.rebuildAll();
      }
   }

   private void onUpdateAddEntry(@Nonnull TriggerVolumeInspectorPage.PageData data) {
      this.addEntry = this.resolveAddEntry(data.addEntry, this.addEventType);
      this.rebuildAll();
   }

   private void onToggleEventCategory(@Nonnull TriggerVolumeInspectorPage.PageData data) {
      if (data.eventType != null && data.eventCategoryScope != null) {
         try {
            TriggerEventType eventType = TriggerEventType.valueOf(data.eventType);
            TriggerVolumeInspectorPage.EventCategoryScope scope = TriggerVolumeInspectorPage.EventCategoryScope.valueOf(data.eventCategoryScope);
            int entry = parseEntry(data.eventEntry);
            this.toggleEventCategory(scope, eventType, entry);
            this.rebuildAll();
         } catch (IllegalArgumentException var5) {
         }
      }
   }

   private static int parseEntry(@Nullable String value) {
      if (value != null && !value.isBlank()) {
         try {
            return Math.max(0, Integer.parseInt(value.trim()));
         } catch (NumberFormatException exception) {
            return 0;
         }
      } else {
         return 0;
      }
   }

   private void onUpdateParameter(@Nonnull TriggerVolumeInspectorPage.PageData data) {
      Object selected = this.getSelectedItem();
      if (selected != null && data.paramKey != null) {
         String key = data.paramKey;
         if ("Event".equals(key)) {
            try {
               TriggerEventType eventType = TriggerEventType.valueOf(data.paramValue);
               int itemEntry = selected instanceof TriggerCondition condition ? condition.getEntry() : ((TriggerEffect)selected).getEntry();
               if (selected instanceof TriggerCondition condition) {
                  condition.setEventType(eventType);
               } else {
                  ((TriggerEffect)selected).setEventType(eventType);
               }

               this.markSelectedDraftDirty();
               this.setEventCategoryExpanded(TriggerVolumeInspectorPage.EventCategoryScope.VOLUME, eventType, itemEntry);
            } catch (IllegalArgumentException var7) {
            }

            this.rebuildAll();
         } else if ("Entry".equals(key)) {
            if (data.paramNumericValue != null) {
               int entryValue = Math.max(0, data.paramNumericValue.intValue());
               TriggerEventType itemEventType = selected instanceof TriggerCondition condition
                  ? condition.getEventType()
                  : ((TriggerEffect)selected).getEventType();
               if (selected instanceof TriggerCondition condition) {
                  condition.setEntry(entryValue);
               } else {
                  ((TriggerEffect)selected).setEntry(entryValue);
               }

               this.markSelectedDraftDirty();
               this.setEventCategoryExpanded(TriggerVolumeInspectorPage.EventCategoryScope.VOLUME, normalizeEventType(itemEventType), entryValue);
            }
         } else if (selected instanceof TriggerCondition condition) {
            BuilderCodec<TriggerCondition> codec = getConditionBuilderCodecFor(getConditionTypeId(condition));
            if (codec != null) {
               this.applyCodecField(codec, condition, key, data);
               this.markSelectedDraftDirty();
            }
         } else {
            TriggerEffect effect = (TriggerEffect)selected;
            if ("Interval".equals(key)) {
               if (data.paramNumericValue != null) {
                  effect.setInterval(data.paramNumericValue.floatValue());
                  this.markSelectedDraftDirty();
               }
            } else if ("Delay".equals(key)) {
               if (data.paramNumericValue != null) {
                  effect.setDelay(data.paramNumericValue.floatValue());
                  this.markSelectedDraftDirty();
               }
            } else {
               BuilderCodec<TriggerEffect> codec = getBuilderCodecFor(getTypeId(effect));
               if (codec != null) {
                  this.applyCodecField(codec, effect, key, data);
                  this.markSelectedDraftDirty();
               }

               if (effect instanceof PastePrefabEffect && this.isPreviewingSelectedEffect()) {
                  if ("Position".equals(key) || "AtVolumeOrigin".equals(key)) {
                     this.refreshActivePastePrefabPreviewPosition();
                  } else if ("Prefab".equals(key) || "PrefabList".equals(key)) {
                     this.refreshActivePastePrefabPreview();
                  }
               }
            }
         }
      }
   }

   private void onCommitEntry() {
      this.rebuildAll();
   }

   private void onTogglePrefabPreview() {
      if (this.isPreviewingSelectedEffect()) {
         this.hidePastePrefabPreview();
         this.rebuildAll();
      } else {
         if (this.getActivePastePrefabPreviewState() != null) {
            this.hidePastePrefabPreview();
         }

         if (this.getSelectedItem() instanceof PastePrefabEffect pastePrefabEffect) {
            Vector3d previewPosition = this.getPastePrefabPreviewPosition(pastePrefabEffect);
            if (previewPosition == null) {
               this.playerRef.sendMessage(Message.translation("server.customUI.triggerVolumeEffectEditor.pastePrefab.preview.noPrefab"));
            } else if (this.sendPastePrefabPreview(pastePrefabEffect, true)) {
               TriggerVolumesPlugin.get()
                  .setPastePrefabPreviewState(
                     this.playerRef.getUuid(),
                     new TriggerVolumesPlugin.PastePrefabPreviewState(
                        this.selectedWorld, this.selectedId, this.selectedIsGroup, this.selectedKind.name(), this.selectedEffectIndex, previewPosition
                     )
                  );
               this.rebuildAll();
            }
         }
      }
   }

   private boolean sendPastePrefabPreview(@Nonnull PastePrefabEffect effect, boolean includePrefabData) {
      Vector3d previewPosition = this.getPastePrefabPreviewPosition(effect);
      return previewPosition == null ? false : this.sendPastePrefabPreview(effect, previewPosition, includePrefabData);
   }

   private boolean sendPastePrefabPreview(@Nonnull PastePrefabEffect effect, @Nonnull Vector3d previewPosition, boolean includePrefabData) {
      ShowTriggerVolumePastePrefabPreview packet = new ShowTriggerVolumePastePrefabPreview();
      packet.position = new Vector3f((float)previewPosition.x(), (float)previewPosition.y(), (float)previewPosition.z());
      if (includePrefabData) {
         Path prefabPath = this.resolvePastePrefabPath(effect);
         if (prefabPath == null) {
            this.playerRef.sendMessage(Message.translation("server.customUI.triggerVolumeEffectEditor.pastePrefab.preview.noPrefab"));
            return false;
         }

         BlockSelection selection;
         try {
            selection = PrefabStore.get().getPrefab(prefabPath);
         } catch (Exception exception) {
            LOGGER.at(Level.WARNING).log("Failed to load PastePrefab preview '%s'", prefabPath, exception);
            this.playerRef.sendMessage(Message.translation("server.customUI.triggerVolumeEffectEditor.pastePrefab.preview.loadFailed"));
            return false;
         }

         if (selection == null) {
            this.playerRef.sendMessage(Message.translation("server.customUI.triggerVolumeEffectEditor.pastePrefab.preview.loadFailed"));
            return false;
         }

         this.fillPreviewPacket(packet, selection);
      }

      this.playerRef.getPacketHandler().write(packet);
      TriggerVolumesPlugin.PastePrefabPreviewState activeState = this.getActivePastePrefabPreviewState();
      if (activeState != null) {
         activeState.setLastSentPosition(previewPosition);
      }

      return true;
   }

   private void fillPreviewPacket(@Nonnull ShowTriggerVolumePastePrefabPreview packet, @Nonnull BlockSelection selection) {
      EditorBlocksChange editorPacket = selection.toPacket();
      packet.blocksChange = editorPacket.blocksChange;
      packet.fluidsChange = editorPacket.fluidsChange;
      packet.entityChanges = editorPacket.entityChanges;
      this.applyTintFromPlayerPosition(packet);
   }

   private void applyTintFromPlayerPosition(@Nonnull ShowTriggerVolumePastePrefabPreview packet) {
      Ref<EntityStore> playerEntityRef = this.playerRef.getReference();
      if (playerEntityRef == null) {
         packet.biomeTint = DEFAULT_BIOME_TINT;
         packet.waterTint = DEFAULT_WATER_TINT;
      } else {
         Store<EntityStore> store = playerEntityRef.getStore();
         World world = store.getExternalData().getWorld();
         if (world == null) {
            packet.biomeTint = DEFAULT_BIOME_TINT;
            packet.waterTint = DEFAULT_WATER_TINT;
         } else {
            Vector3d playerPosition = this.playerRef.getTransform().getPosition();
            int blockX = MathUtil.floor(playerPosition.x);
            int blockY = MathUtil.floor(playerPosition.y);
            int blockZ = MathUtil.floor(playerPosition.z);
            long chunkIndex = ChunkUtil.indexChunkFromBlock(blockX, blockZ);
            WorldChunk chunk = world.getNonTickingChunk(chunkIndex);
            if (chunk != null && chunk.getBlockChunk() != null) {
               BlockChunk blockChunk = chunk.getBlockChunk();
               packet.biomeTint = blockChunk.getTint(blockX, blockZ);
               int environmentId = blockChunk.getEnvironment(blockX, blockY, blockZ);
               Environment environment = Environment.getAssetMap().getAsset(environmentId);
               if (environment != null) {
                  Color waterColor = environment.getWaterTint();
                  if (waterColor != null) {
                     packet.waterTint = (waterColor.red & 255) << 16 | (waterColor.green & 255) << 8 | waterColor.blue & 255;
                     return;
                  }
               }

               packet.waterTint = DEFAULT_WATER_TINT;
            } else {
               packet.biomeTint = DEFAULT_BIOME_TINT;
               packet.waterTint = DEFAULT_WATER_TINT;
            }
         }
      }
   }

   @Nullable
   private Path resolvePastePrefabPath(@Nonnull PastePrefabEffect effect) {
      String directPrefab = effect.getPrefabRelPath();
      if (directPrefab != null && !directPrefab.isBlank()) {
         return PastePrefabEffect.resolveDirectPrefabPath(directPrefab.trim());
      } else {
         String prefabListId = effect.getPrefabListId();
         if (prefabListId != null && !prefabListId.isBlank()) {
            PrefabListAsset prefabListAsset = PrefabListAsset.getAssetMap().getAsset(prefabListId);
            return prefabListAsset != null ? prefabListAsset.getRandomPrefab() : null;
         } else {
            return null;
         }
      }
   }

   @Nullable
   private Vector3d getPastePrefabPreviewPosition(@Nonnull PastePrefabEffect effect) {
      Vector3d origin = this.getCurrentPreviewOrigin();
      return origin == null ? null : this.getPastePrefabPreviewPosition(effect, origin);
   }

   @Nonnull
   private Vector3d getPastePrefabPreviewPosition(@Nonnull PastePrefabEffect effect, @Nonnull Vector3d origin) {
      Vector3d effectPosition = effect.getPosition();
      Vector3d previewPosition;
      if (effect.isAtVolumeOrigin()) {
         previewPosition = new Vector3d(origin);
         if (effectPosition != null) {
            previewPosition.add(effectPosition);
         }
      } else {
         previewPosition = effectPosition != null ? new Vector3d(effectPosition) : new Vector3d();
      }

      return new Vector3d(Math.floor(previewPosition.x()), Math.floor(previewPosition.y()), Math.floor(previewPosition.z()));
   }

   @Nullable
   private Vector3d getCurrentPreviewOrigin() {
      if (this.selectedIsGroup) {
         TriggerVolumeInspectorDrafts.GroupDraft groupDraft = this.selectedGroupDraft();
         return groupDraft != null ? groupDraft.origin : null;
      } else {
         TriggerVolumeInspectorDrafts.VolumeDraft volumeDraft = this.selectedVolumeDraft();
         return volumeDraft != null ? volumeDraft.position : null;
      }
   }

   @Nullable
   private Vector3d getPreviewOriginForState(@Nonnull TriggerVolumesPlugin.PastePrefabPreviewState state) {
      String selectedPreviewId = state.selectedId();
      if (selectedPreviewId == null) {
         return null;
      } else {
         TriggerVolumeManager manager = getManagerForWorld(state.worldName());
         if (manager == null) {
            return null;
         } else if (state.selectedIsGroup()) {
            GroupEntry group = manager.getGroup(selectedPreviewId);
            return group == null ? null : this.draftForGroup(group).origin;
         } else {
            VolumeEntry volume = manager.getVolume(selectedPreviewId);
            return volume == null ? null : this.draftForVolume(volume).position;
         }
      }
   }

   @Nullable
   private PastePrefabEffect getPastePrefabEffectForState(@Nonnull TriggerVolumesPlugin.PastePrefabPreviewState state) {
      String selectedPreviewId = state.selectedId();
      if (selectedPreviewId == null) {
         return null;
      }

      TriggerVolumeInspectorPage.EffectListKind previewKind;
      try {
         previewKind = TriggerVolumeInspectorPage.EffectListKind.valueOf(state.effectListKind());
      } catch (IllegalArgumentException exception) {
         return null;
      }

      if (previewKind == TriggerVolumeInspectorPage.EffectListKind.CONDITION) {
         return null;
      }

      TriggerVolumeManager manager = getManagerForWorld(state.worldName());
      if (manager == null) {
         return null;
      }

      List<TriggerEffect> effects;
      if (state.selectedIsGroup()) {
         GroupEntry group = manager.getGroup(selectedPreviewId);
         if (group == null) {
            return null;
         }

         TriggerVolumeInspectorDrafts.GroupDraft groupDraft = this.draftForGroup(group);
         effects = previewKind == TriggerVolumeInspectorPage.EffectListKind.REJECTION_EFFECT ? groupDraft.rejectionEffects : groupDraft.effects;
      } else {
         VolumeEntry volume = manager.getVolume(selectedPreviewId);
         if (volume == null) {
            return null;
         }

         TriggerVolumeInspectorDrafts.VolumeDraft volumeDraft = this.draftForVolume(volume);
         effects = previewKind == TriggerVolumeInspectorPage.EffectListKind.REJECTION_EFFECT ? volumeDraft.rejectionEffects : volumeDraft.effects;
      }

      int effectIndex = state.effectIndex();
      if (effectIndex >= 0 && effectIndex < effects.size()) {
         TriggerEffect effect = effects.get(effectIndex);
         return effect instanceof PastePrefabEffect pastePrefabEffect ? pastePrefabEffect : null;
      } else {
         return null;
      }
   }

   private void refreshActivePastePrefabPreviewPosition() {
      TriggerVolumesPlugin.PastePrefabPreviewState activeState = this.getActivePastePrefabPreviewState();
      if (activeState != null && activeState.worldName().equals(this.selectedWorld)) {
         PastePrefabEffect pastePrefabEffect = this.getPastePrefabEffectForState(activeState);
         if (pastePrefabEffect == null) {
            this.hidePastePrefabPreview();
         } else {
            Vector3d origin = this.getPreviewOriginForState(activeState);
            if (origin == null) {
               this.hidePastePrefabPreview();
            } else {
               Vector3d previewPosition = this.getPastePrefabPreviewPosition(pastePrefabEffect, origin);
               if (previewPosition == null) {
                  this.hidePastePrefabPreview();
               } else {
                  Vector3d lastSentPosition = activeState.lastSentPosition();
                  if (lastSentPosition == null || !lastSentPosition.equals(previewPosition)) {
                     this.sendPastePrefabPreview(pastePrefabEffect, previewPosition, false);
                  }
               }
            }
         }
      }
   }

   private void refreshActivePastePrefabPreview() {
      if (this.getActivePastePrefabPreviewState() != null && this.isPreviewingSelectedEffect()) {
         if (!(this.getSelectedItem() instanceof PastePrefabEffect pastePrefabEffect && this.sendPastePrefabPreview(pastePrefabEffect, true))) {
            this.hidePastePrefabPreview();
         }
      }
   }

   private static boolean isPastePrefabPreviewAssetField(@Nullable String fieldKey) {
      return "Prefab".equals(fieldKey) || "PrefabList".equals(fieldKey);
   }

   private void hidePastePrefabPreview() {
      if (this.getActivePastePrefabPreviewState() != null) {
         this.playerRef.getPacketHandler().write(new HideTriggerVolumePastePrefabPreview());
      }

      TriggerVolumesPlugin.get().clearPastePrefabPreviewState(this.playerRef.getUuid());
   }

   private void clearPastePrefabPreviewIfFromDifferentWorld() {
      TriggerVolumesPlugin.PastePrefabPreviewState activeState = this.getActivePastePrefabPreviewState();
      if (activeState != null && !activeState.worldName().equals(this.selectedWorld)) {
         this.hidePastePrefabPreview();
      }
   }

   private boolean isPreviewingSelectedEffect() {
      TriggerVolumesPlugin.PastePrefabPreviewState activeState = this.getActivePastePrefabPreviewState();
      return activeState != null
         && activeState.matches(this.selectedWorld, this.selectedId, this.selectedIsGroup, this.selectedKind.name(), this.selectedEffectIndex);
   }

   @Nullable
   private TriggerVolumesPlugin.PastePrefabPreviewState getActivePastePrefabPreviewState() {
      return TriggerVolumesPlugin.get().getPastePrefabPreviewState(this.playerRef.getUuid());
   }

   private <T> void applyCodecField(@Nonnull BuilderCodec<T> codec, @Nonnull T target, @Nonnull String key, @Nonnull TriggerVolumeInspectorPage.PageData data) {
      List<BuilderField<T, ?>> fieldList = codec.getEntries().get(key);
      if (fieldList != null && !fieldList.isEmpty()) {
         BuilderField field = fieldList.getLast();
         Codec childCodec = field.getCodec().getChildCodec();
         ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
         if (data.vecX != null && data.vecY != null && data.vecZ != null) {
            BsonDocument vecDoc = new BsonDocument();
            vecDoc.put("X", new BsonDouble(data.vecX));
            vecDoc.put("Y", new BsonDouble(data.vecY));
            vecDoc.put("Z", new BsonDouble(data.vecZ));
            BsonDocument doc = new BsonDocument();
            doc.put(key, vecDoc);
            field.decode(doc, target, extraInfo);
         } else if (data.paramBool != null && childCodec == Codec.BOOLEAN) {
            BsonDocument doc = new BsonDocument();
            doc.put(key, new BsonBoolean(data.paramBool));
            field.decode(doc, target, extraInfo);
         } else if (data.paramNumericValue != null) {
            double numericValue = isNonNegativeNumericField(getCodecTypeId(target), key) ? Math.max(0.0, data.paramNumericValue) : data.paramNumericValue;
            BsonValue bsonValue;
            if (childCodec == Codec.FLOAT || childCodec == Codec.DOUBLE) {
               bsonValue = new BsonDouble(numericValue);
            } else if (childCodec == Codec.INTEGER) {
               bsonValue = new BsonInt32((int)numericValue);
            } else if (childCodec == Codec.LONG) {
               bsonValue = new BsonInt64((long)numericValue);
            } else if (childCodec == Codec.BOOLEAN) {
               bsonValue = new BsonBoolean(numericValue != 0.0);
            } else {
               bsonValue = new BsonDouble(numericValue);
            }

            BsonDocument doc = new BsonDocument();
            doc.put(key, bsonValue);
            field.decode(doc, target, extraInfo);
         } else {
            try {
               BsonValue bsonValue = stringToBsonValue(childCodec, data.paramValue);
               if (bsonValue != null) {
                  BsonDocument doc = new BsonDocument();
                  doc.put(key, bsonValue);
                  field.decode(doc, target, extraInfo);
               }
            } catch (Exception exception) {
               LOGGER.at(Level.WARNING).log("Failed to parse value '%s' for field '%s'", data.paramValue, key, exception);
            }
         }
      }
   }

   private void onOpenPresetSave() {
      UICommandBuilder cmd = new UICommandBuilder();
      cmd.set("#MainPage.Visible", false);
      cmd.set("#PresetSavePage.Visible", true);
      cmd.set("#PresetName #Input.Value", "");
      cmd.set("#ConfirmSavePresetButton.Disabled", true);
      if (this.presetPackBrowser.hasSelectedPack()) {
         cmd.set("#PresetSavePage #SelectedPackLabel.Text", this.presetPackBrowser.getSelectedPackDisplayName());
      }

      this.sendUpdate(cmd);
   }

   private void onPresetNameChanged(@Nonnull TriggerVolumeInspectorPage.PageData data) {
      UICommandBuilder cmd = new UICommandBuilder();
      cmd.set("#ConfirmSavePresetButton.Disabled", data.presetName == null || data.presetName.isBlank());
      this.sendUpdate(cmd);
   }

   private void onConfirmSavePreset(@Nonnull TriggerVolumeInspectorPage.PageData data) {
      if (data.presetName != null && !data.presetName.isBlank()) {
         String presetName = data.presetName.trim();
         if (!presetName.contains("..") && !presetName.contains("/") && !presetName.contains("\\")) {
            AssetPack targetPack = this.presetPackBrowser.getSelectedPack();
            if (targetPack == null) {
               this.playerRef.sendMessage(Message.translation("server.customUI.assetPackBrowser.packRequired"));
            } else {
               AssetStore<String, TriggerEffectAsset, DefaultAssetMap<String, TriggerEffectAsset>> store = AssetRegistry.getAssetStore(TriggerEffectAsset.class);
               if (store != null) {
                  TriggerEffectAsset asset = TriggerEffectAsset.create(
                     presetName,
                     this.currentConditions().toArray(TriggerCondition[]::new),
                     this.currentEffects(TriggerVolumeInspectorPage.EffectListKind.EFFECT).toArray(TriggerEffect[]::new),
                     this.currentEffects(TriggerVolumeInspectorPage.EffectListKind.REJECTION_EFFECT).toArray(TriggerEffect[]::new),
                     this.currentConditionTiming()
                  );
                  Path path = targetPack.getRoot().resolve("Server").resolve("TriggerVolumes").resolve("Effects").resolve(presetName + ".json");
                  String packName = targetPack.getName();
                  UICommandBuilder cmd = new UICommandBuilder();
                  cmd.set("#PresetSavePage.Visible", false);
                  cmd.set("#MainPage.Visible", true);
                  this.sendUpdate(cmd);
                  HytaleServer.SCHEDULED_EXECUTOR
                     .execute(
                        () -> {
                           try {
                              Files.createDirectories(path.getParent());
                              BsonUtil.writeSync(path, TriggerEffectAsset.CODEC, asset, LOGGER);
                              store.loadAssetsFromPaths(packName, List.of(path));
                              this.playerRef
                                 .sendMessage(Message.translation("server.customUI.triggerVolumeEffectEditor.presetSaved").param("name", presetName));
                           } catch (Exception exception) {
                              LOGGER.at(Level.SEVERE).log("Failed to save effect preset '%s'", presetName, exception);
                              this.playerRef
                                 .sendMessage(
                                    Message.translation("server.customUI.triggerVolumeEffectEditor.presetSaveError").param("error", exception.getMessage())
                                 );
                           }
                        }
                     );
               }
            }
         } else {
            this.playerRef.sendMessage(Message.translation("server.customUI.triggerVolumeEffectEditor.presetInvalidName"));
         }
      }
   }

   private void onCancelPresetSave() {
      UICommandBuilder cmd = new UICommandBuilder();
      cmd.set("#PresetSavePage.Visible", false);
      cmd.set("#MainPage.Visible", true);
      this.sendUpdate(cmd);
   }

   private void onOpenPresetLoad() {
      UICommandBuilder cmd = new UICommandBuilder();
      UIEventBuilder evt = new UIEventBuilder();
      cmd.set("#MainPage.Visible", false);
      cmd.set("#PresetLoadPage.Visible", true);
      cmd.clear("#PresetList");
      AssetStore<String, TriggerEffectAsset, DefaultAssetMap<String, TriggerEffectAsset>> store = AssetRegistry.getAssetStore(TriggerEffectAsset.class);
      if (store != null) {
         int idx = 0;

         for (String assetId : ((DefaultAssetMap)store.getAssetMap()).getAssetMap().keySet()) {
            String sel = "#PresetList[" + idx + "]";
            cmd.append("#PresetList", "Common/TextButton.ui");
            cmd.set(sel + " #Button.Text", assetId);
            evt.addEventBinding(
               CustomUIEventBindingType.Activating,
               sel + " #Button",
               new EventData().append("Action", TriggerVolumeInspectorPage.Action.LoadPreset.name()).append("PresetId", assetId)
            );
            idx++;
         }
      }

      this.sendUpdate(cmd, evt, false);
   }

   private void onLoadPreset(@Nonnull TriggerVolumeInspectorPage.PageData data) {
      if (data.presetId != null && !data.presetId.isBlank()) {
         this.hidePastePrefabPreview();
         AssetStore<String, TriggerEffectAsset, DefaultAssetMap<String, TriggerEffectAsset>> store = AssetRegistry.getAssetStore(TriggerEffectAsset.class);
         if (store != null) {
            TriggerEffectAsset effectAsset = (TriggerEffectAsset)((DefaultAssetMap)store.getAssetMap()).getAsset(data.presetId);
            if (effectAsset == null) {
               this.playerRef.sendMessage(Message.translation("server.customUI.triggerVolumeEffectEditor.presetNotFound").param("name", data.presetId));
            } else {
               this.currentConditions().clear();
               this.currentConditions().addAll(TriggerCondition.deepCopyList(Arrays.asList(effectAsset.getConditions())));
               this.currentEffects(TriggerVolumeInspectorPage.EffectListKind.EFFECT).clear();
               this.currentEffects(TriggerVolumeInspectorPage.EffectListKind.EFFECT)
                  .addAll(TriggerEffect.deepCopyList(Arrays.asList(effectAsset.getEffects())));
               this.currentEffects(TriggerVolumeInspectorPage.EffectListKind.REJECTION_EFFECT).clear();
               this.currentEffects(TriggerVolumeInspectorPage.EffectListKind.REJECTION_EFFECT)
                  .addAll(TriggerEffect.deepCopyList(Arrays.asList(effectAsset.getRejectionEffects())));
               this.setCurrentConditionTiming(effectAsset.getConditionTiming());
               this.markSelectedDraftDirty();
               this.selectedKind = !this.currentConditions().isEmpty()
                  ? TriggerVolumeInspectorPage.EffectListKind.CONDITION
                  : (
                     !this.currentEffects(TriggerVolumeInspectorPage.EffectListKind.EFFECT).isEmpty()
                        ? TriggerVolumeInspectorPage.EffectListKind.EFFECT
                        : TriggerVolumeInspectorPage.EffectListKind.REJECTION_EFFECT
                  );
               this.selectedEffectIndex = this.getItemCount(this.selectedKind) == 0 ? -1 : 0;
               this.playerRef.sendMessage(Message.translation("server.customUI.triggerVolumeEffectEditor.presetLoaded").param("name", data.presetId));
               UICommandBuilder cmd = new UICommandBuilder();
               cmd.set("#PresetLoadPage.Visible", false);
               cmd.set("#MainPage.Visible", true);
               this.sendUpdate(cmd);
               this.rebuildAll();
            }
         }
      }
   }

   private void onCancelPresetLoad() {
      UICommandBuilder cmd = new UICommandBuilder();
      cmd.set("#PresetLoadPage.Visible", false);
      cmd.set("#MainPage.Visible", true);
      this.sendUpdate(cmd);
   }

   private void onOpenAssetPicker(@Nonnull TriggerVolumeInspectorPage.PageData data) {
      if (data.paramKey != null) {
         Object selected = this.getSelectedItem();
         if (selected != null) {
            String typeId = selected instanceof TriggerCondition condition ? getConditionTypeId(condition) : getTypeId((TriggerEffect)selected);
            String source = getAssetSourceForField(typeId, data.paramKey);
            if (source != null) {
               this.pendingPickerFieldKey = data.paramKey;
               this.pendingPickerSource = source;
               this.pendingPickerMultiSelect = this.isPickerFieldMultiSelect(selected, data.paramKey);
               this.pendingPickerSelections.clear();
               if (this.pendingPickerMultiSelect) {
                  this.pendingPickerSelections.addAll(this.currentPickerArrayValues(selected, data.paramKey));
               }

               UICommandBuilder cmd = new UICommandBuilder();
               UIEventBuilder evt = new UIEventBuilder();
               cmd.set("#MainPage.Visible", false);
               cmd.set("#AssetPickerPage.Visible", true);
               cmd.set("#AssetPickerPage #SearchInput.Value", this.assetPickerSearchQuery);
               cmd.set("#ConfirmAssetPickerButton.Visible", this.pendingPickerMultiSelect);
               cmd.set("#AssetPickerFieldLabel.Text", data.paramKey);
               this.buildAssetPickerList(cmd, evt);
               this.bindStaticEvents(evt);
               this.sendUpdate(cmd, evt, false);
            }
         }
      }
   }

   private void onAssetPickerSearch(@Nonnull TriggerVolumeInspectorPage.PageData data) {
      if (data.assetPickerQuery != null) {
         this.assetPickerSearchQuery = data.assetPickerQuery.trim();
      }

      UICommandBuilder cmd = new UICommandBuilder();
      UIEventBuilder evt = new UIEventBuilder();
      this.buildAssetPickerList(cmd, evt);
      this.bindStaticEvents(evt);
      this.sendUpdate(cmd, evt, false);
   }

   private void onAssetPickerSelect(@Nonnull TriggerVolumeInspectorPage.PageData data) {
      if (data.assetPickerSelection != null && this.pendingPickerFieldKey != null) {
         if (this.pendingPickerMultiSelect) {
            if (data.assetPickerSelection.isEmpty()) {
               this.pendingPickerSelections.clear();
            } else if (!this.pendingPickerSelections.remove(data.assetPickerSelection)) {
               this.pendingPickerSelections.add(data.assetPickerSelection);
            }

            UICommandBuilder cmd = new UICommandBuilder();
            UIEventBuilder evt = new UIEventBuilder();
            this.buildAssetPickerList(cmd, evt);
            this.bindStaticEvents(evt);
            this.sendUpdate(cmd, evt, false);
         } else {
            Object selected = this.getSelectedItem();
            if (selected instanceof TriggerCondition condition) {
               BuilderCodec<TriggerCondition> codec = getConditionBuilderCodecFor(getConditionTypeId(condition));
               if (codec != null) {
                  this.applyPickerValue(codec, condition, this.pendingPickerFieldKey, data.assetPickerSelection.isEmpty() ? null : data.assetPickerSelection);
                  this.markSelectedDraftDirty();
               }
            } else if (selected instanceof TriggerEffect effect) {
               BuilderCodec<TriggerEffect> codec = getBuilderCodecFor(getTypeId(effect));
               if (codec != null) {
                  this.applyPickerValue(codec, effect, this.pendingPickerFieldKey, data.assetPickerSelection.isEmpty() ? null : data.assetPickerSelection);
                  this.markSelectedDraftDirty();
               }
            }

            if (isPastePrefabPreviewAssetField(this.pendingPickerFieldKey)) {
               this.refreshActivePastePrefabPreview();
            }

            this.pendingPickerFieldKey = null;
            this.pendingPickerSource = null;
            UICommandBuilder cmd = new UICommandBuilder();
            cmd.set("#AssetPickerPage.Visible", false);
            cmd.set("#MainPage.Visible", true);
            this.sendUpdate(cmd);
            this.rebuildAll();
         }
      }
   }

   private void onConfirmAssetPicker() {
      if (this.pendingPickerMultiSelect && this.pendingPickerFieldKey != null) {
         Object selected = this.getSelectedItem();
         if (selected instanceof TriggerCondition condition) {
            BuilderCodec<TriggerCondition> codec = getConditionBuilderCodecFor(getConditionTypeId(condition));
            if (codec != null) {
               this.applyPickerValues(codec, condition, this.pendingPickerFieldKey, this.pendingPickerSelections);
               this.markSelectedDraftDirty();
            }
         } else if (selected instanceof TriggerEffect effect) {
            BuilderCodec<TriggerEffect> codec = getBuilderCodecFor(getTypeId(effect));
            if (codec != null) {
               this.applyPickerValues(codec, effect, this.pendingPickerFieldKey, this.pendingPickerSelections);
               this.markSelectedDraftDirty();
            }
         }

         if (isPastePrefabPreviewAssetField(this.pendingPickerFieldKey)) {
            this.refreshActivePastePrefabPreview();
         }

         this.closeAssetPicker();
      }
   }

   private void onPreviewSound(@Nonnull TriggerVolumeInspectorPage.PageData data, @Nonnull Store<EntityStore> store) {
      if ("SoundEvent".equals(this.pendingPickerSource) && data.assetPickerSelection != null && !data.assetPickerSelection.isBlank()) {
         int soundEventIndex = SoundEvent.getAssetMap().getIndex(data.assetPickerSelection);
         if (soundEventIndex != Integer.MIN_VALUE && soundEventIndex != 0) {
            Ref<EntityStore> playerEntityRef = this.playerRef.getReference();
            if (playerEntityRef != null && playerEntityRef.isValid()) {
               TransformComponent transform = store.getComponent(playerEntityRef, TransformComponent.getComponentType());
               if (transform != null) {
                  Vector3d position = transform.getPosition();
                  SoundUtil.playSoundEvent3d(soundEventIndex, SoundCategory.SFX, position.x(), position.y(), position.z(), 1.0F, 1.0F, store);
               }
            }
         }
      }
   }

   private void onCancelAssetPicker() {
      this.closeAssetPicker();
   }

   private void closeAssetPicker() {
      this.pendingPickerFieldKey = null;
      this.pendingPickerSource = null;
      this.pendingPickerMultiSelect = false;
      this.pendingPickerSelections.clear();
      UICommandBuilder cmd = new UICommandBuilder();
      cmd.set("#ConfirmAssetPickerButton.Visible", false);
      cmd.set("#AssetPickerPage.Visible", false);
      cmd.set("#MainPage.Visible", true);
      this.sendUpdate(cmd);
      this.rebuildAll();
   }

   private void registerSelectionObserver() {
      TriggerVolumeManager manager = getManagerForWorld(this.selectedWorld);
      if (manager != null) {
         manager.setSelectionObserver(this.playerRef.getUuid(), this.selectionObserver);
      }
   }

   private void registerVolumeUpdateObserver() {
      TriggerVolumeManager manager = getManagerForWorld(this.selectedWorld);
      if (manager != null) {
         manager.setVolumeUpdateObserver(this.playerRef.getUuid(), this.volumeUpdateObserver);
      }
   }

   private void onExternalSelectionChanged(@Nullable String volumeId) {
      if (!this.suppressSelectionObserver) {
         if (!Objects.equals(this.selectedId, volumeId) || this.selectedIsGroup) {
            TriggerVolumeManager manager = getManagerForWorld(this.selectedWorld);
            if (volumeId == null || manager != null && manager.getVolume(volumeId) != null) {
               this.selectedId = volumeId;
               this.selectedIsGroup = false;
               this.selectedEffectIndex = -1;
               this.pendingScrollToSelection = true;
               this.rebuildAll();
            }
         }
      }
   }

   private void onExternalVolumeUpdated(@Nonnull VolumeEntry volume) {
      TriggerVolumesPlugin.PastePrefabPreviewState activeState = this.getActivePastePrefabPreviewState();
      if (activeState != null) {
         if (activeState.selectedId() != null && activeState.worldName().equals(this.selectedWorld)) {
            if (activeState.selectedIsGroup()) {
               TriggerVolumeManager manager = getManagerForWorld(this.selectedWorld);
               GroupEntry group = manager != null ? manager.getGroup(activeState.selectedId()) : null;
               if (group == null || !group.getMemberVolumeIds().contains(volume.getId())) {
                  return;
               }

               TriggerVolumeInspectorDrafts.GroupDraft groupDraft = this.groupDrafts.get(activeState.selectedId());
               if (groupDraft != null) {
                  groupDraft.origin = new Vector3d(group.getOrigin());
               }
            } else {
               if (!activeState.selectedId().equals(volume.getId())) {
                  return;
               }

               TriggerVolumeInspectorDrafts.VolumeDraft volumeDraft = this.volumeDrafts.get(activeState.selectedId());
               if (volumeDraft != null) {
                  volumeDraft.position = new Vector3d(volume.getPosition());
               }
            }

            this.refreshActivePastePrefabPreviewPosition();
         }
      }
   }

   private void onExternalVolumeRemoved(@Nonnull String volumeId) {
      TriggerVolumesPlugin.PastePrefabPreviewState activeState = this.getActivePastePrefabPreviewState();
      if (activeState != null && activeState.selectedId() != null) {
         if (!activeState.selectedIsGroup() && activeState.selectedId().equals(volumeId)) {
            this.hidePastePrefabPreview();
         } else {
            if (activeState.selectedIsGroup()) {
               TriggerVolumeInspectorDrafts.GroupDraft groupDraft = this.groupDrafts.get(activeState.selectedId());
               if (groupDraft != null && groupDraft.memberVolumeIds.contains(volumeId)) {
                  this.hidePastePrefabPreview();
               }
            }
         }
      }
   }

   private void syncSelectionToTool() {
      TriggerVolumeToolSelection packet = new TriggerVolumeToolSelection();
      TriggerVolumeManager manager = getManagerForWorld(this.selectedWorld);
      if (this.selectedId != null && manager != null) {
         if (this.selectedIsGroup) {
            GroupEntry group = manager.getGroup(this.selectedId);
            if (group == null) {
               return;
            }

            String[] ids = group.getMemberVolumeIds().stream().filter(id -> manager.getVolume(id) != null).toArray(String[]::new);
            packet.volumeIds = ids;
            packet.primaryVolumeId = ids.length > 0 ? ids[0] : null;
            this.suppressSelectionObserver = true;
            manager.setPlayerSelection(this.playerRef.getUuid(), packet.primaryVolumeId);
            this.suppressSelectionObserver = false;
         } else {
            packet.primaryVolumeId = this.selectedId;
            packet.volumeIds = new String[]{this.selectedId};
            this.suppressSelectionObserver = true;
            manager.setPlayerSelection(this.playerRef.getUuid(), this.selectedId);
            this.suppressSelectionObserver = false;
         }

         this.playerRef.getPacketHandler().write(packet);
      } else {
         if (manager != null) {
            this.suppressSelectionObserver = true;
            manager.setPlayerSelection(this.playerRef.getUuid(), null);
            this.suppressSelectionObserver = false;
         }

         this.playerRef.getPacketHandler().write(packet);
      }
   }

   private void applyPickerValue(@Nonnull BuilderCodec<?> codec, @Nonnull Object target, @Nonnull String fieldKey, @Nullable String value) {
      List<? extends BuilderField<?, ?>> fieldList = codec.getEntries().get(fieldKey);
      if (fieldList != null && !fieldList.isEmpty()) {
         BuilderField field = fieldList.getLast();
         BsonDocument doc = new BsonDocument();
         Codec childCodec = field.getCodec().getChildCodec();
         if (childCodec == Codec.STRING_ARRAY) {
            doc.put(fieldKey, this.pickerArrayValue(value));
         } else if (value != null) {
            doc.put(fieldKey, new BsonString(value));
         }

         try {
            field.decode(doc, target, ExtraInfo.THREAD_LOCAL.get());
         } catch (Exception exception) {
            LOGGER.at(Level.WARNING).log("Failed to apply picker value '%s' for field '%s'", value, fieldKey, exception);
         }
      }
   }

   private void applyPickerValues(@Nonnull BuilderCodec<?> codec, @Nonnull Object target, @Nonnull String fieldKey, @Nonnull Collection<String> values) {
      List<? extends BuilderField<?, ?>> fieldList = codec.getEntries().get(fieldKey);
      if (fieldList != null && !fieldList.isEmpty()) {
         BuilderField field = fieldList.getLast();
         BsonDocument doc = new BsonDocument();
         doc.put(fieldKey, this.pickerArrayValue(values));

         try {
            field.decode(doc, target, ExtraInfo.THREAD_LOCAL.get());
         } catch (Exception exception) {
            LOGGER.at(Level.WARNING).log("Failed to apply picker values for field '%s'", fieldKey, exception);
         }
      }
   }

   @Nonnull
   private BsonArray pickerArrayValue(@Nullable String value) {
      return value != null && !value.isBlank() ? this.pickerArrayValue(List.of(value)) : new BsonArray();
   }

   @Nonnull
   private BsonArray pickerArrayValue(@Nonnull Collection<String> values) {
      BsonArray array = new BsonArray();

      for (String stringValue : values) {
         array.add(new BsonString(stringValue));
      }

      return array;
   }

   private boolean isPickerFieldMultiSelect(@Nonnull Object selected, @Nonnull String fieldKey) {
      BuilderCodec<? extends Object> codec = selected instanceof TriggerCondition condition
         ? getConditionBuilderCodecFor(getConditionTypeId(condition))
         : getBuilderCodecFor(getTypeId((TriggerEffect)selected));
      if (codec == null) {
         return false;
      } else {
         List<? extends BuilderField<?, ?>> fieldList = codec.getEntries().get(fieldKey);
         if (fieldList != null && !fieldList.isEmpty()) {
            BuilderField field = fieldList.getLast();
            return field.getCodec().getChildCodec() == Codec.STRING_ARRAY;
         } else {
            return false;
         }
      }
   }

   @Nonnull
   private Collection<String> currentPickerArrayValues(@Nonnull Object selected, @Nonnull String fieldKey) {
      BuilderCodec<? extends Object> codec = selected instanceof TriggerCondition condition
         ? getConditionBuilderCodecFor(getConditionTypeId(condition))
         : getBuilderCodecFor(getTypeId((TriggerEffect)selected));
      if (codec == null) {
         return List.of();
      }

      BsonDocument encoded;
      try {
         encoded = codec.encode(selected, EmptyExtraInfo.EMPTY);
      } catch (Exception exception) {
         return List.of();
      }

      LinkedHashSet<String> values = new LinkedHashSet<>();
      BsonValue currentValue = encoded.get(fieldKey);
      if (currentValue instanceof BsonArray) {
         for (BsonValue element : (BsonArray)currentValue) {
            if (element instanceof BsonString bsonString) {
               values.add(bsonString.getValue());
            }
         }
      }

      return values;
   }

   private void buildAssetPickerList(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt) {
      cmd.clear("#AssetPickerList");
      Collection<String> allIds = this.resolvePickerIds();
      String query = this.assetPickerSearchQuery.toLowerCase(Locale.ROOT);
      List<String> ordered;
      if (!query.isEmpty()) {
         Object2IntMap<String> scored = new Object2IntOpenHashMap<>(allIds.size());

         for (String assetId : allIds) {
            if (assetId.toLowerCase(Locale.ROOT).contains(query)) {
               scored.put(assetId, StringCompareUtil.getFuzzyDistance(assetId, query, Locale.ROOT));
            }
         }

         ordered = scored.keySet().stream().sorted().sorted(Comparator.comparingInt(scored::getInt).reversed()).toList();
      } else if ("AnimationApplyOn".equals(this.pendingPickerSource)) {
         ordered = allIds.stream()
            .sorted(Comparator.<String, Boolean>comparing(assetIdx -> !isApplyOnPseudo(assetIdx)).thenComparing(Comparator.naturalOrder()))
            .toList();
      } else {
         ordered = allIds.stream().sorted().toList();
      }

      List<String> filtered;
      if (this.pendingPickerMultiSelect && !this.pendingPickerSelections.isEmpty()) {
         ArrayList<String> selectedFirst = new ArrayList<>(ordered.size());

         for (String assetId : ordered) {
            if (this.pendingPickerSelections.contains(assetId)) {
               selectedFirst.add(assetId);
            }
         }

         for (String assetId : ordered) {
            if (!this.pendingPickerSelections.contains(assetId)) {
               selectedFirst.add(assetId);
            }
         }

         filtered = selectedFirst.size() > 50 ? selectedFirst.subList(0, 50) : selectedFirst;
      } else {
         filtered = ordered.size() > 50 ? ordered.subList(0, 50) : ordered;
      }

      cmd.set("#AssetPickerNoResults.Visible", filtered.isEmpty());
      cmd.append("#AssetPickerList", "Common/TextButton.ui");
      cmd.set("#AssetPickerList[0] #Button.Text", Message.translation("server.customUI.triggerVolumeEffectEditor.assetPicker.clearEntry"));
      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         "#AssetPickerList[0] #Button",
         new EventData().append("Action", TriggerVolumeInspectorPage.Action.AssetPickerSelect.name()).append("AssetPickerSelection", "")
      );
      boolean soundPicker = "SoundEvent".equals(this.pendingPickerSource);

      for (int i = 0; i < filtered.size(); i++) {
         String assetId = filtered.get(i);
         String sel = "#AssetPickerList[" + (i + 1) + "]";
         String label = this.pendingPickerMultiSelect && this.pendingPickerSelections.contains(assetId) ? "[x] " + assetId : assetId;
         if (soundPicker) {
            cmd.append("#AssetPickerList", "Pages/TriggerVolume/TriggerVolumeInspectorSoundAssetRow.ui");
            cmd.set(sel + " #SelectButton.Text", label);
            evt.addEventBinding(
               CustomUIEventBindingType.Activating,
               sel + " #SelectButton",
               new EventData().append("Action", TriggerVolumeInspectorPage.Action.AssetPickerSelect.name()).append("AssetPickerSelection", assetId)
            );
            evt.addEventBinding(
               CustomUIEventBindingType.Activating,
               sel + " #PreviewButton",
               new EventData().append("Action", TriggerVolumeInspectorPage.Action.PreviewSound.name()).append("AssetPickerSelection", assetId),
               false
            );
         } else {
            cmd.append("#AssetPickerList", "Common/TextButton.ui");
            if ("AnimationApplyOn".equals(this.pendingPickerSource) && isApplyOnPseudo(assetId)) {
               cmd.set(sel + " #Button.Text", Message.translation("server.customUI.triggerVolumeEffectEditor.applyOn." + assetId));
            } else if ("Animation".equals(this.pendingPickerSource) && this.isLoopingAnimation("PlayAnimation", "Animation", assetId)) {
               cmd.set(
                  sel + " #Button.Text",
                  Message.translation("server.customUI.triggerVolumeEffectEditor.assetPicker.animationLooping").param("animation", assetId)
               );
            } else {
               cmd.set(sel + " #Button.Text", label);
            }

            evt.addEventBinding(
               CustomUIEventBindingType.Activating,
               sel + " #Button",
               new EventData().append("Action", TriggerVolumeInspectorPage.Action.AssetPickerSelect.name()).append("AssetPickerSelection", assetId)
            );
         }
      }
   }

   private void rebuildAll() {
      this.revertRejectedDraftIds();
      UICommandBuilder cmd = new UICommandBuilder();
      UIEventBuilder evt = new UIEventBuilder();
      this.buildWorldDropdown(cmd);
      this.buildTabs(cmd, evt);
      this.buildList(cmd, evt);
      this.buildSelectedPane(cmd, evt);
      this.bindStaticEvents(evt);
      this.refreshActivePastePrefabPreviewPosition();
      this.sendUpdate(cmd, evt, false);
   }

   @Nullable
   private TriggerVolumeManager getSelectedManager() {
      return getManagerForWorld(this.selectedWorld);
   }

   @Nullable
   private TriggerVolumeInspectorDrafts.VolumeDraft selectedVolumeDraft() {
      TriggerVolumeManager manager = this.getSelectedManager();
      if (manager != null && this.selectedId != null) {
         VolumeEntry volume = manager.getVolume(this.selectedId);
         return volume != null ? this.draftForVolume(volume) : null;
      } else {
         return null;
      }
   }

   @Nullable
   private TriggerVolumeInspectorDrafts.GroupDraft selectedGroupDraft() {
      TriggerVolumeManager manager = this.getSelectedManager();
      if (manager != null && this.selectedId != null) {
         GroupEntry group = manager.getGroup(this.selectedId);
         return group != null ? this.draftForGroup(group) : null;
      } else {
         return null;
      }
   }

   @Nullable
   private TriggerVolumeInspectorDrafts.GroupDraft selectedInheritedGroupDraft() {
      if (this.selectedIsGroup) {
         return null;
      } else {
         TriggerVolumeManager manager = this.getSelectedManager();
         TriggerVolumeInspectorDrafts.VolumeDraft volumeDraft = this.selectedVolumeDraft();
         if (manager != null && volumeDraft != null && volumeDraft.groupId != null) {
            GroupEntry group = manager.getGroup(volumeDraft.groupId);
            return group != null ? this.draftForGroup(group) : null;
         } else {
            return null;
         }
      }
   }

   @Nonnull
   private TriggerVolumeInspectorDrafts.VolumeDraft draftForVolume(@Nonnull VolumeEntry volume) {
      return this.volumeDrafts.computeIfAbsent(volume.getId(), ignored -> TriggerVolumeInspectorDrafts.VolumeDraft.from(volume));
   }

   @Nonnull
   private TriggerVolumeInspectorDrafts.GroupDraft draftForGroup(@Nonnull GroupEntry group) {
      return this.groupDrafts.computeIfAbsent(group.getId(), ignored -> TriggerVolumeInspectorDrafts.GroupDraft.from(group));
   }

   @Nonnull
   private Map<String, String> currentTags() {
      return this.selectedIsGroup ? this.selectedGroupDraft().tags : this.selectedVolumeDraft().tags;
   }

   @Nonnull
   private List<TriggerCondition> currentConditions() {
      return this.selectedIsGroup ? this.selectedGroupDraft().conditions : this.selectedVolumeDraft().conditions;
   }

   @Nonnull
   private List<TriggerEffect> currentEffects(@Nonnull TriggerVolumeInspectorPage.EffectListKind kind) {
      if (this.selectedIsGroup) {
         TriggerVolumeInspectorDrafts.GroupDraft draft = this.selectedGroupDraft();
         return kind == TriggerVolumeInspectorPage.EffectListKind.REJECTION_EFFECT ? draft.rejectionEffects : draft.effects;
      } else {
         TriggerVolumeInspectorDrafts.VolumeDraft draft = this.selectedVolumeDraft();
         return kind == TriggerVolumeInspectorPage.EffectListKind.REJECTION_EFFECT ? draft.rejectionEffects : draft.effects;
      }
   }

   @Nonnull
   private ConditionTiming currentConditionTiming() {
      return this.selectedIsGroup ? this.selectedGroupDraft().conditionTiming : this.selectedVolumeDraft().conditionTiming;
   }

   private void setCurrentConditionTiming(@Nonnull ConditionTiming timing) {
      if (this.selectedIsGroup) {
         TriggerVolumeInspectorDrafts.GroupDraft groupDraft = this.selectedGroupDraft();
         groupDraft.conditionTiming = timing;
         groupDraft.markDirty();
      } else {
         TriggerVolumeInspectorDrafts.VolumeDraft volumeDraft = this.selectedVolumeDraft();
         volumeDraft.conditionTiming = timing;
         volumeDraft.markDirty();
      }
   }

   private void markSelectedDraftDirty() {
      if (this.selectedIsGroup) {
         TriggerVolumeInspectorDrafts.GroupDraft groupDraft = this.selectedGroupDraft();
         if (groupDraft != null) {
            groupDraft.markDirty();
         }
      } else {
         TriggerVolumeInspectorDrafts.VolumeDraft volumeDraft = this.selectedVolumeDraft();
         if (volumeDraft != null) {
            volumeDraft.markDirty();
         }
      }
   }

   @Nullable
   private Object getSelectedItem() {
      if (this.selectedId != null && this.selectedEffectIndex >= 0 && this.selectedEffectIndex < this.getItemCount(this.selectedKind)) {
         return this.selectedKind == TriggerVolumeInspectorPage.EffectListKind.CONDITION
            ? this.currentConditions().get(this.selectedEffectIndex)
            : this.currentEffects(this.selectedKind).get(this.selectedEffectIndex);
      } else {
         return null;
      }
   }

   private int getItemCount(@Nonnull TriggerVolumeInspectorPage.EffectListKind kind) {
      return kind == TriggerVolumeInspectorPage.EffectListKind.CONDITION ? this.currentConditions().size() : this.currentEffects(kind).size();
   }

   private static int getGroupItemCount(@Nonnull TriggerVolumeInspectorPage.EffectListKind kind, @Nonnull TriggerVolumeInspectorDrafts.GroupDraft groupDraft) {
      return switch (kind) {
         case CONDITION -> groupDraft.conditions.size();
         case EFFECT -> groupDraft.effects.size();
         case REJECTION_EFFECT -> groupDraft.rejectionEffects.size();
      };
   }

   @Nullable
   private TriggerEventType getItemEventType(@Nonnull TriggerVolumeInspectorPage.EffectListKind kind, int index) {
      return kind == TriggerVolumeInspectorPage.EffectListKind.CONDITION
         ? this.currentConditions().get(index).getEventType()
         : this.currentEffects(kind).get(index).getEventType();
   }

   @Nullable
   private static TriggerEventType getGroupItemEventType(
      @Nonnull TriggerVolumeInspectorPage.EffectListKind kind, int index, @Nonnull TriggerVolumeInspectorDrafts.GroupDraft groupDraft
   ) {
      return switch (kind) {
         case CONDITION -> groupDraft.conditions.get(index).getEventType();
         case EFFECT -> groupDraft.effects.get(index).getEventType();
         case REJECTION_EFFECT -> groupDraft.rejectionEffects.get(index).getEventType();
      };
   }

   private int getItemEntry(@Nonnull TriggerVolumeInspectorPage.EffectListKind kind, int index) {
      return kind == TriggerVolumeInspectorPage.EffectListKind.CONDITION
         ? this.currentConditions().get(index).getEntry()
         : this.currentEffects(kind).get(index).getEntry();
   }

   private static int getGroupItemEntry(
      @Nonnull TriggerVolumeInspectorPage.EffectListKind kind, int index, @Nonnull TriggerVolumeInspectorDrafts.GroupDraft groupDraft
   ) {
      return switch (kind) {
         case CONDITION -> groupDraft.conditions.get(index).getEntry();
         case EFFECT -> groupDraft.effects.get(index).getEntry();
         case REJECTION_EFFECT -> groupDraft.rejectionEffects.get(index).getEntry();
      };
   }

   @Nonnull
   private String getItemTypeId(@Nonnull TriggerVolumeInspectorPage.EffectListKind kind, int index) {
      return kind == TriggerVolumeInspectorPage.EffectListKind.CONDITION
         ? getConditionTypeId(this.currentConditions().get(index))
         : getTypeId(this.currentEffects(kind).get(index));
   }

   @Nonnull
   private static String getGroupItemTypeId(
      @Nonnull TriggerVolumeInspectorPage.EffectListKind kind, int index, @Nonnull TriggerVolumeInspectorDrafts.GroupDraft groupDraft
   ) {
      return switch (kind) {
         case CONDITION -> getConditionTypeId(groupDraft.conditions.get(index));
         case EFFECT -> getTypeId(groupDraft.effects.get(index));
         case REJECTION_EFFECT -> getTypeId(groupDraft.rejectionEffects.get(index));
      };
   }

   private boolean matchesFilter(@Nonnull String value) {
      return this.filterText.isEmpty() || value.toLowerCase(Locale.ROOT).contains(this.filterText);
   }

   private boolean matchesVolumeFilter(@Nonnull VolumeEntry volume) {
      if (this.filterText.isEmpty()) {
         return true;
      } else {
         return this.draftForVolume(volume).id.toLowerCase(Locale.ROOT).contains(this.filterText)
            ? true
            : this.tagsMatchFilter(this.draftForVolume(volume).tags);
      }
   }

   private boolean matchesGroupFilter(@Nonnull String groupLabel, @Nonnull GroupEntry group, @Nonnull List<VolumeEntry> children) {
      if (this.filterText.isEmpty()) {
         return true;
      }

      if (groupLabel.toLowerCase(Locale.ROOT).contains(this.filterText)) {
         return true;
      }

      if (this.tagsMatchFilter(this.draftForGroup(group).tags)) {
         return true;
      }

      for (VolumeEntry child : children) {
         if (this.matchesVolumeFilter(child)) {
            return true;
         }
      }

      return false;
   }

   private boolean tagsMatchFilter(@Nonnull Map<String, String> tags) {
      for (Entry<String, String> tag : tags.entrySet()) {
         if (this.tagMatchesFilter(tag.getKey(), tag.getValue())) {
            return true;
         }
      }

      return false;
   }

   private boolean tagMatchesFilter(@Nonnull String key, @Nullable String value) {
      if (this.filterText.isEmpty()) {
         return false;
      }

      String keyLower = key.toLowerCase(Locale.ROOT);
      String valueLower = value != null ? value.toLowerCase(Locale.ROOT) : "";
      return keyLower.contains(this.filterText) || valueLower.contains(this.filterText) || (keyLower + "=" + valueLower).contains(this.filterText);
   }

   private int appendRowTagChips(@Nonnull UICommandBuilder cmd, @Nonnull String container, @Nonnull Map<String, String> tags, boolean indented) {
      if (tags.isEmpty()) {
         return 0;
      }

      List<Entry<String, String>> entries = this.orderedRowTags(tags);
      int available = indented ? 236 : 252;
      int chipIdx = 0;
      int rowsUsed = 1;
      int x = 0;

      for (int i = 0; i < entries.size(); i++) {
         Entry<String, String> entry = entries.get(i);
         String text = this.chipDisplay(entry.getKey(), entry.getValue());
         int width = chipWidth(text);
         if (x > 0 && x + width > available) {
            if (rowsUsed == 2) {
               this.appendChip(cmd, container, chipIdx, "+" + (entries.size() - i), 5596014);
               return 2;
            }

            rowsUsed++;
            x = 0;
         }

         this.appendChip(cmd, container, chipIdx++, text, tagChipColor(entry.getKey()));
         x += width;
      }

      return rowsUsed;
   }

   @Nonnull
   private List<Entry<String, String>> orderedRowTags(@Nonnull Map<String, String> tags) {
      ArrayList<Entry<String, String>> entries = new ArrayList<>(tags.entrySet());
      if (!this.filterText.isEmpty()) {
         entries.sort(
            (left, right) -> Boolean.compare(this.tagMatchesFilter(right.getKey(), right.getValue()), this.tagMatchesFilter(left.getKey(), left.getValue()))
         );
      }

      return entries;
   }

   @Nonnull
   private String chipDisplay(@Nonnull String key, @Nullable String value) {
      boolean hasValue = value != null && !value.isEmpty();
      boolean valueMatched = hasValue && !this.filterText.isEmpty() && value.toLowerCase(Locale.ROOT).contains(this.filterText);
      return valueMatched ? key + ": " + value : key;
   }

   private static int chipWidth(@Nonnull String text) {
      return (int)Math.ceil(text.length() * 6.2) + 14;
   }

   @Nullable
   private static TriggerVolumeManager getManagerForWorld(@Nonnull String worldName) {
      for (World world : Universe.get().getWorlds().values()) {
         if (world.getName().equalsIgnoreCase(worldName)) {
            return world.getEntityStore().getStore().getResource(TriggerVolumesPlugin.get().getManagerResourceType());
         }
      }

      return null;
   }

   @Nonnull
   private static PatchStyle colorPatch(int rgb) {
      return new PatchStyle().setColor(Value.of(colorToHex(rgb)));
   }

   @Nonnull
   private static PatchStyle tagColorPatch(int rgb) {
      return new PatchStyle().setColor(Value.of(colorToHex(rgb) + "99"));
   }

   private static int tagChipColor(@Nonnull String key) {
      int hash = key.toLowerCase(Locale.ROOT).hashCode();
      hash ^= hash >>> 16;
      hash *= 2146121005;
      hash ^= hash >>> 15;
      hash *= -2073254261;
      hash ^= hash >>> 16;
      return TAG_CHIP_COLORS[Math.floorMod(hash, TAG_CHIP_COLORS.length)];
   }

   private void appendChip(@Nonnull UICommandBuilder cmd, @Nonnull String container, int idx, @Nonnull String text, int rgb) {
      String sel = container + "[" + idx + "]";
      cmd.append(container, "Pages/TriggerVolume/TriggerVolumeTagChip.ui");
      cmd.setObject(sel + ".Background", tagColorPatch(rgb));
      cmd.set(sel + " #ChipLabel.Text", text);
   }

   @Nonnull
   private static String colorToHex(int rgb) {
      int r = rgb >> 16 & 0xFF;
      int g = rgb >> 8 & 0xFF;
      int b = rgb & 0xFF;
      return String.format("#%02X%02X%02X", r, g, b);
   }

   @Nonnull
   private static String colorToHex(@Nonnull Vector3f color) {
      int r = Math.round(color.x() * 255.0F);
      int g = Math.round(color.y() * 255.0F);
      int b = Math.round(color.z() * 255.0F);
      return String.format("#%02X%02X%02X", r, g, b);
   }

   @Nullable
   private static Vector3f parseColor(@Nullable String value) {
      if (value != null && !value.isBlank()) {
         int packed = parsePackedColor(value, 52428);
         return new Vector3f((packed >> 16 & 0xFF) / 255.0F, (packed >> 8 & 0xFF) / 255.0F, (packed & 0xFF) / 255.0F);
      } else {
         return null;
      }
   }

   private static int parsePackedColor(@Nullable String value, int fallback) {
      if (value == null) {
         return fallback;
      }

      String trimmed = value.trim();
      if (trimmed.startsWith("#")) {
         trimmed = trimmed.substring(1);
      }

      if (trimmed.length() > 6) {
         trimmed = trimmed.substring(0, 6);
      }

      try {
         return Integer.parseInt(trimmed, 16) & 16777215;
      } catch (NumberFormatException exception) {
         return fallback;
      }
   }

   private static void setVec(@Nonnull Vector3d target, @Nonnull TriggerVolumeInspectorPage.PageData data) {
      if (data.vecX != null && data.vecY != null && data.vecZ != null) {
         target.set(data.vecX, data.vecY, data.vecZ);
      }
   }

   private static void setBoxDimensions(@Nonnull Vector3d target, @Nonnull TriggerVolumeInspectorPage.PageData data) {
      if (data.vecX != null && data.vecY != null && data.vecZ != null) {
         target.set(clampDimension(data.vecX, 0.25), clampDimension(data.vecY, 0.25), clampDimension(data.vecZ, 0.25));
      }
   }

   private static void setSphereDimensions(@Nonnull Vector3d target, @Nonnull TriggerVolumeInspectorPage.PageData data) {
      if (data.paramNumericValue != null) {
         target.set(clampDimension(data.paramNumericValue, 0.25), 0.0, 0.0);
      }
   }

   private static void setCylinderDimensions(@Nonnull Vector3d target, @Nonnull TriggerVolumeInspectorPage.PageData data) {
      if (data.vecX != null && data.vecY != null) {
         target.set(clampDimension(data.vecX, 0.25), clampDimension(data.vecY, 0.5), 0.0);
      }
   }

   private static double clampDimension(double value, double minValue) {
      return Math.min(1000.0, Math.max(minValue, value));
   }

   @Nonnull
   private static <T extends Enum<T>> T parseEnum(@Nonnull Class<T> type, @Nullable String value, @Nonnull T fallback) {
      if (value == null) {
         return fallback;
      }

      try {
         return Enum.valueOf(type, value);
      } catch (IllegalArgumentException exception) {
         return fallback;
      }
   }

   @Nonnull
   private static Set<EntityTargetType> parseTargetTypes(@Nullable String value) {
      return switch (value != null ? value : "PLAYER") {
         case "NONE" -> EnumSet.noneOf(EntityTargetType.class);
         case "NPC" -> EnumSet.of(EntityTargetType.NPC);
         case "ITEM_DROP" -> EnumSet.of(EntityTargetType.ITEM_DROP);
         case "PROJECTILE" -> EnumSet.of(EntityTargetType.PROJECTILE);
         case "ALL" -> EnumSet.allOf(EntityTargetType.class);
         default -> EnumSet.of(EntityTargetType.PLAYER);
      };
   }

   @Nonnull
   private static String targetTypesValue(@Nonnull Set<EntityTargetType> targetTypes) {
      if (targetTypes.containsAll(EnumSet.allOf(EntityTargetType.class))) {
         return "ALL";
      } else if (targetTypes.contains(EntityTargetType.PROJECTILE)) {
         return "PROJECTILE";
      } else if (targetTypes.contains(EntityTargetType.ITEM_DROP)) {
         return "ITEM_DROP";
      } else if (targetTypes.contains(EntityTargetType.NPC)) {
         return "NPC";
      } else {
         return targetTypes.contains(EntityTargetType.PLAYER) ? "PLAYER" : "NONE";
      }
   }

   @Nonnull
   private static List<DropdownEntryInfo> targetTypeEntries() {
      return List.of(
         option("server.customUI.triggerVolumeInspector.option.none", "NONE"),
         option("server.customUI.triggerVolumeInspector.option.player", "PLAYER"),
         option("server.customUI.triggerVolumeInspector.option.npc", "NPC"),
         option("server.customUI.triggerVolumeInspector.option.itemDrop", "ITEM_DROP"),
         option("server.customUI.triggerVolumeInspector.option.projectile", "PROJECTILE"),
         option("server.customUI.triggerVolumeInspector.option.all", "ALL")
      );
   }

   @Nonnull
   private static List<DropdownEntryInfo> projectileSourceEntries() {
      return List.of(
         option(
            "server.customUI.triggerVolumeInspector.option.projectileSource.shooter",
            ProjectileSource.SHOOTER.name(),
            "server.customUI.triggerVolumeInspector.option.projectileSource.shooter.tooltip"
         ),
         option(
            "server.customUI.triggerVolumeInspector.option.projectileSource.projectile",
            ProjectileSource.PROJECTILE.name(),
            "server.customUI.triggerVolumeInspector.option.projectileSource.projectile.tooltip"
         )
      );
   }

   @Nonnull
   private static List<DropdownEntryInfo> shapeEntries() {
      return List.of(
         option("server.customUI.triggerVolumeInspector.option.box", TriggerVolumeShapeType.Box.name()),
         option("server.customUI.triggerVolumeInspector.option.sphere", TriggerVolumeShapeType.Sphere.name()),
         option("server.customUI.triggerVolumeInspector.option.cylinder", TriggerVolumeShapeType.Cylinder.name())
      );
   }

   @Nonnull
   private static List<DropdownEntryInfo> cooldownModeEntries() {
      return List.of(
         option(
            "server.customUI.triggerVolumeInspector.option.perEntity",
            CooldownMode.PER_ENTITY.name(),
            "server.customUI.triggerVolumeInspector.option.perEntity.tooltip"
         ),
         option("server.customUI.triggerVolumeInspector.option.total", CooldownMode.TOTAL.name(), "server.customUI.triggerVolumeInspector.option.total.tooltip")
      );
   }

   @Nonnull
   private static List<DropdownEntryInfo> conditionTimingEntries() {
      return List.of(
         option(
            "server.customUI.triggerVolumeInspector.option.beforeVolumeDelay",
            ConditionTiming.BEFORE_VOLUME_DELAY.name(),
            "server.customUI.triggerVolumeInspector.option.beforeVolumeDelay.tooltip"
         ),
         option(
            "server.customUI.triggerVolumeInspector.option.afterVolumeDelay",
            ConditionTiming.AFTER_VOLUME_DELAY.name(),
            "server.customUI.triggerVolumeInspector.option.afterVolumeDelay.tooltip"
         )
      );
   }

   @Nonnull
   private static List<DropdownEntryInfo> rejectionDelayModeEntries() {
      return List.of(
         option(
            "server.customUI.triggerVolumeInspector.option.rejectionImmediate",
            RejectionDelayMode.IMMEDIATE.name(),
            "server.customUI.triggerVolumeInspector.option.rejectionImmediate.tooltip"
         ),
         option(
            "server.customUI.triggerVolumeInspector.option.rejectionUseVolumeDelay",
            RejectionDelayMode.USE_VOLUME_DELAY.name(),
            "server.customUI.triggerVolumeInspector.option.rejectionUseVolumeDelay.tooltip"
         )
      );
   }

   @Nonnull
   private static DropdownEntryInfo option(@Nonnull String labelKey, @Nonnull String value) {
      return new DropdownEntryInfo(LocalizableString.fromMessageId(labelKey), value);
   }

   @Nonnull
   private static DropdownEntryInfo option(@Nonnull String labelKey, @Nonnull String value, @Nonnull String tooltipKey) {
      return new DropdownEntryInfo(LocalizableString.fromMessageId(labelKey), value, LocalizableString.fromMessageId(tooltipKey));
   }

   @Nonnull
   private static Message fieldLabel(@Nonnull String fieldKey) {
      return Message.translation("server.customUI.triggerVolumeInspector.field." + fieldKey);
   }

   @Nonnull
   private static Message volumeFieldTooltip(@Nonnull String fieldKey) {
      return Message.translation("server.customUI.triggerVolumeInspector.field." + fieldKey + ".tooltip");
   }

   @Nonnull
   private static Message fieldLabel(@Nonnull String typeId, @Nonnull String fieldKey) {
      String key = "server.customUI.triggerVolumeEffectEditor.field." + typeId + "." + fieldKey;
      if (messageExists(key)) {
         return Message.translation(key);
      }

      String commonKey = "server.customUI.triggerVolumeEffectEditor.field.common." + fieldKey;
      return messageExists(commonKey) ? Message.translation(commonKey) : Message.translation(key);
   }

   @Nonnull
   private static Message effectFieldTooltip(@Nonnull String typeId, @Nonnull String fieldKey) {
      String key = "server.customUI.triggerVolumeEffectEditor.field." + typeId + "." + fieldKey + ".tooltip";
      if (messageExists(key)) {
         return Message.translation(key);
      }

      String commonKey = "server.customUI.triggerVolumeEffectEditor.field.common." + fieldKey + ".tooltip";
      return messageExists(commonKey) ? Message.translation(commonKey) : Message.translation(key);
   }

   private static void setEffectFieldTooltip(@Nonnull UICommandBuilder cmd, @Nonnull String selector, @Nonnull String typeId, @Nonnull String fieldKey) {
      if (!typeId.isEmpty()) {
         cmd.set(selector + " #Label.TooltipText", effectFieldTooltip(typeId, fieldKey));
      }
   }

   private static void setEffectFieldPlaceholder(@Nonnull UICommandBuilder cmd, @Nonnull String selector, @Nonnull String typeId, @Nonnull String fieldKey) {
      if (!typeId.isEmpty()) {
         String key = "server.customUI.triggerVolumeEffectEditor.field." + typeId + "." + fieldKey + ".placeholder";
         I18nModule i18n = I18nModule.get();
         if (i18n != null && i18n.getMessage("en-US", key) != null) {
            cmd.set(selector + " #Input.PlaceholderText", Message.translation(key));
         }
      }
   }

   private static boolean isNonNegativeNumericField(@Nonnull String typeId, @Nonnull String fieldKey) {
      return NON_NEGATIVE_NUMERIC_FIELDS.contains(typeId + "." + fieldKey);
   }

   @Nonnull
   private static String getCodecTypeId(@Nonnull Object target) {
      return target instanceof TriggerCondition condition ? getConditionTypeId(condition) : getTypeId((TriggerEffect)target);
   }

   @Nonnull
   private static EventData paramEvent(@Nonnull String key, @Nonnull String valueRef) {
      return new EventData().append("Action", TriggerVolumeInspectorPage.Action.UpdateParameter.name()).append("ParamKey", key).append("@ParamValue", valueRef);
   }

   @Nonnull
   private static EventData boolParamEvent(@Nonnull String key, @Nonnull String valueRef) {
      return new EventData().append("Action", TriggerVolumeInspectorPage.Action.UpdateParameter.name()).append("ParamKey", key).append("@ParamBool", valueRef);
   }

   @Nonnull
   private static EventData numericParamEvent(@Nonnull String key, @Nonnull String valueRef) {
      return new EventData()
         .append("Action", TriggerVolumeInspectorPage.Action.UpdateParameter.name())
         .append("ParamKey", key)
         .append("@ParamNumericValue", valueRef);
   }

   @Nonnull
   private static TriggerVolumeInspectorPage.EffectListKind parseEffectListKind(
      @Nonnull String value, @Nonnull TriggerVolumeInspectorPage.EffectListKind fallback
   ) {
      try {
         return TriggerVolumeInspectorPage.EffectListKind.valueOf(value);
      } catch (IllegalArgumentException exception) {
         return fallback;
      }
   }

   @Nonnull
   private static TriggerEventType parseTriggerEventType(@Nonnull String value, @Nonnull TriggerEventType fallback) {
      try {
         return TriggerEventType.valueOf(value);
      } catch (IllegalArgumentException exception) {
         return fallback;
      }
   }

   @Nonnull
   private static String humanizeTypeId(@Nonnull String typeId) {
      if (typeId.isEmpty()) {
         return typeId;
      }

      StringBuilder builder = new StringBuilder(typeId.length() + 4);
      builder.append(typeId.charAt(0));

      for (int i = 1; i < typeId.length(); i++) {
         char currentChar = typeId.charAt(i);
         if (Character.isUpperCase(currentChar) && !Character.isUpperCase(typeId.charAt(i - 1))) {
            builder.append(' ');
         }

         builder.append(currentChar);
      }

      return builder.toString();
   }

   @Nonnull
   private static List<String> getSortedTypeIds() {
      ArrayList<String> ids = new ArrayList<>(TriggerEffect.CODEC.getRegisteredIds());
      Collections.sort(ids);
      return ids;
   }

   @Nonnull
   private static List<String> getSortedConditionTypeIds() {
      ArrayList<String> ids = new ArrayList<>(TriggerCondition.CODEC.getRegisteredIds());
      Collections.sort(ids);
      return ids;
   }

   @Nonnull
   private static String getTypeId(@Nonnull TriggerEffect effect) {
      String typeId = TriggerEffect.CODEC.getIdFor((Class<? extends TriggerEffect>)effect.getClass());
      return typeId != null ? typeId : "unknown";
   }

   @Nonnull
   private static String getConditionTypeId(@Nonnull TriggerCondition condition) {
      String typeId = TriggerCondition.CODEC.getIdFor((Class<? extends TriggerCondition>)condition.getClass());
      return typeId != null ? typeId : "unknown";
   }

   @Nullable
   private static BuilderCodec<TriggerEffect> getBuilderCodecFor(@Nonnull String typeId) {
      return (BuilderCodec<TriggerEffect>)(TriggerEffect.CODEC.getCodecFor(typeId) instanceof BuilderCodec<?> builderCodec ? builderCodec : null);
   }

   @Nullable
   private static BuilderCodec<TriggerCondition> getConditionBuilderCodecFor(@Nonnull String typeId) {
      return (BuilderCodec<TriggerCondition>)(TriggerCondition.CODEC.getCodecFor(typeId) instanceof BuilderCodec<?> builderCodec ? builderCodec : null);
   }

   @Nonnull
   private static BsonDocument encodeEffect(@Nonnull BuilderCodec<TriggerEffect> codec, @Nonnull TriggerEffect effect) {
      try {
         return codec.encode(effect, EmptyExtraInfo.EMPTY);
      } catch (Exception exception) {
         return new BsonDocument();
      }
   }

   @Nonnull
   private static BsonDocument encodeCondition(@Nonnull BuilderCodec<TriggerCondition> codec, @Nonnull TriggerCondition condition) {
      try {
         return codec.encode(condition, EmptyExtraInfo.EMPTY);
      } catch (Exception exception) {
         return new BsonDocument();
      }
   }

   @Nullable
   private static BsonValue stringToBsonValue(@Nonnull Codec<?> childCodec, @Nullable String value) {
      if (value == null) {
         return null;
      } else if (childCodec == Codec.STRING) {
         return new BsonString(value);
      } else if (childCodec == Codec.STRING_ARRAY) {
         return stringsToBsonArray(value);
      } else if (childCodec == Codec.FLOAT || childCodec == Codec.DOUBLE) {
         return new BsonDouble(Double.parseDouble(value));
      } else if (childCodec == Codec.INTEGER) {
         return new BsonInt32(Integer.parseInt(value));
      } else if (childCodec == Codec.LONG) {
         return new BsonInt64(Long.parseLong(value));
      } else if (childCodec == Codec.BOOLEAN) {
         return new BsonBoolean(Boolean.parseBoolean(value));
      } else {
         return childCodec instanceof EnumCodec ? new BsonString(value) : new BsonString(value);
      }
   }

   @Nonnull
   private static BsonArray stringsToBsonArray(@Nonnull String value) {
      BsonArray array = new BsonArray();
      Arrays.stream(value.split(",")).map(String::trim).filter(stringValue -> !stringValue.isEmpty()).map(BsonString::new).forEach(array::add);
      return array;
   }

   @Nonnull
   private static String bsonValueToString(@Nonnull BsonValue value) {
      if (value instanceof BsonString bsonString) {
         return bsonString.getValue();
      } else if (value instanceof BsonBoolean bsonBoolean) {
         return String.valueOf(bsonBoolean.getValue());
      } else if (value instanceof BsonDouble bsonDouble) {
         return String.valueOf(bsonDouble.getValue());
      } else if (value instanceof BsonInt32 bsonInt) {
         return String.valueOf(bsonInt.getValue());
      } else {
         return value instanceof BsonInt64 bsonLong ? String.valueOf(bsonLong.getValue()) : value.toString();
      }
   }

   private static void materializeDefaults(@Nonnull BuilderCodec<TriggerEffect> codec, @Nonnull TriggerEffect effect) {
      BsonDocument encoded = encodeEffect(codec, effect);
      ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();

      for (Entry<String, List<BuilderField<TriggerEffect, ?>>> entry : codec.getEntries().entrySet()) {
         String key = entry.getKey();
         if (!"Event".equals(key) && !"Interval".equals(key) && !"Entry".equals(key) && !encoded.containsKey(key) && !entry.getValue().isEmpty()) {
            BuilderField field = entry.getValue().getLast();
            BsonValue defaultValue = getDefaultBsonValue(getTypeId(effect), key, field.getCodec().getChildCodec());
            if (defaultValue != null) {
               BsonDocument doc = new BsonDocument();
               doc.put(key, defaultValue);

               try {
                  field.decode(doc, effect, extraInfo);
               } catch (Exception var11) {
               }
            }
         }
      }
   }

   private static void materializeConditionDefaults(@Nonnull BuilderCodec<TriggerCondition> codec, @Nonnull TriggerCondition condition) {
      BsonDocument encoded = encodeCondition(codec, condition);
      ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();

      for (Entry<String, List<BuilderField<TriggerCondition, ?>>> entry : codec.getEntries().entrySet()) {
         String key = entry.getKey();
         if (!"Event".equals(key) && !"Entry".equals(key) && !encoded.containsKey(key) && !entry.getValue().isEmpty()) {
            BuilderField field = entry.getValue().getLast();
            BsonValue defaultValue = getDefaultBsonValue(getConditionTypeId(condition), key, field.getCodec().getChildCodec());
            if (defaultValue != null) {
               BsonDocument doc = new BsonDocument();
               doc.put(key, defaultValue);

               try {
                  field.decode(doc, condition, extraInfo);
               } catch (Exception var11) {
               }
            }
         }
      }
   }

   @Nullable
   private static BsonValue getDefaultBsonValue(@Nonnull String typeId, @Nonnull String fieldKey, @Nonnull Codec<?> childCodec) {
      BsonValue defaultValue = DEFAULT_FIELD_VALUES.get(typeId + "." + fieldKey);
      return defaultValue != null ? defaultValue : getDefaultBsonValue(childCodec);
   }

   @Nullable
   private static BsonValue getDefaultBsonValue(@Nonnull Codec<?> childCodec) {
      if (childCodec == Codec.BOOLEAN) {
         return new BsonBoolean(false);
      } else if (childCodec == Codec.FLOAT || childCodec == Codec.DOUBLE) {
         return new BsonDouble(0.0);
      } else if (childCodec == Codec.INTEGER) {
         return new BsonInt32(0);
      } else if (childCodec == Codec.LONG) {
         return new BsonInt64(0L);
      } else if (childCodec == Codec.STRING) {
         return new BsonString("");
      } else if (childCodec == Codec.STRING_ARRAY) {
         return new BsonArray();
      } else if (childCodec == Vector3dUtil.CODEC) {
         BsonDocument doc = new BsonDocument();
         doc.put("X", new BsonDouble(0.0));
         doc.put("Y", new BsonDouble(0.0));
         doc.put("Z", new BsonDouble(0.0));
         return doc;
      } else {
         return null;
      }
   }

   @Nonnull
   private static Collection<String> getAssetIdsForSource(@Nullable String sourceId) {
      return sourceId == null ? List.of() : TriggerVolumesPlugin.get().getAssetIds(sourceId);
   }

   private Collection<String> resolvePickerIds() {
      if (this.getSelectedItem() instanceof PlayAnimationEffect animation) {
         if ("Animation".equals(this.pendingPickerSource)) {
            String applyOn = animation.getNpcType();
            if (!applyOn.isBlank()) {
               return TriggerVolumesPlugin.get().collectAnimationIdsForApplyOn(applyOn);
            }
         } else if ("AnimationApplyOn".equals(this.pendingPickerSource)) {
            String animationKey = animation.getAnimation();
            if (animationKey != null && !animationKey.isBlank()) {
               return TriggerVolumesPlugin.get().collectApplyOnIdsForAnimation(animationKey);
            }
         }
      }

      return getAssetIdsForSource(this.pendingPickerSource);
   }

   @Nullable
   private static String getAssetSourceForField(@Nonnull String typeId, @Nonnull String fieldKey) {
      return TriggerVolumesPlugin.get().getAssetSourceForField(typeId, fieldKey);
   }

   public enum Action {
      Select,
      ChangeWorld,
      FilterChanged,
      ChangeTab,
      UpdateVolumeField,
      UpdateTag,
      RemoveTag,
      DeleteSelection,
      Save,
      Discard,
      Teleport,
      ToggleRenameSelected,
      ConfirmRenameSelected,
      CancelRenameSelected,
      SelectEffect,
      AddEffect,
      RemoveEffect,
      DuplicateEffect,
      MoveEffectUp,
      MoveEffectDown,
      UpdateAddTarget,
      UpdateAddEffectType,
      UpdateAddEventType,
      UpdateAddEntry,
      ToggleEventCategory,
      UpdateParameter,
      CommitEntry,
      TogglePrefabPreview,
      OpenPresetSave,
      PresetNameChanged,
      ConfirmSavePreset,
      CancelPresetSave,
      OpenPresetLoad,
      LoadPreset,
      CancelPresetLoad,
      OpenAssetPicker,
      AssetPickerSearch,
      AssetPickerSelect,
      ConfirmAssetPicker,
      PreviewSound,
      CancelAssetPicker,
      OpenPackBrowser,
      ConfirmPackBrowser,
      CancelPackBrowser,
      OpenCreatePack,
      CreatePack,
      CancelCreatePack,
      PackSearch,
      PackSelect;
   }

   private enum EffectListKind {
      CONDITION,
      EFFECT,
      REJECTION_EFFECT;
   }

   private record EventCategoryKey(@Nonnull TriggerEventType eventType, int entry) {
   }

   private enum EventCategoryScope {
      GROUP,
      VOLUME;
   }

   public enum InspectorTab {
      VOLUME("server.customUI.triggerVolumeInspector.tab.volume", "server.customUI.triggerVolumeInspector.tab.volume.tooltip"),
      EFFECTS("server.customUI.triggerVolumeInspector.tab.effects", "server.customUI.triggerVolumeInspector.tab.effects.tooltip"),
      TAGS("server.customUI.triggerVolumeInspector.tab.tags", "server.customUI.triggerVolumeInspector.tab.tags.tooltip");

      private final String labelKey;
      private final String tooltipKey;

      InspectorTab(@Nonnull String labelKey, @Nonnull String tooltipKey) {
         this.labelKey = labelKey;
         this.tooltipKey = tooltipKey;
      }

      @Nonnull
      Message label() {
         return Message.translation(this.labelKey);
      }

      @Nonnull
      Message tooltip() {
         return Message.translation(this.tooltipKey);
      }
   }

   public static class PageData {
      public static final BuilderCodec<TriggerVolumeInspectorPage.PageData> CODEC = BuilderCodec.builder(
            TriggerVolumeInspectorPage.PageData.class, TriggerVolumeInspectorPage.PageData::new
         )
         .append(
            new KeyedCodec<>("Action", new EnumCodec<>(TriggerVolumeInspectorPage.Action.class, EnumCodec.EnumStyle.LEGACY)),
            (o, v) -> o.action = v,
            o -> o.action
         )
         .add()
         .append(new KeyedCodec<>("Id", Codec.STRING, false), (o, v) -> o.id = v, o -> o.id)
         .add()
         .append(new KeyedCodec<>("IsGroup", Codec.STRING, false), (o, v) -> o.isGroup = v, o -> o.isGroup)
         .add()
         .append(new KeyedCodec<>("Tab", Codec.STRING, false), (o, v) -> o.tab = v, o -> o.tab)
         .add()
         .append(new KeyedCodec<>("@WorldName", Codec.STRING, false), (o, v) -> o.worldName = v, o -> o.worldName)
         .add()
         .append(new KeyedCodec<>("@FilterText", Codec.STRING, false), (o, v) -> o.filterText = v, o -> o.filterText)
         .add()
         .append(new KeyedCodec<>("@TagKey", Codec.STRING, false), (o, v) -> o.tagKey = v, o -> o.tagKey)
         .add()
         .append(new KeyedCodec<>("@TagValues", Codec.STRING, false), (o, v) -> o.tagValues = v, o -> o.tagValues)
         .add()
         .append(new KeyedCodec<>("RemoveTagKey", Codec.STRING, false), (o, v) -> o.removeTagKey = v, o -> o.removeTagKey)
         .add()
         .append(new KeyedCodec<>("EffectIndex", Codec.STRING, false), (o, v) -> o.effectIndex = v, o -> o.effectIndex)
         .add()
         .append(new KeyedCodec<>("EffectListKind", Codec.STRING, false), (o, v) -> o.effectListKind = v, o -> o.effectListKind)
         .add()
         .append(new KeyedCodec<>("@EffectType", Codec.STRING, false), (o, v) -> o.effectType = v, o -> o.effectType)
         .add()
         .append(new KeyedCodec<>("@AddTargetKind", Codec.STRING, false), (o, v) -> o.addTargetKind = v, o -> o.addTargetKind)
         .add()
         .append(new KeyedCodec<>("@AddEventType", Codec.STRING, false), (pageData, value) -> pageData.addEventType = value, pageData -> pageData.addEventType)
         .add()
         .append(new KeyedCodec<>("@AddEntry", Codec.STRING, false), (o, v) -> o.addEntry = v, o -> o.addEntry)
         .add()
         .append(new KeyedCodec<>("EventType", Codec.STRING, false), (o, v) -> o.eventType = v, o -> o.eventType)
         .add()
         .append(new KeyedCodec<>("EventEntry", Codec.STRING, false), (o, v) -> o.eventEntry = v, o -> o.eventEntry)
         .add()
         .append(new KeyedCodec<>("EventCategoryScope", Codec.STRING, false), (o, v) -> o.eventCategoryScope = v, o -> o.eventCategoryScope)
         .add()
         .append(new KeyedCodec<>("ParamKey", Codec.STRING, false), (o, v) -> o.paramKey = v, o -> o.paramKey)
         .add()
         .append(new KeyedCodec<>("@ParamValue", Codec.STRING, false), (o, v) -> o.paramValue = v, o -> o.paramValue)
         .add()
         .append(new KeyedCodec<>("@ParamBool", Codec.BOOLEAN, false), (o, v) -> o.paramBool = v, o -> o.paramBool)
         .add()
         .append(new KeyedCodec<>("@ParamNumericValue", Codec.DOUBLE, false), (o, v) -> o.paramNumericValue = v, o -> o.paramNumericValue)
         .add()
         .append(new KeyedCodec<>("@VecX", Codec.DOUBLE, false), (o, v) -> o.vecX = v, o -> o.vecX)
         .add()
         .append(new KeyedCodec<>("@VecY", Codec.DOUBLE, false), (o, v) -> o.vecY = v, o -> o.vecY)
         .add()
         .append(new KeyedCodec<>("@VecZ", Codec.DOUBLE, false), (o, v) -> o.vecZ = v, o -> o.vecZ)
         .add()
         .append(new KeyedCodec<>("@PresetName", Codec.STRING, false), (o, v) -> o.presetName = v, o -> o.presetName)
         .add()
         .append(new KeyedCodec<>("PresetId", Codec.STRING, false), (o, v) -> o.presetId = v, o -> o.presetId)
         .add()
         .append(new KeyedCodec<>("@AssetPickerQuery", Codec.STRING, false), (o, v) -> o.assetPickerQuery = v, o -> o.assetPickerQuery)
         .add()
         .append(new KeyedCodec<>("AssetPickerSelection", Codec.STRING, false), (o, v) -> o.assetPickerSelection = v, o -> o.assetPickerSelection)
         .add()
         .append(new KeyedCodec<>("Pack", Codec.STRING, false), (o, v) -> o.packBrowserData.pack = v, o -> o.packBrowserData.pack)
         .add()
         .append(new KeyedCodec<>("@PackSearch", Codec.STRING, false), (o, v) -> o.packBrowserData.search = v, o -> o.packBrowserData.search)
         .add()
         .append(new KeyedCodec<>("@CreateName", Codec.STRING, false), (o, v) -> o.packBrowserData.createName = v, o -> o.packBrowserData.createName)
         .add()
         .append(new KeyedCodec<>("@CreateGroup", Codec.STRING, false), (o, v) -> o.packBrowserData.createGroup = v, o -> o.packBrowserData.createGroup)
         .add()
         .append(
            new KeyedCodec<>("@CreateDescription", Codec.STRING, false),
            (o, v) -> o.packBrowserData.createDescription = v,
            o -> o.packBrowserData.createDescription
         )
         .add()
         .append(new KeyedCodec<>("@CreateVersion", Codec.STRING, false), (o, v) -> o.packBrowserData.createVersion = v, o -> o.packBrowserData.createVersion)
         .add()
         .append(new KeyedCodec<>("@CreateWebsite", Codec.STRING, false), (o, v) -> o.packBrowserData.createWebsite = v, o -> o.packBrowserData.createWebsite)
         .add()
         .append(
            new KeyedCodec<>("@CreateAuthorName", Codec.STRING, false),
            (o, v) -> o.packBrowserData.createAuthorName = v,
            o -> o.packBrowserData.createAuthorName
         )
         .add()
         .append(new KeyedCodec<>("ValidateCreate", Codec.STRING, false), (o, v) -> o.packBrowserData.validateCreate = v, o -> o.packBrowserData.validateCreate)
         .add()
         .append(
            new KeyedCodec<>("@CreateTargetDir", Codec.STRING, false), (o, v) -> o.packBrowserData.createTargetDir = v, o -> o.packBrowserData.createTargetDir
         )
         .add()
         .append(
            new KeyedCodec<>("@DirectoryFilter", Codec.STRING, false), (o, v) -> o.packBrowserData.directoryFilter = v, o -> o.packBrowserData.directoryFilter
         )
         .add()
         .build();
      public TriggerVolumeInspectorPage.Action action;
      public String id;
      public String isGroup;
      public String tab;
      public String worldName;
      public String filterText;
      public String tagKey;
      public String tagValues;
      public String removeTagKey;
      public String effectIndex;
      public String effectListKind;
      public String effectType;
      public String addTargetKind;
      public String addEventType;
      public String addEntry;
      public String eventType;
      public String eventEntry;
      public String eventCategoryScope;
      public String paramKey;
      public String paramValue;
      public Boolean paramBool;
      public Double paramNumericValue;
      public Double vecX;
      public Double vecY;
      public Double vecZ;
      public String presetName;
      public String presetId;
      public String assetPickerQuery;
      public String assetPickerSelection;
      public final AssetPackSaveBrowserEventData packBrowserData = new AssetPackSaveBrowserEventData();
   }

   private record RowEntry(@Nonnull String id, boolean isGroup, int listIndex) {
   }
}
