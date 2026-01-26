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
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.UUID;
import javax.annotation.Nonnull;

public class PrefabSetAnchorInteraction
extends SimpleInstantInteraction {
    @Nonnull
    private static final Message MESSAGE_COMMANDS_EDIT_PREFAB_NOT_IN_EDIT_SESSION = Message.translation("server.commands.editprefab.notInEditSession");
    @Nonnull
    private static final Message MESSAGE_COMMANDS_EDIT_PREFAB_ANCHOR_ERROR_NO_ANCHOR_FOUND = Message.translation("server.commands.editprefab.anchor.error.noAnchorFound");
    @Nonnull
    private static final Message MESSAGE_COMMANDS_EDIT_PREFAB_SELECT_ERROR_NO_PREFAB_FOUND = Message.translation("server.commands.editprefab.select.error.noPrefabFound");
    @Nonnull
    private static final Message MESSAGE_COMMANDS_EDIT_PREFAB_ANCHOR_SUCCESS = Message.translation("server.commands.editprefab.anchor.success");
    @Nonnull
    public static final BuilderCodec<PrefabSetAnchorInteraction> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(PrefabSetAnchorInteraction.class, PrefabSetAnchorInteraction::new, SimpleInstantInteraction.CODEC).documentation("Sets the prefab anchor.")).build();

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
        PlayerRef playerRefComponent = commandBuffer.getComponent(ref, PlayerRef.getComponentType());
        assert (playerRefComponent != null);
        UUID playerUuid = uuidComponent.getUuid();
        PrefabEditSessionManager prefabEditSessionManager = BuilderToolsPlugin.get().getPrefabEditSessionManager();
        PrefabEditSession prefabEditSession = prefabEditSessionManager.getPrefabEditSession(playerUuid);
        if (prefabEditSession == null) {
            playerRefComponent.sendMessage(MESSAGE_COMMANDS_EDIT_PREFAB_NOT_IN_EDIT_SESSION);
            return;
        }
        PrefabEditingMetadata prefabEditingMetadata = null;
        BlockPosition targetBlock = context.getTargetBlock();
        if (targetBlock == null) {
            playerRefComponent.sendMessage(MESSAGE_COMMANDS_EDIT_PREFAB_ANCHOR_ERROR_NO_ANCHOR_FOUND);
            return;
        }
        Vector3i targetBlockPos = new Vector3i(targetBlock.x, targetBlock.y, targetBlock.z);
        for (PrefabEditingMetadata value : prefabEditSession.getLoadedPrefabMetadata().values()) {
            boolean isWithinPrefab = value.isLocationWithinPrefabBoundingBox(targetBlockPos);
            if (!isWithinPrefab) continue;
            prefabEditingMetadata = value;
            break;
        }
        if (prefabEditingMetadata == null) {
            playerRefComponent.sendMessage(MESSAGE_COMMANDS_EDIT_PREFAB_SELECT_ERROR_NO_PREFAB_FOUND);
            return;
        }
        prefabEditSession.setSelectedPrefab(ref, prefabEditingMetadata, commandBuffer);
        prefabEditingMetadata.setAnchorPoint(targetBlockPos, commandBuffer.getExternalData().getWorld());
        prefabEditingMetadata.sendAnchorHighlightingPacket(playerRefComponent.getPacketHandler());
        playerRefComponent.sendMessage(MESSAGE_COMMANDS_EDIT_PREFAB_ANCHOR_SUCCESS);
    }
}

