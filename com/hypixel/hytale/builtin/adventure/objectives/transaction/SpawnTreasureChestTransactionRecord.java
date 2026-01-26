/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.objectives.transaction;

import com.hypixel.hytale.builtin.adventure.objectives.blockstates.TreasureChestState;
import com.hypixel.hytale.builtin.adventure.objectives.transaction.TransactionRecord;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import java.util.UUID;
import javax.annotation.Nonnull;

public class SpawnTreasureChestTransactionRecord
extends TransactionRecord {
    public static final BuilderCodec<SpawnTreasureChestTransactionRecord> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(SpawnTreasureChestTransactionRecord.class, SpawnTreasureChestTransactionRecord::new, BASE_CODEC).append(new KeyedCodec<UUID>("WorldUUID", Codec.UUID_BINARY), (spawnTreasureChestTransactionRecord, uuid) -> {
        spawnTreasureChestTransactionRecord.worldUUID = uuid;
    }, spawnTreasureChestTransactionRecord -> spawnTreasureChestTransactionRecord.worldUUID).add()).append(new KeyedCodec<Vector3i>("BlockPosition", Vector3i.CODEC), (spawnTreasureChestTransactionRecord, vector3d) -> {
        spawnTreasureChestTransactionRecord.blockPosition = vector3d;
    }, spawnTreasureChestTransactionRecord -> spawnTreasureChestTransactionRecord.blockPosition).add()).build();
    protected UUID worldUUID;
    protected Vector3i blockPosition;

    public SpawnTreasureChestTransactionRecord(UUID worldUUID, Vector3i blockPosition) {
        this.worldUUID = worldUUID;
        this.blockPosition = blockPosition;
    }

    protected SpawnTreasureChestTransactionRecord() {
    }

    @Override
    public void revert() {
        World world = Universe.get().getWorld(this.worldUUID);
        if (world == null) {
            return;
        }
        Object worldChunk = world.getChunk(ChunkUtil.indexChunkFromBlock(this.blockPosition.x, this.blockPosition.z));
        BlockState blockState = ((WorldChunk)worldChunk).getState(this.blockPosition.x, this.blockPosition.y, this.blockPosition.z);
        if (!(blockState instanceof TreasureChestState)) {
            return;
        }
        ((TreasureChestState)blockState).setOpened(true);
    }

    @Override
    public void complete() {
    }

    @Override
    public void unload() {
    }

    @Override
    public boolean shouldBeSerialized() {
        return true;
    }

    @Override
    @Nonnull
    public String toString() {
        return "SpawnTreasureChestTransactionRecord{worldUUID=" + String.valueOf(this.worldUUID) + ", blockPosition=" + String.valueOf(this.blockPosition) + "} " + super.toString();
    }
}

