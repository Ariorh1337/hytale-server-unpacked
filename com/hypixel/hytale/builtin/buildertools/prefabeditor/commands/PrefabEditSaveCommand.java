/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.prefabeditor.commands;

import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabEditSession;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabEditSessionManager;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabEditingMetadata;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.saving.PrefabSaver;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.saving.PrefabSaverSettings;
import com.hypixel.hytale.common.util.PathUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.singleplayer.SingleplayerModule;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public class PrefabEditSaveCommand
extends AbstractAsyncPlayerCommand {
    @Nonnull
    private static final Message MESSAGE_COMMANDS_EDIT_PREFAB_NOT_IN_EDIT_SESSION = Message.translation("server.commands.editprefab.notInEditSession");
    @Nonnull
    private static final Message MESSAGE_PATH_OUTSIDE_PREFABS_DIR = Message.translation("server.builderTools.attemptedToSaveOutsidePrefabsDir");
    @Nonnull
    private final FlagArg saveAllArg = (FlagArg)this.withFlagArg("saveAll", "server.commands.editprefab.save.saveAll.desc").addAliases("all");
    @Nonnull
    private final FlagArg noEntitiesArg = this.withFlagArg("noEntities", "server.commands.editprefab.save.noEntities.desc");
    @Nonnull
    private final FlagArg emptyArg = this.withFlagArg("empty", "server.commands.editprefab.save.empty.desc");
    @Nonnull
    private final FlagArg confirmArg = this.withFlagArg("confirm", "server.commands.editprefab.save.confirm.desc");

    private static boolean isPathInAllowedPrefabDirectory(@Nonnull Path path) {
        PrefabStore prefabStore = PrefabStore.get();
        return PathUtil.isChildOf(prefabStore.getServerPrefabsPath(), path) || PathUtil.isChildOf(prefabStore.getAssetPrefabsPath(), path) || PathUtil.isChildOf(prefabStore.getWorldGenPrefabsPath(), path);
    }

    public PrefabEditSaveCommand() {
        super("save", "server.commands.editprefab.save.desc");
    }

    @Override
    @Nonnull
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        Player playerComponent = store.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        PrefabEditSessionManager prefabEditSessionManager = BuilderToolsPlugin.get().getPrefabEditSessionManager();
        PrefabEditSession prefabEditSession = prefabEditSessionManager.getPrefabEditSession(playerRef.getUuid());
        if (prefabEditSession == null) {
            context.sendMessage(MESSAGE_COMMANDS_EDIT_PREFAB_NOT_IN_EDIT_SESSION);
            return CompletableFuture.completedFuture(null);
        }
        PrefabSaverSettings prefabSaverSettings = new PrefabSaverSettings();
        prefabSaverSettings.setBlocks(true);
        prefabSaverSettings.setEntities(!this.noEntitiesArg.provided(context));
        prefabSaverSettings.setOverwriteExisting(true);
        prefabSaverSettings.setEmpty((Boolean)this.emptyArg.get(context));
        boolean confirm = this.confirmArg.provided(context);
        if (this.saveAllArg.provided(context)) {
            PrefabEditingMetadata[] values = prefabEditSession.getLoadedPrefabMetadata().values().toArray(new PrefabEditingMetadata[0]);
            int readOnlyCount = 0;
            for (PrefabEditingMetadata value : values) {
                if (!value.isReadOnly()) continue;
                ++readOnlyCount;
            }
            if (readOnlyCount > 0 && !confirm) {
                context.sendMessage(Message.translation("server.commands.editprefab.save.readOnlyNeedsConfirm").param("count", readOnlyCount));
                return CompletableFuture.completedFuture(null);
            }
            if (!SingleplayerModule.isOwner(playerRef)) {
                for (PrefabEditingMetadata value : values) {
                    Path savePath = PrefabEditSaveCommand.getWritableSavePath(value, confirm);
                    if (PrefabEditSaveCommand.isPathInAllowedPrefabDirectory(savePath)) continue;
                    context.sendMessage(MESSAGE_PATH_OUTSIDE_PREFABS_DIR);
                    return CompletableFuture.completedFuture(null);
                }
            }
            context.sendMessage(Message.translation("server.commands.editprefab.save.saveAll.start").param("amount", values.length));
            CompletableFuture[] prefabSavingFutures = new CompletableFuture[values.length];
            for (int i = 0; i < values.length; ++i) {
                PrefabEditingMetadata value = values[i];
                Path savePath = PrefabEditSaveCommand.getWritableSavePath(value, confirm);
                prefabSavingFutures[i] = PrefabSaver.savePrefab(playerComponent, world, savePath, value.getAnchorPoint(), value.getMinPoint(), value.getMaxPoint(), value.getPastePosition(), value.getOriginalFileAnchor(), prefabSaverSettings);
            }
            return CompletableFuture.allOf(prefabSavingFutures).thenAccept(unused -> {
                IntArrayList failedPrefabFutures = new IntArrayList();
                for (int i1 = 0; i1 < prefabSavingFutures.length; ++i1) {
                    if (((Boolean)prefabSavingFutures[i1].join()).booleanValue()) {
                        values[i1].setDirty(false);
                        continue;
                    }
                    failedPrefabFutures.add(Integer.valueOf(i1));
                }
                context.sendMessage(Message.translation("server.commands.editprefab.save.saveAll.success").param("successes", prefabSavingFutures.length - failedPrefabFutures.size()).param("failures", failedPrefabFutures.size()));
            });
        }
        PrefabEditingMetadata selectedPrefab = prefabEditSession.getSelectedPrefab(playerRef.getUuid());
        if (selectedPrefab == null) {
            context.sendMessage(Message.translation("server.commands.editprefab.noPrefabSelected"));
            return CompletableFuture.completedFuture(null);
        }
        if (selectedPrefab.isReadOnly() && !confirm) {
            Path redirectPath = PrefabEditSaveCommand.getWritableSavePath(selectedPrefab, true);
            context.sendMessage(Message.translation("server.commands.editprefab.save.readOnlyNeedsConfirmSingle").param("path", selectedPrefab.getPrefabPath().toString()).param("redirectPath", redirectPath.toString()));
            return CompletableFuture.completedFuture(null);
        }
        BlockSelection selection = BuilderToolsPlugin.getState(playerComponent, playerRef).getSelection();
        if (!selectedPrefab.getMinPoint().equals(selection.getSelectionMin()) || !selectedPrefab.getMaxPoint().equals(selection.getSelectionMax())) {
            context.sendMessage(Message.translation("server.commands.editprefab.save.selectionMismatch"));
            return CompletableFuture.completedFuture(null);
        }
        Path savePath = PrefabEditSaveCommand.getWritableSavePath(selectedPrefab, confirm);
        if (!SingleplayerModule.isOwner(playerRef) && !PrefabEditSaveCommand.isPathInAllowedPrefabDirectory(savePath)) {
            context.sendMessage(MESSAGE_PATH_OUTSIDE_PREFABS_DIR);
            return CompletableFuture.completedFuture(null);
        }
        return PrefabSaver.savePrefab(playerComponent, world, savePath, selectedPrefab.getAnchorPoint(), selectedPrefab.getMinPoint(), selectedPrefab.getMaxPoint(), selectedPrefab.getPastePosition(), selectedPrefab.getOriginalFileAnchor(), prefabSaverSettings).thenAccept(success -> {
            if (success.booleanValue()) {
                selectedPrefab.setDirty(false);
            }
            context.sendMessage(Message.translation("server.commands.editprefab.save." + (success != false ? "success" : "failure")).param("name", savePath.toString()));
        });
    }

    @Nonnull
    private static Path getWritableSavePath(@Nonnull PrefabEditingMetadata metadata, boolean confirm) {
        if (!metadata.isReadOnly() || !confirm) {
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
}

