package com.hypixel.hytale.builtin.buildertools.prefabeditor.commands;

import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
import com.hypixel.hytale.builtin.buildertools.BuilderToolsUserData;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabEditSession;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabEditSessionManager;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabEditingMetadata;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.saving.PrefabSaver;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.saving.PrefabSaverSettings;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.saving.SupportMode;
import com.hypixel.hytale.common.util.PathUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.singleplayer.SingleplayerModule;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public class PrefabEditSaveAsCommand extends AbstractAsyncPlayerCommand {
   @Nonnull
   private static final Message MESSAGE_NOT_IN_EDIT_WORLD = Message.translation("server.commands.editprefab.notInEditWorldWarning");
   private final RequiredArg<String> fileNameArg = this.withRequiredArg("fileNameArg", "server.commands.editprefab.save.saveAs.desc", ArgTypes.STRING);
   private final FlagArg noEntitiesArg = this.withFlagArg("noEntities", "server.commands.editprefab.save.noEntities.desc");
   private final FlagArg overwriteArg = this.withFlagArg("overwrite", "server.commands.editprefab.save.overwrite.desc");
   private final FlagArg emptyArg = this.withFlagArg("empty", "server.commands.editprefab.save.empty.desc");
   private final FlagArg noUpdateArg = this.withFlagArg("noUpdate", "server.commands.editprefab.saveAs.noUpdate.desc");
   private final FlagArg removeSupportArg = this.withFlagArg("removeSupport", "server.commands.editprefab.save.removeSupport.desc");
   private final FlagArg setupSupportArg = this.withFlagArg("setupSupport", "server.commands.editprefab.save.setupSupport.desc");
   private final DefaultArg<String> packArg = this.withDefaultArg(
      "pack", "server.commands.editprefab.save.pack.desc", ArgTypes.STRING, "", "server.commands.editprefab.save.pack.desc"
   );

   public PrefabEditSaveAsCommand() {
      super("saveas", "server.commands.editprefab.saveAs.desc");
   }

   @Nonnull
   @Override
   protected CompletableFuture<Void> executeAsync(
      @Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world
   ) {
      Player playerComponent = store.getComponent(ref, Player.getComponentType());
      assert playerComponent != null;
      UUID uuid = playerRef.getUuid();
      PrefabEditSessionManager prefabEditSessionManager = BuilderToolsPlugin.get().getPrefabEditSessionManager();
      PrefabEditSession prefabEditSession = prefabEditSessionManager.getPrefabEditSession(uuid);
      if (prefabEditSession == null) {
         context.sendMessage(Message.translation("server.commands.editprefab.notInEditSession"));
         return CompletableFuture.completedFuture(null);
      }

      if (!prefabEditSessionManager.isInEditWorld(playerRef, store)) {
         context.sendMessage(MESSAGE_NOT_IN_EDIT_WORLD);
         return CompletableFuture.completedFuture(null);
      }

      PrefabSaverSettings prefabSaverSettings = new PrefabSaverSettings();
      prefabSaverSettings.setBlocks(true);
      prefabSaverSettings.setEntities(!this.noEntitiesArg.provided(context));
      prefabSaverSettings.setOverwriteExisting(this.overwriteArg.get(context));
      prefabSaverSettings.setEmpty(this.emptyArg.get(context));
      boolean removeSupport = this.removeSupportArg.get(context);
      boolean setupSupport = this.setupSupportArg.get(context);
      if (setupSupport) {
         prefabSaverSettings.setSupportMode(SupportMode.CALCULATE);
      } else if (removeSupport) {
         prefabSaverSettings.setSupportMode(SupportMode.REMOVE);
      }

      PrefabEditingMetadata selectedPrefab = prefabEditSession.getSelectedPrefab(uuid);
      String packName = this.packArg.get(context);
      Path sourcePrefabPath = selectedPrefab != null ? selectedPrefab.getPrefabPath() : null;
      AssetPack targetPack = BuilderToolsPlugin.resolveTargetPack(packName != null ? packName : "", sourcePrefabPath, playerComponent, context);
      if (targetPack == null) {
         return CompletableFuture.completedFuture(null);
      }

      BuilderToolsUserData.get(playerComponent).setLastSavePack(targetPack.getName());
      Path prefabRootPath = PrefabStore.get().getAssetPrefabsPathForPack(targetPack);
      String fileName = this.fileNameArg.get(context).trim();
      if (fileName.isBlank()) {
         context.sendMessage(Message.translation("server.builderTools.prefabSave.nameRequired"));
         return CompletableFuture.completedFuture(null);
      }

      if (fileName.contains("..")) {
         context.sendMessage(Message.translation("server.builderTools.attemptedToSaveOutsidePrefabsDir"));
         return CompletableFuture.completedFuture(null);
      }

      if (!PathUtil.isChildOf(prefabRootPath, prefabRootPath.resolve(fileName)) && !SingleplayerModule.isOwner(playerRef)) {
         context.sendMessage(Message.translation("server.builderTools.attemptedToSaveOutsidePrefabsDir"));
         return CompletableFuture.completedFuture(null);
      }

      Path prefabSavePath = prefabRootPath.resolve(fileName);
      if (prefabSavePath.toString().endsWith("/")) {
         context.sendMessage(Message.translation("server.commands.editprefab.saveAs.errors.notAFile"));
         return CompletableFuture.completedFuture(null);
      }

      if (!prefabEditSession.toString().endsWith(".prefab.json")) {
         prefabSavePath = Path.of(prefabSavePath + ".prefab.json");
      }

      if (selectedPrefab == null) {
         context.sendMessage(Message.translation("server.commands.editprefab.noPrefabSelected"));
         return CompletableFuture.completedFuture(null);
      }

      BlockSelection selection = BuilderToolsPlugin.getState(playerComponent, playerRef).getSelection();
      if (selectedPrefab.getMinPoint().equals(selection.getSelectionMin()) && selectedPrefab.getMaxPoint().equals(selection.getSelectionMax())) {
         if (!this.noUpdateArg.provided(context)) {
            prefabEditSessionManager.updatePathOfLoadedPrefab(selectedPrefab.getPrefabPath(), prefabSavePath);
            selectedPrefab.setPrefabPath(prefabSavePath);
         }

         return PrefabSaver.savePrefab(
               playerRef,
               world,
               prefabSavePath,
               selectedPrefab.getAnchorPoint(),
               selectedPrefab.getMinPoint(),
               selectedPrefab.getMaxPoint(),
               selectedPrefab.getPastePosition(),
               selectedPrefab.getOriginalFileAnchor(),
               prefabSaverSettings
            )
            .thenAccept(
               success -> context.sendMessage(
                  Message.translation("server.commands.editprefab.save." + (success ? "success.pack" : "failure.pack"))
                     .param("name", selectedPrefab.getPrefabPath().toString())
                     .param("pack", targetPack.getName())
               )
            );
      } else {
         context.sendMessage(Message.translation("server.commands.editprefab.save.selectionMismatch"));
         return CompletableFuture.completedFuture(null);
      }
   }
}
