/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.interactions;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.validators.NPCRoleValidator;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpawnNPCInteraction
extends SimpleBlockInteraction {
    @Nonnull
    public static final BuilderCodec<SpawnNPCInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(SpawnNPCInteraction.class, SpawnNPCInteraction::new, SimpleBlockInteraction.CODEC).documentation("Spawns an NPC on the block that is being interacted with.")).append(new KeyedCodec<String>("EntityId", Codec.STRING), (spawnNPCInteraction, s) -> {
        spawnNPCInteraction.entityId = s;
    }, spawnNPCInteraction -> spawnNPCInteraction.entityId).documentation("The ID of the entity asset to spawn.").addValidator(NPCRoleValidator.INSTANCE).add()).append(new KeyedCodec<Vector3d>("SpawnOffset", Vector3d.CODEC), (spawnNPCInteraction, s) -> spawnNPCInteraction.spawnOffset.assign((Vector3d)s), spawnNPCInteraction -> spawnNPCInteraction.spawnOffset).documentation("The offset to apply to the spawn position of the NPC, relative to the block's rotation and center.").add()).append(new KeyedCodec<Float>("SpawnYawOffset", Codec.FLOAT), (spawnNPCInteraction, f) -> {
        spawnNPCInteraction.spawnYawOffset = f.floatValue();
    }, spawnNPCInteraction -> Float.valueOf(spawnNPCInteraction.spawnYawOffset)).documentation("The yaw rotation offset in radians to apply to the NPC rotation, relative to the block's yaw.").add()).append(new KeyedCodec<Float>("SpawnChance", Codec.FLOAT), (spawnNPCInteraction, f) -> {
        spawnNPCInteraction.spawnChance = f.floatValue();
    }, spawnNPCInteraction -> Float.valueOf(spawnNPCInteraction.spawnChance)).documentation("The chance of the NPC spawning when the interaction is triggered.").add()).build();
    protected String entityId;
    @Nonnull
    protected Vector3d spawnOffset = new Vector3d();
    protected float spawnYawOffset;
    protected float spawnChance = 1.0f;

    private void spawnNPC(@Nonnull Store<EntityStore> store, @Nonnull Vector3i targetBlock) {
        World world = store.getExternalData().getWorld();
        SpawnData spawnData = this.computeSpawnData(world, targetBlock);
        NPCPlugin.get().spawnNPC(store, this.entityId, null, spawnData.position(), spawnData.rotation());
    }

    @Nonnull
    private SpawnData computeSpawnData(@Nonnull World world, @Nonnull Vector3i targetBlock) {
        long chunkIndex = ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z);
        ChunkStore chunkStore = world.getChunkStore();
        Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(chunkIndex);
        if (chunkRef == null || !chunkRef.isValid()) {
            return new SpawnData(this.spawnOffset.clone().add(targetBlock).add(0.5, 0.5, 0.5), Vector3f.ZERO);
        }
        WorldChunk worldChunkComponent = chunkStore.getStore().getComponent(chunkRef, WorldChunk.getComponentType());
        assert (worldChunkComponent != null);
        BlockType blockType = worldChunkComponent.getBlockType(targetBlock.x, targetBlock.y, targetBlock.z);
        if (blockType == null) {
            return new SpawnData(this.spawnOffset.clone().add(targetBlock).add(0.5, 0.5, 0.5), Vector3f.ZERO);
        }
        BlockChunk blockChunkComponent = chunkStore.getStore().getComponent(chunkRef, BlockChunk.getComponentType());
        if (blockChunkComponent == null) {
            return new SpawnData(this.spawnOffset.clone().add(targetBlock).add(0.5, 0.5, 0.5), Vector3f.ZERO);
        }
        BlockSection section = blockChunkComponent.getSectionAtBlockY(targetBlock.y);
        int rotationIndex = section.getRotationIndex(targetBlock.x, targetBlock.y, targetBlock.z);
        RotationTuple rotationTuple = RotationTuple.get(rotationIndex);
        Vector3d position = rotationTuple.rotate(this.spawnOffset);
        Vector3d blockCenter = new Vector3d();
        blockType.getBlockCenter(rotationIndex, blockCenter);
        position.add(blockCenter).add(targetBlock);
        Vector3f rotation = new Vector3f(0.0f, (float)(rotationTuple.yaw().getRadians() + Math.toRadians(this.spawnYawOffset)), 0.0f);
        return new SpawnData(position, rotation);
    }

    @Override
    protected void interactWithBlock(@Nonnull World world, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull Vector3i targetBlock, @Nonnull CooldownHandler cooldownHandler) {
        if (ThreadLocalRandom.current().nextFloat() > this.spawnChance) {
            return;
        }
        commandBuffer.run(store -> this.spawnNPC(world.getEntityStore().getStore(), targetBlock));
    }

    @Override
    protected void simulateInteractWithBlock(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull World world, @Nonnull Vector3i targetBlock) {
        if (ThreadLocalRandom.current().nextFloat() > this.spawnChance) {
            return;
        }
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        assert (commandBuffer != null);
        commandBuffer.run(store -> this.spawnNPC(world.getEntityStore().getStore(), targetBlock));
    }

    private record SpawnData(@Nonnull Vector3d position, @Nonnull Vector3f rotation) {
    }
}

