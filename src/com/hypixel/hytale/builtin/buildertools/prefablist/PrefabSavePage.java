package com.hypixel.hytale.builtin.buildertools.prefablist;

import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
import com.hypixel.hytale.builtin.buildertools.BuilderToolsUserData;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.saving.SupportMode;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.common.util.PathUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.singleplayer.SingleplayerModule;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.ui.browser.AssetPackSaveBrowser;
import com.hypixel.hytale.server.core.ui.browser.AssetPackSaveBrowserConfig;
import com.hypixel.hytale.server.core.ui.browser.AssetPackSaveBrowserEventData;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class PrefabSavePage extends InteractiveCustomUIPage<PrefabSavePage.PageData> {
   @Nonnull
   private static final Message MESSAGE_NAME_REQUIRED = Message.translation("server.customUI.prefabSavePage.required");
   @Nonnull
   private static final Message MESSAGE_NAME_INVALID = Message.translation("server.customUI.prefabSavePage.invalid");
   @Nonnull
   private static final Message MESSAGE_PACK_REQUIRED = Message.translation("server.customUI.assetPackBrowser.packRequired");
   private final AssetPackSaveBrowser packBrowser = new AssetPackSaveBrowser(AssetPackSaveBrowserConfig.defaults());
   private boolean initialized = false;
   @Nullable
   private PrefabSavePage.PendingSave pendingSave;

   public PrefabSavePage(@Nonnull PlayerRef playerRef) {
      super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, PrefabSavePage.PageData.CODEC);
   }

   @Override
   public void build(
      @Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> store
   ) {
      if (!this.initialized) {
         this.initialized = true;
         Player playerComponent = store.getComponent(ref, Player.getComponentType());
         if (playerComponent != null) {
            String lastPack = BuilderToolsUserData.get(playerComponent).getLastSavePack();
            this.packBrowser.setSelectedPackKey(lastPack);
            if (lastPack != null && !this.packBrowser.hasSelectedPack()) {
               this.playerRef.sendMessage(Message.translation("server.customUI.assetPackBrowser.packNoLongerAvailable"));
            }
         }
      }

      commandBuilder.append("Pages/PrefabSavePage.ui");
      commandBuilder.set("#PackBrowserPage.Visible", false);
      commandBuilder.set("#CreatePackPage.Visible", false);
      if (this.packBrowser.hasSelectedPack()) {
         commandBuilder.set("#MainPage #SelectedPackLabel.Text", this.packBrowser.getSelectedPackDisplayName());
      }

      commandBuilder.set("#MainPage #NameRequiredText.Visible", false);
      commandBuilder.set("#MainPage #Entities #CheckBox.Value", true);
      commandBuilder.set("#MainPage #Empty #CheckBox.Value", false);
      commandBuilder.set("#MainPage #FromClipboard #CheckBox.Value", false);
      commandBuilder.set("#MainPage #UsePlayerAnchor #CheckBox.Value", false);
      commandBuilder.set("#MainPage #RemoveSupport #CheckBox.Value", false);
      commandBuilder.set("#MainPage #SetupSupport #CheckBox.Value", false);
      commandBuilder.set("#OverwriteConfirmPage.Visible", false);
      eventBuilder.addEventBinding(
         CustomUIEventBindingType.Activating,
         "#MainPage #SaveButton",
         new EventData()
            .append("Action", PrefabSavePage.Action.Save.name())
            .append("@Name", "#MainPage #NameInput.Value")
            .append("@Entities", "#MainPage #Entities #CheckBox.Value")
            .append("@Empty", "#MainPage #Empty #CheckBox.Value")
            .append("@FromClipboard", "#MainPage #FromClipboard #CheckBox.Value")
            .append("@UsePlayerAnchor", "#MainPage #UsePlayerAnchor #CheckBox.Value")
            .append("@RemoveSupport", "#MainPage #RemoveSupport #CheckBox.Value")
            .append("@SetupSupport", "#MainPage #SetupSupport #CheckBox.Value")
      );
      eventBuilder.addEventBinding(
         CustomUIEventBindingType.Activating, "#MainPage #CancelButton", new EventData().append("Action", PrefabSavePage.Action.Cancel.name())
      );
      eventBuilder.addEventBinding(
         CustomUIEventBindingType.ValueChanged,
         "#MainPage #RemoveSupport #CheckBox",
         new EventData()
            .append("Action", PrefabSavePage.Action.RemoveSupportChanged.name())
            .append("@RemoveSupport", "#MainPage #RemoveSupport #CheckBox.Value"),
         false
      );
      eventBuilder.addEventBinding(
         CustomUIEventBindingType.ValueChanged,
         "#MainPage #SetupSupport #CheckBox",
         new EventData().append("Action", PrefabSavePage.Action.SetupSupportChanged.name()).append("@SetupSupport", "#MainPage #SetupSupport #CheckBox.Value"),
         false
      );
      eventBuilder.addEventBinding(
         CustomUIEventBindingType.Activating,
         "#OverwriteConfirmPage #ConfirmOverwriteButton",
         new EventData().append("Action", PrefabSavePage.Action.ConfirmOverwrite.name()),
         false
      );
      eventBuilder.addEventBinding(
         CustomUIEventBindingType.Activating,
         "#OverwriteConfirmPage #CancelOverwriteButton",
         new EventData().append("Action", PrefabSavePage.Action.CancelOverwrite.name()),
         false
      );
      this.packBrowser.buildEventBindings(eventBuilder, "#MainPage #BrowsePackButton");
      this.packBrowser.buildUI(commandBuilder, eventBuilder);
   }

   public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull PrefabSavePage.PageData data) {
      Player playerComponent = store.getComponent(ref, Player.getComponentType());
      assert playerComponent != null;
      AssetPackSaveBrowser.ActionResult packResult = this.packBrowser
         .handleAction(data.action != null ? data.action.name() : null, data.packBrowserData, "#MainPage #SelectedPackLabel");
      if (packResult != null) {
         if (packResult.errorKey() != null) {
            this.playerRef.sendMessage(Message.translation(packResult.errorKey()));
         }

         if (packResult.packConfirmed() && this.packBrowser.hasSelectedPack()) {
            BuilderToolsUserData.get(playerComponent).setLastSavePack(this.packBrowser.getSelectedPack().getName());
         }

         this.sendUpdate(packResult.commandBuilder(), packResult.eventBuilder(), false);
      } else {
         switch (data.action) {
            case Save:
               String name = data.name != null ? data.name.trim() : null;
               if (name == null || name.isBlank()) {
                  UICommandBuilder commandBuilderx = new UICommandBuilder();
                  commandBuilderx.set("#MainPage #NameRequiredText.Visible", true);
                  commandBuilderx.set("#MainPage #NameRequiredText.TextSpans", MESSAGE_NAME_REQUIRED);
                  this.sendUpdate(commandBuilderx, null, false);
                  return;
               }

               if (name.contains("..")) {
                  UICommandBuilder commandBuilderx = new UICommandBuilder();
                  commandBuilderx.set("#MainPage #NameInput.Value", name);
                  commandBuilderx.set("#MainPage #NameRequiredText.Visible", true);
                  commandBuilderx.set("#MainPage #NameRequiredText.TextSpans", MESSAGE_NAME_INVALID);
                  this.sendUpdate(commandBuilderx, null, false);
                  return;
               }

               UICommandBuilder clearErrorBuilder = new UICommandBuilder();
               clearErrorBuilder.set("#MainPage #NameInput.Value", name);
               clearErrorBuilder.set("#MainPage #NameRequiredText.Visible", false);
               this.sendUpdate(clearErrorBuilder, null, false);
               AssetPack targetPack = this.packBrowser.getSelectedPack();
               if (targetPack == null) {
                  this.playerRef.sendMessage(MESSAGE_PACK_REQUIRED);
                  this.sendUpdate(null, null, false);
                  return;
               }

               BuilderToolsUserData.get(playerComponent).setLastSavePack(targetPack.getName());
               Vector3i playerAnchor = this.getPlayerAnchor(ref, store, data.usePlayerAnchor && !data.fromClipboard);
               SupportMode finalSupportMode;
               if (data.setupSupport) {
                  finalSupportMode = SupportMode.CALCULATE;
               } else if (data.removeSupport) {
                  finalSupportMode = SupportMode.REMOVE;
               } else {
                  finalSupportMode = SupportMode.KEEP_EXISTING;
               }

               String fileName = name;
               if (!fileName.endsWith(".prefab.json")) {
                  fileName = fileName + ".prefab.json";
               }

               Path targetPrefabsPath = PrefabStore.get().getAssetPrefabsPathForPack(targetPack);
               Path targetPath = targetPrefabsPath.resolve(fileName).toAbsolutePath().normalize();
               if (!PathUtil.isChildOf(targetPrefabsPath, targetPath) && !SingleplayerModule.isOwner(this.playerRef)) {
                  UICommandBuilder commandBuilderx = new UICommandBuilder();
                  commandBuilderx.set("#MainPage #NameRequiredText.Visible", true);
                  commandBuilderx.set("#MainPage #NameRequiredText.TextSpans", MESSAGE_NAME_INVALID);
                  this.sendUpdate(commandBuilderx, null, false);
                  return;
               }

               if (Files.exists(targetPath)) {
                  this.pendingSave = new PrefabSavePage.PendingSave(
                     name, data.entities, data.empty, data.fromClipboard, playerAnchor, finalSupportMode, targetPack, playerComponent
                  );
                  UICommandBuilder confirmBuilder = new UICommandBuilder();
                  confirmBuilder.set("#MainPage.Visible", false);
                  confirmBuilder.set("#OverwriteConfirmPage.Visible", true);
                  confirmBuilder.set(
                     "#OverwriteConfirmPage #MessageText.TextSpans",
                     Message.translation("server.customUI.prefabSavePage.overwriteConfirm.messageSingle").param("name", targetPath.getFileName().toString())
                  );
                  this.sendUpdate(confirmBuilder);
                  return;
               }

               playerComponent.getPageManager().setPage(ref, store, Page.None);
               this.queueSave(playerComponent, name, data.entities, data.empty, data.fromClipboard, playerAnchor, finalSupportMode, targetPack, false);
               break;
            case Cancel:
               playerComponent.getPageManager().setPage(ref, store, Page.None);
               break;
            case ConfirmOverwrite:
               if (this.pendingSave == null) {
                  return;
               }

               PrefabSavePage.PendingSave save = this.pendingSave;
               this.pendingSave = null;
               save.playerComponent.getPageManager().setPage(ref, store, Page.None);
               this.queueSave(
                  save.playerComponent, save.name, save.entities, save.empty, save.fromClipboard, save.playerAnchor, save.supportMode, save.targetPack, true
               );
               break;
            case CancelOverwrite:
               this.pendingSave = null;
               UICommandBuilder commandBuilder = new UICommandBuilder();
               commandBuilder.set("#OverwriteConfirmPage.Visible", false);
               commandBuilder.set("#MainPage.Visible", true);
               this.sendUpdate(commandBuilder);
               break;
            case RemoveSupportChanged:
               if (data.removeSupport) {
                  UICommandBuilder commandBuilderx = new UICommandBuilder();
                  commandBuilderx.set("#MainPage #SetupSupport #CheckBox.Value", false);
                  this.sendUpdate(commandBuilderx, null, false);
               }
               break;
            case SetupSupportChanged:
               if (data.setupSupport) {
                  UICommandBuilder commandBuilderx = new UICommandBuilder();
                  commandBuilderx.set("#MainPage #RemoveSupport #CheckBox.Value", false);
                  this.sendUpdate(commandBuilderx, null, false);
               }
         }
      }
   }

   @Nullable
   private Vector3i getPlayerAnchor(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, boolean usePlayerAnchor) {
      if (!usePlayerAnchor) {
         return null;
      }

      TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
      if (transformComponent == null) {
         return null;
      }

      Vector3d position = transformComponent.getPosition();
      return new Vector3i(MathUtil.floor(position.x()), MathUtil.floor(position.y()), MathUtil.floor(position.z()));
   }

   private void queueSave(
      @Nonnull Player playerComponent,
      @Nonnull String name,
      boolean entities,
      boolean empty,
      boolean fromClipboard,
      @Nullable Vector3i playerAnchor,
      @Nonnull SupportMode supportMode,
      @Nonnull AssetPack targetPack,
      boolean overwrite
   ) {
      BuilderToolsPlugin.addToQueue(playerComponent, this.playerRef, (r, s, componentAccessor) -> {
         if (fromClipboard) {
            s.save(r, name, true, overwrite, supportMode, targetPack, componentAccessor);
         } else {
            s.saveFromSelection(r, name, true, overwrite, entities, empty, playerAnchor, supportMode, targetPack, componentAccessor);
         }
      });
   }

   public enum Action {
      Save,
      Cancel,
      ConfirmOverwrite,
      CancelOverwrite,
      RemoveSupportChanged,
      SetupSupportChanged,
      OpenPackBrowser,
      ConfirmPackBrowser,
      CancelPackBrowser,
      OpenCreatePack,
      CreatePack,
      CancelCreatePack,
      PackSearch,
      PackSelect;
   }

   protected static class PageData {
      public static final String NAME = "@Name";
      public static final String ENTITIES = "@Entities";
      public static final String EMPTY = "@Empty";
      public static final String FROM_CLIPBOARD = "@FromClipboard";
      public static final String USE_PLAYER_ANCHOR = "@UsePlayerAnchor";
      public static final String REMOVE_SUPPORT = "@RemoveSupport";
      public static final String SETUP_SUPPORT = "@SetupSupport";
      public static final BuilderCodec<PrefabSavePage.PageData> CODEC = BuilderCodec.builder(PrefabSavePage.PageData.class, PrefabSavePage.PageData::new)
         .append(
            new KeyedCodec<>("Action", new EnumCodec<>(PrefabSavePage.Action.class, EnumCodec.EnumStyle.LEGACY)),
            (o, action) -> o.action = action,
            o -> o.action
         )
         .add()
         .append(new KeyedCodec<>("@Name", Codec.STRING), (o, name) -> o.name = name, o -> o.name)
         .add()
         .append(new KeyedCodec<>("@Entities", Codec.BOOLEAN), (o, entities) -> o.entities = entities, o -> o.entities)
         .add()
         .append(new KeyedCodec<>("@Empty", Codec.BOOLEAN), (o, empty) -> o.empty = empty, o -> o.empty)
         .add()
         .append(new KeyedCodec<>("@FromClipboard", Codec.BOOLEAN), (o, fromClipboard) -> o.fromClipboard = fromClipboard, o -> o.fromClipboard)
         .add()
         .append(new KeyedCodec<>("@UsePlayerAnchor", Codec.BOOLEAN), (o, usePlayerAnchor) -> o.usePlayerAnchor = usePlayerAnchor, o -> o.usePlayerAnchor)
         .add()
         .append(new KeyedCodec<>("@RemoveSupport", Codec.BOOLEAN), (o, removeSupport) -> o.removeSupport = removeSupport, o -> o.removeSupport)
         .add()
         .append(new KeyedCodec<>("@SetupSupport", Codec.BOOLEAN), (o, setupSupport) -> o.setupSupport = setupSupport, o -> o.setupSupport)
         .add()
         .append(new KeyedCodec<>("Pack", Codec.STRING), (o, s) -> o.packBrowserData.pack = s, o -> o.packBrowserData.pack)
         .add()
         .append(new KeyedCodec<>("@PackSearch", Codec.STRING), (o, s) -> o.packBrowserData.search = s, o -> o.packBrowserData.search)
         .add()
         .append(new KeyedCodec<>("@CreateName", Codec.STRING), (o, s) -> o.packBrowserData.createName = s, o -> o.packBrowserData.createName)
         .add()
         .append(new KeyedCodec<>("@CreateGroup", Codec.STRING), (o, s) -> o.packBrowserData.createGroup = s, o -> o.packBrowserData.createGroup)
         .add()
         .append(
            new KeyedCodec<>("@CreateDescription", Codec.STRING), (o, s) -> o.packBrowserData.createDescription = s, o -> o.packBrowserData.createDescription
         )
         .add()
         .append(new KeyedCodec<>("@CreateVersion", Codec.STRING), (o, s) -> o.packBrowserData.createVersion = s, o -> o.packBrowserData.createVersion)
         .add()
         .append(new KeyedCodec<>("@CreateWebsite", Codec.STRING), (o, s) -> o.packBrowserData.createWebsite = s, o -> o.packBrowserData.createWebsite)
         .add()
         .append(new KeyedCodec<>("@CreateAuthorName", Codec.STRING), (o, s) -> o.packBrowserData.createAuthorName = s, o -> o.packBrowserData.createAuthorName)
         .add()
         .append(new KeyedCodec<>("ValidateCreate", Codec.STRING), (o, s) -> o.packBrowserData.validateCreate = s, o -> o.packBrowserData.validateCreate)
         .add()
         .build();
      public PrefabSavePage.Action action;
      public String name;
      public boolean entities = true;
      public boolean empty = false;
      public boolean fromClipboard = false;
      public boolean usePlayerAnchor = false;
      public boolean removeSupport = false;
      public boolean setupSupport = false;
      public final AssetPackSaveBrowserEventData packBrowserData = new AssetPackSaveBrowserEventData();

      public PageData() {
      }
   }

   private record PendingSave(
      @Nonnull String name,
      boolean entities,
      boolean empty,
      boolean fromClipboard,
      @Nullable Vector3i playerAnchor,
      @Nonnull SupportMode supportMode,
      @Nonnull AssetPack targetPack,
      @Nonnull Player playerComponent
   ) {
   }
}
