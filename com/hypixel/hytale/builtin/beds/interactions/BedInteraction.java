/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.beds.interactions;

import com.hypixel.hytale.builtin.beds.respawn.OverrideNearbyRespawnPointPage;
import com.hypixel.hytale.builtin.beds.respawn.SelectOverrideRespawnPointPage;
import com.hypixel.hytale.builtin.beds.respawn.SetNameRespawnPointPage;
import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSleep;
import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSomnolence;
import com.hypixel.hytale.builtin.mounts.BlockMountAPI;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.gameplay.RespawnConfig;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerRespawnPointData;
import com.hypixel.hytale.server.core.entity.entities.player.pages.PageManager;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.meta.state.RespawnBlock;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BedInteraction
extends SimpleBlockInteraction {
    @Nonnull
    private static final Message MESSAGE_SERVER_CUSTOM_UI_RESPAWN_POINT_CLAIMED = Message.translation("server.customUI.respawnPointClaimed");
    @Nonnull
    public static final BuilderCodec<BedInteraction> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(BedInteraction.class, BedInteraction::new, SimpleBlockInteraction.CODEC).documentation("Interact with a bed block, ostensibly to sleep in it.")).build();

    @Override
    protected void interactWithBlock(@Nonnull World world, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull Vector3i pos, @Nonnull CooldownHandler cooldownHandler) {
        RespawnConfig respawnConfig;
        int radiusLimitRespawnPoint;
        RespawnBlock respawnBlockComponent;
        Ref<EntityStore> ref = context.getEntity();
        Player player = commandBuffer.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }
        Store<EntityStore> store = commandBuffer.getStore();
        PlayerRef playerRefComponent = commandBuffer.getComponent(ref, PlayerRef.getComponentType());
        assert (playerRefComponent != null);
        UUIDComponent playerUuidComponent = commandBuffer.getComponent(ref, UUIDComponent.getComponentType());
        assert (playerUuidComponent != null);
        UUID playerUuid = playerUuidComponent.getUuid();
        Ref<ChunkStore> chunkReference = world.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
        if (chunkReference == null) {
            return;
        }
        Store<ChunkStore> chunkStore = chunkReference.getStore();
        BlockComponentChunk blockComponentChunk = chunkStore.getComponent(chunkReference, BlockComponentChunk.getComponentType());
        assert (blockComponentChunk != null);
        int blockIndex = ChunkUtil.indexBlockInColumn(pos.x, pos.y, pos.z);
        Ref<ChunkStore> blockRef = blockComponentChunk.getEntityReference(blockIndex);
        if (blockRef == null) {
            Holder<ChunkStore> holder = ChunkStore.REGISTRY.newHolder();
            holder.putComponent(BlockModule.BlockStateInfo.getComponentType(), new BlockModule.BlockStateInfo(blockIndex, chunkReference));
            holder.ensureComponent(RespawnBlock.getComponentType());
            blockRef = chunkStore.addEntity(holder, AddReason.SPAWN);
        }
        if ((respawnBlockComponent = chunkStore.getComponent(blockRef, RespawnBlock.getComponentType())) == null) {
            return;
        }
        UUID ownerUUID = respawnBlockComponent.getOwnerUUID();
        PageManager pageManager = player.getPageManager();
        boolean isOwner = playerUuid.equals(ownerUUID);
        if (isOwner) {
            BlockPosition rawTarget = (BlockPosition)context.getMetaStore().getMetaObject(TARGET_BLOCK_RAW);
            Vector3f whereWasHit = new Vector3f((float)rawTarget.x + 0.5f, (float)rawTarget.y + 0.5f, (float)rawTarget.z + 0.5f);
            BlockMountAPI.BlockMountResult result = BlockMountAPI.mountOnBlock(ref, commandBuffer, pos, whereWasHit);
            if (result instanceof BlockMountAPI.DidNotMount) {
                player.sendMessage(Message.translation("server.interactions.didNotMount").param("state", result.toString()));
            } else if (result instanceof BlockMountAPI.Mounted) {
                commandBuffer.putComponent(ref, PlayerSomnolence.getComponentType(), PlayerSleep.NoddingOff.createComponent());
            }
            return;
        }
        if (ownerUUID != null) {
            player.sendMessage(MESSAGE_SERVER_CUSTOM_UI_RESPAWN_POINT_CLAIMED);
            return;
        }
        PlayerRespawnPointData[] respawnPoints = player.getPlayerConfigData().getPerWorldData(world.getName()).getRespawnPoints();
        PlayerRespawnPointData[] nearbyRespawnPoints = this.getNearbySavedRespawnPoints(pos, respawnBlockComponent, respawnPoints, radiusLimitRespawnPoint = (respawnConfig = world.getGameplayConfig().getRespawnConfig()).getRadiusLimitRespawnPoint());
        if (nearbyRespawnPoints != null) {
            pageManager.openCustomPage(ref, store, new OverrideNearbyRespawnPointPage(playerRefComponent, type, pos, respawnBlockComponent, nearbyRespawnPoints, radiusLimitRespawnPoint));
            return;
        }
        if (respawnPoints != null && respawnPoints.length >= respawnConfig.getMaxRespawnPointsPerPlayer()) {
            pageManager.openCustomPage(ref, store, new SelectOverrideRespawnPointPage(playerRefComponent, type, pos, respawnBlockComponent, respawnPoints));
            return;
        }
        pageManager.openCustomPage(ref, store, new SetNameRespawnPointPage(playerRefComponent, type, pos, respawnBlockComponent));
    }

    @Override
    protected void simulateInteractWithBlock(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull World world, @Nonnull Vector3i targetBlock) {
    }

    @Nullable
    private PlayerRespawnPointData[] getNearbySavedRespawnPoints(@Nonnull Vector3i currentRespawnPointPosition, @Nonnull RespawnBlock respawnBlock, @Nullable PlayerRespawnPointData[] respawnPoints, int radiusLimitRespawnPoint) {
        if (respawnPoints == null || respawnPoints.length == 0) {
            return null;
        }
        ObjectArrayList<PlayerRespawnPointData> nearbyRespawnPointList = new ObjectArrayList<PlayerRespawnPointData>();
        for (int i = 0; i < respawnPoints.length; ++i) {
            PlayerRespawnPointData respawnPoint = respawnPoints[i];
            Vector3i respawnPointPosition = respawnPoint.getBlockPosition();
            if (!(respawnPointPosition.distanceTo(currentRespawnPointPosition.x, respawnPointPosition.y, currentRespawnPointPosition.z) < (double)radiusLimitRespawnPoint)) continue;
            nearbyRespawnPointList.add(respawnPoint);
        }
        return nearbyRespawnPointList.isEmpty() ? null : (PlayerRespawnPointData[])nearbyRespawnPointList.toArray(PlayerRespawnPointData[]::new);
    }

    @Override
    @Nonnull
    public String toString() {
        return "BedInteraction{} " + super.toString();
    }
}

