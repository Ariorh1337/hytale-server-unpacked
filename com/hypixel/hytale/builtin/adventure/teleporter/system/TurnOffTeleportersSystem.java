/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.teleporter.system;

import com.hypixel.hytale.builtin.adventure.teleporter.component.Teleporter;
import com.hypixel.hytale.builtin.teleport.TeleportPlugin;
import com.hypixel.hytale.builtin.teleport.Warp;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.AndQuery;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class TurnOffTeleportersSystem
extends RefSystem<ChunkStore> {
    public static final Query<ChunkStore> QUERY = Query.and(Teleporter.getComponentType(), BlockModule.BlockStateInfo.getComponentType());

    @Override
    public void onEntityAdded(@Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
        if (reason == AddReason.LOAD) {
            TurnOffTeleportersSystem.updatePortalBlocksInWorld(store.getExternalData().getWorld());
        }
    }

    @Override
    public void onEntityRemove(@Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
        if (reason == RemoveReason.REMOVE) {
            TurnOffTeleportersSystem.updatePortalBlocksInWorld(store.getExternalData().getWorld());
        }
    }

    public static void updatePortalBlocksInWorld(World world) {
        Store<ChunkStore> store = world.getChunkStore().getStore();
        AndQuery entityQuery = Query.and(Teleporter.getComponentType(), BlockModule.BlockStateInfo.getComponentType());
        store.forEachChunk(entityQuery, (archetypeChunk, commandBuffer) -> {
            for (int i = 0; i < archetypeChunk.size(); ++i) {
                Ref<ChunkStore> ref = archetypeChunk.getReferenceTo(i);
                TurnOffTeleportersSystem.updatePortalBlockInWorld(ref, commandBuffer);
            }
        });
    }

    private static void updatePortalBlockInWorld(Ref<ChunkStore> ref, ComponentAccessor<ChunkStore> store) {
        if (!ref.isValid()) {
            return;
        }
        Teleporter teleporter = store.getComponent(ref, Teleporter.getComponentType());
        BlockModule.BlockStateInfo blockState = store.getComponent(ref, BlockModule.BlockStateInfo.getComponentType());
        TurnOffTeleportersSystem.updatePortalBlockInWorld(store, teleporter, blockState);
    }

    public static void updatePortalBlockInWorld(ComponentAccessor<ChunkStore> store, Teleporter teleporter, BlockModule.BlockStateInfo blockStateInfo) {
        String desiredState;
        int z;
        int y;
        Ref<ChunkStore> chunkRef = blockStateInfo.getChunkRef();
        if (!chunkRef.isValid()) {
            return;
        }
        WorldChunk worldChunkComponent = store.getComponent(chunkRef, WorldChunk.getComponentType());
        if (worldChunkComponent == null) {
            return;
        }
        int index = blockStateInfo.getIndex();
        int x = ChunkUtil.xFromBlockInColumn(index);
        BlockType blockType = worldChunkComponent.getBlockType(x, y = ChunkUtil.yFromBlockInColumn(index), z = ChunkUtil.zFromBlockInColumn(index));
        if (blockType == null) {
            return;
        }
        String warpId = teleporter.getWarp();
        Warp destinationWarp = warpId == null ? null : TeleportPlugin.get().getWarps().get(warpId);
        String currentState = blockType.getStateForBlock(blockType);
        String string = desiredState = destinationWarp == null ? "default" : "Active";
        if (!desiredState.equals(currentState)) {
            worldChunkComponent.setBlockInteractionState(x, y, z, blockType, desiredState, false);
            blockStateInfo.markNeedsSaving(store);
        }
        if (destinationWarp == null) {
            teleporter.setWarp(null);
            blockStateInfo.markNeedsSaving(store);
        }
    }

    @Override
    @NullableDecl
    public Query<ChunkStore> getQuery() {
        return QUERY;
    }
}

