/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.prefabeditor.commands;

import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabEditorCreationSettings;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.commands.PrefabEditLoadCommand;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.enums.PrefabRootDirectory;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.enums.WorldGenType;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public class PrefabEditCreateNewCommand
extends AbstractAsyncPlayerCommand {
    @Nonnull
    private static final Message MESSAGE_COMMANDS_EDIT_PREFAB_NEW_ERRORS_NOT_A_FILE = Message.translation("server.commands.editprefab.new.errors.notAFile");
    @Nonnull
    private final RequiredArg<String> prefabNameArg = this.withRequiredArg("prefabName", "server.commands.editprefab.new.name.desc", ArgTypes.STRING);
    @Nonnull
    private final DefaultArg<WorldGenType> worldGenTypeArg = this.withDefaultArg("worldgen", "server.commands.editprefab.load.worldGenType.desc", ArgTypes.forEnum("WorldGenType", WorldGenType.class), PrefabEditLoadCommand.DEFAULT_WORLD_GEN_TYPE, "server.commands.editprefab.load.worldGenType.default.desc");
    @Nonnull
    private final DefaultArg<Integer> flatNumBlocksBelowArg = (DefaultArg)this.withDefaultArg("numBlocksToSurface", "server.commands.editprefab.load.numBlocksToSurface.desc", ArgTypes.INTEGER, Integer.valueOf(0), "server.commands.editprefab.load.numBlocksToSurface.default.desc").addValidator(Validators.range(0, 120));
    @Nonnull
    private final DefaultArg<PrefabRootDirectory> prefabPathArg = this.withDefaultArg("prefabPath", "server.commands.editprefab.load.path.desc", ArgTypes.forEnum("PrefabPath", PrefabRootDirectory.class), PrefabRootDirectory.ASSET, "server.commands.editprefab.load.path.default.desc");

    public PrefabEditCreateNewCommand() {
        super("new", "server.commands.editprefab.new.desc");
    }

    @Override
    @Nonnull
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        Path prefabPath;
        Player playerComponent = store.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        Path prefabBaseDirectory = ((PrefabRootDirectory)((Object)this.prefabPathArg.get(context))).getPrefabPath();
        Object prefabName = (String)this.prefabNameArg.get(context);
        if (!((String)prefabName).endsWith(".prefab.json")) {
            prefabName = (String)prefabName + ".prefab.json";
        }
        if ((prefabPath = prefabBaseDirectory.resolve((String)prefabName)).toString().endsWith("/")) {
            context.sendMessage(MESSAGE_COMMANDS_EDIT_PREFAB_NEW_ERRORS_NOT_A_FILE);
            return CompletableFuture.completedFuture(null);
        }
        PrefabEditorCreationSettings prefabEditorLoadCommandSettings = new PrefabEditorCreationSettings((PrefabRootDirectory)((Object)this.prefabPathArg.get(context)), List.of(prefabName), 55, 15, (WorldGenType)((Object)this.worldGenTypeArg.get(context)), (Integer)this.flatNumBlocksBelowArg.get(context), PrefabEditLoadCommand.DEFAULT_PREFAB_STACKING_AXIS, PrefabEditLoadCommand.DEFAULT_PREFAB_ALIGNMENT, false, false, true, true, PrefabEditLoadCommand.DEFAULT_ROW_SPLIT_MODE, "Env_Zone1_Plains", "#5B9E28");
        context.sendMessage(Message.translation("server.commands.editprefab.new.success").param("path", prefabPath.toString()));
        return BuilderToolsPlugin.get().getPrefabEditSessionManager().createEditSessionForNewPrefab(ref, playerComponent, prefabEditorLoadCommandSettings, store);
    }
}

