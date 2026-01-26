/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.client;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.BlockRotation;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.InteractionSyncData;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.Rotation;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockFace;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.interaction.BlockPlaceUtils;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlaceBlockInteraction
extends SimpleInteraction {
    public static final int MAX_ADVENTURE_PLACEMENT_RANGE_SQUARED = 36;
    @Nonnull
    public static final BuilderCodec<PlaceBlockInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(PlaceBlockInteraction.class, PlaceBlockInteraction::new, SimpleInteraction.CODEC).documentation("Places the current or given block.")).append(new KeyedCodec<String>("BlockTypeToPlace", Codec.STRING), (placeBlockInteraction, blockTypeKey) -> {
        placeBlockInteraction.blockTypeKey = blockTypeKey;
    }, placeBlockInteraction -> placeBlockInteraction.blockTypeKey).addValidatorLate(() -> BlockType.VALIDATOR_CACHE.getValidator().late()).documentation("Overrides the placed block type of the held item with the provided block type.").add()).append(new KeyedCodec<Boolean>("RemoveItemInHand", Codec.BOOLEAN), (placeBlockInteraction, aBoolean) -> {
        placeBlockInteraction.removeItemInHand = aBoolean;
    }, placeBlockInteraction -> placeBlockInteraction.removeItemInHand).documentation("Determines whether to remove the item that is in the instigating entities hand.").add()).appendInherited(new KeyedCodec<Boolean>("AllowDragPlacement", Codec.BOOLEAN), (placeBlockInteraction, aBoolean) -> {
        placeBlockInteraction.allowDragPlacement = aBoolean;
    }, placeBlockInteraction -> placeBlockInteraction.allowDragPlacement, (placeBlockInteraction, parent) -> {
        placeBlockInteraction.allowDragPlacement = parent.allowDragPlacement;
    }).documentation("If drag placement should be used when click is held for this interaction.").add()).build();
    @Nullable
    protected String blockTypeKey;
    protected boolean removeItemInHand = true;
    protected boolean allowDragPlacement = true;

    @Override
    @Nonnull
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Client;
    }

    @Override
    protected final void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        InteractionSyncData clientState = context.getClientState();
        assert (clientState != null);
        if (!firstRun) {
            context.getState().state = clientState.state;
            return;
        }
        Ref<EntityStore> ref = context.getEntity();
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        assert (commandBuffer != null);
        BlockPosition blockPosition = clientState.blockPosition;
        BlockRotation blockRotation = clientState.blockRotation;
        if (blockPosition != null && blockRotation != null) {
            boolean isAdventure;
            String clientPlacedBlockTypeKey;
            String interactionBlockTypeKey;
            Vector3d blockCenter;
            Vector3d position;
            World world = commandBuffer.getExternalData().getWorld();
            Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
            long chunkIndex = ChunkUtil.indexChunkFromBlock(blockPosition.x, blockPosition.z);
            Ref<ChunkStore> chunkReference = chunkStore.getExternalData().getChunkReference(chunkIndex);
            if (chunkReference == null || !chunkReference.isValid()) {
                return;
            }
            ItemStack heldItemStack = context.getHeldItem();
            if (heldItemStack == null) {
                return;
            }
            ItemContainer heldItemContainer = context.getHeldItemContainer();
            if (heldItemContainer == null) {
                return;
            }
            TransformComponent transformComponent = commandBuffer.getComponent(ref, TransformComponent.getComponentType());
            Player playerComponent = commandBuffer.getComponent(ref, Player.getComponentType());
            if (transformComponent != null && playerComponent != null && playerComponent.getGameMode() != GameMode.Creative && (position = transformComponent.getPosition()).distanceSquaredTo(blockCenter = new Vector3d((double)blockPosition.x + 0.5, (double)blockPosition.y + 0.5, (double)blockPosition.z + 0.5)) > 36.0) {
                return;
            }
            Inventory inventory = null;
            Entity entity = EntityUtils.getEntity(ref, commandBuffer);
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity;
                inventory = livingEntity.getInventory();
            }
            Vector3i targetBlockPosition = new Vector3i(blockPosition.x, blockPosition.y, blockPosition.z);
            String string = interactionBlockTypeKey = this.blockTypeKey != null ? this.blockTypeKey : heldItemStack.getBlockKey();
            if (interactionBlockTypeKey == null) {
                return;
            }
            BlockType interactionBlockType = (BlockType)BlockType.getAssetMap().getAsset(interactionBlockTypeKey);
            int clientPlacedBlockId = clientState.placedBlockId;
            String string2 = clientPlacedBlockTypeKey = clientPlacedBlockId == -1 ? null : BlockType.getAssetMap().getAsset(clientPlacedBlockId).getId();
            if (!(clientPlacedBlockTypeKey == null || clientPlacedBlockTypeKey.equals(this.blockTypeKey) || interactionBlockType != null && BlockPlaceUtils.canPlaceBlock(interactionBlockType, clientPlacedBlockTypeKey))) {
                clientPlacedBlockTypeKey = null;
            }
            if (blockPosition.y < 0 || blockPosition.y >= 320) {
                return;
            }
            BlockPlaceUtils.placeBlock(ref, heldItemStack, clientPlacedBlockTypeKey != null ? clientPlacedBlockTypeKey : this.blockTypeKey, heldItemContainer, BlockFace.fromProtocolFace(context.getClientState().blockFace).getDirection(), targetBlockPosition, blockRotation, inventory, context.getHeldItemSlot(), this.removeItemInHand, chunkReference, chunkStore, commandBuffer);
            boolean bl = isAdventure = playerComponent == null || playerComponent.getGameMode() == GameMode.Adventure;
            if (isAdventure && heldItemStack.getQuantity() == 1 && this.removeItemInHand) {
                context.setHeldItem(null);
            }
            BlockChunk blockChunk = chunkStore.getComponent(chunkReference, BlockChunk.getComponentType());
            BlockSection section = blockChunk.getSectionAtBlockY(blockPosition.y);
            context.getState().blockPosition = blockPosition;
            context.getState().placedBlockId = section.get(blockPosition.x, blockPosition.y, blockPosition.z);
            RotationTuple resultRotation = section.getRotation(blockPosition.x, blockPosition.y, blockPosition.z);
            context.getState().blockRotation = new BlockRotation(resultRotation.yaw().toPacket(), resultRotation.pitch().toPacket(), resultRotation.roll().toPacket());
        }
        super.tick0(firstRun, time, type, context, cooldownHandler);
    }

    @Override
    protected void simulateTick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        super.simulateTick0(firstRun, time, type, context, cooldownHandler);
        if (Interaction.failed(context.getState().state)) {
            return;
        }
        InteractionSyncData clientState = context.getClientState();
        assert (clientState != null);
        if (!firstRun) {
            context.getState().state = context.getClientState().state;
            return;
        }
        clientState.blockRotation = new BlockRotation(Rotation.None, Rotation.None, Rotation.None);
    }

    @Override
    @Nonnull
    protected com.hypixel.hytale.protocol.Interaction generatePacket() {
        return new com.hypixel.hytale.protocol.PlaceBlockInteraction();
    }

    @Override
    protected void configurePacket(com.hypixel.hytale.protocol.Interaction packet) {
        super.configurePacket(packet);
        com.hypixel.hytale.protocol.PlaceBlockInteraction p = (com.hypixel.hytale.protocol.PlaceBlockInteraction)packet;
        p.blockId = this.blockTypeKey == null ? -1 : BlockType.getAssetMap().getIndex(this.blockTypeKey);
        p.removeItemInHand = this.removeItemInHand;
        p.allowDragPlacement = this.allowDragPlacement;
    }

    @Override
    public boolean needsRemoteSync() {
        return true;
    }

    @Override
    public String toString() {
        return "PlaceBlockInteraction{blockTypeKey='" + this.blockTypeKey + "', removeItemInHand=" + this.removeItemInHand + ", allowDragPlacement=" + this.allowDragPlacement + "}";
    }
}

