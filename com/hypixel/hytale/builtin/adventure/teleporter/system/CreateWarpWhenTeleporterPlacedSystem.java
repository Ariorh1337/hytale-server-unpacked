/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.teleporter.system;

import com.hypixel.hytale.builtin.adventure.teleporter.component.Teleporter;
import com.hypixel.hytale.builtin.adventure.teleporter.util.CannedWarpNames;
import com.hypixel.hytale.builtin.teleport.TeleportPlugin;
import com.hypixel.hytale.builtin.teleport.Warp;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.interaction.components.PlacedByInteractionComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CreateWarpWhenTeleporterPlacedSystem
extends RefChangeSystem<ChunkStore, PlacedByInteractionComponent> {
    @Override
    public void onComponentAdded(@Nonnull Ref<ChunkStore> ref, @Nonnull PlacedByInteractionComponent placedBy, @Nonnull Store<ChunkStore> chunkStore, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
        String language;
        UUID whoPlacedUuid;
        World world = chunkStore.getExternalData().getWorld();
        EntityStore entityStore = world.getEntityStore();
        Ref<EntityStore> whoPlacedRef = entityStore.getRefFromUUID(whoPlacedUuid = placedBy.getWhoPlacedUuid());
        if (whoPlacedRef == null || !whoPlacedRef.isValid()) {
            return;
        }
        PlayerRef playerRefComponent = entityStore.getStore().getComponent(whoPlacedRef, PlayerRef.getComponentType());
        String string = language = playerRefComponent == null ? null : playerRefComponent.getLanguage();
        if (language == null) {
            return;
        }
        BlockModule.BlockStateInfo blockStateInfoComponent = commandBuffer.getComponent(ref, BlockModule.BlockStateInfo.getComponentType());
        assert (blockStateInfoComponent != null);
        Ref<ChunkStore> chunkRef = blockStateInfoComponent.getChunkRef();
        if (chunkRef == null || !chunkRef.isValid()) {
            return;
        }
        WorldChunk worldChunk = chunkStore.getComponent(chunkRef, WorldChunk.getComponentType());
        if (worldChunk == null) {
            return;
        }
        String cannedName = CannedWarpNames.generateCannedWarpName(ref, language);
        if (cannedName == null) {
            return;
        }
        CreateWarpWhenTeleporterPlacedSystem.createWarp(worldChunk, blockStateInfoComponent, cannedName);
        Teleporter teleporterComponent = commandBuffer.getComponent(ref, Teleporter.getComponentType());
        assert (teleporterComponent != null);
        teleporterComponent.setOwnedWarp(cannedName);
    }

    public static void createWarp(@Nonnull WorldChunk worldChunk, @Nonnull BlockModule.BlockStateInfo blockStateInfo, @Nonnull String name) {
        int chunkBlockX = worldChunk.getX() << 5;
        int chunkBlockZ = worldChunk.getZ() << 5;
        int index = blockStateInfo.getIndex();
        int x = chunkBlockX + ChunkUtil.xFromBlockInColumn(index);
        int y = ChunkUtil.yFromBlockInColumn(index);
        int z = chunkBlockZ + ChunkUtil.zFromBlockInColumn(index);
        BlockChunk blockChunkComponent = worldChunk.getBlockChunk();
        assert (blockChunkComponent != null);
        BlockSection section = blockChunkComponent.getSectionAtBlockY(y);
        int rotationIndex = section.getRotationIndex(x, y, z);
        RotationTuple rotationTuple = RotationTuple.get(rotationIndex);
        Rotation rotationYaw = rotationTuple.yaw();
        float warpRotationYaw = (float)rotationYaw.getRadians() + (float)Math.toRadians(180.0);
        Vector3d warpPosition = new Vector3d(x, y, z).add(0.5, 0.65, 0.5);
        Transform warpTransform = new Transform(warpPosition, new Vector3f(Float.NaN, warpRotationYaw, Float.NaN));
        String warpId = name.toLowerCase();
        Warp warp = new Warp(warpTransform, name, worldChunk.getWorld(), "*Teleporter", Instant.now());
        TeleportPlugin teleportPlugin = TeleportPlugin.get();
        teleportPlugin.getWarps().put(warpId, warp);
        teleportPlugin.saveWarps();
    }

    @Override
    public void onComponentSet(@Nonnull Ref<ChunkStore> ref, @Nullable PlacedByInteractionComponent oldComponent, @Nonnull PlacedByInteractionComponent newComponent, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
    }

    @Override
    public void onComponentRemoved(@Nonnull Ref<ChunkStore> ref, @Nonnull PlacedByInteractionComponent component, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
    }

    @Override
    @Nonnull
    public ComponentType<ChunkStore, PlacedByInteractionComponent> componentType() {
        return PlacedByInteractionComponent.getComponentType();
    }

    @Override
    @Nullable
    public Query<ChunkStore> getQuery() {
        return Query.and(PlacedByInteractionComponent.getComponentType(), Teleporter.getComponentType(), BlockModule.BlockStateInfo.getComponentType());
    }
}

