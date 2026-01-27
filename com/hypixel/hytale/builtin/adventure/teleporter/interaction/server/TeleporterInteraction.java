/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.teleporter.interaction.server;

import com.hypixel.hytale.builtin.adventure.teleporter.component.Teleporter;
import com.hypixel.hytale.builtin.adventure.teleporter.interaction.server.UsedTeleporter;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.PendingTeleport;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.modules.entity.teleport.TeleportRecord;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.time.Duration;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TeleporterInteraction
extends SimpleBlockInteraction {
    @Nonnull
    public static final BuilderCodec<TeleporterInteraction> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(TeleporterInteraction.class, TeleporterInteraction::new, SimpleBlockInteraction.CODEC).appendInherited(new KeyedCodec<String>("Particle", Codec.STRING), (interaction, s) -> {
        interaction.particle = s;
    }, interaction -> interaction.particle, (interaction, parent) -> {
        interaction.particle = parent.particle;
    }).documentation("The particle to play on the entity when teleporting.").add()).appendInherited(new KeyedCodec<Double>("ClearOutXZ", Codec.DOUBLE), (interaction, s) -> {
        interaction.clearoutXZ = s;
    }, interaction -> interaction.clearoutXZ, (interaction, parent) -> {
        interaction.clearoutXZ = parent.clearoutXZ;
    }).addValidator(Validators.greaterThanOrEqual(0.0)).documentation("Upon reaching the warp destination, how far away one has to move on the XZ plane in order to use another Teleporter.").add()).appendInherited(new KeyedCodec<Double>("ClearOutY", Codec.DOUBLE), (interaction, s) -> {
        interaction.clearoutY = s;
    }, interaction -> interaction.clearoutY, (interaction, parent) -> {
        interaction.clearoutY = parent.clearoutY;
    }).addValidator(Validators.greaterThanOrEqual(0.0)).documentation("Upon reaching the warp destination, how far away one has to move along the Y axis in order to use another Teleporter.").add()).build();
    private static final Duration TELEPORTER_GLOBAL_COOLDOWN = Duration.ofMillis(100L);
    @Nullable
    private String particle;
    private double clearoutXZ = 1.3;
    private double clearoutY = 2.5;

    @Override
    @Nonnull
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Server;
    }

    @Override
    protected void interactWithBlock(@Nonnull World world, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull Vector3i targetBlock, @Nonnull CooldownHandler cooldownHandler) {
        TransformComponent transformComponent;
        BlockType variantBlockType;
        String currentState;
        long chunkIndex;
        ChunkStore chunkStore = world.getChunkStore();
        BlockComponentChunk blockComponentChunk = chunkStore.getChunkComponent(chunkIndex = ChunkUtil.indexChunkFromBlock(targetBlock.getX(), targetBlock.getZ()), BlockComponentChunk.getComponentType());
        if (blockComponentChunk == null) {
            return;
        }
        int blockIndex = ChunkUtil.indexBlockInColumn(targetBlock.x, targetBlock.y, targetBlock.z);
        Ref<ChunkStore> blockRef = blockComponentChunk.getEntityReference(blockIndex);
        if (blockRef == null || !blockRef.isValid()) {
            return;
        }
        BlockModule.BlockStateInfo blockStateInfoComponent = blockRef.getStore().getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());
        if (blockStateInfoComponent == null) {
            return;
        }
        Ref<ChunkStore> chunkRef = blockStateInfoComponent.getChunkRef();
        if (chunkRef == null && !chunkRef.isValid()) {
            return;
        }
        Teleporter teleporter = chunkStore.getStore().getComponent(blockRef, Teleporter.getComponentType());
        if (teleporter == null) {
            return;
        }
        Ref<EntityStore> ref = context.getEntity();
        Player playerComponent = commandBuffer.getComponent(ref, Player.getComponentType());
        if (playerComponent != null && playerComponent.isWaitingForClientReady()) {
            return;
        }
        Archetype<EntityStore> archetype = commandBuffer.getArchetype(ref);
        if (archetype.contains(Teleport.getComponentType()) || archetype.contains(PendingTeleport.getComponentType())) {
            return;
        }
        if (archetype.contains(UsedTeleporter.getComponentType())) {
            return;
        }
        WorldChunk worldChunkComponent = chunkRef.getStore().getComponent(chunkRef, WorldChunk.getComponentType());
        if (worldChunkComponent == null) {
            return;
        }
        BlockType blockType = worldChunkComponent.getBlockType(targetBlock.x, targetBlock.y, targetBlock.z);
        if (blockType == null) {
            return;
        }
        if (!teleporter.isValid() && !"default".equals(currentState = blockType.getStateForBlock(blockType)) && (variantBlockType = blockType.getBlockForState("default")) != null) {
            worldChunkComponent.setBlockInteractionState(targetBlock.x, targetBlock.y, targetBlock.z, variantBlockType, "default", true);
        }
        if ((transformComponent = commandBuffer.getComponent(ref, TransformComponent.getComponentType())) == null) {
            return;
        }
        Teleport teleportComponent = teleporter.toTeleport(transformComponent.getPosition(), transformComponent.getRotation(), targetBlock);
        if (teleportComponent == null) {
            return;
        }
        TeleportRecord recorder = commandBuffer.getComponent(ref, TeleportRecord.getComponentType());
        if (recorder != null && !recorder.hasElapsedSinceLastTeleport(TELEPORTER_GLOBAL_COOLDOWN)) {
            return;
        }
        commandBuffer.addComponent(ref, Teleport.getComponentType(), teleportComponent);
        commandBuffer.addComponent(ref, UsedTeleporter.getComponentType(), new UsedTeleporter(teleporter.getWorldUuid(), teleportComponent.getPosition(), this.clearoutXZ, this.clearoutY));
        if (this.particle != null) {
            Vector3d particlePosition = transformComponent.getPosition();
            SpatialResource<Ref<EntityStore>, EntityStore> playerSpatialResource = commandBuffer.getResource(EntityModule.get().getPlayerSpatialResourceType());
            ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
            playerSpatialResource.getSpatialStructure().collect(particlePosition, 75.0, results);
            ParticleUtil.spawnParticleEffect(this.particle, particlePosition, results, commandBuffer);
        }
    }

    @Override
    protected void simulateInteractWithBlock(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull World world, @Nonnull Vector3i targetBlock) {
    }
}

