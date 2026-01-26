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
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockFace;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.asset.type.fluid.FluidTicker;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.FluidSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlaceFluidInteraction
extends SimpleBlockInteraction {
    @Nonnull
    public static final BuilderCodec<PlaceFluidInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(PlaceFluidInteraction.class, PlaceFluidInteraction::new, SimpleInteraction.CODEC).documentation("Places the current or given block.")).append(new KeyedCodec<String>("FluidToPlace", Codec.STRING), (placeBlockInteraction, blockTypeKey) -> {
        placeBlockInteraction.fluidKey = blockTypeKey;
    }, placeBlockInteraction -> placeBlockInteraction.fluidKey).addValidatorLate(() -> Fluid.VALIDATOR_CACHE.getValidator().late()).add()).append(new KeyedCodec<Boolean>("RemoveItemInHand", Codec.BOOLEAN), (placeBlockInteraction, aBoolean) -> {
        placeBlockInteraction.removeItemInHand = aBoolean;
    }, placeBlockInteraction -> placeBlockInteraction.removeItemInHand).add()).build();
    @Nullable
    protected String fluidKey;
    protected boolean removeItemInHand = true;

    @Nullable
    public String getFluidKey() {
        return this.fluidKey;
    }

    @Override
    @Nonnull
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Client;
    }

    @Override
    protected void interactWithBlock(@Nonnull World world, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull Vector3i targetBlock, @Nonnull CooldownHandler cooldownHandler) {
        Ref<ChunkStore> section;
        Store<ChunkStore> store = world.getChunkStore().getStore();
        int fluidIndex = Fluid.getFluidIdOrUnknown(this.fluidKey, "Unknown fluid: %s", this.fluidKey);
        Fluid fluid = Fluid.getAssetMap().getAsset(fluidIndex);
        Vector3i target = targetBlock;
        BlockType targetBlockType = world.getBlockType(target);
        if (FluidTicker.isSolid(targetBlockType)) {
            target = targetBlock.clone();
            BlockFace face = BlockFace.fromProtocolFace(context.getClientState().blockFace);
            target.add(face.getDirection());
        }
        if ((section = world.getChunkStore().getChunkSectionReference(ChunkUtil.chunkCoordinate(target.x), ChunkUtil.chunkCoordinate(target.y), ChunkUtil.chunkCoordinate(target.z))) == null) {
            return;
        }
        FluidSection fluidSectionComponent = store.getComponent(section, FluidSection.getComponentType());
        if (fluidSectionComponent == null) {
            return;
        }
        fluidSectionComponent.setFluid(target.x, target.y, target.z, fluid, (byte)fluid.getMaxFluidLevel());
        Ref<ChunkStore> chunkColumn = world.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(target.x, target.z));
        if (chunkColumn == null) {
            return;
        }
        BlockChunk blockChunkComponent = store.getComponent(chunkColumn, BlockChunk.getComponentType());
        blockChunkComponent.setTicking(target.x, target.y, target.z, true);
        Ref<EntityStore> ref = context.getEntity();
        Player playerComponent = commandBuffer.getComponent(ref, Player.getComponentType());
        PlayerRef playerRefComponent = commandBuffer.getComponent(ref, PlayerRef.getComponentType());
        if ((playerRefComponent == null || playerComponent != null && playerComponent.getGameMode() == GameMode.Adventure) && itemInHand.getQuantity() == 1 && this.removeItemInHand) {
            context.setHeldItem(null);
        }
    }

    @Override
    protected void simulateInteractWithBlock(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull World world, @Nonnull Vector3i targetBlock) {
    }

    @Override
    public boolean needsRemoteSync() {
        return true;
    }

    @Override
    @Nonnull
    public String toString() {
        return "PlaceBlockInteraction{blockTypeKey=" + this.fluidKey + ", removeItemInHand=" + this.removeItemInHand + "} " + super.toString();
    }
}

