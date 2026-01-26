/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.portals.interactions;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.builtin.portals.components.PortalDevice;
import com.hypixel.hytale.builtin.portals.resources.PortalWorld;
import com.hypixel.hytale.builtin.portals.ui.PortalDeviceActivePage;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnterPortalInteraction
extends SimpleBlockInteraction {
    @Nonnull
    public static final Duration MINIMUM_TIME_IN_WORLD = Duration.ofMillis(3000L);
    @Nonnull
    public static final BuilderCodec<EnterPortalInteraction> CODEC = BuilderCodec.builder(EnterPortalInteraction.class, EnterPortalInteraction::new, SimpleBlockInteraction.CODEC).build();
    private static final Message MESSAGE_PORTALS_DEVICE_REF_INVALID = Message.translation("server.portals.device.refInvalid");
    private static final Message MESSAGE_PORTALS_DEVICE_WORLD_IS_DEAD = Message.translation("server.portals.device.worldIsDead");
    private static final Message MESSAGE_PORTALS_DEVICE_NO_SPAWN = Message.translation("server.portals.device.worldNoSpawn");
    private static final Message MESSAGE_PORTALS_DEVICE_BLOCK_ENTITY_REF_INVALID = Message.translation("server.portals.device.blockEntityRefInvalid");

    @Override
    @Nonnull
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Server;
    }

    @Override
    protected void interactWithBlock(@Nonnull World world, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull Vector3i targetBlock, @Nonnull CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = context.getEntity();
        Player playerComponent = commandBuffer.getComponent(ref, Player.getComponentType());
        if (playerComponent == null) {
            context.getState().state = InteractionState.Failed;
            return;
        }
        if (playerComponent.getSinceLastSpawnNanos() < MINIMUM_TIME_IN_WORLD.toNanos()) {
            context.getState().state = InteractionState.Failed;
            return;
        }
        PortalDevice portalDevice = BlockModule.get().getComponent(PortalDevice.getComponentType(), world, targetBlock.x, targetBlock.y, targetBlock.z);
        if (portalDevice == null) {
            context.getState().state = InteractionState.Failed;
            return;
        }
        WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z));
        if (chunk == null) {
            context.getState().state = InteractionState.Failed;
            return;
        }
        BlockType blockType = chunk.getBlockType(targetBlock);
        if (blockType == null) {
            context.getState().state = InteractionState.Failed;
            return;
        }
        RotationTuple rotation = chunk.getRotation(targetBlock.x, targetBlock.y, targetBlock.z);
        double yaw = rotation.yaw().getRadians() + Math.PI;
        Transform returnTransform = new Transform((double)targetBlock.x + 0.5, (double)targetBlock.y + 0.5, (double)targetBlock.z + 0.5, 0.0f, (float)yaw, 0.0f);
        World targetWorld = portalDevice.getDestinationWorld();
        if (targetWorld == null) {
            playerComponent.sendMessage(MESSAGE_PORTALS_DEVICE_WORLD_IS_DEAD);
            context.getState().state = InteractionState.Failed;
            return;
        }
        UUIDComponent uuidComponent = commandBuffer.getComponent(ref, UUIDComponent.getComponentType());
        assert (uuidComponent != null);
        UUID playerUuid = uuidComponent.getUuid();
        EnterPortalInteraction.fetchTargetWorldState(targetWorld, playerUuid).thenAcceptAsync(state -> {
            if (!ref.isValid()) {
                playerComponent.sendMessage(MESSAGE_PORTALS_DEVICE_REF_INVALID);
                context.getState().state = InteractionState.Failed;
                return;
            }
            switch (state.ordinal()) {
                case 1: {
                    playerComponent.sendMessage(MESSAGE_PORTALS_DEVICE_WORLD_IS_DEAD);
                    context.getState().state = InteractionState.Failed;
                    break;
                }
                case 3: {
                    playerComponent.sendMessage(MESSAGE_PORTALS_DEVICE_NO_SPAWN);
                    context.getState().state = InteractionState.Failed;
                    break;
                }
                case 2: {
                    PlayerRef playerRefComponent = commandBuffer.getComponent(ref, PlayerRef.getComponentType());
                    assert (playerRefComponent != null);
                    Ref<ChunkStore> blockEntityRef = BlockModule.getBlockEntity(world, targetBlock.x, targetBlock.y, targetBlock.z);
                    if (blockEntityRef == null || !blockEntityRef.isValid()) {
                        playerComponent.sendMessage(MESSAGE_PORTALS_DEVICE_BLOCK_ENTITY_REF_INVALID);
                        context.getState().state = InteractionState.Failed;
                        return;
                    }
                    PortalDeviceActivePage activePage = new PortalDeviceActivePage(playerRefComponent, portalDevice.getConfig(), blockEntityRef);
                    playerComponent.getPageManager().openCustomPage(ref, world.getEntityStore().getStore(), activePage);
                    break;
                }
                case 0: {
                    InstancesPlugin.teleportPlayerToInstance(ref, commandBuffer, targetWorld, returnTransform);
                }
            }
        }, (Executor)world);
    }

    @Nonnull
    private static CompletableFuture<TargetWorldState> fetchTargetWorldState(@Nonnull World world, @Nonnull UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            PortalWorld portalWorld = world.getEntityStore().getStore().getResource(PortalWorld.getResourceType());
            if (!portalWorld.exists()) {
                return TargetWorldState.WORLD_DEAD;
            }
            if (portalWorld.getSpawnPoint() == null) {
                return TargetWorldState.NO_SPAWN_AVAILABLE;
            }
            if (portalWorld.getDiedInWorld().contains(playerId)) {
                return TargetWorldState.DIED_IN_WORLD;
            }
            return TargetWorldState.OKAY;
        }, world);
    }

    @Override
    protected void simulateInteractWithBlock(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull World world, @Nonnull Vector3i targetBlock) {
    }

    private static enum TargetWorldState {
        OKAY,
        WORLD_DEAD,
        DIED_IN_WORLD,
        NO_SPAWN_AVAILABLE;

    }
}

