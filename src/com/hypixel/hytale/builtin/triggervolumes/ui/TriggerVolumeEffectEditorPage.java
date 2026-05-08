package com.hypixel.hytale.builtin.triggervolumes.ui;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.builtin.triggervolumes.TriggerVolumesPlugin;
import com.hypixel.hytale.builtin.triggervolumes.asset.TriggerEffectAsset;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEventType;
import com.hypixel.hytale.builtin.triggervolumes.manager.GroupEntry;
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
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.DropdownEntryInfo;
import com.hypixel.hytale.server.core.ui.LocalizableString;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.BsonUtil;
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
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bson.BsonValue;

public class TriggerVolumeEffectEditorPage extends InteractiveCustomUIPage<TriggerVolumeEffectEditorPage.PageData> {
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
   private static final String COMMON_TEXT_BUTTON_DOCUMENT = "Common/TextButton.ui";
   private static final int ASSET_PICKER_MAX_RESULTS = 50;
   private final VolumeEntry volumeEntry;
   private final TriggerVolumeManager manager;
   private final List<TriggerEffect> workingEffects;
   private int selectedEffectIndex = -1;
   private final boolean groupEditMode;
   @Nullable
   private final GroupEntry readOnlyGroup;
   @Nullable
   private final String groupId;
   @Nonnull
   private final List<VolumeEntry> groupMembers;
   @Nullable
   private String pendingPickerFieldKey;
   @Nullable
   private String pendingPickerSource;
   @Nonnull
   private String assetPickerSearchQuery = "";
   private boolean skipSaveOnDismiss;
   private static final String FIELD_TEXT = "Pages/Fields/TextRow.ui";
   private static final String FIELD_NUMBER = "Pages/Fields/NumberRow.ui";
   private static final String FIELD_INT = "Pages/Fields/IntRow.ui";
   private static final String FIELD_CHECKBOX = "Pages/Fields/CheckboxRow.ui";
   private static final String FIELD_DROPDOWN = "Pages/Fields/DropdownRow.ui";
   private static final String FIELD_VEC3 = "Pages/Fields/Vec3Row.ui";
   private static final String FIELD_ASSET_PICKER = "Pages/Fields/AssetPickerRow.ui";
   private static final String INLINE_SECTION_GROUP_EFFECTS = "Label { Anchor: (Top: 6, Bottom: 4, Left: 4); Style: (FontSize: 13, TextColor: #9aacbc, HorizontalAlignment: Start); Text: %server.customUI.triggerVolumeEffectEditor.groupEffects; }";
   private static final String INLINE_SECTION_INDIVIDUAL_EFFECTS = "Label { Anchor: (Top: 10, Bottom: 4, Left: 4); Style: (FontSize: 13, TextColor: #9aacbc, HorizontalAlignment: Start); Text: %server.customUI.triggerVolumeEffectEditor.individualEffects; }";
   private static final String INLINE_SECTION_INDIVIDUAL_EFFECTS_SOLO = "Label { Anchor: (Top: 4, Bottom: 4, Left: 4); Style: (FontSize: 13, TextColor: #9aacbc, HorizontalAlignment: Start); Text: %server.customUI.triggerVolumeEffectEditor.individualEffects; }";

   public TriggerVolumeEffectEditorPage(@Nonnull PlayerRef playerRef, @Nonnull VolumeEntry volumeEntry, @Nonnull TriggerVolumeManager manager) {
      super(playerRef, CustomPageLifetime.CanDismiss, TriggerVolumeEffectEditorPage.PageData.CODEC);
      this.volumeEntry = volumeEntry;
      this.manager = manager;
      this.groupEditMode = false;
      this.readOnlyGroup = null;
      this.groupId = null;
      this.groupMembers = List.of();
      this.workingEffects = TriggerEffect.deepCopyList(volumeEntry.getEffects());
   }

   public TriggerVolumeEffectEditorPage(
      @Nonnull PlayerRef playerRef, @Nonnull VolumeEntry volumeEntry, @Nonnull TriggerVolumeManager manager, @Nonnull GroupEntry readOnlyGroup
   ) {
      super(playerRef, CustomPageLifetime.CanDismiss, TriggerVolumeEffectEditorPage.PageData.CODEC);
      this.volumeEntry = volumeEntry;
      this.manager = manager;
      this.groupEditMode = false;
      this.readOnlyGroup = readOnlyGroup;
      this.groupId = null;
      this.groupMembers = List.of();
      this.workingEffects = TriggerEffect.deepCopyList(volumeEntry.getEffects());
   }

   public TriggerVolumeEffectEditorPage(
      @Nonnull PlayerRef playerRef,
      @Nonnull VolumeEntry volumeEntry,
      @Nonnull TriggerVolumeManager manager,
      @Nonnull String groupId,
      @Nonnull List<VolumeEntry> groupMembers
   ) {
      super(playerRef, CustomPageLifetime.CanDismiss, TriggerVolumeEffectEditorPage.PageData.CODEC);
      this.volumeEntry = volumeEntry;
      this.manager = manager;
      this.groupEditMode = true;
      this.readOnlyGroup = null;
      this.groupId = groupId;
      this.groupMembers = groupMembers;
      GroupEntry g = manager.getGroup(groupId);
      this.workingEffects = g != null ? TriggerEffect.deepCopyList(g.getEffects()) : new ArrayList<>();
   }

   @Override
   public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, @Nonnull Store<EntityStore> store) {
      cmd.append("Pages/TriggerVolumeEffectEditor.ui");
      this.applyPanelChrome(cmd);
      this.buildAddEffectDropdown(cmd);
      this.buildEffectList(cmd, evt);
      this.buildDetailPanel(cmd, evt);
      this.bindStaticEvents(evt);
   }

   @Override
   public void onDismiss(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
      if (this.skipSaveOnDismiss) {
         this.skipSaveOnDismiss = false;
      } else {
         this.saveEffectsToVolumes();
      }
   }

   public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull TriggerVolumeEffectEditorPage.PageData data) {
      if (data.action != null) {
         switch (data.action) {
            case SelectEffect:
               this.onSelectEffect(data);
               break;
            case AddEffect:
               this.onAddEffect(data);
               break;
            case RemoveEffect:
               this.onRemoveEffect();
               break;
            case UpdateParameter:
               this.onUpdateParameter(data);
               break;
            case SaveEffects:
               this.onSaveEffects();
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
            case CancelAssetPicker:
               this.onCancelAssetPicker();
               break;
            case EditGroup:
               this.onEditGroup(ref, store);
               break;
            case DiscardChanges:
               this.close();
         }
      }
   }

   private void buildAddEffectDropdown(@Nonnull UICommandBuilder cmd) {
      List<String> typeIds = getSortedTypeIds();
      ObjectArrayList<DropdownEntryInfo> entries = new ObjectArrayList<>();

      for (String typeId : typeIds) {
         entries.add(new DropdownEntryInfo(LocalizableString.fromString(humanizeTypeId(typeId)), typeId));
      }

      cmd.set("#AddEffectDropdown.Entries", entries);
      if (!typeIds.isEmpty()) {
         cmd.set("#AddEffectDropdown.Value", typeIds.getFirst());
      }
   }

   @Nonnull
   private static String humanizeTypeId(@Nonnull String id) {
      if (id.isEmpty()) {
         return id;
      }

      StringBuilder sb = new StringBuilder(id.length() + 4);
      sb.append(id.charAt(0));

      for (int i = 1; i < id.length(); i++) {
         char c = id.charAt(i);
         if (Character.isUpperCase(c) && !Character.isUpperCase(id.charAt(i - 1))) {
            sb.append(' ');
         }

         sb.append(c);
      }

      return sb.toString();
   }

   private void buildEffectList(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt) {
      cmd.clear("#EffectListContainer");
      int childIndex = 0;
      if (this.groupEditMode) {
         cmd.appendInline(
            "#EffectListContainer",
            "Label { Anchor: (Top: 6, Bottom: 4, Left: 4); Style: (FontSize: 13, TextColor: #9aacbc, HorizontalAlignment: Start); Text: %server.customUI.triggerVolumeEffectEditor.groupEffects; }"
         );
         childIndex++;

         for (int i = 0; i < this.workingEffects.size(); i++) {
            this.appendSelectableEffectRow(cmd, evt, childIndex, i);
            childIndex++;
         }
      } else if (this.readOnlyGroup != null) {
         cmd.appendInline(
            "#EffectListContainer",
            "Label { Anchor: (Top: 6, Bottom: 4, Left: 4); Style: (FontSize: 13, TextColor: #9aacbc, HorizontalAlignment: Start); Text: %server.customUI.triggerVolumeEffectEditor.groupEffects; }"
         );
         childIndex++;
         List<TriggerEffect> ge = this.readOnlyGroup.getEffects();

         for (int i = 0; i < ge.size(); i++) {
            TriggerEffect ef = ge.get(i);
            String typeId = getTypeId(ef);
            String eventStr = ef.getEventType() != null ? ef.getEventType().name() : "?";
            String label = i + 1 + ". " + typeId + " (" + eventStr + ")";
            cmd.appendInline("#EffectListContainer", readOnlyGroupEffectRow(label));
            childIndex++;
         }

         cmd.appendInline(
            "#EffectListContainer",
            "Label { Anchor: (Top: 10, Bottom: 4, Left: 4); Style: (FontSize: 13, TextColor: #9aacbc, HorizontalAlignment: Start); Text: %server.customUI.triggerVolumeEffectEditor.individualEffects; }"
         );
         childIndex++;

         for (int i = 0; i < this.workingEffects.size(); i++) {
            this.appendSelectableEffectRow(cmd, evt, childIndex, i);
            childIndex++;
         }
      } else {
         cmd.appendInline(
            "#EffectListContainer",
            "Label { Anchor: (Top: 4, Bottom: 4, Left: 4); Style: (FontSize: 13, TextColor: #9aacbc, HorizontalAlignment: Start); Text: %server.customUI.triggerVolumeEffectEditor.individualEffects; }"
         );
         childIndex++;

         for (int i = 0; i < this.workingEffects.size(); i++) {
            this.appendSelectableEffectRow(cmd, evt, childIndex, i);
            childIndex++;
         }
      }

      cmd.set("#RemoveEffectButton.Disabled", this.selectedEffectIndex < 0);
   }

   private void appendSelectableEffectRow(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, int listChildIndex, int effectIndex) {
      TriggerEffect effect = this.workingEffects.get(effectIndex);
      String typeId = getTypeId(effect);
      String eventStr = effect.getEventType() != null ? effect.getEventType().name() : "?";
      String label = effectIndex + 1 + ". " + humanizeTypeId(typeId) + " (" + eventStr + ")";
      String selector = "#EffectListContainer[" + listChildIndex + "]";
      boolean selected = effectIndex == this.selectedEffectIndex;
      cmd.appendInline("#EffectListContainer", effectListButton(label, selected));
      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         selector,
         new EventData().append("Action", TriggerVolumeEffectEditorPage.Action.SelectEffect.name()).append("EffectIndex", String.valueOf(effectIndex))
      );
   }

   private void buildDetailPanel(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt) {
      cmd.clear("#DetailPanel");
      if (this.selectedEffectIndex >= 0 && this.selectedEffectIndex < this.workingEffects.size()) {
         cmd.set("#NoSelectionLabel.Visible", false);
         cmd.set("#DetailPanel.Visible", true);
         TriggerEffect effect = this.workingEffects.get(this.selectedEffectIndex);
         String typeId = getTypeId(effect);
         int row = 0;
         row = this.addDropdownRow(
            cmd,
            evt,
            row,
            Message.translation("server.customUI.triggerVolumeEffectEditor.baseField.event"),
            "Event",
            Message.translation("server.customUI.triggerVolumeEffectEditor.baseField.event.tooltip"),
            Arrays.stream(TriggerEventType.values()).map(Enum::name).toList(),
            effect.getEventType() != null ? effect.getEventType().name() : TriggerEventType.ENTER.name()
         );
         row = this.addNumberRow(
            cmd,
            evt,
            row,
            Message.translation("server.customUI.triggerVolumeEffectEditor.baseField.interval"),
            "Interval",
            Message.translation("server.customUI.triggerVolumeEffectEditor.baseField.interval.tooltip"),
            String.valueOf(effect.getInterval()),
            2
         );
         row = this.addNumberRow(
            cmd,
            evt,
            row,
            Message.translation("server.customUI.triggerVolumeEffectEditor.effectDelay"),
            "Delay",
            Message.translation("server.customUI.triggerVolumeEffectEditor.effectDelay.tooltip"),
            String.valueOf(effect.getDelay()),
            1
         );
         BuilderCodec<TriggerEffect> codec = getBuilderCodecFor(typeId);
         if (codec != null) {
            BsonDocument encoded = encodeEffect(codec, effect);

            for (Entry<String, List<BuilderField<TriggerEffect, ?>>> entry : codec.getEntries().entrySet()) {
               String key = entry.getKey();
               if (!"Event".equals(key) && !"Interval".equals(key) && !"Delay".equals(key)) {
                  List<BuilderField<TriggerEffect, ?>> fields = entry.getValue();
                  if (!fields.isEmpty()) {
                     BuilderField<TriggerEffect, ?> field = fields.getLast();
                     Codec<?> childCodec = field.getCodec().getChildCodec();
                     BsonValue bsonValue = encoded.get(key);
                     row = this.addFieldRow(cmd, evt, row, typeId, key, childCodec, bsonValue);
                  }
               }
            }
         }
      } else {
         cmd.set("#NoSelectionLabel.Visible", true);
         cmd.set("#DetailPanel.Visible", false);
      }
   }

   private int addFieldRow(
      @Nonnull UICommandBuilder cmd,
      @Nonnull UIEventBuilder evt,
      int row,
      @Nonnull String typeId,
      @Nonnull String key,
      @Nonnull Codec<?> childCodec,
      @Nullable BsonValue bsonValue
   ) {
      if (childCodec == Codec.BOOLEAN) {
         boolean val = bsonValue instanceof BsonBoolean bb && bb.getValue();
         return this.addCheckboxRow(cmd, evt, row, typeId, key, val);
      }

      if (childCodec == Codec.FLOAT) {
         String val = bsonValue instanceof BsonDouble bd ? String.valueOf((float)bd.getValue()) : "0.0";
         return this.addNumberRow(cmd, evt, row, typeId, key, val, 2);
      }

      if (childCodec == Codec.INTEGER) {
         String val = bsonValue instanceof BsonInt32 bi ? String.valueOf(bi.getValue()) : "0";
         return this.addNumberRow(cmd, evt, row, typeId, key, val, 0);
      }

      if (childCodec == Codec.LONG) {
         String val = bsonValue instanceof BsonInt64 bl ? String.valueOf(bl.getValue()) : "0";
         return this.addNumberRow(cmd, evt, row, typeId, key, val, 0);
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

         return this.addVec3Row(cmd, evt, row, typeId, key, x, y, z);
      } else if (!(childCodec instanceof EnumCodec<?> enumCodec)) {
         if (childCodec == Codec.STRING) {
            String val = bsonValue instanceof BsonString bs ? bs.getValue() : "";
            String source = getAssetSourceForField(typeId, key);
            return source != null ? this.addAssetPickerRow(cmd, evt, row, typeId, key, val) : this.addTextRow(cmd, evt, row, typeId, key, val);
         } else {
            String val = bsonValue != null ? bsonValueToString(bsonValue) : "";
            return this.addTextRow(cmd, evt, row, typeId, key, val);
         }
      } else {
         String val = "";
         if (bsonValue != null) {
            try {
               ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
               Enum<?> decoded = enumCodec.decode(bsonValue, extraInfo);
               val = enumCodec.getEnumKeys()[decoded.ordinal()];
            } catch (Exception e) {
               val = bsonValue instanceof BsonString bs ? bs.getValue() : "";
            }
         }

         return this.addDropdownRow(cmd, evt, row, typeId, key, List.of(enumCodec.getEnumKeys()), val);
      }
   }

   @Nonnull
   private static Message fieldLabel(@Nonnull String typeId, @Nonnull String fieldKey) {
      return Message.translation("server.customUI.triggerVolumeEffectEditor.field." + typeId + "." + fieldKey);
   }

   @Nonnull
   private static Message fieldTooltip(@Nonnull String typeId, @Nonnull String fieldKey) {
      return Message.translation("server.customUI.triggerVolumeEffectEditor.field." + typeId + "." + fieldKey + ".tooltip");
   }

   private static void setFieldTooltip(@Nonnull UICommandBuilder cmd, @Nonnull String selector, @Nonnull String typeId, @Nonnull String fieldKey) {
      if (!typeId.isEmpty()) {
         cmd.set(selector + " #Label.TooltipText", fieldTooltip(typeId, fieldKey));
      }
   }

   private int addTextRow(
      @Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, int row, @Nonnull String typeId, @Nonnull String key, @Nonnull String value
   ) {
      String sel = "#DetailPanel[" + row + "]";
      cmd.append("#DetailPanel", "Pages/Fields/TextRow.ui");
      cmd.set(sel + " #Label.Text", fieldLabel(typeId, key));
      setFieldTooltip(cmd, sel, typeId, key);
      cmd.set(sel + " #Input.Value", value);
      evt.addEventBinding(CustomUIEventBindingType.ValueChanged, sel + " #Input", paramEvent(key, sel + " #Input.Value"), false);
      return row + 1;
   }

   private int addNumberRow(
      @Nonnull UICommandBuilder cmd,
      @Nonnull UIEventBuilder evt,
      int row,
      @Nonnull String typeId,
      @Nonnull String paramKey,
      @Nonnull String value,
      int decimals
   ) {
      return this.addNumberRow(cmd, evt, row, typeId, paramKey, fieldLabel(typeId, paramKey), null, value, decimals);
   }

   private int addNumberRow(
      @Nonnull UICommandBuilder cmd,
      @Nonnull UIEventBuilder evt,
      int row,
      @Nonnull String typeId,
      @Nonnull String paramKey,
      @Nonnull Object label,
      @Nullable Message tooltip,
      @Nonnull String value,
      int decimals
   ) {
      String sel = "#DetailPanel[" + row + "]";
      cmd.append("#DetailPanel", decimals > 0 ? "Pages/Fields/NumberRow.ui" : "Pages/Fields/IntRow.ui");
      if (label instanceof Message m) {
         cmd.set(sel + " #Label.Text", m);
      } else {
         cmd.set(sel + " #Label.Text", label.toString());
      }

      if (tooltip != null) {
         cmd.set(sel + " #Label.TooltipText", tooltip);
      } else {
         setFieldTooltip(cmd, sel, typeId, paramKey);
      }

      try {
         cmd.set(sel + " #Input.Value", Double.parseDouble(value));
      } catch (NumberFormatException e) {
         cmd.set(sel + " #Input.Value", 0.0);
      }

      evt.addEventBinding(CustomUIEventBindingType.ValueChanged, sel + " #Input", numericParamEvent(paramKey, sel + " #Input.Value"), false);
      return row + 1;
   }

   private int addCheckboxRow(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, int row, @Nonnull String typeId, @Nonnull String key, boolean value) {
      String sel = "#DetailPanel[" + row + "]";
      cmd.append("#DetailPanel", "Pages/Fields/CheckboxRow.ui");
      cmd.set(sel + " #Label.Text", fieldLabel(typeId, key));
      setFieldTooltip(cmd, sel, typeId, key);
      cmd.set(sel + " #Input.Value", value);
      evt.addEventBinding(CustomUIEventBindingType.ValueChanged, sel + " #Input", boolParamEvent(key, sel + " #Input.Value"), false);
      return row + 1;
   }

   private int addDropdownRow(
      @Nonnull UICommandBuilder cmd,
      @Nonnull UIEventBuilder evt,
      int row,
      @Nonnull String typeId,
      @Nonnull String key,
      @Nonnull List<String> options,
      @Nonnull String value
   ) {
      String sel = "#DetailPanel[" + row + "]";
      cmd.append("#DetailPanel", "Pages/Fields/DropdownRow.ui");
      cmd.set(sel + " #Label.Text", fieldLabel(typeId, key));
      setFieldTooltip(cmd, sel, typeId, key);
      ObjectArrayList<DropdownEntryInfo> entries = new ObjectArrayList<>();

      for (String opt : options) {
         entries.add(new DropdownEntryInfo(LocalizableString.fromString(opt), opt));
      }

      cmd.set(sel + " #Input.Entries", entries);
      cmd.set(sel + " #Input.Value", value);
      evt.addEventBinding(CustomUIEventBindingType.ValueChanged, sel + " #Input", paramEvent(key, sel + " #Input.Value"), false);
      return row + 1;
   }

   private int addDropdownRow(
      @Nonnull UICommandBuilder cmd,
      @Nonnull UIEventBuilder evt,
      int row,
      @Nonnull Message label,
      @Nonnull String paramKey,
      @Nonnull List<String> options,
      @Nonnull String value
   ) {
      return this.addDropdownRow(cmd, evt, row, label, paramKey, null, options, value);
   }

   private int addDropdownRow(
      @Nonnull UICommandBuilder cmd,
      @Nonnull UIEventBuilder evt,
      int row,
      @Nonnull Message label,
      @Nonnull String paramKey,
      @Nullable Message tooltip,
      @Nonnull List<String> options,
      @Nonnull String value
   ) {
      String sel = "#DetailPanel[" + row + "]";
      cmd.append("#DetailPanel", "Pages/Fields/DropdownRow.ui");
      cmd.set(sel + " #Label.Text", label);
      if (tooltip != null) {
         cmd.set(sel + " #Label.TooltipText", tooltip);
      }

      ObjectArrayList<DropdownEntryInfo> entries = new ObjectArrayList<>();

      for (String opt : options) {
         entries.add(new DropdownEntryInfo(LocalizableString.fromString(opt), opt));
      }

      cmd.set(sel + " #Input.Entries", entries);
      cmd.set(sel + " #Input.Value", value);
      evt.addEventBinding(CustomUIEventBindingType.ValueChanged, sel + " #Input", paramEvent(paramKey, sel + " #Input.Value"), false);
      return row + 1;
   }

   private int addNumberRow(
      @Nonnull UICommandBuilder cmd,
      @Nonnull UIEventBuilder evt,
      int row,
      @Nonnull Message label,
      @Nonnull String paramKey,
      @Nonnull String value,
      int decimals
   ) {
      return this.addNumberRow(cmd, evt, row, label, paramKey, null, value, decimals);
   }

   private int addNumberRow(
      @Nonnull UICommandBuilder cmd,
      @Nonnull UIEventBuilder evt,
      int row,
      @Nonnull Message label,
      @Nonnull String paramKey,
      @Nullable Message tooltip,
      @Nonnull String value,
      int decimals
   ) {
      return this.addNumberRow(cmd, evt, row, "", paramKey, label, tooltip, value, decimals);
   }

   private int addVec3Row(
      @Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, int row, @Nonnull String typeId, @Nonnull String key, double x, double y, double z
   ) {
      String sel = "#DetailPanel[" + row + "]";
      cmd.append("#DetailPanel", "Pages/Fields/Vec3Row.ui");
      cmd.set(sel + " #Label.Text", fieldLabel(typeId, key));
      setFieldTooltip(cmd, sel, typeId, key);
      cmd.set(sel + " #X.Value", x);
      cmd.set(sel + " #Y.Value", y);
      cmd.set(sel + " #Z.Value", z);

      for (String comp : List.of("X", "Y", "Z")) {
         evt.addEventBinding(
            CustomUIEventBindingType.ValueChanged,
            sel + " #" + comp,
            new EventData()
               .append("Action", TriggerVolumeEffectEditorPage.Action.UpdateParameter.name())
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
      String sel = "#DetailPanel[" + row + "]";
      cmd.append("#DetailPanel", "Pages/Fields/AssetPickerRow.ui");
      cmd.set(sel + " #Label.Text", fieldLabel(typeId, key));
      setFieldTooltip(cmd, sel, typeId, key);
      if (value.isEmpty()) {
         cmd.set(sel + " #PickerLabel.Text", Message.translation("server.customUI.triggerVolumeEffectEditor.assetPicker.none"));
      } else {
         cmd.set(sel + " #PickerLabel.Text", value);
      }

      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         sel + " #PickerButton",
         new EventData().append("Action", TriggerVolumeEffectEditorPage.Action.OpenAssetPicker.name()).append("ParamKey", key)
      );
      return row + 1;
   }

   @Nonnull
   private static String effectListButton(@Nonnull String label, boolean selected) {
      String bg = selected ? "Background: (Color: #1a2a3d); " : "";
      return "TextButton { "
         + bg
         + "Anchor: (Height: 32); Padding: (Horizontal: 10); Style: (Default: (LabelStyle: (HorizontalAlignment: Start, VerticalAlignment: Center, FontSize: 14, RenderUppercase: false"
         + (selected ? ", RenderBold: true" : "")
         + ")), Hovered: (Background: (Color: #0a0f17))); Text: \""
         + label.replace("\"", "\\\"")
         + "\"; }";
   }

   @Nonnull
   private static EventData paramEvent(@Nonnull String key, @Nonnull String valueRef) {
      return new EventData()
         .append("Action", TriggerVolumeEffectEditorPage.Action.UpdateParameter.name())
         .append("ParamKey", key)
         .append("@ParamValue", valueRef);
   }

   @Nonnull
   private static EventData boolParamEvent(@Nonnull String key, @Nonnull String valueRef) {
      return new EventData()
         .append("Action", TriggerVolumeEffectEditorPage.Action.UpdateParameter.name())
         .append("ParamKey", key)
         .append("@ParamBool", valueRef);
   }

   @Nonnull
   private static EventData numericParamEvent(@Nonnull String key, @Nonnull String valueRef) {
      return new EventData()
         .append("Action", TriggerVolumeEffectEditorPage.Action.UpdateParameter.name())
         .append("ParamKey", key)
         .append("@ParamNumericValue", valueRef);
   }

   private void bindStaticEvents(@Nonnull UIEventBuilder evt) {
      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         "#AddEffectButton",
         new EventData().append("Action", TriggerVolumeEffectEditorPage.Action.AddEffect.name()).append("@EffectType", "#AddEffectDropdown.Value")
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating, "#RemoveEffectButton", new EventData().append("Action", TriggerVolumeEffectEditorPage.Action.RemoveEffect.name())
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating, "#SaveButton", new EventData().append("Action", TriggerVolumeEffectEditorPage.Action.SaveEffects.name())
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating, "#SavePresetButton", new EventData().append("Action", TriggerVolumeEffectEditorPage.Action.OpenPresetSave.name())
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating, "#LoadPresetButton", new EventData().append("Action", TriggerVolumeEffectEditorPage.Action.OpenPresetLoad.name())
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating, "#CancelButton", new EventData().append("Action", TriggerVolumeEffectEditorPage.Action.DiscardChanges.name())
      );
      evt.addEventBinding(
         CustomUIEventBindingType.ValueChanged,
         "#PresetName #Input",
         new EventData().append("Action", TriggerVolumeEffectEditorPage.Action.PresetNameChanged.name()).append("@PresetName", "#PresetName #Input.Value"),
         false
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         "#ConfirmSavePresetButton",
         new EventData().append("Action", TriggerVolumeEffectEditorPage.Action.ConfirmSavePreset.name()).append("@PresetName", "#PresetName #Input.Value")
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         "#CancelSavePresetButton",
         new EventData().append("Action", TriggerVolumeEffectEditorPage.Action.CancelPresetSave.name())
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         "#CancelLoadPresetButton",
         new EventData().append("Action", TriggerVolumeEffectEditorPage.Action.CancelPresetLoad.name())
      );
      evt.addEventBinding(
         CustomUIEventBindingType.ValueChanged,
         "#AssetPickerPage #SearchInput",
         new EventData()
            .append("Action", TriggerVolumeEffectEditorPage.Action.AssetPickerSearch.name())
            .append("@AssetPickerQuery", "#AssetPickerPage #SearchInput.Value"),
         false
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         "#CancelAssetPickerButton",
         new EventData().append("Action", TriggerVolumeEffectEditorPage.Action.CancelAssetPicker.name())
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating, "#EditGroupButton", new EventData().append("Action", TriggerVolumeEffectEditorPage.Action.EditGroup.name())
      );
   }

   private void onSelectEffect(@Nonnull TriggerVolumeEffectEditorPage.PageData data) {
      try {
         this.selectedEffectIndex = Integer.parseInt(data.effectIndex);
      } catch (NumberFormatException e) {
         return;
      }

      this.rebuildAll();
   }

   private void onAddEffect(@Nonnull TriggerVolumeEffectEditorPage.PageData data) {
      if (data.effectType != null && !data.effectType.isBlank()) {
         BuilderCodec<TriggerEffect> codec = getBuilderCodecFor(data.effectType);
         if (codec == null) {
            this.playerRef.sendMessage(Message.translation("server.customUI.triggerVolumeEffectEditor.unknownType"));
         } else {
            TriggerEffect newEffect = codec.getSupplier().get();
            newEffect.setEventType(TriggerEventType.ENTER);
            materializeDefaults(codec, newEffect);
            this.workingEffects.add(newEffect);
            this.selectedEffectIndex = this.workingEffects.size() - 1;
            this.rebuildAll();
         }
      }
   }

   private static void materializeDefaults(@Nonnull BuilderCodec<TriggerEffect> codec, @Nonnull TriggerEffect effect) {
      BsonDocument encoded = encodeEffect(codec, effect);
      ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();

      for (Entry<String, List<BuilderField<TriggerEffect, ?>>> entry : codec.getEntries().entrySet()) {
         String key = entry.getKey();
         if (!"Event".equals(key) && !"Interval".equals(key) && !encoded.containsKey(key)) {
            List<BuilderField<TriggerEffect, ?>> fields = entry.getValue();
            if (!fields.isEmpty()) {
               BuilderField field = fields.getLast();
               Codec childCodec = field.getCodec().getChildCodec();
               BsonValue defaultValue = getDefaultBsonValue(childCodec);
               if (defaultValue != null) {
                  BsonDocument doc = new BsonDocument();
                  doc.put(key, defaultValue);

                  try {
                     field.decode(doc, effect, extraInfo);
                  } catch (Exception var13) {
                  }
               }
            }
         }
      }
   }

   @Nullable
   private static BsonValue getDefaultBsonValue(@Nonnull Codec<?> childCodec) {
      if (childCodec == Codec.BOOLEAN) {
         return new BsonBoolean(false);
      } else if (childCodec == Codec.FLOAT) {
         return new BsonDouble(0.0);
      } else if (childCodec == Codec.INTEGER) {
         return new BsonInt32(0);
      } else if (childCodec == Codec.LONG) {
         return new BsonInt64(0L);
      } else if (childCodec == Codec.STRING) {
         return new BsonString("");
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

   private void onRemoveEffect() {
      if (this.selectedEffectIndex >= 0 && this.selectedEffectIndex < this.workingEffects.size()) {
         this.workingEffects.remove(this.selectedEffectIndex);
         if (this.selectedEffectIndex >= this.workingEffects.size()) {
            this.selectedEffectIndex = this.workingEffects.size() - 1;
         }

         this.rebuildAll();
      }
   }

   private void onUpdateParameter(@Nonnull TriggerVolumeEffectEditorPage.PageData data) {
      if (this.selectedEffectIndex >= 0 && this.selectedEffectIndex < this.workingEffects.size()) {
         if (data.paramKey != null) {
            TriggerEffect effect = this.workingEffects.get(this.selectedEffectIndex);
            String key = data.paramKey;
            if ("Event".equals(key)) {
               try {
                  effect.setEventType(TriggerEventType.valueOf(data.paramValue));
               } catch (IllegalArgumentException var12) {
               }

               this.rebuildAll();
            } else if ("Interval".equals(key)) {
               if (data.paramNumericValue != null) {
                  effect.setInterval(data.paramNumericValue.floatValue());
               }
            } else if ("Delay".equals(key)) {
               if (data.paramNumericValue != null) {
                  effect.setDelay(data.paramNumericValue.floatValue());
               }
            } else {
               String typeId = getTypeId(effect);
               BuilderCodec<TriggerEffect> codec = getBuilderCodecFor(typeId);
               if (codec != null) {
                  List<BuilderField<TriggerEffect, ?>> fieldList = codec.getEntries().get(key);
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
                        field.decode(doc, effect, extraInfo);
                     } else if (data.paramBool != null && childCodec == Codec.BOOLEAN) {
                        BsonDocument doc = new BsonDocument();
                        doc.put(key, new BsonBoolean(data.paramBool));
                        field.decode(doc, effect, extraInfo);
                     } else if (data.paramNumericValue == null) {
                        try {
                           BsonValue bsonValue = stringToBsonValue(childCodec, data.paramValue);
                           if (bsonValue != null) {
                              BsonDocument doc = new BsonDocument();
                              doc.put(key, bsonValue);
                              field.decode(doc, effect, extraInfo);
                           }
                        } catch (Exception e) {
                           LOGGER.at(Level.WARNING).log("Failed to parse value '%s' for field '%s'", data.paramValue, key, e);
                        }
                     } else {
                        BsonValue bsonValue;
                        if (childCodec == Codec.FLOAT || childCodec == Codec.DOUBLE) {
                           bsonValue = new BsonDouble(data.paramNumericValue);
                        } else if (childCodec == Codec.INTEGER) {
                           bsonValue = new BsonInt32(data.paramNumericValue.intValue());
                        } else if (childCodec == Codec.LONG) {
                           bsonValue = new BsonInt64(data.paramNumericValue.longValue());
                        } else if (childCodec == Codec.BOOLEAN) {
                           bsonValue = new BsonBoolean(data.paramNumericValue != 0.0);
                        } else {
                           bsonValue = new BsonDouble(data.paramNumericValue);
                        }

                        BsonDocument doc = new BsonDocument();
                        doc.put(key, bsonValue);
                        field.decode(doc, effect, extraInfo);
                     }
                  }
               }
            }
         }
      }
   }

   private void saveEffectsToVolumes() {
      this.commitWorkingEffects();
      this.playerRef.sendMessage(Message.translation("server.customUI.triggerVolumeEffectEditor.saved"));
   }

   private void commitWorkingEffects() {
      if (this.groupEditMode) {
         GroupEntry g = this.manager.getGroup(this.groupId);
         if (g != null) {
            g.getEffects().clear();
            g.getEffects().addAll(TriggerEffect.deepCopyList(this.workingEffects));

            for (VolumeEntry m : this.groupMembers) {
               this.manager.notifyViewersAdd(m);
            }
         }
      } else {
         this.volumeEntry.getEffects().clear();
         this.volumeEntry.getEffects().addAll(TriggerEffect.deepCopyList(this.workingEffects));
         this.volumeEntry.setEffectAssetRef(null);
         this.manager.notifyViewersAdd(this.volumeEntry);
      }
   }

   private void onEditGroup(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
      String gid = this.volumeEntry.getGroupId();
      if (gid != null) {
         this.volumeEntry.getEffects().clear();
         this.volumeEntry.getEffects().addAll(TriggerEffect.deepCopyList(this.workingEffects));
         this.volumeEntry.setEffectAssetRef(null);
         this.manager.notifyViewersAdd(this.volumeEntry);
         List<VolumeEntry> members = this.manager.getGroupMembers(gid);
         Player player = store.getComponent(ref, Player.getComponentType());
         if (player != null) {
            player.getPageManager().openCustomPage(ref, store, new TriggerVolumeEffectEditorPage(this.playerRef, this.volumeEntry, this.manager, gid, members));
         }
      }
   }

   private void applyPanelChrome(@Nonnull UICommandBuilder cmd) {
      if (this.groupEditMode) {
         cmd.set("#EditGroupButton.Visible", false);
         cmd.set(
            "#VolumeLabel.Text",
            Message.translation("server.customUI.triggerVolumeEffectEditor.volumeLabel.group")
               .param("groupId", this.groupId)
               .param("count", this.groupMembers.size())
         );
      } else if (this.readOnlyGroup != null) {
         cmd.set("#EditGroupButton.Visible", true);
         cmd.set("#VolumeLabel.Text", this.volumeEntry.getId());
      } else {
         cmd.set("#EditGroupButton.Visible", false);
         cmd.set("#VolumeLabel.Text", this.volumeEntry.getId());
      }
   }

   @Nonnull
   private static String readOnlyGroupEffectRow(@Nonnull String label) {
      return "TextButton { Anchor: (Height: 28); Padding: (Horizontal: 8); Style: (Default: (LabelStyle: (HorizontalAlignment: Start, VerticalAlignment: Center, FontSize: 13, TextColor: #5a6a7a, RenderUppercase: false))); Text: \""
         + label.replace("\"", "\\\"")
         + "\"; }";
   }

   private void onSaveEffects() {
      this.saveEffectsToVolumes();
      this.skipSaveOnDismiss = true;
      this.close();
   }

   private void onOpenPresetSave() {
      UICommandBuilder cmd = new UICommandBuilder();
      cmd.set("#MainPage.Visible", false);
      cmd.set("#PresetSavePage.Visible", true);
      cmd.set("#PresetName #Input.Value", "");
      cmd.set("#ConfirmSavePresetButton.Disabled", true);
      this.sendUpdate(cmd);
   }

   private void onPresetNameChanged(@Nonnull TriggerVolumeEffectEditorPage.PageData data) {
      UICommandBuilder cmd = new UICommandBuilder();
      cmd.set("#ConfirmSavePresetButton.Disabled", data.presetName == null || data.presetName.isBlank());
      this.sendUpdate(cmd);
   }

   private void onConfirmSavePreset(@Nonnull TriggerVolumeEffectEditorPage.PageData data) {
      if (data.presetName != null && !data.presetName.isBlank()) {
         try {
            Path assetRoot = AssetModule.get().getAssetPacks().get(0).getRoot();
            Path path = assetRoot.resolve("Server").resolve("TriggerVolumes").resolve("Effects").resolve(data.presetName + ".json");
            Files.createDirectories(path.getParent());
            TriggerEffectAsset asset = TriggerEffectAsset.create(data.presetName, this.workingEffects.toArray(TriggerEffect[]::new));
            BsonUtil.writeSync(path, TriggerEffectAsset.CODEC, asset, LOGGER);
            this.playerRef.sendMessage(Message.translation("server.customUI.triggerVolumeEffectEditor.presetSaved").param("name", data.presetName));
         } catch (Exception e) {
            LOGGER.at(Level.SEVERE).log("Failed to save effect preset '%s'", data.presetName, e);
            this.playerRef.sendMessage(Message.translation("server.customUI.triggerVolumeEffectEditor.presetSaveError").param("error", e.getMessage()));
         }

         UICommandBuilder cmd = new UICommandBuilder();
         cmd.set("#PresetSavePage.Visible", false);
         cmd.set("#MainPage.Visible", true);
         this.sendUpdate(cmd);
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
      AssetStore<String, TriggerEffectAsset, DefaultAssetMap<String, TriggerEffectAsset>> effectAssetStore = AssetRegistry.getAssetStore(
         TriggerEffectAsset.class
      );
      if (effectAssetStore != null) {
         int idx = 0;

         for (String assetId : ((DefaultAssetMap)effectAssetStore.getAssetMap()).getAssetMap().keySet()) {
            String sel = "#PresetList[" + idx + "]";
            cmd.appendInline("#PresetList", effectListButton(assetId, false));
            evt.addEventBinding(
               CustomUIEventBindingType.Activating,
               sel,
               new EventData().append("Action", TriggerVolumeEffectEditorPage.Action.LoadPreset.name()).append("PresetId", assetId)
            );
            idx++;
         }
      }

      this.sendUpdate(cmd, evt, false);
   }

   private void onLoadPreset(@Nonnull TriggerVolumeEffectEditorPage.PageData data) {
      if (data.presetId != null && !data.presetId.isBlank()) {
         AssetStore<String, TriggerEffectAsset, DefaultAssetMap<String, TriggerEffectAsset>> effectAssetStore = AssetRegistry.getAssetStore(
            TriggerEffectAsset.class
         );
         if (effectAssetStore != null) {
            TriggerEffectAsset effectAsset = (TriggerEffectAsset)((DefaultAssetMap)effectAssetStore.getAssetMap()).getAsset(data.presetId);
            if (effectAsset == null) {
               this.playerRef.sendMessage(Message.translation("server.customUI.triggerVolumeEffectEditor.presetNotFound").param("name", data.presetId));
            } else {
               this.workingEffects.clear();
               this.workingEffects.addAll(TriggerEffect.deepCopyList(Arrays.asList(effectAsset.getEffects())));
               this.selectedEffectIndex = this.workingEffects.isEmpty() ? -1 : 0;
               this.commitWorkingEffects();
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

   private void onOpenAssetPicker(@Nonnull TriggerVolumeEffectEditorPage.PageData data) {
      if (data.paramKey != null) {
         if (this.selectedEffectIndex >= 0 && this.selectedEffectIndex < this.workingEffects.size()) {
            TriggerEffect effect = this.workingEffects.get(this.selectedEffectIndex);
            String typeId = getTypeId(effect);
            String source = getAssetSourceForField(typeId, data.paramKey);
            if (source != null) {
               this.pendingPickerFieldKey = data.paramKey;
               this.pendingPickerSource = source;
               this.assetPickerSearchQuery = "";
               UICommandBuilder cmd = new UICommandBuilder();
               UIEventBuilder evt = new UIEventBuilder();
               cmd.set("#MainPage.Visible", false);
               cmd.set("#AssetPickerPage.Visible", true);
               cmd.set("#AssetPickerFieldLabel.Text", data.paramKey);
               this.buildAssetPickerList(cmd, evt);
               this.bindStaticEvents(evt);
               this.sendUpdate(cmd, evt, false);
            }
         }
      }
   }

   private void onAssetPickerSearch(@Nonnull TriggerVolumeEffectEditorPage.PageData data) {
      if (data.assetPickerQuery != null) {
         this.assetPickerSearchQuery = data.assetPickerQuery.trim().toLowerCase(Locale.ROOT);
      }

      UICommandBuilder cmd = new UICommandBuilder();
      UIEventBuilder evt = new UIEventBuilder();
      this.buildAssetPickerList(cmd, evt);
      this.bindStaticEvents(evt);
      this.sendUpdate(cmd, evt, false);
   }

   private void onAssetPickerSelect(@Nonnull TriggerVolumeEffectEditorPage.PageData data) {
      if (data.assetPickerSelection != null && this.pendingPickerFieldKey != null) {
         if (this.selectedEffectIndex >= 0 && this.selectedEffectIndex < this.workingEffects.size()) {
            TriggerEffect effect = this.workingEffects.get(this.selectedEffectIndex);
            String typeId = getTypeId(effect);
            BuilderCodec<TriggerEffect> codec = getBuilderCodecFor(typeId);
            if (codec != null) {
               this.applyPickerValue(codec, effect, this.pendingPickerFieldKey, data.assetPickerSelection.isEmpty() ? null : data.assetPickerSelection);
            }
         }

         this.pendingPickerFieldKey = null;
         this.pendingPickerSource = null;
         this.assetPickerSearchQuery = "";
         UICommandBuilder cmd = new UICommandBuilder();
         cmd.set("#AssetPickerPage.Visible", false);
         cmd.set("#MainPage.Visible", true);
         this.sendUpdate(cmd);
         this.rebuildAll();
      }
   }

   private void onCancelAssetPicker() {
      this.pendingPickerFieldKey = null;
      this.pendingPickerSource = null;
      this.assetPickerSearchQuery = "";
      UICommandBuilder cmd = new UICommandBuilder();
      cmd.set("#AssetPickerPage.Visible", false);
      cmd.set("#MainPage.Visible", true);
      this.sendUpdate(cmd);
   }

   private void applyPickerValue(@Nonnull BuilderCodec<TriggerEffect> codec, @Nonnull TriggerEffect effect, @Nonnull String fieldKey, @Nullable String value) {
      List<BuilderField<TriggerEffect, ?>> fieldList = codec.getEntries().get(fieldKey);
      if (fieldList != null && !fieldList.isEmpty()) {
         BuilderField field = fieldList.getLast();
         ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
         BsonDocument doc = new BsonDocument();
         if (value != null) {
            doc.put(fieldKey, new BsonString(value));
         }

         try {
            field.decode(doc, effect, extraInfo);
         } catch (Exception e) {
            LOGGER.at(Level.WARNING).log("Failed to apply picker value '%s' for field '%s'", value, fieldKey, e);
         }
      }
   }

   private void buildAssetPickerList(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt) {
      cmd.clear("#AssetPickerList");
      Collection<String> allIds = getAssetIdsForSource(this.pendingPickerSource);
      List<String> filtered;
      if (!this.assetPickerSearchQuery.isEmpty()) {
         Object2IntMap<String> scored = new Object2IntOpenHashMap<>(allIds.size());
         String queryLower = this.assetPickerSearchQuery.toLowerCase(Locale.ROOT);

         for (String id : allIds) {
            if (id.toLowerCase(Locale.ROOT).contains(queryLower)) {
               int distance = StringCompareUtil.getFuzzyDistance(id, this.assetPickerSearchQuery, Locale.ROOT);
               scored.put(id, distance);
            }
         }

         filtered = scored.keySet().stream().sorted().sorted(Comparator.comparingInt(scored::getInt).reversed()).limit(50L).toList();
      } else {
         filtered = allIds.stream().sorted().limit(50L).toList();
      }

      cmd.set("#AssetPickerNoResults.Visible", filtered.isEmpty());
      cmd.append("#AssetPickerList", "Common/TextButton.ui");
      cmd.set("#AssetPickerList[0] #Button.Text", Message.translation("server.customUI.triggerVolumeEffectEditor.assetPicker.clearEntry"));
      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         "#AssetPickerList[0] #Button",
         new EventData().append("Action", TriggerVolumeEffectEditorPage.Action.AssetPickerSelect.name()).append("AssetPickerSelection", "")
      );

      for (int i = 0; i < filtered.size(); i++) {
         String id = filtered.get(i);
         String sel = "#AssetPickerList[" + (i + 1) + "]";
         cmd.append("#AssetPickerList", "Common/TextButton.ui");
         cmd.set(sel + " #Button.Text", id);
         evt.addEventBinding(
            CustomUIEventBindingType.Activating,
            sel + " #Button",
            new EventData().append("Action", TriggerVolumeEffectEditorPage.Action.AssetPickerSelect.name()).append("AssetPickerSelection", id)
         );
      }
   }

   @Nonnull
   private static Collection<String> getAssetIdsForSource(@Nullable String sourceId) {
      return sourceId == null ? List.of() : TriggerVolumesPlugin.get().getAssetIds(sourceId);
   }

   @Nullable
   private static String getAssetSourceForField(@Nonnull String typeId, @Nonnull String fieldKey) {
      return TriggerVolumesPlugin.get().getAssetSourceForField(typeId, fieldKey);
   }

   private void rebuildAll() {
      UICommandBuilder cmd = new UICommandBuilder();
      UIEventBuilder evt = new UIEventBuilder();
      this.applyPanelChrome(cmd);
      this.buildEffectList(cmd, evt);
      this.buildDetailPanel(cmd, evt);
      this.bindStaticEvents(evt);
      this.sendUpdate(cmd, evt, false);
   }

   @Nonnull
   private static List<String> getSortedTypeIds() {
      ArrayList<String> ids = new ArrayList<>(TriggerEffect.CODEC.getRegisteredIds());
      Collections.sort(ids);
      return ids;
   }

   @Nonnull
   private static String getTypeId(@Nonnull TriggerEffect effect) {
      String id = TriggerEffect.CODEC.getIdFor((Class<? extends TriggerEffect>)effect.getClass());
      return id != null ? id : "unknown";
   }

   @Nullable
   private static BuilderCodec<TriggerEffect> getBuilderCodecFor(@Nonnull String typeId) {
      return (BuilderCodec<TriggerEffect>)(TriggerEffect.CODEC.getCodecFor(typeId) instanceof BuilderCodec<?> bc ? bc : null);
   }

   @Nonnull
   private static BsonDocument encodeEffect(@Nonnull BuilderCodec<TriggerEffect> codec, @Nonnull TriggerEffect effect) {
      try {
         return codec.encode(effect, EmptyExtraInfo.EMPTY);
      } catch (Exception e) {
         return new BsonDocument();
      }
   }

   @Nullable
   private static BsonValue stringToBsonValue(@Nonnull Codec<?> childCodec, @Nullable String value) {
      if (value == null) {
         return null;
      } else if (childCodec == Codec.STRING) {
         return new BsonString(value);
      } else if (childCodec == Codec.FLOAT) {
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
   private static String bsonValueToString(@Nonnull BsonValue value) {
      if (value instanceof BsonString bs) {
         return bs.getValue();
      } else if (value instanceof BsonBoolean bb) {
         return String.valueOf(bb.getValue());
      } else if (value instanceof BsonDouble bd) {
         return String.valueOf(bd.getValue());
      } else if (value instanceof BsonInt32 bi) {
         return String.valueOf(bi.getValue());
      } else {
         return value instanceof BsonInt64 bl ? String.valueOf(bl.getValue()) : value.toString();
      }
   }

   public enum Action {
      SelectEffect,
      AddEffect,
      RemoveEffect,
      UpdateParameter,
      SaveEffects,
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
      CancelAssetPicker,
      EditGroup,
      DiscardChanges;
   }

   public static class PageData {
      public static final BuilderCodec<TriggerVolumeEffectEditorPage.PageData> CODEC = BuilderCodec.builder(
            TriggerVolumeEffectEditorPage.PageData.class, TriggerVolumeEffectEditorPage.PageData::new
         )
         .append(
            new KeyedCodec<>("Action", new EnumCodec<>(TriggerVolumeEffectEditorPage.Action.class, EnumCodec.EnumStyle.LEGACY)),
            (o, v) -> o.action = v,
            o -> o.action
         )
         .add()
         .append(new KeyedCodec<>("EffectIndex", Codec.STRING, false), (o, v) -> o.effectIndex = v, o -> o.effectIndex)
         .add()
         .append(new KeyedCodec<>("@EffectType", Codec.STRING, false), (o, v) -> o.effectType = v, o -> o.effectType)
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
         .build();
      public TriggerVolumeEffectEditorPage.Action action;
      public String effectIndex;
      public String effectType;
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
   }
}
