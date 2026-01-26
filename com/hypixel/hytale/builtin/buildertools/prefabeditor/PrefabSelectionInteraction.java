/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.prefabeditor;

import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabEditSession;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabEditSessionManager;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabEditingMetadata;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrefabSelectionInteraction
extends SimpleInstantInteraction {
    @Nonnull
    private static final Message MESSAGE_COMMANDS_EDIT_PREFAB_SELECT_ERROR_NO_TARGET_FOUND = Message.translation("server.commands.editprefab.select.error.noTargetFound");
    @Nonnull
    private static final Message MESSAGE_COMMANDS_EDIT_PREFAB_SELECT_ERROR_NO_PREFAB_FOUND = Message.translation("server.commands.editprefab.select.error.noPrefabFound");
    @Nonnull
    private static final Message MESSAGE_COMMANDS_EDIT_PREFAB_NOT_IN_EDIT_SESSION = Message.translation("server.commands.editprefab.notInEditSession");
    private static final float ENTITY_TARGET_RADIUS = 50.0f;
    @Nonnull
    public static final BuilderCodec<PrefabSelectionInteraction> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(PrefabSelectionInteraction.class, PrefabSelectionInteraction::new, SimpleInstantInteraction.CODEC).documentation("Interaction that handles the selection functionally for the prefab selection tool.")).build();

    @Override
    protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        assert (commandBuffer != null);
        Ref<EntityStore> ref = context.getEntity();
        Player playerComponent = commandBuffer.getComponent(ref, Player.getComponentType());
        if (playerComponent == null) {
            return;
        }
        if (type != InteractionType.Primary && type != InteractionType.Secondary) {
            return;
        }
        UUIDComponent uuidComponent = commandBuffer.getComponent(ref, UUIDComponent.getComponentType());
        assert (uuidComponent != null);
        UUID uuid = uuidComponent.getUuid();
        PrefabEditSessionManager prefabEditSessionManager = BuilderToolsPlugin.get().getPrefabEditSessionManager();
        PrefabEditSession prefabEditSession = prefabEditSessionManager.getPrefabEditSession(uuid);
        if (prefabEditSession == null) {
            playerComponent.sendMessage(MESSAGE_COMMANDS_EDIT_PREFAB_NOT_IN_EDIT_SESSION);
            return;
        }
        TransformComponent transformComponent = commandBuffer.getComponent(ref, TransformComponent.getComponentType());
        assert (transformComponent != null);
        Vector3d playerPosition = transformComponent.getPosition();
        PrefabEditingMetadata prefabEditingMetadata = null;
        if (type == InteractionType.Secondary) {
            Vector3d playerLocation = playerPosition.clone();
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
            Vector3i targetLocation = PrefabSelectionInteraction.getTargetLocation(ref, commandBuffer);
            if (targetLocation == null) {
                playerComponent.sendMessage(MESSAGE_COMMANDS_EDIT_PREFAB_SELECT_ERROR_NO_TARGET_FOUND);
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
            playerComponent.sendMessage(MESSAGE_COMMANDS_EDIT_PREFAB_SELECT_ERROR_NO_PREFAB_FOUND);
            return;
        }
        prefabEditSession.setSelectedPrefab(ref, prefabEditingMetadata, commandBuffer);
    }

    @Nullable
    private static Vector3i getTargetLocation(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        Vector3i targetBlock = TargetUtil.getTargetBlock(ref, 200.0, componentAccessor);
        if (targetBlock != null) {
            return targetBlock;
        }
        Ref<EntityStore> targetEntityRef = TargetUtil.getTargetEntity(ref, 50.0f, componentAccessor);
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

