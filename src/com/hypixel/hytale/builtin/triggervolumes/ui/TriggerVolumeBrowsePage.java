package com.hypixel.hytale.builtin.triggervolumes.ui;

import com.hypixel.hytale.builtin.triggervolumes.TriggerVolumesPlugin;
import com.hypixel.hytale.builtin.triggervolumes.manager.CooldownMode;
import com.hypixel.hytale.builtin.triggervolumes.manager.GroupEntry;
import com.hypixel.hytale.builtin.triggervolumes.manager.TriggerVolumeManager;
import com.hypixel.hytale.builtin.triggervolumes.manager.VolumeEntry;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.player.TriggerVolumeDisplayEntry;
import com.hypixel.hytale.protocol.packets.player.TriggerVolumeShapeType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.DropdownEntryInfo;
import com.hypixel.hytale.server.core.ui.LocalizableString;
import com.hypixel.hytale.server.core.ui.PatchStyle;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TriggerVolumeBrowsePage extends InteractiveCustomUIPage<TriggerVolumeBrowsePage.PageData> {
   private static final Pattern VALID_ID = Pattern.compile("^[a-zA-Z0-9_]{1,64}$");
   private static final Value<String> NORMAL_ROW_STYLE = Value.ref("Pages/TriggerVolume/TriggerVolumeBrowseGroupRow.ui", "NormalRowStyle");
   private static final Value<String> SELECTED_ROW_STYLE = Value.ref("Pages/TriggerVolume/TriggerVolumeBrowseGroupRow.ui", "SelectedRowStyle");
   private static final String GROUP_ROW = "Pages/TriggerVolume/TriggerVolumeBrowseGroupRow.ui";
   private static final String VOLUME_ROW = "Pages/TriggerVolume/TriggerVolumeBrowseVolumeRow.ui";
   private static final String PROPERTY_ROW = "Pages/TriggerVolume/TriggerVolumeBrowsePropertyRow.ui";
   private static final String TAG_ROW = "Pages/TriggerVolume/TriggerVolumeBrowseTagRow.ui";
   @Nonnull
   private String selectedWorld;
   @Nullable
   private String selectedId;
   private boolean selectedIsGroup;
   private final List<TriggerVolumeBrowsePage.RowEntry> currentRows = new ArrayList<>();
   @Nullable
   private final String preSelectedVolumeId;

   public TriggerVolumeBrowsePage(@Nonnull PlayerRef playerRef, @Nonnull String selectedWorld) {
      this(playerRef, selectedWorld, null);
   }

   public TriggerVolumeBrowsePage(@Nonnull PlayerRef playerRef, @Nonnull String selectedWorld, @Nullable String preSelectedVolumeId) {
      super(playerRef, CustomPageLifetime.CanDismiss, TriggerVolumeBrowsePage.PageData.CODEC);
      this.selectedWorld = selectedWorld;
      this.preSelectedVolumeId = preSelectedVolumeId;
   }

   @Override
   public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, @Nonnull Store<EntityStore> store) {
      cmd.append("Pages/TriggerVolume/TriggerVolumeBrowsePage.ui");
      this.buildWorldDropdown(cmd);
      this.buildList(cmd, evt);
      if (this.preSelectedVolumeId != null) {
         this.applyPreSelection(cmd, evt);
      }

      this.bindStaticEvents(evt);
   }

   public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull TriggerVolumeBrowsePage.PageData data) {
      if (data.action != null) {
         switch (data.action) {
            case Select:
               this.onSelect(data);
               break;
            case ChangeWorld:
               this.onChangeWorld(data);
               break;
            case Rename:
               this.onRename(data);
               break;
            case Delete:
               this.onDelete();
               break;
            case AddTag:
               this.onAddTag(data);
               break;
            case RemoveTag:
               this.onRemoveTag(data);
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
   }

   private void buildList(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt) {
      cmd.clear("#ListContainer");
      this.currentRows.clear();
      this.clearSelection(cmd);
      TriggerVolumeManager manager = getManagerForWorld(this.selectedWorld);
      if (manager != null && (!manager.getVolumesMap().isEmpty() || !manager.getGroupsMap().isEmpty())) {
         Map<String, GroupEntry> groups = manager.getGroupsMap();
         Map<String, VolumeEntry> volumes = manager.getVolumesMap();
         LinkedHashMap<String, List<VolumeEntry>> groupedVolumes = new LinkedHashMap<>();

         for (GroupEntry group : groups.values()) {
            groupedVolumes.put(group.getId(), new ArrayList<>());
         }

         ArrayList<VolumeEntry> ungrouped = new ArrayList<>();

         for (VolumeEntry vol : volumes.values()) {
            if (vol.getGroupId() != null && groupedVolumes.containsKey(vol.getGroupId())) {
               groupedVolumes.get(vol.getGroupId()).add(vol);
            } else {
               ungrouped.add(vol);
            }
         }

         int idx = 0;

         for (GroupEntry group : groups.values()) {
            String sel = "#ListContainer[" + idx + "]";
            cmd.append("#ListContainer", "Pages/TriggerVolume/TriggerVolumeBrowseGroupRow.ui");
            cmd.set(sel + " #Label.Text", group.getId());
            cmd.setObject(sel + " #ColorSwatch.Background", colorPatch(group.getColor()));
            evt.addEventBinding(
               CustomUIEventBindingType.Activating,
               sel,
               new EventData().append("Action", TriggerVolumeBrowsePage.Action.Select.name()).append("Id", group.getId()).append("IsGroup", "true"),
               false
            );
            this.currentRows.add(new TriggerVolumeBrowsePage.RowEntry(group.getId(), true, idx));
            idx++;

            for (VolumeEntry vol : groupedVolumes.get(group.getId())) {
               String vSel = "#ListContainer[" + idx + "]";
               cmd.append("#ListContainer", "Pages/TriggerVolume/TriggerVolumeBrowseVolumeRow.ui");
               cmd.set(vSel + " #Label.Text", vol.getId());
               cmd.set(vSel + " #Indent.Visible", true);
               cmd.set(vSel + " #ColorBar.Visible", true);
               cmd.setObject(vSel + " #ColorBar.Background", colorPatch(group.getColor()));
               evt.addEventBinding(
                  CustomUIEventBindingType.Activating,
                  vSel,
                  new EventData().append("Action", TriggerVolumeBrowsePage.Action.Select.name()).append("Id", vol.getId()).append("IsGroup", "false"),
                  false
               );
               this.currentRows.add(new TriggerVolumeBrowsePage.RowEntry(vol.getId(), false, idx));
               idx++;
            }
         }

         if (!ungrouped.isEmpty()) {
            cmd.appendInline(
               "#ListContainer",
               "Label { Style: (FontSize: 14, RenderUppercase: true, RenderBold: true, TextColor: #7a8a9a, VerticalAlignment: Center); Anchor: (Top: 8, Bottom: 4); }"
            );
            cmd.set("#ListContainer[" + idx + "].Text", Message.translation("server.customUI.triggerVolumeBrowse.ungrouped"));
            idx++;

            for (VolumeEntry vol : ungrouped) {
               String vSel = "#ListContainer[" + idx + "]";
               cmd.append("#ListContainer", "Pages/TriggerVolume/TriggerVolumeBrowseVolumeRow.ui");
               cmd.set(vSel + " #Label.Text", vol.getId());
               evt.addEventBinding(
                  CustomUIEventBindingType.Activating,
                  vSel,
                  new EventData().append("Action", TriggerVolumeBrowsePage.Action.Select.name()).append("Id", vol.getId()).append("IsGroup", "false"),
                  false
               );
               this.currentRows.add(new TriggerVolumeBrowsePage.RowEntry(vol.getId(), false, idx));
               idx++;
            }
         }
      } else {
         cmd.appendInline("#ListContainer", "Label { Style: (FontSize: 14, TextColor: #5a6a7a, HorizontalAlignment: Center); Anchor: (Top: 20); }");
         cmd.set("#ListContainer[0].Text", Message.translation("server.customUI.triggerVolumeBrowse.emptyState"));
      }
   }

   private void applyPreSelection(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt) {
      for (TriggerVolumeBrowsePage.RowEntry row : this.currentRows) {
         if (!row.isGroup && row.id.equals(this.preSelectedVolumeId)) {
            this.selectedId = row.id;
            this.selectedIsGroup = false;
            this.updateRowHighlight(cmd, this.selectedId, false, true);
            this.buildDetail(cmd, evt);
            return;
         }
      }
   }

   private void buildDetail(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt) {
      cmd.clear("#DetailProperties");
      cmd.clear("#TagsList");
      if (this.selectedId == null) {
         this.clearSelection(cmd);
      } else {
         TriggerVolumeManager manager = getManagerForWorld(this.selectedWorld);
         if (manager == null) {
            this.clearSelection(cmd);
         } else {
            cmd.set("#DetailContent.Visible", true);
            cmd.set("#DetailFooter.Visible", true);
            int propIdx = 0;
            if (this.selectedIsGroup) {
               GroupEntry group = manager.getGroup(this.selectedId);
               if (group == null) {
                  this.clearSelection(cmd);
                  return;
               }

               propIdx = this.addProperty(cmd, propIdx, "server.customUI.triggerVolumeBrowse.details.id", group.getId());
               propIdx = this.addProperty(cmd, propIdx, "server.customUI.triggerVolumeBrowse.details.group", colorToHex(group.getColor()));
               propIdx = this.addProperty(
                  cmd, propIdx, "server.customUI.triggerVolumeBrowse.details.members", String.valueOf(group.getMemberVolumeIds().size())
               );
               this.addProperty(cmd, propIdx, "server.customUI.triggerVolumeBrowse.details.enabled", this.yesNo(group.isEnabled()));
               cmd.set("#TagsSection.Visible", true);
               this.buildTags(cmd, evt, group.getRawTags());
               cmd.set("#RenameField.Value", group.getId());
            } else {
               VolumeEntry vol = manager.getVolume(this.selectedId);
               if (vol == null) {
                  this.clearSelection(cmd);
                  return;
               }

               TriggerVolumeDisplayEntry display = manager.buildDisplayEntry(vol);
               propIdx = this.addProperty(cmd, propIdx, "server.customUI.triggerVolumeBrowse.details.id", vol.getId());
               propIdx = this.addProperty(cmd, propIdx, "server.customUI.triggerVolumeBrowse.details.shape", shapeName(display.shapeType));
               propIdx = this.addProperty(
                  cmd,
                  propIdx,
                  "server.customUI.triggerVolumeBrowse.details.position",
                  String.format("%.1f, %.1f, %.1f", display.position.x(), display.position.y(), display.position.z())
               );

               String dims = switch (display.shapeType) {
                  case Box -> String.format("%.1f x %.1f x %.1f", display.dimensions.x() * 2.0F, display.dimensions.y() * 2.0F, display.dimensions.z() * 2.0F);
                  case Sphere -> String.format("r=%.1f", display.dimensions.x());
                  case Cylinder -> String.format("r=%.1f, h=%.1f", display.dimensions.x(), display.dimensions.y());
               };
               propIdx = this.addProperty(cmd, propIdx, "server.customUI.triggerVolumeBrowse.details.dimensions", dims);
               propIdx = this.addProperty(cmd, propIdx, "server.customUI.triggerVolumeBrowse.details.targetTypes", targetTypesLabel(display.targetTypes));
               if (vol.getEffectAssetRef() != null) {
                  propIdx = this.addProperty(cmd, propIdx, "server.customUI.triggerVolumeBrowse.details.effects", vol.getEffectAssetRef());
               }

               if (vol.getGroupId() != null) {
                  propIdx = this.addProperty(cmd, propIdx, "server.customUI.triggerVolumeBrowse.details.group", vol.getGroupId());
               }

               propIdx = this.addProperty(cmd, propIdx, "server.customUI.triggerVolumeBrowse.details.enabled", this.yesNo(vol.isEnabled()));
               propIdx = this.addProperty(cmd, propIdx, "server.customUI.triggerVolumeBrowse.details.keepLoaded", this.yesNo(vol.isKeepLoaded()));
               if (vol.getActivationDelay() > 0.0F) {
                  propIdx = this.addProperty(
                     cmd, propIdx, "server.customUI.triggerVolumeBrowse.details.activationDelay", String.format("%.1fs", vol.getActivationDelay())
                  );
               }

               if (vol.getCooldown() > 0.0F) {
                  String modeLabel = vol.getCooldownMode() == CooldownMode.TOTAL ? "Total" : "Per Player";
                  propIdx = this.addProperty(
                     cmd, propIdx, "server.customUI.triggerVolumeBrowse.details.cooldown", String.format("%.1fs (%s)", vol.getCooldown(), modeLabel)
                  );
               }

               cmd.set("#TagsSection.Visible", true);
               this.buildTags(cmd, evt, vol.getRawTags());
               cmd.set("#RenameField.Value", vol.getId());
            }
         }
      }
   }

   private int addProperty(@Nonnull UICommandBuilder cmd, int idx, @Nonnull String labelKey, @Nonnull String value) {
      String sel = "#DetailProperties[" + idx + "]";
      cmd.append("#DetailProperties", "Pages/TriggerVolume/TriggerVolumeBrowsePropertyRow.ui");
      cmd.set(sel + " #Key.Text", Message.translation(labelKey));
      cmd.set(sel + " #Value.Text", value);
      return idx + 1;
   }

   private void buildTags(@Nonnull UICommandBuilder cmd, @Nonnull UIEventBuilder evt, @Nonnull Map<String, String[]> tags) {
      int idx = 0;

      for (Entry<String, String[]> entry : tags.entrySet()) {
         String sel = "#TagsList[" + idx + "]";
         cmd.append("#TagsList", "Pages/TriggerVolume/TriggerVolumeBrowseTagRow.ui");
         cmd.set(sel + " #TagLabel.Text", entry.getKey() + ": " + String.join(", ", entry.getValue()));
         evt.addEventBinding(
            CustomUIEventBindingType.Activating,
            sel + " #RemoveButton",
            new EventData().append("Action", TriggerVolumeBrowsePage.Action.RemoveTag.name()).append("RemoveTagKey", entry.getKey())
         );
         idx++;
      }
   }

   private void bindStaticEvents(@Nonnull UIEventBuilder evt) {
      evt.addEventBinding(
         CustomUIEventBindingType.ValueChanged,
         "#WorldDropdown",
         new EventData().append("Action", TriggerVolumeBrowsePage.Action.ChangeWorld.name()).append("@WorldName", "#WorldDropdown.Value"),
         false
      );
      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         "#RenameButton",
         new EventData().append("Action", TriggerVolumeBrowsePage.Action.Rename.name()).append("@RenameValue", "#RenameField.Value")
      );
      evt.addEventBinding(CustomUIEventBindingType.Activating, "#DeleteButton", new EventData().append("Action", TriggerVolumeBrowsePage.Action.Delete.name()));
      evt.addEventBinding(
         CustomUIEventBindingType.Activating,
         "#AddTagButton",
         new EventData()
            .append("Action", TriggerVolumeBrowsePage.Action.AddTag.name())
            .append("@TagKey", "#TagKeyField.Value")
            .append("@TagValues", "#TagValuesField.Value")
      );
   }

   private void onSelect(@Nonnull TriggerVolumeBrowsePage.PageData data) {
      if (data.id != null) {
         boolean isGroup = "true".equals(data.isGroup);
         UICommandBuilder cmd = new UICommandBuilder();
         UIEventBuilder evt = new UIEventBuilder();
         this.updateRowHighlight(cmd, this.selectedId, this.selectedIsGroup, false);
         this.selectedId = data.id;
         this.selectedIsGroup = isGroup;
         this.updateRowHighlight(cmd, this.selectedId, this.selectedIsGroup, true);
         this.buildDetail(cmd, evt);
         this.sendUpdate(cmd, evt, false);
      }
   }

   private void onChangeWorld(@Nonnull TriggerVolumeBrowsePage.PageData data) {
      if (data.worldName != null && !data.worldName.equals(this.selectedWorld)) {
         this.selectedWorld = data.worldName;
         UICommandBuilder cmd = new UICommandBuilder();
         UIEventBuilder evt = new UIEventBuilder();
         this.buildList(cmd, evt);
         this.bindStaticEvents(evt);
         this.sendUpdate(cmd, evt, false);
      }
   }

   private void onRename(@Nonnull TriggerVolumeBrowsePage.PageData data) {
      if (this.selectedId != null && data.renameValue != null) {
         String newId = data.renameValue.trim();
         if (!newId.isEmpty() && !newId.equals(this.selectedId)) {
            if (VALID_ID.matcher(newId).matches()) {
               TriggerVolumeManager manager = getManagerForWorld(this.selectedWorld);
               if (manager != null) {
                  if (this.selectedIsGroup) {
                     GroupEntry group = manager.getGroup(this.selectedId);
                     if (group == null || manager.hasGroup(newId)) {
                        return;
                     }

                     manager.unregisterGroup(this.selectedId);
                     group.setId(newId);
                     manager.registerGroup(newId, group);

                     for (String memberId : group.getMemberVolumeIds()) {
                        VolumeEntry vol = manager.getVolume(memberId);
                        if (vol != null) {
                           vol.setGroupId(newId);
                           manager.notifyViewersAdd(vol);
                        }
                     }
                  } else {
                     VolumeEntry vol = manager.getVolume(this.selectedId);
                     if (vol == null || manager.hasVolume(newId)) {
                        return;
                     }

                     manager.unregister(this.selectedId);
                     manager.notifyViewersRemove(this.selectedId);
                     vol.setId(newId);
                     manager.register(newId, vol);
                     manager.notifyViewersAdd(vol);
                     if (vol.getGroupId() != null) {
                        GroupEntry group = manager.getGroup(vol.getGroupId());
                        if (group != null) {
                           group.removeMember(this.selectedId);
                           group.addMember(newId);
                        }
                     }
                  }

                  this.selectedId = newId;
                  this.rebuildAll();
               }
            }
         }
      }
   }

   private void onDelete() {
      if (this.selectedId != null) {
         TriggerVolumeManager manager = getManagerForWorld(this.selectedWorld);
         if (manager != null) {
            if (this.selectedIsGroup) {
               GroupEntry group = manager.getGroup(this.selectedId);
               if (group == null) {
                  return;
               }

               for (String memberId : new ArrayList<>(group.getMemberVolumeIds())) {
                  VolumeEntry vol = manager.getVolume(memberId);
                  if (vol != null) {
                     vol.setGroupId(null);
                     manager.notifyViewersAdd(vol);
                  }
               }

               manager.unregisterGroup(this.selectedId);
            } else {
               VolumeEntry vol = manager.getVolume(this.selectedId);
               if (vol == null) {
                  return;
               }

               if (vol.getGroupId() != null) {
                  GroupEntry group = manager.getGroup(vol.getGroupId());
                  if (group != null) {
                     group.removeMember(this.selectedId);
                     if (group.getMemberVolumeIds().isEmpty()) {
                        manager.unregisterGroup(group.getId());
                     }
                  }
               }

               manager.unregister(this.selectedId);
               manager.notifyViewersRemove(this.selectedId);
            }

            this.selectedId = null;
            this.selectedIsGroup = false;
            this.rebuildAll();
         }
      }
   }

   private void onAddTag(@Nonnull TriggerVolumeBrowsePage.PageData data) {
      if (this.selectedId != null && data.tagKey != null) {
         String key = data.tagKey.trim();
         if (!key.isEmpty()) {
            TriggerVolumeManager manager = getManagerForWorld(this.selectedWorld);
            if (manager != null) {
               String[] values;
               if (data.tagValues != null && !data.tagValues.isBlank()) {
                  values = Arrays.stream(data.tagValues.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
               } else {
                  values = new String[0];
               }

               if (this.selectedIsGroup) {
                  GroupEntry group = manager.getGroup(this.selectedId);
                  if (group == null) {
                     return;
                  }

                  HashMap<String, String[]> tags = new HashMap<>(group.getRawTags());
                  tags.put(key, values);
                  group.setTags(tags);
               } else {
                  VolumeEntry vol = manager.getVolume(this.selectedId);
                  if (vol == null) {
                     return;
                  }

                  HashMap<String, String[]> tags = new HashMap<>(vol.getRawTags());
                  tags.put(key, values);
                  vol.setTags(tags);
               }

               UICommandBuilder cmd = new UICommandBuilder();
               UIEventBuilder evt = new UIEventBuilder();
               this.buildDetail(cmd, evt);
               cmd.set("#TagKeyField.Value", "");
               cmd.set("#TagValuesField.Value", "");
               this.sendUpdate(cmd, evt, false);
            }
         }
      }
   }

   private void onRemoveTag(@Nonnull TriggerVolumeBrowsePage.PageData data) {
      if (this.selectedId != null && data.removeTagKey != null) {
         TriggerVolumeManager manager = getManagerForWorld(this.selectedWorld);
         if (manager != null) {
            if (this.selectedIsGroup) {
               GroupEntry group = manager.getGroup(this.selectedId);
               if (group == null) {
                  return;
               }

               HashMap<String, String[]> tags = new HashMap<>(group.getRawTags());
               tags.remove(data.removeTagKey);
               group.setTags(tags);
            } else {
               VolumeEntry vol = manager.getVolume(this.selectedId);
               if (vol == null) {
                  return;
               }

               HashMap<String, String[]> tags = new HashMap<>(vol.getRawTags());
               tags.remove(data.removeTagKey);
               vol.setTags(tags);
            }

            UICommandBuilder cmd = new UICommandBuilder();
            UIEventBuilder evt = new UIEventBuilder();
            this.buildDetail(cmd, evt);
            this.sendUpdate(cmd, evt, false);
         }
      }
   }

   private void rebuildAll() {
      UICommandBuilder cmd = new UICommandBuilder();
      UIEventBuilder evt = new UIEventBuilder();
      this.buildList(cmd, evt);
      if (this.selectedId != null) {
         this.updateRowHighlight(cmd, this.selectedId, this.selectedIsGroup, true);
         this.buildDetail(cmd, evt);
      }

      this.bindStaticEvents(evt);
      this.sendUpdate(cmd, evt, false);
   }

   private void clearSelection(@Nonnull UICommandBuilder cmd) {
      this.selectedId = null;
      this.selectedIsGroup = false;
      cmd.set("#DetailContent.Visible", false);
      cmd.set("#DetailFooter.Visible", false);
      cmd.set("#TagsSection.Visible", false);
   }

   private void updateRowHighlight(@Nonnull UICommandBuilder cmd, @Nullable String id, boolean isGroup, boolean selected) {
      if (id != null) {
         for (int i = 0; i < this.currentRows.size(); i++) {
            TriggerVolumeBrowsePage.RowEntry row = this.currentRows.get(i);
            if (row.id.equals(id) && row.isGroup == isGroup) {
               cmd.set("#ListContainer[" + row.listIndex + "].Style", selected ? SELECTED_ROW_STYLE : NORMAL_ROW_STYLE);
               return;
            }
         }
      }
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
   private static String colorToHex(int rgb) {
      int r = rgb >> 16 & 0xFF;
      int g = rgb >> 8 & 0xFF;
      int b = rgb & 0xFF;
      return String.format("#%02X%02X%02X", r, g, b);
   }

   @Nonnull
   private String yesNo(boolean value) {
      return value ? "Yes" : "No";
   }

   @Nonnull
   private static String shapeName(@Nonnull TriggerVolumeShapeType type) {
      return switch (type) {
         case Box -> "Box";
         case Sphere -> "Sphere";
         case Cylinder -> "Cylinder";
      };
   }

   @Nonnull
   private static String targetTypesLabel(byte bits) {
      return switch (bits & 3) {
         case 1 -> "Player";
         case 2 -> "NPC";
         case 3 -> "All";
         default -> "None";
      };
   }

   public enum Action {
      Select,
      ChangeWorld,
      Rename,
      Delete,
      AddTag,
      RemoveTag;
   }

   public static class PageData {
      public static final BuilderCodec<TriggerVolumeBrowsePage.PageData> CODEC = BuilderCodec.builder(
            TriggerVolumeBrowsePage.PageData.class, TriggerVolumeBrowsePage.PageData::new
         )
         .append(
            new KeyedCodec<>("Action", new EnumCodec<>(TriggerVolumeBrowsePage.Action.class, EnumCodec.EnumStyle.LEGACY)),
            (o, v) -> o.action = v,
            o -> o.action
         )
         .add()
         .append(new KeyedCodec<>("Id", Codec.STRING, false), (o, v) -> o.id = v, o -> o.id)
         .add()
         .append(new KeyedCodec<>("IsGroup", Codec.STRING, false), (o, v) -> o.isGroup = v, o -> o.isGroup)
         .add()
         .append(new KeyedCodec<>("@RenameValue", Codec.STRING, false), (o, v) -> o.renameValue = v, o -> o.renameValue)
         .add()
         .append(new KeyedCodec<>("@TagKey", Codec.STRING, false), (o, v) -> o.tagKey = v, o -> o.tagKey)
         .add()
         .append(new KeyedCodec<>("@TagValues", Codec.STRING, false), (o, v) -> o.tagValues = v, o -> o.tagValues)
         .add()
         .append(new KeyedCodec<>("RemoveTagKey", Codec.STRING, false), (o, v) -> o.removeTagKey = v, o -> o.removeTagKey)
         .add()
         .append(new KeyedCodec<>("@WorldName", Codec.STRING, false), (o, v) -> o.worldName = v, o -> o.worldName)
         .add()
         .build();
      public TriggerVolumeBrowsePage.Action action;
      public String id;
      public String isGroup;
      public String renameValue;
      public String tagKey;
      public String tagValues;
      public String removeTagKey;
      public String worldName;
   }

   private record RowEntry(@Nonnull String id, boolean isGroup, int listIndex) {
   }
}
