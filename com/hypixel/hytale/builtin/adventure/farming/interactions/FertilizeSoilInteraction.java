/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.farming.interactions;

import com.hypixel.hytale.builtin.adventure.farming.states.FarmingBlock;
import com.hypixel.hytale.builtin.adventure.farming.states.TilledSoilBlock;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FertilizeSoilInteraction
extends SimpleBlockInteraction {
    public static final BuilderCodec<FertilizeSoilInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(FertilizeSoilInteraction.class, FertilizeSoilInteraction::new, SimpleBlockInteraction.CODEC).documentation("If the target block is farmable then set it to fertilized.")).addField(new KeyedCodec<T[]>("RefreshModifiers", Codec.STRING_ARRAY), (interaction, refreshModifiers) -> {
        interaction.refreshModifiers = refreshModifiers;
    }, interaction -> interaction.refreshModifiers)).build();
    protected String[] refreshModifiers;

    @Override
    @Nonnull
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Server;
    }

    @Override
    protected void interactWithBlock(@Nonnull World world, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull Vector3i targetBlock, @Nonnull CooldownHandler cooldownHandler) {
        int z;
        int x = targetBlock.getX();
        Object worldChunk = world.getChunk(ChunkUtil.indexChunkFromBlock(x, z = targetBlock.getZ()));
        Ref<ChunkStore> blockRef = ((WorldChunk)worldChunk).getBlockComponentEntity(x, targetBlock.getY(), z);
        if (blockRef == null) {
            blockRef = BlockModule.ensureBlockEntity(worldChunk, targetBlock.x, targetBlock.y, targetBlock.z);
        }
        if (blockRef == null) {
            context.getState().state = InteractionState.Failed;
            return;
        }
        Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
        TilledSoilBlock soil = chunkStore.getComponent(blockRef, TilledSoilBlock.getComponentType());
        if (soil != null && !soil.isFertilized()) {
            soil.setFertilized(true);
            ((WorldChunk)worldChunk).setTicking(x, targetBlock.getY(), z, true);
            ((WorldChunk)worldChunk).setTicking(x, targetBlock.getY() + 1, z, true);
            return;
        }
        FarmingBlock farmingState = chunkStore.getComponent(blockRef, FarmingBlock.getComponentType());
        if (farmingState == null) {
            context.getState().state = InteractionState.Failed;
            return;
        }
        Ref<ChunkStore> soilRef = ((WorldChunk)worldChunk).getBlockComponentEntity(x, targetBlock.getY() - 1, z);
        if (soilRef == null) {
            context.getState().state = InteractionState.Failed;
            return;
        }
        soil = chunkStore.getComponent(soilRef, TilledSoilBlock.getComponentType());
        if (soil == null || soil.isFertilized()) {
            context.getState().state = InteractionState.Failed;
            return;
        }
        soil.setFertilized(true);
        ((WorldChunk)worldChunk).setTicking(x, targetBlock.getY() - 1, z, true);
        ((WorldChunk)worldChunk).setTicking(x, targetBlock.getY(), z, true);
    }

    @Override
    protected void simulateInteractWithBlock(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull World world, @Nonnull Vector3i targetBlock) {
    }
}

