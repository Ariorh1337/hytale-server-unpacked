/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.prefabeditor.commands;

import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabEditSession;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabEditSessionManager;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabEditingMetadata;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrefabEditSelectCommand
extends AbstractPlayerCommand {
    @Nonnull
    private static final Message MESSAGE_COMMANDS_EDIT_PREFAB_SELECT_ERROR_NO_TARGET_FOUND = Message.translation("server.commands.editprefab.select.error.noTargetFound");
    @Nonnull
    private static final Message MESSAGE_COMMANDS_EDIT_PREFAB_SELECT_ERROR_NO_PREFAB_FOUND = Message.translation("server.commands.editprefab.select.error.noPrefabFound");
    @Nonnull
    private static final Message MESSAGE_COMMANDS_EDIT_PREFAB_NOT_IN_EDIT_SESSION = Message.translation("server.commands.editprefab.notInEditSession");
    @Nonnull
    private final FlagArg nearestArg = this.withFlagArg("nearest", "server.commands.editprefab.select.nearest.desc");

    public PrefabEditSelectCommand() {
        super("select", "server.commands.editprefab.select.desc");
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        PrefabEditSessionManager prefabEditSessionManager = BuilderToolsPlugin.get().getPrefabEditSessionManager();
        PrefabEditSession prefabEditSession = prefabEditSessionManager.getPrefabEditSession(playerRef.getUuid());
        if (prefabEditSession == null) {
            context.sendMessage(MESSAGE_COMMANDS_EDIT_PREFAB_NOT_IN_EDIT_SESSION);
            return;
        }
        PrefabEditingMetadata prefabEditingMetadata = null;
        if (((Boolean)this.nearestArg.get(context)).booleanValue()) {
            TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
            assert (transformComponent != null);
            Vector3d playerLocation = transformComponent.getPosition().clone();
            playerLocation.setY(0.0);
            double distance = 2.147483647E9;
            for (PrefabEditingMetadata value : prefabEditSession.getLoadedPrefabMetadata().values()) {
                Vector3d centerPoint = new Vector3d((double)(value.getMaxPoint().x + value.getMinPoint().x) / 2.0, 0.0, (double)(value.getMaxPoint().z + value.getMinPoint().z) / 2.0);
                double distanceTo = centerPoint.distanceTo(playerLocation);
                if (!(distance > distanceTo)) continue;
                distance = distanceTo;
                prefabEditingMetadata = value;
            }
        } else {
            Vector3i targetLocation = PrefabEditSelectCommand.getTargetLocation(ref, store);
            if (targetLocation == null) {
                context.sendMessage(MESSAGE_COMMANDS_EDIT_PREFAB_SELECT_ERROR_NO_TARGET_FOUND);
                return;
            }
            for (PrefabEditingMetadata value : prefabEditSession.getLoadedPrefabMetadata().values()) {
                boolean isWithinPrefab = value.isLocationWithinPrefabBoundingBox(targetLocation);
                if (!isWithinPrefab) continue;
                prefabEditingMetadata = value;
                break;
            }
        }
        if (prefabEditingMetadata == null) {
            context.sendMessage(MESSAGE_COMMANDS_EDIT_PREFAB_SELECT_ERROR_NO_PREFAB_FOUND);
            return;
        }
        Player playerComponent = store.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        prefabEditSession.setSelectedPrefab(ref, prefabEditingMetadata, store);
    }

    @Nullable
    private static Vector3i getTargetLocation(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        Vector3i targetBlock = TargetUtil.getTargetBlock(ref, 200.0, componentAccessor);
        if (targetBlock != null) {
            return targetBlock;
        }
        Ref<EntityStore> targetEntityRef = TargetUtil.getTargetEntity(ref, componentAccessor);
        if (targetEntityRef == null || !targetEntityRef.isValid()) {
            return null;
        }
        TransformComponent entityTransformComponent = componentAccessor.getComponent(targetEntityRef, TransformComponent.getComponentType());
        if (entityTransformComponent == null) {
            return null;
        }
        return entityTransformComponent.getPosition().toVector3i();
    }
}

